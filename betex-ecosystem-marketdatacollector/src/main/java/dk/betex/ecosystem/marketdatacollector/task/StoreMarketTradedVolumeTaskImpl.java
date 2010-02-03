package dk.betex.ecosystem.marketdatacollector.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.betex.ecosystem.marketdatacollector.dao.MarketDetailsDao;
import dk.betex.ecosystem.marketdatacollector.dao.MarketPricesDao;
import dk.betex.ecosystem.marketdatacollector.factory.MarketDetailsFactory;
import dk.betex.ecosystem.marketdatacollector.factory.MarketPricesFactory;
import dk.betex.ecosystem.marketdatacollector.factory.MarketTradedVolumeFactory;
import dk.betex.ecosystem.marketdatacollector.model.MarketDetails;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.RunnerTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices.PriceTradedVolume;
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
	
	private final MarketDetailsDao marketDetailsDao;
	private final MarketPricesDao marketPricesDao;
	
	/**key - marketId*/
	private Map<Long,MarketDetails> marketDetailsMap = new HashMap<Long, MarketDetails>();
	
	/**key - marketId*/
	private Map<Long,MarketTradedVolume> previousRecords = new HashMap<Long, MarketTradedVolume>();
	
	public StoreMarketTradedVolumeTaskImpl(BetFairService betfairService, MarketDetailsDao marketDetailsDao, MarketPricesDao marketPricesDao) {
		this.betfairService = betfairService;
		this.marketDetailsDao = marketDetailsDao;
		this.marketPricesDao = marketPricesDao;
	}
	
	/**Get market traded volume from Betfair betting exchange and store it in a database.
	 * Get market details and store it in a database.
	 * 
	 * @param marketId The market that the market traded volume is stored in a database.
	 */
	@Override
	public void execute(List<Long> marketIds) {
		for(long marketId : marketIds) {
			if(marketId>Integer.MAX_VALUE) {
				throw new IllegalArgumentException("Market Id value is bigger than Integer.MAX: " + marketId);
			}
			
			/**Get market traded volume and calculate delta between subsequent records.*/
			BFMarketTradedVolume bfMarketTradedVolume = betfairService.getMarketTradedVolume((int)marketId);
			MarketTradedVolume marketTradedVolume = MarketTradedVolumeFactory.create(bfMarketTradedVolume, new Date(System.currentTimeMillis()));
			
			MarketTradedVolume delta;
			MarketTradedVolume previousRecord = previousRecords.get(marketId);
			if(previousRecord!=null) {
				delta = MarketTradedVolumeFactory.delta(previousRecord, marketTradedVolume);	
			}
			else {
				delta = MarketTradedVolumeFactory.delta(marketTradedVolume, marketTradedVolume);
			}
			previousRecords.put(marketId,marketTradedVolume);
						
			/**Get market prices and add it to the database.*/
			BFMarketRunners bfMarketRunners = betfairService.getMarketRunners((int)marketId);
			MarketPrices marketPrices = MarketPricesFactory.create(bfMarketRunners,3);
			for(RunnerPrices runnerPrices: marketPrices.getRunnerPrices()) {
				RunnerTradedVolume runnerTradedVolume = delta.getRunnerTradedVolume(runnerPrices.getSelectionId());
				List<PriceTradedVolume> priceTradedVolume = new ArrayList<PriceTradedVolume>();
				if(runnerTradedVolume!=null) {
					for(dk.betex.ecosystem.marketdatacollector.model.PriceTradedVolume volume: runnerTradedVolume.getPriceTradedVolume()) {
						priceTradedVolume.add(new PriceTradedVolume(volume.getPrice(), volume.getTradedVolume()));
					}
				}
				runnerPrices.setPriceTradedVolume(priceTradedVolume);
			}
			marketPricesDao.add(marketPrices);
			
			/**Get market details and add to the database if not added yet.*/
			if(marketDetailsMap.get(marketId)==null) {
				MarketDetails marketDetails = marketDetailsDao.getMarketDetails(marketId);
				if(marketDetails==null) {
					BFMarketDetails bfMarketDetails = betfairService.getMarketDetails((int)marketId);
					marketDetails = MarketDetailsFactory.create(bfMarketDetails);
					marketDetailsDao.addMarketDetails(marketDetails);
					marketDetailsMap.put(marketId,marketDetails);
				}
			}
		}
		
		
	}

}
