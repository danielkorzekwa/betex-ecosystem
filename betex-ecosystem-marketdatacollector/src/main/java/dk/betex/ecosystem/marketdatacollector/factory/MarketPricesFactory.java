package dk.betex.ecosystem.marketdatacollector.factory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import dk.betex.ecosystem.marketdatacollector.model.MarketPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices.PriceUnmatchedVolume;
import dk.bot.betfairservice.model.BFMarketRunner;
import dk.bot.betfairservice.model.BFMarketRunners;
import dk.bot.betfairservice.model.BFRunnerPrice;

/**
 * Creates MarketPrices object from {@link BFMarketRunners}.
 * 
 * @author korzekwad
 * 
 */
public class MarketPricesFactory {
	/**
	 * 
	 * @param bfMarketRunners Max number of best back/lay prices to be returned.
	 * @return
	 */
	public static MarketPrices create(BFMarketRunners bfMarketRunners, int maxNumOfPrices) {
		MarketPrices marketPrices = new MarketPrices();
		marketPrices.setMarketId(bfMarketRunners.getMarketId());
		marketPrices.setInPlayDelay(bfMarketRunners.getInPlayDelay());
		marketPrices.setTimestamp(bfMarketRunners.getTimestamp().getTime());

		List<RunnerPrices> runnerPricesList = new ArrayList<RunnerPrices>();
		for (BFMarketRunner bfMarketRunner : bfMarketRunners.getMarketRunners()) {
			RunnerPrices runnerPrices = new RunnerPrices();
			runnerPrices.setSelectionId(bfMarketRunner.getSelectionId());
			runnerPrices.setActualSP(bfMarketRunner.getActualSP());
			runnerPrices.setFarSP(bfMarketRunner.getFarSP());
			runnerPrices.setNearSP(bfMarketRunner.getNearSP());
			runnerPrices.setLastPriceMatched(bfMarketRunner.getLastPriceMatched());
			runnerPrices.setTotalAmountMatched(bfMarketRunner.getTotalAmountMatched());

			runnerPrices.setPrices(getBestPrices(bfMarketRunner.getPrices(), maxNumOfPrices));

			runnerPricesList.add(runnerPrices);
		}
		marketPrices.setRunnerPrices(runnerPricesList);

		return marketPrices;
	}

	/**
	 * Max number of best back/lay prices to be returned
	 * 
	 * @param prices
	 *            Must be sorted from the lowest to the highest.
	 * @param numberOfPrices
	 * @return
	 */
	private static  List<PriceUnmatchedVolume> getBestPrices(List<BFRunnerPrice> prices, int numberOfPrices) {
	  List<PriceUnmatchedVolume> bestToLayPrices = new ArrayList<PriceUnmatchedVolume>();
	  List<PriceUnmatchedVolume> bestToBackPrices = new ArrayList<PriceUnmatchedVolume>();
	  
	  for(int i=prices.size()-1;i>=0;i--) {
		  BFRunnerPrice bfRunnerPrice = prices.get(i);
		  
		  if(bfRunnerPrice.getTotalToBack()>=2) {
		     bestToBackPrices.add(convert(bfRunnerPrice));
		     if(bestToBackPrices.size()==numberOfPrices) break;
		  }
	  }
	  
	  for(int i=0;i<prices.size();i++) {
		  BFRunnerPrice bfRunnerPrice = prices.get(i);
		  
		  if(bfRunnerPrice.getTotalToLay()>=2) {
		     bestToLayPrices.add(convert(bfRunnerPrice));
		     if(bestToLayPrices.size()==numberOfPrices) break;
		  }
	  }
	  
	  List<PriceUnmatchedVolume> allPrices = new ArrayList<PriceUnmatchedVolume>();
	  for(int i=bestToBackPrices.size()-1;i>=0;i--) {
		  allPrices.add(bestToBackPrices.get(i));
	  }
	  for(int i=0;i<bestToLayPrices.size();i++) {
		  allPrices.add(bestToLayPrices.get(i));
	  }
	  
	  return allPrices;  
  }
	
	private static PriceUnmatchedVolume convert(BFRunnerPrice bfRunnerPrice) {
		PriceUnmatchedVolume priceVolume = new PriceUnmatchedVolume();
		priceVolume.setPrice(bfRunnerPrice.getPrice());
		priceVolume.setTotalToBack(bfRunnerPrice.getTotalToBack());
		priceVolume.setTotalToLay(bfRunnerPrice.getTotalToLay());

		return priceVolume;
	}
}
