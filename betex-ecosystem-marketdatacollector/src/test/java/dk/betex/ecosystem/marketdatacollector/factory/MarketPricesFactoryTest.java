package dk.betex.ecosystem.marketdatacollector.factory;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import dk.betex.ecosystem.marketdatacollector.model.MarketPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices.PriceUnmatchedVolume;
import dk.bot.betfairservice.model.BFMarketRunner;
import dk.bot.betfairservice.model.BFMarketRunners;
import dk.bot.betfairservice.model.BFRunnerPrice;

public class MarketPricesFactoryTest {

	@Test
	public void testCreate() {
		BFMarketRunners bfMarketRunners = createMarketRunners();
		
		MarketPrices marketPrices = MarketPricesFactory.create(bfMarketRunners);
		
		assertEquals(bfMarketRunners.getMarketId(), marketPrices.getMarketId());
		assertEquals(bfMarketRunners.getTimestamp().getTime(), marketPrices.getTimestamp());
		assertEquals(bfMarketRunners.getInPlayDelay(), marketPrices.getInPlayDelay());

		assertEquals(bfMarketRunners.getMarketRunners().size(), marketPrices.getRunnerPrices().size());

		for (int runnerPriceIndex = 0; runnerPriceIndex < bfMarketRunners.getMarketRunners().size(); runnerPriceIndex++) {
			BFMarketRunner bfMarketRunner = bfMarketRunners.getMarketRunners().get(runnerPriceIndex);
			RunnerPrices runnerPrices = marketPrices.getRunnerPrices().get(runnerPriceIndex);
			
			assertEquals(bfMarketRunner.getSelectionId(), runnerPrices.getSelectionId());
			assertEquals(bfMarketRunner.getActualSP(), runnerPrices.getActualSP(), 0);
			assertEquals(bfMarketRunner.getFarSP(), runnerPrices.getFarSP(), 0);
			assertEquals(bfMarketRunner.getNearSP(), runnerPrices.getNearSP(), 0);
			assertEquals(bfMarketRunner.getLastPriceMatched(), runnerPrices.getLastPriceMatched(), 0);
			assertEquals(bfMarketRunner.getTotalAmountMatched(), runnerPrices.getTotalAmountMatched(), 0);

			for (int priceIndex = 0; priceIndex < bfMarketRunner.getPrices().size(); priceIndex++) {
				BFRunnerPrice bfRunnerPrice = bfMarketRunner.getPrices().get(priceIndex);
				PriceUnmatchedVolume priceVolume = runnerPrices.getPrices().get(priceIndex);
				
				assertEquals(bfRunnerPrice.getPrice(), priceVolume.getPrice(), 0);
				assertEquals(bfRunnerPrice.getTotalToBack(), priceVolume.getTotalToBack(), 0);
				assertEquals(bfRunnerPrice.getTotalToLay(), priceVolume.getTotalToLay(), 0);
			}
		}
		
	}
	
	private BFMarketRunners createMarketRunners() {

		List<BFRunnerPrice> priceVolumeList = new ArrayList<BFRunnerPrice>();
		for (int i = 0; i < 10; i++) {
			BFRunnerPrice priceVolume = new BFRunnerPrice(1d / ((double) i + 1),i*2,i*4);
			priceVolumeList.add(priceVolume);
		}
		
		List<BFMarketRunner> runnerPricesList = new ArrayList<BFMarketRunner>();
		for (int i = 0; i < 5; i++) {
			BFMarketRunner runnerPrices = new BFMarketRunner(123,2.3,5.56,3.23,2.35,432.45,priceVolumeList);
			runnerPricesList.add(runnerPrices);
		}
		BFMarketRunners marketPrices = new BFMarketRunners(12,runnerPricesList,3, new Date(1500));
		return marketPrices;
	}

}
