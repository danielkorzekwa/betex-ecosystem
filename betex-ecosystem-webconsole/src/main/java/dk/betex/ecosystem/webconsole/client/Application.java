package dk.betex.ecosystem.webconsole.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.visualizations.PieChart;

import dk.betex.ecosystem.webconsole.client.model.MarketTradedVolume;
import dk.betex.ecosystem.webconsole.client.model.PriceTradedVolume;
import dk.betex.ecosystem.webconsole.client.model.RunnerTradedVolume;
import dk.betex.ecosystem.webconsole.client.service.MarketTradedVolumeService;
import dk.betex.ecosystem.webconsole.client.service.MarketTradedVolumeServiceAsync;
import dk.betex.ecosystem.webconsole.client.visualizations.BioHeatMap;
import dk.betex.ecosystem.webconsole.client.visualizations.BioHeatMap.Options;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Application implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		// Create a callback to be called when the visualization API
		// has been loaded.
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				final MarketTradedVolumeServiceAsync service = GWT.create(MarketTradedVolumeService.class);

				Panel panel = RootPanel.get();
				final TextBox marketIdTextBox = new TextBox();
				panel.add(marketIdTextBox);
				final Button displayMarket = new Button("Display market.");
				panel.add(displayMarket);
				final Label statusBar = new Label();
				panel.add(statusBar);
				displayMarket.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent arg0) {
						statusBar.setText("Please wait...");
						
						int marketId = 0;
						try {
							marketId = Integer.parseInt(marketIdTextBox.getValue());

							/** Get market traded volume. */
							service.getMarketTradedVolumeForAllPrices(marketId,
									new AsyncCallback<MarketTradedVolume>() {

										@Override
										public void onSuccess(MarketTradedVolume marketTradedVolume) {
											buildMarketTradedVolumeVisualization(marketTradedVolume);
											statusBar.setText("Please wait...DONE");	
										}

										@Override
										public void onFailure(Throwable t) {
											GWT.log("failed", t);
											Window.alert("GetMarketTradedVolume failed. " + t.getLocalizedMessage());
										}
									});

						} catch (NumberFormatException e) {
							GWT.log("Error parsing marketId", e);
							Window.alert("Error parsing marketId. " + e.getLocalizedMessage());
						}
					}
				});
			}
		};

		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback, PieChart.PACKAGE);

	}

	private void buildMarketTradedVolumeVisualization(MarketTradedVolume marketTradedVolume) {

		Panel panel = RootPanel.get();

		final Options options = BioHeatMap.Options.create();
		options.setCellWidth(15);
		options.setCellHeight(2);

		final DataTable dataModel = createDataModel(marketTradedVolume);
		final BioHeatMap bioHeatMap = new BioHeatMap(dataModel, options);
		panel.add(bioHeatMap);

		final Timer timer = new Timer() {
			@Override
			public void run() {

				for (int priceIndex = 0; priceIndex < dataModel.getNumberOfRows(); priceIndex++) {
					for (int runnerId = 0; runnerId < dataModel.getNumberOfColumns() - 1; runnerId++) {
						dataModel.setValue(priceIndex, runnerId + 1, Random.nextInt(100));
					}
				}
				bioHeatMap.draw(dataModel, options);
			}
		};

		Button start = new Button("Start animation");
		panel.add(start);
		start.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				timer.scheduleRepeating(200);
			}
		});
	}

	/** Normalised market traded volume (each runner has list of all betfair prices. */
	private DataTable createDataModel(MarketTradedVolume marketTradedVolume) {

		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Price");
		for (int runnerIndex = 0; runnerIndex < marketTradedVolume.getRunnerTradedVolume().size(); runnerIndex++) {
			data.addColumn(ColumnType.NUMBER, "r" + runnerIndex);
		}
		int numOfPrices = marketTradedVolume.getRunnerTradedVolume().get(0).getPriceTradedVolume().size();
		data.addRows(numOfPrices);
		for (int priceIndex = 0; priceIndex < numOfPrices; priceIndex++) {
			double rowPrice = marketTradedVolume.getRunnerTradedVolume().get(0).getPriceTradedVolume().get(priceIndex)
					.getPrice();
			data.setValue(priceIndex, 0, "" + rowPrice);
			for (int runnerIndex = 0; runnerIndex < marketTradedVolume.getRunnerTradedVolume().size(); runnerIndex++) {
				RunnerTradedVolume runnerTradedVolume = marketTradedVolume.getRunnerTradedVolume().get(runnerIndex);
				PriceTradedVolume priceTradedVolume = runnerTradedVolume.getPriceTradedVolume().get(priceIndex);
				if (priceTradedVolume.getPrice() != rowPrice) {
					throw new IllegalStateException("Data consistency error!. rowPrice != runner price");
				}
				data.setValue(priceIndex, runnerIndex + 1, priceTradedVolume.getTradedVolume());
			}
		}

		return data;
	}
}
