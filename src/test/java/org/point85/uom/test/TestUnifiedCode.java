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

import java.math.BigDecimal;

import org.junit.Test;
import org.point85.uom.Conversion;
import org.point85.uom.MeasurementSystem;
import org.point85.uom.Quantity;
import org.point85.uom.ScalarUOM;
import org.point85.uom.Unit;
import org.point85.uom.UnitOfMeasure;
import org.point85.uom.UnitType;

import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;
import com.google.gson.Gson;

public class TestUnifiedCode extends BaseTest {
	// http://www.xml4pharmaserver.com:8080/CDISCCTService/rest/ucumtransform/{source_quantity}/from/{source_unit}/to/{target_unit}
	private static final String FROM = "from/";
	private static final String TO = "to/";
	private static final String BASE_URL = "http://www.xml4pharmaserver.com:8080/CDISCCTService/rest/ucumtransform/";

	private Gson gson = new Gson();

	@Test
	public void runTest() throws Exception {
		String amount = "10";

		MeasurementSystem sys = MeasurementSystem.getUnifiedSystem();

		Quantity from = null;
		Quantity to = null;
		BigDecimal convertedAmount = null;

		// feet to in
		from = new Quantity(amount, sys.getUOM(Unit.FOOT));
		to = from.convert(sys.getUOM(Unit.INCH));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// in to cm
		from = new Quantity(amount, sys.getUOM(Unit.INCH));
		to = from.convert(sys.getUOM(Unit.CENTIMETRE));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// ft-lbf to Newton
		from = new Quantity(amount, sys.getUOM(Unit.FOOT_POUND_FORCE));
		to = from.convert(sys.getUOM(Unit.NEWTON_METRE));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// kilowatt-hours to Joules
		from = new Quantity(amount, sys.getUOM(Unit.KILOWATT_HOUR));
		to = from.convert(sys.getUOM(Unit.KILOJOULE));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA4));

		// acres to square metres
		from = new Quantity(amount, sys.getUOM(Unit.ACRE));
		to = from.convert(sys.getUOM(Unit.SQUARE_METRE));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA0));

		// US gallon to Imperial gal
		from = new Quantity(amount, sys.getUOM(Unit.US_GALLON));
		to = from.convert(sys.getUOM(Unit.BR_GALLON));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA4));

		// US gal to cm3
		from = new Quantity(amount, sys.getUOM(Unit.US_GALLON));

		ScalarUOM cm3 = sys.createScalarUOM(UnitType.VOLUME, "cubic cm", "cm3", null);
		cm3.setConversion(new Conversion(MeasurementSystem.MICRO, sys.getUOM(Unit.CUBIC_METRE)));
		cm3.setUnifiedSymbol("cm3");

		to = from.convert(cm3);

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA3));

		// lbm to kg
		from = new Quantity(amount, sys.getUOM(Unit.POUND_MASS));
		to = from.convert(sys.getUOM((Unit.KILOGRAM)));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// N to lbf
		from = new Quantity(amount, sys.getUOM(Unit.NEWTON));
		to = from.convert(sys.getUOM(Unit.POUND_FORCE));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// acceleration of gravity
		from = new Quantity(amount, sys.getUOM(Unit.GRAVITY));
		to = from.convert(sys.getUOM(Unit.METRE_PER_SECOND_SQUARED));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// mole
		from = new Quantity(amount, sys.getUOM(Unit.MOLE));
		to = from.convert(sys.getUOM(Unit.MOLE));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// calorie to Joule
		from = new Quantity(amount, sys.getUOM(Unit.CALORIE));
		to = from.convert(sys.getUOM(Unit.JOULE));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// kilocalorie to BTU
		ScalarUOM kcal = sys.createScalarUOM(UnitType.ENERGY, "kcal", "kcal", "kilocalorie");
		kcal.setConversion(new Conversion(MeasurementSystem.KILO, sys.getUOM(Unit.CALORIE)));
		kcal.setUnifiedSymbol("kcal_th");

		from = new Quantity(amount, kcal);
		to = from.convert(sys.getUOM(Unit.BTU));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// psi to kilopascal
		from = new Quantity(amount, sys.getUOM(Unit.PSI));
		to = from.convert(sys.getUOM(Unit.KILOPASCAL));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// HP to watt
		from = new Quantity(amount, sys.getUOM(Unit.HP));
		to = from.convert(sys.getUOM(Unit.KILOWATT));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// N.m to ft-lbf torque
		from = new Quantity(amount, sys.getUOM(Unit.NEWTON_METRE));
		to = from.convert(sys.getUOM(Unit.FOOT_POUND_FORCE));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// grain to pound mass
		from = new Quantity(amount, sys.getUOM(Unit.GRAIN));
		to = from.convert(sys.getUOM(Unit.POUND_MASS));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// mg to grain
		from = new Quantity(amount, sys.getUOM(Unit.MILLIGRAM));
		to = from.convert(sys.getUOM(Unit.GRAIN));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// kg to pound mass
		from = new Quantity(amount, sys.getUOM(Unit.KILOGRAM));
		to = from.convert(sys.getUOM(Unit.POUND_MASS));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// velocity of light
		from = new Quantity(amount, sys.getUOM(Unit.LIGHT_VELOCITY));
		to = from.convert(sys.getUOM(Unit.LIGHT_VELOCITY));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// degrees to radians
		from = new Quantity(amount, sys.getUOM(Unit.DEGREE));
		to = from.convert(sys.getUOM(Unit.RADIAN));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// knots to mi/hr
		from = new Quantity(amount, sys.getUOM(Unit.KNOT));
		to = from.convert(sys.getUOM(Unit.MILES_PER_HOUR));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA5));

		// kg to grains
		from = new Quantity(amount, sys.getUOM(Unit.KILOGRAM));
		to = from.convert(sys.getUOM(Unit.GRAIN));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA2));

		// a deg Fahrenheit to a deg Celsius
		UnitOfMeasure degF = sys.getUOM(Unit.FAHRENHEIT);
		UnitOfMeasure degC = sys.getUOM(Unit.CELSIUS);
		BigDecimal factor = degF.getConversionFactor(degC);

		convertedAmount = wsConvert("1", degF, degC);
		assertThat(factor, closeTo(convertedAmount, DELTA6));

		// a deg Kelvin to a deg Rankine
		UnitOfMeasure degK = sys.getUOM(Unit.KELVIN);
		UnitOfMeasure degR = sys.getUOM(Unit.RANKINE);
		factor = degK.getConversionFactor(degR);

		convertedAmount = wsConvert("1", degK, degR);
		assertThat(factor, closeTo(convertedAmount, DELTA6));

		// lux
		Quantity lumen = new Quantity(amount, sys.getUOM(Unit.LUMEN));
		Quantity m2 = new Quantity("1", sys.getUOM(Unit.SQUARE_METRE));
		Quantity lux = lumen.divide(m2);
		lux.getUOM().setUnifiedSymbol("lm/m2");

		convertedAmount = wsConvert(lux.getAmount().toPlainString(), lux.getUOM(), sys.getUOM(Unit.LUX));
		assertThat(lux.getAmount(), closeTo(convertedAmount, DELTA2));

		// carat to ounces
		from = new Quantity(amount, sys.getUOM(Unit.CARAT));
		to = from.convert(sys.getUOM(Unit.OUNCE));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		from = new Quantity(amount, sys.getUOM(Unit.HERTZ));
		to = from.convert(sys.getUOM(Unit.BECQUEREL));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// kWh to BTU
		from = new Quantity(amount, sys.getUOM(Unit.KILOWATT_HOUR));
		to = from.convert(sys.getUOM(Unit.BTU));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA3));

		// Angstrom to inch
		from = new Quantity(amount, sys.getUOM(Unit.ANGSTROM));
		to = from.convert(sys.getUOM(Unit.INCH));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// days to weeks
		from = new Quantity(amount, sys.getUOM(Unit.DAY));
		to = from.convert(sys.getUOM(Unit.WEEK));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// elementary charge to Coulombs
		from = new Quantity(amount, sys.getUOM(Unit.ELEMENTARY_CHARGE));
		to = from.convert(sys.getUOM(Unit.COULOMB));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// eV to joules
		from = new Quantity(amount, sys.getUOM(Unit.ELECTRON_VOLT));
		to = from.convert(sys.getUOM(Unit.JOULE));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// joules to electron-volts
		from = new Quantity(amount, sys.getUOM(Unit.JOULE));
		to = from.convert(sys.getUOM(Unit.ELECTRON_VOLT));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		Quantity convertedQty = new Quantity(convertedAmount, sys.getUOM(Unit.ELECTRON_VOLT));
		Quantity back = convertedQty.convert(sys.getUOM(Unit.JOULE));

		assertThat(back.getAmount(), closeTo(from.getAmount(), DELTA4));

		// julian year to weeks
		from = new Quantity(amount, sys.getUOM(Unit.JULIAN_YEAR));
		to = from.convert(sys.getUOM(Unit.WEEK));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA5));

		// light year to metres
		from = new Quantity(amount, sys.getUOM(Unit.LIGHT_YEAR));
		to = from.convert(sys.getUOM(Unit.METRE));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());

		convertedQty = new Quantity(convertedAmount, sys.getUOM(Unit.METRE));
		back = convertedQty.convert(sys.getUOM(Unit.LIGHT_YEAR));

		assertThat(back.getAmount(), closeTo(from.getAmount(), DELTA6));

		// fathom to metre
		from = new Quantity(amount, sys.getUOM(Unit.FATHOM));
		to = from.convert(sys.getUOM(Unit.METRE));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// square metres to square yards
		from = new Quantity(amount, sys.getUOM(Unit.SQUARE_METRE));
		to = from.convert(sys.getUOM(Unit.SQUARE_YARD));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// cubic yards to cubic metres
		from = new Quantity(amount, sys.getUOM(Unit.CUBIC_YARD));
		to = from.convert(sys.getUOM(Unit.CUBIC_METRE));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// mils to millmetres
		from = new Quantity(amount, sys.getUOM(Unit.MIL));
		to = from.convert(sys.getUOM(Unit.MILLIMETRE));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// cubic meters to US barrels
		from = new Quantity(amount, sys.getUOM(Unit.US_BARREL));
		to = from.convert(sys.getUOM(Unit.CUBIC_METRE));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// cord to cubic metres
		from = new Quantity(amount, sys.getUOM(Unit.CORD));
		to = from.convert(sys.getUOM(Unit.CUBIC_METRE));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// cubic metres to US bushels
		from = new Quantity(amount, sys.getUOM(Unit.CUBIC_METRE));
		to = from.convert(sys.getUOM(Unit.US_BUSHEL));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA4));

		// British bushels to cubic metres
		from = new Quantity(amount, sys.getUOM(Unit.BR_BUSHEL));
		to = from.convert(sys.getUOM(Unit.CUBIC_METRE));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// point to inches
		from = new Quantity(amount, sys.getUOM(Unit.POINT));
		to = from.convert(sys.getUOM(Unit.INCH));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// in Hg to kiloPascal
		from = new Quantity(amount, sys.getUOM(Unit.IN_HG));
		to = from.convert(sys.getUOM(Unit.PSI));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA5));

		// bytes to bits
		from = new Quantity(amount, sys.getUOM(Unit.BYTE));
		to = from.convert(sys.getUOM(Unit.BIT));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

		// rpm to Hz
		from = new Quantity(amount, sys.getUOM(Unit.REV_PER_MIN));
		to = from.convert(sys.getUOM(Unit.HERTZ));

		convertedAmount = wsConvert(amount, from.getUOM(), to.getUOM());
		assertThat(to.getAmount(), closeTo(convertedAmount, DELTA6));

	}

	private BigDecimal wsConvert(String amount, UnitOfMeasure from, UnitOfMeasure to)
			throws HttpRequestException, Exception {
		String fromUnit = prep(from.getUnifiedSymbol());
		String toUnit = prep(to.getUnifiedSymbol());

		String conversionUrl = amount + "/" + FROM + fromUnit + "/" + TO + toUnit;
		String url = BASE_URL + conversionUrl;
		System.out.println(url);

		HttpRequest request = HttpRequest.get(url).accept("application/json");

		if (request.code() != 200) {
			throw new Exception("Web service call returned " + request.code());
		}

		BigDecimal decimal = null;
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

	private BigDecimal getResultQuantity(WebServiceConversion conversion) {
		return conversion.getWebServiceResponse().getResponse().getResultQuantity();
	}
}
