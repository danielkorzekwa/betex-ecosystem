package dk.betex.ecosystem.webconsole.client.visualizations;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.CommonOptions;
import com.google.gwt.visualization.client.visualizations.Visualization;

/**
 * GWT visualization wrapper for BioHeapMap
 * 
 * @see <a href= "http://informatics.systemsbiology.net/visualizations/heatmap/bioheatmap.html" >Pie Chart Visualization
 *      Reference</a>
 * 
 * @author daniel
 * 
 */
public class BioHeatMap extends Visualization<BioHeatMap.Options> {

	/**
	 * Options for drawing the BioHeapMap.
	 * 
	 */
	public static class Options extends CommonOptions {

		public static Options create() {
			return JavaScriptObject.createObject().cast();
		}

		protected Options() {
		}

		public final native void setCellWidth(int cellWidth) /*-{
		      this.cellWidth = cellWidth;
		    }-*/;
		
		public final native void setCellHeight(int cellHeight) /*-{
	      this.cellHeight = cellHeight;
	    }-*/;
	}
	
	public BioHeatMap() {
		super();
	}

	public BioHeatMap(AbstractDataTable data, Options options) {
		super(data, options);
	}

	@Override
	protected native JavaScriptObject createJso(Element parent) /*-{
	    return new $wnd.org.systemsbiology.visualization.BioHeatMap(parent);
	  }-*/;
}
