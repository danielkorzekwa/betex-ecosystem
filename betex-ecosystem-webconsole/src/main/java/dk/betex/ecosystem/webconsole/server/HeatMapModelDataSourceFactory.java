package dk.betex.ecosystem.webconsole.server;

import java.util.ArrayList;
import java.util.List;

import dk.betex.ecosystem.marketdatacollector.model.MarketPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.PriceTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.RunnerTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices.PriceUnmatchedVolume;
import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapModel;
import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapModel.HeatMapColumn;
import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapModel.HeatMapValue;

/**
 * Creates {@link BioHeatMapModel} object from different types of data.
 * 
 * @author korzekwad
 * 
 */
public class HeatMapModelDataSourceFactory {

	public static BioHeatMapModel create(MarketTradedVolume marketTradedVolume) {
		List<HeatMapColumn> columns = new ArrayList<HeatMapColumn>();
		for (RunnerTradedVolume runnerTradedVolume : marketTradedVolume.getRunnerTradedVolume()) {
			List<HeatMapValue> values = new ArrayList<HeatMapValue>();
			for (PriceTradedVolume priceTradedVolume : runnerTradedVolume.getPriceTradedVolume()) {
				values.add(new HeatMapValue(priceTradedVolume.getPrice(), priceTradedVolume.getTradedVolume()));
			}
			columns.add(new HeatMapColumn("" + runnerTradedVolume.getSelectionId(), values));
		}
		BioHeatMapModel ds = new BioHeatMapModel(columns);
		return ds;
	}

	public static BioHeatMapModel create(MarketPrices marketPrices) {
		List<HeatMapColumn> columns = new ArrayList<HeatMapColumn>();
		for (RunnerPrices runnerPrices : marketPrices.getRunnerPrices()) {
			List<HeatMapValue> values = new ArrayList<HeatMapValue>();

			values.add(new HeatMapValue(runnerPrices.getLastPriceMatched(), 1d));
			columns.add(new HeatMapColumn("" + runnerPrices.getSelectionId(), values));
		}

		BioHeatMapModel ds = new BioHeatMapModel(columns);
		return ds;
	}

	public static BioHeatMapModel createMarketTradedVolume(MarketPrices marketPrices) {
		List<HeatMapColumn> columns = new ArrayList<HeatMapColumn>();
		for (RunnerPrices runnerPrices : marketPrices.getRunnerPrices()) {
			List<HeatMapValue> values = new ArrayList<HeatMapValue>();

			values.add(new HeatMapValue(avgPrice(runnerPrices.getPriceTradedVolume()), 1d));
			values.add(new HeatMapValue(runnerPrices.getLastPriceMatched(), -1d));

			values.add(new HeatMapValue(1, 1d));
			values.add(new HeatMapValue(1000, 1d));

			columns.add(new HeatMapColumn("" + runnerPrices.getSelectionId(), values));
		}

		BioHeatMapModel ds = new BioHeatMapModel(columns);
		return ds;
	}
	
	private static double avgPrice(List<dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices.PriceTradedVolume> priceTradedVolume) {
		double sumOfPayouts = 0;
		double sumOfStakes = 0;
		/** Calculate avgPrices based on traded volume */
		for (dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices.PriceTradedVolume volume : priceTradedVolume) {
			sumOfPayouts += volume.getPrice() * volume.getTradedVolume();
			sumOfStakes += volume.getTradedVolume();
		}
		double avgPrice = sumOfPayouts / sumOfStakes;
		return avgPrice;
	}
}
