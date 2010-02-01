package dk.betex.ecosystem.marketdatacollector.factory;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
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

	private BFMarketTradedVolume bfMarketTradedVolume1;
	private BFMarketTradedVolume bfMarketTradedVolume2;
	
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

		bfMarketTradedVolume1 = new BFMarketTradedVolume(12, runnersTradedVolume);
		
		List<BFRunnerTradedVolume> runnersTradedVolume2 = new ArrayList<BFRunnerTradedVolume>();

		List<BFPriceTradedVolume> pricesTradedVolume2 = new ArrayList<BFPriceTradedVolume>();
		pricesTradedVolume2.add(new BFPriceTradedVolume(2.1, 37.32));
		pricesTradedVolume2.add(new BFPriceTradedVolume(2.2, 769.56));
		pricesTradedVolume2.add(new BFPriceTradedVolume(2.3, 3.2));
		runnersTradedVolume2.add(new BFRunnerTradedVolume(105, pricesTradedVolume2));

		pricesTradedVolume2 = new ArrayList<BFPriceTradedVolume>();
		pricesTradedVolume2.add(new BFPriceTradedVolume(3.4, 43.24));
		pricesTradedVolume2.add(new BFPriceTradedVolume(3.6, 69.12));
		runnersTradedVolume2.add(new BFRunnerTradedVolume(106, pricesTradedVolume2));

		bfMarketTradedVolume2 = new BFMarketTradedVolume(12, runnersTradedVolume2);
	}

	@Test
	public void testCreate() {
		MarketTradedVolume marketTradedVolume = MarketTradedVolumeFactory.create(bfMarketTradedVolume1, new Date(1234));

		assertEquals(bfMarketTradedVolume1.getMarketId(), marketTradedVolume.getMarketId());
		assertEquals(1234, marketTradedVolume.getTimestamp());

		assertEquals(bfMarketTradedVolume1.getRunnerTradedVolume().size(), marketTradedVolume.getRunnerTradedVolume()
				.size());

		for (int runnerIndex = 0; runnerIndex < bfMarketTradedVolume1.getRunnerTradedVolume().size(); runnerIndex++) {
			BFRunnerTradedVolume bfRunnerTradedVolume = bfMarketTradedVolume1.getRunnerTradedVolume().get(runnerIndex);
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
	public void testDelta() {
		long now = System.currentTimeMillis();
		
		MarketTradedVolume marketTradedVolume1 = MarketTradedVolumeFactory.create(bfMarketTradedVolume1,new Date(now));
		MarketTradedVolume marketTradedVolume2 = MarketTradedVolumeFactory.create(bfMarketTradedVolume2,new Date(now+1000));
		MarketTradedVolume delta = MarketTradedVolumeFactory.delta(marketTradedVolume1, marketTradedVolume2);
		
		assertEquals(marketTradedVolume2.getMarketId(), delta.getMarketId());
		assertEquals(marketTradedVolume2.getTimestamp(), delta.getTimestamp());

		assertEquals(marketTradedVolume2.getRunnerTradedVolume().size(), delta.getRunnerTradedVolume()
				.size());

		for (int runnerIndex = 0; runnerIndex < delta.getRunnerTradedVolume().size(); runnerIndex++) {
			RunnerTradedVolume deltaRunnerTradedVolume = delta.getRunnerTradedVolume().get(runnerIndex);
			RunnerTradedVolume runnerTradedVolume = marketTradedVolume2.getRunnerTradedVolume().get(runnerIndex);

			assertEquals(runnerTradedVolume.getSelectionId(), deltaRunnerTradedVolume.getSelectionId());
		}
		
		assertEquals(2.1,delta.getRunnerTradedVolume().get(0).getPriceTradedVolume().get(0).getPrice(),0);
		assertEquals(2,delta.getRunnerTradedVolume().get(0).getPriceTradedVolume().get(0).getTradedVolume(),0);
		assertEquals(2.2,delta.getRunnerTradedVolume().get(0).getPriceTradedVolume().get(1).getPrice(),0);
		assertEquals(4,delta.getRunnerTradedVolume().get(0).getPriceTradedVolume().get(1).getTradedVolume(),0);
		assertEquals(2.3,delta.getRunnerTradedVolume().get(0).getPriceTradedVolume().get(2).getPrice(),0);
		assertEquals(3.2,delta.getRunnerTradedVolume().get(0).getPriceTradedVolume().get(2).getTradedVolume(),0);
		
		assertEquals(3.6,delta.getRunnerTradedVolume().get(1).getPriceTradedVolume().get(0).getPrice(),0);
		assertEquals(4,delta.getRunnerTradedVolume().get(1).getPriceTradedVolume().get(0).getTradedVolume(),0);
	
		
	}
}
