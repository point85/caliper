package org.point85.uom.test;

import static org.hamcrest.number.BigDecimalCloseTo.closeTo;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Test;
import org.point85.uom.Conversion;
import org.point85.uom.MeasurementSystem;
import org.point85.uom.ProductUOM;
import org.point85.uom.Quantity;
import org.point85.uom.ScalarUOM;
import org.point85.uom.Unit;
import org.point85.uom.UnitOfMeasure;
import org.point85.uom.MeasurementService;
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

		MeasurementService uomService = MeasurementService.getInstance();

		MeasurementSystem sys = uomService.getUnifiedSystem();

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
		ProductUOM kwh = sys.createProductUOM(UnitType.ENERGY, "kilowatt-hour", "kWh", "kilowatt-hour",
				sys.getUOM(Unit.KILOWATT), sys.getHour());
		kwh.setUnifiedSymbol("kW.h");

		from = new Quantity(amount, kwh);
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
