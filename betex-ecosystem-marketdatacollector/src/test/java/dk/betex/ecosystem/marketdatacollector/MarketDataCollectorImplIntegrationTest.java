package dk.betex.ecosystem.marketdatacollector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import org.jcouchdb.document.ViewResult;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dk.betex.ecosystem.marketdatacollector.dao.MarketTradedVolumeDao;
import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;
import dk.betex.ecosystem.marketdatacollector.task.StoreMarketTradedVolumeTask;
import dk.bot.betfairservice.BetFairService;
import dk.bot.betfairservice.model.BFMarketData;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class MarketDataCollectorImplIntegrationTest {

	@Resource
	private StoreMarketTradedVolumeTask storeMarketTradedVolumeTask;
	@Resource
	private BetFairService betfairService;
	@Resource
	private MarketTradedVolumeDao marketTradedVolumeDao;

	private MarketDataCollectorImpl marketDataCollector;
	private BFMarketData hrMarket;

	@BeforeClass
	public static void setUpBeforeClass() {
		/**
		 * In addition to the properties set below the following properties have to be set for this test to run:bfUser,
		 * bfPassword.
		 */
		System.setProperty("bfProductId", "82");
		System.setProperty("marketTradedVolumeDb.url", "10.2.2.72");
		System.setProperty("marketTradedVolumeDb.name", "market_traded_volume_test");
	}

	@Before
	public void setUp() {
		hrMarket = getHRMarket();
		if (hrMarket == null) {
			fail("Cannot run test because of betfair market not found on the betting exchange");
		}

		marketDataCollector = new MarketDataCollectorImpl(hrMarket.getMarketId(), 500, storeMarketTradedVolumeTask);
		marketDataCollector.start();
	}

	@Test
	public void test() throws Exception {
		/** Check if market traded volume is stored in database */
		ViewResult<MarketTradedVolume> marketTradedVolumeBefore = marketTradedVolumeDao.getMarketTradedVolume(hrMarket
				.getMarketId(), 0, Long.MAX_VALUE);

		/** Wait 3 seconds, then check if some traded volume records are written to the database. */
		Thread.sleep(3000);
		ViewResult<MarketTradedVolume> marketTradedVolumeAfter = marketTradedVolumeDao.getMarketTradedVolume(hrMarket
				.getMarketId(), 0, Long.MAX_VALUE);
		assertTrue("Atleast 4 marketTradedVolume records should be stored in the database.", marketTradedVolumeAfter
				.getRows().size() >= marketTradedVolumeBefore.getRows().size() + 4);

		/**
		 * Test stopping
		 * 
		 */
		marketDataCollector.stop();
		Thread.sleep(1000);
		int before = marketTradedVolumeDao.getMarketTradedVolume(hrMarket.getMarketId(), 0, Long.MAX_VALUE).getRows()
				.size();
		Thread.sleep(3000);
		int after = marketTradedVolumeDao.getMarketTradedVolume(hrMarket.getMarketId(), 0, Long.MAX_VALUE).getRows()
				.size();
		assertEquals(before, after);
	}

	/** Find and return HR market from betfair or null if not able to find it. */
	private BFMarketData getHRMarket() {

		/** Find market */
		long now = System.currentTimeMillis();
		HashSet<Integer> eventIds = new HashSet<Integer>();
		eventIds.add(7);
		List<BFMarketData> markets = betfairService.getMarkets(new Date(now - (1000 * 3600 * 24 * 7)), new Date(now
				+ (1000 * 3600 * 24 * 7)), eventIds);
		for (BFMarketData market : markets) {
			if (market.isBsbMarket() && market.isTurningInPlay()) {
				return market;
			}
		}
		return null;
	}

}
