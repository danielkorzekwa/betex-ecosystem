package dk.betex.ecosystem.webconsole.client.components.markethistory;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.SliderBar;

import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapModel;
import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapPanel;
import dk.betex.ecosystem.webconsole.client.service.MarketTradedVolumeService;
import dk.betex.ecosystem.webconsole.client.service.MarketTradedVolumeServiceAsync;

/**
 * Displays market traded volume as a bioHeatMap for a historical data. Slider bar is available to move
 * forward/backwards over the time.
 * 
 * @author korzekwad
 * 
 */
public class MarketHistory extends Composite {

	private final MarketTradedVolumeServiceAsync service = GWT.create(MarketTradedVolumeService.class);

	private final VerticalPanel mainPanel = new VerticalPanel();
	private final TextBox marketIdTextBox = new TextBox();
	private final Panel heatMapPanel = new VerticalPanel();

	public MarketHistory() {

		mainPanel.add(marketIdTextBox);
		final Button displayMarket = new Button("Display market.");
		mainPanel.add(displayMarket);
		mainPanel.add(heatMapPanel);
		displayMarket.addClickHandler(new DisplayMarketHeatMapAction());

		heatMapPanel.setWidth("100%");
		initWidget(mainPanel);
	}

	/** Displays heat map animation for a market traded volume for a historical data. */
	private class DisplayMarketHeatMapAction implements ClickHandler {

		@Override
		public void onClick(ClickEvent arg0) {
			heatMapPanel.clear();

			try {
				final int marketId = Integer.parseInt(marketIdTextBox.getValue());

				/** Get time range for history of market traded volume. */
				service.getTimeRange(marketId, new AsyncCallback<List<Long>>() {

					@Override
					public void onFailure(Throwable t) {
						GWT.log("failed", t);
						Window.alert("GetNumOfRecords failed. " + t.getMessage());
					}

					@Override
					public void onSuccess(final List<Long> timeRange) {
						heatMapPanel.add(new MarketTradedVolumeHistoryPanel(marketId, timeRange));
					}
				});

			} catch (NumberFormatException e) {
				GWT.log("MarketId parsing error.", e);
				Window.alert("MarketId parsing error." + e.getLocalizedMessage());
			}
		}
	}

	/**
	 * Displays history of market traded volume as bio heat map. Slider bar is available to move forward/backwards over
	 * the time (similar to playing videos).
	 */
	private class MarketTradedVolumeHistoryPanel extends Composite {

		private final int marketId;
		private final List<Long> timeRange;

		private Panel mainPanel = new VerticalPanel();
		private BioHeatMapPanel bioHeatMapPanel;
		private Label valueLabel = new Label("");
		private SliderBar slider;

		public MarketTradedVolumeHistoryPanel(int marketId, List<Long> timeRange) {
			this.marketId = marketId;
			this.timeRange = timeRange;

			build();
		}

		private void build() {
			mainPanel.setWidth("100%");

			slider = new SliderBar(timeRange.get(0), timeRange.get(timeRange.size() - 1));
			slider.setStepSize(1.0);
			slider.setCurrentValue(timeRange.get(0));
			SliderChangeListener sliderChangeListener = new SliderChangeListener();
			slider.addChangeListener(sliderChangeListener);
			sliderChangeListener.onChange(slider);
			mainPanel.add(valueLabel);
			mainPanel.add(slider);

			initWidget(mainPanel);
		}

		private class SliderChangeListener implements ChangeListener {
			@Override
			public void onChange(Widget arg0) {
				GWT.log("Start: " + new Date(timeRange.get(0)) + ", End: "
						+ new Date(timeRange.get(timeRange.size() - 1)) + ", Current: "
						+ new Date((long) slider.getCurrentValue()), null);

				service.getMarketTradedVolumeHistory(marketId, (long) slider.getCurrentValue(), Long.MAX_VALUE, 1,
						new AsyncCallback<List<BioHeatMapModel>>() {

							@Override
							public void onFailure(Throwable t) {
								GWT.log("failed", t);
								Window.alert("GetMarketTradedVolumeHistory failed. " + t.getMessage());
							}

							@Override
							public void onSuccess(List<BioHeatMapModel> bioHeatMapModel) {
								if (bioHeatMapModel.size() > 0) {
									valueLabel.setText("Start: " + new Date(timeRange.get(0)) + ", End: "
											+ new Date(timeRange.get(timeRange.size() - 1)) + ", Current: "
											+ new Date((long) slider.getCurrentValue()) + " ,Total: "
											+ bioHeatMapModel.get(0).getTotal());

									if (bioHeatMapPanel == null) {
										bioHeatMapPanel = new BioHeatMapPanel(bioHeatMapModel.get(0));
										mainPanel.add(bioHeatMapPanel);
									} else {
										bioHeatMapPanel.update(bioHeatMapModel.get(0));
									}
									GWT.log("getMarketTradedVolumeHistory success", null);
								}
							}
						});
			}
		}
	}

}
