package dk.betex.ecosystem.webconsole.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.PieChart;

import dk.betex.ecosystem.webconsole.client.modules.markethistory.MarketHistory;
import dk.betex.ecosystem.webconsole.client.modules.marketview.MarketView;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Application implements EntryPoint {

	private TabPanel tp;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		// Create a callback to be called when the visualization API
		// has been loaded.
		Runnable onLoadCallback = new Runnable() {
			public void run() {
				// Create a tab panel with three tabs, each of which displays a different
				// piece of text.
				tp = new TabPanel();
				tp.add(new MarketView(), "Market View");
				tp.add(new MarketHistory(), "Market History");
				// Show the 'bar' tab initially.
				tp.selectTab(0);
				tp.setWidth("100%");
							
				// Add it to the root panel.
				RootPanel.get().add(tp);
			}
		};

		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback, PieChart.PACKAGE);
	}

	
}
