package dk.betex.ecosystem.marketdatacollector.marketservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.bot.betfairservice.BetFairService;
import dk.bot.betfairservice.model.BFMarketData;

/**
 * Discover markets that fulfil given criteria
 * 
 * @author korzekwad
 * 
 */
public class DiscoveryMarketServiceImpl implements MarketService {

	private static Logger logger = LoggerFactory.getLogger(DiscoveryMarketServiceImpl.class);

	private final BetFairService betFairService;

	private List<Long> marketIds = new ArrayList<Long>();

	private final Set<Integer> eventTypeIds;

	private final String eventPath;

	private final boolean turningInPlay;

	private final long startinMinutesFrom;

	private final long startinMinutesTo;

	private final long intervalInSec;

	/**
	 * 
	 * @param betFairService
	 * @param intervalInSec
	 *            How often this service should refresh markets from the betting exchange
	 * @param dateFrom
	 * @param dateTo
	 * @param eventTypeIds
	 * @param eventPath
	 * @param turningInPlay
	 *            If true then only markets that turns in play are returned.
	 */
	public DiscoveryMarketServiceImpl(BetFairService betFairService, long intervalInSec, int startinMinutesFrom,
			int startinMinutesTo, Set<Integer> eventTypeIds, String eventPath, boolean turningInPlay) {
		this.betFairService = betFairService;
		this.intervalInSec = intervalInSec;
		this.startinMinutesFrom = startinMinutesFrom;
		this.startinMinutesTo = startinMinutesTo;
		this.eventTypeIds = eventTypeIds;
		this.eventPath = eventPath;
		this.turningInPlay = turningInPlay;
	}

	/** Start discovering markets. */
	public void start() {
		Thread thread = new Thread(new MarketsPoller(), "MarketsPoller");
		thread.start();
	}

	@Override
	public List<Long> getMarketIds() {
		return marketIds;
	}

	private class MarketsPoller implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					
					Thread.sleep(intervalInSec);
					
					long now = System.currentTimeMillis();
					Date dateFrom = new Date(now + startinMinutesFrom * 60000l);
					Date dateTo = new Date(now + startinMinutesTo * 60000l);
					List<BFMarketData> markets = betFairService.getMarkets(dateFrom, dateTo, eventTypeIds);
					List<Long> newMarketIds = new ArrayList<Long>();
					for (BFMarketData marketData : markets) {
						if (marketData.getEventHierarchy().startsWith(eventPath)
								&& (!turningInPlay || marketData.isTurningInPlay())) {
							newMarketIds.add((long) marketData.getMarketId());
						}
					}
					marketIds = newMarketIds;
				} catch (Exception e) {
					logger.error("Discovery markets error", e);
				}
			}
		}

	}
}
