package dk.betex.ecosystem.webconsole.client.components.markettradedvolumehistory;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.SliderBar;
import com.google.gwt.widgetideas.client.SliderBar.LabelFormatter;

import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapPanel;
import dk.betex.ecosystem.webconsole.client.service.HeatMapModelDataSource;
import dk.betex.ecosystem.webconsole.client.service.MarketFunctionEnum;
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

	interface MyUiBinder extends UiBinder<Widget, MarketTradedVolumeHistoryPanel> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private DateTimeFormat df = DateTimeFormat.getMediumDateTimeFormat();
	private NumberFormat nf = NumberFormat.getFormat(".##");
	private final static String PLAY = "Play";
	private final static String PAUSE = "Pause";

	private final MarketTradedVolumeServiceAsync service = GWT.create(MarketTradedVolumeService.class);

	private final int marketId;
	private final List<Long> timeRange;
	private final double minProb;
	private final double maxProb;

	@UiField Panel mainPanel;
	@UiField Button playButton;
	@UiField TextBox playSpeed;
	@UiField Label errorLabel;
	@UiField Label timeLabel;
	@UiField Label totalLabel;
	@UiField SliderBar slider;
	private BioHeatMapPanel bioHeatMapPanel;
	
	/**Plays animation of historical market data.*/
	private final Timer timer;
	
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

		initWidget(uiBinder.createAndBindUi(this));

		final SliderChangeListener sliderChangeListener = new SliderChangeListener();
		slider.addChangeListener(sliderChangeListener);
		sliderChangeListener.onChange(slider);
		slider.setNumLabels(1);
		slider.setLabelFormatter(new DateTimeLabelFormatter());

		timer = new Timer() {
			@Override
			public void run() {
				slider.setCurrentValue(slider.getCurrentValue() + 100 * Integer.parseInt(playSpeed.getText()));
				//sliderChangeListener.onChange(slider);
			}
		};
		playButton.setText(PLAY);
	}

	/** Stop animation of historical data. */
	public void stopAnimation() {
		timer.cancel();
	}

	/** Used by MyUiBinder to instantiate SliderBar */
	@UiFactory
	SliderBar makeSliderBar() { // method name is insignificant
		return new SliderBar(timeRange.get(0), timeRange.get(timeRange.size() - 1));
	}
	
	@UiHandler("playButton")
	public void onClick(ClickEvent arg0) {
		if (playButton.getText().equals(PLAY)) {
			timer.scheduleRepeating(100);
			playButton.setText(PAUSE);
		} else if (playButton.getText().equals(PAUSE)) {
			timer.cancel();
			playButton.setText(PLAY);
		}
	}
	
	private class SliderChangeListener implements ChangeListener {
		@Override
		public void onChange(Widget arg0) {
			service.getMarketData(marketId, MarketFunctionEnum.MARKET_TRADED_VOLUME,(long) slider.getCurrentValue(), Long.MAX_VALUE, 1, minProb,
					maxProb, new AsyncCallback<List<HeatMapModelDataSource>>() {

						@Override
						public void onFailure(Throwable t) {
							GWT.log("failed", t);
							errorLabel.setText(new Date() + ": " + t.getLocalizedMessage());
						}

						@Override
						public void onSuccess(List<HeatMapModelDataSource> bioHeatMapModel) {
							if (bioHeatMapModel.size() > 0) {
								timeLabel.setText(df.format(new Date((long) slider.getCurrentValue())));
								totalLabel.setText(nf.format(bioHeatMapModel.get(0).getTotal()));
								
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
	
	private class DateTimeLabelFormatter implements LabelFormatter {

		@Override
		public String formatLabel(SliderBar slider, double value) {
			return df.format(new Date((long)value));
		}
		
	}
	

}
