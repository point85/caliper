package org.point85.uom.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.point85.uom.MeasurementSystem;
import org.point85.uom.Unit;
import org.point85.uom.UnitEnumeration;
import org.point85.uom.UnitOfMeasure;
import org.point85.uom.UnitType;

public class TestSystems extends BaseTest {

	@Test
	public void testCustom() throws Exception {

		MeasurementSystem cs1 = null;

		try {
			cs1 = uomService.createSystem("custom1", null, "description");
			fail("no symbol");
		} catch (Exception e) {
		}

		try {
			uomService.createSystem("custom1", "", "description");
			fail("no symbol");
		} catch (Exception e) {
		}

		String symbol = "cs1";
		cs1 = uomService.getSystem(symbol);

		if (cs1 == null) {
			cs1 = uomService.createSystem("custom1", symbol, "description");
		}
		assertNotNull(cs1.getName());
		assertNotNull(cs1.getSymbol());
		assertNotNull(cs1.getDescription());
		assertNotNull(cs1.toString());

		MeasurementSystem cs2 = uomService.getSystem(symbol);
		assertTrue(cs1.equals(cs2));

		try {
			cs1 = uomService.createSystem("custom1", symbol, "description");
			fail("already created");
		} catch (Exception e) {
		}

		cs2 = uomService.createSystem("custom2", "cs2", "description");
	}

	@Test
	public void testUnifiedSystem() throws Exception {

		MeasurementSystem sys = uomService.getUnifiedSystem();
		assertFalse(sys.equals(null));
		assertNotNull(sys.getName());
		assertNotNull(sys.getSymbol());
		assertNotNull(sys.getDescription());
		assertNotNull(sys.toString());

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
		}

		List<UnitEnumeration> allUnits = new ArrayList<>();

		for (UnitEnumeration u : Unit.values()) {
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
		sys.clearCache();
	}

	@Test
	public void testCache() throws Exception {
		MeasurementSystem customSys = uomService.createSystem("Cans", UUID.randomUUID().toString(), "Cache test");

		for (int i = 0; i < 10; i++) {
			customSys.createScalarUOM(UnitType.CUSTOM, null, UUID.randomUUID().toString(), null);
		}

		assertTrue(customSys.getRegisteredUnits().size() == 11);
		customSys.clearCache();
		assertTrue(customSys.getRegisteredUnits().size() == 0);

		UnitOfMeasure uom = customSys.createScalarUOM(UnitType.CUSTOM, CustomUnit.CAN, "Can",
				UUID.randomUUID().toString(), "a can", null);
		uom.setEnumeration(CustomUnit.CAN);

		assertTrue(customSys.getUOM(CustomUnit.CAN).equals(uom));

		customSys.unregisterUnit(uom);
		assertNull(customSys.getUOM(CustomUnit.CAN));

		uomService.unregisterSystem(customSys);
		assertNull(uomService.getSystem(customSys.getSymbol()));

		uomService.unregisterSystem(customSys);
		assertNull(uomService.getSystem(customSys.getSymbol()));
	}
}
