package dk.betex.ecosystem.marketdatacollector;

import java.util.concurrent.atomic.AtomicBoolean;

import dk.betex.ecosystem.marketdatacollector.task.StoreMarketTradedVolumeTask;

/**
 * Continuously collects market traded volume for a betting exchange market and stores it in a database.
 * 
 * @author korzekwad
 * 
 */
public class MarketDataCollectorImpl implements MarketDataCollector {

	/** What is the market that the traded volume is collected for. */
	private final long marketId;

	/** How often the traded volume for that market is collected. */
	private final long intervalInMillis;

	private final StoreMarketTradedVolumeTask storeMarketTradedVolumeTask;

	private AtomicBoolean stopped = new AtomicBoolean(false);

	/**
	 * 
	 * @param marketId
	 *            What is the market that the traded volume is collected for
	 * @param intervalInMillis
	 *            How often the traded volume for that market is collected
	 * @param storeMarketTradedVolumeTask
	 */
	public MarketDataCollectorImpl(long marketId, long intervalInMillis,
			StoreMarketTradedVolumeTask storeMarketTradedVolumeTask) {
		this.marketId = marketId;
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
			try {

				while (!stopped.get()) {
					storeMarketTradedVolumeTask.execute(marketId);
					Thread.sleep(intervalInMillis);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
