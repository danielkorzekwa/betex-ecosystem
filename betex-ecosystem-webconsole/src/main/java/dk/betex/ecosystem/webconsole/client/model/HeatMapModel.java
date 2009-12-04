package dk.betex.ecosystem.webconsole.client.model;

import java.io.Serializable;

/** Data model for a heat map chart.
 * 
 * @author korzekwad
 *
 */
public class HeatMapModel implements Serializable{

	/** Labels for x-axis.*/
	private String[] xAxisLabels;
	
	/** Labels for y-axis.*/
	private String[] yAxisLabels;
	
	/**[x][y] - value for (x,y) coordinates of a heat map chart.*/
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
	
}
