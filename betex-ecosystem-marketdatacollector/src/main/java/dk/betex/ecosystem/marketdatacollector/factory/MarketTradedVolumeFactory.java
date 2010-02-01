package dk.betex.ecosystem.marketdatacollector.factory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.PriceTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.RunnerTradedVolume;
import dk.bot.betfairservice.model.BFMarketTradedVolume;
import dk.bot.betfairservice.model.BFPriceTradedVolume;
import dk.bot.betfairservice.model.BFRunnerTradedVolume;

/**
 * Creates MarketTradedVolume object from {@link BFMarketTradedVolume}.
 * 
 * @author korzekwad
 * 
 */
public class MarketTradedVolumeFactory {

	/**
	 * 
	 * @param bfMarketTradedVolume
	 * @param timestamp
	 *            The time when the market traded volume was obtained from the betting exchange.
	 * @return
	 */
	public static MarketTradedVolume create(BFMarketTradedVolume bfMarketTradedVolume, Date timestamp) {

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
				runnerTradedVolume, timestamp.getTime());
		return marketTradedVolume;
	}

	/**
	 * Calculates delta = marketTradedVolume2 - marketTradedVolume1
	 * 
	 * @param marketTradedVolume1
	 * @param marketTradedVolume2
	 * @return
	 */
	public static MarketTradedVolume delta(MarketTradedVolume marketTradedVolume1,
			MarketTradedVolume marketTradedVolume2) {

		List<RunnerTradedVolume> deltaRunnerTradedVolume = new ArrayList<RunnerTradedVolume>(marketTradedVolume2
				.getRunnerTradedVolume().size());

		for (RunnerTradedVolume runnerTradedVolume2 : marketTradedVolume2.getRunnerTradedVolume()) {
			RunnerTradedVolume runnerTradedVolume1 = marketTradedVolume1.getRunnerTradedVolume(runnerTradedVolume2
					.getSelectionId());

			List<PriceTradedVolume> deltaPriceTradedVolume = new ArrayList<PriceTradedVolume>();
			for (PriceTradedVolume priceTradedVolume2 : runnerTradedVolume2.getPriceTradedVolume()) {
				double previousVolume = 0;
				if (runnerTradedVolume1 != null) {
					PriceTradedVolume priceTradedVolume = runnerTradedVolume1.getPriceTradedVolume(priceTradedVolume2
							.getPrice());
					if (priceTradedVolume != null) {
						previousVolume = priceTradedVolume.getTradedVolume();
					}
				}
				double deltaPriceVolume = priceTradedVolume2.getTradedVolume()- previousVolume;
				if(deltaPriceVolume>0) {
				deltaPriceTradedVolume.add(new PriceTradedVolume(priceTradedVolume2.getPrice(), deltaPriceVolume));
				}

			}

			RunnerTradedVolume delta = new RunnerTradedVolume(runnerTradedVolume2.getSelectionId(),
					deltaPriceTradedVolume);
			deltaRunnerTradedVolume.add(delta);
		}

		MarketTradedVolume deltaMarketTradedVolume = new MarketTradedVolume(marketTradedVolume2.getMarketId(),
				deltaRunnerTradedVolume, marketTradedVolume2.getTimestamp());
		return deltaMarketTradedVolume;
	}
}
