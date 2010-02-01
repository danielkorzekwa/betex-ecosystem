package dk.betex.ecosystem.marketdatacollector.task;

import java.util.Date;

import dk.betex.ecosystem.marketdatacollector.dao.MarketDetailsDao;
import dk.betex.ecosystem.marketdatacollector.dao.MarketPricesDao;
import dk.betex.ecosystem.marketdatacollector.dao.MarketTradedVolumeDao;
import dk.betex.ecosystem.marketdatacollector.factory.MarketDetailsFactory;
import dk.betex.ecosystem.marketdatacollector.factory.MarketPricesFactory;
import dk.betex.ecosystem.marketdatacollector.factory.MarketTradedVolumeFactory;
import dk.betex.ecosystem.marketdatacollector.model.MarketDetails;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;
import dk.bot.betfairservice.BetFairService;
import dk.bot.betfairservice.model.BFMarketDetails;
import dk.bot.betfairservice.model.BFMarketRunners;
import dk.bot.betfairservice.model.BFMarketTradedVolume;

/** Get market traded volume from Betfair betting exchange and store it in a database.
 *  Get market details and store it in a database.
 * 
 * @author korzekwad
 *
 */
public class StoreMarketTradedVolumeTaskImpl implements StoreMarketTradedVolumeTask{

	private final BetFairService betfairService;
	private final MarketTradedVolumeDao marketTradedVolumeDao;
	private final MarketDetailsDao marketDetailsDao;
	private final MarketPricesDao marketPricesDao;
	
	private MarketDetails marketDetails;
	
	private MarketTradedVolume previousRecord;
	
	public StoreMarketTradedVolumeTaskImpl(BetFairService betfairService, MarketTradedVolumeDao marketTradedVolumeDao, MarketDetailsDao marketDetailsDao, MarketPricesDao marketPricesDao) {
		this.betfairService = betfairService;
		this.marketTradedVolumeDao = marketTradedVolumeDao;
		this.marketDetailsDao = marketDetailsDao;
		this.marketPricesDao = marketPricesDao;
	}
	
	/**Get market traded volume from Betfair betting exchange and store it in a database.
	 * Get market details and store it in a database.
	 * 
	 * @param marketId The market that the market traded volume is stored in a database.
	 */
	@Override
	public void execute(long marketId) {
		if(marketId>Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Market Id value is bigger than Integer.MAX: " + marketId);
		}
		
		/**Get market traded volume and add it to the database.*/
		BFMarketTradedVolume bfMarketTradedVolume = betfairService.getMarketTradedVolume((int)marketId);
		MarketTradedVolume marketTradedVolume = MarketTradedVolumeFactory.create(bfMarketTradedVolume, new Date(System.currentTimeMillis()));
		
		MarketTradedVolume delta;
		if(previousRecord!=null) {
			delta = MarketTradedVolumeFactory.delta(previousRecord, marketTradedVolume);	
		}
		else {
			delta = MarketTradedVolumeFactory.delta(marketTradedVolume, marketTradedVolume);
		}
		previousRecord=marketTradedVolume;
		marketTradedVolumeDao.addMarketTradedVolume(delta);
		
		/**Get market prices and add it to the database.*/
		BFMarketRunners bfMarketRunners = betfairService.getMarketRunners((int)marketId);
		MarketPrices marketPrices = MarketPricesFactory.create(bfMarketRunners,3);
		marketPricesDao.add(marketPrices);
		
		/**Get market details and add to the database if not added yet.*/
		if(marketDetails==null) {
			marketDetails = marketDetailsDao.getMarketDetails(marketId);
			if(marketDetails==null) {
				BFMarketDetails bfMarketDetails = betfairService.getMarketDetails((int)marketId);
				MarketDetails marketDetails = MarketDetailsFactory.create(bfMarketDetails);
				marketDetailsDao.addMarketDetails(marketDetails);
			}
		}
	}

}
