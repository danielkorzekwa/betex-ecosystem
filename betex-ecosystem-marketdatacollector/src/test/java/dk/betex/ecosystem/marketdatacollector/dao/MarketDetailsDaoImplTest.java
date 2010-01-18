package dk.betex.ecosystem.marketdatacollector.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.jcouchdb.db.Database;
import org.jcouchdb.document.ViewResult;
import org.junit.Before;
import org.junit.Test;

import dk.betex.ecosystem.marketdatacollector.model.MarketDetails;
import dk.betex.ecosystem.marketdatacollector.model.MarketDetailsRunner;

public class MarketDetailsDaoImplTest {

	private MarketDetailsDao marketDetailsDao;
	private Database database;

	@Before
	public void setUp() {
		database = new Database("10.2.2.72", "market_details_test");
		marketDetailsDao = new MarketDetailsDaoImpl(database);
	}

	@Test
	public void testAddGetMarketDetails() {
		MarketDetails marketDetails = createMarketDetails(System.currentTimeMillis(), 1000);
		marketDetails.setId("" + marketDetails.getMarketId());
		marketDetailsDao.addMarketDetails(marketDetails);

		MarketDetails marketDetailsFromDb = marketDetailsDao.getMarketDetails(marketDetails.getMarketId());
		assertMarketDetails(marketDetails, marketDetailsFromDb);
	}

	@Test(expected = IllegalStateException.class)
	public void testAddAlreadyExist() {
		MarketDetails marketDetails = createMarketDetails(System.currentTimeMillis(), 1000);
		marketDetails.setId("" + marketDetails.getMarketId());
		marketDetailsDao.addMarketDetails(marketDetails);

		MarketDetails marketDetails2 = createMarketDetails(marketDetails.getMarketId(), 1000);
		marketDetails2.setId("" + marketDetails2.getMarketId());
		marketDetailsDao.addMarketDetails(marketDetails);
	}

	public void testGetNotExist() {
		assertNull(marketDetailsDao.getMarketDetails(-1111));
	}

	@Test
	public void testGetMarketDetailsList() throws InterruptedException {
		long time1 = System.currentTimeMillis();
		MarketDetails marketDetails1 = createMarketDetails(time1, time1);
		marketDetailsDao.addMarketDetails(marketDetails1);
		Thread.sleep(10);

		long time2 = System.currentTimeMillis();
		MarketDetails marketDetails2 = createMarketDetails(time2, time2);
		marketDetailsDao.addMarketDetails(marketDetails2);
		Thread.sleep(10);

		long time3 = System.currentTimeMillis();
		MarketDetails marketDetails3 = createMarketDetails(time3, time3);
		marketDetailsDao.addMarketDetails(marketDetails3);
		Thread.sleep(10);

		ViewResult<MarketDetails> result = marketDetailsDao.getMarketDetails(2);

		assertEquals(2, result.getRows().size());
		assertMarketDetails(marketDetails3, result.getRows().get(0).getValue());
		assertMarketDetails(marketDetails2, result.getRows().get(1).getValue());

	}

	private void assertMarketDetails(MarketDetails expected, MarketDetails actual) {

		assertEquals(expected.getMarketId(), actual.getMarketId());
		assertEquals(expected.getMenuPath(), actual.getMenuPath());
		assertEquals(expected.getMarketTime(), actual.getMarketTime());
		assertEquals(expected.getSuspendTime(), actual.getSuspendTime());

		assertEquals(expected.getRunners().get(0).getSelectionId(), actual.getRunners().get(0).getSelectionId());
		assertEquals(expected.getRunners().get(0).getSelectionName(), actual.getRunners().get(0).getSelectionName());

		assertEquals(expected.getRunners().get(1).getSelectionId(), actual.getRunners().get(1).getSelectionId());
		assertEquals(expected.getRunners().get(1).getSelectionName(), actual.getRunners().get(1).getSelectionName());

	}

	private MarketDetails createMarketDetails(long marketId, long marketTime) {
		MarketDetails marketDetails = new MarketDetails();
		marketDetails.setMarketId(marketId);
		marketDetails.setMarketTime(marketTime);
		marketDetails.setSuspendTime(marketTime - 1000);
		marketDetails.setMenuPath("uk/soccer/MatUtd vs Arsenal");

		List<MarketDetailsRunner> runners = new ArrayList<MarketDetailsRunner>();
		MarketDetailsRunner runner1 = new MarketDetailsRunner();
		runner1.setSelectionId(234);
		runner1.setSelectionName("Man Utd");
		runners.add(runner1);

		MarketDetailsRunner runner2 = new MarketDetailsRunner();
		runner2.setSelectionId(567);
		runner2.setSelectionName("Arsenal");
		runners.add(runner2);

		marketDetails.setRunners(runners);

		return marketDetails;
	}

}
