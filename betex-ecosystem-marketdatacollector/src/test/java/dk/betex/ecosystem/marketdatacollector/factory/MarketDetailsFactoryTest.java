package dk.betex.ecosystem.marketdatacollector.factory;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import dk.betex.ecosystem.marketdatacollector.model.MarketDetails;
import dk.bot.betfairservice.model.BFMarketDetails;
import dk.bot.betfairservice.model.BFMarketDetailsRunner;

public class MarketDetailsFactoryTest {

	@Test
	public void testCreate() {
		BFMarketDetails bfMarketDetails = createMarketDetails(112, System.currentTimeMillis());
		MarketDetails marketDetails = MarketDetailsFactory.create(bfMarketDetails);

		assertMarketDetails(bfMarketDetails, marketDetails);
	}

	private void assertMarketDetails(BFMarketDetails expected, MarketDetails actual) {

		assertEquals("" + expected.getMarketId(), actual.getId());
		assertEquals(expected.getMarketId(), actual.getMarketId());
		assertEquals(expected.getMarketId(), actual.getMarketId());
		assertEquals(expected.getMenuPath(), actual.getMenuPath());
		assertEquals(expected.getMarketTime().getTime(), actual.getMarketTime());
		assertEquals(expected.getMarketSuspendTime().getTime(), actual.getSuspendTime());
		assertEquals(expected.getNumOfWinners(), actual.getNumOfWinners());
		
		assertEquals(expected.getRunners().get(0).getSelectionId(), actual.getRunners().get(0).getSelectionId());
		assertEquals(expected.getRunners().get(0).getSelectionName(), actual.getRunners().get(0).getSelectionName());

		assertEquals(expected.getRunners().get(1).getSelectionId(), actual.getRunners().get(1).getSelectionId());
		assertEquals(expected.getRunners().get(1).getSelectionName(), actual.getRunners().get(1).getSelectionName());

	}

	private BFMarketDetails createMarketDetails(int marketId, long marketTime) {
		List<BFMarketDetailsRunner> runners = new ArrayList<BFMarketDetailsRunner>();

		runners.add(new BFMarketDetailsRunner(234, "Man Utd"));
		runners.add(new BFMarketDetailsRunner(456, "Arsenal"));

		BFMarketDetails marketDetails = new BFMarketDetails(marketId, "Match Odds","uk/soccer/MatUtd vs Arsenal", new Date(
				marketTime), new Date(marketTime - 1000), 3,runners);

		return marketDetails;
	}

}
