package dk.betex.ecosystem.webconsole.server.esper;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import dk.betex.ecosystem.webconsole.server.esper.LiabilityCalculator.Bet;
import dk.betex.ecosystem.webconsole.server.esper.LiabilityCalculator.MarketProb;

public class LiabilityCalculatorImplTest {

	private List<Bet> bets = new ArrayList<Bet>();
	private Map<Long, MarketProb> marketProbs = new HashMap<Long, MarketProb>();

	private LiabilityCalculator calc = new LiabilityCalculatorImpl();

	@Test
	public void testCalculateLiability1() {
		bets.add(new Bet(1, 2, 2, 3));

		Map<Long, Double> runnerProbs = new HashMap<Long, Double>();
		runnerProbs.put(2l, 1 / 3d);
		marketProbs.put(1l, new MarketProb(1, runnerProbs));

		assertEquals(0, calc.calculateLiability(bets).get(0).getExpectedLiability().get(0).getRunnerLiability(1/3d), 0);
	}

	@Test
	public void testCalculateLiability2() {
		bets.add(new Bet(1, 2, 2, 3));

		Map<Long, Double> runnerProbs = new HashMap<Long, Double>();
		runnerProbs.put(2l, 1 / 2d);
		marketProbs.put(1l, new MarketProb(1, runnerProbs));

		assertEquals(1, calc.calculateLiability(bets).get(0).getExpectedLiability().get(0).getRunnerLiability(1/2d), 0);
	}

	@Test
	public void testCalculateLiability3() {
		bets.add(new Bet(1, 2, 2, 3));

		Map<Long, Double> runnerProbs = new HashMap<Long, Double>();
		runnerProbs.put(2l, 1 / 4d);
		marketProbs.put(1l, new MarketProb(1, runnerProbs));

		assertEquals(-0.5, calc.calculateLiability(bets).get(0).getExpectedLiability().get(0).getRunnerLiability(1/4d), 0);
	}

	@Test
	public void testCalculateLiability4() {
		bets.add(new Bet(1, 2, 2, 3));

		Map<Long, Double> runnerProbs = new HashMap<Long, Double>();
		runnerProbs.put(2l, 0d);
		marketProbs.put(1l, new MarketProb(1, runnerProbs));

		assertEquals(-2, calc.calculateLiability(bets).get(0).getExpectedLiability().get(0).getRunnerLiability(0d), 0);
	}

	@Test
	public void testCalculateLiability5() {
		bets.add(new Bet(1, 2, 2, 3));

		Map<Long, Double> runnerProbs = new HashMap<Long, Double>();
		runnerProbs.put(2l, 1d);
		marketProbs.put(1l, new MarketProb(1, runnerProbs));

		assertEquals(4, calc.calculateLiability(bets).get(0).getExpectedLiability().get(0).getRunnerLiability(1d), 0);
	}

	@Test
	public void testCalculateLiability6() {
		bets.add(new Bet(1, 2, 2, 3));
		bets.add(new Bet(1, 2, -2, 3)); // lay bet

		Map<Long, Double> runnerProbs = new HashMap<Long, Double>();
		runnerProbs.put(2l, 1 / 3d);
		marketProbs.put(1l, new MarketProb(1, runnerProbs));

		assertEquals(0, calc.calculateLiability(bets).get(0).getExpectedLiability().get(0).getRunnerLiability(1/3d), 0);
	}
	
	@Test
	public void testCalculateLiability7() {
		bets.add(new Bet(1, 2, 2, 5));
		bets.add(new Bet(1, 2, -2, 5)); // lay bet

		Map<Long, Double> runnerProbs = new HashMap<Long, Double>();
		runnerProbs.put(2l, 1 / 3d);
		marketProbs.put(1l, new MarketProb(1, runnerProbs));

		assertEquals(0, calc.calculateLiability(bets).get(0).getExpectedLiability().get(0).getRunnerLiability(1/3d), 0);
	}
	
	@Test
	public void testCalculateLiability8() {
		bets.add(new Bet(1, 2, 2, 5));
		bets.add(new Bet(1, 2, -2, 4)); // lay bet

		assertEquals(0.666, calc.calculateLiability(bets).get(0).getExpectedLiability().get(0).getRunnerLiability(1/3d), 0.001);
	}
	
	@Test
	public void testCalculateLiability9() {
		bets.add(new Bet(1, 2, 2, 4));
		bets.add(new Bet(1, 2, -2, 5)); // lay bet

		assertEquals(-0.666, calc.calculateLiability(bets).get(0).getExpectedLiability().get(0).getRunnerLiability(1/3d), 0.001);
	}
	
	@Test
	public void testCalculateLiability10() {
		bets.add(new Bet(1, 2, 2, 4));
		bets.add(new Bet(2, 3, -2, 5)); // lay bet

		assertEquals(0.666, calc.calculateLiability(bets).get(0).getExpectedLiability().get(0).getRunnerLiability(1/3d), 0.001);
		assertEquals(-1.333, calc.calculateLiability(bets).get(1).getExpectedLiability().get(0).getRunnerLiability(1/3d), 0.001);
		
	}

}
