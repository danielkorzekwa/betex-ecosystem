package dk.betex.ecosystem.webconsole.client.components.markettradedvolumehistory;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
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
 * Displays history of market traded volume as bio heat map. Slider bar allows to move forward/backwards over the time
 * (similar to playing videos).
 * 
 * @author korzekwad
 * 
 */
public class MarketTradedVolumeHistoryPanel extends Composite {

	private final static String PLAY = "play";
	private final static String PAUSE = "pause";

	private final MarketTradedVolumeServiceAsync service = GWT.create(MarketTradedVolumeService.class);

	private final int marketId;
	private final List<Long> timeRange;
	private final double minProb;
	private final double maxProb;

	private Panel mainPanel = new VerticalPanel();
	private BioHeatMapPanel bioHeatMapPanel;
	private Label legendLabel = new Label("");
	private Button playButton = new Button(PLAY);
	private TextBox playSpeed = new TextBox();
	private Label errorLabel = new Label();
	private SliderBar slider;

	/**
	 * The range min/max allows to zoom in/out inside the market traded volume and to analyse given range of
	 * probabilities in more details.
	 * 
	 * @param marketId
	 * @param timeRange
	 * @param minProb
	 * @param maxProb
	 */
	public MarketTradedVolumeHistoryPanel(int marketId, List<Long> timeRange, double minProb, double maxProb) {
		this.marketId = marketId;
		this.timeRange = timeRange;
		this.minProb = minProb;
		this.maxProb = maxProb;

		build();
	}

	private void build() {
		mainPanel.setWidth("100%");

		mainPanel.add(playButton);
		playSpeed.setText("20");
		mainPanel.add(playSpeed);
		mainPanel.add(errorLabel);

		slider = new SliderBar(timeRange.get(0), timeRange.get(timeRange.size() - 1));
		slider.setStepSize(1.0);
		slider.setCurrentValue(timeRange.get(0));
		SliderChangeListener sliderChangeListener = new SliderChangeListener();
		slider.addChangeListener(sliderChangeListener);
		sliderChangeListener.onChange(slider);
		mainPanel.add(legendLabel);
		mainPanel.add(slider);

		playButton.addClickHandler(new PlayPauseAction(sliderChangeListener));

		initWidget(mainPanel);
	}

	private class PlayPauseAction implements ClickHandler {

		private final Timer timer;
		private final SliderChangeListener sliderChangeListener;

		public PlayPauseAction(SliderChangeListener listener) {
			this.sliderChangeListener = listener;
			timer = new Timer() {

				@Override
				public void run() {
					slider.setCurrentValue(slider.getCurrentValue() + 100*Integer.parseInt(playSpeed.getText()));
						sliderChangeListener.onChange(slider);
				}
			};
		}

		@Override
		public void onClick(ClickEvent arg0) {
			if (playButton.getText().equals(PLAY)) {
				timer.scheduleRepeating(100);
				playButton.setText(PAUSE);
			} else if (playButton.getText().equals(PAUSE)) {
				timer.cancel();
				playButton.setText(PLAY);
			}
		}
	}

	private class SliderChangeListener implements ChangeListener {
		@Override
		public void onChange(Widget arg0) {
			service.getMarketTradedVolumeHistory(marketId, (long) slider.getCurrentValue(), Long.MAX_VALUE, 1, minProb,
					maxProb, new AsyncCallback<List<BioHeatMapModel>>() {

						@Override
						public void onFailure(Throwable t) {
							GWT.log("failed", t);
							errorLabel.setText(new Date() + ": " + t.getLocalizedMessage());
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
