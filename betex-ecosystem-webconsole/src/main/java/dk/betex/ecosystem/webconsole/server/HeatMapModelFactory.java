package dk.betex.ecosystem.webconsole.server;

import java.util.ArrayList;
import java.util.List;

import dk.betex.ecosystem.webconsole.client.service.BioHeatMapModel;
import dk.betex.ecosystem.webconsole.client.service.BioHeatMapModel.HeatMapColumn;
import dk.betex.ecosystem.webconsole.client.service.BioHeatMapModel.HeatMapValue;

/**
 * Creates MarketTradedVolume object.
 * 
 * @author korzekwad
 * 
 */
public class HeatMapModelFactory {

	/**
	 * Returns market traded volume in a form of bio heat map. The range min/max allows to zoom in/out inside the
	 * market traded volume and to analyse given range of probabilities in more details.
	 * 
	 * @param marketTradedVolume
	 * @param min
	 *            Minimum probability of traded volume that the heat map model is created for. From 0 to 1
	 * @param max
	 *            Maximum probability of traded volume that the heat map model is created for. From 0 to 1
	 * 
	 * 
	 * @return
	 */
	public static BioHeatMapModel createHeatMap(BioHeatMapModel ds, double min, double max) {

		List<HeatMapColumn> columns = new ArrayList<HeatMapColumn>();
		
		for (int columnIndex = 0; columnIndex <  ds.getColumns().size(); columnIndex++) {
			HeatMapColumn column = ds.getColumns().get(columnIndex);
			
			/**Create 100 empty values*/
			List<HeatMapValue> values = new ArrayList<HeatMapValue>();
			for(int rowIndex=0;rowIndex<101;rowIndex++) {
				double rowValue = ((double)rowIndex/100  * (max - min) + min);
				values.add(new HeatMapValue(rowValue, 0));
			}	
			
			/**Reduce input values.*/
			for (int rowIndex=0;rowIndex<column.getValues().size();rowIndex++) {
				double prob = 1 / column.getValues().get(rowIndex).getRowValue();
				if (prob >= min && prob <= max) {
					double scaledProb = (prob - min) / (max - min);
					HeatMapValue value = values.get((int) (scaledProb * 100));
					value.setCellValue(value.getCellValue() + column.getValues().get(rowIndex).getCellValue());
				}
			}
			
			columns.add(new HeatMapColumn( column.getLabel(), values));
		}
		
		BioHeatMapModel heatMapModel = new BioHeatMapModel(columns );
		return heatMapModel;
	}

}
