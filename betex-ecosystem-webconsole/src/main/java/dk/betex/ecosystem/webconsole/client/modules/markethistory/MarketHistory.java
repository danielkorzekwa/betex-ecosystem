package dk.betex.ecosystem.webconsole.client.modules.markethistory;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import dk.betex.ecosystem.webconsole.client.components.markettradedvolumehistory.MarketTradedVolumeHistoryPanel;
import dk.betex.ecosystem.webconsole.client.service.MarketFunctionEnum;
import dk.betex.ecosystem.webconsole.client.service.MarketInfo;
import dk.betex.ecosystem.webconsole.client.service.MarketTradedVolumeService;
import dk.betex.ecosystem.webconsole.client.service.MarketTradedVolumeServiceAsync;

/**
 * Displays market traded volume as a bioHeatMap for a historical data. Slider bar allows to move forward/backwards over
 * the time.
 * 
 * @author korzekwad
 * 
 */
public class MarketHistory extends Composite {

	interface MyUiBinder extends UiBinder<Widget, MarketHistory> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private final MarketTradedVolumeServiceAsync service = GWT.create(MarketTradedVolumeService.class);

	@UiField
	ListBox marketsList;
	@UiField
	TextBox minProb;
	@UiField
	TextBox maxProb;
	@UiField
	Panel heatMapPanel = new VerticalPanel();

	private MarketTradedVolumeHistoryPanel marketTradedVolumeHistoryPanel;

	public MarketHistory() {
		initWidget(uiBinder.createAndBindUi(MarketHistory.this));
		
		AsyncCallback<List<MarketInfo>> getMarkets = new AsyncCallback<List<MarketInfo>>() {

			@Override
			public void onFailure(Throwable t) {
				GWT.log("failed", t);
				Window.alert("GetMarketMenuPaths failed. " + t.getMessage());
			}

			@Override
			public void onSuccess(List<MarketInfo> marketInfos) {		
				for(MarketInfo marketInfo: marketInfos) {
					marketsList.addItem(marketInfo.getMenuPath(),"" + marketInfo.getMarketId());
				}
			}
		};	
		service.getMarketInfos(50, getMarkets);
	}

	/** Displays heat map animation for a historical data of a market traded volume. */
	@UiHandler("displayHeatMapButton")
	public void onClick(ClickEvent arg0) {
		heatMapPanel.clear();

		try {
			final int marketIdValue = Integer.parseInt(marketsList.getValue(marketsList.getSelectedIndex()));

			/** Get time range for history of market traded volume. */
			service.getTimeRange(marketIdValue,MarketFunctionEnum.MARKET_TRADED_VOLUME, new AsyncCallback<List<Long>>() {

				@Override
				public void onFailure(Throwable t) {
					GWT.log("failed", t);
					Window.alert("GetNumOfRecords failed. " + t.getMessage());
				}

				@Override
				public void onSuccess(final List<Long> timeRange) {
					if (marketTradedVolumeHistoryPanel != null) {
						marketTradedVolumeHistoryPanel.stopAnimation();
					}
					marketTradedVolumeHistoryPanel = new MarketTradedVolumeHistoryPanel(marketIdValue, timeRange,
							Double.parseDouble(minProb.getText()), Double.parseDouble(maxProb.getText()));
					heatMapPanel.add(marketTradedVolumeHistoryPanel);
				}
			});

		} catch (NumberFormatException e) {
			GWT.log("MarketId parsing error.", e);
			Window.alert("MarketId parsing error." + e.getLocalizedMessage());
		}
	}
}
