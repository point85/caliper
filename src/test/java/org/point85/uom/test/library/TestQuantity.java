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
package org.point85.uom.test.library;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;
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

public class TestQuantity extends BaseTest {

	@Test
	public void testNamedQuantity() throws Exception {

		Quantity q = new Quantity(10d, Unit.CELSIUS);
		assertTrue(q.toString() != null);

		// faraday
		Quantity f = sys.getQuantity(Constant.FARADAY_CONSTANT);
		Quantity qe = sys.getQuantity(Constant.ELEMENTARY_CHARGE);
		Quantity na = sys.getQuantity(Constant.AVAGADRO_CONSTANT);
		Quantity eNA = qe.multiply(na);
		assertTrue(isCloseTo(f.getAmount(), eNA.getAmount(), DELTA6));
		assertTrue(isCloseTo(f.getAmount(), 96485.332123, DELTA5));

		// epsilon 0
		UnitOfMeasure fm = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "Farad per metre", "F/m", "Farad per metre",
				sys.getUOM(Unit.FARAD), sys.getUOM(Unit.METRE));
		Quantity eps0 = sys.getQuantity(Constant.ELECTRIC_PERMITTIVITY);
		assertTrue(isCloseTo(eps0.getAmount(), 8.854187817E-12, DELTA6));
		assertTrue(isCloseTo(eps0.convert(fm).getAmount(), 8.854187817E-12, DELTA6));

		// atomic masses
		Quantity u = new Quantity(1.66053904020E-24, sys.getUOM(Unit.GRAM));
		Quantity me = sys.getQuantity(Constant.ELECTRON_MASS);
		double bd = me.divide(u).getAmount();
		assertTrue(isCloseTo(bd, 5.48579909016E-04, DELTA6));

		Quantity mp = sys.getQuantity(Constant.PROTON_MASS);
		bd = mp.divide(u).getAmount();
		assertTrue(isCloseTo(bd, 1.00727646687991, DELTA6));

		// caesium
		Quantity cs = sys.getQuantity(Constant.CAESIUM_FREQUENCY);
		Quantity periods = cs.multiply(new Quantity(1, Unit.SECOND));
		assertTrue(isCloseTo(periods.getAmount(), 9192631770d, DELTA0));

		// luminous efficacy
		Quantity kcd = sys.getQuantity(Constant.LUMINOUS_EFFICACY);
		Quantity lum = kcd.multiply(new Quantity(1, Unit.WATT));
		assertTrue(isCloseTo(lum.getAmount(), 683d, DELTA0));
	}

	@Test
	public void testAllUnits() throws Exception {

		for (Unit u : Unit.values()) {
			UnitOfMeasure uom1 = sys.getUOM(u);
			UnitOfMeasure uom2 = sys.getUOM(u);
			assertTrue(uom1.equals(uom2));

			Quantity q1 = new Quantity(10d, uom1);
			Quantity q2 = q1.convert(uom2);
			assertTrue(q1.equals(q2));
		}
	}

	@Test
	public void testTime() throws Exception {

		UnitOfMeasure second = sys.getSecond();
		UnitOfMeasure minute = sys.getMinute();

		Quantity oneMin = new Quantity(1d, minute);
		Quantity oneSec = new Quantity(1d, second);
		Quantity converted = oneMin.convert(second);
		double bd60 = 60;

		assertTrue(isCloseTo(converted.getAmount(), bd60, DELTA6));
		assertTrue(converted.getUOM().equals(second));

		Quantity sixty = oneMin.divide(oneSec);
		assertTrue(isCloseTo(sixty.getAmount(), 1d, DELTA6));
		assertTrue(isCloseTo(sixty.getUOM().getScalingFactor(), bd60, DELTA6));

		Quantity q1 = sixty.convert(sys.getOne());
		assertTrue(q1.getUOM().equals(sys.getOne()));
		assertTrue(isCloseTo(q1.getAmount(), bd60, DELTA6));

		q1 = q1.multiply(oneSec);
		assertTrue(q1.convert(second).getUOM().equals(second));
		assertTrue(isCloseTo(q1.getAmount(), bd60, DELTA6));

		q1 = q1.convert(minute);
		assertTrue(q1.getUOM().equals(minute));
		assertTrue(isCloseTo(q1.getAmount(), 1d, DELTA6));

		assertTrue(q1.hashCode() != 0);

	}

	@Test
	public void testTemperature() throws Exception {

		UnitOfMeasure K = sys.getUOM(Unit.KELVIN);
		UnitOfMeasure C = sys.getUOM(Unit.CELSIUS);
		UnitOfMeasure R = sys.getUOM(Unit.RANKINE);
		UnitOfMeasure F = sys.getUOM(Unit.FAHRENHEIT);

		double bd212 = 212;
		double oneHundred = 100;

		Quantity q1 = new Quantity(bd212, F);
		Quantity q2 = q1.convert(C);
		assertTrue(isCloseTo(q2.getAmount(), oneHundred, DELTA6));
		assertTrue(isCloseTo(q2.convert(F).getAmount(), bd212, DELTA6));

		double bd32 = 32;
		q1 = new Quantity(bd32, F);
		q2 = q1.convert(C);
		assertTrue(isCloseTo(q2.getAmount(), 0d, DELTA6));
		assertTrue(isCloseTo(q2.convert(F).getAmount(), bd32, DELTA6));

		q1 = new Quantity(0d, F);
		q2 = q1.convert(C);
		assertTrue(isCloseTo(q2.getAmount(), -17.7777777777778, DELTA6));
		assertTrue(isCloseTo(q2.convert(F).getAmount(), 0d, DELTA6));

		double bd459 = 459.67;
		q1 = new Quantity(bd459, R);
		q2 = q1.convert(F);
		assertTrue(isCloseTo(q2.getAmount(), 0d, DELTA6));
		assertTrue(isCloseTo(q2.convert(R).getAmount(), bd459, DELTA6));

		double bd255 = 255.3722222222222;
		q2 = q1.convert(K);
		assertTrue(isCloseTo(q2.getAmount(), bd255, DELTA6));
		assertTrue(isCloseTo(q2.convert(R).getAmount(), bd459, DELTA6));

		double bd17 = -17.7777777777778;
		q2 = q1.convert(C);
		assertTrue(isCloseTo(q2.getAmount(), bd17, DELTA6));
		assertTrue(isCloseTo(q2.convert(R).getAmount(), bd459, DELTA6));

		double bd273 = 273.15;
		q1 = new Quantity(bd273, K);
		q2 = q1.convert(C);
		assertTrue(isCloseTo(q2.getAmount(), 0d, DELTA6));
		assertTrue(isCloseTo(q2.convert(K).getAmount(), bd273, DELTA6));

		q1 = new Quantity(0d, K);
		q2 = q1.convert(R);
		assertTrue(isCloseTo(q2.getAmount(), 0d, DELTA6));
		assertTrue(isCloseTo(q2.convert(K).getAmount(), 0d, DELTA6));
	}

	@Test
	public void testLength() throws Exception {

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

		double oneHundred = 100;

		Quantity q1 = new Quantity(1d, ft2);
		Quantity q2 = q1.convert(in2);
		assertTrue(isCloseTo(q2.getAmount(), 144, DELTA6));
		assertTrue(isCloseTo(q2.convert(ft2).getAmount(), 1d, DELTA6));

		q1 = new Quantity(1d, sys.getUOM(Unit.SQUARE_METRE));
		q2 = q1.convert(ft2);
		assertTrue(isCloseTo(q2.getAmount(), 10.76391041670972, DELTA6));
		assertTrue(isCloseTo(q2.convert(m2).getAmount(), 1d, DELTA6));

		double bd = 3;
		q1 = new Quantity(bd, ft);
		q2 = q1.convert(yd);
		assertTrue(isCloseTo(q2.getAmount(), 1d, DELTA6));
		assertTrue(isCloseTo(q2.convert(ft).getAmount(), bd, DELTA6));

		bd = 0.3048;
		q1 = new Quantity(1d, ft);
		q2 = q1.convert(m);
		assertTrue(isCloseTo(q2.getAmount(), bd, DELTA6));
		assertTrue(isCloseTo(q2.convert(ft).getAmount(), 1d, DELTA6));

		bd = oneHundred;
		q1 = new Quantity(bd, cm);
		q2 = q1.convert(m);
		assertTrue(isCloseTo(q2.getAmount(), 1d, DELTA6));
		assertTrue(isCloseTo(q2.convert(cm).getAmount(), bd, DELTA6));

		// add
		bd = 50;
		q1 = new Quantity(bd, cm);
		q2 = new Quantity(bd, cm);
		Quantity q3 = q1.add(q2);
		assertTrue(isCloseTo(q3.getAmount(), 100d, DELTA6));
		assertTrue(isCloseTo(q3.convert(m).getAmount(), 1d, DELTA6));

		Quantity q4 = q2.add(q1);
		assertTrue(isCloseTo(q4.getAmount(), 100d, DELTA6));
		assertTrue(isCloseTo(q4.convert(m).getAmount(), 1d, DELTA6));
		assertTrue(q3.equals(q4));

		// subtract
		q3 = q1.subtract(q2);
		assertTrue(isCloseTo(q3.getAmount(), 0d, DELTA6));
		assertTrue(isCloseTo(q3.convert(m).getAmount(), 0d, DELTA6));

		q4 = q2.subtract(q1);
		assertTrue(isCloseTo(q4.getAmount(), 0d, DELTA6));
		assertTrue(isCloseTo(q4.convert(m).getAmount(), 0d, DELTA6));
		assertTrue(q3.equals(q4));

		// multiply
		q3 = q1.multiply(q2);
		assertTrue(isCloseTo(q3.getAmount(), 2500, DELTA6));

		q4 = q3.convert(cm2);
		assertTrue(isCloseTo(q4.getAmount(), 2500, DELTA6));

		q4 = q3.convert(m2);
		assertTrue(isCloseTo(q4.getAmount(), 0.25, DELTA6));

		// divide
		q4 = q3.divide(q1);
		assertTrue(q4.equals(q2));

	}

	@Test
	public void testUSQuantity() throws Exception {

		UnitOfMeasure gal = sys.getUOM(Unit.US_GALLON);
		UnitOfMeasure in3 = sys.getUOM(Unit.CUBIC_INCH);
		UnitOfMeasure floz = sys.getUOM(Unit.US_FLUID_OUNCE);
		UnitOfMeasure qt = sys.getUOM(Unit.US_QUART);

		Quantity q1 = new Quantity(10d, gal);
		Quantity q2 = q1.convert(in3);
		assertTrue(isCloseTo(q2.getAmount(), 2310, DELTA6));
		assertTrue(q2.getUOM().equals(in3));

		q1 = new Quantity(128d, floz);
		q2 = q1.convert(qt);
		assertTrue(isCloseTo(q2.getAmount(), 4, DELTA6));
		assertTrue(q2.getUOM().equals(qt));

		UnitOfMeasure ft = sys.getUOM(Unit.FOOT);
		UnitOfMeasure in = sys.getUOM(Unit.INCH);
		UnitOfMeasure mi = sys.getUOM(Unit.MILE);

		q1 = new Quantity(10d, ft);
		q2 = q1.convert(in);

		q1 = new Quantity(1d, mi);

		// British cup to US gallon
		q1 = new Quantity(10d, sys.getUOM(Unit.BR_CUP));
		q2 = q1.convert(sys.getUOM(Unit.US_GALLON));
		assertTrue(isCloseTo(q2.getAmount(), 0.6, DELTA3));

		// US ton to British ton
		q1 = new Quantity(10d, sys.getUOM(Unit.US_TON));
		q2 = q1.convert(sys.getUOM(Unit.BR_TON));
		assertTrue(isCloseTo(q2.getAmount(), 8.928571428, DELTA6));

		// troy ounce to ounce
		q1 = new Quantity(10d, Unit.TROY_OUNCE);
		assertTrue(isCloseTo(q1.convert(Unit.OUNCE).getAmount(), 10.971, DELTA3));

		// deci-litre to quart
		q1 = new Quantity(10d, Prefix.DECI, Unit.LITRE);
		q2 = q1.convert(Unit.US_QUART);
		assertTrue(isCloseTo(q2.getAmount(), 1.0566882, DELTA6));
	}

	@Test
	public void testSIQuantity() throws Exception {

		double ten = 10;

		UnitOfMeasure litre = sys.getUOM(Unit.LITRE);
		UnitOfMeasure m3 = sys.getUOM(Unit.CUBIC_METRE);
		UnitOfMeasure m2 = sys.getUOM(Unit.SQUARE_METRE);
		UnitOfMeasure m = sys.getUOM(Unit.METRE);
		UnitOfMeasure cm = sys.getUOM(Prefix.CENTI, m);
		UnitOfMeasure mps = sys.getUOM(Unit.METRE_PER_SEC);
		UnitOfMeasure secPerM = sys.createQuotientUOM(UnitType.UNCLASSIFIED, null, "s/m", null, sys.getSecond(), m);
		UnitOfMeasure oneOverM = sys.getUOM(Unit.DIOPTER);
		UnitOfMeasure fperm = sys.getUOM(Unit.FARAD_PER_METRE);

		UnitOfMeasure oneOverCm = sys.createScalarUOM(UnitType.UNCLASSIFIED, null, "1/cm", null);
		oneOverCm.setConversion(100d, oneOverM);

		Quantity q1 = new Quantity(ten, litre);
		Quantity q2 = q1.convert(m3);
		assertTrue(isCloseTo(q2.getAmount(), 0.01, DELTA6));
		assertTrue(q2.getUOM().equals(m3));

		q2 = q1.convert(litre);
		assertTrue(isCloseTo(q2.getAmount(), ten, DELTA6));
		assertTrue(q2.getUOM().equals(litre));

		// add
		q1 = new Quantity(2d, m);
		q2 = new Quantity(2d, cm);
		Quantity q3 = q1.add(q2);

		assertTrue(isCloseTo(q3.getUOM().getScalingFactor(), 1d, DELTA6));
		assertTrue(q3.getUOM().getAbscissaUnit().equals(m));
		assertTrue(isCloseTo(q3.getUOM().getOffset(), 0d, DELTA6));
		assertTrue(isCloseTo(q3.getAmount(), 2.02, DELTA6));

		Quantity q4 = q3.convert(cm);
		assertTrue(isCloseTo(q4.getAmount(), 202, DELTA6));
		assertTrue(q4.getUOM().equals(cm));

		// subtract
		q3 = q3.subtract(q1);
		assertTrue(isCloseTo(q3.getUOM().getScalingFactor(), 1d, DELTA6));
		assertTrue(q3.getUOM().getAbscissaUnit().equals(m));
		assertTrue(isCloseTo(q3.getAmount(), 0.02, DELTA6));

		q4 = q3.convert(cm);
		assertTrue(isCloseTo(q4.getAmount(), 2, DELTA6));
		assertTrue(q4.getUOM().equals(cm));

		// multiply
		q3 = q1.multiply(q2);
		assertTrue(isCloseTo(q3.getAmount(), 4, DELTA6));
		UnitOfMeasure u = q3.getUOM();
		assertTrue(isCloseTo(u.getScalingFactor(), 0.01, DELTA6));
		assertTrue(u.getBaseSymbol().equals(m2.getBaseSymbol()));

		q4 = q3.divide(q3);
		assertTrue(isCloseTo(q4.getAmount(), 1d, DELTA6));
		assertTrue(q4.getUOM().equals(sys.getOne()));

		q4 = q3.divide(q1);
		assertTrue(q4.equals(q2));

		q4 = q3.convert(m2);
		assertTrue(isCloseTo(q4.getAmount(), 0.04, DELTA6));
		assertTrue(q4.getUOM().equals(m2));

		// divide
		q3 = q3.divide(q2);
		assertTrue(isCloseTo(q3.getAmount(), 2.0, DELTA6));
		assertTrue(q3.getUOM().equals(m));
		assertTrue(q3.equals(q1));

		q3 = q3.convert(m);
		assertTrue(isCloseTo(q3.getAmount(), 2.0, DELTA6));

		q1 = new Quantity(0d, litre);

		try {
			q2 = q1.divide(q1);
			fail("divide by zero)");
		} catch (Exception e) {
		}

		q1 = q3.convert(cm).divide(ten);
		assertTrue(isCloseTo(q1.getAmount(), 20, DELTA6));

		// invert
		q1 = new Quantity(10d, mps);
		q2 = q1.invert();
		assertTrue(isCloseTo(q2.getAmount(), 0.1, DELTA6));
		assertTrue(q2.getUOM().equals(secPerM));

		q2 = q2.invert();
		assertTrue(isCloseTo(q2.getAmount(), 10d, DELTA6));
		assertTrue(q2.getUOM().equals(mps));

		q1 = new Quantity(10d, cm);
		q2 = q1.invert();
		assertTrue(isCloseTo(q2.getAmount(), 0.1, DELTA6));
		u = q2.getUOM();
		assertTrue(u.equals(oneOverCm));

		q2 = q2.convert(m.invert());
		assertTrue(isCloseTo(q2.getAmount(), 10d, DELTA6));
		assertTrue(q2.getUOM().equals(oneOverM));

		assertTrue(q2.toString() != null);

		// Newton-metres divided by metres
		q1 = new Quantity(10d, sys.getUOM(Unit.NEWTON_METRE));
		q2 = new Quantity(1d, sys.getUOM(Unit.METRE));
		q3 = q1.divide(q2);
		assertTrue(isCloseTo(q3.getAmount(), 10d, DELTA6));
		assertTrue(q3.getUOM().equals(sys.getUOM(Unit.NEWTON)));

		// length multiplied by force
		q1 = new Quantity(10d, sys.getUOM(Unit.NEWTON));
		q2 = new Quantity(1d, sys.getUOM(Unit.METRE));
		q3 = q1.multiply(q2);
		assertTrue(isCloseTo(q3.getAmount(), 10d, DELTA6));
		UnitOfMeasure nm1 = q3.getUOM();
		UnitOfMeasure nm2 = sys.getUOM(Unit.NEWTON_METRE);
		assertTrue(nm1.getBaseSymbol().equals(nm2.getBaseSymbol()));
		q4 = q3.convert(sys.getUOM(Unit.JOULE));
		assertTrue(q4.getUOM().equals(sys.getUOM(Unit.JOULE)));

		// farads
		q1 = new Quantity(10d, fperm);
		q2 = new Quantity(1d, m);
		q3 = q1.multiply(q2);
		assertTrue(isCloseTo(q3.getAmount(), 10d, DELTA6));
		assertTrue(q3.getUOM().equals(sys.getUOM(Unit.FARAD)));

		// amps
		q1 = new Quantity(10d, sys.getUOM(Unit.AMPERE_PER_METRE));
		q2 = new Quantity(1d, m);
		q3 = q1.multiply(q2);
		assertTrue(isCloseTo(q3.getAmount(), 10d, DELTA6));
		assertTrue(q3.getUOM().equals(sys.getUOM(Unit.AMPERE)));

		// Boltzmann and Avogadro
		Quantity boltzmann = sys.getQuantity(Constant.BOLTZMANN_CONSTANT);
		Quantity avogadro = sys.getQuantity(Constant.AVAGADRO_CONSTANT);
		Quantity gas = sys.getQuantity(Constant.GAS_CONSTANT);
		Quantity qR = boltzmann.multiply(avogadro);
		assertTrue(isCloseTo(qR.getUOM().getScalingFactor(), gas.getUOM().getScalingFactor(), DELTA6));

		// Sieverts
		q1 = new Quantity(20d, sys.getUOM(Prefix.MILLI, Unit.SIEVERTS_PER_HOUR));
		q2 = new Quantity(24d, sys.getHour());
		q3 = q1.multiply(q2);
		assertTrue(isCloseTo(q3.getAmount(), 480, DELTA6));

		// If the concentration of a sulfuric acid solution is c(H2SO4) = 1 mol/L and
		// the equivalence factor is 0.5, what is the normality?
		UnitOfMeasure mol = sys.getUOM(Unit.MOLE);
		UnitOfMeasure molPerL = sys.createQuotientUOM(UnitType.MOLAR_CONCENTRATION, "moler conc", "mol/L",
				"mole per litre", mol, litre);

		Quantity feq = new Quantity(0.5, molPerL);

		Quantity N = new Quantity(1, molPerL).divide(feq);
		assertTrue(isCloseTo(N.getAmount(), 2.0, DELTA6));
	}

	@Test
	public void testPowers() throws Exception {

		UnitOfMeasure m2 = sys.getUOM(Unit.SQUARE_METRE);
		UnitOfMeasure p2 = sys.createPowerUOM(UnitType.AREA, "m2^1", "m2^1", "square metres raised to power 1", m2, 1);
		UnitOfMeasure p4 = sys.createPowerUOM(UnitType.UNCLASSIFIED, "m2^2", "m2^2", "square metres raised to power 2",
				m2, 2);

		double amount = 10d;

		Quantity q1 = new Quantity(amount, m2);
		Quantity q3 = new Quantity(amount, p4);

		Quantity q4 = q3.divide(q1);
		assertTrue(isCloseTo(q4.getAmount(), 1d, DELTA6));
		assertTrue(q4.getUOM().getBaseUOM().equals(m2));

		Quantity q2 = q1.convert(p2);
		assertTrue(isCloseTo(q2.getAmount(), amount, DELTA6));
		assertTrue(q2.getUOM().getBaseUOM().equals(m2));

		// power method
		UnitOfMeasure ft = sys.getUOM(Unit.FOOT);
		UnitOfMeasure ft2 = sys.getUOM(Unit.SQUARE_FOOT);
		q1 = new Quantity(10d, ft);

		q3 = q1.power(2);
		assertTrue(isCloseTo(q3.getAmount(), 100, DELTA6));
		assertTrue(q3.getUOM().getBaseSymbol().equals(ft2.getBaseSymbol()));

		q4 = q3.convert(sys.getUOM(Unit.SQUARE_METRE));
		assertTrue(isCloseTo(q4.getAmount(), 9.290304, DELTA6));

		q3 = q1.power(1);
		assertTrue(q3.getAmount() == q1.getAmount());
		assertTrue(q3.getUOM().getBaseSymbol().equals(q1.getUOM().getBaseSymbol()));

		q3 = q1.power(0);
		assertTrue(q3.getAmount() == 1d);
		assertTrue(q3.getUOM().getBaseSymbol().equals(sys.getOne().getBaseSymbol()));

		q3 = q1.power(-1);
		assertTrue(q3.getAmount() == 0.1);
		assertTrue(q3.getUOM().equals(ft.invert()));

		q3 = q1.power(-2);
		assertTrue(q3.getAmount() == 0.01);
		assertTrue(q3.getUOM().equals(ft2.invert()));
	}

	@Test
	public void testSIUnits() throws Exception {

		UnitOfMeasure newton = sys.getUOM(Unit.NEWTON);
		UnitOfMeasure metre = sys.getUOM(Unit.METRE);
		UnitOfMeasure m2 = sys.getUOM(Unit.SQUARE_METRE);
		UnitOfMeasure cm = sys.getUOM(Prefix.CENTI, metre);
		UnitOfMeasure mps = sys.getUOM(Unit.METRE_PER_SEC);
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
		UnitOfMeasure hz = sys.getUOM(Unit.HERTZ);

		double oneHundred = 100;

		assertTrue(nm.getBaseSymbol().equals(joule.getBaseSymbol()));
		assertTrue(cv.getBaseSymbol().equals(joule.getBaseSymbol()));
		assertTrue(ws.getBaseSymbol().equals(joule.getBaseSymbol()));

		Quantity q1 = new Quantity(10d, newton);
		Quantity q2 = new Quantity(10d, metre);
		Quantity q3 = q1.multiply(q2);
		assertTrue(isCloseTo(q3.getAmount(), oneHundred, DELTA6));
		assertTrue(q3.getUOM().getBaseSymbol().equals(nm.getBaseSymbol()));
		assertTrue(isCloseTo(q3.getUOM().getScalingFactor(), 1d, DELTA6));
		assertTrue(isCloseTo(q3.getUOM().getOffset(), 0d, DELTA6));

		q3 = q3.convert(joule);
		assertTrue(isCloseTo(q3.getAmount(), oneHundred, DELTA6));
		assertTrue(q3.getUOM().equals(joule));
		assertTrue(isCloseTo(q3.getUOM().getScalingFactor(), 1d, DELTA6));

		q3 = q3.convert(nm);
		assertTrue(isCloseTo(q3.getAmount(), oneHundred, DELTA6));
		assertTrue(q3.getUOM().equals(nm));
		assertTrue(isCloseTo(q3.getUOM().getScalingFactor(), 1d, DELTA6));

		double bd1 = 10000;

		q1 = new Quantity(oneHundred, cm);
		q2 = q1.convert(metre);
		assertTrue(isCloseTo(q2.getAmount(), 1d, DELTA6));
		assertTrue(q2.getUOM().getEnumeration().equals(Unit.METRE));
		assertTrue(isCloseTo(q2.getUOM().getScalingFactor(), 1d, DELTA6));

		q2 = q2.convert(cm);
		assertTrue(isCloseTo(q2.getAmount(), oneHundred, DELTA6));
		assertTrue(isCloseTo(q2.getUOM().getScalingFactor(), 0.01, DELTA6));

		q2 = q1;
		q3 = q1.multiply(q2);
		assertTrue(isCloseTo(q3.getAmount(), 10000, DELTA6));
		assertTrue(isCloseTo(q3.getUOM().getScalingFactor(), 0.0001, DELTA6));
		assertTrue(isCloseTo(q3.getUOM().getOffset(), 0d, DELTA6));

		Quantity q4 = q3.convert(m2);
		assertTrue(q4.getUOM().equals(m2));
		assertTrue(isCloseTo(q4.getAmount(), 1d, DELTA6));

		q3 = q3.convert(m2);
		assertTrue(isCloseTo(q3.getAmount(), 1d, DELTA6));
		assertTrue(q3.getUOM().equals(m2));
		assertTrue(isCloseTo(q3.getUOM().getScalingFactor(), 1d, DELTA6));

		q3 = q3.convert(cm2);
		assertTrue(isCloseTo(q3.getAmount(), bd1, DELTA6));
		assertTrue(q3.getUOM().equals(cm2));
		assertTrue(isCloseTo(q3.getUOM().getScalingFactor(), 1d, DELTA6));

		// power
		Quantity onem3 = new Quantity(1d, m3);
		String cm3sym = "cm" + (char) 0xB3;
		UnitOfMeasure cm3 = sys.createPowerUOM(UnitType.VOLUME, cm3sym, cm3sym, null, cm, 3);
		Quantity megcm3 = new Quantity(1E+06, cm3);

		Quantity qft3 = onem3.convert(ft3);
		assertTrue(isCloseTo(qft3.getAmount(), 35.31466672148859, DELTA6));

		Quantity qtym3 = qft3.convert(m3);
		assertTrue(isCloseTo(qtym3.getAmount(), 1d, DELTA6));

		Quantity qm3 = megcm3.convert(m3);
		assertTrue(isCloseTo(qm3.getAmount(), 1d, DELTA6));
		qm3 = qm3.convert(cm3);
		assertTrue(isCloseTo(qm3.getAmount(), 1E+06, DELTA6));

		Quantity qcm3 = onem3.convert(cm3);
		assertTrue(isCloseTo(qcm3.getAmount(), 1E+06, DELTA6));

		// inversions
		UnitOfMeasure u = metre.invert();
		String sym = u.getAbscissaUnit().getSymbol();
		assertTrue(sym.equals(sys.getUOM(Unit.DIOPTER).getSymbol()));

		u = mps.invert();
		assertTrue(u.getSymbol().equals("s/m"));

		UnitOfMeasure uom = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "1/F", "1/F", "one over farad", sys.getOne(),
				farad);
		assertTrue(uom.getSymbol().equals("1/F"));

		// hz to radians per sec
		q1 = new Quantity(10d, sys.getUOM(Unit.HERTZ));
		q2 = q1.convert(sys.getUOM(Unit.RAD_PER_SEC));
		double twentyPi = 20d * Math.PI;
		assertTrue(isCloseTo(q2.getAmount(), twentyPi, DELTA6));

		q3 = q2.convert(sys.getUOM(Unit.HERTZ));
		assertTrue(isCloseTo(q3.getAmount(), 10d, DELTA6));

		// rpm to radians per second
		q1 = new Quantity(10d, sys.getUOM(Unit.REV_PER_MIN));
		q2 = q1.convert(sys.getUOM(Unit.RAD_PER_SEC));
		assertTrue(isCloseTo(q2.getAmount(), 1.04719755119, DELTA6));

		q3 = q2.convert(sys.getUOM(Unit.REV_PER_MIN));
		assertTrue(isCloseTo(q3.getAmount(), 10d, DELTA6));

		q1 = new Quantity(10d, hz);
		q2 = new Quantity(1d, sys.getMinute());
		q3 = q1.multiply(q2).convert(sys.getOne());
		assertTrue(isCloseTo(q3.getAmount(), 600, DELTA6));

		q1 = new Quantity(1d, sys.getUOM(Unit.ELECTRON_VOLT));
		q2 = q1.convert(sys.getUOM(Unit.JOULE));
		assertTrue(isCloseTo(q2.getAmount(), 1.60217656535E-19, DELTA6));

	}

	@Test
	public void testEquations() throws Exception {

		// body mass index
		Quantity height = new Quantity(2d, sys.getUOM(Unit.METRE));
		Quantity mass = new Quantity(100d, sys.getUOM(Unit.KILOGRAM));
		Quantity bmi = mass.divide(height.multiply(height));
		assertTrue(isCloseTo(bmi.getAmount(), 25, DELTA6));

		// E = mc^2
		Quantity c = sys.getQuantity(Constant.LIGHT_VELOCITY);
		Quantity m = new Quantity(1d, sys.getUOM(Unit.KILOGRAM));
		Quantity e = m.multiply(c).multiply(c);
		assertTrue(isCloseTo(e.getAmount(), 8.987551787368176E+16, 1d));

		// Ideal Gas Law, PV = nRT
		// A cylinder of argon gas contains 50.0 L of Ar at 18.4 atm and 127 °C.
		// How many moles of argon are in the cylinder?
		Quantity p = new Quantity(18.4, sys.getUOM(Unit.ATMOSPHERE)).convert(Unit.PASCAL);
		Quantity v = new Quantity(50.0, Unit.LITRE).convert(Unit.CUBIC_METRE);
		Quantity t = new Quantity(127.0, Unit.CELSIUS).convert(Unit.KELVIN);
		Quantity n = p.multiply(v).divide(sys.getQuantity(Constant.GAS_CONSTANT).multiply(t));
		assertTrue(isCloseTo(n.getAmount(), 28.018664, DELTA6));

		// energy of red light photon = Planck's constant times the frequency
		Quantity frequency = new Quantity(400d, sys.getUOM(Prefix.TERA, Unit.HERTZ));
		Quantity ev = sys.getQuantity(Constant.PLANCK_CONSTANT).multiply(frequency).convert(Unit.ELECTRON_VOLT);
		assertTrue(isCloseTo(ev.getAmount(), 1.65, DELTA2));

		// wavelength of red light in nanometres
		Quantity wavelength = sys.getQuantity(Constant.LIGHT_VELOCITY).divide(frequency)
				.convert(sys.getUOM(Prefix.NANO, Unit.METRE));
		assertTrue(isCloseTo(wavelength.getAmount(), 749.48, DELTA2));

		// Newton's second law of motion (F = ma). Weight of 1 kg in lbf
		Quantity mkg = new Quantity(1d, Unit.KILOGRAM);
		Quantity f = mkg.multiply(sys.getQuantity(Constant.GRAVITY)).convert(Unit.POUND_FORCE);
		assertTrue(isCloseTo(f.getAmount(), 2.20462, DELTA5));

		// units per volume of solution, C = A x (m/V)
		// create the "A" unit of measure
		UnitOfMeasure activityUnit = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "activity", "act",
				"activity of material", sys.getUOM(Unit.UNIT), sys.getUOM(Prefix.MILLI, Unit.GRAM));

		// calculate concentration
		Quantity activity = new Quantity(1d, activityUnit);
		Quantity grams = new Quantity(1d, Unit.GRAM).convert(Prefix.MILLI, Unit.GRAM);
		Quantity volume = new Quantity(1d, sys.getUOM(Prefix.MILLI, Unit.LITRE));
		Quantity concentration = activity.multiply(grams.divide(volume));
		assertTrue(isCloseTo(concentration.getAmount(), 1000, DELTA6));

		Quantity katals = concentration.multiply(new Quantity(1d, Unit.LITRE)).convert(Unit.KATAL);
		assertTrue(isCloseTo(katals.getAmount(), 0.01666667, DELTA6));

		// The Stefan–Boltzmann law states that the power emitted per unit area
		// of the surface of a black body is directly proportional to the fourth
		// power of its absolute temperature: sigma * T^4

		// calculate at 1000 Kelvin
		Quantity temp = new Quantity(1000d, Unit.KELVIN);
		Quantity intensity = sys.getQuantity(Constant.STEFAN_BOLTZMANN).multiply(temp.power(4));
		assertTrue(isCloseTo(intensity.getAmount(), 56703.67, DELTA2));

		// Hubble's law, v = H0 x D. Let D = 10 Mpc
		Quantity d = new Quantity(10d, sys.getUOM(Prefix.MEGA, sys.getUOM(Unit.PARSEC)));
		Quantity h0 = sys.getQuantity(Constant.HUBBLE_CONSTANT);
		Quantity velocity = h0.multiply(d);
		assertTrue(isCloseTo(velocity.getAmount(), 719, DELTA3));

		// Arrhenius equation
		// A device has an activation energy of 0.5 and a characteristic life of
		// 2,750 hours at an accelerated temperature of 150 degrees Celsius.
		// Calculate the characteristic life at an expected use temperature of
		// 85 degrees Celsius.

		// Convert the Boltzman constant from J/K to eV/K for the Arrhenius
		// equation
		// eV per Joule
		Quantity j = new Quantity(1d, Unit.JOULE);
		Quantity eV = j.convert(Unit.ELECTRON_VOLT);
		// Boltzmann constant
		Quantity Kb = sys.getQuantity(Constant.BOLTZMANN_CONSTANT).multiply(eV.getAmount());
		// accelerated temperature
		Quantity Ta = new Quantity(150d, Unit.CELSIUS);
		// expected use temperature
		Quantity Tu = new Quantity(85d, Unit.CELSIUS);
		// calculate the acceleration factor
		Quantity factor1 = Tu.convert(Unit.KELVIN).invert().subtract(Ta.convert(Unit.KELVIN).invert());
		Quantity factor2 = Kb.invert().multiply(0.5);
		Quantity factor3 = factor1.multiply(factor2);
		double AF = Math.exp(factor3.getAmount());
		// calculate longer life at expected use temperature
		Quantity life85 = new Quantity(2750d, Unit.HOUR);
		Quantity life150 = life85.multiply(AF);
		assertTrue(isCloseTo(life150.getAmount(), 33121.4, DELTA1));
	}

	@Test
	public void testPackaging() throws Exception {
		double one = 1;
		double four = 4;
		double six = 6;
		double ten = 10;
		double forty = 40;

		UnitOfMeasure one16ozCan = sys.createScalarUOM(UnitType.VOLUME, "16 oz can", "16ozCan", "16 oz can");
		one16ozCan.setConversion(16d, sys.getUOM(Unit.US_FLUID_OUNCE));

		Quantity q400 = new Quantity(400d, one16ozCan);
		Quantity q50 = q400.convert(sys.getUOM(Unit.US_GALLON));
		assertTrue(isCloseTo(q50.getAmount(), 50, DELTA6));

		// 1 12 oz can = 12 fl.oz.
		UnitOfMeasure one12ozCan = sys.createScalarUOM(UnitType.VOLUME, "12 oz can", "12ozCan", "12 oz can");
		one12ozCan.setConversion(12d, sys.getUOM(Unit.US_FLUID_OUNCE));

		Quantity q48 = new Quantity(48d, one12ozCan);
		Quantity q36 = q48.convert(one16ozCan);
		assertTrue(isCloseTo(q36.getAmount(), 36, DELTA6));

		// 6 12 oz cans = 1 6-pack of 12 oz cans
		UnitOfMeasure sixPackCan = sys.createScalarUOM(UnitType.VOLUME, "6-pack", "6PCan", "6-pack of 12 oz cans");
		sixPackCan.setConversion(six, one12ozCan);

		UnitOfMeasure fourPackCase = sys.createScalarUOM(UnitType.VOLUME, "4 pack case", "4PCase", "case of 4 6-packs");
		fourPackCase.setConversion(four, sixPackCan);

		double bd = fourPackCase.getConversionFactor(one12ozCan);
		assertTrue(isCloseTo(bd, 24, DELTA6));

		bd = one12ozCan.getConversionFactor(fourPackCase);

		bd = fourPackCase.getConversionFactor(sixPackCan);
		bd = sixPackCan.getConversionFactor(fourPackCase);

		bd = sixPackCan.getConversionFactor(one12ozCan);
		bd = one12ozCan.getConversionFactor(sixPackCan);

		Quantity tenCases = new Quantity(ten, fourPackCase);

		Quantity q1 = tenCases.convert(one12ozCan);
		assertTrue(isCloseTo(q1.getAmount(), 240, DELTA6));

		Quantity q2 = q1.convert(fourPackCase);
		assertTrue(isCloseTo(q2.getAmount(), 10, DELTA6));

		Quantity fortyPacks = new Quantity(forty, sixPackCan);
		q2 = fortyPacks.convert(one12ozCan);
		assertTrue(isCloseTo(q2.getAmount(), 240, DELTA6));

		Quantity oneCan = new Quantity(one, one12ozCan);
		q2 = oneCan.convert(sixPackCan);
		assertTrue(isCloseTo(q2.getAmount(), 0.1666666666666667, DELTA6));

		// A beer bottling line is rated at 2000 12 ounce cans/hour (US) at the
		// filler. The case packer packs four 6-packs of cans into a case.
		// Assuming no losses, what should be the rating of the case packer in
		// cases per hour? And, what is the draw-down rate on the holding tank
		// in gallons/minute?
		UnitOfMeasure canph = sys.createQuotientUOM(one12ozCan, sys.getHour());
		UnitOfMeasure caseph = sys.createQuotientUOM(fourPackCase, sys.getHour());
		UnitOfMeasure gpm = sys.createQuotientUOM(sys.getUOM(Unit.US_GALLON), sys.getMinute());
		Quantity filler = new Quantity(2000d, canph);

		// draw-down
		Quantity draw = filler.convert(gpm);
		assertTrue(isCloseTo(draw.getAmount(), 3.125, DELTA6));

		// case production
		Quantity packer = filler.convert(caseph);
		assertTrue(isCloseTo(packer.getAmount(), 83.333333, DELTA6));
	}

	@Test
	public void testGenericQuantity() throws Exception {

		UnitOfMeasure a = sys.createScalarUOM(UnitType.UNCLASSIFIED, "a", "aUOM", "A");

		UnitOfMeasure b = sys.createScalarUOM(UnitType.UNCLASSIFIED, "b", "b", "B");
		b.setConversion(10d, a);

		double four = 4;

		double bd = Quantity.createAmount(new BigInteger("4"));
		assertTrue(bd == four);

		bd = Quantity.createAmount(Double.valueOf(4.0d));
		assertTrue(bd == four);

		bd = Quantity.createAmount(Float.valueOf(4.0f));
		assertTrue(bd == four);

		bd = Quantity.createAmount(Long.valueOf(4l));
		assertTrue(bd == four);

		bd = Quantity.createAmount(Integer.valueOf(4));
		assertTrue(bd == four);

		bd = Quantity.createAmount(Short.valueOf((short) 4));
		assertTrue(bd == four);

		// add
		Quantity q1 = new Quantity(four, a);

		assertFalse(q1 == null);

		Quantity q2 = new Quantity(four, b);
		Quantity q3 = q1.add(q2);

		assertTrue(isCloseTo(q3.getUOM().getScalingFactor(), 1d, DELTA6));
		assertTrue(q3.getUOM().getAbscissaUnit().equals(a));
		assertTrue(isCloseTo(q3.getUOM().getOffset(), 0d, DELTA6));
		assertTrue(isCloseTo(q3.getAmount(), 44, DELTA6));

		// subtract
		q3 = q1.subtract(q2);
		assertTrue(isCloseTo(q3.getUOM().getScalingFactor(), 1d, DELTA6));
		assertTrue(q3.getUOM().getAbscissaUnit().equals(a));
		assertTrue(isCloseTo(q3.getUOM().getOffset(), 0d, DELTA6));
		assertTrue(isCloseTo(q3.getAmount(), -36, DELTA6));

		// multiply
		q3 = q1.multiply(q2);
		assertTrue(isCloseTo(q3.getAmount(), 16, DELTA6));
		assertTrue(isCloseTo(q3.getUOM().getScalingFactor(), 1d, DELTA6));
		assertTrue(isCloseTo(q3.getUOM().getOffset(), 0d, DELTA6));

		UnitOfMeasure a2 = sys.createPowerUOM(UnitType.UNCLASSIFIED, "a*2", "a*2", "A squared", a, 2);
		Quantity q4 = q3.convert(a2);
		assertTrue(isCloseTo(q4.getAmount(), 160, DELTA6));
		assertTrue(q4.getUOM().equals(a2));

		q4 = q3.divide(q2);
		assertTrue(q4.equals(q1));
		assertTrue(isCloseTo(q4.getAmount(), 4, DELTA6));

		// divide
		q3 = q1.divide(q2);
		assertTrue(isCloseTo(q3.getAmount(), 1d, DELTA6));
		assertTrue(isCloseTo(q3.getUOM().getScalingFactor(), 0.1, DELTA6));

		q4 = q3.multiply(q2);
		assertTrue(q4.equals(q1));

		// population density
		UnitOfMeasure person = sys.createScalarUOM(UnitType.UNCLASSIFIED, "person", "person", "an individual");
		Quantity qPopulation = new Quantity(2.7, Prefix.MEGA, person);
		UnitOfMeasure sqmi = sys.createPowerUOM(sys.getUOM(Unit.MILE), 2);
		Quantity qArea = new Quantity(5, sqmi);
		Quantity popDensity = qArea.convert(Unit.SQUARE_FOOT).divide(qPopulation);
		assertTrue(isCloseTo(popDensity.getAmount(), 51.626666666666665, DELTA6));
	}

	@Test
	public void testExceptions() throws Exception {

		UnitOfMeasure floz = sys.getUOM(Unit.BR_FLUID_OUNCE);

		Quantity q1 = new Quantity(10d, sys.getDay());
		Quantity q2 = new Quantity(10d, sys.getUOM(Unit.BR_FLUID_OUNCE));

		try {
			String amount = null;
			Quantity.createAmount(amount);
			fail("create");
		} catch (Exception e) {
		}

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

		UnitOfMeasure newton = sys.getUOM(Unit.NEWTON);
		UnitOfMeasure metre = sys.getUOM(Unit.METRE);
		UnitOfMeasure nm = sys.getUOM(Unit.NEWTON_METRE);
		UnitOfMeasure m2 = sys.getUOM(Unit.SQUARE_METRE);
		UnitOfMeasure J = sys.getUOM(Unit.JOULE);
		double amount = 10;

		final Quantity q1 = new Quantity(amount, newton);
		final Quantity q2 = new Quantity(amount, metre);
		final Quantity q3 = new Quantity(amount, nm);
		Quantity q5 = new Quantity(100d, nm);

		// unity
		Quantity q4 = q5.divide(q3);
		assertTrue(q4.getUOM().getBaseSymbol().equals(sys.getOne().getSymbol()));
		assertTrue(q4.getAmount() == amount);

		// Newton-metre (Joules)
		q4 = q1.multiply(q2);
		assertTrue(q5.getUOM().getBaseSymbol().equals(q4.getUOM().getBaseSymbol()));
		Quantity q6 = q5.convert(J);
		assertTrue(q6.getAmount() == q4.getAmount());

		// Newton
		q5 = q4.divide(q2);
		assertTrue(q5.getUOM().getBaseSymbol().equals(q1.getUOM().getBaseSymbol()));
		assertTrue(q5.equals(q1));

		// metre
		q5 = q4.divide(q1);
		assertTrue(q5.getUOM().getBaseSymbol().equals(q2.getUOM().getBaseSymbol()));
		assertTrue(q5.equals(q2));

		// square metre
		q4 = q2.multiply(q2);
		q5 = new Quantity(100d, m2);
		assertTrue(q4.getUOM().getSymbol().equals(q5.getUOM().getSymbol()));
		assertTrue(q5.equals(q4));

		// metre
		q4 = q5.divide(q2);
		assertTrue(q4.getUOM().getBaseSymbol().equals(q2.getUOM().getBaseSymbol()));
		assertTrue(q4.equals(q2));

	}

	@Test
	public void testComparison() throws Exception {

		UnitOfMeasure newton = sys.getUOM(Unit.NEWTON);
		UnitOfMeasure metre = sys.getUOM(Unit.METRE);
		UnitOfMeasure cm = sys.getUOM(Prefix.CENTI, metre);

		double amount = 10;

		final Quantity qN = new Quantity(amount, newton);
		final Quantity qm10 = new Quantity(amount, metre);
		final Quantity qm1 = new Quantity(1d, metre);
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

		Quantity conc = new Quantity(0.0025, sys.getUOM(Unit.MOLARITY));
		double pH = -Math.log10(conc.getAmount());
		assertTrue(isCloseTo(pH, 2.60, DELTA2));
	}

	@Test
	public void testArithmetic() throws Exception {

		UnitOfMeasure in = sys.getUOM(Unit.INCH);
		UnitOfMeasure cm = sys.getUOM(Prefix.CENTI, sys.getUOM(Unit.METRE));
		Quantity qcm = new Quantity(1d, cm);
		Quantity qin = new Quantity(1d, in);
		double bd = 2.54;
		Quantity q1 = qcm.multiply(bd).convert(in);
		assertTrue(isCloseTo(q1.getAmount(), qin.getAmount(), DELTA6));
		Quantity q2 = q1.convert(cm);
		assertTrue(isCloseTo(q2.getAmount(), bd, DELTA6));
	}

	@Test
	public void testFinancial() throws Exception {
		Quantity q1 = new Quantity(10d, Unit.US_DOLLAR);
		Quantity q2 = new Quantity(12d, Unit.US_DOLLAR);
		Quantity q3 = q2.subtract(q1).divide(q1).convert(Unit.PERCENT);
		assertTrue(isCloseTo(q3.getAmount(), 20, DELTA6));
	}

	@Test
	public void testPowerProductConversions() throws Exception {
		sys.clearCache();

		UnitOfMeasure one = sys.getOne();
		UnitOfMeasure mps = sys.getUOM(Unit.METRE_PER_SEC);
		UnitOfMeasure fps = sys.getUOM(Unit.FEET_PER_SEC);
		UnitOfMeasure nm = sys.getUOM(Unit.NEWTON_METRE);
		UnitOfMeasure ft = sys.getUOM(Unit.FOOT);
		UnitOfMeasure in = sys.getUOM(Unit.INCH);
		UnitOfMeasure mi = sys.getUOM(Unit.MILE);
		UnitOfMeasure hr = sys.getUOM(Unit.HOUR);
		UnitOfMeasure m = sys.getUOM(Unit.METRE);
		UnitOfMeasure s = sys.getUOM(Unit.SECOND);
		UnitOfMeasure n = sys.getUOM(Unit.NEWTON);
		UnitOfMeasure lbf = sys.getUOM(Unit.POUND_FORCE);
		UnitOfMeasure m2 = sys.getUOM(Unit.SQUARE_METRE);
		UnitOfMeasure m3 = sys.getUOM(Unit.CUBIC_METRE);
		UnitOfMeasure ft2 = sys.getUOM(Unit.SQUARE_FOOT);

		// test products and quotients
		sys.unregisterUnit(sys.getUOM(Unit.FOOT_POUND_FORCE));
		Quantity nmQ = new Quantity(1d, nm);
		Quantity lbfinQ = nmQ.convertToPowerProduct(lbf, in);
		assertTrue(isCloseTo(lbfinQ.getAmount(), 8.850745791327183, DELTA6));
		Quantity mpsQ = new Quantity(1d, mps);

		Quantity fphQ = mpsQ.convertToPowerProduct(ft, hr);
		assertTrue(isCloseTo(fphQ.getAmount(), 11811.02362204724, DELTA6));

		Quantity mps2Q = fphQ.convertToPowerProduct(m, s);
		assertTrue(isCloseTo(mps2Q.getAmount(), 1d, DELTA6));

		Quantity mps3Q = mpsQ.convertToPowerProduct(m, s);
		assertTrue(isCloseTo(mps3Q.getAmount(), 1d, DELTA6));

		Quantity inlbfQ = nmQ.convertToPowerProduct(in, lbf);
		assertTrue(isCloseTo(inlbfQ.getAmount(), 8.850745791327183, DELTA6));

		Quantity nm2Q = lbfinQ.convertToPowerProduct(n, m);
		assertTrue(isCloseTo(nm2Q.getAmount(), 1d, DELTA6));

		nm2Q = lbfinQ.convertToPowerProduct(m, n);
		assertTrue(isCloseTo(nm2Q.getAmount(), 1d, DELTA6));

		Quantity mQ = new Quantity(1d, m);
		try {
			mQ.convertToPowerProduct(ft, hr);
			fail();
		} catch (Exception e) {
		}

		sys.unregisterUnit(sys.getUOM(Unit.SQUARE_FOOT));
		Quantity m2Q = new Quantity(1d, m2);
		Quantity ft2Q = m2Q.convertToPowerProduct(ft, ft);
		assertTrue(isCloseTo(ft2Q.getAmount(), 10.76391041670972, DELTA6));

		Quantity mmQ = ft2Q.convertToPowerProduct(m, m);
		assertTrue(isCloseTo(mmQ.getAmount(), 1d, DELTA6));

		try {
			m2Q.convertToPowerProduct(m, one);
			fail();
		} catch (Exception e) {
		}

		sys.unregisterUnit(sys.getUOM(Unit.CUBIC_FOOT));
		Quantity m3Q = new Quantity(1d, m3);
		Quantity ft3Q = m3Q.convertToPowerProduct(ft2, ft);
		assertTrue(isCloseTo(ft3Q.getAmount(), 35.31466672148858, DELTA6));

		Quantity m3Q2 = m3Q.convertToPowerProduct(m2, m);
		assertTrue(isCloseTo(m3Q2.getAmount(), 1d, DELTA6));

		ft3Q = m3Q.convertToPowerProduct(ft, ft2);
		assertTrue(isCloseTo(ft3Q.getAmount(), 35.31466672148858, DELTA6));

		UnitOfMeasure perM = sys.getUOM(Unit.DIOPTER);
		Quantity perMQ = new Quantity(1d, perM);
		Quantity perInQ = perMQ.convertToPowerProduct(one, in);
		assertTrue(isCloseTo(perInQ.getAmount(), 0.0254, DELTA6));

		try {
			perMQ.convertToPowerProduct(in, one);
			fail();
		} catch (Exception e) {
		}

		try {
			perMQ.convertToPowerProduct(in, in);
			fail();
		} catch (Exception e) {
		}

		Quantity fpsQ = new Quantity(1d, fps);
		Quantity mphQ = fpsQ.convertToPowerProduct(mi, hr);
		assertTrue(isCloseTo(mphQ.getAmount(), 0.6818181818181818, DELTA6));

		// test powers
		Quantity in2Q = m2Q.convertToPower(in);
		assertTrue(isCloseTo(in2Q.getAmount(), 1550.003100006200, DELTA6));

		Quantity m2Q2 = in2Q.convertToPower(m);
		assertTrue(isCloseTo(m2Q2.getAmount(), 1d, DELTA6));

		Quantity perInQ2 = perMQ.convertToPower(in);
		assertTrue(isCloseTo(perInQ2.getAmount(), 0.0254, DELTA6));

		Quantity q1 = perInQ2.convertToPower(m);
		assertTrue(isCloseTo(q1.getAmount(), 1d, DELTA6));

		Quantity inQ2 = mQ.convertToPower(in);
		assertTrue(isCloseTo(inQ2.getAmount(), 39.37007874015748, DELTA6));

		q1 = inQ2.convertToPower(m);
		assertTrue(isCloseTo(q1.getAmount(), 1d, DELTA6));

		Quantity one1 = new Quantity(1d, sys.getOne());
		q1 = one1.convertToPower(sys.getOne());
		assertTrue(isCloseTo(q1.getAmount(), 1d, DELTA6));
	}

	@Test
	public void testEnergy() throws Exception {
		// A nutrition label says the energy content is 1718 KJ. What is this amount in
		// kilo-calories?
		Quantity kcal = new Quantity(1718, Prefix.KILO, Unit.JOULE).convert(Prefix.KILO, Unit.CALORIE);
		assertTrue(isCloseTo(kcal.getAmount(), 410.6, DELTA1));

		// A Tesla Model S battery has a capacity of 100 KwH.
		// When fully charged, how many electrons are in the battery?
		Quantity c = sys.getQuantity(Constant.LIGHT_VELOCITY);
		Quantity me = sys.getQuantity(Constant.ELECTRON_MASS);
		Quantity kwh = new Quantity(100, Prefix.KILO, Unit.WATT_HOUR);

		Quantity wh = kwh.convert(Unit.WATT_HOUR);
		assertTrue(wh.getAmount() == 1.0E+05);

		Quantity electrons = kwh.divide(c).divide(c).divide(me);
		double d = electrons.getAmount() / 1.221E12;
		assertTrue(isCloseTo(d, 1.0, DELTA1));
	}

	@Test
	public void testClassification() throws Exception {
		Quantity mass = new Quantity(1035, Unit.KILOGRAM);
		Quantity volume = new Quantity(1000, Unit.LITRE);
		Quantity density = mass.divide(volume).classify();
		assertTrue(density.getUOM().getUnitType().equals(UnitType.DENSITY));
	}

	@Test
	public void testMultiConversion() throws Exception {
		// convert 74 inches to feet and inches
		Quantity qHeight = new Quantity(74, Unit.INCH);
		List<UnitOfMeasure> uoms = new ArrayList<>();
		uoms.add(sys.getUOM(Unit.FOOT));
		uoms.add(sys.getUOM(Unit.INCH));
		List<Quantity> converted = qHeight.convert(uoms);
		assertTrue(isCloseTo(converted.get(0).getAmount(), 6.0, DELTA0));
		assertTrue(isCloseTo(converted.get(1).getAmount(), 2.0, DELTA6));

		Quantity qkm = new Quantity(10, Prefix.KILO, Unit.METRE);

		uoms = new ArrayList<>();
		uoms.add(sys.getUOM(Prefix.KILO, Unit.METRE));
		converted = qkm.convert(uoms);
		assertTrue(isCloseTo(converted.get(0).getAmount(), 10.0, DELTA6));

		uoms = new ArrayList<>();
		uoms.add(sys.getUOM(Unit.MILE));
		uoms.add(sys.getUOM(Unit.FOOT));
		uoms.add(sys.getUOM(Unit.INCH));
		converted = qkm.convert(uoms);
		assertTrue(isCloseTo(converted.get(0).getAmount(), 6.0, DELTA6));
		assertTrue(isCloseTo(converted.get(1).getAmount(), 1128.0, DELTA6));
		assertTrue(isCloseTo(converted.get(2).getAmount(), 4.787, DELTA3));
	}
	
	@Test
	public void testCurrencyConversion() throws Exception {
		MeasurementSystem sys = MeasurementSystem.getSystem();
		UnitOfMeasure usd_uom = sys.createScalarUOM(UnitType.CURRENCY, "US-Dollar", "USD", "US 'paper' dollar");
		UnitOfMeasure usdt_uom = sys.createScalarUOM(UnitType.CURRENCY, "Tether", "USDT", "USD 'stable' coin");

		// Initial conversion rate
		usdt_uom.setConversion(0.9, usd_uom);

		Quantity portfolio = new Quantity(200, usdt_uom);
		Quantity portfolio_usd = portfolio.convert(usd_uom);
		assertTrue(isCloseTo(portfolio_usd.getAmount(), 180.0, DELTA6));

		// change conversion rate
		usdt_uom.setConversion(1.0, usd_uom);
		portfolio_usd = portfolio.convert(usd_uom);
		assertTrue(isCloseTo(portfolio_usd.getAmount(), 200.0, DELTA6));
	}
}
