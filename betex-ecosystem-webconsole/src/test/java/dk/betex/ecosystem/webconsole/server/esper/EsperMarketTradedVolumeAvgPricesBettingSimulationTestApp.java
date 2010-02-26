package dk.betex.ecosystem.webconsole.server.esper;

import static org.apache.commons.math.util.MathUtils.round;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jcouchdb.db.Database;
import org.jcouchdb.document.BaseDocument;
import org.jcouchdb.document.ValueAndDocumentRow;
import org.jcouchdb.document.ViewAndDocumentsResult;
import org.junit.Before;
import org.junit.Test;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

import dk.betex.ecosystem.marketdatacollector.dao.MarketDetailsDao;
import dk.betex.ecosystem.marketdatacollector.dao.MarketDetailsDaoImpl;
import dk.betex.ecosystem.marketdatacollector.dao.MarketPricesDao;
import dk.betex.ecosystem.marketdatacollector.dao.MarketPricesDaoImpl;
import dk.betex.ecosystem.marketdatacollector.model.MarketDetails;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices.PriceTradedVolume;
import dk.betex.ecosystem.webconsole.server.MarketPricesCalculator;
import dk.betex.ecosystem.webconsole.server.esper.LiabilityCalculator.Bet;
import dk.betex.ecosystem.webconsole.server.esper.LiabilityCalculator.MarketLiability;
import dk.betex.ecosystem.webconsole.server.esper.LiabilityCalculator.MarketProb;
import dk.betex.ecosystem.webconsole.server.esper.LiabilityCalculator.RunnerLiability;

/**
 * Calculates avgPrice for all runners based on totalTradedVolume within time window and compare it to the best to
 * back/lay/lastMatched prices.
 * 
 * Then do betting simulation.
 * 
 * @author korzekwad
 * 
 */
public class EsperMarketTradedVolumeAvgPricesBettingSimulationTestApp {

	private static final int PAGE_COUNT = 1000;

	private EPServiceProvider epService;

	private MarketPricesDao marketPricesDao;
	private MarketDetailsDao marketDetailsDao;

	private SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	private String dbUrl = "10.2.4.191";

	private ViewAndDocumentsResult<BaseDocument, MarketDetails> marketDetailsList;

	private EventLister eventListener = new EventLister();

	private LiabilityCalculator liabilityCalc = new LiabilityCalculatorImpl();
	private List<Bet> bets = new ArrayList<Bet>();
	/** key - marketId */
	private Map<Long, MarketProb> marketProbs = new HashMap<Long, MarketProb>();

	/** Maximum number of bets to be processed within simulation. */
	private int maxNumOfMarkets = 40;

	@Before
	public void before() {
		Configuration config = new Configuration();
		config.addEventTypeAutoName("dk.betex.ecosystem.marketdatacollector.model");

		epService = EPServiceProviderManager.getDefaultProvider(config);
		epService.initialize();

		EPStatement statement = epService.getEPAdministrator().createEPL("select * from MarketPrices as event");
		statement.addListener(eventListener);

		/** Init DAOs */
		marketPricesDao = new MarketPricesDaoImpl(new Database(dbUrl, "market_prices"));
		marketDetailsDao = new MarketDetailsDaoImpl(new Database(dbUrl, "market_details"));

		/** Get market time. */
		marketDetailsList = marketDetailsDao.getMarketDetailsList(10000);

	}

	@Test
	public void testProcessMarketTradedVolume() {
		long now = System.currentTimeMillis();

		/** Run simulation for all markets. */
		int numberOfAnalyzedMarkets = 0;
		for (int i = 0; i < marketDetailsList.getRows().size(); i++) {
			if (numberOfAnalyzedMarkets > maxNumOfMarkets) {
				break;
			}

			ValueAndDocumentRow<BaseDocument, MarketDetails> marketDetailsRow = marketDetailsList.getRows().get(i);
			MarketDetails marketDetails = marketDetailsRow.getDocument();
			 if (marketDetails.getMarketId() != 101181193)
			 continue;

			/** Get first 200 of records. */
			ViewAndDocumentsResult<BaseDocument, MarketPrices> marketPricesList = marketPricesDao.get(marketDetails
					.getMarketId(), 0, Long.MAX_VALUE, PAGE_COUNT);

			for (ValueAndDocumentRow<BaseDocument, MarketPrices> marketPricesRow : marketPricesList.getRows()) {
				epService.getEPRuntime().sendEvent(marketPricesRow.getDocument());
			}

			/** Page through the rest of records. */
			while (marketPricesList.getRows().size() > 0) {

				marketPricesList = marketPricesDao.get(marketDetails.getMarketId(), marketPricesList.getRows().get(
						marketPricesList.getRows().size() - 1).getDocument().getTimestamp() + 1, Long.MAX_VALUE,
						PAGE_COUNT);

				for (ValueAndDocumentRow<BaseDocument, MarketPrices> marketPricesRow : marketPricesList.getRows()) {
					epService.getEPRuntime().sendEvent(marketPricesRow.getDocument());
				}
			}

		}

		/** Calculate liability */
		List<MarketLiability> liabilities = liabilityCalc.calculateLiability(bets);
		double totalLiability = 0;
		for (MarketLiability liability : liabilities) {
			System.out.println("*****************************************************");
			System.out.println("Market liabaility: " + liability.getMarketId());
			double marketLiability = 0;
			int marketNumOfBets = 0;
			for (RunnerLiability runnerLiability : liability.getExpectedLiability()) {
				double runnerProb = marketProbs.get(liability.getMarketId()).getRunnerProbs().get(
						runnerLiability.getSelectionId());
				marketLiability += runnerLiability.getRunnerLiability(runnerProb);
				marketNumOfBets += runnerLiability.getNumberOfBets();

				System.out
						.println("   - selectionId: " + runnerLiability.getSelectionId() + ",bets: "
								+ runnerLiability.getNumberOfBets() + ", liability: "
								+ round(runnerLiability.getRunnerLiability(runnerProb), 2) + ",ifWin: "
								+ round(runnerLiability.getRunnerLiability(1), 2) + ",ifLose: "
								+ round(runnerLiability.getRunnerLiability(0), 2) + ",runnerPrice: "
								+ round(1 / runnerProb, 2));
			}
			totalLiability += marketLiability;
			System.out.println("Total market liability: " + marketNumOfBets + ":" + round(marketLiability, 2));
			numberOfAnalyzedMarkets++;
		}
		System.out.println("*****************************************************");
		System.out.println("Global liability: " + round(totalLiability, 2));
		System.out.println("*****************************************************");

		System.out.println("\nProcessing market prices. NumOfMarkets: " + numberOfAnalyzedMarkets + ", time: "
				+ (System.currentTimeMillis() - now));

	}

	private List<Bet> filterBets(long marketId, long selectionId) {
		List<Bet> filteredBets = new ArrayList<Bet>();
		for (Bet bet : bets) {
			if (bet.getMarketId() == marketId && bet.getSelectionId() == selectionId) {
				filteredBets.add(bet);
			}
		}
		return filteredBets;
	}

	private class EventLister implements UpdateListener {

		@Override
		public void update(EventBean[] newEvents, EventBean[] oldEvents) {

			MarketPrices marketPrices = (MarketPrices) newEvents[0].getUnderlying();
			if (marketPrices.getInPlayDelay() > 0)
				return;

			Map<Long, Double> runnerProbs = new HashMap<Long, Double>();
			for (RunnerPrices runnerPrices : marketPrices.getRunnerPrices()) {

				double avgPrice = avgPrice(runnerPrices.getPriceTradedVolume());
				double bestToBack = MarketPricesCalculator.getPriceToBack(runnerPrices);
				double bestToLay = MarketPricesCalculator.getPriceToLay(runnerPrices);

				double runnerProb = (1 / bestToBack + 1 / bestToLay) / 2;
				runnerProbs.put(runnerPrices.getSelectionId(), runnerProb);

				List<MarketLiability> marketLiabilities = liabilityCalc.calculateLiability(filterBets(marketPrices
						.getMarketId(), runnerPrices.getSelectionId()));
				RunnerLiability runnerLiability = new RunnerLiability(runnerPrices.getSelectionId());
				if (marketLiabilities.size() > 0) {
					runnerLiability = marketLiabilities.get(0).getExpectedLiability(runnerPrices.getSelectionId());
				}

				Bet bet = null;
				if (runnerPrices.totalTradedVolume() > 100 && runnerLiability.getRunnerLiability(0) > -1000
						&& ((1 / avgPrice) / (1 / bestToBack)) > 1.04) {
					bet = new Bet(marketPrices.getMarketId(), runnerPrices.getSelectionId(), 2, bestToBack);
				}
				if (runnerPrices.totalTradedVolume() > 100 && runnerLiability.getRunnerLiability(1) > -100
						&& ((1 / avgPrice) / (1 / bestToLay)) < 0.96) {

					bet = new Bet(marketPrices.getMarketId(), runnerPrices.getSelectionId(), -2, bestToLay);
				}
				
				if (marketPrices.getInPlayDelay() == 0 && (1 / runnerProb) < 5 && bet != null) {
					bets.add(bet);
					List<MarketLiability> betLiabilities = liabilityCalc.calculateLiability(Arrays.asList(bet));
					System.out.println("Bet liability: "
							+ round(betLiabilities.get(0).getExpectedLiability().get(0).getRunnerLiability(runnerProb),2));
					System.out.println(df.format(new Date(marketPrices.getTimestamp())) + ",tV="
							+ round(runnerPrices.totalTradedVolume(), 2) + ",avgP/lM/bB/bL=" + round(avgPrice, 2) + "/"
							+ runnerPrices.getLastPriceMatched() + "/" + round(bestToBack, 2) + "/"
							+ round(bestToLay, 2) + ", " + bet);
				}

			}
			marketProbs.put(marketPrices.getMarketId(), new MarketProb(marketPrices.getMarketId(), runnerProbs));
		}

		private double avgPrice(List<PriceTradedVolume> priceTradedVolume) {
			double sumOfPayouts = 0;
			double sumOfStakes = 0;
			/** Calculate avgPrices based on traded volume */
			for (PriceTradedVolume volume : priceTradedVolume) {
				sumOfPayouts += volume.getPrice() * volume.getTradedVolume();
				sumOfStakes += volume.getTradedVolume();
			}
			double avgPrice = sumOfPayouts / sumOfStakes;
			return avgPrice;
		}

	}
}
