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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.MathContext;

import org.junit.Test;
import org.point85.uom.Conversion;
import org.point85.uom.Prefix;
import org.point85.uom.Quantity;
import org.point85.uom.Unit;
import org.point85.uom.UnitOfMeasure;
import org.point85.uom.UnitType;

public class TestBridges extends BaseTest {

	@Test
	public void testBridges() throws Exception {

		// SI
		UnitOfMeasure kg = sys.getUOM(Unit.KILOGRAM);
		UnitOfMeasure m = sys.getUOM(Unit.METRE);
		UnitOfMeasure km = sys.getUOM(Prefix.KILO, m);
		UnitOfMeasure litre = sys.getUOM(Unit.LITRE);
		UnitOfMeasure N = sys.getUOM(Unit.NEWTON);
		UnitOfMeasure m3 = sys.getUOM(Unit.CUBIC_METRE);
		UnitOfMeasure m2 = sys.getUOM(Unit.SQUARE_METRE);
		UnitOfMeasure Nm = sys.getUOM(Unit.NEWTON_METRE);
		UnitOfMeasure kPa = sys.getUOM(Prefix.KILO, sys.getUOM(Unit.PASCAL));
		UnitOfMeasure celsius = sys.getUOM(Unit.CELSIUS);

		// US
		UnitOfMeasure lbm = sys.getUOM(Unit.POUND_MASS);
		UnitOfMeasure lbf = sys.getUOM(Unit.POUND_FORCE);
		UnitOfMeasure mi = sys.getUOM(Unit.MILE);
		UnitOfMeasure ft = sys.getUOM(Unit.FOOT);
		UnitOfMeasure gal = sys.getUOM(Unit.US_GALLON);
		UnitOfMeasure ft2 = sys.getUOM(Unit.SQUARE_FOOT);
		UnitOfMeasure ft3 = sys.getUOM(Unit.CUBIC_FOOT);
		UnitOfMeasure acre = sys.getUOM(Unit.ACRE);
		UnitOfMeasure ftlbf = sys.getUOM(Unit.FOOT_POUND_FORCE);
		UnitOfMeasure psi = sys.getUOM(Unit.PSI);
		UnitOfMeasure fahrenheit = sys.getUOM(Unit.FAHRENHEIT);

		Quantity q1 = new Quantity(BigDecimal.TEN, ft);
		Quantity q2 = q1.convert(m);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("3.048"), DELTA6));
		Quantity q3 = q2.convert(q1.getUOM());
		assertThat(q3.getAmount(), closeTo(BigDecimal.TEN, DELTA6));

		q1 = new Quantity(BigDecimal.TEN, kg);
		q2 = q1.convert(lbm);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("22.0462"), DELTA4));
		q3 = q2.convert(q1.getUOM());
		assertThat(q3.getAmount(), closeTo(BigDecimal.TEN, DELTA6));

		q1 = new Quantity(Quantity.createAmount("212"), fahrenheit);
		q2 = q1.convert(celsius);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("100"), DELTA6));
		q3 = q2.convert(q1.getUOM());
		assertThat(q3.getAmount(), closeTo(Quantity.createAmount("212"), DELTA6));

		UnitOfMeasure mm = sys.createProductUOM(UnitType.AREA, "name", "mxm", "", m, m);

		q1 = new Quantity(BigDecimal.TEN, mm);
		q2 = q1.convert(ft2);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("107.639104167"), DELTA6));
		q2 = q2.convert(m2);
		assertThat(q2.getAmount(), closeTo(BigDecimal.TEN, DELTA6));

		UnitOfMeasure mhr = sys.getUOM("m/hr");

		if (mhr == null) {
			Conversion conversion = new Conversion(
					BigDecimal.ONE.divide(Quantity.createAmount("3600"), MathContext.DECIMAL64),
					sys.getUOM(Unit.METRE_PER_SECOND));
			mhr = sys.createScalarUOM(UnitType.VELOCITY, "m/hr", "m/hr", "");
			mhr.setConversion(conversion);
		}

		q1 = new Quantity(BigDecimal.TEN, psi);
		q2 = q1.convert(kPa);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("68.94757280343134"), DELTA6));
		q2 = q2.convert(psi);
		assertThat(q2.getAmount(), closeTo(BigDecimal.TEN, DELTA6));

		q1 = new Quantity(BigDecimal.TEN, mhr);
		q2 = q1.convert(sys.getUOM(Unit.FEET_PER_SECOND));
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("0.009113444152814231"), DELTA6));
		q2 = q2.convert(mhr);
		assertThat(q2.getAmount(), closeTo(BigDecimal.TEN, DELTA6));

		q1 = new Quantity(BigDecimal.TEN, gal);
		q2 = q1.convert(litre);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("37.8541178"), DELTA6));
		q2 = q2.convert(gal);
		assertThat(q2.getAmount(), closeTo(BigDecimal.TEN, DELTA6));

		q1 = new Quantity(BigDecimal.TEN, m3);
		q2 = q1.convert(ft3);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("353.1466672398284"), DELTA6));
		q2 = q2.convert(m3);
		assertThat(q2.getAmount(), closeTo(BigDecimal.TEN, DELTA6));

		q1 = new Quantity(BigDecimal.TEN, N);
		q2 = q1.convert(lbf);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("2.24809"), DELTA6));
		q2 = q2.convert(N);
		assertThat(q2.getAmount(), closeTo(BigDecimal.TEN, DELTA6));

		q1 = new Quantity(BigDecimal.TEN, ftlbf);
		q2 = q1.convert(Nm);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("13.558179483314004"), DELTA6));
		q2 = q2.convert(ftlbf);
		assertThat(q2.getAmount(), closeTo(BigDecimal.TEN, DELTA6));

		q1 = new Quantity(BigDecimal.TEN, lbm);
		q2 = q1.convert(kg);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("4.5359237"), DELTA6));
		q2 = q2.convert(lbm);
		assertThat(q2.getAmount(), closeTo(BigDecimal.TEN, DELTA6));

		q1 = new Quantity(BigDecimal.TEN, km);
		q2 = q1.convert(mi);
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("6.21371192237"), DELTA6));
		q2 = q2.convert(km);
		assertThat(q2.getAmount(), closeTo(BigDecimal.TEN, DELTA6));

		// length
		q1 = new Quantity(BigDecimal.TEN, sys.getUOM(Unit.METRE));
		q2 = q1.convert(sys.getUOM(Unit.INCH));
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("393.7007874015748"), DELTA6));
		q2 = q2.convert(sys.getUOM(Unit.METRE));
		assertThat(q2.getAmount(), closeTo(BigDecimal.TEN, DELTA6));

		q2 = q1.convert(sys.getUOM(Unit.FOOT));
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("32.80839895013123"), DELTA6));
		q2 = q2.convert(sys.getUOM(Unit.METRE));
		assertThat(q2.getAmount(), closeTo(BigDecimal.TEN, DELTA6));

		// area
		q1 = new Quantity(BigDecimal.TEN, sys.getUOM(Unit.SQUARE_METRE));
		q2 = q1.convert(sys.getUOM(Unit.SQUARE_INCH));
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("15500.031000062"), DELTA6));
		q2 = q2.convert(sys.getUOM(Unit.SQUARE_METRE));
		assertThat(q2.getAmount(), closeTo(BigDecimal.TEN, DELTA6));

		q2 = q1.convert(sys.getUOM(Unit.SQUARE_FOOT));
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("107.6391041670972"), DELTA6));
		q2 = q2.convert(sys.getUOM(Unit.SQUARE_METRE));
		assertThat(q2.getAmount(), closeTo(BigDecimal.TEN, DELTA6));

		// volume
		q1 = new Quantity(BigDecimal.TEN, sys.getUOM(Unit.LITRE));
		q2 = q1.convert(sys.getUOM(Unit.US_GALLON));
		assertThat(q2.getAmount(), closeTo(Quantity.createAmount("2.641720523581484"), DELTA6));
		q2 = q2.convert(sys.getUOM(Unit.LITRE));
		assertThat(q2.getAmount(), closeTo(BigDecimal.TEN, DELTA6));

		q1 = new Quantity("4.0468564224", m);
		q2 = new Quantity("1000", m);
		q3 = q1.multiply(q2);
		assertThat(q3.getAmount(), closeTo(Quantity.createAmount("4046.8564224"), DELTA6));

		UnitOfMeasure uom = q3.getUOM();
		UnitOfMeasure base = uom.getPowerBase();
		BigDecimal sf = uom.getScalingFactor();

		assertTrue(uom.getAbscissaUnit().equals(m2));
		assertTrue(base.equals(m));
		assertThat(sf, closeTo(BigDecimal.ONE, DELTA6));

		Quantity q4 = q3.convert(acre);
		assertThat(q4.getAmount(), closeTo(BigDecimal.ONE, DELTA6));
		assertTrue(q4.getUOM().equals(acre));

		UnitOfMeasure usSec = sys.getSecond();

		UnitOfMeasure v1 = sys.getUOM("m/hr");

		UnitOfMeasure v2 = sys.getUOM(Unit.METRE_PER_SECOND);
		UnitOfMeasure v3 = sys.createQuotientUOM(UnitType.VELOCITY, "", "ft/usec", "", ft, usSec);

		UnitOfMeasure d1 = sys.getUOM(Unit.KILOGRAM_PER_CUBIC_METRE);
		UnitOfMeasure d2 = sys.createQuotientUOM(UnitType.DENSITY, "density", "lbm/gal", "", lbm, gal);

		q1 = new Quantity(BigDecimal.TEN, v1);
		q2 = q1.convert(v3);

		q1 = new Quantity(BigDecimal.TEN, v1);
		q2 = q1.convert(v2);

		q1 = new Quantity(BigDecimal.TEN, d1);
		q2 = q1.convert(d2);

	}
}
