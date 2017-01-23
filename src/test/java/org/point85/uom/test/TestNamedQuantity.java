package org.point85.uom.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.point85.uom.Constant;
import org.point85.uom.MeasurementSystem;
import org.point85.uom.NamedQuantity;

public class TestNamedQuantity extends BaseTest {
	@Test
	public void testCase() throws Exception {
		MeasurementSystem sys = MeasurementSystem.getSystem();

		for (Constant value : Constant.values()) {
			NamedQuantity q = sys.getQuantity(value);
			assertTrue(q.getName() != null);
			assertTrue(q.getSymbol() != null);
			assertTrue(q.getDescription() != null);
			assertTrue(q.getAmount() != null);
			assertTrue(q.getUOM() != null);
			assertTrue(q.toString() != null);
		}

	}
}
