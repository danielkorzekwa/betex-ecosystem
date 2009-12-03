package dk.betex.ecosystem.webconsole.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dk.betex.ecosystem.webconsole.client.model.MarketTradedVolume;
import dk.betex.ecosystem.webconsole.client.model.PriceTradedVolume;
import dk.betex.ecosystem.webconsole.client.model.RunnerTradedVolume;
import dk.bot.betfairservice.BetFairUtil;
import dk.bot.betfairservice.model.BFMarketTradedVolume;
import dk.bot.betfairservice.model.BFPriceTradedVolume;
import dk.bot.betfairservice.model.BFRunnerTradedVolume;

/**
 * Creates MarketTradedVolume object.
 * 
 * @author korzekwad
 * 
 */
public class MarketTradedVolumeFactory {

	public static MarketTradedVolume create(BFMarketTradedVolume bfMarketTradedVolume) {

		List<RunnerTradedVolume> runnerTradedVolume = new ArrayList<RunnerTradedVolume>();
		for (BFRunnerTradedVolume bfRunnerTradedVolume : bfMarketTradedVolume.getRunnerTradedVolume()) {

			List<PriceTradedVolume> priceTradedVolume = new ArrayList<PriceTradedVolume>();
			for (BFPriceTradedVolume bfPRiceTradedVolume : bfRunnerTradedVolume.getPriceTradedVolume()) {
				priceTradedVolume.add(new PriceTradedVolume(bfPRiceTradedVolume.getPrice(), bfPRiceTradedVolume
						.getTradedVolume()));
			}

			runnerTradedVolume.add(new RunnerTradedVolume(bfRunnerTradedVolume.getSelectionId(), priceTradedVolume));
		}

		MarketTradedVolume marketTradedVolume = new MarketTradedVolume(bfMarketTradedVolume.getMarketId(),
				runnerTradedVolume);
		return marketTradedVolume;
	}

	/**
	 * Returns market traded volume for all valid betfair prices.
	 * 
	 * @param marketTradedVolume
	 *            contains only runner prices with traded volume bigger than 0.
	 * @return
	 */
	public static MarketTradedVolume createNormalized(MarketTradedVolume marketTradedVolume) {
		List<Double> allPrices = BetFairUtil.getAllPricesForPriceRanges(BetFairUtil.getPriceRanges());

		List<RunnerTradedVolume> normalizedRunnerTradedVolume = new ArrayList<RunnerTradedVolume>();
		for (RunnerTradedVolume runnerTradedVolume : marketTradedVolume.getRunnerTradedVolume()) {

			/** key - price, value - traded volume */
			Map<Double, Double> pricesTradedVolumeMap = new HashMap<Double, Double>();
			for (PriceTradedVolume priceTradedVolume : runnerTradedVolume.getPriceTradedVolume()) {
				pricesTradedVolumeMap.put(priceTradedVolume.getPrice(), priceTradedVolume.getTradedVolume());
			}

			List<PriceTradedVolume> normalizedPriceTradedVolume = new ArrayList<PriceTradedVolume>();
			for (Double price : allPrices) {
				Double tradedVolume = pricesTradedVolumeMap.get(price);
				if (tradedVolume == null) {
					tradedVolume = 0d;
				}
				normalizedPriceTradedVolume.add(new PriceTradedVolume(price, tradedVolume));
			}

			normalizedRunnerTradedVolume.add(new RunnerTradedVolume(runnerTradedVolume.getSelectionId(),
					normalizedPriceTradedVolume));
		}

		MarketTradedVolume normalizedMarketTradedVolume = new MarketTradedVolume(marketTradedVolume.getMarketId(),
				normalizedRunnerTradedVolume);
		return normalizedMarketTradedVolume;
	}

	/**
	 * Returns market traded volume for probabilities 0,1,2...100.
	 * 
	 * @param marketTradedVolume
	 *            contains only runner prices with traded volume bigger than 0.
	 * @return
	 */
	public static MarketTradedVolume createNormalizedAsProbs(MarketTradedVolume marketTradedVolume) {

		List<Integer> allProbabilities = new ArrayList<Integer>();
		for (int i = 0; i <= 100; i++) {
			allProbabilities.add(i);
		}

		List<RunnerTradedVolume> normalizedRunnerTradedVolume = new ArrayList<RunnerTradedVolume>();
		for (RunnerTradedVolume runnerTradedVolume : marketTradedVolume.getRunnerTradedVolume()) {

			/** key - probability from 0..100, value - traded volume */
			Map<Integer, Double> pricesTradedVolumeMap = new HashMap<Integer, Double>();
			for (PriceTradedVolume priceTradedVolume : runnerTradedVolume.getPriceTradedVolume()) {
				int prob = (int)((1/priceTradedVolume.getPrice())*100);
				pricesTradedVolumeMap.put(prob, priceTradedVolume.getTradedVolume());
			}

			List<PriceTradedVolume> normalizedPriceTradedVolume = new ArrayList<PriceTradedVolume>();
			for (int prob : allProbabilities) {
				Double tradedVolume = pricesTradedVolumeMap.get(prob);
				if (tradedVolume == null) {
					tradedVolume = 0d;
				}
				
				normalizedPriceTradedVolume.add(new PriceTradedVolume(prob, tradedVolume));
			}

			normalizedRunnerTradedVolume.add(new RunnerTradedVolume(runnerTradedVolume.getSelectionId(),
					normalizedPriceTradedVolume));
		}

		MarketTradedVolume normalizedMarketTradedVolume = new MarketTradedVolume(marketTradedVolume.getMarketId(),
				normalizedRunnerTradedVolume);
		return normalizedMarketTradedVolume;
	}

}
