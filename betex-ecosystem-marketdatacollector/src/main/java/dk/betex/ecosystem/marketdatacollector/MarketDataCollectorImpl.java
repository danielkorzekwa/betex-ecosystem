package dk.betex.ecosystem.marketdatacollector;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.betex.ecosystem.marketdatacollector.marketservice.MarketService;
import dk.betex.ecosystem.marketdatacollector.task.StoreMarketTradedVolumeTask;

/**
 * Continuously collects market traded volume for a betting exchange market and stores it in a database.
 * 
 * @author korzekwad
 * 
 */
public class MarketDataCollectorImpl implements MarketDataCollector {

	private static Logger logger = LoggerFactory.getLogger(MarketDataCollectorImpl.class);

	/** How often the traded volume for that market is collected. */
	private final long intervalInMillis;

	private final StoreMarketTradedVolumeTask storeMarketTradedVolumeTask;

	private AtomicBoolean stopped = new AtomicBoolean(false);

	/**Provides list of markets that the market data is collected for.*/
	private final MarketService marketService;

	/**
	 * 
	 * @param marketService
	 *            Provides list of markets that the market data is collected for
	 * @param intervalInMillis
	 *            How often the traded volume for that market is collected
	 * @param storeMarketTradedVolumeTask
	 */
	public MarketDataCollectorImpl(MarketService marketService, long intervalInMillis,
			StoreMarketTradedVolumeTask storeMarketTradedVolumeTask) {
		
		this.marketService = marketService;
		this.intervalInMillis = intervalInMillis;
		this.storeMarketTradedVolumeTask = storeMarketTradedVolumeTask;
	}

	/** Start collecting market traded volume. */
	public void start() {
		Thread thread = new Thread(new MarketTradedVolumePoller(), "MarketTradedVolumePoller");
		thread.start();
	}

	/** Stop collecting market traded volume. It can't be restarted */
	public void stop() {
		stopped.set(true);
	}

	private class MarketTradedVolumePoller implements Runnable {

		@Override
		public void run() {

			while (!stopped.get()) {
				try {
					Thread.sleep(intervalInMillis);
					storeMarketTradedVolumeTask.execute(marketService.getMarketIds());
				} catch (Exception e) {
					logger.error("MArketDataCollector error", e);
				}
			}

		}
	}
}
