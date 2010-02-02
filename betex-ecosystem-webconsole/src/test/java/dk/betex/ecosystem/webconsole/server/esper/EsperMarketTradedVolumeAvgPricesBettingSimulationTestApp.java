package dk.betex.ecosystem.webconsole.server.esper;

import static org.apache.commons.math.util.MathUtils.round;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.DateFormatter;

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
import dk.betex.ecosystem.webconsole.server.esper.LiabilityCalculator.MarketProb;

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

	private static final int PAGE_COUNT = 100;

	private EPServiceProvider epService;

	private MarketPricesDao marketPricesDao;
	private MarketDetailsDao marketDetailsDao;

	private String dbUrl = "10.2.2.72";
	private long marketId = 101112284;
	private long twentyMinBeforeMarketTime;

	@Before
	public void before() {
		Configuration config = new Configuration();
		config.addEventTypeAutoName("dk.betex.ecosystem.marketdatacollector.model");

		epService = EPServiceProviderManager.getDefaultProvider(config);
		epService.initialize();

		/** Init DAOs */
		marketPricesDao = new MarketPricesDaoImpl(new Database(dbUrl, "market_prices"));
		marketDetailsDao = new MarketDetailsDaoImpl(new Database(dbUrl, "market_details"));

		/** Get market time. */
		MarketDetails marketDetails = marketDetailsDao.getMarketDetails(marketId);
		// twentyMinBeforeMarketTime = marketDetails.getMarketTime() - (1000 * 60 * 20);
		twentyMinBeforeMarketTime = 0;

	}

	@Test
	public void testProcessMarketTradedVolume() {
		long now = System.currentTimeMillis();

		EPStatement statement = epService.getEPAdministrator().createEPL("select * from MarketPrices as event");
		statement.addListener(new EventLister());

		/** Get first 200 of records. */
		ViewAndDocumentsResult<BaseDocument, MarketPrices> marketPricesList = marketPricesDao.get(marketId,
				twentyMinBeforeMarketTime, Long.MAX_VALUE, PAGE_COUNT);

		for (ValueAndDocumentRow<BaseDocument, MarketPrices> marketPricesRow : marketPricesList.getRows()) {
			epService.getEPRuntime().sendEvent(marketPricesRow.getDocument());
		}

		/** Page through the rest of records. */
		while (marketPricesList.getRows().size() > 0) {

			marketPricesList = marketPricesDao
					.get(marketId, marketPricesList.getRows().get(marketPricesList.getRows().size() - 1).getDocument()
							.getTimestamp() + 1, Long.MAX_VALUE, PAGE_COUNT);

			for (ValueAndDocumentRow<BaseDocument, MarketPrices> marketPricesRow : marketPricesList.getRows()) {
				epService.getEPRuntime().sendEvent(marketPricesRow.getDocument());
			}
		}

		System.out.println("Processing market prices: " + (System.currentTimeMillis() - now));

	}

	private class EventLister implements UpdateListener {

		private SimpleDateFormat df = new SimpleDateFormat();
		
		private LiabilityCalculator liabilityCalc = new LiabilityCalculatorImpl();

		private List<Bet> bets = new ArrayList<Bet>();
		/** key - marketId */
		private Map<Long, MarketProb> marketProbs = new HashMap<Long, MarketProb>();

		@Override
		public void update(EventBean[] newEvents, EventBean[] oldEvents) {

			MarketPrices marketPrices = (MarketPrices) newEvents[0].getUnderlying();

			Map<Long, Double> runnerProbs = new HashMap<Long, Double>();
			for (RunnerPrices runnerPrices : marketPrices.getRunnerPrices()) {

				double avgPrice = avgPrice(runnerPrices.getPriceTradedVolume());
				double bestToBack = MarketPricesCalculator.getPriceToBack(runnerPrices);
				double bestToLay = MarketPricesCalculator.getPriceToLay(runnerPrices);
				double priceFactor = (1 / avgPrice) / (1 / runnerPrices.getLastPriceMatched());

				double runnerProb = (1 / bestToBack + 1 / bestToLay) / 2;
				runnerProbs.put(runnerPrices.getSelectionId(), runnerProb);

				Bet bet = null;
				if (runnerPrices.getTotalTradedVolume() > 0 && priceFactor > 1.03) {
					bet = new Bet(marketId, runnerPrices.getSelectionId(), 2, bestToBack);
				}
				if (runnerPrices.getTotalTradedVolume() > 0 && priceFactor < 0.95) {
					bet = new Bet(marketId, runnerPrices.getSelectionId(), -2, bestToLay);
				}
				if (marketPrices.getInPlayDelay() == 0 && (1 / runnerProb) < 5 && bet != null) {
					bets.add(bet);
					System.out.println(bet);
				}

				System.out.println(newEvents.length + ":" + df.format(new Date(marketPrices.getTimestamp())) + ":"
						+ round(runnerPrices.getTotalTradedVolume(), 2) + ":" + round(avgPrice, 2) + ":"
						+ runnerPrices.getLastPriceMatched() + ":" + round(bestToBack, 2) + ":" + round(bestToLay, 2)
						+ ":" + round(priceFactor, 2));
			}
			marketProbs.put(marketPrices.getMarketId(), new MarketProb(marketPrices.getMarketId(), runnerProbs));

			if (marketPrices.getInPlayDelay() > 0) {
				System.out.println("Market in play.");
			}
			System.out.println("Liability: " + df.format(new Date(marketPrices.getTimestamp())) + ":" + bets.size() + ":"
					+ round(liabilityCalc.calculateLiability(bets, marketProbs), 2));
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
