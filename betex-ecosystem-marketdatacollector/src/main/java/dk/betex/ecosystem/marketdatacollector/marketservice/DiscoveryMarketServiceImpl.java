package dk.betex.ecosystem.marketdatacollector.marketservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import dk.bot.betfairservice.BetFairService;
import dk.bot.betfairservice.model.BFMarketData;

/**Discover markets that fulfil given criteria
 * 
 * @author korzekwad
 *
 */
public class DiscoveryMarketServiceImpl implements MarketService{

	private final BetFairService betFairService;
	
	private List<Long> marketIds = new ArrayList<Long>();

	private final Date dateFrom;

	private final Date dateTo;

	private final Set<Integer> eventTypeIds;

	private final String eventPath;

	private final boolean turningInPlay;

	/**
	 * 
	 * @param betFairService
	 * @param intervalInSec How often this service should refresh markets from the betting exchange
	 * @param dateFrom
	 * @param dateTo
	 * @param eventTypeIds
	 * @param eventPath
	 * @param turningInPlay If true then only markets that turns in play are returned.
	 */
	public DiscoveryMarketServiceImpl(BetFairService betFairService, long intervalInSec,Date dateFrom, Date dateTo, Set<Integer> eventTypeIds, String eventPath, boolean turningInPlay) {
		this.betFairService = betFairService;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
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
			
			List<BFMarketData> markets = betFairService.getMarkets(dateFrom, dateTo, eventTypeIds);
			List<Long> newMarketIds = new ArrayList<Long>();
			for(BFMarketData marketData:markets) {
				if(marketData.getEventHierarchy().startsWith(eventPath) && (!turningInPlay ||marketData.isTurningInPlay())) {
					newMarketIds.add((long)marketData.getMarketId());
				}
			}
			marketIds = newMarketIds;
		}
		
	}
}
