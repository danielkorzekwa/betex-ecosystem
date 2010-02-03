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
public class MarketPrices extends BaseDocument implements Serializable {

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

	public RunnerPrices getRunnerPrices(long selectionId) {
		for (RunnerPrices prices : runnerPrices) {
			if (prices.getSelectionId() == selectionId)
				return prices;
		}
		return null;
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

	/** Returns total traded volume on market. */
	public double getTotalTradedVolume() {
		double totalTradedVolume = 0;

		for (RunnerPrices runnerPrices : getRunnerPrices()) {
			totalTradedVolume += runnerPrices.getTotalAmountMatched();
		}

		return totalTradedVolume;
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
		private List<PriceTradedVolume> priceTradedVolume;

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

		public List<PriceTradedVolume> getPriceTradedVolume() {
			return priceTradedVolume;
		}

		@JSONTypeHint(PriceTradedVolume.class)
		public void setPriceTradedVolume(List<PriceTradedVolume> priceTradedVolume) {
			this.priceTradedVolume = priceTradedVolume;
		}

		/** Returns delta of total traded volume on runner. */
		public double totalTradedVolume() {
			double totalTradedVolume = 0;

			for (PriceTradedVolume tradedVolume : this.priceTradedVolume) {
				totalTradedVolume += tradedVolume.getTradedVolume();
			}

			return totalTradedVolume;
		}

		/**
		 * Data model for volume of unmatched bets for a particular price on a particular runner in a market.
		 * 
		 * @author korzekwad
		 * 
		 */
		public static class PriceUnmatchedVolume implements Serializable {

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

		/**
		 * Represents traded volume for the given price on the given runner in a particular market.
		 * 
		 * @author korzekwad
		 * 
		 */
		public static class PriceTradedVolume implements Serializable {

			private double price;
			private double tradedVolume;

			public PriceTradedVolume() {
			}

			/**
			 * 
			 * @param price
			 * @param tradedVolume
			 *            The total amount matched for the given price
			 */
			public PriceTradedVolume(double price, double tradedVolume) {
				this.price = price;
				this.tradedVolume = tradedVolume;
			}

			public double getPrice() {
				return price;
			}

			public double getTradedVolume() {
				return tradedVolume;
			}

			public void setPrice(double price) {
				this.price = price;
			}

			public void setTradedVolume(double tradedVolume) {
				this.tradedVolume = tradedVolume;
			}

			@Override
			public String toString() {
				return "PriceTradedVolume [price=" + price + ", tradedVolume=" + tradedVolume + "]";
			}
		}
	}
}
