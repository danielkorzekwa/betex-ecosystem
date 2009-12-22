package dk.betex.ecosystem.webconsole.client.components.marketview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;

import dk.betex.ecosystem.webconsole.client.model.HeatMapModel;
import dk.betex.ecosystem.webconsole.client.service.MarketTradedVolumeService;
import dk.betex.ecosystem.webconsole.client.service.MarketTradedVolumeServiceAsync;
import dk.betex.ecosystem.webconsole.client.visualizations.BioHeatMap;
import dk.betex.ecosystem.webconsole.client.visualizations.BioHeatMap.Options;

/**
 * Displays heat map for a market traded volume at the current time.
 * 
 * @author korzekwad
 * 
 */
public class MarketView extends Composite {

	private final MarketTradedVolumeServiceAsync service = GWT.create(MarketTradedVolumeService.class);
	
	private final VerticalPanel mainPanel = new VerticalPanel();
	private final TextBox marketIdTextBox = new TextBox();
	private final Label statusBar = new Label();
	private final Panel heatMapPanel = new VerticalPanel();
	
	
	public MarketView() {
	
		mainPanel.add(marketIdTextBox);
		final Button displayMarket = new Button("Display market.");
		mainPanel.add(displayMarket);
		mainPanel.add(statusBar);
		mainPanel.add(heatMapPanel);
		displayMarket.addClickHandler(new DisplayMarketHeatMapAction());

		initWidget(mainPanel);
	}
	
	private class DisplayMarketHeatMapAction implements ClickHandler {

		@Override
		public void onClick(ClickEvent arg0) {
			statusBar.setText("Please wait...");
			heatMapPanel.clear();

			int marketId = 0;
			try {
				marketId = Integer.parseInt(marketIdTextBox.getValue());

				/** Get market traded volume. */
				service.getMarketTradedVolume(marketId, new AsyncCallback<HeatMapModel>() {

					@Override
					public void onSuccess(HeatMapModel heatMapModel) {
						Panel panel = buildMarketTradedVolumeVisualization(heatMapModel);
						heatMapPanel.add(panel);
						statusBar.setText("Please wait...DONE");
					}

					@Override
					public void onFailure(Throwable t) {
						GWT.log("failed", t);
						statusBar.setText("Please wait...ERROR");
						Window.alert("GetMarketTradedVolume failed. " + t.getMessage());
					}
				});

			} catch (NumberFormatException e) {
				GWT.log("MarketId parsing error.", e);
				Window.alert("MarketId parsing error." + e.getLocalizedMessage());
			}
		}
		
		private Panel buildMarketTradedVolumeVisualization(HeatMapModel heatMapModel) {

			Panel panel = new VerticalPanel();

			final Options options = BioHeatMap.Options.create();
			options.setCellWidth(20);
			options.setCellHeight(4);
			options.setNumberOfColors(256);

			final DataTable dataModel = createDataModel(heatMapModel);
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
			
			return panel;
		}

		/**
		 * 
		 * @param heatMapModel  Normalised market traded volume (each runner has a list of all betfair prices.
		 * @return
		 */
		private DataTable createDataModel(HeatMapModel heatMapModel) {

			DataTable data = DataTable.create();
			data.addColumn(ColumnType.STRING, "Price");
			for (int runnerIndex = 0; runnerIndex < heatMapModel.getxAxisLabels().length; runnerIndex++) {
				data.addColumn(ColumnType.NUMBER, heatMapModel.getxAxisLabels()[runnerIndex]);
			}
			int numOfPrices = heatMapModel.getyAxisLabels().length;
			data.addRows(numOfPrices);
			for (int priceIndex = 0; priceIndex < numOfPrices; priceIndex++) {
				
				String xAxisLabel = priceIndex % 5 ==0 ? "" + heatMapModel.getyAxisLabels()[priceIndex]: "";
				data.setValue(priceIndex, 0, xAxisLabel);
				for (int runnerIndex = 0; runnerIndex < heatMapModel.getxAxisLabels().length; runnerIndex++) {
					data.setValue(priceIndex, runnerIndex + 1, heatMapModel.getValues()[runnerIndex][priceIndex]);
				}
			}

			return data;
		}	
	}
}
