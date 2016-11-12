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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;
import org.point85.uom.Conversion;
import org.point85.uom.MeasurementSystem;
import org.point85.uom.PowerUOM;
import org.point85.uom.ProductUOM;
import org.point85.uom.Quantity;
import org.point85.uom.QuotientUOM;
import org.point85.uom.ScalarUOM;
import org.point85.uom.Unit;
import org.point85.uom.UnitOfMeasure;
import org.point85.uom.UnitType;

public class TestUnits extends BaseTest {

	@Test
	public void testExceptions() throws Exception {

		MeasurementSystem sys = uomService.getUnifiedSystem();

		try {
			sys.createProductUOM(UnitType.CUSTOM, null, "abcd", "", null, null);
			fail("null");
		} catch (Exception e) {
		}

		try {
			sys.createQuotientUOM(UnitType.CUSTOM, null, "abcd", "", null, null);
			fail("null");
		} catch (Exception e) {
		}

		try {
			sys.createPowerUOM(UnitType.CUSTOM, null, "abcd", "", null, 2);
			fail("null");
		} catch (Exception e) {
		}

		try {
			sys.createScalarUOM(null, "1/1", "1/1", "");
			fail("no type");
		} catch (Exception e) {
		}

		try {
			sys.createScalarUOM(UnitType.CUSTOM, "", null, "");
			sys.createScalarUOM(UnitType.CUSTOM, "", "", "");
			fail("already created");
		} catch (Exception e) {
		}

		UnitOfMeasure u = sys.createQuotientUOM(UnitType.CUSTOM, "1/1", "1/1", "", sys.getOne(), sys.getOne());
		Quantity q1 = new Quantity(BigDecimal.TEN, u);
		Quantity q2 = q1.convert(sys.getOne());
		assertThat(q2.getAmount(), closeTo(BigDecimal.TEN, DELTA6));
		assertTrue(q2.getUOM().equals(sys.getOne()));
		sys.unregisterUnit(u);

		u = sys.createProductUOM(UnitType.CUSTOM, "1x1", "1x1", "", sys.getOne(), sys.getOne());
		BigDecimal bd = u.getConversionFactor(sys.getOne());
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		try {
			sys.createProductUOM(UnitType.CUSTOM, "1x1", "1x1", "", sys.getOne(), sys.getOne());
			fail("1x1");
		} catch (Exception e) {
		}
		sys.unregisterUnit(u);

		u = sys.createPowerUOM(UnitType.CUSTOM, "1^2", "1^2", "", sys.getOne(), 2);
		bd = u.getConversionFactor(sys.getOne());
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		try {
			sys.createPowerUOM(UnitType.CUSTOM, "1^2", "1^2", "", sys.getOne(), 2);
			fail("1^2");
		} catch (Exception e) {
		}
		sys.unregisterUnit(u);

		u = sys.createPowerUOM(UnitType.CUSTOM, "1^0", "1^0", "", sys.getOne(), 0);
		bd = u.getConversionFactor(sys.getOne());
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		try {
			sys.createPowerUOM(UnitType.CUSTOM, "1^0", "1^0", "", sys.getOne(), 0);
			fail("1^0");
		} catch (Exception e) {
		}
		sys.unregisterUnit(u);

		UnitOfMeasure uno = sys.getOne();
		u = sys.createPowerUOM(UnitType.CUSTOM, "m^0", "m^0", "", sys.getUOM(Unit.METRE), 0);
		bd = u.getConversionFactor(uno);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(u.getBaseSymbol().equals(sys.getOne().getBaseSymbol()));
		
		UnitOfMeasure m1 = sys.getUOM(Unit.METRE);
		u = sys.createPowerUOM(UnitType.CUSTOM, "m^1", "m^1", "", sys.getUOM(Unit.METRE), 1);
		assertTrue(u.getBaseSymbol().equals(m1.getBaseSymbol()));		
		
		UnitOfMeasure m2 = sys.getUOM(Unit.SQUARE_METRE);
		u = sys.createPowerUOM(UnitType.CUSTOM, "m^2", "m^2", "", sys.getUOM(Unit.METRE), 2);
		assertTrue(u.getBaseSymbol().equals(m2.getBaseSymbol()));
		
		UnitOfMeasure perMetre = m1.invert();
		u = sys.createPowerUOM(UnitType.CUSTOM, "m*-1", "m*-1", "", sys.getUOM(Unit.METRE), -1);
		assertTrue(u.getBaseSymbol().equals(perMetre.getBaseSymbol()));
		
		UnitOfMeasure perMetre2 = m2.invert();
		u = sys.createPowerUOM(UnitType.CUSTOM, "m*-2", "m*-2", "", sys.getUOM(Unit.METRE), -2);
		assertTrue(u.getBaseSymbol().equals(perMetre2.getBaseSymbol()));

		try {
			sys.createPowerUOM(UnitType.CUSTOM, "m^0", "m^0", "", sys.getUOM(Unit.METRE), 0);
			fail("m^0");
		} catch (Exception e) {
		}
		sys.unregisterUnit(u);
	}

	@Test
	public void testOne() throws Exception {
		MeasurementSystem sys = uomService.getUnifiedSystem();
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
		ScalarUOM uom = sys.createScalarUOM(UnitType.CUSTOM, "1/1", "1/1", "");
		uom.setConversion(conversion);

		assertThat(uom.getScalingFactor(), closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(uom.getAbscissaUnit().equals(sys.getOne()));
		assertThat(uom.getOffset(), closeTo(BigDecimal.ONE, DELTA6));
		sys.unregisterUnit(uom);

		u = sys.getOne().invert();
		assertTrue(u.getAbscissaUnit().equals(sys.getOne()));

		UnitOfMeasure one = sys.getOne();
		assertTrue(one.getBaseSymbol().equals("1"));
		assertTrue(one.equals(one));

		QuotientUOM uno = sys.createQuotientUOM(UnitType.CUSTOM, "", ".1", "", one, one);
		assertTrue(uno.getBaseSymbol().equals(one.getBaseSymbol()));
		sys.unregisterUnit(uno);

		ProductUOM p = sys.createProductUOM(UnitType.CUSTOM, "", "..1", "", one, one);
		assertTrue(p.getBaseSymbol().equals(one.getBaseSymbol()));
		sys.unregisterUnit(p);

		PowerUOM p3 = sys.createPowerUOM(UnitType.CUSTOM, "", "...1", "", one, 3);
		assertTrue(p3.getBaseSymbol().equals(one.getBaseSymbol()));

		try {
			p3 = sys.createPowerUOM(UnitType.CUSTOM, "", "...1", "", one, -1);
			fail("already created");
		} catch (Exception e) {
		}
		sys.unregisterUnit(p3);

		ScalarUOM a1 = sys.createScalarUOM(UnitType.CUSTOM, "a1", "a1", "A1");
		assertTrue(a1.getBaseSymbol().equals("a1"));
		sys.unregisterUnit(a1);

		uno = sys.createQuotientUOM(UnitType.CUSTOM, "one", "one", "", a1, a1);
		assertTrue(uno.getBaseSymbol().equals(one.getBaseSymbol()));
		sys.unregisterUnit(uno);
	}

	@Test
	public void testGeneric() throws Exception {
		MeasurementSystem sys = uomService.getUnifiedSystem();

		UnitOfMeasure b = sys.getUOM("b");

		if (b != null) {
			sys.unregisterUnit(b);
		}
		b = sys.createScalarUOM(UnitType.CUSTOM, "b", "b", "B");

		assertFalse(b.equals(null));

		// scalar
		BigDecimal two = Quantity.createAmount("2");
		Conversion conversion = new Conversion(two, b, BigDecimal.ONE);
		ScalarUOM ab1 = sys.createScalarUOM(UnitType.CUSTOM, "a=2b+1", "a=2b+1", "custom");
		ab1.setConversion(conversion);

		assertThat(ab1.getScalingFactor(), closeTo(two, DELTA6));
		assertTrue(ab1.getAbscissaUnit().equals(b));
		assertThat(ab1.getOffset(), closeTo(BigDecimal.ONE, DELTA6));

		sys.unregisterUnit(ab1);
		assertNull(sys.getUOM(ab1.getSymbol()));

		// quotient
		UnitOfMeasure a = sys.getUOM("a");

		if (a != null) {
			sys.unregisterUnit(a);
		}
		a = sys.createScalarUOM(UnitType.CUSTOM, "a", "a", "A");
		assertTrue(a.getAbscissaUnit().equals(a));

		QuotientUOM aOverb = sys.createQuotientUOM(UnitType.CUSTOM, "a/b", "a/b", "", a, b);
		aOverb.setScalingFactor(two);

		assertThat(aOverb.getScalingFactor(), closeTo(two, DELTA6));
		assertTrue(aOverb.getDividend().equals(a));
		assertTrue(aOverb.getDivisor().equals(b));
		assertThat(aOverb.getOffset(), closeTo(BigDecimal.ZERO, DELTA6));
		assertTrue(aOverb.getAbscissaUnit().equals(aOverb));

		QuotientUOM bOvera = sys.createQuotientUOM(UnitType.CUSTOM, "b/a", "b/a", "", b, a);
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
		assertTrue(u.equals(sys.getOne()));

		sys.clearCache();

		// product
		a = sys.createScalarUOM(UnitType.CUSTOM, "a", "a", "A");
		b = sys.createScalarUOM(UnitType.CUSTOM, "b", "b", "B");
		a.setScalingFactor(two);

		ProductUOM ab = sys.createProductUOM(UnitType.CUSTOM, "name", "symbol", "custom", a, b);
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
		assertThat(uom5.getScalingFactor(), closeTo(two, DELTA6));
		u = uom5.getAbscissaUnit();
		assertTrue(u.getBaseSymbol().equals(ab.getBaseSymbol()));

		// invert
		QuotientUOM uom6 = (QuotientUOM) ab.invert();
		assertThat(uom6.getScalingFactor(), closeTo(Quantity.createAmount("0.5"), DELTA6));
		assertTrue(uom6.getDividend().equals(sys.getOne()));
		assertTrue(uom6.getDivisor().equals(ab));
		assertThat(uom6.getOffset(), closeTo(BigDecimal.ZERO, DELTA6));

		sys.clearCache();

		// power
		a = sys.createScalarUOM(UnitType.CUSTOM, "a", "a", "A");
		PowerUOM a2 = sys.createPowerUOM(UnitType.CUSTOM, "name", "symbol", "custom", a, 2);
		a2.setScalingFactor(two);

		assertThat(a2.getScalingFactor(), closeTo(two, DELTA6));
		assertTrue(a2.getBase().equals(a));
		assertTrue(a2.getPower() == 2);
		assertThat(a2.getOffset(), closeTo(BigDecimal.ZERO, DELTA6));
		assertTrue(a2.getAbscissaUnit().equals(a2));

		UnitOfMeasure uom8 = a2.divide(a);
		assertThat(uom8.getScalingFactor(), closeTo(two, DELTA6));
		assertThat(uom8.getOffset(), closeTo(BigDecimal.ZERO, DELTA6));
		assertTrue(uom8.getAbscissaUnit().equals(a));

		UnitOfMeasure uom9 = uom8.multiply(a);
		assertThat(uom9.getScalingFactor(), closeTo(Quantity.createAmount("2"), DELTA6));
		assertThat(uom9.getOffset(), closeTo(BigDecimal.ZERO, DELTA6));
		u = uom9.getAbscissaUnit();
		assertTrue(u.getBaseSymbol().equals(a2.getBaseSymbol()));

		sys.unregisterUnit(a);
		uom = sys.getUOM(a.getSymbol());
		assertTrue(uom == null);

		sys.clearCache();

		// again
		a = sys.createScalarUOM(UnitType.CUSTOM, "a", "a", "A");
		b = sys.createScalarUOM(UnitType.CUSTOM, "b", "b", "B");
		ScalarUOM c = sys.createScalarUOM(UnitType.CUSTOM, "c", "c", "C");
		ScalarUOM x = sys.createScalarUOM(UnitType.CUSTOM, "x", "x", "X");
		ScalarUOM e = sys.createScalarUOM(UnitType.CUSTOM, "e", "e", "E");

		uom = sys.createProductUOM(UnitType.CUSTOM, "", "a*2", "", a, a);
		assertTrue(uom.divide(a).equals(a));
		String symbol = uom.getSymbol();
		uom2 = sys.getUOM(symbol);
		assertTrue(uom2.equals(uom));

		aOverb = sys.createQuotientUOM(UnitType.CUSTOM, "", "a/b", "", a, b);
		assertTrue(aOverb.multiply(b).equals(a));

		QuotientUOM cOverx = sys.createQuotientUOM(UnitType.CUSTOM, "", "c/x", "", c, x);
		assertTrue(aOverb.divide(cOverx).multiply(cOverx).equals(aOverb));
		assertTrue(aOverb.multiply(cOverx).divide(cOverx).equals(aOverb));

		ProductUOM axb = sys.createProductUOM(UnitType.CUSTOM, "", "a.b", "", a, b);
		uom = sys.getUOM(axb.getSymbol());
		assertTrue(uom.equals(axb));
		assertTrue(axb.divide(a).equals(b));

		symbol = axb.getSymbol() + "." + axb.getSymbol();
		ProductUOM axbsq = sys.createProductUOM(UnitType.CUSTOM, "", symbol, "", axb, axb);
		uom = axbsq.divide(axb);
		assertTrue(uom.getBaseSymbol().equals(axb.getBaseSymbol()));

		PowerUOM b2 = sys.createPowerUOM(UnitType.CUSTOM, "b2", "b*2", "", b, 2);

		symbol = axb.getBaseSymbol();
		uom = sys.getUOM(symbol);
		assertTrue(uom != null);

		PowerUOM axb2 = sys.createPowerUOM(UnitType.CUSTOM, "axb2", "(a.b)*2", "", axb, 2);
		uom = axb2.divide(axb);
		assertTrue(uom.getBaseSymbol().equals(axb.getBaseSymbol()));
		assertTrue(uom.equals(axb));

		PowerUOM aOverb2 = sys.createPowerUOM(UnitType.CUSTOM, "aOverb2", "(a/b)*2", "", aOverb, 2);
		uom = aOverb2.multiply(b2);
		assertTrue(uom.getBaseSymbol().equals(a2.getBaseSymbol()));

		symbol = axb.getSymbol() + "^-2";
		PowerUOM axbm2 = sys.createPowerUOM(UnitType.CUSTOM, "", symbol, "", axb, -2);
		uom = axbm2.multiply(axb2);
		assertTrue(uom.getSymbol().indexOf(axb.getSymbol()) != -1);

		ProductUOM cxd = sys.createProductUOM(UnitType.CUSTOM, "", "c.D", "", c, x);
		final char MULT = 0xB7;
		StringBuffer sb = new StringBuffer();
		sb.append("(c").append(MULT).append("x)");
		String str = sb.toString();
		assertTrue(cxd.getBaseSymbol().indexOf(str) != -1);

		QuotientUOM abdivcd = sys.createQuotientUOM(UnitType.CUSTOM, "", "(a.b)/(c.D)", "", axb, cxd);
		sb = new StringBuffer();
		sb.append("(a").append(MULT).append("b)/(c").append(MULT).append("x)");
		str = sb.toString();
		assertTrue(abdivcd.getBaseSymbol().indexOf(str) != -1);

		QuotientUOM cde = sys.createQuotientUOM(UnitType.CUSTOM, "", "(c.D)/(e)", "", cxd, e);
		sb = new StringBuffer();
		sb.append("(c").append(MULT).append("x)/e");
		str = sb.toString();
		assertTrue(cde.getBaseSymbol().indexOf(str) != -1);
	}

	@Test
	public void testUSUnits() throws Exception {

		MeasurementSystem sys = uomService.getUnifiedSystem();

		UnitOfMeasure foot = sys.getUOM(Unit.FOOT);
		UnitOfMeasure second = sys.getSecond();
		UnitOfMeasure gal = sys.getUOM(Unit.US_GALLON);
		ScalarUOM flush = sys.createScalarUOM(UnitType.CUSTOM, "flush", "f", "");
		QuotientUOM gpf = sys.createQuotientUOM(UnitType.CUSTOM, "gal per flush", "gpf", "", gal, flush);
		QuotientUOM velocity = sys.createQuotientUOM(UnitType.VELOCITY, "velocity", "v", "velocity", foot, second);

		UnitOfMeasure litre = sys.getUOM(Unit.LITRE);
		QuotientUOM lpf = sys.createQuotientUOM(UnitType.CUSTOM, "litre per flush", "lpf", "", litre, flush);

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

		MeasurementSystem sys = uomService.getUnifiedSystem();

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
		MeasurementSystem sys = uomService.getUnifiedSystem();
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
	}

	@Test
	public void testTime() throws Exception {

		MeasurementSystem sys = uomService.getUnifiedSystem();

		UnitOfMeasure s2 = sys.getUOM(Unit.SQUARE_SECOND);
		UnitOfMeasure second = sys.getSecond();
		UnitOfMeasure min = sys.getMinute();
		UnitOfMeasure hour = sys.getHour();
		UnitOfMeasure msec = sys.getUOM(Unit.MILLISECOND);
		PowerUOM min2 = sys.createPowerUOM(UnitType.TIME, "sqMin", "min^2", null, min, 2);

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
		assertTrue(u.equals(sys.getOne()));

		Quantity q1 = new Quantity(BigDecimal.ONE, u);
		Quantity q2 = q1.convert(sys.getOne());
		assertThat(q2.getAmount(), closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(q2.getUOM().equals(sys.getOne()));

		u = second.invert();

		assertTrue(((QuotientUOM) u).getDividend().equals(sys.getOne()));
		assertTrue(((QuotientUOM) u).getDivisor().equals(second));

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
	public void testBaseSymbols() throws Exception {
		final char deg = 0xB0;
		final char times = 0xB7;
		final char sq = 0xB2;
		final char cu = 0xB3;
		StringBuffer sb = new StringBuffer();
		MeasurementSystem sys = uomService.getUnifiedSystem();

		String symbol = sys.getOne().getBaseSymbol();
		assertTrue(symbol.equals("1"));

		symbol = sys.getSecond().getBaseSymbol();
		assertTrue(symbol.equals("s"));

		symbol = sys.getUOM(Unit.METRE).getBaseSymbol();
		assertTrue(symbol.equals("m"));

		UnitOfMeasure mm = sys.getUOM(Unit.MILLIMETRE);

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

		symbol = sys.getUOM(Unit.KILOGRAM).getSymbol();
		assertTrue(symbol.equals("kg"));

		symbol = sys.getUOM(Unit.KILOGRAM).getBaseSymbol();
		assertTrue(symbol.equals("kg"));

		symbol = sys.getUOM(Unit.CUBIC_METRE).getBaseSymbol();
		assertTrue(symbol.equals("m" + cu));

		symbol = sys.getUOM(Unit.LITRE).getBaseSymbol();
		assertTrue(symbol.equals("m" + cu));

		symbol = sys.getUOM(Unit.NEWTON).getBaseSymbol();
		sb = new StringBuffer();
		sb.append("(kg").append(times).append("m)/s").append(sq);
		assertTrue(symbol.equals(sb.toString()));

		symbol = sys.getUOM(Unit.WATT).getBaseSymbol();
		sb = new StringBuffer();
		sb.append("(kg").append(times).append("m").append(sq).append(")/s").append((char) 0xB3);
		assertTrue(symbol.equals(sb.toString()));

		symbol = sys.getUOM(Unit.NEWTON_METRE).getBaseSymbol();
		sb = new StringBuffer();
		sb.append("(kg").append(times).append("m").append(sq).append(")/s").append(sq);
		assertTrue(symbol.equals(sb.toString()));

		symbol = sys.getUOM(Unit.VOLT).getBaseSymbol();
		sb = new StringBuffer();
		sb.append("(kg").append(times).append("m").append(sq).append(")/(A").append(times).append("s").append(cu)
				.append(')');
		assertTrue(symbol.equals(sb.toString()));

		symbol = sys.getUOM(Unit.OHM).getBaseSymbol();
		sb = new StringBuffer();
		sb.append("(kg").append(times).append("m").append(sq).append(")/(A").append(sq).append(times).append("s")
				.append(cu).append(')');
		assertTrue(symbol.equals(sb.toString()));

		symbol = sys.getUOM(Unit.WEBER).getBaseSymbol();
		sb = new StringBuffer();
		sb.append("(kg").append(times).append("m").append(sq).append(")/(A").append(times).append("s").append(sq)
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
		assertTrue(symbol.equals("Hz"));

		symbol = sys.getUOM(Unit.BECQUEREL).getSymbol();
		assertTrue(symbol.equals("Bq"));

		symbol = sys.getUOM(Unit.GRAY).getBaseSymbol();
		sb = new StringBuffer();
		sb.append("m").append((char) 0xB2).append("/s").append((char) 0xB2);
		assertTrue(symbol.equals(sb.toString()));

		symbol = sys.getUOM(Unit.KATAL).getBaseSymbol();
		assertTrue(symbol.equals("1/s"));

	}

	@Test
	public void testConversions1() throws Exception {
		MeasurementSystem sys = uomService.getUnifiedSystem();

		UnitOfMeasure cm = sys.getUOM(Unit.CENTIMETRE);
		UnitOfMeasure m = sys.getUOM(Unit.METRE);
		UnitOfMeasure N = sys.getUOM(Unit.NEWTON);
		UnitOfMeasure Nm = sys.getUOM(Unit.NEWTON_METRE);
		UnitOfMeasure mps = sys.getUOM(Unit.METRE_PER_SECOND);
		UnitOfMeasure sqm = sys.getUOM(Unit.SQUARE_METRE);
		ProductUOM mm = sys.createProductUOM(UnitType.AREA, "mxm", "mTimesm", "", m, m);
		ProductUOM mcm = sys.createProductUOM(UnitType.AREA, "mxcm", "mxcm", "", m, cm);
		UnitOfMeasure s2 = sys.getUOM(Unit.SQUARE_SECOND);
		QuotientUOM minOverSec = sys.createQuotientUOM(UnitType.TIME, "minsec", "min/sec", "", sys.getMinute(),
				sys.getSecond());

		ProductUOM minOverSecTimesSec = sys.createProductUOM(UnitType.TIME, "minOverSecTimesSec", "minOverSecTimesSec",
				"minOverSecTimesSec", minOverSec, sys.getSecond());

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

		PowerUOM perSec = sys.createPowerUOM(UnitType.TIME, "per second", "perSec", null, sys.getSecond(), -1);
		assertTrue(oneDivSec.getBaseSymbol().equals(perSec.getBaseSymbol()));

		UnitOfMeasure u = sys.getSecond().invert();
		assertTrue(u.getBaseSymbol().equals(perSec.getBaseSymbol()));

		inverted = u.invert();
		assertTrue(inverted.equals(sys.getSecond()));

		sys.unregisterUnit(perSec);

		UnitOfMeasure oneOverSec = sys.getUOM("1/s");
		assertTrue(oneOverSec.getBaseSymbol().equals(perSec.getBaseSymbol()));

		inverted = oneOverSec.invert();
		assertTrue(inverted.equals(sys.getSecond()));

		Conversion conversion = new Conversion(Quantity.createAmount("60"), sys.getUOM(Unit.SQUARE_SECOND));
		ProductUOM minTimesSec = sys.createProductUOM(UnitType.TIME, "minsec", "minxsec", "minute times a second",
				sys.getMinute(), sys.getSecond());
		minTimesSec.setConversion(conversion);

		UnitOfMeasure sqMin = sys.getUOM("min^2");
		if (sqMin == null) {
			sqMin = sys.createPowerUOM(UnitType.TIME, "square minute", "min^2", null, sys.getUOM(Unit.MINUTE), 2);
			conversion = new Conversion(Quantity.createAmount("3600"), sys.getUOM(Unit.SQUARE_SECOND));
			sqMin.setConversion(conversion);
		}

		PowerUOM perSec2 = sys.createPowerUOM(UnitType.TIME, "per second squared", "perSec^2", null, sys.getSecond(),
				-2);

		PowerUOM perMin = sys.createPowerUOM(UnitType.TIME, "per minute", "perMin", null, sys.getMinute(), -1);
		conversion = new Conversion(BigDecimal.ONE.divide(Quantity.createAmount("60"), UnitOfMeasure.MATH_CONTEXT),
				perSec);
		perMin.setConversion(conversion);

		PowerUOM perMin2 = sys.createPowerUOM(UnitType.TIME, "per minute squared", "perMin^2", null, sys.getMinute(),
				-2);
		conversion = new Conversion(BigDecimal.ONE.divide(Quantity.createAmount("3600"), UnitOfMeasure.MATH_CONTEXT),
				perSec2);
		perMin2.setConversion(conversion);

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

		bd = sys.getUOM(Unit.GRAVITY).getConversionFactor(sys.getUOM(Unit.FEET_PER_SECOND_SQUARED));
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
		MeasurementSystem sys = uomService.getUnifiedSystem();
		sys.clearCache();

		UnitOfMeasure m3 = sys.getUOM(Unit.CUBIC_METRE);
		UnitOfMeasure degree = sys.getUOM(Unit.DEGREE);
		UnitOfMeasure radian = sys.getUOM(Unit.RADIAN);
		UnitOfMeasure kgPerM3 = sys.getUOM(Unit.KILOGRAM_PER_CUBIC_METRE);
		UnitOfMeasure mps = sys.getUOM(Unit.METRE_PER_SECOND);
		UnitOfMeasure pascal = sys.getUOM(Unit.PASCAL);
		UnitOfMeasure s2 = sys.getUOM(Unit.SQUARE_SECOND);
		UnitOfMeasure joule = sys.getUOM(Unit.JOULE);

		QuotientUOM rpm = sys.createQuotientUOM(UnitType.CUSTOM, "rpm", "rpm", "revolutions per minute", degree,
				sys.getMinute());
		rpm.setScalingFactor(Quantity.createAmount("360"));

		QuotientUOM rps = sys.createQuotientUOM(UnitType.CUSTOM, "rps", "rad/s", "radians per second", radian,
				sys.getSecond());

		UnitOfMeasure m3s = sys.getUOM(Unit.CUBIC_METRE_PER_SECOND);
		UnitOfMeasure ms2 = sys.getUOM(Unit.METRE_PER_SECOND_SQUARED);

		UnitOfMeasure lbm = sys.getUOM(Unit.POUND_MASS);
		UnitOfMeasure ft = sys.getUOM(Unit.FOOT);
		UnitOfMeasure ft2 = sys.getUOM(Unit.SQUARE_FOOT);
		UnitOfMeasure ft3 = sys.getUOM(Unit.CUBIC_FOOT);
		UnitOfMeasure acreFoot = sys.createProductUOM(UnitType.VOLUME, "acreFoot", "ac-ft", "", sys.getUOM(Unit.ACRE),
				sys.getUOM(Unit.FOOT));
		UnitOfMeasure lbmPerFt3 = sys.createQuotientUOM(UnitType.DENSITY, "lbmPerFt3", "lbm/ft^3", null, lbm, ft3);
		UnitOfMeasure fps = sys.getUOM(Unit.FEET_PER_SECOND);
		UnitOfMeasure knot = sys.getUOM(Unit.KNOT);
		UnitOfMeasure usGal = sys.getUOM(Unit.US_GALLON);
		UnitOfMeasure btu = sys.getUOM(Unit.BTU);

		QuotientUOM gph = sys.createQuotientUOM(UnitType.VOLUMETRIC_FLOW, "gph", "gal/hr", "gallons per hour", usGal,
				sys.getHour());
		UnitOfMeasure mi = sys.getUOM(Unit.MILE);
		ProductUOM hrsec = sys.createProductUOM(UnitType.TIME, "", "hr.sec", "", sys.getHour(), sys.getSecond());
		QuotientUOM mphs = sys.createQuotientUOM(UnitType.ACCELERATION, "mph/sec", "mi/hr-sec",
				"mile per hour per second", mi, hrsec);

		Conversion conversion = new Conversion(Quantity.createAmount("3386.389"), pascal);
		UnitOfMeasure inHg = sys.createScalarUOM(UnitType.PRESSURE, "inHg", "inHg", "inHg");
		inHg.setConversion(conversion);

		conversion = new Conversion(Quantity.createAmount("101325"), pascal);
		UnitOfMeasure atm = sys.createScalarUOM(UnitType.PRESSURE, "atm", "atm", "standard atmosphere");
		atm.setConversion(conversion);

		ProductUOM ft2ft = sys.createProductUOM(UnitType.VOLUME, "ft2ft", "ft2ft", null, ft2, ft);

		BigDecimal bd = hrsec.getConversionFactor(s2);
		assertThat(bd, closeTo(Quantity.createAmount("3600"), DELTA6));

		bd = s2.getConversionFactor(hrsec);
		assertThat(bd, closeTo(Quantity.createAmount("2.777777777777778E-04"), DELTA6));

		UnitOfMeasure cubicFt = ft2.multiply(ft);
		bd = cubicFt.getConversionFactor(ft3);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

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

		bd = gph.getConversionFactor(m3s);
		assertThat(bd, closeTo(Quantity.createAmount("1.051503273E-06"), DELTA6));

		bd = m3s.getConversionFactor(gph);
		assertThat(bd, closeTo(Quantity.createAmount("951019.3884893342"), DELTA6));

		bd = mphs.getConversionFactor(ms2);
		assertThat(bd, closeTo(Quantity.createAmount("0.44704"), DELTA6));

		bd = ms2.getConversionFactor(mphs);
		assertThat(bd, closeTo(Quantity.createAmount("2.236936292054402"), DELTA6));

		bd = pascal.getConversionFactor(inHg);
		assertThat(bd, closeTo(Quantity.createAmount("2.952998016471232E-04"), DELTA6));

		bd = inHg.getConversionFactor(pascal);
		assertThat(bd, closeTo(Quantity.createAmount("3386.389"), DELTA6));

		bd = atm.getConversionFactor(inHg);
		assertThat(bd, closeTo(Quantity.createAmount("29.92125240189478"), DELTA6));

		bd = inHg.getConversionFactor(atm);
		assertThat(bd, closeTo(Quantity.createAmount("0.0334210609425117"), DELTA6));

		bd = btu.getConversionFactor(joule);
		assertThat(bd, closeTo(Quantity.createAmount("1055.05585262"), DELTA6));

		bd = joule.getConversionFactor(btu);
		assertThat(bd, closeTo(Quantity.createAmount("9.478171203133172E-04"), DELTA6));

	}

	@Test
	public void testConversions3() throws Exception {
		MeasurementSystem sys = uomService.getUnifiedSystem();

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
		UnitOfMeasure bq = sys.getUOM(Unit.BECQUEREL);
		UnitOfMeasure hertz = sys.getUOM(Unit.HERTZ);
		UnitOfMeasure gray = sys.getUOM(Unit.GRAY);
		UnitOfMeasure sievert = sys.getUOM(Unit.SIEVERT);

		QuotientUOM WeberPerSec = sys.createQuotientUOM(UnitType.ELECTROMOTIVE_FORCE, "W/s", "W/s", null, weber,
				second);
		QuotientUOM WeberPerAmp = sys.createQuotientUOM(UnitType.INDUCTANCE, "W/A", "W/A", null, weber, amp);
		ProductUOM fTimesV = sys.createProductUOM(UnitType.ELECTRIC_CHARGE, "FxV", "FxV", null, farad, volt);
		QuotientUOM WPerAmp = sys.createQuotientUOM(UnitType.ELECTROMOTIVE_FORCE, "Watt/A", "Watt/A", null, watt, amp);
		QuotientUOM VPerA = sys.createQuotientUOM(UnitType.ELECTRICAL_RESISTANCE, "V/A", "V/A", null, volt, amp);
		QuotientUOM CPerV = sys.createQuotientUOM(UnitType.CAPACITANCE, "C/V", "C/V", null, coulomb, volt);
		ProductUOM VTimesSec = sys.createProductUOM(UnitType.MAGNETIC_FLUX, "Vxs", "Vxs", null, volt, second);
		ProductUOM cdTimesSr = sys.createProductUOM(UnitType.LUMINOUS_FLUX, "cdxsr", "cdxsr", null, cd, sr);

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

		bd = gray.getConversionFactor(sievert);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		bd = sievert.getConversionFactor(gray);
		assertThat(bd, closeTo(BigDecimal.ONE, DELTA6));

		assertTrue(bq.getBaseSymbol().equals(hertz.getSymbol()));

	}

	@Test
	public void testConversions4() throws Exception {
		MeasurementSystem sys = uomService.getUnifiedSystem();

		UnitOfMeasure K = sys.getUOM(Unit.KELVIN);
		UnitOfMeasure C = sys.getUOM(Unit.CELSIUS);

		UnitOfMeasure R = sys.getUOM(Unit.RANKINE);
		UnitOfMeasure F = sys.getUOM(Unit.FAHRENHEIT);

		BigDecimal fiveNinths = Quantity.createAmount("5").divide(Quantity.createAmount("9"),
				Quantity.getMathContext());
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

	}

	// @Test
	public void testPerformance() throws Exception {
		int its = 25000;
		MeasurementSystem sys = uomService.getUnifiedSystem();

		UnitOfMeasure metre = sys.getUOM(Unit.METRE);
		UnitOfMeasure cm = sys.getUOM(Unit.CENTIMETRE);
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
}
