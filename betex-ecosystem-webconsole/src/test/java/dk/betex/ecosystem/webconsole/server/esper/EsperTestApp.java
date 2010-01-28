package dk.betex.ecosystem.webconsole.server.esper;

import java.util.Date;

import org.jcouchdb.db.Database;
import org.jcouchdb.document.BaseDocument;
import org.jcouchdb.document.ValueAndDocumentRow;
import org.jcouchdb.document.ViewAndDocumentsResult;
import org.junit.Before;
import org.junit.Test;

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

/**
 * Process all marketTradedVolume events for market. Add event update listener that prints newly added event.
 * 
 * @author korzekwad
 * 
 */
public class EsperTestApp {

	private EPServiceProvider epService;
	
	private MarketTradedVolumeDao marketTradedVolueDao;
	private MarketPricesDao marketPricesDao;
	private MarketDetailsDao marketDetailsDao;
	
    private long marketId=101081282l;
    private long twentyMinBeforeMarketTime;

	@Before
	public void before() {
		epService = EPServiceProviderManager.getDefaultProvider();
		epService.initialize();
		
		EPStatement statement = epService.getEPAdministrator().createEPL("select 'marketTradedVolume' as eventType, timestamp,totalTradedVolume from dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume.win:ext_timed(timestamp,10 sec)");
		statement.addListener(new EventLister());
		EPStatement statement2 = epService.getEPAdministrator().createEPL("select 'marketPrices' as eventType,timestamp,totalTradedVolume from dk.betex.ecosystem.marketdatacollector.model.MarketPrices.win:ext_timed(timestamp,10 sec)");
		statement2.addListener(new EventLister());

		/** Init DAOs */
		marketTradedVolueDao = new MarketTradedVolumeDaoImpl(new Database("10.2.2.72", "market_traded_volume"));
		marketPricesDao = new MarketPricesDaoImpl(new Database("10.2.2.72", "market_prices"));
		marketDetailsDao = new MarketDetailsDaoImpl(new Database("10.2.2.72", "market_details"));
		
		/**Get market time.*/
		MarketDetails marketDetails = marketDetailsDao.getMarketDetails(marketId);
		twentyMinBeforeMarketTime = marketDetails.getMarketTime() - (1000*60*20);
		
	}

	@Test
	public void testProcessMarketTradedVolume() {
		long now = System.currentTimeMillis();
    		
		/** Get first 200 of records. */
		ViewAndDocumentsResult<BaseDocument, MarketTradedVolume> marketTradedVolumeList = marketTradedVolueDao
				.getMarketTradedVolume(marketId,twentyMinBeforeMarketTime, Long.MAX_VALUE, 200);
		for (ValueAndDocumentRow<BaseDocument, MarketTradedVolume> row : marketTradedVolumeList.getRows())
			epService.getEPRuntime().sendEvent(row.getDocument());

		/** Page through the rest of records. */
		while (marketTradedVolumeList.getRows().size() > 0) {
			marketTradedVolumeList = marketTradedVolueDao.getMarketTradedVolume(101081282l, marketTradedVolumeList
					.getRows().get(marketTradedVolumeList.getRows().size() - 1).getDocument().getTimestamp() + 1,
					Long.MAX_VALUE, 200);

			for (ValueAndDocumentRow<BaseDocument, MarketTradedVolume> row : marketTradedVolumeList.getRows())
				epService.getEPRuntime().sendEvent(row.getDocument());
		}

		System.out.println("Processing market traded volume: " + (System.currentTimeMillis() - now));

	}
	
	@Test
	public void testProcessMarketPrices() {
		long now = System.currentTimeMillis();
       
		/** Get first 200 of records. */
		ViewAndDocumentsResult<BaseDocument, MarketPrices> marketPricesList = marketPricesDao.get(marketId,twentyMinBeforeMarketTime, Long.MAX_VALUE, 200);
		for (ValueAndDocumentRow<BaseDocument, MarketPrices> row : marketPricesList.getRows())
			epService.getEPRuntime().sendEvent(row.getDocument());	

		/** Page through the rest of records. */
		while (marketPricesList.getRows().size() > 0) {
			marketPricesList = marketPricesDao.get(101081282l, marketPricesList
					.getRows().get(marketPricesList.getRows().size() - 1).getDocument().getTimestamp() + 1,
					Long.MAX_VALUE, 200);

			for (ValueAndDocumentRow<BaseDocument, MarketPrices> row : marketPricesList.getRows())
				epService.getEPRuntime().sendEvent(row.getDocument());	
		}

		System.out.println("Processing market prices: " + (System.currentTimeMillis() - now));

	}

	private class EventLister implements UpdateListener {

		@Override
		public void update(EventBean[] newEvents, EventBean[] oldEvents) {
			String eventType = (String)newEvents[0].get("eventType");
			Date timestamp = new Date((Long) newEvents[0].get("timestamp"));
			double totalTradedVolume = (Double) newEvents[0].get("totalTradedVolume");
			System.out.println(eventType + ":" + newEvents.length + ":" + timestamp + ":" + totalTradedVolume);
		}

	}
}
