package dk.betex.ecosystem.webconsole.client.modules.markethistory;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import dk.betex.ecosystem.webconsole.client.components.markettradedvolumehistory.MarketTradedVolumeHistoryPanel;
import dk.betex.ecosystem.webconsole.client.service.MarketTradedVolumeService;
import dk.betex.ecosystem.webconsole.client.service.MarketTradedVolumeServiceAsync;

/**
 * Displays market traded volume as a bioHeatMap for a historical data. Slider bar allows to move
 * forward/backwards over the time.
 * 
 * @author korzekwad
 * 
 */
public class MarketHistory extends Composite {

	private final MarketTradedVolumeServiceAsync service = GWT.create(MarketTradedVolumeService.class);

	private final VerticalPanel mainPanel = new VerticalPanel();
	private final TextBox marketIdTextBox = new TextBox();
	private final TextBox minProb = new TextBox();
	private final TextBox maxProb = new TextBox();
	private final Panel heatMapPanel = new VerticalPanel();

	public MarketHistory() {
		
		mainPanel.add(new Label("Enter marketId, min and max probabilities:"));
		mainPanel.add(marketIdTextBox);
		minProb.setText("0");
		maxProb.setText("1");
		mainPanel.add(minProb);
		mainPanel.add(maxProb);
		
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
						heatMapPanel.add(new MarketTradedVolumeHistoryPanel(marketId, timeRange,Double.parseDouble(minProb.getText()),Double.parseDouble(maxProb.getText())));
					}
				});

			} catch (NumberFormatException e) {
				GWT.log("MarketId parsing error.", e);
				Window.alert("MarketId parsing error." + e.getLocalizedMessage());
			}
		}
	}
}
