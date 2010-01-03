package dk.betex.ecosystem.webconsole.server;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.PriceTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.RunnerTradedVolume;
import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapModel;
import dk.bot.betfairservice.model.BFRunnerTradedVolume;

public class HeatMapModelFactoryTest {

	private MarketTradedVolume marketTradedVolume;

	@Before
	public void setUp() {
		List<RunnerTradedVolume> runnersTradedVolume = new ArrayList<RunnerTradedVolume>();

		List<PriceTradedVolume> pricesTradedVolume = new ArrayList<PriceTradedVolume>();
		pricesTradedVolume.add(new PriceTradedVolume(2.1, 35.32));
		pricesTradedVolume.add(new PriceTradedVolume(2.2, 765.56));
		runnersTradedVolume.add(new RunnerTradedVolume(105, pricesTradedVolume));

		pricesTradedVolume = new ArrayList<PriceTradedVolume>();
		pricesTradedVolume.add(new PriceTradedVolume(3.4, 43.24));
		pricesTradedVolume.add(new PriceTradedVolume(3.6, 65.12));
		runnersTradedVolume.add(new RunnerTradedVolume(106, pricesTradedVolume));

		marketTradedVolume = new MarketTradedVolume(12, runnersTradedVolume,0);
	}

	@Test
	public void testCreateHeatMap() {

		BioHeatMapModel heapMap = HeatMapModelFactory
				.createHeatMap(marketTradedVolume);

		assertEquals(marketTradedVolume.getRunnerTradedVolume().size(), heapMap.getxAxisLabels().length);
		assertEquals(101, heapMap.getyAxisLabels().length);

		assertEquals(marketTradedVolume.getRunnerTradedVolume().size(), heapMap.getValues().length);	
		for (int runnerIndex = 0; runnerIndex < marketTradedVolume.getRunnerTradedVolume().size(); runnerIndex++) {
			RunnerTradedVolume runnerTradedVolume = marketTradedVolume.getRunnerTradedVolume().get(runnerIndex);
			
			assertEquals(101,heapMap.getValues()[runnerIndex].length);
			for (int priceIndex = 0; priceIndex < heapMap.getValues()[runnerIndex].length; priceIndex++) {
				double normalizedPriceTradedVolume = heapMap.getValues()[runnerIndex][priceIndex];
					
				if (runnerTradedVolume.getSelectionId() == 105 && priceIndex == 47) {
					assertEquals(35.32, normalizedPriceTradedVolume, 0);
				}
				else if (runnerTradedVolume.getSelectionId() == 105 && priceIndex == 45) {
					assertEquals(765.56, normalizedPriceTradedVolume, 0);
				}
				else if (runnerTradedVolume.getSelectionId() == 106 && priceIndex == 29) {
					assertEquals(43.24, normalizedPriceTradedVolume, 0);
				}
				else if (runnerTradedVolume.getSelectionId() == 106 && priceIndex == 27) {
					assertEquals(65.12, normalizedPriceTradedVolume, 0);
				}
				else {
					assertEquals("selectionId=" + runnerTradedVolume.getSelectionId() + ", priceIndex=" + priceIndex,0, normalizedPriceTradedVolume, 0);
				}
			}
		}
	}
}
