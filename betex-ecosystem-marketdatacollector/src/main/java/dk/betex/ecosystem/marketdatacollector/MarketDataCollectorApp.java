package dk.betex.ecosystem.marketdatacollector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import dk.betex.ecosystem.marketdatacollector.marketservice.DiscoveryMarketServiceImpl;
import dk.betex.ecosystem.marketdatacollector.marketservice.MarketService;
import dk.betex.ecosystem.marketdatacollector.marketservice.OneMarketServiceImpl;
import dk.betex.ecosystem.marketdatacollector.task.StoreMarketTradedVolumeTask;
import dk.bot.betfairservice.BetFairService;

/** Java main application for data collector. */
public class MarketDataCollectorApp {

	public static void main(String[] args) throws IOException {

		/** Set config properties. */
		System.setProperty("marketDetailsDb.name", DbNames.MARKET_DETAILS.getDbName());
		System.setProperty("marketPricesDb.name", DbNames.MARKET_PRICES.getDbName());

		Long marketId = askForMarketId();
		long pollingInterval = askForPollingInterval();

		System.out.println("MarketDataCollector - starting....");

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				"/StoreMarketTradedVolumeTask-spring.xml");
		StoreMarketTradedVolumeTask storeMarketTradedVolumeTask = (StoreMarketTradedVolumeTask) ctx
				.getBean("storeMarketTradedVolumeTask");

		MarketService marketService;
		if (marketId != null) {
			marketService = new OneMarketServiceImpl(marketId);
		} else {
			BetFairService betfairService = (BetFairService) ctx.getBean("betfairService");
			Set<Integer> eventIds = new HashSet<Integer>();
			eventIds.add(7);
			marketService = new DiscoveryMarketServiceImpl(betfairService, 60, -60 * 2, 12, eventIds, "/7/298251/",
					true);
			((DiscoveryMarketServiceImpl)marketService).start();
		}
		MarketDataCollectorImpl marketDataCollector = new MarketDataCollectorImpl(marketService, pollingInterval,
				storeMarketTradedVolumeTask);
		marketDataCollector.start();

		System.out.println("MarketDataCollector - started.");

	}

	/**
	 * 
	 * @return null if market id is not provded
	 * @throws IOException
	 */
	private static Long askForMarketId() throws IOException {
		System.out
				.print("Enter market id (or none to collect all HR_UK_turningInPlay markets 10 min before market time): ");

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String marketIdData = reader.readLine();
		Long marketId = null;
		if (marketIdData.length() > 0) {
			try {
				marketId = Long.parseLong(marketIdData);
			} catch (NumberFormatException e) {
				System.out.println("Can't parse marketId: " + marketIdData);
				System.exit(-1);
			}
		} else {
			System.out.println("HR markets will be collected.");
		}
		return marketId;
	}

	private static long askForPollingInterval() throws IOException {
		System.out.print("Enter polling interval[s]: ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String intervalData = reader.readLine();
		long interval = -1;
		try {
			interval = Long.parseLong(intervalData) * 1000;
			if (interval < 1000) {
				System.out.println("Minumum interval is 1 second.");
				interval = 1000;
			}
		} catch (NumberFormatException e) {
			System.out.println("Can't parse polling interval: " + intervalData);
			System.exit(-1);
		}

		return interval;
	}
}
