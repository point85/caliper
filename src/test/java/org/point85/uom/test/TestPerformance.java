package org.point85.uom.test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.point85.uom.MeasurementSystem;
import org.point85.uom.Quantity;
import org.point85.uom.Unit;
import org.point85.uom.UnitOfMeasure;
import org.point85.uom.UnitType;

public class TestPerformance {

	// unit map
	private Map<UnitType, List<UnitOfMeasure>> unitListMap = new HashMap<UnitType, List<UnitOfMeasure>>();

	private void addUnit(UnitOfMeasure uom) {
		List<UnitOfMeasure> unitList = unitListMap.get(uom.getUnitType());

		if (unitList == null) {
			unitList = new ArrayList<UnitOfMeasure>();
			unitListMap.put(uom.getUnitType(), unitList);
		}
		unitList.add(uom);
	}

	// @Test
	public void runSingleTest() throws Exception {
		MeasurementSystem sys = MeasurementSystem.getSystem();

		for (Unit u : Unit.values()) {
			UnitOfMeasure uom = sys.getUOM(u);
			this.addUnit(uom);
		}

		// for each unit type, execute the quantity operations
		for (Entry<UnitType, List<UnitOfMeasure>> entry : unitListMap.entrySet()) {
			// run the matrix
			for (UnitOfMeasure rowUOM : entry.getValue()) {

				// row quantity
				Quantity rowQty = new Quantity(BigDecimal.TEN, rowUOM);

				for (UnitOfMeasure colUOM : entry.getValue()) {

					// column qty
					Quantity colQty = new Quantity(BigDecimal.TEN, colUOM);

					// arithmetic operations
					rowQty.add(colQty);
					rowQty.subtract(colQty);

					// offsets are not supported
					if (rowUOM.getOffset().compareTo(BigDecimal.ZERO) == 0
							&& colUOM.getOffset().compareTo(BigDecimal.ZERO) == 0) {
						rowQty.multiply(colQty);
						rowQty.divide(colQty);
						rowQty.invert();
					}

					rowQty.convert(colUOM);
					rowQty.equals(colQty);
					rowQty.getAmount();
					rowQty.getUOM();
					rowQty.toString();
				}
			}
		}
	}
}
