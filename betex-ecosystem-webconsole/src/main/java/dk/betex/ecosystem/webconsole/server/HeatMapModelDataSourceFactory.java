package dk.betex.ecosystem.webconsole.server;

import java.util.ArrayList;
import java.util.List;

import dk.betex.ecosystem.marketdatacollector.model.MarketPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.PriceTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.RunnerTradedVolume;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices;
import dk.betex.ecosystem.marketdatacollector.model.MarketPrices.RunnerPrices.PriceUnmatchedVolume;
import dk.betex.ecosystem.webconsole.client.service.HeatMapModelDataSource;
import dk.betex.ecosystem.webconsole.client.service.HeatMapModelDataSource.HeatMapColumn;
import dk.betex.ecosystem.webconsole.client.service.HeatMapModelDataSource.HeatMapValue;

/**
 * Creates {@link HeatMapModelDataSource} object from different types of data.
 * 
 * @author korzekwad
 * 
 */
public class HeatMapModelDataSourceFactory {

	public static HeatMapModelDataSource create(MarketTradedVolume marketTradedVolume) {
		List<HeatMapColumn> columns = new ArrayList<HeatMapColumn>();
		for (RunnerTradedVolume runnerTradedVolume : marketTradedVolume.getRunnerTradedVolume()) {
			List<HeatMapValue> values = new ArrayList<HeatMapValue>();
			for (PriceTradedVolume priceTradedVolume : runnerTradedVolume.getPriceTradedVolume()) {
				values.add(new HeatMapValue(priceTradedVolume.getPrice(), priceTradedVolume.getTradedVolume()));
			}
			columns.add(new HeatMapColumn("" + runnerTradedVolume.getSelectionId(), values));
		}
		HeatMapModelDataSource ds = new HeatMapModelDataSource(columns);
		return ds;
	}

	public static HeatMapModelDataSource create(MarketPrices marketPrices) {
		List<HeatMapColumn> columns = new ArrayList<HeatMapColumn>();
		for (RunnerPrices runnerPrices : marketPrices.getRunnerPrices()) {
			List<HeatMapValue> values = new ArrayList<HeatMapValue>();
			for (PriceUnmatchedVolume priceVolume : runnerPrices.getPrices()) {
				if (priceVolume.getTotalToBack() > 0 && priceVolume.getTotalToLay() == 0) {
					values.add(new HeatMapValue(priceVolume.getPrice(), priceVolume.getTotalToBack()));
				} else if (priceVolume.getTotalToBack() == 0 && priceVolume.getTotalToLay() > 0) {
					values.add(new HeatMapValue(priceVolume.getPrice(), -priceVolume.getTotalToLay()));
				} else if (priceVolume.getTotalToBack() == 0 && priceVolume.getTotalToLay() == 0) {
					/** Do nothing */
				} else {
					throw new IllegalArgumentException(
							"Both toBack and toLay unmatched bets available at the same price");
				}
			}
			columns.add(new HeatMapColumn("" + runnerPrices.getSelectionId(), values));
		}

		HeatMapModelDataSource ds = new HeatMapModelDataSource(columns);
		return ds;
	}
}
