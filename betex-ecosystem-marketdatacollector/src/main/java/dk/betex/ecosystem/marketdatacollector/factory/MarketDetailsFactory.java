package dk.betex.ecosystem.marketdatacollector.factory;

import java.util.ArrayList;
import java.util.List;

import dk.betex.ecosystem.marketdatacollector.model.MarketDetails;
import dk.betex.ecosystem.marketdatacollector.model.MarketDetailsRunner;
import dk.bot.betfairservice.model.BFMarketDetails;
import dk.bot.betfairservice.model.BFMarketDetailsRunner;

/**
 * Creates MarketDetails object from {@link BFMarketDetails}
 * 
 * @author korzekwad
 * 
 */
public class MarketDetailsFactory {

	public static MarketDetails create(BFMarketDetails bfMarketDetails) {
		MarketDetails marketDetails = new MarketDetails();
		
		marketDetails.setId("" + bfMarketDetails.getMarketId());
		marketDetails.setMarketName(bfMarketDetails.getMarketName());
		marketDetails.setMarketId(bfMarketDetails.getMarketId());
		marketDetails.setMenuPath(bfMarketDetails.getMenuPath());
		marketDetails.setMarketTime(bfMarketDetails.getMarketTime().getTime());
		marketDetails.setSuspendTime(bfMarketDetails.getMarketSuspendTime().getTime());
		marketDetails.setNumOfWinners(bfMarketDetails.getNumOfWinners());
		
		List<MarketDetailsRunner> marketDetailsRunners = new ArrayList<MarketDetailsRunner>();
		for(BFMarketDetailsRunner bfRunner: bfMarketDetails.getRunners()) {
			MarketDetailsRunner runner = new MarketDetailsRunner();
			runner.setSelectionId(bfRunner.getSelectionId());
			runner.setSelectionName(bfRunner.getSelectionName());
			marketDetailsRunners.add(runner);
		}
		marketDetails.setRunners(marketDetailsRunners);

		return marketDetails;
	}
}
