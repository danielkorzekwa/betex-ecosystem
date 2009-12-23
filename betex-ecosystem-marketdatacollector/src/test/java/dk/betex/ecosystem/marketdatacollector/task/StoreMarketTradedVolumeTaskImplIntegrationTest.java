package dk.betex.ecosystem.marketdatacollector.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import org.jcouchdb.document.ViewResult;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dk.betex.ecosystem.marketdatacollector.dao.MarketTradedVolumeDao;
import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;
import dk.bot.betfairservice.BetFairService;
import dk.bot.betfairservice.model.BFMarketData;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/StoreMarketTradedVolumeTask-spring.xml")
public class StoreMarketTradedVolumeTaskImplIntegrationTest {

	@Resource
	private StoreMarketTradedVolumeTask storeMarketTradedVolumeTask;

	@Resource
	private BetFairService betfairService;
	
	@Resource
	private MarketTradedVolumeDao marketTradedVolumeDao;

	@BeforeClass
	public static void setUp() {
		/**
		 * In addition to the properties set below the following properties have to be set for this test to run:bfUser,
		 * bfPassword.
		 */
		System.setProperty("bfProductId", "82");
		System.setProperty("marketTradedVolumeDb.url", "10.2.2.72");
		System.setProperty("marketTradedVolumeDb.name", "market_traded_volume_test");
	}

	@Test
	public void testExecuteRunTaskOnce() {
		BFMarketData hrMarket = getHRMarket();
		if (hrMarket == null) {
			fail("Cannot run test because of betfair market not found on the betting exchange");
		}
		
		ViewResult<MarketTradedVolume> marketTradedVolumeBefore = marketTradedVolumeDao.getMarketTradedVolume(hrMarket.getMarketId(), 0,Long.MAX_VALUE,Integer.MAX_VALUE);
		
		storeMarketTradedVolumeTask.execute(hrMarket.getMarketId());
		
		/**Check if market traded volume is stored in db*/
		ViewResult<MarketTradedVolume> marketTradedVolumeAfter = marketTradedVolumeDao.getMarketTradedVolume(hrMarket.getMarketId(), 0,Long.MAX_VALUE,Integer.MAX_VALUE);
		
		assertEquals(marketTradedVolumeBefore.getRows().size()+1, marketTradedVolumeAfter.getRows().size());
	}
	
	@Test
	public void testExecuteRunTaskTwice() {
		BFMarketData hrMarket = getHRMarket();
		if (hrMarket == null) {
			fail("Cannot run test because of betfair market not found on the betting exchange");
		}
		
		ViewResult<MarketTradedVolume> marketTradedVolumeBefore = marketTradedVolumeDao.getMarketTradedVolume(hrMarket.getMarketId(), 0,Long.MAX_VALUE,Integer.MAX_VALUE);
		
		storeMarketTradedVolumeTask.execute(hrMarket.getMarketId());
		storeMarketTradedVolumeTask.execute(hrMarket.getMarketId());
		
		/**Check if market traded volume is stored in db*/
		ViewResult<MarketTradedVolume> marketTradedVolumeAfter = marketTradedVolumeDao.getMarketTradedVolume(hrMarket.getMarketId(), 0,Long.MAX_VALUE,Integer.MAX_VALUE);
		
		assertEquals(marketTradedVolumeBefore.getRows().size()+2, marketTradedVolumeAfter.getRows().size());
	}

	/** Find and return HR market from betfair or null if not able to find it. */
	private BFMarketData getHRMarket() {
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
