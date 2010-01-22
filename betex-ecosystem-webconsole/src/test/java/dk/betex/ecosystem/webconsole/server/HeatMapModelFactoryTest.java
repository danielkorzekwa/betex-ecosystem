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

	private BioHeatMapModel ds;

	@Before
	public void setUp() {

		List<HeatMapColumn> columns = new ArrayList<HeatMapColumn>();

		List<HeatMapValue> values1 = new ArrayList<HeatMapValue>();
		values1.add(new HeatMapValue(2.1, 35.32));
		values1.add(new HeatMapValue(2.2, 765.56));
		HeatMapColumn heatMapColumn1 = new HeatMapColumn("ManUtd", values1);
		columns.add(heatMapColumn1);

		List<HeatMapValue> values2 = new ArrayList<HeatMapValue>();
		values2.add(new HeatMapValue(3.4, 43.24));
		values2.add(new HeatMapValue(3.6, 65.12));
		HeatMapColumn heatMapColumn2 = new HeatMapColumn("ManUtd", values2);
		columns.add(heatMapColumn2);

		ds = new BioHeatMapModel(columns);
	}

	@Test
	public void testCreateHeatMap() {

		BioHeatMapModel heatMap = HeatMapModelFactory.createHeatMap(ds, 0, 1);

		assertEquals(ds.getColumns().size(), heatMap.getColumns().size());
		assertEquals(101, heatMap.getColumns().get(0).getValues().size());

		assertEquals(765.56, heatMap.getColumns().get(0).getValues().get(45).getCellValue(), 0);
		assertEquals(35.32, heatMap.getColumns().get(0).getValues().get(47).getCellValue(), 0);

		assertEquals(43.24, heatMap.getColumns().get(1).getValues().get(29).getCellValue(), 0);
		assertEquals(65.12, heatMap.getColumns().get(1).getValues().get(27).getCellValue(), 0);
	}
}
