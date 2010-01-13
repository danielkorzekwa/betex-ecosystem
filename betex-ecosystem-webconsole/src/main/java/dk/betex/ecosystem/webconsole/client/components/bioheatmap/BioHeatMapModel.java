package dk.betex.ecosystem.webconsole.client.components.bioheatmap;

import com.google.gwt.user.client.rpc.IsSerializable;

/** Data model for the bioheatmap panel.
 * 
 * @author korzekwad
 *
 */
public class BioHeatMapModel implements IsSerializable{

	/** Labels for x-axis.*/
	private String[] xAxisLabels;
	
	/** Labels for y-axis.*/
	private String[] yAxisLabels;
	
	/**[x][y] - value for (x,y) coordinates of a heat map.*/
	private double[][] values;

	public String[] getxAxisLabels() {
		return xAxisLabels;
	}

	public void setxAxisLabels(String[] xAxisLabels) {
		this.xAxisLabels = xAxisLabels;
	}

	public String[] getyAxisLabels() {
		return yAxisLabels;
	}

	public void setyAxisLabels(String[] yAxisLabels) {
		this.yAxisLabels = yAxisLabels;
	}

	public double[][] getValues() {
		return values;
	}

	public void setValues(double[][] values) {
		this.values = values;
	}
	
	/**Returns sum of all values for all cells in the matrix.*/
	public double getTotal() {
		double total=0;
		for(int i=0;i<values.length;i++) {
			for(int j=0;j<values[i].length;j++) {
				total += values[i][j];
			}
		}
		return total;
	}
	
}
