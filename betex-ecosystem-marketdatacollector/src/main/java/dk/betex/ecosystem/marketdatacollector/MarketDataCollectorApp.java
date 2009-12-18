package dk.betex.ecosystem.marketdatacollector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import dk.betex.ecosystem.marketdatacollector.task.StoreMarketTradedVolumeTask;

/** Java main application for data collector. */
public class MarketDataCollectorApp {

	public static void main(String[] args) throws IOException {

		long markeId = askForMarketId();
		long pollingInterval = askForPollingInterval();
		
		System.out.println("MarketDataCollector - starting....");
		
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("/StoreMarketTradedVolumeTask-spring.xml");
		StoreMarketTradedVolumeTask storeMarketTradedVolumeTask = (StoreMarketTradedVolumeTask)ctx.getBean("storeMarketTradedVolumeTask");
		
		MarketDataCollectorImpl marketDataCollector = new MarketDataCollectorImpl(markeId, pollingInterval, storeMarketTradedVolumeTask);
		marketDataCollector.start();
		
		System.out.println("MarketDataCollector - started.");

	}

	private static long askForMarketId() throws IOException {
		System.out.print("Enter market id: ");

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String marketIdData = reader.readLine();
		long marketId = -1;
		try {
			marketId = Long.parseLong(marketIdData);
		} catch (NumberFormatException e) {
			System.out.println("Can't parse marketId: " + marketIdData);
			System.exit(-1);
		}
		return marketId;
	}

	private static long askForPollingInterval() throws IOException {
		System.out.print("Enter polling interval[s]: ");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String intervalData = reader.readLine();
		long interval = -1;
		try {
			interval = Long.parseLong(intervalData) *1000;
			if(interval<1000) {
				System.out.println("Minumum interval is 1 second.");
				interval=1000;
			}
		} catch (NumberFormatException e) {
			System.out.println("Can't parse polling interval: " + intervalData);
			System.exit(-1);
		}
		
		return interval;
	}
}
