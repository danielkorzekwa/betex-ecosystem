package dk.betex.ecosystem.webconsole.server;

import dk.betex.ecosystem.webconsole.client.model.HeatMapModel;
import dk.bot.betfairservice.model.BFMarketTradedVolume;
import dk.bot.betfairservice.model.BFPriceTradedVolume;
import dk.bot.betfairservice.model.BFRunnerTradedVolume;

/**
 * Creates MarketTradedVolume object.
 * 
 * @author korzekwad
 * 
 */
public class MarketTradedVolumeFactory {

	/**
	 * Returns market traded volume for probabilities 0,1,2...100.
	 * 
	 * @param marketTradedVolume
	 *            
	 * @return
	 */
	public static HeatMapModel createHeatMap(BFMarketTradedVolume marketTradedVolume) {

		HeatMapModel heatMapModel = new HeatMapModel();

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
			yAxisLabels[i] = "" + i;
		}
		heatMapModel.setyAxisLabels(yAxisLabels);

		/** Set values */
		double[][] values = new double[xAxisSize][yAxisSize];
		for (int runnerIndex = 0; runnerIndex < marketTradedVolume.getRunnerTradedVolume().size(); runnerIndex++) {

			BFRunnerTradedVolume runnerTradedVolume = marketTradedVolume.getRunnerTradedVolume().get(runnerIndex);

			for (BFPriceTradedVolume priceTradedVolume : runnerTradedVolume.getPriceTradedVolume()) {
				int prob = (int) ((1 / priceTradedVolume.getPrice()) * 100);

				values[runnerIndex][prob] = priceTradedVolume.getTradedVolume();
			}
		}
		heatMapModel.setValues(values);
		
		return heatMapModel;
	}

}
