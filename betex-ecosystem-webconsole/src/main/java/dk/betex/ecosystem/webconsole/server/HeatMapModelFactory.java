package dk.betex.ecosystem.webconsole.server;

import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.PriceTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.RunnerTradedVolume;
import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapModel;

/**
 * Creates MarketTradedVolume object.
 * 
 * @author korzekwad
 * 
 */
public class HeatMapModelFactory {

	/**
	 * Returns market traded volume in a form of bio heat map. The range min/max allows to zoom in/out inside the
	 * market traded volume and to analyse given range of probabilities in more details.
	 * 
	 * @param marketTradedVolume
	 * @param min
	 *            Minimum probability of traded volume that the heat map model is created for. From 0 to 1
	 * @param max
	 *            Maximum probability of traded volume that the heat map model is created for. From 0 to 1
	 * 
	 * 
	 * @return
	 */
	public static BioHeatMapModel createHeatMap(MarketTradedVolume marketTradedVolume, double min, double max) {

		BioHeatMapModel heatMapModel = new BioHeatMapModel();

		/** Set x-axis labels */
		int xAxisSize = marketTradedVolume.getRunnerTradedVolume().size();
		String[] xAxisLabels = new String[xAxisSize];
		for (int i = 0; i < marketTradedVolume.getRunnerTradedVolume().size(); i++) {
			int selectionId = marketTradedVolume.getRunnerTradedVolume().get(i).getSelectionId();
			xAxisLabels[i] = "" + selectionId;
		}
		heatMapModel.setxAxisLabels(xAxisLabels);

		/** Set y-axis labels */
		int yAxisSize = 101;
		String[] yAxisLabels = new String[yAxisSize];
		for (int i = 0; i < yAxisSize; i++) {
			yAxisLabels[i] = "" + ((double)i/100  * (max - min) + min);
		}
		heatMapModel.setyAxisLabels(yAxisLabels);

		/** Set values */
		double[][] values = new double[xAxisSize][yAxisSize];
		for (int runnerIndex = 0; runnerIndex < marketTradedVolume.getRunnerTradedVolume().size(); runnerIndex++) {

			RunnerTradedVolume runnerTradedVolume = marketTradedVolume.getRunnerTradedVolume().get(runnerIndex);

			for (PriceTradedVolume priceTradedVolume : runnerTradedVolume.getPriceTradedVolume()) {
				double prob = 1 / priceTradedVolume.getPrice();
				if (prob >= min && prob <= max) {
					double scaledProb = (prob - min) / (max - min);
					values[runnerIndex][(int) (scaledProb * 100)] += priceTradedVolume.getTradedVolume();
				}
			}
		}
		heatMapModel.setValues(values);

		return heatMapModel;
	}

}
