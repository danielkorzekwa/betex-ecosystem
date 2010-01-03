package dk.betex.ecosystem.webconsole.client.components.bioheatmap;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;

import dk.betex.ecosystem.webconsole.client.visualizations.BioHeatMap;
import dk.betex.ecosystem.webconsole.client.visualizations.BioHeatMap.Options;

/**
 * Gwt panel that displays bio heat map.
 * 
 * @author korzekwad
 * 
 */
public class BioHeatMapPanel extends Composite {

	/** Data model for BioHeatMap visualisation.*/
	private DataTable dataModel;
	/** Options for BioHeatMap visualisation.*/
	private Options options;

	/** Bio heat map visualisation object. */
	private BioHeatMap bioHeatMap;

	public BioHeatMapPanel(BioHeatMapModel heatMapModel) {
		
		options = BioHeatMap.Options.create();
		options.setCellWidth(20);
		options.setCellHeight(4);
		options.setNumberOfColors(256);

		dataModel = createDataModel(heatMapModel);
		bioHeatMap = new BioHeatMap(dataModel, options);

		initWidget(bioHeatMap);
	}

	/** Updates bioheatmap panel with new data. */
	public void update(BioHeatMapModel bioHeatMapModel) {
		
		int numOfRows = bioHeatMapModel.getyAxisLabels().length;
		int numOfColumns =  bioHeatMapModel.getxAxisLabels().length;
		
		if(numOfRows > dataModel.getNumberOfRows()) {
			throw new IllegalArgumentException("Can't update bio heat map. New numOfRows is bigger than numOfRows in data model.");
		}
		if(numOfColumns > dataModel.getNumberOfColumns()) {
			throw new IllegalArgumentException("Can't update bio heat map. New numOfColumns is bigger than numOfColumns in data model.");
		}
		
		for (int rowIndex = 0; rowIndex < numOfRows; rowIndex++) {
			for (int columnIndex = 0; columnIndex <numOfColumns; columnIndex++) {
				dataModel.setValue(rowIndex, columnIndex + 1, bioHeatMapModel.getValues()[columnIndex][rowIndex]);
			}
		}

		bioHeatMap.draw(dataModel, options);
	}

	/**
	 * Creates data model for bioHeatMap visualisation.
	 * 
	 * @param heatMapModel
	 *            
	 * @return
	 */
	private DataTable createDataModel(BioHeatMapModel heatMapModel) {

		DataTable data = DataTable.create();
		data.addColumn(ColumnType.STRING, "Y_label");
		for (int columnIndex = 0; columnIndex < heatMapModel.getxAxisLabels().length; columnIndex++) {
			data.addColumn(ColumnType.NUMBER, heatMapModel.getxAxisLabels()[columnIndex]);
		}
		int numOfRows = heatMapModel.getyAxisLabels().length;
		data.addRows(numOfRows);
		for (int rowIndex = 0; rowIndex < numOfRows; rowIndex++) {

			String yAxisLabel = rowIndex % 5 == 0 ? "" + heatMapModel.getyAxisLabels()[rowIndex] : "";
			data.setValue(rowIndex, 0, yAxisLabel);
			for (int columnIndex = 0; columnIndex < heatMapModel.getxAxisLabels().length; columnIndex++) {
				data.setValue(rowIndex, columnIndex + 1, heatMapModel.getValues()[columnIndex][rowIndex]);
			}
		}

		return data;
	}

}
