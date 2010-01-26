package dk.betex.ecosystem.webconsole.server;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapModel;
import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapModel.HeatMapColumn;
import dk.betex.ecosystem.webconsole.client.components.bioheatmap.BioHeatMapModel.HeatMapValue;

public class HeatMapModelFactoryTest {

	@Test
	public void testCreateHeatMap() {

		BioHeatMapModel ds1 = createModel1();
		BioHeatMapModel heatMap = HeatMapModelFactory.createHeatMap(ds1, 0, 1);

		assertEquals(ds1.getColumns().size(), heatMap.getColumns().size());

		assertEquals(101, heatMap.getColumns().get(0).getValues().size());
		assertEquals(765.56, heatMap.getColumns().get(0).getValues().get(45).getCellValue(), 0);
		assertEquals(35.32, heatMap.getColumns().get(0).getValues().get(47).getCellValue(), 0);

		assertEquals(101, heatMap.getColumns().get(1).getValues().size());
		assertEquals(43.24, heatMap.getColumns().get(1).getValues().get(29).getCellValue(), 0);
		assertEquals(65.12, heatMap.getColumns().get(1).getValues().get(27).getCellValue(), 0);
	}

	@Test
	public void testDelta() {
		BioHeatMapModel model1 = HeatMapModelFactory.createHeatMap(createModel1(), 0, 1);
		BioHeatMapModel model2 = HeatMapModelFactory.createHeatMap(createModel2(), 0, 1);

		BioHeatMapModel delta = HeatMapModelFactory.delta(model1, model2);

		assertEquals(model2.getColumns().size(), delta.getColumns().size());

		assertEquals(model2.getColumns().get(0).getValues().size(), delta.getColumns().get(0).getValues().size());
		assertEquals(20, delta.getColumns().get(0).getValues().get(45).getCellValue(), 0);
		assertEquals(2, delta.getColumns().get(0).getValues().get(47).getCellValue(), 0);
		assertEquals(22.34, delta.getColumns().get(0).getValues().get(43).getCellValue(), 0);

		assertEquals(model2.getColumns().get(1).getValues().size(), delta.getColumns().get(1).getValues().size());
		assertEquals(5, delta.getColumns().get(1).getValues().get(29).getCellValue(), 0);
		assertEquals(4.5, delta.getColumns().get(1).getValues().get(27).getCellValue(), 0);
		assertEquals(17.43, delta.getColumns().get(1).getValues().get(26).getCellValue(), 0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDeltaNumberOfColumnsIsNotTheSame() {
		BioHeatMapModel heatMapModel1 = HeatMapModelFactory.createHeatMap(createModel1(), 0, 1);
		BioHeatMapModel heatMapModel2 = HeatMapModelFactory.createHeatMap(createModel2(), 0, 1);
		heatMapModel1.getColumns().remove(0);
		
		HeatMapModelFactory.delta(heatMapModel1, heatMapModel2);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDeltaNumberOfValuesIsNotTheSame() {
		BioHeatMapModel heatMapModel1 = HeatMapModelFactory.createHeatMap(createModel1(), 0, 1);
		BioHeatMapModel heatMapModel2 = HeatMapModelFactory.createHeatMap(createModel2(), 0, 1);
		heatMapModel1.getColumns().get(0).getValues().remove(1);
		
		HeatMapModelFactory.delta(heatMapModel1, heatMapModel2);
	}

	private BioHeatMapModel createModel1() {
		List<HeatMapColumn> columns = new ArrayList<HeatMapColumn>();

		List<HeatMapValue> values1 = new ArrayList<HeatMapValue>();
		values1.add(new HeatMapValue(2.1, 35.32));
		values1.add(new HeatMapValue(2.2, 765.56));
		HeatMapColumn heatMapColumn1 = new HeatMapColumn("ManUtd", values1);
		columns.add(heatMapColumn1);

		List<HeatMapValue> values2 = new ArrayList<HeatMapValue>();
		values2.add(new HeatMapValue(3.4, 43.24));
		values2.add(new HeatMapValue(3.6, 65.12));
		HeatMapColumn heatMapColumn2 = new HeatMapColumn("Arsenal", values2);
		columns.add(heatMapColumn2);

		return new BioHeatMapModel(columns);
	}

	private BioHeatMapModel createModel2() {
		List<HeatMapColumn> columns = new ArrayList<HeatMapColumn>();

		List<HeatMapValue> values1 = new ArrayList<HeatMapValue>();
		values1.add(new HeatMapValue(2.1, 37.32));
		values1.add(new HeatMapValue(2.2, 785.56));
		values1.add(new HeatMapValue(2.3, 22.34));
		HeatMapColumn heatMapColumn1 = new HeatMapColumn("ManUtd", values1);
		columns.add(heatMapColumn1);

		List<HeatMapValue> values2 = new ArrayList<HeatMapValue>();
		values2.add(new HeatMapValue(3.4, 48.24));
		values2.add(new HeatMapValue(3.6, 69.62));
		values2.add(new HeatMapValue(3.8, 17.43));
		HeatMapColumn heatMapColumn2 = new HeatMapColumn("Arsenal", values2);
		columns.add(heatMapColumn2);

		return new BioHeatMapModel(columns);
	}
}
