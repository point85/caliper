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

import static org.hamcrest.number.BigDecimalCloseTo.closeTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;
import org.point85.uom.Conversion;
import org.point85.uom.MeasurementSystem;
import org.point85.uom.Prefix;
import org.point85.uom.Quantity;
import org.point85.uom.Unit;
import org.point85.uom.UnitOfMeasure;
import org.point85.uom.UnitType;

public class TestQuantity extends BaseTest {

	@Test
	public void testAllUnits() throws Exception {
		MeasurementSystem sys = MeasurementSystem.getSystem();

		for (Unit u : Unit.values()) {
			UnitOfMeasure uom1 = sys.getUOM(u);
			UnitOfMeasure uom2 = sys.getUOM(u);
			assertTrue(uom1.equals(uom2));

			Quantity q1 = new Quantity(BigDecimal.TEN, uom1);
			Quantity q2 = q1.convert(uom2);
			assertTrue(q1.equals(q2));
		}
	}

	@Test
	public void testTime() throws Exception {
		MeasurementSystem sys = MeasurementSystem.getSystem();

		UnitOfMeasure second = sys.getSecond();
		UnitOfMeasure minute = sys.getMinute();

		Quantity oneMin = new Quantity(BigDecimal.ONE, minute);
		Quantity oneSec = new Quantity(BigDecimal.ONE, second);
		Quantity converted = oneMin.convert(second);
		BigDecimal bd60 = Quantity.createAmount("60");

		assertThat(converted.getAmount(), closeTo(bd60, DELTA6));
		assertTrue(converted.getUOM().equals(second));

		Quantity sixty = oneMin.divide(oneSec);
		assertThat(sixty.getAmount(), closeTo(BigDecimal.ONE, DELTA6));
		assertThat(sixty.getUOM().getScalingFactor(), closeTo(bd60, DELTA6));

		Quantity q1 = sixty.convert(sys.getOne());
		assertTrue(q1.getUOM().equals(sys.getOne()));
		assertThat(q1.getAmount(), closeTo(bd60, DELTA6));

		q1 = q1.multiply(oneSec);
		assertTrue(q1.convert(second).getUOM().equals(second));
		assertThat(q1.getAmount(), closeTo(bd60, DELTA6));

		q1 = q1.convert(minute);
		assertTrue(q1.getUOM().equals(minute));
		assertThat(q1.getAmount(), closeTo(BigDecimal.ONE, DELTA6));

		assertTrue(q1.hashCode() != 0);

	}

	@Test
	public void testTemperature() throws Exception {
		MeasurementSystem sys = MeasurementSystem.getSystem();

		UnitOfMeasure K = sys.getUOM(Unit.KELVIN);
		UnitOfMeasure C = sys.getUOM(Unit.CELSIUS);
		UnitOfMeasure R = sys.getUOM(Unit.RANKINE);
		UnitOfMeasure F = sys.getUOM(Unit.FAHRENHEIT);

		BigDecimal bd212 = Quantity.createAmount("212");
		BigDecimal oneHundred = Quantity.createAmount("100");

		Quantity q1 = new Quantity(bd212, F);
		Quantity q2 = q1.convert(C);
		assertThat(q2.getAmount(), closeTo(oneHundred, DELTA6));
		assertThat(q2.convert(F).getAmount(), closeTo(bd212, DELTA6));

		BigDecimal bd32 = Quantity.createAmount("32");
		q1 = new Quantity(bd32, F);
		q2 = q1.convert(C);
		assertThat(q2.getAmount(), closeTo(BigDecimal.ZERO, DELTA6));
		assertThat(q2.convert(F).getAmount(), closeTo(bd32, DELTA6));

		q1 = new Quantity(BigDecimal.ZERO, F);
		q2 = q1.convert(C);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("-17.7777777777778"), DELTA6));
		assertThat(q2.convert(F).getAmount(), closeTo(BigDecimal.ZERO, DELTA6));

		BigDecimal bd459 = Quantity.createAmount("459.67");
		q1 = new Quantity(bd459, R);
		q2 = q1.convert(F);
		assertThat(q2.getAmount(), closeTo(BigDecimal.ZERO, DELTA6));
		assertThat(q2.convert(R).getAmount(), closeTo(bd459, DELTA6));

		BigDecimal bd255 = Quantity.createAmount("255.3722222222222");
		q2 = q1.convert(K);
		assertThat(q2.getAmount(), closeTo(bd255, DELTA6));
		assertThat(q2.convert(R).getAmount(), closeTo(bd459, DELTA6));

		BigDecimal bd17 = Quantity.createAmount("-17.7777777777778");
		q2 = q1.convert(C);
		assertThat(q2.getAmount(), closeTo(bd17, DELTA6));
		assertThat(q2.convert(R).getAmount(), closeTo(bd459, DELTA6));

		BigDecimal bd273 = Quantity.createAmount("273.15");
		q1 = new Quantity(bd273, K);
		q2 = q1.convert(C);
		assertThat(q2.getAmount(), closeTo(BigDecimal.ZERO, DELTA6));
		assertThat(q2.convert(K).getAmount(), closeTo(bd273, DELTA6));

		q1 = new Quantity(BigDecimal.ZERO, K);
		q2 = q1.convert(R);
		assertThat(q2.getAmount(), closeTo(BigDecimal.ZERO, DELTA6));
		assertThat(q2.convert(K).getAmount(), closeTo(BigDecimal.ZERO, DELTA6));
	}

	@Test
	public void testLength() throws Exception {
		MeasurementSystem sys = MeasurementSystem.getSystem();

		UnitOfMeasure m = sys.getUOM(Unit.METRE);
		UnitOfMeasure cm = sys.getUOM(Prefix.CENTI, m);
		UnitOfMeasure m2 = sys.getUOM(Unit.SQUARE_METRE);

		final char squared = 0x00B2;
		String cmsym = "cm" + squared;
		UnitOfMeasure cm2 = sys.getUOM(cmsym);

		if (cm2 == null) {
			cm2 = sys.createPowerUOM(UnitType.AREA, "square centimetres", cmsym, "centimetres squared", cm, 2);
		}

		UnitOfMeasure ft = sys.getUOM(Unit.FOOT);
		UnitOfMeasure yd = sys.getUOM(Unit.YARD);
		UnitOfMeasure ft2 = sys.getUOM(Unit.SQUARE_FOOT);
		UnitOfMeasure in2 = sys.getUOM(Unit.SQUARE_INCH);

		BigDecimal oneHundred = Quantity.createAmount("100");

		Quantity q1 = new Quantity(BigDecimal.ONE, ft2);
		Quantity q2 = q1.convert(in2);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("144"), DELTA6));
		assertThat(q2.convert(ft2).getAmount(), closeTo(BigDecimal.ONE, DELTA6));

		q1 = new Quantity(BigDecimal.ONE, sys.getUOM(Unit.SQUARE_METRE));
		q2 = q1.convert(ft2);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("10.76391041670972"), DELTA6));
		assertThat(q2.convert(m2).getAmount(), closeTo(BigDecimal.ONE, DELTA6));

		BigDecimal bd = Quantity.createAmount("3");
		q1 = new Quantity(bd, ft);
		q2 = q1.convert(yd);
		assertThat(q2.getAmount(), closeTo(BigDecimal.ONE, DELTA6));
		assertThat(q2.convert(ft).getAmount(), closeTo(bd, DELTA6));

		bd = Quantity.createAmount("0.3048");
		q1 = new Quantity(BigDecimal.ONE, ft);
		q2 = q1.convert(m);
		assertThat(q2.getAmount(), closeTo(bd, DELTA6));
		assertThat(q2.convert(ft).getAmount(), closeTo(BigDecimal.ONE, DELTA6));

		bd = oneHundred;
		q1 = new Quantity(bd, cm);
		q2 = q1.convert(m);
		assertThat(q2.getAmount(), closeTo(BigDecimal.ONE, DELTA6));
		assertThat(q2.convert(cm).getAmount(), closeTo(bd, DELTA6));

		// add
		bd = Quantity.createAmount("50");
		q1 = new Quantity(bd, cm);
		q2 = new Quantity(bd, cm);
		Quantity q3 = q1.add(q2);
		assertThat(q3.getAmount(), closeTo(bd.add(bd), DELTA6));
		assertThat(q3.convert(m).getAmount(), closeTo(BigDecimal.ONE, DELTA6));

		Quantity q4 = q2.add(q1);
		assertThat(q4.getAmount(), closeTo(bd.add(bd), DELTA6));
		assertThat(q4.convert(m).getAmount(), closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(q3.equals(q4));

		// subtract
		q3 = q1.subtract(q2);
		assertThat(q3.getAmount(), closeTo(BigDecimal.ZERO, DELTA6));
		assertThat(q3.convert(m).getAmount(), closeTo(BigDecimal.ZERO, DELTA6));

		q4 = q2.subtract(q1);
		assertThat(q4.getAmount(), closeTo(BigDecimal.ZERO, DELTA6));
		assertThat(q4.convert(m).getAmount(), closeTo(BigDecimal.ZERO, DELTA6));
		assertTrue(q3.equals(q4));

		// multiply
		q3 = q1.multiply(q2);
		assertThat(q3.getAmount(), closeTo(Quantity.createAmount("2500"), DELTA6));

		q4 = q3.convert(cm2);
		assertThat(q4.getAmount(), closeTo(Quantity.createAmount("2500"), DELTA6));

		q4 = q3.convert(m2);
		assertThat(q4.getAmount(), closeTo(Quantity.createAmount("0.25"), DELTA6));

		// divide
		q4 = q3.divide(q1);
		assertTrue(q4.equals(q2));

	}

	@Test
	public void testUSQuantity() throws Exception {
		MeasurementSystem sys = MeasurementSystem.getSystem();

		UnitOfMeasure gal = sys.getUOM(Unit.US_GALLON);
		UnitOfMeasure in3 = sys.getUOM(Unit.CUBIC_INCH);
		UnitOfMeasure floz = sys.getUOM(Unit.US_FLUID_OUNCE);
		UnitOfMeasure qt = sys.getUOM(Unit.US_QUART);

		Quantity q1 = new Quantity(BigDecimal.TEN, gal);
		Quantity q2 = q1.convert(in3);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("2310"), DELTA6));
		assertTrue(q2.getUOM().equals(in3));

		q1 = new Quantity("128", floz);
		q2 = q1.convert(qt);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("4"), DELTA6));
		assertTrue(q2.getUOM().equals(qt));

		UnitOfMeasure ft = sys.getUOM(Unit.FOOT);
		UnitOfMeasure in = sys.getUOM(Unit.INCH);
		UnitOfMeasure mi = sys.getUOM(Unit.MILE);

		q1 = new Quantity(BigDecimal.TEN, ft);
		q2 = q1.convert(in);

		q1 = new Quantity(BigDecimal.ONE, mi);

		// British cup to US gallon
		q1 = new Quantity(BigDecimal.TEN, sys.getUOM(Unit.BR_CUP));
		q2 = q1.convert(sys.getUOM(Unit.US_GALLON));
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("0.6"), DELTA3));

		// US ton to British ton
		q1 = new Quantity(BigDecimal.TEN, sys.getUOM(Unit.US_TON));
		q2 = q1.convert(sys.getUOM(Unit.BR_TON));
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("8.928571428"), DELTA6));
	}

	@Test
	public void testSIQuantity() throws Exception {
		MeasurementSystem sys = MeasurementSystem.getSystem();

		BigDecimal ten = Quantity.createAmount("10");

		UnitOfMeasure litre = sys.getUOM(Unit.LITRE);
		UnitOfMeasure m3 = sys.getUOM(Unit.CUBIC_METRE);
		UnitOfMeasure m2 = sys.getUOM(Unit.SQUARE_METRE);
		UnitOfMeasure m = sys.getUOM(Unit.METRE);
		UnitOfMeasure cm = sys.getUOM(Prefix.CENTI, m);
		UnitOfMeasure mps = sys.getUOM(Unit.METRE_PER_SECOND);
		UnitOfMeasure secPerM = sys.createQuotientUOM(UnitType.CUSTOM, null, "s/m", null, sys.getSecond(), m);
		UnitOfMeasure oneOverM = sys.getUOM(Unit.DIOPTER);
		UnitOfMeasure fperm = sys.getUOM(Unit.FARAD_PER_METRE);

		Conversion conversion = new Conversion(Quantity.createAmount("100"), oneOverM);
		UnitOfMeasure oneOverCm = sys.createScalarUOM(UnitType.CUSTOM, null, "1/cm", null);
		oneOverCm.setConversion(conversion);

		Quantity q1 = new Quantity(ten, litre);
		Quantity q2 = q1.convert(m3);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("0.01"), DELTA6));
		assertTrue(q2.getUOM().equals(m3));

		q2 = q1.convert(litre);
		assertThat(q2.getAmount(), closeTo(ten, DELTA6));
		assertTrue(q2.getUOM().equals(litre));

		// add
		q1 = new Quantity("2", m);
		q2 = new Quantity("2", cm);
		Quantity q3 = q1.add(q2);

		assertThat(q3.getUOM().getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(q3.getUOM().getAbscissaUnit().equals(m));
		assertThat(q3.getUOM().getOffset(), closeTo(BigDecimal.ZERO, DELTA6));
		assertThat(q3.getAmount(), closeTo(Quantity.createAmount("2.02"), DELTA6));

		Quantity q4 = q3.convert(cm);
		assertThat(q4.getAmount(), closeTo(Quantity.createAmount("202"), DELTA6));
		assertTrue(q4.getUOM().equals(cm));

		// subtract
		q3 = q3.subtract(q1);
		assertThat(q3.getUOM().getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(q3.getUOM().getAbscissaUnit().equals(m));
		assertThat(q3.getAmount(), closeTo(Quantity.createAmount("0.02"), DELTA6));

		q4 = q3.convert(cm);
		assertThat(q4.getAmount(), closeTo(Quantity.createAmount("2"), DELTA6));
		assertTrue(q4.getUOM().equals(cm));

		// multiply
		q3 = q1.multiply(q2);
		assertThat(q3.getAmount(), closeTo(Quantity.createAmount("4"), DELTA6));
		UnitOfMeasure u = q3.getUOM();
		assertThat(u.getScalingFactor(), closeTo(Quantity.createAmount("0.01"), DELTA6));
		assertTrue(u.getBaseSymbol().equals(m2.getBaseSymbol()));

		q4 = q3.divide(q3);
		assertThat(q4.getAmount(), closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(q4.getUOM().equals(sys.getOne()));

		q4 = q3.divide(q1);
		assertTrue(q4.equals(q2));

		q4 = q3.convert(m2);
		assertThat(q4.getAmount(), closeTo(Quantity.createAmount("0.04"), DELTA6));
		assertTrue(q4.getUOM().equals(m2));

		// divide
		q3 = q3.divide(q2);
		assertThat(q3.getAmount(), closeTo(Quantity.createAmount("2.0"), DELTA6));
		assertTrue(q3.getUOM().equals(m));
		assertTrue(q3.equals(q1));

		q3 = q3.convert(m);
		assertThat(q3.getAmount(), closeTo(Quantity.createAmount("2.0"), DELTA6));

		q1 = new Quantity(BigDecimal.ZERO, litre);

		try {
			q2 = q1.divide(q1);
			fail("divide by zero)");
		} catch (Exception e) {
		}

		// invert
		q1 = new Quantity(BigDecimal.TEN, mps);
		q2 = q1.invert();
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("0.1"), DELTA6));
		assertTrue(q2.getUOM().equals(secPerM));

		q2 = q2.invert();
		assertThat(q2.getAmount(), closeTo(BigDecimal.TEN, DELTA6));
		assertTrue(q2.getUOM().equals(mps));

		q1 = new Quantity(BigDecimal.TEN, cm);
		q2 = q1.invert();
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("0.1"), DELTA6));
		u = q2.getUOM();
		assertTrue(u.equals(oneOverCm));

		q2 = q2.convert(m.invert());
		assertThat(q2.getAmount(), closeTo(BigDecimal.TEN, DELTA6));
		assertTrue(q2.getUOM().equals(oneOverM));

		assertNotNull(q2.toString());

		// Newton-metres divided by metres
		q1 = new Quantity(BigDecimal.TEN, sys.getUOM(Unit.NEWTON_METRE));
		q2 = new Quantity(BigDecimal.ONE, sys.getUOM(Unit.METRE));
		q3 = q1.divide(q2);
		assertThat(q3.getAmount(), closeTo(BigDecimal.TEN, DELTA6));
		assertTrue(q3.getUOM().equals(sys.getUOM(Unit.NEWTON)));

		// length multiplied by force
		q1 = new Quantity(BigDecimal.TEN, sys.getUOM(Unit.NEWTON));
		q2 = new Quantity(BigDecimal.ONE, sys.getUOM(Unit.METRE));
		q3 = q1.multiply(q2);
		assertThat(q3.getAmount(), closeTo(BigDecimal.TEN, DELTA6));
		UnitOfMeasure nm1 = q3.getUOM();
		UnitOfMeasure nm2 = sys.getUOM(Unit.NEWTON_METRE);
		assertTrue(nm1.getBaseSymbol().equals(nm2.getBaseSymbol()));
		q4 = q3.convert(sys.getUOM(Unit.JOULE));
		assertTrue(q4.getUOM().equals(sys.getUOM(Unit.JOULE)));
		
		// farads
		q1 = new Quantity(BigDecimal.TEN, fperm);
		q2 = new Quantity(BigDecimal.ONE, m);
		q3 = q1.multiply(q2);
		assertThat(q3.getAmount(), closeTo(BigDecimal.TEN, DELTA6));
		assertTrue(q3.getUOM().equals(sys.getUOM(Unit.FARAD)));
		
		// amps
		q1 = new Quantity(BigDecimal.TEN, sys.getUOM(Unit.AMPERE_PER_METRE));
		q2 = new Quantity(BigDecimal.ONE, m);
		q3 = q1.multiply(q2);
		assertThat(q3.getAmount(), closeTo(BigDecimal.TEN, DELTA6));
		assertTrue(q3.getUOM().equals(sys.getUOM(Unit.AMPERE)));
		
		// body mass index
		Quantity height = new Quantity("2", sys.getUOM(Unit.METRE));
		Quantity mass = new Quantity("100", sys.getUOM(Unit.KILOGRAM));
		Quantity bmi = mass.divide(height.multiply(height));
		assertThat(bmi.getAmount(), closeTo(Quantity.createAmount("25"), DELTA6));
	}
	
	@Test
	public void testPowers() throws Exception {
		MeasurementSystem sys = MeasurementSystem.getSystem();
		
		UnitOfMeasure m2 = sys.getUOM(Unit.SQUARE_METRE);
		UnitOfMeasure p2 = sys.createPowerUOM(UnitType.AREA, "m2^1", "m2^1", "square metres raised to power 1", m2, 1);
		UnitOfMeasure p4 = sys.createPowerUOM(UnitType.CUSTOM, "m2^2", "m2^2", "square metres raised to power 2", m2, 2);
		
		BigDecimal amount = Quantity.createAmount("10");
		
		Quantity q1 = new Quantity(amount, m2);
		Quantity q3 = new Quantity(amount, p4);
		
		Quantity q4 = q3.divide(q1);
		assertThat(q4.getAmount(), closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(q4.getUOM().getBaseUOM().equals(m2));
		
		Quantity q2 = q1.convert(p2);
		assertThat(q2.getAmount(), closeTo(amount, DELTA6));
		assertTrue(q2.getUOM().getBaseUOM().equals(m2));
	}

	@Test
	public void testSIUnits() throws Exception {

		MeasurementSystem sys = MeasurementSystem.getSystem();

		UnitOfMeasure newton = sys.getUOM(Unit.NEWTON);
		UnitOfMeasure metre = sys.getUOM(Unit.METRE);
		UnitOfMeasure m2 = sys.getUOM(Unit.SQUARE_METRE);
		UnitOfMeasure cm = sys.getUOM(Prefix.CENTI, metre);
		UnitOfMeasure mps = sys.getUOM(Unit.METRE_PER_SECOND);
		UnitOfMeasure joule = sys.getUOM(Unit.JOULE);
		UnitOfMeasure m3 = sys.getUOM(Unit.CUBIC_METRE);
		UnitOfMeasure farad = sys.getUOM(Unit.FARAD);
		UnitOfMeasure nm = sys.getUOM(Unit.NEWTON_METRE);
		UnitOfMeasure coulomb = sys.getUOM(Unit.COULOMB);
		UnitOfMeasure volt = sys.getUOM(Unit.VOLT);
		UnitOfMeasure watt = sys.getUOM(Unit.WATT);
		UnitOfMeasure cm2 = sys.createProductUOM(UnitType.AREA, "square centimetres", "cm" + (char) 0xB2, "", cm, cm);
		UnitOfMeasure cv = sys.createProductUOM(UnitType.ENERGY, "CxV", "C·V", "Coulomb times Volt", coulomb, volt);
		UnitOfMeasure ws = sys.createProductUOM(UnitType.ENERGY, "Wxs", "W·s", "Watt times second", watt,
				sys.getSecond());

		UnitOfMeasure ft3 = sys.getUOM(Unit.CUBIC_FOOT);

		BigDecimal oneHundred = Quantity.createAmount("100");

		assertTrue(nm.getBaseSymbol().equals(joule.getBaseSymbol()));
		assertTrue(cv.getBaseSymbol().equals(joule.getBaseSymbol()));
		assertTrue(ws.getBaseSymbol().equals(joule.getBaseSymbol()));

		Quantity q1 = new Quantity(BigDecimal.TEN, newton);
		Quantity q2 = new Quantity(BigDecimal.TEN, metre);
		Quantity q3 = q1.multiply(q2);
		assertThat(q3.getAmount(), closeTo(oneHundred, DELTA6));
		assertTrue(q3.getUOM().getBaseSymbol().equals(nm.getBaseSymbol()));
		assertThat(q3.getUOM().getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));
		assertThat(q3.getUOM().getOffset(), closeTo(BigDecimal.ZERO, DELTA6));

		q3 = q3.convert(joule);
		assertThat(q3.getAmount(), closeTo(oneHundred, DELTA6));
		assertTrue(q3.getUOM().equals(joule));
		assertThat(q3.getUOM().getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));

		q3 = q3.convert(nm);
		assertThat(q3.getAmount(), closeTo(oneHundred, DELTA6));
		assertTrue(q3.getUOM().equals(nm));
		assertThat(q3.getUOM().getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));

		BigDecimal bd1 = Quantity.createAmount("10000");

		q1 = new Quantity(oneHundred, cm);
		q2 = q1.convert(metre);
		assertThat(q2.getAmount(), closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(q2.getUOM().getEnumeration().equals(Unit.METRE));
		assertThat(q2.getUOM().getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));

		q2 = q2.convert(cm);
		assertThat(q2.getAmount(), closeTo(oneHundred, DELTA6));
		assertThat(q2.getUOM().getScalingFactor(), closeTo(Quantity.createAmount("0.01"), DELTA6));

		q2 = q1;
		q3 = q1.multiply(q2);
		assertThat(q3.getAmount(), closeTo(Quantity.createAmount("10000"), DELTA6));
		assertThat(q3.getUOM().getScalingFactor(), closeTo(Quantity.createAmount("0.0001"), DELTA6));
		assertThat(q3.getUOM().getOffset(), closeTo(BigDecimal.ZERO, DELTA6));

		Quantity q4 = q3.convert(m2);
		assertTrue(q4.getUOM().equals(m2));
		assertThat(q4.getAmount(), closeTo(BigDecimal.ONE, DELTA6));

		q3 = q3.convert(m2);
		assertThat(q3.getAmount(), closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(q3.getUOM().equals(m2));
		assertThat(q3.getUOM().getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));

		q3 = q3.convert(cm2);
		assertThat(q3.getAmount(), closeTo(bd1, DELTA6));
		assertTrue(q3.getUOM().equals(cm2));
		assertThat(q3.getUOM().getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));

		// power
		Quantity onem3 = new Quantity(BigDecimal.ONE, m3);
		String cm3sym = "cm" + (char) 0xB3;
		UnitOfMeasure cm3 = sys.createPowerUOM(UnitType.VOLUME, cm3sym, cm3sym, null, cm, 3);
		Quantity megcm3 = new Quantity("1E+06", cm3);

		Quantity qft3 = onem3.convert(ft3);
		assertThat(qft3.getAmount(), closeTo(Quantity.createAmount("35.31466672148859"), DELTA6));

		Quantity qtym3 = qft3.convert(m3);
		assertThat(qtym3.getAmount(), closeTo(BigDecimal.ONE, DELTA6));

		Quantity qm3 = megcm3.convert(m3);
		assertThat(qm3.getAmount(), closeTo(BigDecimal.ONE, DELTA6));
		qm3 = qm3.convert(cm3);
		assertThat(qm3.getAmount(), closeTo(Quantity.createAmount("1E+06"), DELTA6));

		Quantity qcm3 = onem3.convert(cm3);
		assertThat(qcm3.getAmount(), closeTo(Quantity.createAmount("1E+06"), DELTA6));

		// inversions
		UnitOfMeasure u = metre.invert();
		assertTrue(u.getSymbol().equals(sys.getUOM(Unit.DIOPTER).getSymbol()));

		u = mps.invert();
		assertTrue(u.getSymbol().equals("s/m"));

		UnitOfMeasure uom = sys.createQuotientUOM(UnitType.CUSTOM, "1/F", "1/F", "one over farad", sys.getOne(), farad);
		assertTrue(uom.getSymbol().equals("1/F"));

		// hz to radians per sec
		q1 = new Quantity(BigDecimal.TEN, sys.getUOM(Unit.HERTZ));
		q2 = q1.convert(sys.getUOM(Unit.RAD_PER_SEC));
		BigDecimal twentyPi = new BigDecimal("20").multiply(new BigDecimal(Math.PI), UnitOfMeasure.MATH_CONTEXT);
		assertThat(q2.getAmount(), closeTo(twentyPi, DELTA6));

		q3 = q2.convert(sys.getUOM(Unit.HERTZ));
		assertThat(q3.getAmount(), closeTo(BigDecimal.TEN, DELTA6));

		// rpm to radians per second
		q1 = new Quantity(BigDecimal.TEN, sys.getUOM(Unit.REV_PER_MIN));
		q2 = q1.convert(sys.getUOM(Unit.RAD_PER_SEC));
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("1.04719755119"), DELTA6));

		q3 = q2.convert(sys.getUOM(Unit.REV_PER_MIN));
		assertThat(q3.getAmount(), closeTo(BigDecimal.TEN, DELTA6));
	}

	@Test
	public void testPackaging() throws Exception {
		MeasurementSystem sys = MeasurementSystem.getSystem();
		BigDecimal one = Quantity.createAmount("1");
		BigDecimal four = Quantity.createAmount("4");
		BigDecimal six = Quantity.createAmount("6");
		BigDecimal ten = Quantity.createAmount("10");
		BigDecimal forty = Quantity.createAmount("40");

		UnitOfMeasure one16ozCan = sys.createScalarUOM(UnitType.VOLUME, "16 oz can", "16ozCan", "16 oz can");
		one16ozCan.setConversion(new Conversion(Quantity.createAmount("16"), sys.getUOM(Unit.US_FLUID_OUNCE)));

		Quantity q400 = new Quantity("400", one16ozCan);
		Quantity q50 = q400.convert(sys.getUOM(Unit.US_GALLON));
		assertThat(q50.getAmount(), closeTo(Quantity.createAmount("50"), DELTA6));

		UnitOfMeasure one12ozCan = sys.createScalarUOM(UnitType.VOLUME, "12 oz can", "12ozCan", "12 oz can");
		one12ozCan.setConversion(new Conversion(Quantity.createAmount("12"), sys.getUOM(Unit.US_FLUID_OUNCE)));

		Quantity q48 = new Quantity("48", one12ozCan);
		Quantity q36 = q48.convert(one16ozCan);
		assertThat(q36.getAmount(), closeTo(Quantity.createAmount("36"), DELTA6));

		Conversion conversion = new Conversion(six, one12ozCan);
		UnitOfMeasure sixPackCan = sys.createScalarUOM(UnitType.VOLUME, "6-pack", "6PCan", "6-pack of 12 oz cans");
		sixPackCan.setConversion(conversion);

		conversion = new Conversion(four, sixPackCan);
		UnitOfMeasure fourPackCase = sys.createScalarUOM(UnitType.VOLUME, "4 pack case", "4PCase", "case of 4 6-packs");
		fourPackCase.setConversion(conversion);

		BigDecimal bd = fourPackCase.getConversionFactor(one12ozCan);
		assertThat(bd, closeTo(Quantity.createAmount("24"), DELTA6));

		bd = one12ozCan.getConversionFactor(fourPackCase);

		bd = fourPackCase.getConversionFactor(sixPackCan);
		bd = sixPackCan.getConversionFactor(fourPackCase);

		bd = sixPackCan.getConversionFactor(one12ozCan);
		bd = one12ozCan.getConversionFactor(sixPackCan);

		Quantity tenCases = new Quantity(ten, fourPackCase);

		Quantity q1 = tenCases.convert(one12ozCan);
		assertThat(q1.getAmount(), closeTo(Quantity.createAmount("240"), DELTA6));

		Quantity q2 = q1.convert(fourPackCase);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("10"), DELTA6));

		Quantity fortyPacks = new Quantity(forty, sixPackCan);
		q2 = fortyPacks.convert(one12ozCan);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("240"), DELTA6));

		Quantity oneCan = new Quantity(one, one12ozCan);
		q2 = oneCan.convert(sixPackCan);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("0.1666666666666667"), DELTA6));
	}

	@Test
	public void testGenericQuantity() throws Exception {
		MeasurementSystem sys = MeasurementSystem.getSystem();

		UnitOfMeasure a = sys.createScalarUOM(UnitType.CUSTOM, "a", "a", "A");

		Conversion conversion = new Conversion(BigDecimal.TEN, a);
		UnitOfMeasure b = sys.createScalarUOM(UnitType.CUSTOM, "b", "b", "B");
		b.setConversion(conversion);

		BigDecimal two = Quantity.multiplyAmounts("2", "2");

		// add
		Quantity q1 = new Quantity(two, a);

		assertFalse(q1.equals(null));

		Quantity q2 = new Quantity(two, b);
		Quantity q3 = q1.add(q2);

		assertThat(q3.getUOM().getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(q3.getUOM().getAbscissaUnit().equals(a));
		assertThat(q3.getUOM().getOffset(), closeTo(BigDecimal.ZERO, DELTA6));
		assertThat(q3.getAmount(), closeTo(Quantity.createAmount("44"), DELTA6));

		// subtract
		q3 = q1.subtract(q2);
		assertThat(q3.getUOM().getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(q3.getUOM().getAbscissaUnit().equals(a));
		assertThat(q3.getUOM().getOffset(), closeTo(BigDecimal.ZERO, DELTA6));
		assertThat(q3.getAmount(), closeTo(Quantity.createAmount("-36"), DELTA6));

		// multiply
		q3 = q1.multiply(q2);
		assertThat(q3.getAmount(), closeTo(Quantity.createAmount("16"), DELTA6));
		assertThat(q3.getUOM().getScalingFactor(), closeTo(BigDecimal.TEN, DELTA6));
		assertThat(q3.getUOM().getOffset(), closeTo(BigDecimal.ZERO, DELTA6));

		Quantity q4 = q3.divide(q2);
		assertThat(q4.getAmount(), closeTo(Quantity.createAmount("4"), DELTA6));
		q4 = q4.convert(b);
		assertTrue(q4.equals(q2));

		// divide
		q3 = q1.divide(q2);
		assertThat(q3.getAmount(), closeTo(BigDecimal.ONE, DELTA6));
		assertThat(q3.getUOM().getScalingFactor(), closeTo(Quantity.createAmount("0.1"), DELTA6));

		q4 = q3.multiply(q2);
		assertTrue(q4.equals(q1));
	}

	@Test
	public void testExceptions() throws Exception {

		MeasurementSystem sys = MeasurementSystem.getSystem();
		UnitOfMeasure floz = sys.getUOM(Unit.BR_FLUID_OUNCE);

		Quantity q1 = new Quantity(BigDecimal.TEN, sys.getDay());
		Quantity q2 = new Quantity(BigDecimal.TEN, sys.getUOM(Unit.BR_FLUID_OUNCE));

		try {
			q1.convert(floz);
			fail("convert");
		} catch (Exception e) {
		}

		try {
			q1.add(q2);
			fail("add");
		} catch (Exception e) {
		}

		try {
			q1.subtract(q2);
			fail("subtract");
		} catch (Exception e) {
		}

		// OK
		q1.multiply(q2);

		// OK
		q1.divide(q2);
	}

	@Test
	public void testEquality() throws Exception {
		MeasurementSystem sys = MeasurementSystem.getSystem();

		UnitOfMeasure newton = sys.getUOM(Unit.NEWTON);
		UnitOfMeasure metre = sys.getUOM(Unit.METRE);
		UnitOfMeasure nm = sys.getUOM(Unit.NEWTON_METRE);
		UnitOfMeasure m2 = sys.getUOM(Unit.SQUARE_METRE);
		UnitOfMeasure J = sys.getUOM(Unit.JOULE);
		BigDecimal amount = Quantity.createAmount("10");

		final Quantity q1 = new Quantity(amount, newton);
		final Quantity q2 = new Quantity(amount, metre);
		final Quantity q3 = new Quantity(amount, nm);
		Quantity q5 = new Quantity(Quantity.createAmount("100"), nm);

		// unity
		Quantity q4 = q5.divide(q3);
		assertTrue(q4.getUOM().getSymbol().equals(sys.getOne().getSymbol()));
		assertTrue(q4.getAmount().equals(amount));

		// Newton-metre (Joules)
		q4 = q1.multiply(q2);
		assertTrue(q5.getUOM().getBaseSymbol().equals(q4.getUOM().getBaseSymbol()));
		Quantity q6 = q5.convert(J);
		assertTrue(q6.getAmount().equals(q4.getAmount()));

		// Newton
		q5 = q4.divide(q2);
		assertTrue(q5.getUOM().getSymbol().equals(q1.getUOM().getSymbol()));
		assertTrue(q5.equals(q1));

		// metre
		q5 = q4.divide(q1);
		assertTrue(q5.getUOM().getSymbol().equals(q2.getUOM().getSymbol()));
		assertTrue(q5.equals(q2));

		// square metre
		q4 = q2.multiply(q2);
		q5 = new Quantity(Quantity.createAmount("100"), m2);
		assertTrue(q4.getUOM().getSymbol().equals(q5.getUOM().getSymbol()));
		assertTrue(q5.equals(q4));

		// metre
		q4 = q5.divide(q2);
		assertTrue(q4.getUOM().getSymbol().equals(q2.getUOM().getSymbol()));
		assertTrue(q4.equals(q2));

	}

	@Test
	public void testComparison() throws Exception {
		MeasurementSystem sys = MeasurementSystem.getSystem();

		UnitOfMeasure newton = sys.getUOM(Unit.NEWTON);
		UnitOfMeasure metre = sys.getUOM(Unit.METRE);
		UnitOfMeasure cm = sys.getUOM(Prefix.CENTI, metre);

		BigDecimal amount = Quantity.createAmount("10");

		final Quantity qN = new Quantity(amount, newton);
		final Quantity qm10 = new Quantity(amount, metre);
		final Quantity qm1 = new Quantity(BigDecimal.ONE, metre);
		final Quantity qcm = new Quantity(amount, cm);

		assertTrue(qN.compare(qN) == 0);
		assertTrue(qm10.compare(qm1) == 1);
		assertTrue(qm1.compare(qm10) == -1);
		assertTrue(qcm.compare(qm1) == -1);
		assertTrue(qm1.compare(qcm) == 1);

		try {
			qN.compare(qm10);
			fail("not comparable)");
		} catch (Exception e) {
		}

		Quantity acidpH = new Quantity("4.5", sys.getUOM(Unit.PH));
		Quantity neutralpH = new Quantity("7.0", sys.getUOM(Unit.PH));
		assertTrue(acidpH.compare(neutralpH) == -1);
	}
	
	@Test
	public void testArithmetic() throws Exception {
		MeasurementSystem sys = MeasurementSystem.getSystem();
		UnitOfMeasure in = sys.getUOM(Unit.INCH);
		UnitOfMeasure cm = sys.getUOM(Prefix.CENTI, sys.getUOM(Unit.METRE));
		Quantity qcm = new Quantity("1", cm);
		Quantity qin = new Quantity("1", in);
		BigDecimal bd = Quantity.createAmount("2.54");
		Quantity q1 = qcm.multiply(bd).convert(in);
		assertTrue(q1.equals(qin)); 
		Quantity q2 = q1.convert(cm);
		assertThat(q2.getAmount(), closeTo(bd, DELTA6));
		
	}
}
