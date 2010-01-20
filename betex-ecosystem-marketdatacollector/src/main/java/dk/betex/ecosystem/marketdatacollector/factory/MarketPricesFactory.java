package dk.betex.ecosystem.marketdatacollector.factory;

import java.util.ArrayList;
import java.util.List;

import dk.betex.ecosystem.marketdatacollector.model.MarketPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices.PriceUnmatchedVolume;
import dk.bot.betfairservice.model.BFMarketRunner;
import dk.bot.betfairservice.model.BFMarketRunners;
import dk.bot.betfairservice.model.BFRunnerPrice;

/**Creates MarketPrices object from {@link BFMarketRunners}.
 * 
 * @author korzekwad
 *
 */
public class MarketPricesFactory {
  public static MarketPrices create(BFMarketRunners bfMarketRunners) {
	 MarketPrices marketPrices = new MarketPrices();
	 marketPrices.setMarketId(bfMarketRunners.getMarketId());
	 marketPrices.setInPlayDelay(bfMarketRunners.getInPlayDelay());
	 marketPrices.setTimestamp(bfMarketRunners.getTimestamp().getTime());
	 
	 List<RunnerPrices> runnerPricesList = new ArrayList<RunnerPrices>();
	 for(BFMarketRunner bfMarketRunner: bfMarketRunners.getMarketRunners()) {
		 RunnerPrices runnerPrices = new RunnerPrices();
		 runnerPrices.setSelectionId(bfMarketRunner.getSelectionId());
		 runnerPrices.setActualSP(bfMarketRunner.getActualSP());
		 runnerPrices.setFarSP(bfMarketRunner.getFarSP());
		 runnerPrices.setNearSP(bfMarketRunner.getNearSP());
		 runnerPrices.setLastPriceMatched(bfMarketRunner.getLastPriceMatched());
		 runnerPrices.setTotalAmountMatched(bfMarketRunner.getTotalAmountMatched());
		 
		 List<PriceUnmatchedVolume> priceVolumeList = new ArrayList<PriceUnmatchedVolume>();
		for(BFRunnerPrice bfRunnerPrice: bfMarketRunner.getPrices()) {
			PriceUnmatchedVolume priceVolume = new PriceUnmatchedVolume();
			priceVolume.setPrice(bfRunnerPrice.getPrice());
			priceVolume.setTotalToBack(bfRunnerPrice.getTotalToBack());
			priceVolume.setTotalToLay(bfRunnerPrice.getTotalToLay());
			
			priceVolumeList.add(priceVolume);
		}
		runnerPrices.setPrices(priceVolumeList);
		
		runnerPricesList.add(runnerPrices);
	 }
	 marketPrices.setRunnerPrices(runnerPricesList);
	 
	 return marketPrices;
  }
}
