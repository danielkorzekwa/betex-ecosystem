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

import dk.betex.ecosystem.marketdatacollector.dao.MarketTradedVolumeDao;
import dk.betex.ecosystem.marketdatacollector.dao.MarketTradedVolumeDaoImpl;
import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;

/**
 * Process all marketTradedVolume events for market. Add event update listener that prints newly added event.
 * 
 * @author korzekwad
 * 
 */
public class EsperTestApp {

	private EPServiceProvider epService;
	private EPStatement statement;

	private MarketTradedVolumeDao marketTradedVolueDao;

	@Before
	public void before() {
		epService = EPServiceProviderManager.getDefaultProvider();
		String expression = "select timestamp,totalTradedVolume from dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume.win:ext_timed(timestamp,10 sec)";
		EPStatement statement = epService.getEPAdministrator().createEPL(expression);
		statement.addListener(new EventLister());

		/** Init DAOs */
		marketTradedVolueDao = new MarketTradedVolumeDaoImpl(new Database("10.2.2.72", "market_traded_volume"));
	}

	@Test
	public void test() {
		long now = System.currentTimeMillis();

		/** Get first 200 of records. */
		ViewAndDocumentsResult<BaseDocument, MarketTradedVolume> marketTradedVolumeList = marketTradedVolueDao
				.getMarketTradedVolume(101081282l, 0, Long.MAX_VALUE, 50);
		process(marketTradedVolumeList);

		/** Page through the rest of records. */
		while (marketTradedVolumeList.getRows().size() > 0) {
			marketTradedVolumeList = marketTradedVolueDao.getMarketTradedVolume(101081282l, marketTradedVolumeList
					.getRows().get(marketTradedVolumeList.getRows().size() - 1).getDocument().getTimestamp() + 1,
					Long.MAX_VALUE, 50);

			process(marketTradedVolumeList);
		}

		System.out.println(System.currentTimeMillis() - now);

	}

	private void process(ViewAndDocumentsResult<BaseDocument, MarketTradedVolume> marketTradedVolumeList) {

		for (ValueAndDocumentRow<BaseDocument, MarketTradedVolume> row : marketTradedVolumeList.getRows())
			epService.getEPRuntime().sendEvent(row.getDocument());
	}

	private class EventLister implements UpdateListener {

		@Override
		public void update(EventBean[] newEvents, EventBean[] oldEvents) {
			Date timestamp = new Date((Long)newEvents[0].get("timestamp"));
			double totalTradedVolume = (Double)newEvents[0].get("totalTradedVolume");
			System.out.println(newEvents.length + ":" + timestamp + ":" + totalTradedVolume);
		}

	}
}
