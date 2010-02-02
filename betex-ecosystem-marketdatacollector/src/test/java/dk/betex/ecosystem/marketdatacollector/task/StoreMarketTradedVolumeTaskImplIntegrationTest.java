package dk.betex.ecosystem.marketdatacollector.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import org.jcouchdb.document.BaseDocument;
import org.jcouchdb.document.ViewAndDocumentsResult;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dk.betex.ecosystem.marketdatacollector.dao.MarketDetailsDao;
import dk.betex.ecosystem.marketdatacollector.dao.MarketPricesDao;
import dk.betex.ecosystem.marketdatacollector.dao.MarketTradedVolumeDao;
import dk.betex.ecosystem.marketdatacollector.model.MarketDetails;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices;
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
	
	@Resource
	private MarketDetailsDao marketDetailsDao;
	
	@Resource
	private MarketPricesDao marketPricesDao;

	@BeforeClass
	public static void setUp() {
		/**
		 * In addition to the properties set below the following properties have to be set for this test to run:bfUser,
		 * bfPassword.
		 */
		System.setProperty("bfProductId", "82");
		System.setProperty("couchdb.url", "10.2.2.72");
		System.setProperty("marketTradedVolumeDb.name", "market_traded_volume_test");
		System.setProperty("marketDetailsDb.name", "market_details_test");
		System.setProperty("marketPricesDb.name", "market_prices_test");
	}

	@Test
	public void testExecuteRunTaskOnce() {
		BFMarketData hrMarket = getHRMarket();
		if (hrMarket == null) {
			fail("Cannot run test because of betfair market not found on the betting exchange");
		}
		
		 ViewAndDocumentsResult<BaseDocument,MarketTradedVolume> marketTradedVolumeBefore = marketTradedVolumeDao.getMarketTradedVolume(hrMarket.getMarketId(), 0,Long.MAX_VALUE,Integer.MAX_VALUE);
		 ViewAndDocumentsResult<BaseDocument,MarketPrices> marketPricesBefore = marketPricesDao.get(hrMarket.getMarketId(), 0, Long.MAX_VALUE,Integer.MAX_VALUE);
		
		storeMarketTradedVolumeTask.execute(Arrays.asList((long)hrMarket.getMarketId()));
		
		/**Check if market traded volume is stored in db*/
		 ViewAndDocumentsResult<BaseDocument,MarketTradedVolume> marketTradedVolumeAfter = marketTradedVolumeDao.getMarketTradedVolume(hrMarket.getMarketId(), 0,Long.MAX_VALUE,Integer.MAX_VALUE);
		 ViewAndDocumentsResult<BaseDocument,MarketPrices> marketPricesAfter = marketPricesDao.get(hrMarket.getMarketId(), 0, Long.MAX_VALUE,Integer.MAX_VALUE);
		
		assertEquals(marketTradedVolumeBefore.getRows().size()+1, marketTradedVolumeAfter.getRows().size());
		assertEquals(marketPricesBefore.getRows().size()+1, marketPricesAfter.getRows().size());
		
		/**Checking for market details.*/
		MarketDetails marketDetails = marketDetailsDao.getMarketDetails(hrMarket.getMarketId());
		assertNotNull("Market details is null",marketDetails);
		assertEquals(hrMarket.getMarketId(), marketDetails.getMarketId());
		assertTrue("No runners in marketDetails",marketDetails.getRunners().size()>0);
	}
	
	@Test
	public void testExecuteRunTaskTwice() {
		BFMarketData hrMarket = getHRMarket();
		if (hrMarket == null) {
			fail("Cannot run test because of betfair market not found on the betting exchange");
		}
		
		ViewAndDocumentsResult<BaseDocument,MarketTradedVolume> marketTradedVolumeBefore = marketTradedVolumeDao.getMarketTradedVolume(hrMarket.getMarketId(), 0,Long.MAX_VALUE,Integer.MAX_VALUE);
		ViewAndDocumentsResult<BaseDocument,MarketPrices> marketPricesBefore = marketPricesDao.get(hrMarket.getMarketId(), 0, Long.MAX_VALUE,Integer.MAX_VALUE);
		
		storeMarketTradedVolumeTask.execute(Arrays.asList((long)hrMarket.getMarketId()));
		storeMarketTradedVolumeTask.execute(Arrays.asList((long)hrMarket.getMarketId()));
		
		/**Check if market traded volume is stored in db*/
		 ViewAndDocumentsResult<BaseDocument,MarketTradedVolume> marketTradedVolumeAfter = marketTradedVolumeDao.getMarketTradedVolume(hrMarket.getMarketId(), 0,Long.MAX_VALUE,Integer.MAX_VALUE);
		 ViewAndDocumentsResult<BaseDocument,MarketPrices> marketPricesAfter = marketPricesDao.get(hrMarket.getMarketId(), 0, Long.MAX_VALUE,Integer.MAX_VALUE);
		
		assertEquals(marketTradedVolumeBefore.getRows().size()+2, marketTradedVolumeAfter.getRows().size());
		assertEquals(marketPricesBefore.getRows().size()+2, marketPricesAfter.getRows().size());
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
