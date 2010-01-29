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

/**
 * Process all marketTradedVolume events for market. Add event update listener that prints newly added event.
 * 
 * @author korzekwad
 * 
 */
public class EsperMarketTradedVolumeTestApp {

	private EPServiceProvider epService;
	
	private MarketTradedVolumeDao marketTradedVolueDao;
	private MarketDetailsDao marketDetailsDao;
	
	private String dbUrl = "10.2.2.72";
    private long marketId=101081282l;
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
		
		/**Get market time.*/
		MarketDetails marketDetails = marketDetailsDao.getMarketDetails(marketId);
		twentyMinBeforeMarketTime = marketDetails.getMarketTime() - (1000*60*20);
		
	}

	@Test
	public void testProcessMarketTradedVolume() {
		long now = System.currentTimeMillis();
		
		EPStatement statement = epService.getEPAdministrator().createEPL("select timestamp,totalTradedVolume,totalTradedVolume - prev(count(*) - 1, totalTradedVolume) as delta from MarketTradedVolume.win:ext_timed(timestamp,10 sec)");
		statement.addListener(new EventLister());
    		
		/** Get first 200 of records. */
		ViewAndDocumentsResult<BaseDocument, MarketTradedVolume> marketTradedVolumeList = marketTradedVolueDao
				.getMarketTradedVolume(marketId,twentyMinBeforeMarketTime, Long.MAX_VALUE, 200);
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
			Date timestamp = new Date((Long) newEvents[0].get("timestamp"));
			double totalTradedVolume = (Double) newEvents[0].get("totalTradedVolume");
			double delta = (Double)newEvents[0].get("delta");
			System.out.println(newEvents.length + ":" + timestamp + ":" + MathUtils.round(totalTradedVolume,2) + ":" + MathUtils.round(delta,2));
	
		}

	}
}
