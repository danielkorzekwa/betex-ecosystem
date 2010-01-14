package dk.betex.ecosystem.webconsole.client.modules.marketview;

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

import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapModel;
import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapPanel;
import dk.betex.ecosystem.webconsole.client.service.MarketTradedVolumeService;
import dk.betex.ecosystem.webconsole.client.service.MarketTradedVolumeServiceAsync;

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

		mainPanel.add(new Label("Enter marketId:"));
		mainPanel.add(marketIdTextBox);
		final Button displayMarket = new Button("Display market.");
		mainPanel.add(displayMarket);
		mainPanel.add(statusBar);
		mainPanel.add(heatMapPanel);
		displayMarket.addClickHandler(new DisplayMarketHeatMapAction());

		initWidget(mainPanel);
	}

	/** Gets market traded volume from betfair exchange and displays it in a bioHeatMap format. */
	private class DisplayMarketHeatMapAction implements ClickHandler {

		@Override
		public void onClick(ClickEvent arg0) {
			statusBar.setText("Please wait...");
			heatMapPanel.clear();

			int marketId = 0;
			try {
				marketId = Integer.parseInt(marketIdTextBox.getValue());

				/** Get market traded volume and updates bio heat map panel. */
				service.getMarketTradedVolume(marketId, new GetTradedVolumeCallBack());

			} catch (NumberFormatException e) {
				GWT.log("MarketId parsing error.", e);
				Window.alert("MarketId parsing error." + e.getLocalizedMessage());
			}
		}
	}

	/** Gets market traded volume and updates bioheatmap panel. */
	private class GetTradedVolumeCallBack implements AsyncCallback<BioHeatMapModel> {

		@Override
		public void onSuccess(final BioHeatMapModel heatMapModel) {
			final BioHeatMapPanel bioHeatMapPanel = new BioHeatMapPanel(heatMapModel);
			heatMapPanel.add(bioHeatMapPanel);
			statusBar.setText("Please wait...DONE");
		}

		@Override
		public void onFailure(Throwable t) {
			GWT.log("failed", t);
			statusBar.setText("Please wait...ERROR");
			Window.alert("GetMarketTradedVolume failed. " + t.getMessage());
		}
	}
}
