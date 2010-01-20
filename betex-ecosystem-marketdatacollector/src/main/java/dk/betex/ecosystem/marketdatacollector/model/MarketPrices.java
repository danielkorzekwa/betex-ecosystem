package dk.betex.ecosystem.marketdatacollector.model;

import java.io.Serializable;
import java.util.List;

import org.jcouchdb.document.BaseDocument;
import org.svenson.JSONTypeHint;

/**
 * Data model for volume of unmatched bets for all prices on all runners in a market at the given point of time.
 * 
 * @author korzekwad
 * 
 */
public class MarketPrices extends BaseDocument implements Serializable{

	private long marketId;
	private List<RunnerPrices> runnerPrices;
	private int inPlayDelay;
	
	/** The time at which the response was received from the betting exchange. */
	private long timestamp;

	public long getMarketId() {
		return marketId;
	}

	public void setMarketId(long marketId) {
		this.marketId = marketId;
	}

	public List<RunnerPrices> getRunnerPrices() {
		return runnerPrices;
	}

	@JSONTypeHint(RunnerPrices.class)
	public void setRunnerPrices(List<RunnerPrices> runnerPrices) {
		this.runnerPrices = runnerPrices;
	}

	public int getInPlayDelay() {
		return inPlayDelay;
	}

	public void setInPlayDelay(int inPlayDelay) {
		this.inPlayDelay = inPlayDelay;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * Data model for volume of unmatched bets for all prices on a particular runner in a market.
	 * 
	 * @author korzekwad
	 * 
	 */
	public static class RunnerPrices implements Serializable {

		public RunnerPrices() {	
		}

		private long selectionId;
		private double totalAmountMatched;
		private double lastPriceMatched;
		private double farSP;
		private double nearSP;
		private double actualSP;
		private List<PriceUnmatchedVolume> prices;

		public long getSelectionId() {
			return selectionId;
		}

		public void setSelectionId(long selectionId) {
			this.selectionId = selectionId;
		}

		public double getTotalAmountMatched() {
			return totalAmountMatched;
		}

		public void setTotalAmountMatched(double totalAmountMatched) {
			this.totalAmountMatched = totalAmountMatched;
		}

		public double getLastPriceMatched() {
			return lastPriceMatched;
		}

		public void setLastPriceMatched(double lastPriceMatched) {
			this.lastPriceMatched = lastPriceMatched;
		}

		public double getFarSP() {
			return farSP;
		}

		public void setFarSP(double farSP) {
			this.farSP = farSP;
		}

		public double getNearSP() {
			return nearSP;
		}

		public void setNearSP(double nearSP) {
			this.nearSP = nearSP;
		}

		public double getActualSP() {
			return actualSP;
		}

		public void setActualSP(double actualSP) {
			this.actualSP = actualSP;
		}

		public List<PriceUnmatchedVolume> getPrices() {
			return prices;
		}

		@JSONTypeHint(PriceUnmatchedVolume.class)
		public void setPrices(List<PriceUnmatchedVolume> prices) {
			this.prices = prices;
		}
		
		/**Data model for volume of unmatched bets for a particular price on a particular runner in a market.
		 * 
		 * @author korzekwad
		 *
		 */
		public static class PriceUnmatchedVolume implements Serializable{

			private double price;

			private double totalToBack;
			
			private double totalToLay;

			public PriceUnmatchedVolume() {
			}
			
			public double getPrice() {
				return price;
			}

			public void setPrice(double price) {
				this.price = price;
			}

			public double getTotalToBack() {
				return totalToBack;
			}

			public void setTotalToBack(double totalToBack) {
				this.totalToBack = totalToBack;
			}

			public double getTotalToLay() {
				return totalToLay;
			}

			public void setTotalToLay(double totalToLay) {
				this.totalToLay = totalToLay;
			}
		}
	}
}
