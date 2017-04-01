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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;
import org.point85.uom.Constant;
import org.point85.uom.Conversion;
import org.point85.uom.Prefix;
import org.point85.uom.Quantity;
import org.point85.uom.Unit;
import org.point85.uom.UnitOfMeasure;
import org.point85.uom.UnitType;

public class TestUnits extends BaseTest {

	@Test
	public void testPrefixes() {
		for (Prefix prefix : Prefix.values()) {
			assertTrue(prefix.getPrefixName().length() > 0);
			assertTrue(prefix.getSymbol().length() > 0);
			assertTrue(!prefix.getScalingFactor().equals(BigDecimal.ONE));
			assertTrue(prefix.toString().length() > 0);
		}
	}

	@Test
	public void testExceptions() throws Exception {

		UnitOfMeasure uom1 = sys.createScalarUOM(UnitType.UNCLASSIFIED, "uom1", "uom1", "");
		UnitOfMeasure uom2 = sys.createScalarUOM(UnitType.UNCLASSIFIED, "uom2", "uom2", "");
		UnitOfMeasure uom3 = sys.createScalarUOM(UnitType.UNCLASSIFIED, "uom3", "uom3", "");

		uom1.setConversion(new Conversion(BigDecimal.ONE, uom3, BigDecimal.TEN));
		uom2.setConversion(new Conversion(BigDecimal.ONE, uom3, BigDecimal.ONE));
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
			Quantity q = new Quantity(BigDecimal.TEN, Unit.METRE);
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
		Quantity q1 = new Quantity(BigDecimal.TEN, u);
		Quantity q2 = q1.convert(sys.getOne());
		assertThat(q2.getAmount(), closeTo(BigDecimal.TEN, DELTA6));
		assertTrue(q2.getUOM().equals(sys.getOne()));

		u = sys.createProductUOM(UnitType.UNCLASSIFIED, "1x1", "1x1", "", sys.getOne(), sys.getOne());
		BigDecimal bd = u.getConversionFactor(sys.getOne());
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		u = sys.createProductUOM(UnitType.UNCLASSIFIED, "1x1", "1x1", "", sys.getOne(), sys.getOne());
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getBaseSymbol()));

		u = sys.createPowerUOM(UnitType.UNCLASSIFIED, "1^2", "1^2", "", sys.getOne(), 2);
		bd = u.getConversionFactor(sys.getOne());
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		u = sys.createPowerUOM(UnitType.UNCLASSIFIED, "1^2", "1^2", "", sys.getOne(), 2);
		bd = u.getConversionFactor(sys.getOne());
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		u = sys.createPowerUOM(UnitType.UNCLASSIFIED, "1^0", "1^0", "", sys.getOne(), 0);
		bd = u.getConversionFactor(sys.getOne());
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		u = sys.createPowerUOM(UnitType.UNCLASSIFIED, "1^0", "1^0", "", sys.getOne(), 0);
		bd = u.getConversionFactor(sys.getOne());
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		UnitOfMeasure uno = sys.getOne();
		u = sys.createPowerUOM(UnitType.UNCLASSIFIED, "m^0", "m^0", "", sys.getUOM(Unit.METRE), 0);
		bd = u.getConversionFactor(uno);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));
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
		assertTrue(mult.equals(sys.getUOM(Unit.ONE)));

		UnitOfMeasure perMetre2 = m2.invert();
		u = sys.createPowerUOM(UnitType.UNCLASSIFIED, "m*-2", "m*-2", "", sys.getUOM(Unit.METRE), -2);
		assertTrue(u.getBaseSymbol().equals(perMetre2.getBaseSymbol()));

		u = sys.createPowerUOM(UnitType.UNCLASSIFIED, "m^0", "m^0", "", sys.getUOM(Unit.METRE), 0);

		try {
			UnitOfMeasure abscissaUnit = null;
			new Conversion(abscissaUnit);
			fail();
		} catch (Exception e) {
		}

		try {
			UnitOfMeasure abscissaUnit = sys.getOne();
			;
			BigDecimal decimal = null;
			new Conversion(decimal, abscissaUnit);
			fail();
		} catch (Exception e) {
		}

		try {
			UnitOfMeasure abscissaUnit = sys.getOne();
			String decimal = null;
			new Conversion(decimal, abscissaUnit);
			fail();
		} catch (Exception e) {
		}

		try {
			UnitOfMeasure abscissaUnit = sys.getOne();
			;
			BigDecimal decimal = BigDecimal.ONE;
			BigDecimal offset = null;
			new Conversion(decimal, abscissaUnit, offset);
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
		assertTrue(u.equals(sys.getOne()));

		u = metre.divide(metre);
		assertTrue(u.equals(sys.getOne()));

		u = sys.getOne().divide(metre).multiply(metre);
		assertTrue(u.equals(sys.getOne()));

		Conversion conversion = new Conversion(BigDecimal.ONE, sys.getOne(), BigDecimal.ONE);
		UnitOfMeasure uom = sys.createScalarUOM(UnitType.UNCLASSIFIED, "1/1", "1/1", "");
		uom.setConversion(conversion);
		Conversion c = uom.getConversion();
		assertTrue(c.equals(conversion));

		assertThat(uom.getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(uom.getAbscissaUnit().equals(sys.getOne()));
		assertThat(uom.getOffset(), closeTo(BigDecimal.ONE, DELTA6));

		u = sys.getOne().invert();
		assertTrue(u.getAbscissaUnit().equals(sys.getOne()));

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
		BigDecimal two = Quantity.createAmount("2");
		Conversion conversion = new Conversion(two, b, BigDecimal.ONE);
		UnitOfMeasure ab1 = sys.createScalarUOM(UnitType.UNCLASSIFIED, "a=2b+1", "a=2b+1", "custom");
		ab1.setConversion(conversion);

		assertThat(ab1.getScalingFactor(), closeTo(two, DELTA6));
		assertTrue(ab1.getAbscissaUnit().equals(b));
		assertThat(ab1.getOffset(), closeTo(BigDecimal.ONE, DELTA6));

		// quotient
		UnitOfMeasure a = sys.createScalarUOM(UnitType.UNCLASSIFIED, "a", "alpha", "Alpha");
		assertTrue(a.getAbscissaUnit().equals(a));

		UnitOfMeasure aOverb = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "a/b", "a/b", "", a, b);
		aOverb.setScalingFactor(two);

		assertThat(aOverb.getScalingFactor(), closeTo(two, DELTA6));
		assertTrue(aOverb.getDividend().equals(a));
		assertTrue(aOverb.getDivisor().equals(b));
		assertThat(aOverb.getOffset(), closeTo(BigDecimal.ZERO, DELTA6));
		assertTrue(aOverb.getAbscissaUnit().equals(aOverb));

		UnitOfMeasure bOvera = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "b/a", "b/a", "", b, a);
		UnitOfMeasure bOveraI = bOvera.invert();
		assertTrue(bOveraI.getBaseSymbol().equals(aOverb.getBaseSymbol()));

		// multiply2
		UnitOfMeasure uom = aOverb.multiply(b);
		assertTrue(uom.getAbscissaUnit().equals(a));
		assertThat(uom.getScalingFactor(), closeTo(two, DELTA6));
		BigDecimal bd = uom.getConversionFactor(a);
		assertThat(bd, closeTo(two, DELTA6));

		// divide2
		UnitOfMeasure uom2 = uom.divide(b);
		assertThat(uom2.getScalingFactor(), closeTo(two, DELTA6));
		assertThat(uom2.getOffset(), closeTo(BigDecimal.ZERO, DELTA6));
		assertTrue(uom2.equals(aOverb));

		// invert
		UnitOfMeasure uom3 = uom2.invert();
		UnitOfMeasure u = uom3.multiply(uom2);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getBaseSymbol()));

		// product
		UnitOfMeasure ab = sys.createProductUOM(UnitType.UNCLASSIFIED, "name", "symbol", "custom", a, b);
		ab.setOffset(BigDecimal.ONE);

		assertThat(ab.getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(ab.getMultiplier().equals(a));
		assertTrue(ab.getMultiplicand().equals(b));
		assertThat(ab.getOffset(), closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(ab.getAbscissaUnit().equals(ab));

		ab.setOffset(BigDecimal.ZERO);

		UnitOfMeasure uom4 = ab.divide(a);
		assertThat(uom4.getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(uom4.getAbscissaUnit().equals(b));

		UnitOfMeasure uom5 = uom4.multiply(a);
		assertThat(uom5.getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));
		u = uom5.getAbscissaUnit();
		assertTrue(u.getBaseSymbol().equals(ab.getBaseSymbol()));

		// invert
		UnitOfMeasure uom6 = ab.invert();
		assertThat(uom6.getScalingFactor(), closeTo(Quantity.createAmount("1"), DELTA6));
		assertTrue(uom6.getDividend().equals(sys.getOne()));
		assertTrue(uom6.getDivisor().equals(ab));
		assertThat(uom6.getOffset(), closeTo(BigDecimal.ZERO, DELTA6));

		// power
		UnitOfMeasure a2 = sys.createPowerUOM(UnitType.UNCLASSIFIED, "name", "a**2", "custom", a, 2);

		assertThat(a2.getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(a2.getPowerBase().equals(a));
		assertTrue(a2.getPowerExponent() == 2);
		assertThat(a2.getOffset(), closeTo(BigDecimal.ZERO, DELTA6));
		assertTrue(a2.getAbscissaUnit().equals(a2));

		UnitOfMeasure uom8 = a2.divide(a);
		assertThat(uom8.getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));
		assertThat(uom8.getOffset(), closeTo(BigDecimal.ZERO, DELTA6));
		assertTrue(uom8.getAbscissaUnit().equals(a));

		UnitOfMeasure uom9 = uom8.multiply(a);
		assertThat(uom9.getScalingFactor(), closeTo(Quantity.createAmount("1"), DELTA6));
		assertThat(uom9.getOffset(), closeTo(BigDecimal.ZERO, DELTA6));
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
		assertTrue(aOverb.divide(cOverx).multiply(cOverx).equals(aOverb));
		assertTrue(aOverb.multiply(cOverx).divide(cOverx).equals(aOverb));

		UnitOfMeasure axb = sys.createProductUOM(UnitType.UNCLASSIFIED, "", "a.b", "", a, b);
		u = sys.getUOM(axb.getSymbol());
		assertTrue(u.equals(axb));
		assertTrue(axb.divide(a).equals(b));

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
		UnitOfMeasure flush = sys.createScalarUOM(UnitType.UNCLASSIFIED, "flush", "f", "");
		UnitOfMeasure gpf = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "gal per flush", "gpf", "", gal, flush);
		UnitOfMeasure velocity = sys.getUOM(Unit.FEET_PER_SECOND);

		UnitOfMeasure litre = sys.getUOM(Unit.LITRE);
		UnitOfMeasure lpf = sys.createQuotientUOM(UnitType.UNCLASSIFIED, "litre per flush", "lpf", "", litre, flush);

		BigDecimal bd = gpf.getConversionFactor(lpf);
		assertThat(bd, closeTo(Quantity.createAmount("3.785411784"), DELTA6));

		bd = lpf.getConversionFactor(gpf);
		assertThat(bd, closeTo(Quantity.createAmount("0.2641720523581484"), DELTA6));

		// inversions
		UnitOfMeasure u = foot.invert();
		assertTrue(u.getSymbol().equals("1/ft"));

		u = u.multiply(foot);
		assertTrue(u.equals(sys.getOne()));

		u = velocity.invert();
		assertTrue(u.getSymbol().equals("s/ft"));

		u = u.multiply(velocity);
		assertTrue(u.equals(sys.getOne()));

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

		BigDecimal bd = impGal.getConversionFactor(litre);
		assertThat(bd, closeTo(Quantity.createAmount("4.54609"), DELTA6));

		bd = litre.getConversionFactor(impGal);
		assertThat(bd, closeTo(Quantity.createAmount("0.2199692482990878"), DELTA6));

		bd = impGal.getConversionFactor(usGal);
		assertThat(bd, closeTo(Quantity.createAmount("1.200949925504855"), DELTA6));

		bd = usGal.getConversionFactor(impGal);
		assertThat(bd, closeTo(Quantity.createAmount("0.8326741846289888"), DELTA6));

		bd = impGal.getConversionFactor(impPint);
		assertThat(bd, closeTo(Quantity.createAmount("8"), DELTA6));

		bd = impPint.getConversionFactor(impGal);
		assertThat(bd, closeTo(Quantity.createAmount("0.125"), DELTA6));

		bd = usGal.getConversionFactor(usPint);
		assertThat(bd, closeTo(Quantity.createAmount("8"), DELTA6));

		bd = usPint.getConversionFactor(usGal);
		assertThat(bd, closeTo(Quantity.createAmount("0.125"), DELTA6));

		bd = impOz.getConversionFactor(m3);
		assertThat(bd, closeTo(Quantity.createAmount("28.4130625E-06"), DELTA6));

		bd = m3.getConversionFactor(impOz);
		assertThat(bd, closeTo(Quantity.createAmount("35195.07972785405"), DELTA6));

	}

	@Test
	public void testOperations() throws Exception {

		UnitOfMeasure u = null;
		UnitOfMeasure hour = sys.getHour();
		UnitOfMeasure metre = sys.getUOM(Unit.METRE);
		UnitOfMeasure m2 = sys.getUOM(Unit.SQUARE_METRE);

		// multiply2
		UnitOfMeasure velocity = sys.getUOM("m/hr");

		if (velocity == null) {
			Conversion conversion = new Conversion(
					BigDecimal.ONE.divide(Quantity.createAmount("3600"), UnitOfMeasure.MATH_CONTEXT),
					sys.getUOM(Unit.METRE_PER_SECOND));
			velocity = sys.createScalarUOM(UnitType.VELOCITY, "m/hr", "m/hr", "");
			velocity.setConversion(conversion);
		}

		u = velocity.multiply(hour);
		BigDecimal bd = u.getConversionFactor(metre);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		u = hour.multiply(velocity);
		bd = u.getConversionFactor(metre);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		u = metre.multiply(metre);
		bd = u.getConversionFactor(m2);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(u.equals(m2));

		// divide2
		u = metre.divide(hour);
		bd = u.getConversionFactor(velocity);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		u = u.multiply(hour);
		assertTrue(u.equals(metre));

		// invert
		UnitOfMeasure vinvert = velocity.invert();
		vinvert.getScalingFactor().equals(Quantity.createAmount("3600"));

		// max symbol length
		Quantity v = null;
		Quantity h = null;
		UnitOfMeasure mpc = sys.getUOM(Prefix.MEGA, sys.getUOM(Unit.PARSEC));
		Quantity d = new Quantity(BigDecimal.TEN, mpc);
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

		assertThat(second.getConversionFactor(msec), closeTo(Quantity.createAmount("1000"), DELTA6));

		assertThat(second.getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(second.getAbscissaUnit().equals(second));
		assertThat(second.getOffset(), closeTo(BigDecimal.ZERO, DELTA6));

		BigDecimal bd = hour.getConversionFactor(second);

		UnitOfMeasure u = second.multiply(second);

		assertThat(u.getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));
		assertThat(u.getOffset(), closeTo(BigDecimal.ZERO, DELTA6));
		assertTrue(u.equals(s2));

		u = second.divide(second);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getSymbol()));

		Quantity q1 = new Quantity(BigDecimal.ONE, u);
		Quantity q2 = q1.convert(sys.getOne());
		assertThat(q2.getAmount(), closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(q2.getUOM().equals(sys.getOne()));

		u = second.invert();

		assertTrue(u.getDividend().equals(sys.getOne()));
		assertTrue(u.getDivisor().equals(second));

		u = min.divide(second);
		assertThat(u.getScalingFactor(), closeTo(Quantity.createAmount("60"), DELTA6));
		assertThat(u.getOffset(), closeTo(BigDecimal.ZERO, DELTA6));

		UnitOfMeasure uom = u.multiply(second);
		bd = uom.getConversionFactor(min);
		assertTrue(uom.equals(min));
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		q1 = new Quantity(BigDecimal.TEN, u);
		q2 = q1.convert(sys.getOne());
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("600"), DELTA6));
		assertTrue(q2.getUOM().equals(sys.getOne()));

		// multiply2
		u = min.multiply(min);
		assertThat(u.getScalingFactor(), closeTo(Quantity.createAmount("3600"), DELTA6));
		assertTrue(u.getAbscissaUnit().equals(s2));
		assertThat(u.getOffset(), closeTo(BigDecimal.ZERO, DELTA6));

		q1 = new Quantity(BigDecimal.TEN, u);
		q2 = q1.convert(s2);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("36000"), DELTA6));
		assertTrue(q2.getUOM().equals(s2));

		q2 = q2.convert(min2);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("10"), DELTA6));

		u = min.multiply(second);
		assertThat(u.getScalingFactor(), closeTo(Quantity.createAmount("60"), DELTA6));
		assertTrue(u.getAbscissaUnit().equals(s2));

		u = second.multiply(min);
		bd = u.getConversionFactor(s2);
		assertThat(bd, closeTo(Quantity.createAmount("60"), DELTA6));

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
		assertTrue(Prefix.MILLI.getScalingFactor() != null);

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

		symbol = sys.getUOM(Unit.BECQUEREL).getBaseSymbol();
		assertTrue(symbol.equals("Bq"));

		symbol = sys.getUOM(Unit.BECQUEREL).getSymbol();
		assertTrue(symbol.equals("Bq"));

		symbol = sys.getUOM(Unit.GRAY).getBaseSymbol();
		sb = new StringBuffer();
		sb.append("m").append((char) 0xB2).append("/s").append((char) 0xB2);
		assertTrue(symbol.equals(sb.toString()));

		symbol = sys.getUOM(Unit.HERTZ).getBaseSymbol();
		assertTrue(symbol.equals("1/s"));

		assertTrue(sys.getUOM(Unit.KATAL).getBaseSymbol().equals("mol/s"));
	}

	@Test
	public void testConversions1() throws Exception {

		UnitOfMeasure m = sys.getUOM(Unit.METRE);
		UnitOfMeasure cm = sys.getUOM(Prefix.CENTI, m);
		UnitOfMeasure N = sys.getUOM(Unit.NEWTON);
		UnitOfMeasure Nm = sys.getUOM(Unit.NEWTON_METRE);
		UnitOfMeasure mps = sys.getUOM(Unit.METRE_PER_SECOND);
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
		assertTrue(u.getScalingFactor().equals(oneDivSec.getScalingFactor()));

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
		BigDecimal bd = u.getConversionFactor(sys.getMinute());
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = u.getConversionFactor(sys.getSecond());
		assertThat(bd, closeTo(Quantity.createAmount("60"), DELTA6));

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
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(m.equals(m));

		bd = m.getConversionFactor(cm);
		assertThat(bd, closeTo(Quantity.createAmount("100"), DELTA6));
		assertTrue(m.equals(m));
		assertTrue(cm.equals(cm));
		assertTrue((!m.equals(cm)));

		bd = m.getConversionFactor(cm);
		assertThat(bd, closeTo(Quantity.createAmount("100"), DELTA6));

		bd = cm.getConversionFactor(m);
		assertThat(bd, closeTo(Quantity.createAmount("0.01"), DELTA6));

		bd = m.getConversionFactor(cm);
		assertThat(bd, closeTo(Quantity.createAmount("100"), DELTA6));

		bd = m.getConversionFactor(in);
		assertThat(bd, closeTo(Quantity.createAmount("39.37007874015748"), DELTA6));

		bd = in.getConversionFactor(m);
		assertThat(bd, closeTo(Quantity.createAmount("0.0254"), DELTA6));

		bd = m.getConversionFactor(ft);
		assertThat(bd, closeTo(Quantity.createAmount("3.280839895013123"), DELTA6));

		bd = ft.getConversionFactor(m);
		assertThat(bd, closeTo(Quantity.createAmount("0.3048"), DELTA6));

		Quantity g = sys.getQuantity(Constant.GRAVITY).convert(sys.getUOM(Unit.FEET_PER_SECOND_SQUARED));
		bd = g.getAmount();
		assertThat(bd, closeTo(Quantity.createAmount("32.17404855"), DELTA6));

		bd = lbf.getConversionFactor(N);
		assertThat(bd, closeTo(Quantity.createAmount("4.448221615"), DELTA6));

		bd = N.getConversionFactor(lbf);
		assertThat(bd, closeTo(Quantity.createAmount("0.2248089430997105"), DELTA6));

		// product
		bd = Nm.getConversionFactor(ftlb);
		assertThat(bd, closeTo(Quantity.createAmount("0.7375621492772656"), DELTA6));

		bd = ftlb.getConversionFactor(Nm);
		assertThat(bd, closeTo(Quantity.createAmount("1.3558179483314004"), DELTA6));

		// quotient
		UnitOfMeasure one = sys.getOne();
		bd = minOverSec.getConversionFactor(one);
		assertThat(bd, closeTo(Quantity.createAmount("60"), DELTA6));

		bd = one.getConversionFactor(minOverSec);
		assertThat(bd, closeTo(Quantity.createAmount("0.0166666666666667"), DELTA6));

		bd = mps.getConversionFactor(fph);
		assertThat(bd, closeTo(Quantity.createAmount("11811.02362204724"), DELTA6));

		bd = fph.getConversionFactor(mps);
		assertThat(bd, closeTo(Quantity.createAmount("8.46666666666667E-05"), DELTA6));

		// power
		bd = sqm.getConversionFactor(sqft);
		assertThat(bd, closeTo(Quantity.createAmount("10.76391041670972"), DELTA6));

		bd = sqft.getConversionFactor(sqm);
		assertThat(bd, closeTo(Quantity.createAmount("0.09290304"), DELTA6));

		// mixed
		bd = mm.getConversionFactor(sqm);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = sqm.getConversionFactor(mm);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = mcm.getConversionFactor(sqm);
		assertThat(bd, closeTo(Quantity.createAmount("0.01"), DELTA6));

		bd = sqm.getConversionFactor(mcm);
		assertThat(bd, closeTo(Quantity.createAmount("100"), DELTA6));

		bd = minTimesSec.getConversionFactor(s2);
		assertThat(bd, closeTo(Quantity.createAmount("60"), DELTA6));

		bd = s2.getConversionFactor(minTimesSec);
		assertThat(bd, closeTo(Quantity.createAmount("0.0166666666666667"), DELTA6));

		bd = minTimesSec.getConversionFactor(sqMin);
		assertThat(bd, closeTo(Quantity.createAmount("0.0166666666666667"), DELTA6));

		bd = sqMin.getConversionFactor(minTimesSec);
		assertThat(bd, closeTo(Quantity.createAmount("60"), DELTA6));

		bd = minOverSecTimesSec.getConversionFactor(sys.getSecond());
		assertThat(bd, closeTo(Quantity.createAmount("60"), DELTA6));

	}

	@Test
	public void testConversions2() throws Exception {
		BigDecimal bd = null;

		sys.unregisterUnit(sys.getUOM(Unit.CUBIC_INCH));
		UnitOfMeasure ft = sys.getUOM(Unit.FOOT);
		UnitOfMeasure ft2 = sys.getUOM(Unit.SQUARE_FOOT);
		UnitOfMeasure ft3 = sys.getUOM(Unit.CUBIC_FOOT);

		UnitOfMeasure cubicFt = ft2.multiply(ft);
		bd = cubicFt.getConversionFactor(ft3);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		UnitOfMeasure m3 = sys.getUOM(Unit.CUBIC_METRE);
		UnitOfMeasure degree = sys.getUOM(Unit.DEGREE);
		UnitOfMeasure arcsec = sys.getUOM(Unit.ARC_SECOND);
		UnitOfMeasure radian = sys.getUOM(Unit.RADIAN);
		UnitOfMeasure kgPerM3 = sys.getUOM(Unit.KILOGRAM_PER_CUBIC_METRE);
		UnitOfMeasure mps = sys.getUOM(Unit.METRE_PER_SECOND);
		UnitOfMeasure pascal = sys.getUOM(Unit.PASCAL);
		UnitOfMeasure s2 = sys.getUOM(Unit.SQUARE_SECOND);
		UnitOfMeasure joule = sys.getUOM(Unit.JOULE);
		UnitOfMeasure rpm = sys.getUOM(Unit.REV_PER_MIN);
		UnitOfMeasure rps = sys.getUOM(Unit.RAD_PER_SEC);
		UnitOfMeasure m3s = sys.getUOM(Unit.CUBIC_METRE_PER_SECOND);
		UnitOfMeasure ms2 = sys.getUOM(Unit.METRE_PER_SECOND_SQUARED);

		UnitOfMeasure lbm = sys.getUOM(Unit.POUND_MASS);
		UnitOfMeasure acreFoot = sys.createProductUOM(UnitType.VOLUME, "acreFoot", "ac-ft", "", sys.getUOM(Unit.ACRE),
				sys.getUOM(Unit.FOOT));
		UnitOfMeasure lbmPerFt3 = sys.createQuotientUOM(UnitType.DENSITY, "lbmPerFt3", "lbm/ft^3", null, lbm, ft3);
		UnitOfMeasure fps = sys.getUOM(Unit.FEET_PER_SECOND);
		UnitOfMeasure knot = sys.getUOM(Unit.KNOT);
		UnitOfMeasure btu = sys.getUOM(Unit.BTU);

		Conversion conversion = new Conversion("1.466666666666667", sys.getUOM(Unit.FEET_PER_SECOND_SQUARED));
		UnitOfMeasure miphs = sys.createScalarUOM(UnitType.ACCELERATION, "mph/sec", "mi/hr-sec",
				"mile per hour per second");
		miphs.setConversion(conversion);

		conversion = new Conversion(Quantity.createAmount("3386.389"), pascal);
		UnitOfMeasure inHg = sys.createScalarUOM(UnitType.PRESSURE, "inHg", "inHg", "inHg");
		inHg.setConversion(conversion);

		Quantity atm = new Quantity(BigDecimal.ONE, Unit.ATMOSPHERE).convert(Unit.PASCAL);
		assertThat(atm.getAmount(), closeTo(Quantity.createAmount("101325"), DELTA6));

		UnitOfMeasure ft2ft = sys.createProductUOM(UnitType.VOLUME, "ft2ft", "ft2ft", null, ft2, ft);

		conversion = new Conversion("3600", sys.getUOM(Unit.SQUARE_SECOND));
		UnitOfMeasure hrsec = sys.createScalarUOM(UnitType.TIME_SQUARED, "", "hr.sec", "");
		hrsec.setConversion(conversion);
		bd = hrsec.getConversionFactor(s2);
		assertThat(bd, closeTo(Quantity.createAmount("3600"), DELTA6));

		bd = s2.getConversionFactor(hrsec);
		assertThat(bd, closeTo(Quantity.createAmount("2.777777777777778E-04"), DELTA6));

		bd = ft2ft.getConversionFactor(m3);
		assertThat(bd, closeTo(Quantity.createAmount("0.028316846592"), DELTA6));

		bd = m3.getConversionFactor(ft2ft);
		assertThat(bd, closeTo(Quantity.createAmount("35.31466672148859"), DELTA6));

		bd = acreFoot.getConversionFactor(m3);
		assertThat(bd, closeTo(Quantity.createAmount("1233.48183754752"), DELTA6));

		bd = m3.getConversionFactor(acreFoot);
		assertThat(bd, closeTo(Quantity.createAmount("8.107131937899125E-04"), DELTA6));

		bd = degree.getConversionFactor(radian);
		assertThat(bd, closeTo(Quantity.createAmount("0.01745329251994329"), DELTA6));

		bd = radian.getConversionFactor(degree);
		assertThat(bd, closeTo(Quantity.createAmount("57.29577951308264"), DELTA6));

		bd = arcsec.getConversionFactor(degree);
		assertThat(bd, closeTo(Quantity.createAmount("2.777777777777778E-4"), DELTA6));

		bd = degree.getConversionFactor(arcsec);
		assertThat(bd, closeTo(Quantity.createAmount("3600"), DELTA6));

		bd = lbmPerFt3.getConversionFactor(kgPerM3);
		assertThat(bd, closeTo(Quantity.createAmount("16.01846337"), DELTA6));

		bd = kgPerM3.getConversionFactor(lbmPerFt3);
		assertThat(bd, closeTo(Quantity.createAmount("0.0624279605915783"), DELTA6));

		bd = rpm.getConversionFactor(rps);
		assertThat(bd, closeTo(Quantity.createAmount("0.104719755"), DELTA6));

		bd = rps.getConversionFactor(rpm);
		assertThat(bd, closeTo(Quantity.createAmount("9.549296596425383"), DELTA6));

		bd = mps.getConversionFactor(fps);
		assertThat(bd, closeTo(Quantity.createAmount("3.280839895013123"), DELTA6));

		bd = fps.getConversionFactor(mps);
		assertThat(bd, closeTo(Quantity.createAmount("0.3048"), DELTA6));

		bd = knot.getConversionFactor(mps);
		assertThat(bd, closeTo(Quantity.createAmount("0.5147733333333333"), DELTA6));

		bd = mps.getConversionFactor(knot);
		assertThat(bd, closeTo(Quantity.createAmount("1.942602569415665"), DELTA6));

		UnitOfMeasure usGal = sys.getUOM(Unit.US_GALLON);
		UnitOfMeasure gph = sys.createQuotientUOM(UnitType.VOLUMETRIC_FLOW, "gph", "gal/hr", "gallons per hour", usGal,
				sys.getHour());

		bd = gph.getConversionFactor(m3s);
		assertThat(bd, closeTo(Quantity.createAmount("1.051503273E-06"), DELTA6));

		bd = m3s.getConversionFactor(gph);
		assertThat(bd, closeTo(Quantity.createAmount("951019.3884893342"), DELTA6));

		bd = miphs.getConversionFactor(ms2);
		assertThat(bd, closeTo(Quantity.createAmount("0.44704"), DELTA6));

		bd = ms2.getConversionFactor(miphs);
		assertThat(bd, closeTo(Quantity.createAmount("2.236936292054402"), DELTA6));

		bd = pascal.getConversionFactor(inHg);
		assertThat(bd, closeTo(Quantity.createAmount("2.952998016471232E-04"), DELTA6));

		bd = inHg.getConversionFactor(pascal);
		assertThat(bd, closeTo(Quantity.createAmount("3386.389"), DELTA6));

		bd = atm.convert(inHg).getAmount();
		assertThat(bd, closeTo(Quantity.createAmount("29.92125240189478"), DELTA6));

		bd = inHg.getConversionFactor(atm.getUOM());
		assertThat(bd, closeTo(Quantity.createAmount("3386.389"), DELTA6));

		bd = btu.getConversionFactor(joule);
		assertThat(bd, closeTo(Quantity.createAmount("1055.05585262"), DELTA6));

		bd = joule.getConversionFactor(btu);
		assertThat(bd, closeTo(Quantity.createAmount("9.478171203133172E-04"), DELTA6));

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

		BigDecimal bd = fTimesV.getConversionFactor(coulomb);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = coulomb.getConversionFactor(fTimesV);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = WeberPerSec.getConversionFactor(volt);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = volt.getConversionFactor(WeberPerSec);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = volt.getConversionFactor(WPerAmp);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = WPerAmp.getConversionFactor(volt);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = ohm.getConversionFactor(VPerA);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = VPerA.getConversionFactor(ohm);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = farad.getConversionFactor(CPerV);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = CPerV.getConversionFactor(farad);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = weber.getConversionFactor(VTimesSec);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = VTimesSec.getConversionFactor(weber);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = henry.getConversionFactor(WeberPerAmp);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = WeberPerAmp.getConversionFactor(henry);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = lumen.getConversionFactor(cdTimesSr);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = cdTimesSr.getConversionFactor(lumen);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

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

		BigDecimal fiveNinths = Quantity.createAmount("5").divide(Quantity.createAmount("9"),
				UnitOfMeasure.MATH_CONTEXT);
		BigDecimal nineFifths = Quantity.createAmount("1.8");

		// K to C
		BigDecimal bd = K.getConversionFactor(C);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = C.getConversionFactor(K);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		// R to F
		bd = R.getConversionFactor(F);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = F.getConversionFactor(R);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		// C to F
		bd = F.getConversionFactor(C);
		assertThat(bd, closeTo(fiveNinths, DELTA6));

		bd = C.getConversionFactor(F);
		assertThat(bd, closeTo(nineFifths, DELTA6));

		// K to R
		bd = K.getConversionFactor(R);
		assertThat(bd, closeTo(nineFifths, DELTA6));

		bd = F.getConversionFactor(K);
		assertThat(bd, closeTo(fiveNinths, DELTA6));

		// invert diopters to metre
		Quantity from = new Quantity(BigDecimal.TEN, sys.getUOM(Unit.DIOPTER));
		Quantity inverted = from.invert();
		assertThat(inverted.getAmount(), closeTo(Quantity.createAmount("0.1"), DELTA6));

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
		from = new Quantity(BigDecimal.ONE, ha);
		Quantity to = from.convert(Unit.ACRE);
		assertThat(to.getAmount(), closeTo(Quantity.createAmount("2.47105"), DELTA5));
	}

	@Test
	public void testPerformance() throws Exception {
		int its = 1000;

		UnitOfMeasure metre = sys.getUOM(Unit.METRE);
		UnitOfMeasure cm = sys.getUOM(Prefix.CENTI, sys.getUOM(Unit.METRE));
		UnitOfMeasure ft = sys.getUOM(Unit.FOOT);

		Quantity q1 = new Quantity("10", metre);
		Quantity q2 = new Quantity("2", cm);

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

		Quantity qmm = new Quantity("1", mm);
		Quantity qm = qmm.convert(m);
		assertThat(qm.getAmount(), closeTo(new BigDecimal("1.0E+06"), DELTA6));

		UnitOfMeasure mm2 = sys.getUOM(Prefix.MEGA, m);
		assertTrue(mm.equals(mm2));

		// centilitre
		UnitOfMeasure litre = sys.getUOM(Unit.LITRE);
		UnitOfMeasure cL = sys.getUOM(Prefix.CENTI, litre);
		Quantity qL = new Quantity("1", litre);
		Quantity qcL = qL.convert(cL);
		assertThat(qcL.getAmount(), closeTo(new BigDecimal("100"), DELTA6));

		// a mega buck
		UnitOfMeasure buck = sys.createScalarUOM(UnitType.UNCLASSIFIED, "buck", "$", "one US dollar");
		UnitOfMeasure megabuck = sys.getUOM(Prefix.MEGA, buck);
		Quantity qmb = new Quantity("10", megabuck);
		Quantity qb = qmb.convert(buck);
		assertThat(qb.getAmount(), closeTo(new BigDecimal("1.0E+07"), DELTA6));

		// kilogram vs. scaled gram
		UnitOfMeasure kgm = sys.getUOM(Prefix.KILO, sys.getUOM(Unit.GRAM));
		UnitOfMeasure kg = sys.getUOM(Unit.KILOGRAM);
		assertTrue(kgm.equals(kg));

		// kilo and megabytes
		UnitOfMeasure kiB = sys.getUOM(Prefix.KIBI, sys.getUOM(Unit.BYTE));
		UnitOfMeasure miB = sys.getUOM(Prefix.MEBI, sys.getUOM(Unit.BYTE));
		Quantity qmB = new Quantity("1", miB);
		Quantity qkB = qmB.convert(kiB);
		assertThat(qkB.getAmount(), closeTo(new BigDecimal("1024"), DELTA6));
	}

	@Test
	public void testPowers() throws Exception {

		BigDecimal bd = null;

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
		assertTrue(ij.equals(ixjm1));

		bd = min2.getConversionFactor(s2);
		assertThat(bd, closeTo(Quantity.createAmount("3600"), DELTA6));

		bd = s2.getConversionFactor(min2);
		assertThat(bd, closeTo(Quantity.createAmount("2.777777777777778e-4"), DELTA6));

		u = sys.getBaseUOM(sm1.getSymbol());
		assertTrue(u != null);
		u = sys.getUOM(sm1.getBaseSymbol());

		u = sys.getOne().divide(min);
		bd = u.getScalingFactor();
		assertThat(bd, closeTo(Quantity.createAmount("0.0166666666666667"), DELTA6));
		bd = u.getConversionFactor(sm1);
		assertThat(bd, closeTo(Quantity.createAmount("0.0166666666666667"), DELTA6));

		u = ftm1.multiply(ft);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getSymbol()));

		u = ft.multiply(inm1);
		assertThat(u.getScalingFactor(), closeTo(Quantity.createAmount("12"), DELTA6));

		u = inm1.multiply(ft);
		assertThat(u.getScalingFactor(), closeTo(Quantity.createAmount("12"), DELTA6));

		u = s.multiply(minminus1);
		assertThat(u.getScalingFactor(), closeTo(Quantity.createAmount("0.0166666666666667"), DELTA6));

		u = minminus1.multiply(s);
		assertThat(u.getScalingFactor(), closeTo(Quantity.createAmount("0.0166666666666667"), DELTA6));

		u = s.multiply(minminus1Q);
		assertThat(u.getScalingFactor(), closeTo(Quantity.createAmount("0.0166666666666667"), DELTA6));

		u = minminus1Q.multiply(s);
		assertThat(u.getScalingFactor(), closeTo(Quantity.createAmount("0.0166666666666667"), DELTA6));

		u = ftm1.multiply(in);
		assertThat(u.getScalingFactor(), closeTo(Quantity.createAmount("0.0833333333333333"), DELTA6));

		u = in.multiply(ftm1);
		assertThat(u.getScalingFactor(), closeTo(Quantity.createAmount("0.0833333333333333"), DELTA6));

		u = newtonm1.multiply(newton);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getBaseSymbol()));

		u = newton.multiply(newtonm1);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getBaseSymbol()));

		u = minminus1.multiply(s);
		assertThat(u.getScalingFactor(), closeTo(Quantity.createAmount("0.0166666666666667"), DELTA6));

		sys.unregisterUnit(sys.getUOM(Unit.HERTZ));
		UnitOfMeasure min1 = min.invert();
		bd = min1.getScalingFactor();
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = sqs.getScalingFactor();
		assertThat(bd, closeTo(Quantity.createAmount("1"), DELTA6));

		u = sminus1.multiply(s);
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getSymbol()));

		u = sys.getOne().divide(min);
		bd = u.getScalingFactor();
		assertThat(bd, closeTo(Quantity.createAmount("1"), DELTA6));
		bd = u.getConversionFactor(sm1);
		assertThat(bd, closeTo(Quantity.createAmount("0.0166666666666667"), DELTA6));

		t = s2.getUnitType();

		t = min.getUnitType();

		u = sys.getOne().multiply(min);
		bd = u.getConversionFactor(s);

		t = min2.getUnitType();

		u = min2.divide(min);
		t = u.getUnitType();
		assertTrue(t.equals(UnitType.TIME));

		u = min.multiply(min);
		assertThat(u.getScalingFactor(), closeTo(Quantity.createAmount("3600"), DELTA6));
		assertTrue(u.getAbscissaUnit().equals(s2));
		assertThat(u.getOffset(), closeTo(BigDecimal.ZERO, DELTA6));
		t = u.getUnitType();
		assertTrue(t.equals(UnitType.TIME_SQUARED));

		u2 = sys.getOne().divide(min);
		assertThat(u2.getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));

		q1 = new Quantity(BigDecimal.ONE, u2);
		q2 = q1.convert(hz);

		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("0.0166666666666667"), DELTA6));

		u = u2.multiply(u2);
		assertThat(u.getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));

		q1 = new Quantity(BigDecimal.ONE, u);
		q2 = q1.convert(s2.invert());
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("2.777777777777778e-4"), DELTA6));

		u2 = u2.divide(min);
		q1 = new Quantity(BigDecimal.ONE, u2);
		q2 = q1.convert(s2.invert());
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("2.777777777777778e-4"), DELTA6));

		u2 = u2.invert();
		assertTrue(u2.getBaseSymbol().equals(min2.getBaseSymbol()));

		q1 = new Quantity(BigDecimal.TEN, u2);
		bd = u2.getConversionFactor(s2);
		assertThat(bd, closeTo(Quantity.createAmount("3600"), DELTA6));

		q2 = q1.convert(s2);
		assertTrue(q2.getUOM().equals(s2));
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("36000"), DELTA6));

		bd = min.getConversionFactor(sys.getSecond());
		assertThat(bd, closeTo(Quantity.createAmount("60"), DELTA6));

		u = q2.getUOM();
		bd = u.getConversionFactor(min2);
		assertThat(bd, closeTo(Quantity.createAmount("2.777777777777778e-4"), DELTA6));

		q2 = q2.convert(min2);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("10"), DELTA6));
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
		Quantity testResult = new Quantity("4.9", mEqPerL);
		assertTrue(testResult.getAmount().compareTo(Quantity.createAmount("3.5")) == 1);
		assertTrue(testResult.getAmount().compareTo(Quantity.createAmount("5.3")) == -1);

		// Unit
		UnitOfMeasure u = sys.getUOM(Unit.UNIT);
		UnitOfMeasure katal = sys.getUOM(Unit.KATAL);
		Quantity q1 = new Quantity(BigDecimal.ONE, u);
		Quantity q2 = q1.convert(sys.getUOM(Prefix.NANO, katal));
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("16.666667"), DELTA6));

		// blood cell counts
		UnitOfMeasure k = sys.getUOM(Prefix.KILO, sys.getOne());
		UnitOfMeasure uL = sys.getUOM(Prefix.MICRO, Unit.LITRE);
		UnitOfMeasure kul = sys.createQuotientUOM(UnitType.MOLAR_CONCENTRATION, "K/uL", "K/uL",
				"thousands per microlitre", k, uL);
		testResult = new Quantity("6.6", kul);
		assertTrue(testResult.getAmount().compareTo(Quantity.createAmount("3.5")) == 1);
		assertTrue(testResult.getAmount().compareTo(Quantity.createAmount("12.5")) == -1);

		UnitOfMeasure fL = sys.getUOM(Prefix.FEMTO, Unit.LITRE);
		testResult = new Quantity("90", fL);
		assertTrue(testResult.getAmount().compareTo(Quantity.createAmount("80")) == 1);
		assertTrue(testResult.getAmount().compareTo(Quantity.createAmount("100")) == -1);

		// TSH
		UnitOfMeasure uIU = sys.getUOM(Prefix.MICRO, Unit.INTERNATIONAL_UNIT);
		UnitOfMeasure mL = sys.getUOM(Prefix.MILLI, Unit.LITRE);
		UnitOfMeasure uiuPerml = sys.createQuotientUOM(UnitType.MOLAR_CONCENTRATION, "uIU/mL", "uIU/mL",
				"micro IU per millilitre", uIU, mL);
		testResult = new Quantity("2.11", uiuPerml);
		assertTrue(testResult.getAmount().compareTo(Quantity.createAmount("0.40")) == 1);
		assertTrue(testResult.getAmount().compareTo(Quantity.createAmount("5.50")) == -1);

	}
}
