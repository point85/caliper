/*
MIT License

Copyright (c) 2016 Kent Randall

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package org.point85.uom.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;
import org.point85.uom.MeasurementSystem;
import org.point85.uom.Unit;
import org.point85.uom.UnitOfMeasure;
import org.point85.uom.UnitType;

public class TestSystems extends BaseTest {

	@Test
	public void testUnifiedSystem() throws Exception {

		MeasurementSystem sys = MeasurementSystem.getSystem();
		assertFalse(sys.equals(null));

		Map<String, UnitOfMeasure> unitMap = new HashMap<>();

		// check the SI units
		for (Unit unit : Unit.values()) {
			UnitOfMeasure uom = sys.getUOM(unit);

			assertNotNull(uom);
			assertNotNull(uom.getName());
			assertNotNull(uom.getSymbol());
			assertNotNull(uom.getDescription());
			assertNotNull(uom.toString());
			assertNotNull(uom.getBaseSymbol());
			assertNotNull(uom.getAbscissaUnit());
			assertNotNull(uom.getScalingFactor());
			assertNotNull(uom.getOffset());
			assertNotNull(uom.getUnifiedSymbol());

			// symbol uniqueness
			assertFalse(unitMap.containsKey(uom.getSymbol()));
			unitMap.put(uom.getSymbol(), uom);
		}

		List<Unit> allUnits = new ArrayList<Unit>();

		for (Unit u : Unit.values()) {
			allUnits.add(u);
		}

		for (UnitOfMeasure uom : sys.getRegisteredUnits()) {
			if (uom.getEnumeration() != null) {
				assertTrue(allUnits.contains(uom.getEnumeration()));
			}
		}

		for (UnitType unitType : UnitType.values()) {
			UnitType found = null;
			for (UnitOfMeasure u : sys.getRegisteredUnits()) {
				if (u.getUnitType().equals(unitType)) {
					found = u.getUnitType();
					break;
				}
			}

			if (found == null && !unitType.equals(UnitType.CUSTOM)) {
				fail("No unit found for type " + unitType);
			}
		}
	}

	@Test
	public void testCache() throws Exception {
		MeasurementSystem sys = MeasurementSystem.getSystem();

		// unit cache
		sys.getOne();

		int before = sys.getRegisteredUnits().size();

		for (int i = 0; i < 10; i++) {
			sys.createScalarUOM(UnitType.CUSTOM, null, UUID.randomUUID().toString(), null);
		}

		int after = sys.getRegisteredUnits().size();

		assertTrue(after == (before + 10));

	}
}
