package dk.betex.ecosystem.marketdatacollector.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.jcouchdb.db.Database;
import org.jcouchdb.document.ViewResult;
import org.junit.Before;
import org.junit.Test;

import dk.betex.ecosystem.marketdatacollector.model.MarketPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.PriceTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.RunnerTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices.PriceUnmatchedVolume;

public class MarketPricesDaoImplTest {

	private MarketPricesDao marketPricesDao;
	private Database database;

	@Before
	public void setUp() {
		database = new Database("10.2.2.72", "market_prices_test");
		marketPricesDao = new MarketPricesDaoImpl(database);
	}

	@Test
	public void testAddGetMarketPrices() {

		/** Add market prices to the couch db. */
		MarketPrices marketPrices = createMarketPrices(1).get(0);
		marketPricesDao.add(marketPrices);

		/** Get market prices from the couch db and check if it's correct. */
		ViewResult<MarketPrices> marketPricesFromDb = marketPricesDao.get(marketPrices.getMarketId(), marketPrices
				.getTimestamp(), marketPrices.getTimestamp(), Integer.MAX_VALUE);
		assertEquals(1, marketPricesFromDb.getRows().size());

		assertEquals(marketPrices.getMarketId(), marketPricesFromDb.getRows().get(0).getValue().getMarketId());
		assertEquals(marketPrices.getTimestamp(), marketPricesFromDb.getRows().get(0).getValue().getTimestamp());
		assertEquals(marketPrices.getInPlayDelay(), marketPricesFromDb.getRows().get(0).getValue().getInPlayDelay());

		assertEquals(marketPrices.getRunnerPrices().size(), marketPricesFromDb.getRows().get(0).getValue()
				.getRunnerPrices().size());

		for (int runnerPriceIndex = 0; runnerPriceIndex < marketPrices.getRunnerPrices().size(); runnerPriceIndex++) {
			RunnerPrices runnerPrices = marketPrices.getRunnerPrices().get(runnerPriceIndex);
			RunnerPrices runnerPricesFromDb = marketPricesFromDb.getRows().get(0).getValue().getRunnerPrices().get(
					runnerPriceIndex);

			assertEquals(runnerPrices.getSelectionId(), runnerPricesFromDb.getSelectionId());
			assertEquals(runnerPrices.getActualSP(), runnerPricesFromDb.getActualSP(), 0);
			assertEquals(runnerPrices.getFarSP(), runnerPricesFromDb.getFarSP(), 0);
			assertEquals(runnerPrices.getNearSP(), runnerPricesFromDb.getNearSP(), 0);
			assertEquals(runnerPrices.getLastPriceMatched(), runnerPricesFromDb.getLastPriceMatched(), 0);
			assertEquals(runnerPrices.getTotalAmountMatched(), runnerPricesFromDb.getTotalAmountMatched(), 0);

			for (int priceIndex = 0; priceIndex < runnerPrices.getPrices().size(); priceIndex++) {
				PriceUnmatchedVolume priceVolume = runnerPrices.getPrices().get(priceIndex);
				PriceUnmatchedVolume priceVolumeFromDb = runnerPricesFromDb.getPrices().get(priceIndex);

				assertEquals(priceVolume.getPrice(), priceVolumeFromDb.getPrice(), 0);
				assertEquals(priceVolume.getTotalToBack(), priceVolumeFromDb.getTotalToBack(), 0);
				assertEquals(priceVolume.getTotalToLay(), priceVolumeFromDb.getTotalToLay(), 0);
			}
		}
	}

	@Test
	public void testAddGetMarketPricesWithTwoRecords() {

		/** Add market prices to the couch db. */
		List<MarketPrices> marketPricesList = createMarketPrices(2);
		marketPricesDao.add(marketPricesList.get(0));
		marketPricesDao.add(marketPricesList.get(1));

		/** Get market prices from the couch db and check if it's correct. */
		ViewResult<MarketPrices> marketPricesFromDb = marketPricesDao.get(marketPricesList.get(0).getMarketId(),
				marketPricesList.get(0).getTimestamp(), marketPricesList.get(1).getTimestamp(), Integer.MAX_VALUE);

		assertEquals(2, marketPricesFromDb.getRows().size());

		assertEquals(marketPricesList.get(0).getMarketId(), marketPricesFromDb.getRows().get(0).getValue()
				.getMarketId());
		assertEquals(marketPricesList.get(0).getTimestamp(), marketPricesFromDb.getRows().get(0).getValue()
				.getTimestamp());

		assertEquals(marketPricesList.get(1).getMarketId(), marketPricesFromDb.getRows().get(1).getValue()
				.getMarketId());
		assertEquals(marketPricesList.get(1).getTimestamp(), marketPricesFromDb.getRows().get(1).getValue()
				.getTimestamp());
	}

	@Test
	public void testAddGetMarketPricesWith20Records() {

		/** Add market prices to the couch db. */
		List<MarketPrices> marketPricesList = createMarketPrices(20);
		for (MarketPrices marketPrices : marketPricesList) {
			marketPricesDao.add(marketPrices);
		}

		/** Get market prices from the couch db and check if it's correct. */
		ViewResult<MarketPrices> marketPricesFromDb = marketPricesDao.get(marketPricesList.get(0).getMarketId(),
				marketPricesList.get(0).getTimestamp(), marketPricesList.get(19).getTimestamp(), Integer.MAX_VALUE);

		assertEquals(20, marketPricesFromDb.getRows().size());

	}
	
	@Test
	public void testAddGetMarketPricesWith20RecordsGetAll() {

		/** Add market prices to the couch db. */
		List<MarketPrices> marketPricesList = createMarketPrices(20);
		for (MarketPrices marketPrices : marketPricesList) {
			marketPricesDao.add(marketPrices);
		}

		/** Get market prices from the couch db and check if it's correct. */
		ViewResult<MarketPrices> marketPricesFromDb = marketPricesDao.get(marketPricesList.get(0).getMarketId(),
				0, Long.MAX_VALUE, Integer.MAX_VALUE);

		assertEquals(20, marketPricesFromDb.getRows().size());

	}
	
	@Test
	public void testAddGetMarketPricesWith20RecordsGet10() {

		/** Add market prices to the couch db. */
		List<MarketPrices> marketPricesList = createMarketPrices(20);
		for (MarketPrices marketPrices : marketPricesList) {
			marketPricesDao.add(marketPrices);
		}

		/** Get market prices from the couch db and check if it's correct. */
		ViewResult<MarketPrices> marketPricesFromDb = marketPricesDao.get(marketPricesList.get(0).getMarketId(),
				0, Long.MAX_VALUE, 10);

		assertEquals(10, marketPricesFromDb.getRows().size());

	}
	
	@Test
	public void testGetNumOfRecordsWith24Records() {
		/** Add market prices to the couch db. */
		List<MarketPrices> marketPricesList = createMarketPrices(24);
		for (MarketPrices marketPrices : marketPricesList) {
			marketPricesDao.add(marketPrices);
		}

		/** Get number of market price records from the couch db and check if it's correct. */
		long numOfRecords = marketPricesDao.getNumOfRecords(marketPricesList.get(0).getMarketId());

		assertEquals(24, numOfRecords);
	}
	
	@Test
	public void testGetNumOfRecordsWith0Records() {
		
		/**Get number of market price records from the couch db and check if it's correct.*/
		long numOfRecords = marketPricesDao.getNumOfRecords(-1234);
		assertEquals(0, numOfRecords);
	}
	
	@Test
	public void testGetTimeRange() {
		/** Add market prices to the couch db. */
		List<MarketPrices> marketPricesList = createMarketPrices(24);
		for (MarketPrices marketPrices : marketPricesList) {
			marketPricesDao.add(marketPrices);
		}

		/** Get time range of market prices for market and check if it's correct. */
		List<Long> timeRange = marketPricesDao.getTimeRange(marketPricesList.get(0).getMarketId());

		assertEquals(marketPricesList.get(0).getTimestamp(), timeRange.get(0).longValue());
		assertEquals(marketPricesList.get(marketPricesList.size()-1).getTimestamp(), timeRange.get(1).longValue());
	}
	
	@Test
	public void testGetTimeRangeMarketPricesNotFound() {
	
		/** Get time range of market prices for market and check if it's correct. */
		List<Long> timeRange = marketPricesDao.getTimeRange(-1234);
		assertNull(timeRange);
	}
	

	/**
	 * Generates timestamped marketPrices records for one market.
	 * 
	 * @param numberOfRecords
	 *            Number of timestamped records to be generated
	 * @return
	 */
	private List<MarketPrices> createMarketPrices(int numberOfRecords) {

		/** Sleep for a while to guarantee unique marketId. */
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		long marketId = System.currentTimeMillis();

		List<MarketPrices> marketPricesList = new ArrayList<MarketPrices>();
		for (int i = 0; i < numberOfRecords; i++) {

			/** Sleep for a while to guarantee unique timestamp */
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			MarketPrices marketPrices = new MarketPrices();
			marketPrices.setMarketId(marketId);
			marketPrices.setInPlayDelay(5);
			marketPrices.setTimestamp(System.currentTimeMillis());
			marketPrices.setRunnerPrices(createRunnerPrices(5));

			marketPricesList.add(marketPrices);
		}

		return marketPricesList;
	}

	private List<RunnerPrices> createRunnerPrices(int numOfRecords) {
		List<RunnerPrices> runnerPricesList = new ArrayList<RunnerPrices>();
		for (int i = 0; i < numOfRecords; i++) {
			RunnerPrices runnerPrices = new RunnerPrices();
			runnerPrices.setSelectionId(123);
			runnerPrices.setActualSP(2.3);
			runnerPrices.setFarSP(5.56);
			runnerPrices.setNearSP(3.23);
			runnerPrices.setLastPriceMatched(2.35);
			runnerPrices.setTotalAmountMatched(432.45);
			runnerPrices.setPrices(createPrices(5));
			runnerPricesList.add(runnerPrices);
		}
		return runnerPricesList;
	}

	private List<PriceUnmatchedVolume> createPrices(int numOfRecords) {
		List<PriceUnmatchedVolume> priceVolumeList = new ArrayList<PriceUnmatchedVolume>();
		for (int i = 0; i < numOfRecords; i++) {
			PriceUnmatchedVolume priceVolume = new PriceUnmatchedVolume();
			priceVolume.setPrice(1d / ((double) i + 1));
			priceVolume.setTotalToBack(i * 2);
			priceVolume.setTotalToLay(i * 4);
			priceVolumeList.add(priceVolume);
		}
		return priceVolumeList;
	}
}
