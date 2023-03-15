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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.point85.uom.Constant;
import org.point85.uom.Prefix;
import org.point85.uom.Quantity;
import org.point85.uom.Unit;
import org.point85.uom.UnitOfMeasure;
import org.point85.uom.UnitType;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;
import com.google.gson.Gson;

public class TestUnifiedCode extends BaseTest {
	private static final String FROM = "from/";
	private static final String TO = "to/";
	private static final String BASE_URL = "https://ucum.nlm.nih.gov/ucum-service/v1/ucumtransform/";

	private Gson gson = new Gson();

	@Test
	public void runTest() throws Exception {
		double amount = 10;

		Quantity from = null;
		Quantity to = null;
		double convertedAmount = 0;

		// astronomical unit to miles
		from = new Quantity(amount, sys.getUOM(Unit.ASTRONOMICAL_UNIT));
		to = from.convert(sys.getUOM(Unit.MILE));

		convertedAmount = wsConvert(amount, "AU", "[mi_i]");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA_10));

		// atmosphere to psi
		from = new Quantity(amount, sys.getUOM(Unit.ATMOSPHERE));
		to = from.convert(sys.getUOM(Unit.PSI));

		convertedAmount = wsConvert(amount, "atm", "[psi]");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA5));

		// in Hg to PSI
		from = new Quantity(amount, sys.getUOM(Unit.IN_HG));
		to = from.convert(sys.getUOM(Unit.PSI));

		convertedAmount = wsConvert(amount, "[in_i'Hg]", "[psi]");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA4));

		// days to weeks
		from = new Quantity(amount, sys.getUOM(Unit.DAY));
		to = from.convert(sys.getUOM(Unit.WEEK));

		convertedAmount = wsConvert(amount, "d", "wk");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// kilowatt-hours to Joules
		UnitOfMeasure kwh = sys.getUOM(Prefix.KILO, sys.getUOM(Unit.WATT_HOUR));
		UnitOfMeasure kJ = sys.getUOM(Prefix.KILO, sys.getUOM(Unit.JOULE));
		from = new Quantity(amount, kwh);
		to = from.convert(kJ);

		convertedAmount = wsConvert(amount, "kW.h", "kJ");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA4));

		// hour to second
		from = new Quantity(amount, sys.getHour());
		to = from.convert(sys.getSecond());

		convertedAmount = wsConvert(amount, "h", "s");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		/// US gal to litres
		// hour to second
		from = new Quantity(amount, Unit.US_GALLON);
		to = from.convert(Unit.LITRE);

		convertedAmount = wsConvert(amount, "[gal_us]", "l");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// elementary charge to Coulombs
		from = sys.getQuantity(Constant.ELEMENTARY_CHARGE);
		to = from.convert(sys.getUOM(Unit.COULOMB));

		convertedAmount = wsConvert(from.getAmount(), "[e]", "C");
		assertTrue(isCloseTo(from.getAmount(), convertedAmount, DELTA6));

		// mole
		from = new Quantity(amount, sys.getUOM(Unit.MOLE));
		to = from.convert(sys.getUOM(Unit.MOLE));

		convertedAmount = wsConvert(amount, "mol", "mol");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// velocity of light
		from = sys.getQuantity(Constant.LIGHT_VELOCITY);
		to = from.convert(sys.getUOM(Unit.MILES_PER_HOUR));

		convertedAmount = wsConvert(2.99792458E+08, "m/s", "[mi_i]/h");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, 1d));

		// acceleration of gravity
		from = sys.getQuantity(Constant.GRAVITY);
		to = from.convert(sys.getUOM(Unit.FEET_PER_SEC_SQUARED));

		convertedAmount = wsConvert(9.80665, "m/s2", "[ft_i]/s2");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA4));

		// feet to in
		from = new Quantity(amount, sys.getUOM(Unit.FOOT));
		to = from.convert(sys.getUOM(Unit.INCH));

		convertedAmount = wsConvert(amount, "[ft_i]", "[in_i]");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// in to cm
		from = new Quantity(amount, sys.getUOM(Unit.INCH));
		UnitOfMeasure cm = sys.getUOM(Prefix.CENTI, sys.getUOM(Unit.METRE));
		to = from.convert(cm);

		convertedAmount = wsConvert(amount, "[in_i]", "cm");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// ft-lbf to Newton
		from = new Quantity(amount, sys.getUOM(Unit.FOOT_POUND_FORCE));
		to = from.convert(sys.getUOM(Unit.NEWTON_METRE));

		convertedAmount = wsConvert(amount, "[ft_i].[lbf_av]", "N.m");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// acres to square metres
		from = new Quantity(amount, sys.getUOM(Unit.ACRE));
		to = from.convert(sys.getUOM(Unit.SQUARE_METRE));

		convertedAmount = wsConvert(amount, "[acr_us]", "m2");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA0));

		// US gallon to Imperial gal
		from = new Quantity(amount, sys.getUOM(Unit.US_GALLON));
		to = from.convert(sys.getUOM(Unit.BR_GALLON));

		convertedAmount = wsConvert(amount, "[gal_us]", "[gal_br]");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA4));

		// US gal to cm3
		from = new Quantity(amount, sys.getUOM(Unit.US_GALLON));

		UnitOfMeasure cm3 = sys.getUOM(Prefix.MICRO, sys.getUOM(Unit.CUBIC_METRE));
		sys.createScalarUOM(UnitType.VOLUME, "cubic cm", "cm3", null);

		to = from.convert(cm3);

		convertedAmount = wsConvert(amount, "[gal_us]", "cm3");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA3));

		// lbm to kg
		from = new Quantity(amount, sys.getUOM(Unit.POUND_MASS));
		UnitOfMeasure kg = sys.getUOM(Unit.KILOGRAM);
		to = from.convert(kg);

		convertedAmount = wsConvert(amount, "[lb_av]", "kg");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// N to lbf
		from = new Quantity(amount, sys.getUOM(Unit.NEWTON));
		to = from.convert(sys.getUOM(Unit.POUND_FORCE));

		convertedAmount = wsConvert(amount, "N", "[lbf_av]");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// thermochemical calorie to Joule
		from = new Quantity(amount, sys.getUOM(Unit.CALORIE));
		to = from.convert(sys.getUOM(Unit.JOULE));

		convertedAmount = wsConvert(amount, "cal_th", "J");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// kilocalorie to BTU
		UnitOfMeasure kcal = sys.getUOM(Prefix.KILO, sys.getUOM(Unit.CALORIE));

		from = new Quantity(amount, kcal);
		to = from.convert(sys.getUOM(Unit.BTU));

		convertedAmount = wsConvert(amount, "kcal_th", "[Btu_IT]");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// psi to kilopascal
		from = new Quantity(amount, sys.getUOM(Unit.PSI));
		UnitOfMeasure kPa = sys.getUOM(Prefix.KILO, sys.getUOM(Unit.PASCAL));
		to = from.convert(kPa);

		convertedAmount = wsConvert(amount, "[psi]", "kPa");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// HP to kilowatt
		from = new Quantity(amount, sys.getUOM(Unit.HP));
		UnitOfMeasure kW = sys.getUOM(Prefix.KILO, sys.getUOM(Unit.WATT));
		to = from.convert(kW);

		convertedAmount = wsConvert(amount, "[HP]", "kW");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// N.m to ft-lbf torque
		from = new Quantity(amount, sys.getUOM(Unit.NEWTON_METRE));
		to = from.convert(sys.getUOM(Unit.FOOT_POUND_FORCE));

		convertedAmount = wsConvert(amount, "N.m", "[ft_i].[lbf_av]");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// grain to pound mass
		from = new Quantity(amount, sys.getUOM(Unit.GRAIN));
		to = from.convert(sys.getUOM(Unit.POUND_MASS));

		convertedAmount = wsConvert(amount, "[gr]", "[lb_av]");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// kg to grain
		kg = sys.getUOM(Unit.KILOGRAM);
		from = new Quantity(amount, kg);
		to = from.convert(sys.getUOM(Unit.GRAIN));

		convertedAmount = wsConvert(amount, "kg", "[gr]");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA2));

		// kg to pound mass
		from = new Quantity(amount, kg);
		to = from.convert(sys.getUOM(Unit.POUND_MASS));

		convertedAmount = wsConvert(amount, "kg", "[lb_av]");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// degrees to radians
		from = new Quantity(amount, sys.getUOM(Unit.DEGREE));
		to = from.convert(sys.getUOM(Unit.RADIAN));

		convertedAmount = wsConvert(amount, "deg", "rad");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// knots to mi/hr
		from = new Quantity(amount, sys.getUOM(Unit.KNOT));
		to = from.convert(sys.getUOM(Unit.MILES_PER_HOUR));

		convertedAmount = wsConvert(amount, "[kn_br]", "[mi_i]/h");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA5));

		// kg to grains
		kg = sys.getUOM(Unit.KILOGRAM);
		from = new Quantity(amount, kg);
		to = from.convert(sys.getUOM(Unit.GRAIN));

		convertedAmount = wsConvert(amount, "kg", "[gr]");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA2));

		// a deg Fahrenheit to a deg Celsius
		UnitOfMeasure degF = sys.getUOM(Unit.FAHRENHEIT);
		UnitOfMeasure degC = sys.getUOM(Unit.CELSIUS);
		double factor = degF.getConversionFactor(degC);

		convertedAmount = wsConvert(1d, "[degF]", "Cel");
		assertTrue(isCloseTo(factor, convertedAmount, DELTA6));

		// a deg Kelvin to a deg Rankine
		UnitOfMeasure degK = sys.getUOM(Unit.KELVIN);
		UnitOfMeasure degR = sys.getUOM(Unit.RANKINE);
		factor = degK.getConversionFactor(degR);

		convertedAmount = wsConvert(1d, "K", "[degR]");
		assertTrue(isCloseTo(factor, convertedAmount, DELTA6));

		// lux
		Quantity lumen = new Quantity(amount, sys.getUOM(Unit.LUMEN));
		Quantity m2 = new Quantity(1d, sys.getUOM(Unit.SQUARE_METRE));
		Quantity lux = lumen.divide(m2);

		convertedAmount = wsConvert(lux.getAmount(), "lm/m2", "lx");
		assertTrue(isCloseTo(lux.getAmount(), convertedAmount, DELTA2));

		// carat to ounces
		from = new Quantity(amount, sys.getUOM(Unit.CARAT));
		to = from.convert(sys.getUOM(Unit.OUNCE));

		convertedAmount = wsConvert(amount, "[car_m]", "[oz_av]");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// Hertz and Becquerel
		from = new Quantity(amount, sys.getUOM(Unit.HERTZ));
		try {
			to = from.convert(sys.getUOM(Unit.BECQUEREL));
			convertedAmount = wsConvert(amount, "Hz", "Bq");
			fail("No conversion");
		} catch (Exception e) {
		}

		// kWh to BTU
		from = new Quantity(amount, kwh);
		to = from.convert(sys.getUOM(Unit.BTU));

		convertedAmount = wsConvert(amount, "kW.h", "[Btu_IT]");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA3));

		// Angstrom to inch
		from = new Quantity(amount, sys.getUOM(Unit.ANGSTROM));
		to = from.convert(sys.getUOM(Unit.INCH));

		convertedAmount = wsConvert(amount, "Ao", "[in_i]");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// eV to joules
		from = new Quantity(amount, sys.getUOM(Unit.ELECTRON_VOLT));
		to = from.convert(sys.getUOM(Unit.JOULE));

		convertedAmount = wsConvert(amount, "[e].V", "J");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// joules to electron-volts
		from = new Quantity(amount, sys.getUOM(Unit.JOULE));
		to = from.convert(sys.getUOM(Unit.ELECTRON_VOLT));

		convertedAmount = wsConvert(amount, "J", "[e].V");
		Quantity convertedQty = new Quantity(convertedAmount, sys.getUOM(Unit.ELECTRON_VOLT));
		Quantity back = convertedQty.convert(sys.getUOM(Unit.JOULE));

		assertTrue(isCloseTo(back.getAmount(), from.getAmount(), DELTA4));

		// julian year to weeks
		from = new Quantity(amount, sys.getUOM(Unit.JULIAN_YEAR));
		to = from.convert(sys.getUOM(Unit.WEEK));

		convertedAmount = wsConvert(amount, "a_j", "wk");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA5));

		// fathom to metre
		from = new Quantity(amount, sys.getUOM(Unit.FATHOM));
		to = from.convert(sys.getUOM(Unit.METRE));

		convertedAmount = wsConvert(amount, "[fth_i]", "m");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// square metres to square yards
		from = new Quantity(amount, sys.getUOM(Unit.SQUARE_METRE));
		to = from.convert(sys.getUOM(Unit.SQUARE_YARD));

		convertedAmount = wsConvert(amount, "m2", "[syd_i]");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// cubic yards to cubic metres
		from = new Quantity(amount, sys.getUOM(Unit.CUBIC_YARD));
		to = from.convert(sys.getUOM(Unit.CUBIC_METRE));

		convertedAmount = wsConvert(amount, "[cyd_i]", "m3");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// mils to millmetres
		from = new Quantity(amount, sys.getUOM(Unit.MIL));
		UnitOfMeasure mm = sys.getUOM(Prefix.MILLI, sys.getUOM(Unit.METRE));
		to = from.convert(mm);

		convertedAmount = wsConvert(amount, "[mil_i]", "mm");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// US barrels to cubic meters
		from = new Quantity(amount, sys.getUOM(Unit.US_BARREL));
		to = from.convert(sys.getUOM(Unit.CUBIC_METRE));

		convertedAmount = wsConvert(amount, "[bbl_us]", "m3");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// cord to cubic metres
		from = new Quantity(amount, sys.getUOM(Unit.CORD));
		to = from.convert(sys.getUOM(Unit.CUBIC_METRE));

		convertedAmount = wsConvert(amount, "[crd_us]", "m3");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// cubic metres to US bushels
		from = new Quantity(amount, sys.getUOM(Unit.CUBIC_METRE));
		to = from.convert(sys.getUOM(Unit.US_BUSHEL));

		convertedAmount = wsConvert(amount, "m3", "[bu_us]");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA4));

		// British bushels to cubic metres
		from = new Quantity(amount, sys.getUOM(Unit.BR_BUSHEL));
		to = from.convert(sys.getUOM(Unit.CUBIC_METRE));

		convertedAmount = wsConvert(amount, "[bu_br]", "m3");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// point to inches
		from = new Quantity(amount, sys.getUOM(Unit.POINT));
		to = from.convert(sys.getUOM(Unit.INCH));

		convertedAmount = wsConvert(amount, "[pnt]", "[in_i]");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// bytes to bits
		from = new Quantity(amount, sys.getUOM(Unit.BYTE));
		to = from.convert(sys.getUOM(Unit.BIT));

		convertedAmount = wsConvert(amount, "By", "bit");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// rpm to Hz
		from = new Quantity(amount, sys.getUOM(Unit.REV_PER_MIN));
		to = from.convert(sys.getUOM(Unit.HERTZ));

		convertedAmount = wsConvert(amount, "min-1", "Hz");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA6));

		// tonne to lbm
		from = new Quantity(amount, sys.getUOM(Unit.TONNE));
		to = from.convert(sys.getUOM(Unit.POUND_MASS));

		convertedAmount = wsConvert(amount, "t", "[lb_av]");
		assertTrue(isCloseTo(to.getAmount(), convertedAmount, DELTA3));

	}

	private double wsConvert(double amount, String fromUnifiedSymbol, String toUnifiedSymbol)
			throws HttpRequestException, Exception {
		String fromUnit = prep(fromUnifiedSymbol);
		String toUnit = prep(toUnifiedSymbol);

		String conversionUrl = amount + "/" + FROM + fromUnit + "/" + TO + toUnit;
		String url = BASE_URL + conversionUrl;
		System.out.println(url);

		HttpRequest request = HttpRequest.get(url).accept("application/json");

		if (request.code() != 200) {
			throw new Exception("Web service call returned " + request.code());
		}

		double decimal = 0;
		String body = request.body();
		if (body.startsWith("{")) {
			WebServiceConversion wsResponse = gson.fromJson(body, WebServiceConversion.class);

			decimal = getResultQuantity(wsResponse);
		} else {
			throw new Exception(body);
		}
		return decimal;
	}

	private String prep(String unit) {
		String preped = null;
		if (unit != null) {
			preped = unit.replace("[", "%5B").replace("]", "%5D");
		}
		return preped;
	}

	private double getResultQuantity(WebServiceConversion conversion) {
		return conversion.getWebServiceResponse().getResponse().getResultQuantity();
	}
}
