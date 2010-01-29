package dk.betex.ecosystem.webconsole.server.esper;

import java.util.Date;

import org.apache.commons.math.util.MathUtils;
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
import dk.betex.ecosystem.marketdatacollector.dao.MarketTradedVolumeDao;
import dk.betex.ecosystem.marketdatacollector.dao.MarketTradedVolumeDaoImpl;
import dk.betex.ecosystem.marketdatacollector.model.MarketDetails;
import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;
import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapModel;
import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapModel.HeatMapColumn;
import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapModel.HeatMapValue;
import dk.betex.ecosystem.webconsole.server.HeatMapModelDataSourceFactory;
import dk.betex.ecosystem.webconsole.server.HeatMapModelFactory;

/**
 * Calculates avgPrice for all runners based on totalTradedVolume w time window.
 * 
 * @author korzekwad
 * 
 */
public class EsperMarketTradedVolumeAvgPriceTestApp {

	private EPServiceProvider epService;

	private MarketTradedVolumeDao marketTradedVolueDao;
	private MarketDetailsDao marketDetailsDao;

	private String dbUrl = "10.2.2.72";
	private long marketId = 101081282l;
	private long twentyMinBeforeMarketTime;

	@Before
	public void before() {
		Configuration config = new Configuration();
		config.addEventTypeAutoName("dk.betex.ecosystem.marketdatacollector.model");

		epService = EPServiceProviderManager.getDefaultProvider(config);
		epService.initialize();

		/** Init DAOs */
		marketTradedVolueDao = new MarketTradedVolumeDaoImpl(new Database(dbUrl, "market_traded_volume"));
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
						"select timestamp,mtvEvent as lastEvent,prev(count(*)-1,mtvEvent) as firstEvent from MarketTradedVolume.win:ext_timed(timestamp,10 sec) as mtvEvent");
		statement.addListener(new EventLister());

		/** Get first 200 of records. */
		ViewAndDocumentsResult<BaseDocument, MarketTradedVolume> marketTradedVolumeList = marketTradedVolueDao
				.getMarketTradedVolume(marketId, twentyMinBeforeMarketTime, Long.MAX_VALUE, 200);
		for (ValueAndDocumentRow<BaseDocument, MarketTradedVolume> row : marketTradedVolumeList.getRows())
			epService.getEPRuntime().sendEvent(row.getDocument());

		/** Page through the rest of records. */
		while (marketTradedVolumeList.getRows().size() > 0) {
			marketTradedVolumeList = marketTradedVolueDao.getMarketTradedVolume(marketId, marketTradedVolumeList
					.getRows().get(marketTradedVolumeList.getRows().size() - 1).getDocument().getTimestamp() + 1,
					Long.MAX_VALUE, 200);

			for (ValueAndDocumentRow<BaseDocument, MarketTradedVolume> row : marketTradedVolumeList.getRows())
				epService.getEPRuntime().sendEvent(row.getDocument());
		}

		System.out.println("Processing market traded volume: " + (System.currentTimeMillis() - now));

	}

	private class EventLister implements UpdateListener {

		@Override
		public void update(EventBean[] newEvents, EventBean[] oldEvents) {
			MarketTradedVolume firstEvent = (MarketTradedVolume) newEvents[0].get("firstEvent");
			MarketTradedVolume lastEvent = (MarketTradedVolume) newEvents[0].get("lastEvent");
			double delta = lastEvent.getTotalTradedVolume() - firstEvent.getTotalTradedVolume();

			BioHeatMapModel firstModel = HeatMapModelFactory.createHeatMap(HeatMapModelDataSourceFactory
					.create(firstEvent), 0, 1);
			BioHeatMapModel lastModel = HeatMapModelFactory.createHeatMap(HeatMapModelDataSourceFactory
					.create(lastEvent), 0, 1);
			double modelDetlaValue = lastModel.getTotal() - firstModel.getTotal();
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
				/**Print avgPrice for a particular runner only.*/
				if(column.getLabel().equals("2370221"))
					System.out.println(newEvents.length + ":" + new Date(lastEvent.getTimestamp()) + ":"
							+ MathUtils.round(lastEvent.getTotalTradedVolume(), 2) + ":" + MathUtils.round(delta, 2) + ":"
							+ MathUtils.round(modelDetlaValue, 2) + ":" + MathUtils.round(modelDelta.getTotal(), 2) + ":" + MathUtils.round(sumOfPayouts / sumOfStakes,2));
			}

		}

	}
}
