package dk.betex.ecosystem.webconsole.server;

import dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices.PriceUnmatchedVolume;

/**
 * 
 * @author korzekwad
 * 
 */
public class MarketPricesCalculator {

	public static double getTotalToBack(RunnerPrices runnerPrices) {
		double total = 0;

		for (PriceUnmatchedVolume price : runnerPrices.getPrices()) {
			total = total + price.getTotalToBack();
		}
		return total;
	}

	public static double getTotalToLay(RunnerPrices runnerPrices) {
		double total = 0;

		for (PriceUnmatchedVolume price : runnerPrices.getPrices()) {
			total = total + price.getTotalToLay();
		}
		return total;
	}

	/** Returns amount of all offers to back and lay. */
	public static double getTotalToBet(RunnerPrices runnerPrices) {
		double total = 0;

		for (PriceUnmatchedVolume price : runnerPrices.getPrices()) {
			total = total + price.getTotalToLay() + price.getTotalToBack();
		}
		return total;
	}

	public static double getTotalOnPriceToBack(RunnerPrices runnerPrices) {
		double bestPrice = 1.01d;
		double total = 0;

		for (PriceUnmatchedVolume price : runnerPrices.getPrices()) {
			if (price.getTotalToBack() >= 2 && price.getPrice() > 0) {
				if (price.getPrice() >= bestPrice) {
					bestPrice = price.getPrice();
					total = price.getTotalToBack();
				}
			}
		}
		return total;
	}

	public static double getPriceToBack(RunnerPrices runnerPrices) {
		double bestPrice = 1.01d;

		if (runnerPrices != null) {
			for (PriceUnmatchedVolume price : runnerPrices.getPrices()) {
				if (price.getTotalToBack() >= 2 && price.getPrice() > 0) {
					if (price.getPrice() >= bestPrice) {
						bestPrice = price.getPrice();
					}
				}
			}
		}

		return bestPrice;
	}

	public static double getTotalOnPriceToLay(RunnerPrices runnerPrices) {
		double bestPrice = 1000;
		double total = 0;

		for (PriceUnmatchedVolume price : runnerPrices.getPrices()) {
			if (price.getTotalToLay() >= 2 && price.getPrice() > 0) {
				if (price.getPrice() <= bestPrice) {
					bestPrice = price.getPrice();
					total = price.getTotalToLay();
				}
			}
		}
		return total;
	}

	public static double getPriceToLay(RunnerPrices runnerPrices) {
		double bestPrice = 1000;

		if (runnerPrices.getPrices() != null) {
			for (PriceUnmatchedVolume price : runnerPrices.getPrices()) {
				if (price.getTotalToLay() >= 2 && price.getPrice() > 0) {
					if (price.getPrice() <= bestPrice) {
						bestPrice = price.getPrice();
					}
				}
			}
		}
		return bestPrice;
	}

}
