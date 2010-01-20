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
	 * @param timestamp The time when the market traded volume was obtained from the betting exchange.
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
                                runnerTradedVolume,timestamp.getTime());
                return marketTradedVolume;
        }
}
