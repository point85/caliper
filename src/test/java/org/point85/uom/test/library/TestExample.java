package org.point85.uom.test.library;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.point85.uom.Constant;
import org.point85.uom.MeasurementSystem;
import org.point85.uom.Prefix;
import org.point85.uom.Quantity;
import org.point85.uom.Unit;
import org.point85.uom.UnitOfMeasure;
import org.point85.uom.UnitType;

public class TestExample {
	@Test
	public void testExample1() throws Exception {
		MeasurementSystem sys = MeasurementSystem.getSystem();

		// furlong
		UnitOfMeasure furlong = sys.createScalarUOM(UnitType.LENGTH, "furlong", "furlong", "Old English length");
		furlong.setConversion(220, sys.getUOM(Unit.YARD));
		Quantity qFurlong = new Quantity(2, furlong);
		Quantity qMetre = new Quantity(10, Unit.METRE);
		Quantity sum = qMetre.add(qFurlong).convert(Unit.FOOT);
		System.out.println("(1)  " + sum);

		// minutes conversion
		Quantity qMinutes = new Quantity(525600, Unit.MINUTE);
		System.out.println("(2)  " + qMinutes.convert(Unit.JULIAN_YEAR));

		// corn on sale
		UnitOfMeasure cob = sys.createScalarUOM(UnitType.UNCLASSIFIED, "corn cob", "cos", "corn on sale");
		UnitOfMeasure dollar = sys.getUOM(Unit.US_DOLLAR);
		cob.setConversion(0.25, dollar);
		Quantity qCob = new Quantity(1, cob);
		UnitOfMeasure dph = sys.createQuotientUOM(dollar, sys.getHour());
		Quantity wage = new Quantity(15, dph);
		Quantity hours = new Quantity(8, sys.getHour());
		Quantity qTotal = wage.multiply(hours).divide(qCob).convert(sys.getOne());
		System.out.println("(3)  " + qTotal);

		// kilofeet
		Quantity qkm = new Quantity(1, Prefix.KILO, Unit.METRE);
		Quantity qkft = qkm.convert(Prefix.KILO, Unit.FOOT);
		System.out.println("(4)  " + qkft);

		List<UnitOfMeasure> uoms = new ArrayList<>();
		uoms.add(qkft.getUOM());
		uoms.add(sys.getUOM(Unit.FOOT));
		uoms.add(sys.getUOM(Unit.INCH));
		List<Quantity> converted = qkm.convert(uoms);

		for (Quantity qty : converted) {
			System.out.println("  (5)  " + qty);
		}

		// population density
		UnitOfMeasure person = sys.createScalarUOM(UnitType.UNCLASSIFIED, "person", "person", "an individual");
		Quantity qPopulation = new Quantity(2.7, Prefix.MEGA, person);
		UnitOfMeasure sqmi = sys.createPowerUOM(sys.getUOM(Unit.MILE), 2);
		Quantity qArea = new Quantity(5, sqmi);
		System.out.println("(5)  " + qArea.convert(Unit.SQUARE_FOOT).divide(qPopulation));
	}

	@Test
	public void testMetrology() throws Exception {
		MeasurementSystem sys = MeasurementSystem.getSystem();

		// Planck's constant
		Quantity planck = sys.getQuantity(Constant.PLANCK_CONSTANT);

		// cesium-133 hyperfine transition frequency
		Quantity vCs = new Quantity(9.192631770E+09, Unit.HERTZ);

		// speed of light
		Quantity c = sys.getQuantity(Constant.LIGHT_VELOCITY);

		// number of cesium 133 atoms in a kilogram
		Quantity kgCount = c.power(2).divide(planck.multiply(vCs));
		System.out.println(kgCount);
	}
}
