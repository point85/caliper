package org.point85.uom.test.persistence;

import org.point85.uom.Unit;
import org.point85.uom.UnitOfMeasure;
import org.point85.uom.test.app.PersistentMeasurementSystem;

public class TestPersistency {

	// persistent measurement system
	private PersistentMeasurementSystem sys = PersistentMeasurementSystem.getSystem();

	public void saveUOMs() throws Exception {
		for (Unit unit : Unit.values()) {
			saveUnit(unit);
		}
	}

	public void saveUnit(Unit unit) throws Exception {
		UnitOfMeasure uom = sys.fetchUOMByUnit(unit);

		if (uom == null) {
			uom = sys.getUOM(unit);
		}

		if (uom.getCategory() == null) {
			uom.setCategory(uom.getUnitType().name());
		}
		
		System.out.println("Saving UOM " + uom.getName());
		sys.saveUOM(uom);
	}

	public static void main(String[] args) {
		TestPersistency tester = new TestPersistency();

		try {
			tester.saveUOMs();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Test finished!");
	}
}
