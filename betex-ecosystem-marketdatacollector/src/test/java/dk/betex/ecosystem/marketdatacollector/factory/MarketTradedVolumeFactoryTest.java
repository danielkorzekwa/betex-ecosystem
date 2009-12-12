package dk.betex.ecosystem.marketdatacollector.factory;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.PriceTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.RunnerTradedVolume;
import dk.bot.betfairservice.model.BFMarketTradedVolume;
import dk.bot.betfairservice.model.BFPriceTradedVolume;
import dk.bot.betfairservice.model.BFRunnerTradedVolume;

public class MarketTradedVolumeFactoryTest {

        private BFMarketTradedVolume bfMarketTradedVolume;

        @Before
        public void setUp() {
                List<BFRunnerTradedVolume> runnersTradedVolume = new ArrayList<BFRunnerTradedVolume>();

                List<BFPriceTradedVolume> pricesTradedVolume = new ArrayList<BFPriceTradedVolume>();
                pricesTradedVolume.add(new BFPriceTradedVolume(2.1, 35.32));
                pricesTradedVolume.add(new BFPriceTradedVolume(2.2, 765.56));
                runnersTradedVolume.add(new BFRunnerTradedVolume(105, pricesTradedVolume));

                pricesTradedVolume = new ArrayList<BFPriceTradedVolume>();
                pricesTradedVolume.add(new BFPriceTradedVolume(3.4, 43.24));
                pricesTradedVolume.add(new BFPriceTradedVolume(3.6, 65.12));
                runnersTradedVolume.add(new BFRunnerTradedVolume(106, pricesTradedVolume));

                bfMarketTradedVolume = new BFMarketTradedVolume(12, runnersTradedVolume);
        }

        @Test
        public void testCreate() {
                MarketTradedVolume marketTradedVolume = MarketTradedVolumeFactory.create(bfMarketTradedVolume);

                assertEquals(bfMarketTradedVolume.getMarketId(), marketTradedVolume.getMarketId());

                assertEquals(bfMarketTradedVolume.getRunnerTradedVolume().size(), marketTradedVolume.getRunnerTradedVolume()
                                .size());

                for (int runnerIndex = 0; runnerIndex < bfMarketTradedVolume.getRunnerTradedVolume().size(); runnerIndex++) {
                        BFRunnerTradedVolume bfRunnerTradedVolume = bfMarketTradedVolume.getRunnerTradedVolume().get(runnerIndex);
                        RunnerTradedVolume runnerTradedVolume = marketTradedVolume.getRunnerTradedVolume().get(runnerIndex);

                        assertEquals(bfRunnerTradedVolume.getSelectionId(), runnerTradedVolume.getSelectionId());
                        assertEquals(bfRunnerTradedVolume.getPriceTradedVolume().size(), runnerTradedVolume.getPriceTradedVolume()
                                        .size());

                        for (int priceIndex = 0; priceIndex < bfRunnerTradedVolume.getPriceTradedVolume().size(); priceIndex++) {
                                BFPriceTradedVolume bfPriceTradedVolume = bfRunnerTradedVolume.getPriceTradedVolume().get(priceIndex);
                                PriceTradedVolume priceTradedVolume = runnerTradedVolume.getPriceTradedVolume().get(priceIndex);

                                assertEquals(bfPriceTradedVolume.getPrice(), priceTradedVolume.getPrice(), 0);
                                assertEquals(bfPriceTradedVolume.getTradedVolume(), priceTradedVolume.getTradedVolume(), 0);
                        }

                }
        }

        @Test
        public void testCreateNormalized() {
                MarketTradedVolume marketTradedVolume = MarketTradedVolumeFactory.create(bfMarketTradedVolume);
                MarketTradedVolume normalizedMarketTradedVolume = MarketTradedVolumeFactory
                                .createNormalized(marketTradedVolume);

                assertEquals(marketTradedVolume.getMarketId(), normalizedMarketTradedVolume.getMarketId());

                assertEquals(marketTradedVolume.getRunnerTradedVolume().size(), normalizedMarketTradedVolume
                                .getRunnerTradedVolume().size());

                for (int runnerIndex = 0; runnerIndex < marketTradedVolume.getRunnerTradedVolume().size(); runnerIndex++) {
                        RunnerTradedVolume runnerTradedVolume = marketTradedVolume.getRunnerTradedVolume().get(runnerIndex);
                        RunnerTradedVolume normalizedRunnerTradedVolume = normalizedMarketTradedVolume.getRunnerTradedVolume().get(
                                        runnerIndex);

                        assertEquals(runnerTradedVolume.getSelectionId(), normalizedRunnerTradedVolume.getSelectionId());
                        assertEquals(347, normalizedRunnerTradedVolume.getPriceTradedVolume().size());

                        for (int priceIndex = 0; priceIndex < normalizedRunnerTradedVolume.getPriceTradedVolume().size(); priceIndex++) {
                                PriceTradedVolume normalizedPriceTradedVolume = normalizedRunnerTradedVolume.getPriceTradedVolume()
                                                .get(priceIndex);

                                if (runnerTradedVolume.getSelectionId() == 105 && normalizedPriceTradedVolume.getPrice() == 2.1) {
                                        assertEquals(35.32, normalizedPriceTradedVolume.getTradedVolume(), 0);
                                }
                                else if (runnerTradedVolume.getSelectionId() == 105 && normalizedPriceTradedVolume.getPrice() == 2.2) {
                                        assertEquals(765.56, normalizedPriceTradedVolume.getTradedVolume(), 0);
                                }
                                else if (runnerTradedVolume.getSelectionId() == 106 && normalizedPriceTradedVolume.getPrice() == 3.4) {
                                        assertEquals(43.24, normalizedPriceTradedVolume.getTradedVolume(), 0);
                                }
                                else if (runnerTradedVolume.getSelectionId() == 106 && normalizedPriceTradedVolume.getPrice() == 3.6) {
                                        assertEquals(65.12, normalizedPriceTradedVolume.getTradedVolume(), 0);
                                }
                                else {
                                        assertEquals(0, normalizedPriceTradedVolume.getTradedVolume(), 0);
                                }
                        }
                }
        }
        
        @Test
        public void testCreateNormalizedProbs() {
                MarketTradedVolume marketTradedVolume = MarketTradedVolumeFactory.create(bfMarketTradedVolume);
                MarketTradedVolume normalizedMarketTradedVolume = MarketTradedVolumeFactory
                                .createNormalizedAsProbs(marketTradedVolume);

                assertEquals(marketTradedVolume.getMarketId(), normalizedMarketTradedVolume.getMarketId());

                assertEquals(marketTradedVolume.getRunnerTradedVolume().size(), normalizedMarketTradedVolume
                                .getRunnerTradedVolume().size());

                for (int runnerIndex = 0; runnerIndex < marketTradedVolume.getRunnerTradedVolume().size(); runnerIndex++) {
                        RunnerTradedVolume runnerTradedVolume = marketTradedVolume.getRunnerTradedVolume().get(runnerIndex);
                        RunnerTradedVolume normalizedRunnerTradedVolume = normalizedMarketTradedVolume.getRunnerTradedVolume().get(
                                        runnerIndex);

                        assertEquals(runnerTradedVolume.getSelectionId(), normalizedRunnerTradedVolume.getSelectionId());
                        assertEquals(101, normalizedRunnerTradedVolume.getPriceTradedVolume().size());

                        for (int priceIndex = 0; priceIndex < normalizedRunnerTradedVolume.getPriceTradedVolume().size(); priceIndex++) {
                                PriceTradedVolume normalizedPriceTradedVolume = normalizedRunnerTradedVolume.getPriceTradedVolume()
                                                .get(priceIndex);

                                if (runnerTradedVolume.getSelectionId() == 105 && normalizedPriceTradedVolume.getPrice() == 47) {
                                        assertEquals(35.32, normalizedPriceTradedVolume.getTradedVolume(), 0);
                                }
                                else if (runnerTradedVolume.getSelectionId() == 105 && normalizedPriceTradedVolume.getPrice() == 45) {
                                        assertEquals(765.56, normalizedPriceTradedVolume.getTradedVolume(), 0);
                                }
                                else if (runnerTradedVolume.getSelectionId() == 106 && normalizedPriceTradedVolume.getPrice() == 29) {
                                        assertEquals(43.24, normalizedPriceTradedVolume.getTradedVolume(), 0);
                                }
                                else if (runnerTradedVolume.getSelectionId() == 106 && normalizedPriceTradedVolume.getPrice() == 27) {
                                        assertEquals(65.12, normalizedPriceTradedVolume.getTradedVolume(), 0);
                                }
                                else {
                                        assertEquals(0, normalizedPriceTradedVolume.getTradedVolume(), 0);
                                }
                        }
                }
        }
}
