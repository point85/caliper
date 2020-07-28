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

import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.point85.uom.Constant;
import org.point85.uom.Prefix;
import org.point85.uom.Quantity;
import org.point85.uom.Unit;
import org.point85.uom.UnitOfMeasure;
import org.point85.uom.UnitType;

public class TestUnits extends BaseTest {
	@Test
	public void testBaseUnits() throws Exception {
		Map<UnitOfMeasure, Integer> terms = sys.getUOM(Unit.NEWTON).getBaseUnitsOfMeasure();
		assertTrue(terms.size() == 3);

		for (Entry<UnitOfMeasure, Integer> entry : terms.entrySet()) {
			if (entry.getKey().getUnitType().equals(UnitType.MASS)) {
				assertTrue(entry.getValue().equals(1));
			} else if (entry.getKey().getUnitType().equals(UnitType.TIME)) {
				assertTrue(entry.getValue().equals(-2));
			} else if (entry.getKey().getUnitType().equals(UnitType.LENGTH)) {
				assertTrue(entry.getValue().equals(1));
			}
		}

		UnitOfMeasure m = sys.getUOM(Unit.METRE);
		UnitOfMeasure m2 = m.power(2);

		assertTrue(m2.getPowerExponent() == 2);
	}

	@Test
	public void testPrefixes() {
		for (Prefix prefix : Prefix.getDefinedPrefixes()) {
			String prefixName = prefix.getName();
			assertTrue(prefixName.length() > 0);
			assertTrue(prefix.getSymbol().length() > 0);
			assertTrue(prefix.getFactor() != 1.0d);
			assertTrue(prefix.toString().length() > 0);
			assertTrue(Prefix.fromName(prefixName).equals(prefix));
		}
	}

	@Test
	public void testExceptions() throws Exception {

		UnitOfMeasure uom1 = sys.createScalarUOM(UnitType.UNCLASSIFIED, "uom1", "uom1", "");
		UnitOfMeasure uom2 = sys.createScalarUOM(UnitType.UNCLASSIFIED, "uom2", "uom2", "");
		UnitOfMeasure uom3 = sys.createScalarUOM(UnitType.UNCLASSIFIED, "uom3", "uom3", "");

		uom1.setConversion(1.0d, uom3, 10.0d);
		uom2.setConversion(1.0d, uom3, 1.0d);
		assertFalse(uom1.equals(uom2));

		try {
			sys.createPowerUOM(null, 0);
			fail();
		} catch (Exception e) {
		}

		try {
			sys.createProductUOM(null, sys.getOne());
			fail();
		} catch (Exception e) {
		}

		try {
			sys.createProductUOM(sys.getOne(), null);
			fail();
		} catch (Exception e) {
		}

		try {
			sys.createQuotientUOM(null, sys.getOne());
			fail();
		} catch (Exception e) {
		}

		try {
			sys.createQuotientUOM(sys.getOne(), null);
			fail();
		} catch (Exception e) {
		}

		try {
			sys.createQuotientUOM(UnitType.UNCLASSIFIED, "uom4", "uom4", "", sys.getUOM(Unit.METRE), null);
			fail();
		} catch (Exception e) {
		}

		try {
			sys.createQuotientUOM(UnitType.UNCLASSIFIED, "uom4", "uom4", "", null, sys.getUOM(Unit.METRE));
			fail();
		} catch (Exception e) {
		}

		try {
			sys.createProductUOM(UnitType.UNCLASSIFIED, "uom4", "uom4", "", sys.getUOM(Unit.METRE), null);
			fail();
		} catch (Exception e) {
		}

		try {
			sys.createProductUOM(UnitType.UNCLASSIFIED, "uom4", "uom4", "", null, sys.getUOM(Unit.METRE));
			fail();
		} catch (Exception e) {
		}

		try {
			Quantity q = new Quantity(10.0d, Unit.METRE);
			q.convert(Unit.SECOND);
			fail("no conversion");
		} catch (Exception e) {
		}

		sys.unregisterUnit(null);

		UnitOfMeasure u = null;

		try {
			sys.createScalarUOM(UnitType.UNCLASSIFIED, "456", null, "description");
			fail("no symbol");
		} catch (Exception e) {
		}

		try {
			sys.createScalarUOM(UnitType.UNCLASSIFIED, "456", "", "description");
			fail("no symbol");
		} catch (Exception e) {
		}

		try {
			sys.createProductUOM(UnitType.UNCLASSIFIED, null, "abcd", "", null, null);
			fail("null");
		} catch (Exception e) {
		}

		try {
			sys.createQuotientUOM(UnitType.UNCLASSIFIED, null, "abcd", "", null, null);
			fail("null");
		} catch (Exception e) {
		}

		try {
			sys.createPowerUOM(UnitType.UNCLASSIFIED, null, "abcd", "", null, 2);
			fail("null");
		} catch (Exception e) {
		}

		try {
			sys.createScalarUOM(null, "1/1", "1/1", "");
			fail("no type");
		} catch (Exception e) {
		}

		try {
			sys.createScalarUOM(UnitType.UNCLASSIFIED, "", null, "");
			sys.createScalarUOM(UnitType.UNCLASSIFIED, "", "", "");
			fail("already created");
		} catch (Exception e) {
		}

		u = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "1/1", "1/1", "", sys.getOne(), sys.getOne());
		Quantity q1 = new Quantity(10.0d, u);
		Quantity q2 = q1.convert(sys.getOne());
		assertTrue(isCloseTo(q2.getAmount(), 10.0d, DELTA6));
		assertTrue(q2.getUOM().equals(sys.getOne()));

		u = sys.createProductUOM(UnitType.UNCLASSIFIED, "1x1", "1x1", "", sys.getOne(), sys.getOne());
		double bd = u.getConversionFactor(sys.getOne());
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		u = sys.createProductUOM(UnitType.UNCLASSIFIED, "1x1", "1x1", "", sys.getOne(), sys.getOne());
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getBaseSymbol()));

		u = sys.createPowerUOM(UnitType.UNCLASSIFIED, "1^2", "1^2", "", sys.getOne(), 2);
		bd = u.getConversionFactor(sys.getOne());
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		u = sys.createPowerUOM(UnitType.UNCLASSIFIED, "1^2", "1^2", "", sys.getOne(), 2);
		bd = u.getConversionFactor(sys.getOne());
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		u = sys.createPowerUOM(UnitType.UNCLASSIFIED, "1^0", "1^0", "", sys.getOne(), 0);
		bd = u.getConversionFactor(sys.getOne());
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		u = sys.createPowerUOM(UnitType.UNCLASSIFIED, "1^0", "1^0", "", sys.getOne(), 0);
		bd = u.getConversionFactor(sys.getOne());
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		UnitOfMeasure uno = sys.getOne();
		u = sys.createPowerUOM(UnitType.UNCLASSIFIED, "m^0", "m^0", "", sys.getUOM(Unit.METRE), 0);
		bd = u.getConversionFactor(uno);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getBaseSymbol()));

		UnitOfMeasure m1 = sys.getUOM(Unit.METRE);
		u = sys.createPowerUOM(UnitType.UNCLASSIFIED, "m^1", "m^1", "", sys.getUOM(Unit.METRE), 1);
		assertTrue(u.getBaseSymbol().equals(m1.getBaseSymbol()));

		UnitOfMeasure m2 = sys.getUOM(Unit.SQUARE_METRE);
		u = sys.createPowerUOM(UnitType.UNCLASSIFIED, "m^2", "m^2", "", sys.getUOM(Unit.METRE), 2);
		assertTrue(u.getBaseSymbol().equals(m2.getBaseSymbol()));

		UnitOfMeasure perMetre = m1.invert();
		UnitOfMeasure diopter = sys.getUOM(Unit.DIOPTER);
		assertTrue(perMetre.getBaseSymbol().equals(diopter.getBaseSymbol()));

		u = sys.createPowerUOM(UnitType.UNCLASSIFIED, "m*-1", "m*-1", "", sys.getUOM(Unit.METRE), -1);
		UnitOfMeasure mult = u.multiply(m1);
		assertTrue(mult.getBaseSymbol().equals(sys.getUOM(Unit.ONE).getBaseSymbol()));

		UnitOfMeasure perMetre2 = m2.invert();
		u = sys.createPowerUOM(UnitType.UNCLASSIFIED, "m*-2", "m*-2", "", sys.getUOM(Unit.METRE), -2);
		assertTrue(u.getBaseSymbol().equals(perMetre2.getBaseSymbol()));

		u = sys.createPowerUOM(UnitType.UNCLASSIFIED, "m^0", "m^0", "", sys.getUOM(Unit.METRE), 0);

		try {
			UnitOfMeasure abscissaUnit = null;
			uno.setConversion(abscissaUnit);
			fail();
		} catch (Exception e) {
		}
	}

	@Test
	public void testOne() throws Exception {

		UnitOfMeasure metre = sys.getUOM(Unit.METRE);

		UnitOfMeasure u = metre.multiply(sys.getOne());
		assertTrue(u.equals(metre));

		u = metre.divide(sys.getOne());
		assertTrue(u.equals(metre));

		UnitOfMeasure oneOverM = metre.invert();
		u = oneOverM.invert();
		assertTrue(u.equals(metre));

		u = oneOverM.multiply(metre);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getBaseSymbol()));

		u = metre.divide(metre);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getBaseSymbol()));

		u = sys.getOne().divide(metre).multiply(metre);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getBaseSymbol()));

		UnitOfMeasure uom = sys.createScalarUOM(UnitType.UNCLASSIFIED, "1/1", "1/1", "");
		uom.setConversion(1.0d, sys.getOne(), 1.0d);

		assertTrue(isCloseTo(uom.getScalingFactor(), 1.0d, DELTA6));
		assertTrue(uom.getAbscissaUnit().equals(sys.getOne()));
		assertTrue(isCloseTo(uom.getOffset(), 1.0d, DELTA6));

		u = sys.getOne().invert();
		assertTrue(u.getAbscissaUnit().getBaseSymbol().equals(sys.getOne().getBaseSymbol()));

		UnitOfMeasure one = sys.getOne();
		assertTrue(one.getBaseSymbol().equals("1"));
		assertTrue(one.equals(one));

		UnitOfMeasure uno = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "", ".1", "", one, one);
		assertTrue(uno.getBaseSymbol().equals(one.getBaseSymbol()));

		UnitOfMeasure p = sys.createProductUOM(UnitType.UNCLASSIFIED, "", "..1", "", one, one);
		assertTrue(p.getBaseSymbol().equals(one.getBaseSymbol()));

		UnitOfMeasure p3 = sys.createPowerUOM(UnitType.UNCLASSIFIED, "", "...1", "", one, 3);
		assertTrue(p3.getBaseSymbol().equals(one.getBaseSymbol()));

		p3 = sys.createPowerUOM(UnitType.UNCLASSIFIED, "", "...1", "", one, -1);
		assertTrue(p3.getBaseSymbol().equals(one.getBaseSymbol()));

		UnitOfMeasure a1 = sys.createScalarUOM(UnitType.UNCLASSIFIED, "a1", "a1", "A1");
		assertTrue(a1.getBaseSymbol().equals("a1"));

		uno = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "one", "one", "", a1, a1);
		assertTrue(uno.getBaseSymbol().equals(one.getBaseSymbol()));
	}

	@Test
	public void testGeneric() throws Exception {

		UnitOfMeasure b = sys.createScalarUOM(UnitType.UNCLASSIFIED, "b", "beta", "Beta");
		assertFalse(b.equals(null));

		// scalar
		double two = 2d;
		UnitOfMeasure ab1 = sys.createScalarUOM(UnitType.UNCLASSIFIED, "a=2b+1", "a=2b+1", "custom");
		ab1.setConversion(2d, b, 1.0d);

		assertTrue(isCloseTo(ab1.getScalingFactor(), two, DELTA6));
		assertTrue(ab1.getAbscissaUnit().equals(b));
		assertTrue(isCloseTo(ab1.getOffset(), 1.0d, DELTA6));

		// quotient
		UnitOfMeasure a = sys.createScalarUOM(UnitType.UNCLASSIFIED, "a", "alpha", "Alpha");
		assertTrue(a.getAbscissaUnit().equals(a));

		UnitOfMeasure aOverb = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "a/b", "a/b", "", a, b);
		aOverb.setScalingFactor(two);

		assertTrue(isCloseTo(aOverb.getScalingFactor(), two, DELTA6));
		assertTrue(aOverb.getDividend().equals(a));
		assertTrue(aOverb.getDivisor().equals(b));
		assertTrue(isCloseTo(aOverb.getOffset(), 0.0d, DELTA6));
		assertTrue(aOverb.getAbscissaUnit().equals(aOverb));

		UnitOfMeasure bOvera = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "b/a", "b/a", "", b, a);
		UnitOfMeasure bOveraI = bOvera.invert();
		assertTrue(bOveraI.getBaseSymbol().equals(aOverb.getBaseSymbol()));

		// multiply2
		UnitOfMeasure uom = aOverb.multiply(b);
		assertTrue(uom.getAbscissaUnit().getBaseSymbol().equals(a.getBaseSymbol()));
		assertTrue(isCloseTo(uom.getScalingFactor(), two, DELTA6));
		double bd = uom.getConversionFactor(a);
		assertTrue(isCloseTo(bd, two, DELTA6));

		// divide2
		UnitOfMeasure uom2 = uom.divide(b);
		assertTrue(isCloseTo(uom2.getScalingFactor(), two, DELTA6));
		assertTrue(isCloseTo(uom2.getOffset(), 0.0d, DELTA6));
		assertTrue(uom2.getBaseSymbol().equals(aOverb.getBaseSymbol()));

		// invert
		UnitOfMeasure uom3 = uom2.invert();
		UnitOfMeasure u = uom3.multiply(uom2);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getBaseSymbol()));

		// product
		UnitOfMeasure ab = sys.createProductUOM(UnitType.UNCLASSIFIED, "name", "symbol", "custom", a, b);
		ab.setOffset(1.0d);

		assertTrue(isCloseTo(ab.getScalingFactor(), 1.0d, DELTA6));
		assertTrue(ab.getMultiplier().equals(a));
		assertTrue(ab.getMultiplicand().equals(b));
		assertTrue(isCloseTo(ab.getOffset(), 1.0d, DELTA6));
		assertTrue(ab.getAbscissaUnit().equals(ab));

		ab.setOffset(0.0d);

		UnitOfMeasure uom4 = ab.divide(a);
		assertTrue(isCloseTo(uom4.getScalingFactor(), 1.0d, DELTA6));
		assertTrue(uom4.getAbscissaUnit().getBaseSymbol().equals(b.getBaseSymbol()));

		UnitOfMeasure uom5 = uom4.multiply(a);
		assertTrue(isCloseTo(uom5.getScalingFactor(), 1.0d, DELTA6));
		u = uom5.getAbscissaUnit();
		assertTrue(u.getBaseSymbol().equals(ab.getBaseSymbol()));

		// invert
		UnitOfMeasure uom6 = ab.invert();
		assertTrue(isCloseTo(uom6.getScalingFactor(), 1, DELTA6));
		assertTrue(uom6.getDividend().equals(sys.getOne()));
		assertTrue(uom6.getDivisor().equals(ab));
		assertTrue(isCloseTo(uom6.getOffset(), 0.0d, DELTA6));

		// power
		UnitOfMeasure a2 = sys.createPowerUOM(UnitType.UNCLASSIFIED, "name", "a**2", "custom", a, 2);

		assertTrue(isCloseTo(a2.getScalingFactor(), 1.0d, DELTA6));
		assertTrue(a2.getPowerBase().equals(a));
		assertTrue(a2.getPowerExponent() == 2);
		assertTrue(isCloseTo(a2.getOffset(), 0.0d, DELTA6));
		assertTrue(a2.getAbscissaUnit().equals(a2));

		UnitOfMeasure uom8 = a2.divide(a);
		assertTrue(isCloseTo(uom8.getScalingFactor(), 1.0d, DELTA6));
		assertTrue(isCloseTo(uom8.getOffset(), 0.0d, DELTA6));
		assertTrue(uom8.getAbscissaUnit().getBaseSymbol().equals(a.getBaseSymbol()));

		UnitOfMeasure uom9 = uom8.multiply(a);
		assertTrue(isCloseTo(uom9.getScalingFactor(), 1, DELTA6));
		assertTrue(isCloseTo(uom9.getOffset(), 0.0d, DELTA6));
		u = uom9.getAbscissaUnit();
		assertTrue(u.getBaseSymbol().equals(a2.getBaseSymbol()));

		u = sys.getUOM(a.getSymbol());
		assertFalse(uom == null);

		// again
		UnitOfMeasure c = sys.createScalarUOM(UnitType.UNCLASSIFIED, "c", "cUnit", "C");
		UnitOfMeasure x = sys.createScalarUOM(UnitType.UNCLASSIFIED, "x", "xUnit", "X");
		UnitOfMeasure e = sys.createScalarUOM(UnitType.UNCLASSIFIED, "e", "eUnit", "E");

		UnitOfMeasure aTimesa = sys.createProductUOM(UnitType.UNCLASSIFIED, "", "aUnit*2", "", a, a);
		u = aTimesa.divide(a);
		assertTrue(u.getBaseSymbol().equals(a.getBaseSymbol()));

		u = aOverb.multiply(b);
		assertTrue(u.getBaseSymbol().equals(a.getBaseSymbol()));

		UnitOfMeasure cOverx = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "", "c/x", "", c, x);
		UnitOfMeasure alpha = aOverb.divide(cOverx);
		UnitOfMeasure beta = alpha.multiply(cOverx);
		assertTrue(beta.getBaseSymbol().equals(aOverb.getBaseSymbol()));

		u = aOverb.multiply(cOverx).divide(cOverx);
		assertTrue(u.getAbscissaUnit().getBaseSymbol().equals(aOverb.getBaseSymbol()));

		UnitOfMeasure axb = sys.createProductUOM(UnitType.UNCLASSIFIED, "", "a.b", "", a, b);
		u = sys.getUOM(axb.getSymbol());
		assertTrue(u.equals(axb));
		u = axb.divide(a);
		assertTrue(u.getBaseSymbol().equals(b.getBaseSymbol()));

		String symbol = axb.getSymbol() + "." + axb.getSymbol();
		UnitOfMeasure axbsq = sys.createProductUOM(UnitType.UNCLASSIFIED, "", symbol, "", axb, axb);
		u = axbsq.divide(axb);
		assertTrue(u.getBaseSymbol().equals(axb.getBaseSymbol()));

		UnitOfMeasure b2 = sys.createPowerUOM(UnitType.UNCLASSIFIED, "b2", "b*2", "", b, 2);

		symbol = axb.getBaseSymbol();
		u = sys.getBaseUOM(symbol);
		assertTrue(u != null);

		UnitOfMeasure axb2 = sys.createPowerUOM(UnitType.UNCLASSIFIED, "axb2", "(a.b)*2", "", axb, 2);
		u = axb2.divide(axb);
		assertTrue(u.getBaseSymbol().equals(axb.getBaseSymbol()));

		UnitOfMeasure aOverb2 = sys.createPowerUOM(UnitType.UNCLASSIFIED, "aOverb2", "(a/b)*2", "", aOverb, 2);
		u = aOverb2.multiply(b2);
		assertTrue(u.getBaseSymbol().equals(aTimesa.getBaseSymbol()));

		symbol = axb.getSymbol() + "^-2";
		UnitOfMeasure axbm2 = sys.createPowerUOM(UnitType.UNCLASSIFIED, "", symbol, "", axb, -2);
		uom = axbm2.multiply(axb2);
		assertTrue(uom.getBaseSymbol().equals(sys.getOne().getSymbol()));
		UnitOfMeasure cxd = sys.createProductUOM(UnitType.UNCLASSIFIED, "", "c.D", "", c, x);
		final char MULT = 0xB7;
		StringBuffer sb = new StringBuffer();
		sb.append("cUnit").append(MULT).append("xUnit");
		String str = sb.toString();
		assertTrue(cxd.getBaseSymbol().indexOf(str) != -1);

		UnitOfMeasure abdivcd = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "", "(a.b)/(c.D)", "", axb, cxd);
		assertTrue(abdivcd.getDividend().equals(axb));
		assertTrue(abdivcd.getDivisor().equals(cxd));

		UnitOfMeasure cde = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "", "(c.D)/(e)", "", cxd, e);
		sb = new StringBuffer();
		sb.append("cUnit").append(MULT).append("xUnit/eUnit");
		str = sb.toString();
		assertTrue(cde.getBaseSymbol().indexOf(str) != -1);

		u = sys.createScalarUOM(UnitType.UNCLASSIFIED, null, "not null", null);
		assertTrue(u.toString() != null);
	}

	@Test
	public void testUSUnits() throws Exception {

		UnitOfMeasure foot = sys.getUOM(Unit.FOOT);
		UnitOfMeasure gal = sys.getUOM(Unit.US_GALLON);
		UnitOfMeasure flush = sys.createScalarUOM(UnitType.UNCLASSIFIED, "flush", "flush", "");
		UnitOfMeasure gpf = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "gal per flush", "gpf", "", gal, flush);
		UnitOfMeasure velocity = sys.getUOM(Unit.FEET_PER_SEC);

		UnitOfMeasure litre = sys.getUOM(Unit.LITRE);
		UnitOfMeasure lpf = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "litre per flush", "lpf", "", litre, flush);

		double bd = gpf.getConversionFactor(lpf);
		assertTrue(isCloseTo(bd, 3.785411784, DELTA6));

		bd = lpf.getConversionFactor(gpf);
		assertTrue(isCloseTo(bd, 0.2641720523581484, DELTA6));

		// inversions
		UnitOfMeasure u = foot.invert();
		assertTrue(u.getSymbol().equals("1/ft"));

		u = u.multiply(foot);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getBaseSymbol()));

		u = velocity.invert();
		assertTrue(u.getSymbol().equals("s/ft"));

		u = u.multiply(velocity);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getBaseSymbol()));

	}

	@Test
	public void testImperialUnits() throws Exception {

		UnitOfMeasure impGal = sys.getUOM(Unit.BR_GALLON);
		UnitOfMeasure impPint = sys.getUOM(Unit.BR_PINT);
		UnitOfMeasure impOz = sys.getUOM(Unit.BR_FLUID_OUNCE);

		UnitOfMeasure usGal = sys.getUOM(Unit.US_GALLON);
		UnitOfMeasure usPint = sys.getUOM(Unit.US_PINT);

		UnitOfMeasure litre = sys.getUOM(Unit.LITRE);
		UnitOfMeasure m3 = sys.getUOM(Unit.CUBIC_METRE);

		double bd = impGal.getConversionFactor(litre);
		assertTrue(isCloseTo(bd, 4.54609, DELTA6));

		bd = litre.getConversionFactor(impGal);
		assertTrue(isCloseTo(bd, 0.2199692482990878, DELTA6));

		bd = impGal.getConversionFactor(usGal);
		assertTrue(isCloseTo(bd, 1.200949925504855, DELTA6));

		bd = usGal.getConversionFactor(impGal);
		assertTrue(isCloseTo(bd, 0.8326741846289888, DELTA6));

		bd = impGal.getConversionFactor(impPint);
		assertTrue(isCloseTo(bd, 8, DELTA6));

		bd = impPint.getConversionFactor(impGal);
		assertTrue(isCloseTo(bd, 0.125, DELTA6));

		bd = usGal.getConversionFactor(usPint);
		assertTrue(isCloseTo(bd, 8, DELTA6));

		bd = usPint.getConversionFactor(usGal);
		assertTrue(isCloseTo(bd, 0.125, DELTA6));

		bd = impOz.getConversionFactor(m3);
		assertTrue(isCloseTo(bd, 28.4130625E-06, DELTA6));

		bd = m3.getConversionFactor(impOz);
		assertTrue(isCloseTo(bd, 35195.07972785405, DELTA6));

	}

	@Test
	public void testOperations() throws Exception {
		sys.clearCache();

		UnitOfMeasure u = null;
		UnitOfMeasure hour = sys.getHour();
		UnitOfMeasure metre = sys.getUOM(Unit.METRE);
		UnitOfMeasure m2 = sys.getUOM(Unit.SQUARE_METRE);

		// multiply2
		UnitOfMeasure velocity = sys.createScalarUOM(UnitType.VELOCITY, "meter/hr", "meter/hr", "");
		velocity.setConversion(1d / 3600d, sys.getUOM(Unit.METRE_PER_SEC));

		double sf = 1d / 3600d;
		assertTrue(isCloseTo(velocity.getScalingFactor(), sf, DELTA6));
		assertTrue(velocity.getAbscissaUnit().equals(sys.getUOM(Unit.METRE_PER_SEC)));
		assertTrue(isCloseTo(velocity.getOffset(), 0.0d, DELTA6));

		u = velocity.multiply(hour);
		double bd = u.getConversionFactor(metre);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		u = hour.multiply(velocity);
		bd = u.getConversionFactor(metre);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		u = metre.multiply(metre);
		bd = u.getConversionFactor(m2);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));
		assertTrue(u.getBaseSymbol().equals(m2.getBaseSymbol()));

		// divide2
		u = metre.divide(hour);
		bd = u.getConversionFactor(velocity);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		u = u.multiply(hour);
		assertTrue(u.getBaseSymbol().equals(metre.getBaseSymbol()));

		// invert
		UnitOfMeasure vinvert = velocity.invert();
		sf = vinvert.getScalingFactor();
		assertTrue(sf == 1d);

		// max symbol length
		Quantity v = null;
		Quantity h = null;
		UnitOfMeasure mpc = sys.getUOM(Prefix.MEGA, sys.getUOM(Unit.PARSEC));
		Quantity d = new Quantity(10.0d, mpc);
		Quantity h0 = sys.getQuantity(Constant.HUBBLE_CONSTANT);

		for (int i = 0; i < 3; i++) {
			v = h0.multiply(d);
			d = v.divide(h0);
			h = v.divide(d);
		}
		assertTrue(h.getUOM().getSymbol().length() < 16);

		// conflict with 1/s
		sys.unregisterUnit(h0.getUOM());
	}

	@Test
	public void testTime() throws Exception {

		UnitOfMeasure s2 = sys.getUOM(Unit.SQUARE_SECOND);
		UnitOfMeasure second = sys.getSecond();
		UnitOfMeasure min = sys.getMinute();
		UnitOfMeasure hour = sys.getHour();
		UnitOfMeasure msec = sys.getUOM(Prefix.MILLI, second);
		UnitOfMeasure min2 = sys.createPowerUOM(UnitType.TIME_SQUARED, "sqMin", "min^2", null, min, 2);

		double factor = second.getConversionFactor(msec);
		assertTrue(isCloseTo(factor, 1000, DELTA6));

		assertTrue(isCloseTo(second.getScalingFactor(), 1.0d, DELTA6));
		assertTrue(second.getAbscissaUnit().equals(second));
		assertTrue(isCloseTo(second.getOffset(), 0.0d, DELTA6));

		double bd = hour.getConversionFactor(second);

		UnitOfMeasure u = second.multiply(second);

		assertTrue(isCloseTo(u.getScalingFactor(), 1.0d, DELTA6));
		assertTrue(isCloseTo(u.getOffset(), 0.0d, DELTA6));
		assertTrue(u.equals(s2));

		u = second.divide(second);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getSymbol()));

		Quantity q1 = new Quantity(1.0d, u);
		Quantity q2 = q1.convert(sys.getOne());
		assertTrue(isCloseTo(q2.getAmount(), 1.0d, DELTA6));
		assertTrue(q2.getUOM().equals(sys.getOne()));

		u = second.invert();

		assertTrue(u.getDividend().equals(sys.getOne()));
		assertTrue(u.getDivisor().equals(second));

		u = min.divide(second);
		factor = u.getConversionFactor(sys.getOne());
		assertTrue(isCloseTo(factor, 60d, DELTA6));
		assertTrue(isCloseTo(u.getOffset(), 0.0d, DELTA6));

		UnitOfMeasure uom = u.multiply(second);
		bd = uom.getConversionFactor(min);
		assertTrue(uom.getBaseSymbol().equals(min.getBaseSymbol()));
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		q1 = new Quantity(10.0d, u);
		q2 = q1.convert(sys.getOne());
		assertTrue(isCloseTo(q2.getAmount(), 600, DELTA6));
		assertTrue(q2.getUOM().equals(sys.getOne()));

		// multiply2
		u = min.multiply(min);
		assertTrue(isCloseTo(u.getScalingFactor(), 3600, DELTA6));
		assertTrue(u.getAbscissaUnit().getBaseSymbol().equals(s2.getBaseSymbol()));
		assertTrue(isCloseTo(u.getOffset(), 0.0d, DELTA6));

		q1 = new Quantity(10.0d, u);
		q2 = q1.convert(s2);
		assertTrue(isCloseTo(q2.getAmount(), 36000, DELTA6));
		assertTrue(q2.getUOM().equals(s2));

		q2 = q2.convert(min2);
		assertTrue(isCloseTo(q2.getAmount(), 10, DELTA6));

		u = min.multiply(second);
		assertTrue(isCloseTo(u.getScalingFactor(), 60, DELTA6));
		assertTrue(u.getAbscissaUnit().getBaseSymbol().equals(s2.getBaseSymbol()));

		u = second.multiply(min);
		bd = u.getConversionFactor(s2);
		assertTrue(isCloseTo(bd, 60, DELTA6));

	}

	@Test
	public void testSymbolCache() throws Exception {
		UnitOfMeasure uom = sys.getUOM(Unit.KILOGRAM);
		UnitOfMeasure other = sys.getUOM(uom.getSymbol());
		assertTrue(uom.equals(other));

		other = sys.getUOM(uom.getBaseSymbol());
		assertTrue(uom.equals(other));

		uom = sys.getUOM(Prefix.CENTI, sys.getUOM(Unit.METRE));
		other = sys.getUOM(uom.getSymbol());
		assertTrue(uom.equals(other));

		other = sys.getUOM(uom.getBaseSymbol());
		assertTrue(uom.getBaseSymbol().equals(other.getBaseSymbol()));

		uom = sys.getUOM(Unit.NEWTON);
		other = sys.getUOM(uom.getSymbol());
		assertTrue(uom.equals(other));

		other = sys.getBaseUOM(uom.getBaseSymbol());
		assertTrue(uom.equals(other));
	}

	@Test
	public void testBaseSymbols() throws Exception {
		sys.clearCache();

		final char deg = 0xB0;
		final char times = 0xB7;
		final char sq = 0xB2;
		final char cu = 0xB3;
		StringBuffer sb = new StringBuffer();

		UnitOfMeasure metre = sys.getUOM(Unit.METRE);

		String symbol = sys.getOne().getBaseSymbol();
		assertTrue(symbol.equals("1"));

		symbol = sys.getSecond().getBaseSymbol();
		assertTrue(symbol.equals("s"));

		symbol = metre.getBaseSymbol();
		assertTrue(symbol.equals("m"));

		UnitOfMeasure mm = sys.getUOM(Prefix.MILLI, metre);
		assertTrue(Prefix.MILLI.getFactor() > 0);

		symbol = mm.getSymbol();
		assertTrue(symbol.equals("mm"));

		symbol = mm.getBaseSymbol();
		assertTrue(symbol.equals("m"));

		symbol = sys.getUOM(Unit.SQUARE_METRE).getBaseSymbol();
		assertTrue(symbol.equals("m" + sq));

		symbol = sys.getUOM(Unit.CUBIC_METRE).getBaseSymbol();
		assertTrue(symbol.equals("m" + cu));

		symbol = sys.getUOM(Unit.KELVIN).getBaseSymbol();
		sb.append(deg).append('K');
		assertTrue(symbol.equals(sb.toString()));

		symbol = sys.getUOM(Unit.CELSIUS).getBaseSymbol();
		assertTrue(symbol.equals(sb.toString()));

		symbol = sys.getUOM(Unit.CELSIUS).getSymbol();
		sb = new StringBuffer();
		sb.append(deg).append('C');
		assertTrue(symbol.equals(sb.toString()));

		symbol = sys.getUOM(Unit.GRAM).getBaseSymbol();
		assertTrue(symbol.equals("kg"));

		UnitOfMeasure kg = sys.getUOM(Unit.KILOGRAM);
		symbol = kg.getSymbol();
		assertTrue(symbol.equals("kg"));

		symbol = kg.getBaseSymbol();
		assertTrue(symbol.equals("kg"));

		symbol = sys.getUOM(Unit.CUBIC_METRE).getBaseSymbol();
		assertTrue(symbol.equals("m" + cu));

		symbol = sys.getUOM(Unit.LITRE).getBaseSymbol();
		assertTrue(symbol.equals("m" + cu));

		symbol = sys.getUOM(Unit.NEWTON).getBaseSymbol();
		sb = new StringBuffer();
		sb.append("kg").append(times).append("m/s").append(sq);
		assertTrue(symbol.equals(sb.toString()));

		symbol = sys.getUOM(Unit.WATT).getBaseSymbol();
		sb = new StringBuffer();
		sb.append("kg").append(times).append("m").append(sq).append("/s").append((char) 0xB3);
		assertTrue(symbol.equals(sb.toString()));

		symbol = sys.getUOM(Unit.NEWTON_METRE).getBaseSymbol();
		sb = new StringBuffer();
		sb.append("kg").append(times).append("m").append(sq).append("/s").append(sq);
		assertTrue(symbol.equals(sb.toString()));

		symbol = sys.getUOM(Unit.VOLT).getBaseSymbol();
		sb = new StringBuffer();
		sb.append("kg").append(times).append("m").append(sq).append("/(A").append(times).append("s").append(cu)
				.append(')');
		assertTrue(symbol.equals(sb.toString()));

		symbol = sys.getUOM(Unit.OHM).getBaseSymbol();
		sb = new StringBuffer();
		sb.append("kg").append(times).append("m").append(sq).append("/(A").append(sq).append(times).append("s")
				.append(cu).append(')');
		assertTrue(symbol.equals(sb.toString()));

		symbol = sys.getUOM(Unit.WEBER).getBaseSymbol();
		sb = new StringBuffer();
		sb.append("kg").append(times).append("m").append(sq).append("/(A").append(times).append("s").append(sq)
				.append(')');
		assertTrue(symbol.equals(sb.toString()));

		symbol = sys.getUOM(Unit.MOLE).getSymbol();
		assertTrue(symbol.equals("mol"));

		symbol = sys.getUOM(Unit.RADIAN).getSymbol();
		assertTrue(symbol.equals("rad"));

		symbol = sys.getUOM(Unit.RADIAN).getBaseSymbol();
		assertTrue(symbol.equals("1"));

		symbol = sys.getUOM(Unit.STERADIAN).getSymbol();
		assertTrue(symbol.equals("sr"));

		symbol = sys.getUOM(Unit.STERADIAN).getBaseSymbol();
		assertTrue(symbol.equals("1"));

		symbol = sys.getUOM(Unit.CANDELA).getBaseSymbol();
		assertTrue(symbol.equals("cd"));

		symbol = sys.getUOM(Unit.LUMEN).getBaseSymbol();
		assertTrue(symbol.equals("cd"));

		symbol = sys.getUOM(Unit.LUMEN).getSymbol();
		assertTrue(symbol.equals("lm"));

		symbol = sys.getUOM(Unit.LUX).getBaseSymbol();
		sb = new StringBuffer();
		sb.append("cd/m").append((char) 0xB2);
		assertTrue(symbol.equals(sb.toString()));

		symbol = sys.getUOM(Unit.HERTZ).getBaseSymbol();
		assertTrue(symbol.equals("1/s"));

		symbol = sys.getUOM(Unit.BECQUEREL).getBaseSymbol();
		assertTrue(symbol.equals("1/s"));

		symbol = sys.getUOM(Unit.BECQUEREL).getSymbol();
		assertTrue(symbol.equals("Bq"));

		symbol = sys.getUOM(Unit.GRAY).getBaseSymbol();
		sb = new StringBuffer();
		sb.append("m").append((char) 0xB2).append("/s").append((char) 0xB2);
		assertTrue(symbol.equals(sb.toString()));

		assertTrue(sys.getUOM(Unit.KATAL).getBaseSymbol().equals("mol/s"));
	}

	@Test
	public void testConversions1() throws Exception {
		sys.clearCache();

		UnitOfMeasure m = sys.getUOM(Unit.METRE);
		UnitOfMeasure cm = sys.getUOM(Prefix.CENTI, m);
		UnitOfMeasure N = sys.getUOM(Unit.NEWTON);
		UnitOfMeasure Nm = sys.getUOM(Unit.NEWTON_METRE);
		UnitOfMeasure mps = sys.getUOM(Unit.METRE_PER_SEC);
		UnitOfMeasure sqm = sys.getUOM(Unit.SQUARE_METRE);
		UnitOfMeasure mm = sys.createProductUOM(UnitType.AREA, "mxm", "mTimesm", "", m, m);
		UnitOfMeasure mcm = sys.createProductUOM(UnitType.AREA, "mxcm", "mxcm", "", m, cm);
		UnitOfMeasure s2 = sys.getUOM(Unit.SQUARE_SECOND);
		UnitOfMeasure minOverSec = sys.createQuotientUOM(UnitType.TIME, "minsec", "min/sec", "", sys.getMinute(),
				sys.getSecond());

		UnitOfMeasure minOverSecTimesSec = sys.createProductUOM(UnitType.TIME, "minOverSecTimesSec",
				"minOverSecTimesSec", "minOverSecTimesSec", minOverSec, sys.getSecond());

		UnitOfMeasure in = sys.getUOM(Unit.INCH);
		UnitOfMeasure ft = sys.getUOM(Unit.FOOT);
		UnitOfMeasure lbf = sys.getUOM(Unit.POUND_FORCE);
		UnitOfMeasure fph = sys.createQuotientUOM(UnitType.VELOCITY, "fph", "ft/hr", "feet per hour", ft,
				sys.getHour());
		UnitOfMeasure ftlb = sys.getUOM(Unit.FOOT_POUND_FORCE);
		UnitOfMeasure sqft = sys.getUOM(Unit.SQUARE_FOOT);

		UnitOfMeasure oneDivSec = sys.getOne().divide(sys.getSecond());
		UnitOfMeasure inverted = oneDivSec.invert();
		assertTrue(inverted.equals(sys.getSecond()));

		UnitOfMeasure perSec = sys.createPowerUOM(UnitType.TIME, "per second", "perSec", null, sys.getSecond(), -1);
		UnitOfMeasure mult = perSec.multiply(sys.getUOM(Unit.SECOND));
		assertTrue(mult.getBaseSymbol().equals(sys.getUOM(Unit.ONE).getSymbol()));

		UnitOfMeasure u = sys.getSecond().invert();
		assertTrue(u.getScalingFactor() == oneDivSec.getScalingFactor());

		inverted = u.invert();
		assertTrue(inverted.equals(sys.getSecond()));

		UnitOfMeasure oneOverSec = sys.getBaseUOM("1/s");
		assertTrue(oneOverSec.getBaseSymbol().equals(oneDivSec.getBaseSymbol()));

		inverted = oneOverSec.invert();
		assertTrue(inverted.getBaseSymbol().equals(sys.getSecond().getBaseSymbol()));

		UnitOfMeasure minTimesSec = sys.createProductUOM(UnitType.TIME_SQUARED, "minsec", "minxsec",
				"minute times a second", sys.getMinute(), sys.getSecond());

		UnitOfMeasure sqMin = sys.getUOM("min^2");
		if (sqMin == null) {
			sqMin = sys.createPowerUOM(UnitType.TIME_SQUARED, "square minute", "min^2", null, sys.getUOM(Unit.MINUTE),
					2);
		}

		UnitOfMeasure perMin = sys.createPowerUOM(UnitType.TIME, "per minute", "perMin", null, sys.getMinute(), -1);

		UnitOfMeasure perMin2 = sys.createPowerUOM(UnitType.TIME, "per minute squared", "perMin^2", null,
				sys.getMinute(), -2);

		u = perMin2.invert();
		assertTrue(u.getBaseSymbol().equals(sqMin.getBaseSymbol()));

		u = perMin.invert();
		double bd = u.getConversionFactor(sys.getMinute());
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		bd = u.getConversionFactor(sys.getSecond());
		assertTrue(isCloseTo(bd, 60, DELTA6));

		try {
			m.getConversionFactor(null);
			fail("null");
		} catch (Exception e) {
		}

		try {
			m.multiply(null);
			fail("null");
		} catch (Exception e) {
		}

		// scalar
		bd = m.getConversionFactor(m);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));
		assertTrue(m.equals(m));

		bd = m.getConversionFactor(cm);
		assertTrue(isCloseTo(bd, 100, DELTA6));
		assertTrue(m.equals(m));
		assertTrue(cm.equals(cm));
		assertTrue((!m.equals(cm)));

		bd = m.getConversionFactor(cm);
		assertTrue(isCloseTo(bd, 100, DELTA6));

		bd = cm.getConversionFactor(m);
		assertTrue(isCloseTo(bd, 0.01, DELTA6));

		bd = m.getConversionFactor(cm);
		assertTrue(isCloseTo(bd, 100, DELTA6));

		bd = m.getConversionFactor(in);
		assertTrue(isCloseTo(bd, 39.37007874015748, DELTA6));

		bd = in.getConversionFactor(m);
		assertTrue(isCloseTo(bd, 0.0254, DELTA6));

		bd = m.getConversionFactor(ft);
		assertTrue(isCloseTo(bd, 3.280839895013123, DELTA6));

		bd = ft.getConversionFactor(m);
		assertTrue(isCloseTo(bd, 0.3048, DELTA6));

		Quantity g = sys.getQuantity(Constant.GRAVITY).convert(sys.getUOM(Unit.FEET_PER_SEC_SQUARED));
		bd = g.getAmount();
		assertTrue(isCloseTo(bd, 32.17404855, DELTA6));

		bd = lbf.getConversionFactor(N);
		assertTrue(isCloseTo(bd, 4.448221615, DELTA6));

		bd = N.getConversionFactor(lbf);
		assertTrue(isCloseTo(bd, 0.2248089430997105, DELTA6));

		// product
		bd = Nm.getConversionFactor(ftlb);
		assertTrue(isCloseTo(bd, 0.7375621492772656, DELTA6));

		bd = ftlb.getConversionFactor(Nm);
		assertTrue(isCloseTo(bd, 1.3558179483314004, DELTA6));

		// quotient
		UnitOfMeasure one = sys.getOne();
		bd = minOverSec.getConversionFactor(one);
		assertTrue(isCloseTo(bd, 60, DELTA6));

		bd = one.getConversionFactor(minOverSec);
		assertTrue(isCloseTo(bd, 0.0166666666666667, DELTA6));

		bd = mps.getConversionFactor(fph);
		assertTrue(isCloseTo(bd, 11811.02362204724, DELTA6));

		bd = fph.getConversionFactor(mps);
		assertTrue(isCloseTo(bd, 8.46666666666667E-05, DELTA6));

		// power
		bd = sqm.getConversionFactor(sqft);
		assertTrue(isCloseTo(bd, 10.76391041670972, DELTA6));

		bd = sqft.getConversionFactor(sqm);
		assertTrue(isCloseTo(bd, 0.09290304, DELTA6));

		// mixed
		bd = mm.getConversionFactor(sqm);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		bd = sqm.getConversionFactor(mm);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		bd = mcm.getConversionFactor(sqm);
		assertTrue(isCloseTo(bd, 0.01, DELTA6));

		bd = sqm.getConversionFactor(mcm);
		assertTrue(isCloseTo(bd, 100, DELTA6));

		bd = minTimesSec.getConversionFactor(s2);
		assertTrue(isCloseTo(bd, 60, DELTA6));

		bd = s2.getConversionFactor(minTimesSec);
		assertTrue(isCloseTo(bd, 0.0166666666666667, DELTA6));

		bd = minTimesSec.getConversionFactor(sqMin);
		assertTrue(isCloseTo(bd, 0.0166666666666667, DELTA6));

		bd = sqMin.getConversionFactor(minTimesSec);
		assertTrue(isCloseTo(bd, 60, DELTA6));

		bd = minOverSecTimesSec.getConversionFactor(sys.getSecond());
		assertTrue(isCloseTo(bd, 60, DELTA6));

	}

	@Test
	public void testConversions2() throws Exception {
		double bd = 0;

		sys.unregisterUnit(sys.getUOM(Unit.CUBIC_INCH));
		UnitOfMeasure ft = sys.getUOM(Unit.FOOT);
		UnitOfMeasure ft2 = sys.getUOM(Unit.SQUARE_FOOT);
		UnitOfMeasure ft3 = sys.getUOM(Unit.CUBIC_FOOT);

		UnitOfMeasure cubicFt = ft2.multiply(ft);
		bd = cubicFt.getConversionFactor(ft3);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		UnitOfMeasure m3 = sys.getUOM(Unit.CUBIC_METRE);
		UnitOfMeasure degree = sys.getUOM(Unit.DEGREE);
		UnitOfMeasure arcsec = sys.getUOM(Unit.ARC_SECOND);
		UnitOfMeasure radian = sys.getUOM(Unit.RADIAN);
		UnitOfMeasure kgPerM3 = sys.getUOM(Unit.KILOGRAM_PER_CU_METRE);
		UnitOfMeasure mps = sys.getUOM(Unit.METRE_PER_SEC);
		UnitOfMeasure pascal = sys.getUOM(Unit.PASCAL);
		UnitOfMeasure s2 = sys.getUOM(Unit.SQUARE_SECOND);
		UnitOfMeasure joule = sys.getUOM(Unit.JOULE);
		UnitOfMeasure rpm = sys.getUOM(Unit.REV_PER_MIN);
		UnitOfMeasure rps = sys.getUOM(Unit.RAD_PER_SEC);
		UnitOfMeasure m3s = sys.getUOM(Unit.CUBIC_METRE_PER_SEC);
		UnitOfMeasure ms2 = sys.getUOM(Unit.METRE_PER_SEC_SQUARED);

		UnitOfMeasure lbm = sys.getUOM(Unit.POUND_MASS);
		UnitOfMeasure acreFoot = sys.createProductUOM(UnitType.VOLUME, "acreFoot", "ac-ft", "", sys.getUOM(Unit.ACRE),
				sys.getUOM(Unit.FOOT));
		UnitOfMeasure lbmPerFt3 = sys.createQuotientUOM(UnitType.DENSITY, "lbmPerFt3", "lbm/ft^3", null, lbm, ft3);
		UnitOfMeasure fps = sys.getUOM(Unit.FEET_PER_SEC);
		UnitOfMeasure knot = sys.getUOM(Unit.KNOT);
		UnitOfMeasure btu = sys.getUOM(Unit.BTU);

		UnitOfMeasure miphs = sys.createScalarUOM(UnitType.ACCELERATION, "mph/sec", "mi/hr-sec",
				"mile per hour per second");
		miphs.setConversion(1.466666666666667, sys.getUOM(Unit.FEET_PER_SEC_SQUARED));

		UnitOfMeasure inHg = sys.createScalarUOM(UnitType.PRESSURE, "inHg", "inHg", "inHg");
		inHg.setConversion(3386.389, pascal);

		Quantity atm = new Quantity(1.0d, Unit.ATMOSPHERE).convert(Unit.PASCAL);
		assertTrue(isCloseTo(atm.getAmount(), 101325, DELTA6));

		UnitOfMeasure ft2ft = sys.createProductUOM(UnitType.VOLUME, "ft2ft", "ft2ft", null, ft2, ft);

		UnitOfMeasure hrsec = sys.createScalarUOM(UnitType.TIME_SQUARED, "", "hr.sec", "");
		hrsec.setConversion(3600d, sys.getUOM(Unit.SQUARE_SECOND));
		bd = hrsec.getConversionFactor(s2);
		assertTrue(isCloseTo(bd, 3600, DELTA6));

		bd = s2.getConversionFactor(hrsec);
		assertTrue(isCloseTo(bd, 2.777777777777778E-04, DELTA6));

		bd = ft2ft.getConversionFactor(m3);
		assertTrue(isCloseTo(bd, 0.028316846592, DELTA6));

		bd = m3.getConversionFactor(ft2ft);
		assertTrue(isCloseTo(bd, 35.31466672148859, DELTA6));

		bd = acreFoot.getConversionFactor(m3);
		assertTrue(isCloseTo(bd, 1233.48183754752, DELTA6));

		bd = m3.getConversionFactor(acreFoot);
		assertTrue(isCloseTo(bd, 8.107131937899125E-04, DELTA6));

		bd = degree.getConversionFactor(radian);
		assertTrue(isCloseTo(bd, 0.01745329251994329, DELTA6));

		bd = radian.getConversionFactor(degree);
		assertTrue(isCloseTo(bd, 57.29577951308264, DELTA6));

		bd = arcsec.getConversionFactor(degree);
		assertTrue(isCloseTo(bd, 2.777777777777778E-4, DELTA6));

		bd = degree.getConversionFactor(arcsec);
		assertTrue(isCloseTo(bd, 3600, DELTA6));

		bd = lbmPerFt3.getConversionFactor(kgPerM3);
		assertTrue(isCloseTo(bd, 16.01846337, DELTA6));

		bd = kgPerM3.getConversionFactor(lbmPerFt3);
		assertTrue(isCloseTo(bd, 0.0624279605915783, DELTA6));

		bd = rpm.getConversionFactor(rps);
		assertTrue(isCloseTo(bd, 0.104719755, DELTA6));

		bd = rps.getConversionFactor(rpm);
		assertTrue(isCloseTo(bd, 9.549296596425383, DELTA6));

		bd = mps.getConversionFactor(fps);
		assertTrue(isCloseTo(bd, 3.280839895013123, DELTA6));

		bd = fps.getConversionFactor(mps);
		assertTrue(isCloseTo(bd, 0.3048, DELTA6));

		bd = knot.getConversionFactor(mps);
		assertTrue(isCloseTo(bd, 0.5147733333333333, DELTA6));

		bd = mps.getConversionFactor(knot);
		assertTrue(isCloseTo(bd, 1.942602569415665, DELTA6));

		UnitOfMeasure usGal = sys.getUOM(Unit.US_GALLON);
		UnitOfMeasure gph = sys.createQuotientUOM(UnitType.VOLUMETRIC_FLOW, "gph", "gal/hr", "gallons per hour", usGal,
				sys.getHour());

		bd = gph.getConversionFactor(m3s);
		assertTrue(isCloseTo(bd, 1.051503273E-06, DELTA6));

		bd = m3s.getConversionFactor(gph);
		assertTrue(isCloseTo(bd, 951019.3884893342, DELTA6));

		bd = miphs.getConversionFactor(ms2);
		assertTrue(isCloseTo(bd, 0.44704, DELTA6));

		bd = ms2.getConversionFactor(miphs);
		assertTrue(isCloseTo(bd, 2.236936292054402, DELTA6));

		bd = pascal.getConversionFactor(inHg);
		assertTrue(isCloseTo(bd, 2.952998016471232E-04, DELTA6));

		bd = inHg.getConversionFactor(pascal);
		assertTrue(isCloseTo(bd, 3386.389, DELTA6));

		bd = atm.convert(inHg).getAmount();
		assertTrue(isCloseTo(bd, 29.92125240189478, DELTA6));

		bd = inHg.getConversionFactor(atm.getUOM());
		assertTrue(isCloseTo(bd, 3386.389, DELTA6));

		bd = btu.getConversionFactor(joule);
		assertTrue(isCloseTo(bd, 1055.05585262, DELTA6));

		bd = joule.getConversionFactor(btu);
		assertTrue(isCloseTo(bd, 9.478171203133172E-04, DELTA6));

	}

	@Test
	public void testConversions3() throws Exception {

		UnitOfMeasure weber = sys.getUOM(Unit.WEBER);
		UnitOfMeasure coulomb = sys.getUOM(Unit.COULOMB);
		UnitOfMeasure second = sys.getSecond();
		UnitOfMeasure volt = sys.getUOM(Unit.VOLT);
		UnitOfMeasure watt = sys.getUOM(Unit.WATT);
		UnitOfMeasure amp = sys.getUOM(Unit.AMPERE);
		UnitOfMeasure farad = sys.getUOM(Unit.FARAD);
		UnitOfMeasure ohm = sys.getUOM(Unit.OHM);
		UnitOfMeasure henry = sys.getUOM(Unit.HENRY);
		UnitOfMeasure sr = sys.getUOM(Unit.STERADIAN);
		UnitOfMeasure cd = sys.getUOM(Unit.CANDELA);
		UnitOfMeasure lumen = sys.getUOM(Unit.LUMEN);
		UnitOfMeasure gray = sys.getUOM(Unit.GRAY);
		UnitOfMeasure sievert = sys.getUOM(Unit.SIEVERT);

		UnitOfMeasure WeberPerSec = sys.createQuotientUOM(UnitType.ELECTROMOTIVE_FORCE, "W/s", "W/s", null, weber,
				second);
		UnitOfMeasure WeberPerAmp = sys.createQuotientUOM(UnitType.ELECTRIC_INDUCTANCE, "W/A", "W/A", null, weber, amp);
		UnitOfMeasure fTimesV = sys.createProductUOM(UnitType.ELECTRIC_CHARGE, "FxV", "FxV", null, farad, volt);
		UnitOfMeasure WPerAmp = sys.createQuotientUOM(UnitType.ELECTROMOTIVE_FORCE, "Watt/A", "Watt/A", null, watt,
				amp);
		UnitOfMeasure VPerA = sys.createQuotientUOM(UnitType.ELECTRIC_RESISTANCE, "V/A", "V/A", null, volt, amp);
		UnitOfMeasure CPerV = sys.createQuotientUOM(UnitType.ELECTRIC_CAPACITANCE, "C/V", "C/V", null, coulomb, volt);
		UnitOfMeasure VTimesSec = sys.createProductUOM(UnitType.MAGNETIC_FLUX, "Vxs", "Vxs", null, volt, second);
		UnitOfMeasure cdTimesSr = sys.createProductUOM(UnitType.LUMINOUS_FLUX, "cdxsr", "cdxsr", null, cd, sr);

		double bd = fTimesV.getConversionFactor(coulomb);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		bd = coulomb.getConversionFactor(fTimesV);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		bd = WeberPerSec.getConversionFactor(volt);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		bd = volt.getConversionFactor(WeberPerSec);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		bd = volt.getConversionFactor(WPerAmp);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		bd = WPerAmp.getConversionFactor(volt);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		bd = ohm.getConversionFactor(VPerA);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		bd = VPerA.getConversionFactor(ohm);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		bd = farad.getConversionFactor(CPerV);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		bd = CPerV.getConversionFactor(farad);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		bd = weber.getConversionFactor(VTimesSec);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		bd = VTimesSec.getConversionFactor(weber);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		bd = henry.getConversionFactor(WeberPerAmp);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		bd = WeberPerAmp.getConversionFactor(henry);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		bd = lumen.getConversionFactor(cdTimesSr);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		bd = cdTimesSr.getConversionFactor(lumen);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		try {
			bd = gray.getConversionFactor(sievert);
			fail("No conversion");
		} catch (Exception e) {
		}

		try {
			bd = sievert.getConversionFactor(gray);
			fail("No conversion");
		} catch (Exception e) {
		}

	}

	@Test
	public void testConversions4() throws Exception {

		UnitOfMeasure K = sys.getUOM(Unit.KELVIN);
		UnitOfMeasure C = sys.getUOM(Unit.CELSIUS);

		UnitOfMeasure R = sys.getUOM(Unit.RANKINE);
		UnitOfMeasure F = sys.getUOM(Unit.FAHRENHEIT);

		double fiveNinths = 5d / 9d;
		double nineFifths = 1.8;

		// K to C
		double bd = K.getConversionFactor(C);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		bd = C.getConversionFactor(K);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		// R to F
		bd = R.getConversionFactor(F);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		bd = F.getConversionFactor(R);
		assertTrue(isCloseTo(bd, 1.0d, DELTA6));

		// C to F
		bd = F.getConversionFactor(C);
		assertTrue(isCloseTo(bd, fiveNinths, DELTA6));

		bd = C.getConversionFactor(F);
		assertTrue(isCloseTo(bd, nineFifths, DELTA6));

		// K to R
		bd = K.getConversionFactor(R);
		assertTrue(isCloseTo(bd, nineFifths, DELTA6));

		bd = F.getConversionFactor(K);
		assertTrue(isCloseTo(bd, fiveNinths, DELTA6));

		// invert diopters to metre
		Quantity from = new Quantity(10.0d, sys.getUOM(Unit.DIOPTER));
		Quantity inverted = from.invert();
		assertTrue(isCloseTo(inverted.getAmount(), 0.1, DELTA6));

		UnitOfMeasure u = sys.createPowerUOM(UnitType.UNCLASSIFIED, "t*4", "t*4", "", K, 4);
		assertTrue(u != null);

		try {
			u = C.multiply(C);
			fail("Can't multiply Celcius");
		} catch (Exception e) {
			// ignore
		}

		u = K.divide(K);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getBaseSymbol()));

		// hectare to acre
		UnitOfMeasure ha = sys.getUOM(Unit.HECTARE);
		from = new Quantity(1.0d, ha);
		Quantity to = from.convert(Unit.ACRE);
		assertTrue(isCloseTo(to.getAmount(), 2.47105, DELTA5));
	}

	@Test
	public void testPerformance() throws Exception {
		int its = 1000;

		UnitOfMeasure metre = sys.getUOM(Unit.METRE);
		UnitOfMeasure cm = sys.getUOM(Prefix.CENTI, sys.getUOM(Unit.METRE));
		UnitOfMeasure ft = sys.getUOM(Unit.FOOT);

		Quantity q1 = new Quantity(10d, metre);
		Quantity q2 = new Quantity(2d, cm);

		for (int i = 0; i < its; i++) {
			q1.add(q2);
		}

		for (int i = 0; i < its; i++) {
			q1.subtract(q2);
		}

		for (int i = 0; i < its; i++) {
			q1.multiply(q2);
		}

		for (int i = 0; i < its; i++) {
			q1.divide(q2);
		}

		for (int i = 0; i < its; i++) {
			q1.convert(ft);
		}
	}

	@Test
	public void testScaledUnits() throws Exception {

		UnitOfMeasure m = sys.getUOM(Unit.METRE);

		// mega metre
		UnitOfMeasure mm = sys.getUOM(Prefix.MEGA, m);

		Quantity qmm = new Quantity(1d, mm);
		Quantity qm = qmm.convert(m);
		assertTrue(isCloseTo(qm.getAmount(), 1.0E+06, DELTA6));

		UnitOfMeasure mm2 = sys.getUOM(Prefix.MEGA, m);
		assertTrue(mm.equals(mm2));

		// centilitre
		UnitOfMeasure litre = sys.getUOM(Unit.LITRE);
		UnitOfMeasure cL = sys.getUOM(Prefix.CENTI, litre);
		Quantity qL = new Quantity(1d, litre);
		Quantity qcL = qL.convert(cL);
		assertTrue(isCloseTo(qcL.getAmount(), 100, DELTA6));

		// a mega buck
		UnitOfMeasure buck = sys.createScalarUOM(UnitType.UNCLASSIFIED, "buck", "$", "one US dollar");
		UnitOfMeasure megabuck = sys.getUOM(Prefix.MEGA, buck);
		Quantity qmb = new Quantity(10d, megabuck);
		Quantity qb = qmb.convert(buck);
		assertTrue(isCloseTo(qb.getAmount(), 1.0E+07, DELTA6));

		// kilogram vs. scaled gram
		UnitOfMeasure kgm = sys.getUOM(Prefix.KILO, sys.getUOM(Unit.GRAM));
		UnitOfMeasure kg = sys.getUOM(Unit.KILOGRAM);
		assertTrue(kgm.equals(kg));

		// kilo and megabytes
		UnitOfMeasure kiB = sys.getUOM(Prefix.KIBI, sys.getUOM(Unit.BYTE));
		UnitOfMeasure miB = sys.getUOM(Prefix.MEBI, sys.getUOM(Unit.BYTE));
		Quantity qmB = new Quantity(1d, miB);
		Quantity qkB = qmB.convert(kiB);
		assertTrue(isCloseTo(qkB.getAmount(), 1024, DELTA6));
	}

	@Test
	public void testPowers() throws Exception {

		double bd = 0;

		Quantity q1 = null;
		Quantity q2 = null;

		UnitType t = null;

		UnitOfMeasure u = null;
		UnitOfMeasure u2 = null;

		UnitOfMeasure min = sys.getMinute();
		UnitOfMeasure s = sys.getSecond();
		UnitOfMeasure sm1 = s.invert();
		UnitOfMeasure s2 = sys.getUOM(Unit.SQUARE_SECOND);
		UnitOfMeasure min2 = sys.createPowerUOM(UnitType.TIME_SQUARED, "sqMin", "min'2", null, min, 2);
		UnitOfMeasure sqs = sys.createPowerUOM(UnitType.TIME_SQUARED, "sqSec", "s'2", null, s, 2);
		UnitOfMeasure sminus1 = sys.createPowerUOM(UnitType.UNCLASSIFIED, "sminus1", "s'-1", null, s, -1);
		UnitOfMeasure minminus1Q = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "minminus1Q", "minQ'-1", null,
				sys.getOne(), min);
		UnitOfMeasure minminus1 = sys.createPowerUOM(UnitType.UNCLASSIFIED, "minminus1", "min'-1", null, min, -1);
		UnitOfMeasure newton = sys.getUOM(Unit.NEWTON);
		UnitOfMeasure newtonm1 = sys.createPowerUOM(UnitType.UNCLASSIFIED, "Nminus1", "N'-1", null, newton, -1);
		UnitOfMeasure in = sys.getUOM(Unit.INCH);
		UnitOfMeasure ft = sys.getUOM(Unit.FOOT);
		UnitOfMeasure ftm1 = sys.createPowerUOM(UnitType.UNCLASSIFIED, "ftm1", "ft'-1", null, ft, -1);
		UnitOfMeasure inm1 = sys.createPowerUOM(UnitType.UNCLASSIFIED, "inm1", "in'-1", null, in, -1);
		UnitOfMeasure ui = sys.createScalarUOM(UnitType.UNCLASSIFIED, "ui", "ui", "");
		UnitOfMeasure uj = sys.createScalarUOM(UnitType.UNCLASSIFIED, "uj", "uj", "");
		UnitOfMeasure ixj = sys.createProductUOM(UnitType.UNCLASSIFIED, "ixj", "ixj", "", ui, uj);
		UnitOfMeasure oneOveri = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "oneOveri", "oneOveri", "", sys.getOne(),
				ui);
		UnitOfMeasure oneOverj = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "oneOverj", "oneOverj", "", sys.getOne(),
				uj);
		UnitOfMeasure ixjm1 = sys.createPowerUOM(UnitType.UNCLASSIFIED, "ixjm1", "ixjm1", "", ixj, -1);
		UnitOfMeasure hz = sys.getUOM(Unit.HERTZ);

		UnitOfMeasure ij = oneOveri.multiply(oneOverj);
		assertTrue(ij.getBaseSymbol().equals(ixjm1.getBaseSymbol()));

		bd = min2.getConversionFactor(s2);
		assertTrue(isCloseTo(bd, 3600, DELTA6));

		bd = s2.getConversionFactor(min2);
		assertTrue(isCloseTo(bd, 2.777777777777778e-4, DELTA6));

		u = sys.getBaseUOM(sm1.getSymbol());
		assertTrue(u != null);
		u = sys.getUOM(sm1.getBaseSymbol());

		u = sys.getOne().divide(min);
		bd = u.getScalingFactor();
		assertTrue(isCloseTo(bd, 1d / 60d, DELTA6));
		bd = u.getConversionFactor(sm1);
		assertTrue(isCloseTo(bd, 0.0166666666666667, DELTA6));

		u = ftm1.multiply(ft);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getSymbol()));

		u = ft.multiply(inm1);
		assertTrue(isCloseTo(u.getScalingFactor(), 12, DELTA6));

		u = inm1.multiply(ft);
		assertTrue(isCloseTo(u.getScalingFactor(), 12, DELTA6));

		u = s.multiply(minminus1);
		assertTrue(isCloseTo(u.getScalingFactor(), 0.0166666666666667, DELTA6));

		u = minminus1.multiply(s);
		assertTrue(isCloseTo(u.getScalingFactor(), 0.0166666666666667, DELTA6));

		u = s.multiply(minminus1Q);
		assertTrue(isCloseTo(u.getScalingFactor(), 0.0166666666666667, DELTA6));

		u = minminus1Q.multiply(s);
		assertTrue(isCloseTo(u.getScalingFactor(), 0.0166666666666667, DELTA6));

		u = ftm1.multiply(in);
		assertTrue(isCloseTo(u.getScalingFactor(), 1d / 12d, DELTA6));

		u = in.multiply(ftm1);
		assertTrue(isCloseTo(u.getScalingFactor(), 1d / 12d, DELTA6));

		u = newtonm1.multiply(newton);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getBaseSymbol()));

		u = newton.multiply(newtonm1);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getBaseSymbol()));

		u = minminus1.multiply(s);
		assertTrue(isCloseTo(u.getScalingFactor(), 1d / 60d, DELTA6));

		sys.unregisterUnit(sys.getUOM(Unit.HERTZ));
		UnitOfMeasure min1 = min.invert();
		bd = min1.getScalingFactor();
		assertTrue(isCloseTo(bd, 1, DELTA6));

		bd = sqs.getScalingFactor();
		assertTrue(isCloseTo(bd, 1, DELTA6));

		u = sminus1.multiply(s);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getSymbol()));

		u = sys.getOne().divide(min);
		bd = u.getScalingFactor();
		assertTrue(isCloseTo(bd, 1, DELTA6));
		bd = u.getConversionFactor(sm1);
		assertTrue(isCloseTo(bd, 0.0166666666666667, DELTA6));

		t = s2.getUnitType();

		t = min.getUnitType();

		u = sys.getOne().multiply(min);
		bd = u.getConversionFactor(s);

		t = min2.getUnitType();

		u = min2.divide(min);
		t = u.getUnitType();
		assertTrue(t.equals(UnitType.TIME));

		u = min.multiply(min);
		assertTrue(isCloseTo(u.getScalingFactor(), 3600, DELTA6));
		assertTrue(u.getAbscissaUnit().getBaseSymbol().equals(s2.getBaseSymbol()));
		assertTrue(isCloseTo(u.getOffset(), 0.0d, DELTA6));
		t = u.getUnitType();
		assertTrue(t.equals(UnitType.TIME_SQUARED));

		u2 = sys.getOne().divide(min);
		assertTrue(isCloseTo(u2.getScalingFactor(), 1.0d, DELTA6));

		q1 = new Quantity(1.0d, u2);
		q2 = q1.convert(hz);

		assertTrue(isCloseTo(q2.getAmount(), 0.0166666666666667, DELTA6));

		u = u2.multiply(u2);
		assertTrue(isCloseTo(u.getScalingFactor(), 1.0d, DELTA6));

		q1 = new Quantity(1.0d, u);
		q2 = q1.convert(s2.invert());
		assertTrue(isCloseTo(q2.getAmount(), 2.777777777777778e-4, DELTA6));

		u2 = u2.divide(min);
		q1 = new Quantity(1.0d, u2);
		q2 = q1.convert(s2.invert());
		assertTrue(isCloseTo(q2.getAmount(), 2.777777777777778e-4, DELTA6));

		u2 = u2.invert();
		assertTrue(u2.getBaseSymbol().equals(min2.getBaseSymbol()));

		q1 = new Quantity(10.0d, u2);
		bd = u2.getConversionFactor(s2);
		assertTrue(isCloseTo(bd, 3600, DELTA6));

		q2 = q1.convert(s2);
		assertTrue(q2.getUOM().equals(s2));
		assertTrue(isCloseTo(q2.getAmount(), 10 * 3600, DELTA6));

		bd = min.getConversionFactor(sys.getSecond());
		assertTrue(isCloseTo(bd, 60, DELTA6));

		u = q2.getUOM();
		bd = u.getConversionFactor(min2);
		assertTrue(isCloseTo(bd, 2.777777777777778e-4, DELTA6));

		q2 = q2.convert(min2);
		assertTrue(isCloseTo(q2.getAmount(), 10, DELTA6));
	}

	@Test
	public void testInversions() throws Exception {
		UnitOfMeasure uom = null;
		UnitOfMeasure inverted = null;
		UnitOfMeasure u = null;
		UnitOfMeasure metre = sys.getUOM(Unit.METRE);

		uom = sys.createPowerUOM(metre, -3);
		inverted = uom.invert();
		u = uom.multiply(inverted);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getSymbol()));

		uom = sys.createPowerUOM(metre, 2);
		inverted = uom.invert();
		u = uom.multiply(inverted);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getSymbol()));

		uom = sys.createPowerUOM(metre, -2);
		inverted = uom.invert();
		u = uom.multiply(inverted);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getSymbol()));

		uom = sys.createPowerUOM(metre, 2);
		inverted = uom.invert();
		u = uom.multiply(inverted);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getSymbol()));

		uom = sys.createPowerUOM(metre, 1);
		inverted = uom.invert();
		u = uom.multiply(inverted);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getSymbol()));

		uom = sys.createPowerUOM(metre, -1);
		inverted = uom.invert();
		u = uom.multiply(inverted);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getSymbol()));

		uom = sys.createPowerUOM(metre, -2);
		inverted = uom.invert();
		u = uom.multiply(inverted);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getSymbol()));

		uom = sys.createPowerUOM(metre, -4);
		inverted = uom.invert();
		u = uom.multiply(inverted);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getSymbol()));
	}

	@Test
	public void testMedicalUnits() throws Exception {
		// Equivalent
		UnitOfMeasure eq = sys.getUOM(Unit.EQUIVALENT);
		UnitOfMeasure litre = sys.getUOM(Unit.LITRE);
		UnitOfMeasure mEqPerL = sys.createQuotientUOM(UnitType.MOLAR_CONCENTRATION, "milliNormal", "mEq/L",
				"solute per litre of solvent ", sys.getUOM(Prefix.MILLI, eq), litre);
		Quantity testResult = new Quantity(4.9, mEqPerL);
		assertTrue(Double.valueOf(testResult.getAmount()).compareTo(3.5) == 1);
		assertTrue(Double.valueOf(testResult.getAmount()).compareTo(5.3) == -1);

		// Unit
		UnitOfMeasure u = sys.getUOM(Unit.UNIT);
		UnitOfMeasure katal = sys.getUOM(Unit.KATAL);
		Quantity q1 = new Quantity(1.0d, u);
		Quantity q2 = q1.convert(sys.getUOM(Prefix.NANO, katal));
		assertTrue(isCloseTo(q2.getAmount(), 16.666667, DELTA6));

		// blood cell counts
		UnitOfMeasure k = sys.getUOM(Prefix.KILO, sys.getOne());
		UnitOfMeasure uL = sys.getUOM(Prefix.MICRO, Unit.LITRE);
		UnitOfMeasure kul = sys.createQuotientUOM(UnitType.MOLAR_CONCENTRATION, "K/uL", "K/uL",
				"thousands per microlitre", k, uL);
		testResult = new Quantity(6.6, kul);
		assertTrue(Double.valueOf(testResult.getAmount()).compareTo(3.5) == 1);
		assertTrue(Double.valueOf(testResult.getAmount()).compareTo(12.5) == -1);

		UnitOfMeasure fL = sys.getUOM(Prefix.FEMTO, Unit.LITRE);
		testResult = new Quantity(90d, fL);
		assertTrue(Double.valueOf(testResult.getAmount()).compareTo(80d) == 1);
		assertTrue(Double.valueOf(testResult.getAmount()).compareTo(100d) == -1);

		// TSH
		UnitOfMeasure uIU = sys.getUOM(Prefix.MICRO, Unit.INTERNATIONAL_UNIT);
		UnitOfMeasure mL = sys.getUOM(Prefix.MILLI, Unit.LITRE);
		UnitOfMeasure uiuPerml = sys.createQuotientUOM(UnitType.MOLAR_CONCENTRATION, "uIU/mL", "uIU/mL",
				"micro IU per millilitre", uIU, mL);
		testResult = new Quantity(2.11, uiuPerml);
		assertTrue(Double.valueOf(testResult.getAmount()).compareTo(0.40) == 1);
		assertTrue(Double.valueOf(testResult.getAmount()).compareTo(5.50) == -1);

	}

	@Test
	public void testCategory() throws Exception {
		String category = "category";
		UnitOfMeasure m = sys.getUOM(Unit.METRE);
		m.setCategory(category);
		assertTrue(m.getCategory().equals(category));
	}

	@Test
	public void testMeasurementTypes() throws Exception {
		UnitOfMeasure m = sys.getUOM(Unit.METRE);
		UnitOfMeasure mps = sys.getUOM(Unit.METRE_PER_SEC);
		UnitOfMeasure n = sys.getUOM(Unit.NEWTON_METRE);
		UnitOfMeasure a = sys.getUOM(Unit.SQUARE_METRE);

		assertTrue(m.getMeasurementType().equals(UnitOfMeasure.MeasurementType.SCALAR));
		assertTrue(mps.getMeasurementType().equals(UnitOfMeasure.MeasurementType.QUOTIENT));
		assertTrue(n.getMeasurementType().equals(UnitOfMeasure.MeasurementType.PRODUCT));
		assertTrue(a.getMeasurementType().equals(UnitOfMeasure.MeasurementType.POWER));
	}

	@Test
	public void testScaling() throws Exception {
		// test scaling factors
		double sf = 0d;

		UnitOfMeasure second = sys.getSecond();
		UnitOfMeasure min = sys.getMinute();
		UnitOfMeasure s2 = sys.getUOM(Unit.SQUARE_SECOND);
		UnitOfMeasure msec = sys.getUOM(Prefix.MILLI, second);
		UnitOfMeasure k = sys.getUOM(Unit.KELVIN);
		UnitOfMeasure r = sys.getUOM(Unit.RANKINE);
		UnitOfMeasure m = sys.getUOM(Unit.METRE);
		UnitOfMeasure km = sys.getUOM(Prefix.KILO, m);

		sf = m.getConversionFactor(km);
		assertTrue(isCloseTo(sf, 0.001, DELTA6));

		UnitOfMeasure kinv = k.invert();
		sf = kinv.getScalingFactor();
		assertTrue(sf == 1d);

		sf = r.getConversionFactor(k);
		assertTrue(isCloseTo(sf, 5d / 9d, DELTA6));

		sf = k.getConversionFactor(r);
		assertTrue(isCloseTo(sf, 1.8, DELTA6));

		sf = second.getConversionFactor(msec);
		assertTrue(isCloseTo(sf, 1000, DELTA6));

		sf = min.getScalingFactor();
		assertTrue(sf == 60d);

		// inversions
		UnitOfMeasure mininv = min.invert();
		sf = mininv.getScalingFactor();
		assertTrue(sf == 1d / 60d);
		sf = mininv.getConversionFactor(sys.getUOM(Unit.HERTZ));
		assertTrue(sf == 1d / 60d);

		// quotient UOM
		UnitOfMeasure q = sys.createQuotientUOM(sys.getOne(), min);
		sf = q.getScalingFactor();
		assertTrue(sf == 1d);

		// power UOM
		UnitOfMeasure p = sys.createPowerUOM(min, -1);
		sf = p.getScalingFactor();
		assertTrue(sf == 1d);

		sf = p.getConversionFactor(sys.getUOM(Unit.HERTZ));
		assertTrue(sf == 1d / 60d);

		UnitOfMeasure u = p.invert();
		sf = u.getScalingFactor();
		assertTrue(sf == 60d);

		sf = min.getConversionFactor(u);
		assertTrue(isCloseTo(sf, 1.0d, DELTA6));

		sf = u.getConversionFactor(min);
		assertTrue(isCloseTo(sf, 1.0d, DELTA6));

		UnitOfMeasure min2 = mininv.invert();
		sf = min2.getScalingFactor();
		assertTrue(sf == 60d);

		// divisions
		UnitOfMeasure perMin = sys.getOne().divide(min);

		UnitOfMeasure num = perMin.getDividend();
		UnitOfMeasure denom = perMin.getDivisor();
		min2 = denom.divide(num);
		sf = min2.getScalingFactor();
		assertTrue(sf == 60d);

		sf = perMin.getScalingFactor();
		assertTrue(sf == 1d / 60d);

		UnitOfMeasure perMin1 = perMin.divide(sys.getOne());
		assertTrue(perMin1.equals(perMin));

		min2 = perMin.invert();
		sf = min2.getScalingFactor();
		assertTrue(sf == 60d);

		min2 = sys.getOne().divide(perMin);
		sf = min2.getScalingFactor();
		assertTrue(sf == 60d);

		int count = 4;
		UnitOfMeasure[] inversions = new UnitOfMeasure[count + 1];
		UnitOfMeasure[] divides = new UnitOfMeasure[count + 1];

		inversions[0] = min;
		divides[0] = min;
		for (int i = 0; i < count; i++) {
			inversions[i + 1] = inversions[i].invert();
			divides[i + 1] = sys.getOne().divide(divides[i]);
		}
		sf = inversions[count].getScalingFactor();
		assertTrue(sf == 60d);

		UnitOfMeasure last = null;
		for (int i = count; i > 0; i--) {
			last = divides[i].invert();
		}
		assertTrue(last.equals(min));

		// multiply
		UnitOfMeasure minsq = min.multiply(min);
		sf = minsq.getScalingFactor();
		assertTrue(sf == 3600d);

		sf = minsq.getConversionFactor(s2);
		assertTrue(isCloseTo(sf, 3600d, DELTA6));

		sf = s2.getConversionFactor(minsq);
		assertTrue(isCloseTo(sf, 1d / 3600d, DELTA6));

		// power of 2
		UnitOfMeasure p2 = sys.createPowerUOM(min, 2);
		sf = p2.getScalingFactor();
		assertTrue(sf == 1d);

		sf = p2.getConversionFactor(s2);
		assertTrue(sf == 3600d);

		sf = p2.getConversionFactor(minsq);
		assertTrue(sf == 1d);

		sf = minsq.getConversionFactor(p2);
		assertTrue(sf == 1d);
	}
}
