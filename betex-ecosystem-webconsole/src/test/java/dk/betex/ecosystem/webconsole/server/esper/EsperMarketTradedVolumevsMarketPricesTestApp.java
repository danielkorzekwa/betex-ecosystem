package dk.betex.ecosystem.webconsole.server.esper;

import static org.apache.commons.math.util.MathUtils.round;

import java.util.Date;

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
import dk.betex.ecosystem.marketdatacollector.dao.MarketTradedVolumeDao;
import dk.betex.ecosystem.marketdatacollector.dao.MarketTradedVolumeDaoImpl;
import dk.betex.ecosystem.marketdatacollector.model.MarketDetails;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices;
import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapModel;
import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapModel.HeatMapColumn;
import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapModel.HeatMapValue;
import dk.betex.ecosystem.webconsole.server.HeatMapModelDataSourceFactory;
import dk.betex.ecosystem.webconsole.server.HeatMapModelFactory;
import dk.betex.ecosystem.webconsole.server.MarketPricesCalculator;

/**
 * Calculates avgPrice for all runners based on totalTradedVolume within time window and compare it to the best
 * to back/lay/lastMatched prices.
 * 
 * @author korzekwad
 * 
 */
public class EsperMarketTradedVolumevsMarketPricesTestApp {

	private static final int PAGE_COUNT=10;
	
	private EPServiceProvider epService;

	private MarketTradedVolumeDao marketTradedVolueDao;
	private MarketPricesDao marketPricesDao;
	private MarketDetailsDao marketDetailsDao;

	private String dbUrl = "10.2.2.72";
	private long marketId = 101081282l;
	private long twentyMinBeforeMarketTime;
	
	@Before
	public void before() {
		Configuration config = new Configuration();
		config.addEventTypeAutoName("dk.betex.ecosystem.marketdatacollector.model");
		config.addEventTypeAutoName("dk.betex.ecosystem.webconsole.server.esper");


		epService = EPServiceProviderManager.getDefaultProvider(config);
		epService.initialize();

		/** Init DAOs */
		marketTradedVolueDao = new MarketTradedVolumeDaoImpl(new Database(dbUrl, "market_traded_volume"));
		marketPricesDao = new MarketPricesDaoImpl(new Database(dbUrl, "market_prices"));
		marketDetailsDao = new MarketDetailsDaoImpl(new Database(dbUrl, "market_details"));

		/** Get market time. */
		MarketDetails marketDetails = marketDetailsDao.getMarketDetails(marketId);
		twentyMinBeforeMarketTime = marketDetails.getMarketTime() - (1000 * 60 * 20);

	}

	@Test
	public void testProcessMarketTradedVolume() {
		long now = System.currentTimeMillis();

		EPStatement statement = epService
				.getEPAdministrator()
				.createEPL(
						"select marketTradedVolume.timestamp,mtvEvent as lastEvent,prev(count(*)-1,mtvEvent) as firstEvent from MarketDataEvent.win:ext_timed(marketTradedVolume.timestamp,10 sec) as mtvEvent");
		statement.addListener(new EventLister());
		
		/** Get first 200 of records. */
		ViewAndDocumentsResult<BaseDocument, MarketTradedVolume> marketTradedVolumeList = marketTradedVolueDao
				.getMarketTradedVolume(marketId, twentyMinBeforeMarketTime, Long.MAX_VALUE, PAGE_COUNT);
		ViewAndDocumentsResult<BaseDocument, MarketPrices> marketPricesList = marketPricesDao
		.get(marketId, twentyMinBeforeMarketTime, Long.MAX_VALUE, PAGE_COUNT);

		for (int i=0;i< marketTradedVolumeList.getRows().size();i++) {
			ValueAndDocumentRow<BaseDocument, MarketTradedVolume> marketTradedVolumeRow = marketTradedVolumeList.getRows().get(i);
			ValueAndDocumentRow<BaseDocument, MarketPrices> marketPricesRow = marketPricesList.getRows().get(i);
			
			MarketDataEvent marketDataEvent = new MarketDataEvent(marketTradedVolumeRow.getDocument(), marketPricesRow.getDocument());
			
			epService.getEPRuntime().sendEvent(marketDataEvent);
		}

		/** Page through the rest of records. */
		while (marketTradedVolumeList.getRows().size() > 0 && marketPricesList.getRows().size() > 0) {
			marketTradedVolumeList = marketTradedVolueDao.getMarketTradedVolume(marketId, marketTradedVolumeList
					.getRows().get(marketTradedVolumeList.getRows().size() - 1).getDocument().getTimestamp() + 1,
					Long.MAX_VALUE, PAGE_COUNT);
			marketPricesList = marketPricesDao.get(marketId, marketPricesList
					.getRows().get(marketPricesList.getRows().size() - 1).getDocument().getTimestamp() + 1, Long.MAX_VALUE, PAGE_COUNT);

			for (int i=0;i< marketTradedVolumeList.getRows().size();i++) {
				ValueAndDocumentRow<BaseDocument, MarketTradedVolume> marketTradedVolumeRow = marketTradedVolumeList.getRows().get(i);
				ValueAndDocumentRow<BaseDocument, MarketPrices> marketPricesRow = marketPricesList.getRows().get(i);
				
				MarketDataEvent marketDataEvent = new MarketDataEvent(marketTradedVolumeRow.getDocument(), marketPricesRow.getDocument());
				
				epService.getEPRuntime().sendEvent(marketDataEvent);
			}
		}

		System.out.println("Processing market traded volume: " + (System.currentTimeMillis() - now));

	}

	private class EventLister implements UpdateListener {

		@Override
		public void update(EventBean[] newEvents, EventBean[] oldEvents) {
			
			MarketDataEvent firstEvent = (MarketDataEvent) newEvents[0].get("firstEvent");
			MarketDataEvent lastEvent = (MarketDataEvent) newEvents[0].get("lastEvent");

			BioHeatMapModel firstModel = HeatMapModelFactory.createHeatMap(HeatMapModelDataSourceFactory
					.create(firstEvent.getMarketTradedVolume()), 0, 1);
			BioHeatMapModel lastModel = HeatMapModelFactory.createHeatMap(HeatMapModelDataSourceFactory
					.create(lastEvent.getMarketTradedVolume()), 0, 1);
			BioHeatMapModel modelDelta = HeatMapModelFactory.delta(firstModel, lastModel);

			for (HeatMapColumn column : modelDelta.getColumns()) {
				double sumOfPayouts = 0;
				double sumOfStakes = 0;
				for (HeatMapValue value : column.getValues()) {
					if (value.getRowValue() > 0) {
						double price = 1 / value.getRowValue();
						sumOfPayouts += price * value.getCellValue();
						sumOfStakes += value.getCellValue();
					}
				}
				
				/** Print avgPrice for a particular runner only. */
				long selectionId = Long.parseLong(column.getLabel());
				if (selectionId ==2370221) {
					RunnerPrices runnerPrices = lastEvent.getMarketPrices().getRunnerPrices(selectionId);	
				
					double avgPrice = sumOfPayouts / sumOfStakes;
					double bestToBack = MarketPricesCalculator.getPriceToBack(runnerPrices);
					double bestToLay = MarketPricesCalculator.getPriceToLay(runnerPrices); 
					double priceFactor = (1/avgPrice) / (1/runnerPrices.getLastPriceMatched());
					
					System.out.println(newEvents.length + ":" + new Date(lastEvent.getMarketTradedVolume().getTimestamp()) + ":"
							+ round(column.getTotal(), 2) + ":"
							+ round(avgPrice, 2) + ":"
							+ runnerPrices.getLastPriceMatched() + ":" 
							+ round(bestToBack,2) + ":" 
							+ round(bestToLay,2) + ":" 
							+ round(priceFactor,2));
				}
			}
		}
	}
}
