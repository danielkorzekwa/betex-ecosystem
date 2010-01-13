package dk.betex.ecosystem.webconsole.client.components.markettradedvolumehistory;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.SliderBar;

import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapModel;
import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapPanel;
import dk.betex.ecosystem.webconsole.client.service.MarketTradedVolumeService;
import dk.betex.ecosystem.webconsole.client.service.MarketTradedVolumeServiceAsync;

/**
 * Displays history of market traded volume as bio heat map. Slider bar allows to move forward/backwards over the
 * time (similar to playing videos).
 * 
 * @author korzekwad
 * 
 */
public class MarketTradedVolumeHistoryPanel extends Composite {

	private final MarketTradedVolumeServiceAsync service = GWT.create(MarketTradedVolumeService.class);

	private final int marketId;
	private final List<Long> timeRange;

	private Panel mainPanel = new VerticalPanel();
	private BioHeatMapPanel bioHeatMapPanel;
	private Label legendLabel = new Label("");
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
		mainPanel.add(legendLabel);
		mainPanel.add(slider);

		initWidget(mainPanel);
	}

	private class SliderChangeListener implements ChangeListener {
		@Override
		public void onChange(Widget arg0) {
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
								legendLabel.setText("Start: " + new Date(timeRange.get(0)) + ", End: "
										+ new Date(timeRange.get(timeRange.size() - 1)) + ", Current: "
										+ new Date((long) slider.getCurrentValue()) + " ,Total: "
										+ bioHeatMapModel.get(0).getTotal());

								if (bioHeatMapPanel == null) {
									bioHeatMapPanel = new BioHeatMapPanel(bioHeatMapModel.get(0));
									mainPanel.add(bioHeatMapPanel);
								} else {
									mainPanel.remove(bioHeatMapPanel);
									bioHeatMapPanel = new BioHeatMapPanel(bioHeatMapModel.get(0));
									mainPanel.add(bioHeatMapPanel);
								}
							}
						}
					});
		}
	}
}
