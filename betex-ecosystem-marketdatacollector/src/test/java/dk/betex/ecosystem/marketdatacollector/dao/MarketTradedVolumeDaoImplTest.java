package dk.betex.ecosystem.marketdatacollector.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.jcouchdb.db.Database;
import org.jcouchdb.document.ViewResult;
import org.junit.Before;
import org.junit.Test;

import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.PriceTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.RunnerTradedVolume;

public class MarketTradedVolumeDaoImplTest {

	private MarketTradedVolumeDao marketTradedVolueDao;
	private Database database;

	@Before
	public void setUp() {
		database = new Database("10.2.2.72", "market_traded_volume_test");
		marketTradedVolueDao = new MarketTradedVolumeDaoImpl(database);
	}

	@Test
	public void testAddGetMarketTradedVolume() {

		/** Add market traded volume to the couch db. */
		MarketTradedVolume marketTradedVolume = createMarketTradedVolume(1).get(0);
		marketTradedVolueDao.addMarketTradedVolume(marketTradedVolume);

		/** Get market traded volume from the couch db and check if it's correct. */
		ViewResult<MarketTradedVolume> marketTradedVolumeList = marketTradedVolueDao.getMarketTradedVolume(
				marketTradedVolume.getMarketId(), marketTradedVolume.getTimestamp(), marketTradedVolume.getTimestamp(),Integer.MAX_VALUE);

		assertEquals(1, marketTradedVolumeList.getRows().size());

		assertEquals(marketTradedVolume.getMarketId(), marketTradedVolumeList.getRows().get(0).getValue().getMarketId());
		assertEquals(marketTradedVolume.getTimestamp(), marketTradedVolumeList.getRows().get(0).getValue()
				.getTimestamp());

		assertEquals(marketTradedVolume.getRunnerTradedVolume().size(), marketTradedVolumeList.getRows().get(0)
				.getValue().getRunnerTradedVolume().size());

		for (int runnerIndex = 0; runnerIndex < marketTradedVolume.getRunnerTradedVolume().size(); runnerIndex++) {
			RunnerTradedVolume runnerTradedVolume = marketTradedVolume.getRunnerTradedVolume().get(runnerIndex);
			RunnerTradedVolume runnerTradedVolumeDB = marketTradedVolumeList.getRows().get(0).getValue()
					.getRunnerTradedVolume().get(runnerIndex);

			assertEquals(runnerTradedVolume.getSelectionId(), runnerTradedVolumeDB.getSelectionId());
			assertEquals(runnerTradedVolume.getPriceTradedVolume().size(), runnerTradedVolumeDB.getPriceTradedVolume()
					.size());

			for (int priceIndex = 0; priceIndex < runnerTradedVolume.getPriceTradedVolume().size(); priceIndex++) {
				PriceTradedVolume priceTradedVolume = runnerTradedVolume.getPriceTradedVolume().get(priceIndex);
				PriceTradedVolume priceTradedVolumeDB = runnerTradedVolumeDB.getPriceTradedVolume().get(priceIndex);

				assertEquals(priceTradedVolume.getPrice(), priceTradedVolumeDB.getPrice(), 0);
				assertEquals(priceTradedVolume.getTradedVolume(), priceTradedVolumeDB.getTradedVolume(), 0);
			}
		}

	}

	@Test
	public void testGetMarketTradedVolumeMarketWithTwoRecords() {
		/** Add market traded volume to the couch db. */
		List<MarketTradedVolume> marketTradedVolumeList = createMarketTradedVolume(2);
		marketTradedVolueDao.addMarketTradedVolume(marketTradedVolumeList.get(0));
		marketTradedVolueDao.addMarketTradedVolume(marketTradedVolumeList.get(1));

		/** Get market traded volume from the couch db and check if it's correct. */
		ViewResult<MarketTradedVolume> marketTradedVolume = marketTradedVolueDao.getMarketTradedVolume(
				marketTradedVolumeList.get(0).getMarketId(), marketTradedVolumeList.get(0).getTimestamp(),
				marketTradedVolumeList.get(1).getTimestamp(),Integer.MAX_VALUE);

		assertEquals(2, marketTradedVolume.getRows().size());

		assertEquals(marketTradedVolumeList.get(0).getMarketId(), marketTradedVolume.getRows().get(0).getValue()
				.getMarketId());
		assertEquals(marketTradedVolumeList.get(0).getTimestamp(), marketTradedVolume.getRows().get(0).getValue()
				.getTimestamp());

		assertEquals(marketTradedVolumeList.get(1).getMarketId(), marketTradedVolume.getRows().get(1).getValue()
				.getMarketId());
		assertEquals(marketTradedVolumeList.get(1).getTimestamp(), marketTradedVolume.getRows().get(1).getValue()
				.getTimestamp());

	}

	@Test
	public void testGetMarketTradedVolumeMarketWith20Records() {
		/** Add market traded volume to the couch db. */
		List<MarketTradedVolume> marketTradedVolumeList = createMarketTradedVolume(20);
		for (MarketTradedVolume marketTradedVolume : marketTradedVolumeList) {
			marketTradedVolueDao.addMarketTradedVolume(marketTradedVolume);
		}

		/** Get market traded volume from the couch db and check if it's correct. */
		ViewResult<MarketTradedVolume> marketTradedVolume = marketTradedVolueDao.getMarketTradedVolume(
				marketTradedVolumeList.get(0).getMarketId(), marketTradedVolumeList.get(0).getTimestamp(),
				marketTradedVolumeList.get(19).getTimestamp(),Integer.MAX_VALUE);

		assertEquals(20, marketTradedVolume.getRows().size());

	}

	@Test
	public void testGetMarketTradedVolumeMarketWith20RecordsGetAll() {
		/** Add market traded volume to the couch db. */
		List<MarketTradedVolume> marketTradedVolumeList = createMarketTradedVolume(20);
		for (MarketTradedVolume marketTradedVolume : marketTradedVolumeList) {
			marketTradedVolueDao.addMarketTradedVolume(marketTradedVolume);
		}

		/** Get market traded volume from the couch db and check if it's correct. */
		ViewResult<MarketTradedVolume> marketTradedVolume = marketTradedVolueDao.getMarketTradedVolume(
				marketTradedVolumeList.get(0).getMarketId(), 0, Long.MAX_VALUE,Integer.MAX_VALUE);

		assertEquals(20, marketTradedVolume.getRows().size());

	}
	
	@Test
	public void testGetMarketTradedVolumeMarketWith20RecordsGet10() {
		/** Add market traded volume to the couch db. */
		List<MarketTradedVolume> marketTradedVolumeList = createMarketTradedVolume(20);
		for (MarketTradedVolume marketTradedVolume : marketTradedVolumeList) {
			marketTradedVolueDao.addMarketTradedVolume(marketTradedVolume);
		}

		/** Get market traded volume from the couch db and check if it's correct. */
		ViewResult<MarketTradedVolume> marketTradedVolume = marketTradedVolueDao.getMarketTradedVolume(
				marketTradedVolumeList.get(0).getMarketId(), 0, Long.MAX_VALUE,10);

		assertEquals(10, marketTradedVolume.getRows().size());

	}
	
	@Test
	public void testGetNumOfRecordsWith20Records() {
		/** Add market traded volume to the couch db. */
		List<MarketTradedVolume> marketTradedVolumeList = createMarketTradedVolume(24);
		for (MarketTradedVolume marketTradedVolume : marketTradedVolumeList) {
			marketTradedVolueDao.addMarketTradedVolume(marketTradedVolume);
		}

		/** Get market traded volume from the couch db and check if it's correct. */
		long numOfRecords = marketTradedVolueDao.getNumOfRecords(marketTradedVolumeList.get(0).getMarketId());

		assertEquals(24, numOfRecords);
	}
	
	@Test
	public void testGetNumOfRecordsWith0Records() {
		
		/** Get market traded volume from the couch db and check if it's correct. */
		long numOfRecords = marketTradedVolueDao.getNumOfRecords(-1234);

		assertEquals(0, numOfRecords);
	}
	

	/**
	 * Generates timestamped marketTradedVolume records for one market.
	 * 
	 * @param numberOfRecords
	 *            Number of timestamped records to be generated
	 * @return
	 */
	private List<MarketTradedVolume> createMarketTradedVolume(int numberOfRecords) {

		/** Sleep for a while to guarantee unique marketId. */
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		long marketId = System.currentTimeMillis();

		List<MarketTradedVolume> marketTradedVolumeList = new ArrayList<MarketTradedVolume>();
		for (int i = 0; i < numberOfRecords; i++) {
			List<RunnerTradedVolume> runnersTradedVolume = new ArrayList<RunnerTradedVolume>();

			List<PriceTradedVolume> pricesTradedVolume = new ArrayList<PriceTradedVolume>();
			pricesTradedVolume.add(new PriceTradedVolume(2.1, 35.32));
			pricesTradedVolume.add(new PriceTradedVolume(2.2, 765.56));
			runnersTradedVolume.add(new RunnerTradedVolume(105, pricesTradedVolume));

			pricesTradedVolume = new ArrayList<PriceTradedVolume>();
			pricesTradedVolume.add(new PriceTradedVolume(3.4, 43.24));
			pricesTradedVolume.add(new PriceTradedVolume(3.6, 65.12));
			runnersTradedVolume.add(new RunnerTradedVolume(106, pricesTradedVolume));

			/** Sleep for a while to guarantee unique timestamp */
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			MarketTradedVolume marketTradedVolume = new MarketTradedVolume(marketId, runnersTradedVolume, System
					.currentTimeMillis());
			marketTradedVolumeList.add(marketTradedVolume);
		}

		return marketTradedVolumeList;
	}
}
