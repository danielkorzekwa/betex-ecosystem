package dk.betex.ecosystem.marketdatacollector.marketservice;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import dk.bot.betfairservice.BetFairService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/StoreMarketTradedVolumeTask-spring.xml")
public class DiscoveryMarketServiceImplIntegrationTest {

	@Resource
	private BetFairService betfairService;

	private DiscoveryMarketServiceImpl marketService;

	@BeforeClass
	public static void setUpBeforeClass() {
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

	@Before
	public void setUp() {
		long now = System.currentTimeMillis();
		 Set<Integer> eventIds = new HashSet<Integer>();
		 eventIds.add(7);
		marketService = new DiscoveryMarketServiceImpl(betfairService, 1,-240, 60*24*2, eventIds, "/7/298251/", true,true);
		
		marketService.start();
	}

	@Test
	public void testGetMarketIds() throws InterruptedException {
		Thread.sleep(3000);
		
		assertTrue("No markets are returned.",marketService.getMarketIds().size()>0);
	}

}
