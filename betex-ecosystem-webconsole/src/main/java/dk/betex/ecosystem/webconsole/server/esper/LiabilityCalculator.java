package dk.betex.ecosystem.webconsole.server.esper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calculates market liability based on bets and probabilities.
 * 
 * @author korzekwad
 * 
 */
public interface LiabilityCalculator {

	/**
	 * Calculates expected liability based on bets and probabilities.
	 * 
	 * @param bets
	 * @return expected liability for all markets that the bets are on.
	 */
	public List<MarketLiability> calculateLiability(List<Bet> bets);

	public static class Bet {

		private final long marketId;
		private final long selectionId;
		private final double betSize;
		private final double betPrice;

		/**
		 * To create back bet use positive bet size, whereas to create lay bet use negative size.
		 * 
		 * @param marketId
		 * @param selectionId
		 * @param betSize
		 * @param betPrice
		 */
		public Bet(long marketId, long selectionId, double betSize, double betPrice) {
			this.marketId = marketId;
			this.selectionId = selectionId;
			this.betSize = betSize;
			this.betPrice = betPrice;
		}

		public long getMarketId() {
			return marketId;
		}

		public long getSelectionId() {
			return selectionId;
		}

		public double getBetSize() {
			return betSize;
		}

		public double getBetPrice() {
			return betPrice;
		}

		@Override
		public String toString() {
			return "Bet [marketId=" + marketId + ", selectionId=" + selectionId + ", betSize=" + betSize
					+ ", betPrice=" + betPrice + "]";
		}

	}

	public static class MarketProb {

		private final long marketId;
		private final Map<Long, Double> runnerProbs;

		/**
		 * 
		 * @param marketId
		 * @param runnerProbs
		 *            key - selectionId, value - probability
		 */
		public MarketProb(long marketId, Map<Long, Double> runnerProbs) {
			this.marketId = marketId;
			this.runnerProbs = runnerProbs;
		}

		public long getMarketId() {
			return marketId;
		}

		public Map<Long, Double> getRunnerProbs() {
			return runnerProbs;
		}
	}

	public static class MarketLiability {

		private final long marketId;
		private Map<Long, RunnerLiability> runnerLiabilities = new HashMap<Long, RunnerLiability>();

		public MarketLiability(long marketId) {
			this.marketId = marketId;
		}

		public List<RunnerLiability> getExpectedLiability() {
			return new ArrayList<RunnerLiability>(runnerLiabilities.values());
		}
		
		public RunnerLiability getExpectedLiability(long selectionId) {
			return runnerLiabilities.get(selectionId);
		}

		public void addBetPayout(long selectionId, double size, double price) {
			RunnerLiability runnerLiability = runnerLiabilities.get(selectionId);
			if(runnerLiability==null) {
				runnerLiability = new RunnerLiability(selectionId);
				runnerLiabilities.put(selectionId, runnerLiability);
			}
			runnerLiability.addBet(size,price);
		}

		public long getMarketId() {
			return marketId;
		}
	}

	public class RunnerLiability {

		private final long selectionId;
		private double runnerPayout=0;
		private double runnerStake=0;
		private int numberOfBets=0;

		public RunnerLiability(long selectionId) {
			this.selectionId = selectionId;
		}

		public void addBet(double size, double price) {
			runnerPayout +=size*price;
			runnerStake += size;
			numberOfBets++;
		}

		public long getSelectionId() {
			return selectionId;
		}

		public double getRunnerLiability(double runnerProbability) {
			return runnerPayout*runnerProbability-runnerStake;
		}

		public int getNumberOfBets() {
			return numberOfBets;
		}		
	}

}
