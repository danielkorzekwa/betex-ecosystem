package dk.betex.ecosystem.marketdatacollector.task;

import java.util.Date;

import dk.betex.ecosystem.marketdatacollector.dao.MarketTradedVolumeDao;
import dk.betex.ecosystem.marketdatacollector.factory.MarketTradedVolumeFactory;
import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;
import dk.bot.betfairservice.BetFairService;
import dk.bot.betfairservice.model.BFMarketTradedVolume;

/** Get market traded volume from Betfair betting exchange and store it in a database.
 * 
 * @author korzekwad
 *
 */
public class StoreMarketTradedVolumeTaskImpl implements StoreMarketTradedVolumeTask{

	private final BetFairService betfairService;
	private final MarketTradedVolumeDao marketTradedVolumeDao;

	public StoreMarketTradedVolumeTaskImpl(BetFairService betfairService, MarketTradedVolumeDao marketTradedVolumeDao) {
		this.betfairService = betfairService;
		this.marketTradedVolumeDao = marketTradedVolumeDao;
	}
	
	/**Get market traded volume from Betfair betting exchange and store it in a database
	 * 
	 * @param marketId The market that the market traded volume is stored in a database.
	 */
	@Override
	public void execute(long marketId) {
		if(marketId>Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Market Id value is bigger than Integer.MAX: " + marketId);
		}
		BFMarketTradedVolume bfMarketTradedVolume = betfairService.getMarketTradedVolume((int)marketId);
		MarketTradedVolume marketTradedVolume = MarketTradedVolumeFactory.create(bfMarketTradedVolume, new Date(System.currentTimeMillis()));
		
		marketTradedVolumeDao.addMarketTradedVolume(marketTradedVolume);
	}

}
