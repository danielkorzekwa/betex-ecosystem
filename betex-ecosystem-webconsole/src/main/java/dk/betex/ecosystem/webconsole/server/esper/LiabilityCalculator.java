package dk.betex.ecosystem.webconsole.server.esper;

import java.util.List;
import java.util.Map;

/**Calculates market liability based on bets and probabilities.
 * 
 * @author korzekwad
 *
 */
public interface LiabilityCalculator {

	/**Calculates expected liability based on bets and probabilities.
	 * 
	 * @param bets
	 * @param marketProbabilities key - marketId
	 * @return
	 */
	double calculateLiability(List<Bet> bets, Map<Long,MarketProb> marketProbabilities);
	
	public static class Bet {
		
		private final long marketId;
		private final long selectionId;
		private final double betSize;
		private final double betPrice;

		/**To create back bet use positive bet size, whereas to create lay bet use negative size.
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
		
	}
	
	public static class MarketProb {
		
		private final long marketId;
		private final Map<Long, Double> runnerProbs;

		/**
		 * 
		 * @param marketId
		 * @param runnerProbs key - selectionId, value - probability 
		 */
		public MarketProb(long marketId, Map<Long,Double> runnerProbs) {
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
}
