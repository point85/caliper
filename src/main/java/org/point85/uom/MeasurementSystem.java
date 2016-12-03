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

package org.point85.uom;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * A MeasurementSystem is a collection of units of measure that have a linear
 * relationship to each other: y = ax + b where x is the unit to be converted, y
 * is the converted unit, a is the scaling factor and b is the offset.
 * 
 * See
 * <ul>
 * <li>Wikipedia: <i><a href=
 * "https://en.wikipedia.org/wiki/International_System_of_Units">International
 * System of Units</a></i></li>
 * <li>Table of conversions:
 * <i><a href="https://en.wikipedia.org/wiki/Conversion_of_units">Conversion of
 * Units</a></i></li>
 * <li>Unified Code for Units of Measure :
 * <i><a href="http://unitsofmeasure.org/trac">UCUM</a></i></li>
 * <li>SI derived units:
 * <i><a href="https://en.wikipedia.org/wiki/SI_derived_unit">SI Derived
 * Units</a></i></li>
 * <li>US system:
 * <i><a href="https://en.wikipedia.org/wiki/United_States_customary_units">US
 * Units</a></i></li>
 * <li>British Imperial system:
 * <i><a href="https://en.wikipedia.org/wiki/Imperial_units">British Imperial
 * Units</a></i></li>
 * </ul>
 * 
 * The MeasurementSystem class:
 * <ul>
 * <li>Provides a definition of standard SI prefixes</li>
 * <li>Creates the 7 SI fundamental units of measure</li>
 * <li>Creates 20 SI units derived from these fundamental units</li>
 * <li>Creates other units in the International Customary, US and British
 * Imperial systems</li>
 * </ul>
 *
 */

public class MeasurementSystem {
	// name of resource bundle with translatable strings for exception messages
	static final String MESSAGES_BUNDLE_NAME = "Message";

	// resource bundle for exception messages
	private static ResourceBundle messages;

	// standard unified system
	private static MeasurementSystem unifiedSystem;

	// name of resource bundle with translatable strings for UOMs (e.g. time)
	static final String UNIT_BUNDLE_NAME = "Unit";

	// unit resource bundle (e.g. time units)
	private transient ResourceBundle symbols;

	// SI prefix factors
	public static final BigDecimal YOTTA = Quantity.createAmount("1.0E+24");
	public static final BigDecimal ZETTA = Quantity.createAmount("1.0E+21");
	public static final BigDecimal EXA = Quantity.createAmount("1.0E+18");
	public static final BigDecimal PETA = Quantity.createAmount("1.0E+15");
	public static final BigDecimal TERA = Quantity.createAmount("1.0E+12");
	public static final BigDecimal GIGA = Quantity.createAmount("1.0E+09");
	public static final BigDecimal MEGA = Quantity.createAmount("1000000");
	public static final BigDecimal KILO = Quantity.createAmount("1000");
	public static final BigDecimal HECTO = Quantity.createAmount("100");
	public static final BigDecimal DECA = Quantity.createAmount("10");
	public static final BigDecimal DECI = Quantity.createAmount("0.1");
	public static final BigDecimal CENTI = Quantity.createAmount("0.01");
	public static final BigDecimal MILLI = Quantity.createAmount("0.001");
	public static final BigDecimal MICRO = Quantity.createAmount("0.000001");
	public static final BigDecimal NANO = Quantity.createAmount("1.0E-09");
	public static final BigDecimal PICO = Quantity.createAmount("1.0E-12");
	public static final BigDecimal FEMTO = Quantity.createAmount("1.0E-15");
	public static final BigDecimal ATTO = Quantity.createAmount("1.0E-18");
	public static final BigDecimal ZEPTO = Quantity.createAmount("1.0E-21");
	public static final BigDecimal YOCTO = Quantity.createAmount("1.0E-24");

	// registry by unit symbol
	private Map<String, UnitOfMeasure> symbolRegistry = Collections.synchronizedMap(new HashMap<>());

	// registry for units by enumeration
	private Map<Unit, UnitOfMeasure> unitRegistry = Collections.synchronizedMap(new HashMap<>());

	private MeasurementSystem() throws Exception {
		initialize();
	}

	void initialize() throws Exception {
		// common unit strings
		symbols = ResourceBundle.getBundle(UNIT_BUNDLE_NAME, Locale.getDefault());
		messages = ResourceBundle.getBundle(MESSAGES_BUNDLE_NAME, Locale.getDefault());
	}

	// get a particular message by its key
	static String getMessage(String key) {
		return messages.getString(key);
	}

	/**
	 * Get the unified system of units of measure from International Customary,
	 * SI, US and British Imperial systems
	 * 
	 * @return {@link MeasurementSystem}
	 * @throws Exception
	 *             Exception
	 */
	public static MeasurementSystem getUnifiedSystem() throws Exception {
		if (unifiedSystem == null) {
			createUnifiedSystem();
		}

		return unifiedSystem;
	}

	private static void createUnifiedSystem() throws Exception {
		unifiedSystem = new MeasurementSystem();
	}

	private UnitOfMeasure createUOM(Unit enumeration) throws Exception {
		UnitOfMeasure uom = null;

		if (!(enumeration instanceof Unit)) {
			return uom;
		}

		// SI
		uom = createSIUnit((Unit) enumeration);

		if (uom != null) {
			return uom;
		}

		// Customary
		uom = createCustomaryUnit((Unit) enumeration);

		if (uom != null) {
			return uom;
		}

		// US
		uom = createUSUnit((Unit) enumeration);

		if (uom != null) {
			return uom;
		}

		// British
		uom = createBRUnit((Unit) enumeration);

		if (uom != null) {
			return uom;
		}

		return uom;
	}

	private UnitOfMeasure createSIUnit(Unit unit) throws Exception {
		// In addition to the two dimensionless derived units radian (rad) and
		// steradian (sr), 20 other derived units have special names as defined
		// below. The seven fundamental SI units are metre, kilogram, kelvin,
		// ampere, candela and mole.

		UnitOfMeasure uom = null;
		Conversion conversion = null;
		BigDecimal factor = null;

		switch (unit) {
		case ONE:
			// unity
			uom = createScalarUOM(UnitType.UNITY, Unit.ONE, symbols.getString("one.name"),
					symbols.getString("one.symbol"), symbols.getString("one.desc"), symbols.getString("one.unified"));
			break;

		case SECOND:
			// second
			uom = createScalarUOM(UnitType.TIME, Unit.SECOND, symbols.getString("sec.name"),
					symbols.getString("sec.symbol"), symbols.getString("sec.desc"), symbols.getString("sec.unified"));
			break;

		case MILLISECOND:
			// millisecond
			conversion = new Conversion(MILLI, getUOM(Unit.SECOND));
			uom = createScalarUOM(UnitType.TIME, Unit.MILLISECOND, symbols.getString("ms.name"),
					symbols.getString("ms.symbol"), symbols.getString("ms.desc"), symbols.getString("ms.unified"));
			uom.setConversion(conversion);
			break;

		case MINUTE:
			// minute
			conversion = new Conversion(Quantity.createAmount("60"), getUOM(Unit.SECOND));
			uom = createScalarUOM(UnitType.TIME, Unit.MINUTE, symbols.getString("min.name"),
					symbols.getString("min.symbol"), symbols.getString("min.desc"), symbols.getString("min.unified"));
			uom.setConversion(conversion);
			break;

		case HOUR:
			// hour
			conversion = new Conversion(Quantity.createAmount("60"), getUOM(Unit.MINUTE));
			uom = createScalarUOM(UnitType.TIME, Unit.HOUR, symbols.getString("hr.name"),
					symbols.getString("hr.symbol"), symbols.getString("hr.desc"), symbols.getString("hr.unified"));
			uom.setConversion(conversion);
			break;

		case DAY:
			// day
			conversion = new Conversion(Quantity.createAmount("24"), getUOM(Unit.HOUR));
			uom = createScalarUOM(UnitType.TIME, Unit.DAY, symbols.getString("day.name"),
					symbols.getString("day.symbol"), symbols.getString("day.desc"), symbols.getString("day.unified"));
			uom.setConversion(conversion);
			break;

		case WEEK:
			// week
			conversion = new Conversion(Quantity.createAmount("7"), getUOM(Unit.DAY));
			uom = createScalarUOM(UnitType.TIME, Unit.WEEK, symbols.getString("week.name"),
					symbols.getString("week.symbol"), symbols.getString("week.desc"),
					symbols.getString("week.unified"));
			uom.setConversion(conversion);
			break;

		case JULIAN_YEAR:
			// Julian year
			conversion = new Conversion(Quantity.createAmount("365.25"), getUOM(Unit.DAY));
			uom = createScalarUOM(UnitType.TIME, Unit.JULIAN_YEAR, symbols.getString("jyear.name"),
					symbols.getString("jyear.symbol"), symbols.getString("jyear.desc"),
					symbols.getString("jyear.unified"));
			uom.setConversion(conversion);

			break;

		case SQUARE_SECOND:
			// square second
			uom = createPowerUOM(UnitType.TIME_SQUARED, Unit.SQUARE_SECOND, symbols.getString("s2.name"),
					symbols.getString("s2.symbol"), symbols.getString("s2.desc"), symbols.getString("s2.unified"),
					getUOM(Unit.SECOND), 2);
			break;

		case DECIBEL:
			// decibel
			uom = createScalarUOM(UnitType.INTENSITY, Unit.DECIBEL, symbols.getString("db.name"),
					symbols.getString("db.symbol"), symbols.getString("db.desc"), symbols.getString("db.unified"));
			break;

		case RADIAN:
			// plane angle radian (rad)
			conversion = new Conversion(getOne());
			uom = createScalarUOM(UnitType.PLANE_ANGLE, Unit.RADIAN, symbols.getString("radian.name"),
					symbols.getString("radian.symbol"), symbols.getString("radian.desc"),
					symbols.getString("radian.unified"));
			uom.setConversion(conversion);
			break;

		case STERADIAN:
			// solid angle steradian (sr)
			conversion = new Conversion(getOne());
			uom = createScalarUOM(UnitType.SOLID_ANGLE, Unit.STERADIAN, symbols.getString("steradian.name"),
					symbols.getString("steradian.symbol"), symbols.getString("steradian.desc"),
					symbols.getString("steradian.unified"));
			uom.setConversion(conversion);
			break;

		case DEGREE:
			// degree of arc
			factor = Quantity.divide(String.valueOf(Math.PI), "180");
			conversion = new Conversion(factor, getUOM(Unit.RADIAN));
			uom = createScalarUOM(UnitType.PLANE_ANGLE, Unit.DEGREE, symbols.getString("degree.name"),
					symbols.getString("degree.symbol"), symbols.getString("degree.desc"),
					symbols.getString("degree.unified"));
			uom.setConversion(conversion);
			break;

		case METRE:
			// fundamental length
			uom = createScalarUOM(UnitType.LENGTH, Unit.METRE, symbols.getString("m.name"),
					symbols.getString("m.symbol"), symbols.getString("m.desc"), symbols.getString("m.unified"));
			break;

		case DIOPTER:
			// per metre
			uom = createQuotientUOM(UnitType.RECIPROCAL_LENGTH, Unit.DIOPTER, symbols.getString("diopter.name"),
					symbols.getString("diopter.symbol"), symbols.getString("diopter.desc"),
					symbols.getString("diopter.unified"), getOne(), getUOM(Unit.METRE));
			break;

		case KILOGRAM:
			// fundamental mass
			uom = createScalarUOM(UnitType.MASS, Unit.KILOGRAM, symbols.getString("kg.name"),
					symbols.getString("kg.symbol"), symbols.getString("kg.desc"), symbols.getString("kg.unified"));
			break;

		case TONNE:
			// mass
			conversion = new Conversion(KILO, getUOM(Unit.KILOGRAM));
			uom = createScalarUOM(UnitType.MASS, Unit.TONNE, symbols.getString("tonne.name"),
					symbols.getString("tonne.symbol"), symbols.getString("tonne.desc"),
					symbols.getString("tonne.unified"));
			uom.setConversion(conversion);
			break;

		case KELVIN:
			// fundamental temperature
			uom = createScalarUOM(UnitType.TEMPERATURE, Unit.KELVIN, symbols.getString("kelvin.name"),
					symbols.getString("kelvin.symbol"), symbols.getString("kelvin.desc"),
					symbols.getString("kelvin.unified"));
			break;

		case AMPERE:
			// electric current
			uom = createScalarUOM(UnitType.ELECTRIC_CURRENT, Unit.AMPERE, symbols.getString("amp.name"),
					symbols.getString("amp.symbol"), symbols.getString("amp.desc"), symbols.getString("amp.unified"));
			break;

		case CANDELA:
			// luminosity
			uom = createScalarUOM(UnitType.LUMINOSITY, Unit.CANDELA, symbols.getString("cd.name"),
					symbols.getString("cd.symbol"), symbols.getString("cd.desc"), symbols.getString("cd.unified"));
			break;

		case MOLE:
			// substance amount
			conversion = new Conversion(Quantity.createAmount("6.0221367E+23"), getOne());
			uom = createScalarUOM(UnitType.SUBSTANCE_AMOUNT, Unit.MOLE, symbols.getString("mole.name"),
					symbols.getString("mole.symbol"), symbols.getString("mole.desc"),
					symbols.getString("mole.unified"));
			uom.setConversion(conversion);
			break;

		case LIGHT_YEAR:
			uom = createProductUOM(UnitType.LENGTH, Unit.LIGHT_YEAR, symbols.getString("ly.name"),
					symbols.getString("ly.symbol"), symbols.getString("ly.desc"), symbols.getString("ly.unified"),
					getUOM(Unit.LIGHT_VELOCITY), getUOM(Unit.JULIAN_YEAR));
			break;

		case KILOMETRE:
			// kilometre
			conversion = new Conversion(KILO, getUOM(Unit.METRE));
			uom = createScalarUOM(UnitType.LENGTH, Unit.KILOMETRE, symbols.getString("km.name"),
					symbols.getString("km.symbol"), symbols.getString("km.desc"), symbols.getString("km.unified"));
			uom.setConversion(conversion);
			break;

		case CENTIMETRE:
			// centimetre
			conversion = new Conversion(CENTI, getUOM(Unit.METRE));
			uom = createScalarUOM(UnitType.LENGTH, Unit.CENTIMETRE, symbols.getString("cm.name"),
					symbols.getString("cm.symbol"), symbols.getString("cm.desc"), symbols.getString("cm.unified"));
			uom.setConversion(conversion);
			break;

		case MILLIMETRE:
			// millimetre
			conversion = new Conversion(MILLI, getUOM(Unit.METRE));
			uom = createScalarUOM(UnitType.LENGTH, Unit.MILLIMETRE, symbols.getString("mm.name"),
					symbols.getString("mm.symbol"), symbols.getString("mm.desc"), symbols.getString("mm.unified"));
			uom.setConversion(conversion);
			break;

		case MICROMETRE:
			// micrometre (micron = 10-6 metre)
			conversion = new Conversion(MICRO, getUOM(Unit.METRE));
			uom = createScalarUOM(UnitType.LENGTH, Unit.MICROMETRE, symbols.getString("mu.name"),
					symbols.getString("mu.symbol"), symbols.getString("mu.desc"), symbols.getString("mu.unified"));
			uom.setConversion(conversion);
			break;

		case NANOMETRE:
			// nanometre (10-9 metre)
			conversion = new Conversion(NANO, getUOM(Unit.METRE));
			uom = createScalarUOM(UnitType.LENGTH, Unit.NANOMETRE, symbols.getString("nm.name"),
					symbols.getString("nm.symbol"), symbols.getString("nm.desc"), symbols.getString("nm.unified"));
			uom.setConversion(conversion);
			break;

		case GRAM:
			// gram
			conversion = new Conversion(MILLI, getUOM(Unit.KILOGRAM));
			uom = createScalarUOM(UnitType.MASS, Unit.GRAM, symbols.getString("gram.name"),
					symbols.getString("gram.symbol"), symbols.getString("gram.desc"),
					symbols.getString("gram.unified"));
			uom.setConversion(conversion);
			break;

		case MILLIGRAM:
			// milligram
			conversion = new Conversion(MILLI, getUOM(Unit.GRAM));
			uom = createScalarUOM(UnitType.MASS, Unit.MILLIGRAM, symbols.getString("milligram.name"),
					symbols.getString("milligram.symbol"), symbols.getString("milligram.desc"),
					symbols.getString("milligram.unified"));
			uom.setConversion(conversion);
			break;

		case MICROGRAM:
			// microgram
			conversion = new Conversion(MICRO, getUOM(Unit.GRAM));
			uom = createScalarUOM(UnitType.MASS, Unit.MICROGRAM, symbols.getString("microgram.name"),
					symbols.getString("microgram.symbol"), symbols.getString("microgram.desc"),
					symbols.getString("microgram.unified"));
			uom.setConversion(conversion);
			break;

		case CARAT:
			// carat
			conversion = new Conversion(Quantity.createAmount("0.2"), getUOM(Unit.GRAM));
			uom = createScalarUOM(UnitType.MASS, Unit.CARAT, symbols.getString("carat.name"),
					symbols.getString("carat.symbol"), symbols.getString("carat.desc"),
					symbols.getString("carat.unified"));
			uom.setConversion(conversion);
			break;

		case SQUARE_METRE:
			// square metre
			uom = createPowerUOM(UnitType.AREA, Unit.SQUARE_METRE, symbols.getString("m2.name"),
					symbols.getString("m2.symbol"), symbols.getString("m2.desc"), symbols.getString("m2.unified"),
					getUOM(Unit.METRE), 2);
			break;

		case METRE_PER_SECOND:
			// velocity
			uom = createQuotientUOM(UnitType.VELOCITY, Unit.METRE_PER_SECOND, symbols.getString("mps.name"),
					symbols.getString("mps.symbol"), symbols.getString("mps.desc"), symbols.getString("mps.unified"),
					getUOM(Unit.METRE), getSecond());
			break;

		case METRE_PER_SECOND_SQUARED:
			// acceleration
			uom = createQuotientUOM(UnitType.ACCELERATION, Unit.METRE_PER_SECOND_SQUARED,
					symbols.getString("mps2.name"), symbols.getString("mps2.symbol"), symbols.getString("mps2.desc"),
					symbols.getString("mps2.unified"), getUOM(Unit.METRE), getUOM(Unit.SQUARE_SECOND));
			break;

		case CUBIC_METRE:
			// cubic metre
			uom = createPowerUOM(UnitType.VOLUME, Unit.CUBIC_METRE, symbols.getString("m3.name"),
					symbols.getString("m3.symbol"), symbols.getString("m3.desc"), symbols.getString("m3.unified"),
					getUOM(Unit.METRE), 3);
			break;

		case LITRE:
			// litre
			conversion = new Conversion(MILLI, getUOM(Unit.CUBIC_METRE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.LITRE, symbols.getString("litre.name"),
					symbols.getString("litre.symbol"), symbols.getString("litre.desc"),
					symbols.getString("litre.unified"));
			uom.setConversion(conversion);
			break;

		case MILLILITRE:
			// millilitre
			conversion = new Conversion(MILLI, getUOM(Unit.LITRE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.MILLILITRE, symbols.getString("ml.name"),
					symbols.getString("ml.symbol"), symbols.getString("ml.desc"), symbols.getString("ml.unified"));
			uom.setConversion(conversion);
			break;

		case CENTILITRE:
			// centilitre
			conversion = new Conversion(CENTI, getUOM(Unit.LITRE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.CENTILITRE, symbols.getString("cl.name"),
					symbols.getString("cl.symbol"), symbols.getString("cl.desc"), symbols.getString("cl.unified"));
			uom.setConversion(conversion);
			break;

		case DECILITRE:
			// decilitre
			conversion = new Conversion(DECI, getUOM(Unit.LITRE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.DECILITRE, symbols.getString("dl.name"),
					symbols.getString("dl.symbol"), symbols.getString("dl.desc"), symbols.getString("dl.unified"));
			uom.setConversion(conversion);
			break;

		case CUBIC_METRE_PER_SECOND:
			// flow (volume)
			uom = createQuotientUOM(UnitType.VOLUMETRIC_FLOW, Unit.CUBIC_METRE_PER_SECOND,
					symbols.getString("m3PerSec.name"), symbols.getString("m3PerSec.symbol"),
					symbols.getString("m3PerSec.desc"), symbols.getString("m3PerSec.unified"), getUOM(Unit.CUBIC_METRE),
					getSecond());
			break;

		case KILOGRAM_PER_SECOND:
			// flow (mass)
			uom = createQuotientUOM(UnitType.MASS_FLOW, Unit.KILOGRAM_PER_SECOND, symbols.getString("kgPerSec.name"),
					symbols.getString("kgPerSec.symbol"), symbols.getString("kgPerSec.desc"),
					symbols.getString("kgPerSec.unified"), getUOM(Unit.KILOGRAM), getSecond());
			break;

		case KILOGRAM_PER_CUBIC_METRE:
			// kg/m^3
			uom = createQuotientUOM(UnitType.DENSITY, Unit.KILOGRAM_PER_CUBIC_METRE, symbols.getString("kg_m3.name"),
					symbols.getString("kg_m3.symbol"), symbols.getString("kg_m3.desc"),
					symbols.getString("kg_m3.unified"), getUOM(Unit.KILOGRAM), getUOM(Unit.CUBIC_METRE));
			break;

		case PASCAL_SECOND:
			// dynamic viscosity
			uom = createProductUOM(UnitType.DYNAMIC_VISCOSITY, Unit.PASCAL_SECOND, symbols.getString("pascal_sec.name"),
					symbols.getString("pascal_sec.symbol"), symbols.getString("pascal_sec.desc"),
					symbols.getString("pascal_sec.unified"), getUOM(Unit.PASCAL), getSecond());
			break;

		case SQUARE_METRE_PER_SECOND:
			// kinematic viscosity
			uom = createQuotientUOM(UnitType.KINEMATIC_VISCOSITY, Unit.SQUARE_METRE_PER_SECOND,
					symbols.getString("m2PerSec.name"), symbols.getString("m2PerSec.symbol"),
					symbols.getString("m2PerSec.desc"), symbols.getString("m2PerSec.unified"),
					getUOM(Unit.SQUARE_METRE), getSecond());
			break;

		case CALORIE:
			// thermodynamic calorie
			conversion = new Conversion(Quantity.createAmount("4.184"), getUOM(Unit.JOULE));
			uom = createScalarUOM(UnitType.ENERGY, Unit.CALORIE, symbols.getString("calorie.name"),
					symbols.getString("calorie.symbol"), symbols.getString("calorie.desc"),
					symbols.getString("calorie.unified"));
			uom.setConversion(conversion);
			break;

		case NEWTON:
			// force F = m·A (newton)
			uom = createProductUOM(UnitType.FORCE, Unit.NEWTON, symbols.getString("newton.name"),
					symbols.getString("newton.symbol"), symbols.getString("newton.desc"),
					symbols.getString("newton.unified"), getUOM(Unit.KILOGRAM), getUOM(Unit.METRE_PER_SECOND_SQUARED));
			break;
		case NEWTON_METRE:
			// newton-metre
			uom = createProductUOM(UnitType.ENERGY, Unit.NEWTON_METRE, symbols.getString("n_m.name"),
					symbols.getString("n_m.symbol"), symbols.getString("n_m.desc"), symbols.getString("n_m.unified"),
					getUOM(Unit.NEWTON), getUOM(Unit.METRE));
			break;

		case JOULE:
			// energy (joule)
			uom = createProductUOM(UnitType.ENERGY, Unit.JOULE, symbols.getString("joule.name"),
					symbols.getString("joule.symbol"), symbols.getString("joule.desc"),
					symbols.getString("joule.unified"), getUOM(Unit.NEWTON), getUOM(Unit.METRE));
			break;

		case KILOJOULE:
			// 1000 J
			conversion = new Conversion(KILO, getUOM(Unit.JOULE));
			uom = createScalarUOM(UnitType.ENERGY, Unit.KILOJOULE, symbols.getString("kj.name"),
					symbols.getString("kj.symbol"), symbols.getString("kj.desc"), symbols.getString("kj.unified"));
			uom.setConversion(conversion);
			break;

		case ELECTRON_VOLT:
			// ev
			uom = createProductUOM(UnitType.ENERGY, Unit.ELECTRON_VOLT, symbols.getString("ev.name"),
					symbols.getString("ev.symbol"), symbols.getString("ev.desc"), symbols.getString("ev.unified"),
					getUOM(Unit.ELEMENTARY_CHARGE), getUOM(Unit.VOLT));
			break;

		case KILOWATT_HOUR:
			// kw-hour
			uom = createProductUOM(UnitType.ENERGY, Unit.KILOWATT_HOUR, symbols.getString("kwh.name"),
					symbols.getString("kwh.symbol"), symbols.getString("kwh.desc"), symbols.getString("kwh.unified"),
					getUOM(Unit.KILOWATT), getHour());
			break;

		case WATT:
			// power (watt)
			uom = createQuotientUOM(UnitType.POWER, Unit.WATT, symbols.getString("watt.name"),
					symbols.getString("watt.symbol"), symbols.getString("watt.desc"), symbols.getString("watt.unified"),
					getUOM(Unit.JOULE), getSecond());
			break;

		case KILOWATT:
			// power
			conversion = new Conversion(KILO, getUOM(Unit.WATT));
			uom = createScalarUOM(UnitType.POWER, Unit.KILOWATT, symbols.getString("kw.name"),
					symbols.getString("kw.symbol"), symbols.getString("kw.desc"), symbols.getString("kw.unified"));
			uom.setConversion(conversion);
			break;

		case HERTZ:
			// frequency (hertz)
			uom = createQuotientUOM(UnitType.FREQUENCY, Unit.HERTZ, symbols.getString("hertz.name"),
					symbols.getString("hertz.symbol"), symbols.getString("hertz.desc"),
					symbols.getString("hertz.unified"), getOne(), getSecond());
			break;

		case RAD_PER_SEC:
			// angular frequency
			BigDecimal twoPi = new BigDecimal("2").multiply(new BigDecimal(Math.PI), UnitOfMeasure.MATH_CONTEXT);
			conversion = new Conversion(BigDecimal.ONE.divide(twoPi, UnitOfMeasure.MATH_CONTEXT), getUOM(Unit.HERTZ));
			uom = createQuotientUOM(UnitType.FREQUENCY, Unit.RAD_PER_SEC, symbols.getString("radpers.name"),
					symbols.getString("radpers.symbol"), symbols.getString("radpers.desc"),
					symbols.getString("radpers.unified"), getUOM(Unit.RADIAN), getSecond());
			uom.setConversion(conversion);
			break;

		case PASCAL:
			// pressure (pascal)
			uom = createQuotientUOM(UnitType.PRESSURE, Unit.PASCAL, symbols.getString("pascal.name"),
					symbols.getString("pascal.symbol"), symbols.getString("pascal.desc"),
					symbols.getString("pascal.unified"), getUOM(Unit.NEWTON), getUOM(Unit.SQUARE_METRE));
			break;

		case KILOPASCAL:
			// 1000 pascal
			conversion = new Conversion(KILO, getUOM(Unit.PASCAL));
			uom = createScalarUOM(UnitType.PRESSURE, Unit.KILOPASCAL, symbols.getString("kpa.name"),
					symbols.getString("kpa.symbol"), symbols.getString("kpa.desc"), symbols.getString("kpa.unified"));
			uom.setConversion(conversion);
			break;

		case BAR:
			// pressure (bar)
			conversion = new Conversion(BigDecimal.ONE, getUOM(Unit.PASCAL), Quantity.createAmount("1.0E+05"));
			uom = createScalarUOM(UnitType.PRESSURE, Unit.BAR, symbols.getString("bar.name"),
					symbols.getString("bar.symbol"), symbols.getString("bar.desc"), symbols.getString("bar.unified"));
			uom.setConversion(conversion);
			break;

		case COULOMB:
			// charge (coulomb)
			uom = createProductUOM(UnitType.ELECTRIC_CHARGE, Unit.COULOMB, symbols.getString("coulomb.name"),
					symbols.getString("coulomb.symbol"), symbols.getString("coulomb.desc"),
					symbols.getString("coulomb.unified"), getUOM(Unit.AMPERE), getSecond());
			break;

		case ELEMENTARY_CHARGE:
			// e
			conversion = new Conversion(Quantity.createAmount("1.602176620898E-19"), getUOM(Unit.COULOMB));
			uom = createScalarUOM(UnitType.ELECTRIC_CHARGE, Unit.ELEMENTARY_CHARGE, symbols.getString("e.name"),
					symbols.getString("e.symbol"), symbols.getString("e.desc"), symbols.getString("e.unified"));
			uom.setConversion(conversion);
			break;

		case VOLT:
			// voltage (volt)
			uom = createQuotientUOM(UnitType.ELECTROMOTIVE_FORCE, Unit.VOLT, symbols.getString("volt.name"),
					symbols.getString("volt.symbol"), symbols.getString("volt.desc"), symbols.getString("volt.unified"),
					getUOM(Unit.WATT), getUOM(Unit.AMPERE));
			break;

		case OHM:
			// resistance (ohm)
			uom = createQuotientUOM(UnitType.ELECTRICAL_RESISTANCE, Unit.OHM, symbols.getString("ohm.name"),
					symbols.getString("ohm.symbol"), symbols.getString("ohm.desc"), symbols.getString("ohm.unified"),
					getUOM(Unit.VOLT), getUOM(Unit.AMPERE));
			break;

		case FARAD:
			// capacitance (farad)
			uom = createQuotientUOM(UnitType.CAPACITANCE, Unit.FARAD, symbols.getString("farad.name"),
					symbols.getString("farad.symbol"), symbols.getString("farad.desc"),
					symbols.getString("farad.unified"), getUOM(Unit.COULOMB), getUOM(Unit.VOLT));
			break;

		case WEBER:
			// magnetic flux (weber)
			uom = createProductUOM(UnitType.MAGNETIC_FLUX, Unit.WEBER, symbols.getString("weber.name"),
					symbols.getString("weber.symbol"), symbols.getString("weber.desc"),
					symbols.getString("weber.unified"), getUOM(Unit.VOLT), getSecond());
			break;

		case TESLA:
			// magnetic flux density (tesla)
			uom = createQuotientUOM(UnitType.MAGNETIC_FLUX_DENSITY, Unit.TESLA, symbols.getString("tesla.name"),
					symbols.getString("tesla.symbol"), symbols.getString("tesla.desc"),
					symbols.getString("tesla.unified"), getUOM(Unit.WEBER), getUOM(Unit.SQUARE_METRE));
			break;

		case HENRY:
			// inductance (henry)
			uom = createQuotientUOM(UnitType.INDUCTANCE, Unit.HENRY, symbols.getString("henry.name"),
					symbols.getString("henry.symbol"), symbols.getString("henry.desc"),
					symbols.getString("henry.unified"), getUOM(Unit.WEBER), getUOM(Unit.AMPERE));
			break;

		case SIEMENS:
			// electrical conductance (siemens)
			uom = createQuotientUOM(UnitType.ELECTRICAL_CONDUCTANCE, Unit.SIEMENS, symbols.getString("siemens.name"),
					symbols.getString("siemens.symbol"), symbols.getString("siemens.desc"),
					symbols.getString("siemens.unified"), getUOM(Unit.AMPERE), getUOM(Unit.VOLT));
			break;

		case CELSIUS:
			// °C = °K - 273.15
			conversion = new Conversion(BigDecimal.ONE, getUOM(Unit.KELVIN), Quantity.createAmount("273.15"));
			uom = createScalarUOM(UnitType.TEMPERATURE, Unit.CELSIUS, symbols.getString("celsius.name"),
					symbols.getString("celsius.symbol"), symbols.getString("celsius.desc"),
					symbols.getString("celsius.unified"));
			uom.setConversion(conversion);
			break;

		case LUMEN:
			// luminous flux (lumen)
			uom = createProductUOM(UnitType.LUMINOUS_FLUX, Unit.LUMEN, symbols.getString("lumen.name"),
					symbols.getString("lumen.symbol"), symbols.getString("lumen.desc"),
					symbols.getString("lumen.unified"), getUOM(Unit.CANDELA), getUOM(Unit.STERADIAN));
			break;

		case LUX:
			// illuminance (lux)
			uom = createQuotientUOM(UnitType.ILLUMINANCE, Unit.LUX, symbols.getString("lux.name"),
					symbols.getString("lux.symbol"), symbols.getString("lux.desc"), symbols.getString("lux.unified"),
					getUOM(Unit.LUMEN), getUOM(Unit.SQUARE_METRE));
			break;

		case BECQUEREL:
			// radioactivity (becquerel). Same base symbol as Hertz
			conversion = new Conversion(BigDecimal.ONE, getUOM(Unit.HERTZ));
			uom = createScalarUOM(UnitType.FREQUENCY, Unit.BECQUEREL, symbols.getString("becquerel.name"),
					symbols.getString("becquerel.symbol"), symbols.getString("becquerel.desc"),
					symbols.getString("becquerel.unified"));
			uom.setConversion(conversion);
			break;

		case GRAY:
			// gray (Gy)
			uom = createQuotientUOM(UnitType.RADIATION_DOSE, Unit.GRAY, symbols.getString("gray.name"),
					symbols.getString("gray.symbol"), symbols.getString("gray.desc"), symbols.getString("gray.unified"),
					getUOM(Unit.JOULE), getUOM(Unit.KILOGRAM));
			break;
		case SIEVERT:
			// sievert (Sv)
			uom = createQuotientUOM(UnitType.RADIATION_DOSE, Unit.SIEVERT, symbols.getString("sievert.name"),
					symbols.getString("sievert.symbol"), symbols.getString("sievert.desc"),
					symbols.getString("sievert.unified"), getUOM(Unit.JOULE), getUOM(Unit.KILOGRAM));
			break;

		case KATAL:
			// katal (kat)
			uom = createQuotientUOM(UnitType.CATALYTIC_ACTIVITY, Unit.KATAL, symbols.getString("katal.name"),
					symbols.getString("katal.symbol"), symbols.getString("katal.desc"),
					symbols.getString("katal.unified"), getUOM(Unit.MOLE), getSecond());
			break;

		case GRAVITY:
			// acceleration of gravity
			conversion = new Conversion(Quantity.createAmount("9.80665"), getUOM(Unit.METRE_PER_SECOND_SQUARED));
			uom = createScalarUOM(UnitType.ACCELERATION, Unit.GRAVITY, symbols.getString("gravity.name"),
					symbols.getString("gravity.symbol"), symbols.getString("gravity.desc"),
					symbols.getString("gravity.unified"));
			uom.setConversion(conversion);
			break;

		case LIGHT_VELOCITY:
			// speed of light
			conversion = new Conversion(Quantity.createAmount("299792458"), getUOM(Unit.METRE_PER_SECOND));
			uom = createScalarUOM(UnitType.VELOCITY, Unit.LIGHT_VELOCITY, symbols.getString("light.name"),
					symbols.getString("light.symbol"), symbols.getString("light.desc"),
					symbols.getString("light.unified"));
			uom.setConversion(conversion);
			break;

		case ANGSTROM:
			// length
			conversion = new Conversion(Quantity.createAmount("0.1"), getUOM(Unit.NANOMETRE));
			uom = createScalarUOM(UnitType.LENGTH, Unit.ANGSTROM, symbols.getString("angstrom.name"),
					symbols.getString("angstrom.symbol"), symbols.getString("angstrom.desc"),
					symbols.getString("angstrom.unified"));
			uom.setConversion(conversion);
			break;

		case BIT:
			// computer bit
			uom = createScalarUOM(UnitType.IT, Unit.BIT, symbols.getString("bit.name"), symbols.getString("bit.symbol"),
					symbols.getString("bit.desc"), symbols.getString("bit.unified"));
			break;

		case BYTE:
			// computer byte
			conversion = new Conversion(Quantity.createAmount("8"), getUOM(Unit.BIT));
			uom = createScalarUOM(UnitType.IT, Unit.BYTE, symbols.getString("byte.name"),
					symbols.getString("byte.symbol"), symbols.getString("byte.desc"),
					symbols.getString("byte.unified"));
			uom.setConversion(conversion);

		default:
			break;
		}

		return uom;
	}

	private UnitOfMeasure createCustomaryUnit(Unit unit) throws Exception {
		UnitOfMeasure uom = null;
		Conversion conversion = null;
		BigDecimal factor = null;
		BigDecimal amount = null;

		switch (unit) {

		case RANKINE:
			// Rankine (base)
			uom = createScalarUOM(UnitType.TEMPERATURE, Unit.RANKINE, symbols.getString("rankine.name"),
					symbols.getString("rankine.symbol"), symbols.getString("rankine.desc"),
					symbols.getString("rankine.unified"));

			// create bridge to SI
			factor = Quantity.divide("5", "9");
			conversion = new Conversion(factor, getUOM(Unit.KELVIN));
			uom.setBridge(conversion);

			break;

		case FAHRENHEIT:
			// Fahrenheit
			conversion = new Conversion(BigDecimal.ONE, getUOM(Unit.RANKINE), Quantity.createAmount("459.67"));
			uom = createScalarUOM(UnitType.TEMPERATURE, Unit.FAHRENHEIT, symbols.getString("fahrenheit.name"),
					symbols.getString("fahrenheit.symbol"), symbols.getString("fahrenheit.desc"),
					symbols.getString("fahrenheit.unified"));
			uom.setConversion(conversion);
			break;

		case POUND_MASS:
			// lb mass (base)
			uom = createScalarUOM(UnitType.MASS, Unit.POUND_MASS, symbols.getString("lbm.name"),
					symbols.getString("lbm.symbol"), symbols.getString("lbm.desc"), symbols.getString("lbm.unified"));

			// create bridge to SI
			conversion = new Conversion(Quantity.createAmount("0.45359237"), getUOM(Unit.KILOGRAM));
			uom.setBridge(conversion);
			break;

		case OUNCE:
			// ounce
			conversion = new Conversion(Quantity.createAmount("0.0625"), getUOM(Unit.POUND_MASS));
			uom = createScalarUOM(UnitType.MASS, Unit.OUNCE, symbols.getString("ounce.name"),
					symbols.getString("ounce.symbol"), symbols.getString("ounce.desc"),
					symbols.getString("ounce.unified"));
			uom.setConversion(conversion);
			break;

		case SLUG:
			// slug
			factor = getUOM(Unit.GRAVITY).getConversionFactor(getUOM(Unit.FEET_PER_SECOND_SQUARED));
			conversion = new Conversion(factor, getUOM(Unit.POUND_MASS));
			uom = createScalarUOM(UnitType.MASS, Unit.SLUG, symbols.getString("slug.name"),
					symbols.getString("slug.symbol"), symbols.getString("slug.desc"),
					symbols.getString("slug.unified"));
			uom.setConversion(conversion);
			break;

		case FOOT:
			// foot (foot is base conversion unit)
			uom = createScalarUOM(UnitType.LENGTH, Unit.FOOT, symbols.getString("foot.name"),
					symbols.getString("foot.symbol"), symbols.getString("foot.desc"),
					symbols.getString("foot.unified"));

			// bridge to SI
			conversion = new Conversion(Quantity.createAmount("0.3048"), getUOM(Unit.METRE));
			uom.setBridge(conversion);
			break;

		case INCH:
			// inch
			factor = Quantity.divide("1", "12");
			conversion = new Conversion(factor, getUOM(Unit.FOOT));
			uom = createScalarUOM(UnitType.LENGTH, Unit.INCH, symbols.getString("inch.name"),
					symbols.getString("inch.symbol"), symbols.getString("inch.desc"),
					symbols.getString("inch.unified"));
			uom.setConversion(conversion);
			break;

		case MIL:
			// inch
			conversion = new Conversion(MILLI, getUOM(Unit.INCH));
			uom = createScalarUOM(UnitType.LENGTH, Unit.MIL, symbols.getString("mil.name"),
					symbols.getString("mil.symbol"), symbols.getString("mil.desc"), symbols.getString("mil.unified"));
			uom.setConversion(conversion);
			break;

		case POINT:
			// point
			conversion = new Conversion(Quantity.divide("1", "72"), getUOM(Unit.INCH));
			uom = createScalarUOM(UnitType.LENGTH, Unit.POINT, symbols.getString("point.name"),
					symbols.getString("point.symbol"), symbols.getString("point.desc"),
					symbols.getString("point.unified"));
			uom.setConversion(conversion);
			break;

		case YARD:
			// yard
			conversion = new Conversion(Quantity.createAmount("3"), getUOM(Unit.FOOT));
			uom = createScalarUOM(UnitType.LENGTH, Unit.YARD, symbols.getString("yard.name"),
					symbols.getString("yard.symbol"), symbols.getString("yard.desc"),
					symbols.getString("yard.unified"));
			uom.setConversion(conversion);
			break;

		case MILE:
			// mile
			conversion = new Conversion(Quantity.createAmount("5280"), getUOM(Unit.FOOT));
			uom = createScalarUOM(UnitType.LENGTH, Unit.MILE, symbols.getString("mile.name"),
					symbols.getString("mile.symbol"), symbols.getString("mile.desc"),
					symbols.getString("mile.unified"));
			uom.setConversion(conversion);
			break;

		case NAUTICAL_MILE:
			// nautical mile
			conversion = new Conversion(Quantity.createAmount("6080"), getUOM(Unit.FOOT));
			uom = createScalarUOM(UnitType.LENGTH, Unit.NAUTICAL_MILE, symbols.getString("NM.name"),
					symbols.getString("NM.symbol"), symbols.getString("NM.desc"), symbols.getString("NM.unified"));
			uom.setConversion(conversion);
			break;

		case FATHOM:
			// fathom
			conversion = new Conversion(Quantity.createAmount("6"), getUOM(Unit.FOOT));
			uom = createScalarUOM(UnitType.LENGTH, Unit.FATHOM, symbols.getString("fth.name"),
					symbols.getString("fth.symbol"), symbols.getString("fth.desc"), symbols.getString("fth.unified"));
			uom.setConversion(conversion);

			break;

		case PSI:
			// psi
			uom = createQuotientUOM(UnitType.PRESSURE, Unit.PSI, symbols.getString("psi.name"),
					symbols.getString("psi.symbol"), symbols.getString("psi.desc"), symbols.getString("psi.unified"),
					getUOM(Unit.POUND_FORCE), getUOM(Unit.SQUARE_INCH));
			break;

		case IN_HG:
			// inches of Mercury
			conversion = new Conversion(Quantity.createAmount("0.4911531047"), getUOM(Unit.PSI));
			uom = createScalarUOM(UnitType.PRESSURE, Unit.IN_HG, symbols.getString("inhg.name"),
					symbols.getString("inhg.symbol"), symbols.getString("inhg.desc"),
					symbols.getString("inhg.unified"));
			uom.setConversion(conversion);
			break;

		case SQUARE_INCH:
			// square inch
			factor = Quantity.divide("1", "144");
			conversion = new Conversion(factor, getUOM(Unit.SQUARE_FOOT));
			uom = createPowerUOM(UnitType.AREA, Unit.SQUARE_INCH, symbols.getString("in2.name"),
					symbols.getString("in2.symbol"), symbols.getString("in2.desc"), symbols.getString("in2.unified"),
					getUOM(Unit.INCH), 2);
			uom.setConversion(conversion);
			break;

		case SQUARE_FOOT:
			// square foot
			uom = createPowerUOM(UnitType.AREA, Unit.SQUARE_FOOT, symbols.getString("ft2.name"),
					symbols.getString("ft2.symbol"), symbols.getString("ft2.desc"), symbols.getString("ft2.unified"),
					getUOM(Unit.FOOT), 2);
			break;

		case SQUARE_YARD:
			// square yard
			uom = createPowerUOM(UnitType.AREA, Unit.SQUARE_YARD, symbols.getString("yd2.name"),
					symbols.getString("yd2.symbol"), symbols.getString("yd2.desc"), symbols.getString("yd2.unified"),
					getUOM(Unit.YARD), 2);
			break;

		case ACRE:
			// acre
			conversion = new Conversion(Quantity.createAmount("43560"), getUOM(Unit.SQUARE_FOOT));
			uom = createScalarUOM(UnitType.AREA, Unit.ACRE, symbols.getString("acre.name"),
					symbols.getString("acre.symbol"), symbols.getString("acre.desc"),
					symbols.getString("acre.unified"));
			uom.setConversion(conversion);
			break;

		case CUBIC_INCH:
			// cubic inch
			factor = Quantity.divide("1", "1728");
			conversion = new Conversion(factor, getUOM(Unit.CUBIC_FOOT));
			uom = createPowerUOM(UnitType.VOLUME, Unit.CUBIC_INCH, symbols.getString("in3.name"),
					symbols.getString("in3.symbol"), symbols.getString("in3.desc"), symbols.getString("in3.unified"),
					getUOM(Unit.INCH), 3);
			uom.setConversion(conversion);
			break;

		case CUBIC_FOOT:
			// cubic feet
			uom = createPowerUOM(UnitType.VOLUME, Unit.CUBIC_FOOT, symbols.getString("ft3.name"),
					symbols.getString("ft3.symbol"), symbols.getString("ft3.desc"), symbols.getString("ft3.unified"),
					getUOM(Unit.FOOT), 3);
			break;

		case CORD:
			// cord
			conversion = new Conversion(Quantity.createAmount("128"), getUOM(Unit.CUBIC_FOOT));
			uom = createScalarUOM(UnitType.VOLUME, Unit.CORD, symbols.getString("cord.name"),
					symbols.getString("cord.symbol"), symbols.getString("cord.desc"),
					symbols.getString("cord.unified"));
			uom.setConversion(conversion);
			break;

		case CUBIC_YARD:
			// cubic yard
			uom = createPowerUOM(UnitType.VOLUME, Unit.CUBIC_YARD, symbols.getString("yd3.name"),
					symbols.getString("yd3.symbol"), symbols.getString("yd3.desc"), symbols.getString("yd3.unified"),
					getUOM(Unit.YARD), 3);
			break;

		case FEET_PER_SECOND:
			// feet/sec
			uom = createQuotientUOM(UnitType.VELOCITY, Unit.FEET_PER_SECOND, symbols.getString("fps.name"),
					symbols.getString("fps.symbol"), symbols.getString("fps.desc"), symbols.getString("fps.unified"),
					getUOM(Unit.FOOT), getSecond());
			break;

		case KNOT:
			// knot
			factor = Quantity.divide("6080", "3600");
			conversion = new Conversion(factor, getUOM(Unit.FEET_PER_SECOND));
			uom = createScalarUOM(UnitType.VELOCITY, Unit.KNOT, symbols.getString("knot.name"),
					symbols.getString("knot.symbol"), symbols.getString("knot.desc"),
					symbols.getString("knot.unified"));
			uom.setConversion(conversion);
			break;

		case FEET_PER_SECOND_SQUARED:
			// acceleration
			uom = createQuotientUOM(UnitType.ACCELERATION, Unit.FEET_PER_SECOND_SQUARED,
					symbols.getString("ftps2.name"), symbols.getString("ftps2.symbol"), symbols.getString("ftps2.desc"),
					symbols.getString("ftps2.unified"), getUOM(Unit.FOOT), getUOM(Unit.SQUARE_SECOND));
			break;

		case HP:
			// HP (mechanical)
			uom = createProductUOM(UnitType.POWER, Unit.HP, symbols.getString("hp.name"),
					symbols.getString("hp.symbol"), symbols.getString("hp.desc"), symbols.getString("hp.unified"),
					getUOM(Unit.POUND_FORCE), getUOM(Unit.FEET_PER_SECOND));
			uom.setScalingFactor(Quantity.createAmount("550"));
			break;

		case BTU:
			// BTU = 1055.056 Joules (778.169 ft-lbf)
			conversion = new Conversion(Quantity.createAmount("778.1692622659652"), getUOM(Unit.FOOT_POUND_FORCE));
			uom = createScalarUOM(UnitType.ENERGY, Unit.BTU, symbols.getString("btu.name"),
					symbols.getString("btu.symbol"), symbols.getString("btu.desc"), symbols.getString("btu.unified"));
			uom.setConversion(conversion);
			break;

		case FOOT_POUND_FORCE:
			// ft-lbf
			uom = createProductUOM(UnitType.ENERGY, Unit.FOOT_POUND_FORCE, symbols.getString("ft_lbf.name"),
					symbols.getString("ft_lbf.symbol"), symbols.getString("ft_lbf.desc"),
					symbols.getString("ft_lbf.unified"), getUOM(Unit.FOOT), getUOM(Unit.POUND_FORCE));
			break;

		case POUND_FORCE:
			// force F = m·A (lbf)
			uom = createProductUOM(UnitType.FORCE, Unit.POUND_FORCE, symbols.getString("lbf.name"),
					symbols.getString("lbf.symbol"), symbols.getString("lbf.desc"), symbols.getString("lbf.unified"),
					getUOM(Unit.POUND_MASS), getUOM(Unit.FEET_PER_SECOND_SQUARED));

			// factor is acceleration of gravity
			factor = getUOM(Unit.GRAVITY).getConversionFactor(getUOM(Unit.FEET_PER_SECOND_SQUARED));
			uom.setScalingFactor(factor);
			break;

		case GRAIN:
			// mass
			amount = Quantity.divide("1", "7000");
			conversion = new Conversion(amount, getUOM(Unit.POUND_MASS));
			uom = createScalarUOM(UnitType.MASS, Unit.GRAIN, symbols.getString("grain.name"),
					symbols.getString("grain.symbol"), symbols.getString("grain.desc"),
					symbols.getString("grain.unified"));
			uom.setConversion(conversion);
			break;

		case MILES_PER_HOUR:
			// velocity
			amount = Quantity.divide("5280", "3600");
			conversion = new Conversion(amount, getUOM(Unit.FEET_PER_SECOND));
			uom = createScalarUOM(UnitType.VELOCITY, Unit.MILES_PER_HOUR, symbols.getString("mph.name"),
					symbols.getString("mph.symbol"), symbols.getString("mph.desc"), symbols.getString("mph.unified"));
			uom.setConversion(conversion);
			break;

		case REV_PER_MIN:
			// rpm
			uom = createQuotientUOM(UnitType.FREQUENCY, Unit.REV_PER_MIN, symbols.getString("rpm.name"),
					symbols.getString("rpm.symbol"), symbols.getString("rpm.desc"), symbols.getString("rpm.unified"),
					getOne(), getMinute());
			break;

		default:
			break;
		}

		return uom;
	}

	private UnitOfMeasure createUSUnit(Unit unit) throws Exception {

		UnitOfMeasure uom = null;
		Conversion conversion = null;
		BigDecimal factor = null;

		switch (unit) {

		case US_GALLON:
			// gallon
			conversion = new Conversion(Quantity.createAmount("231"), getUOM(Unit.CUBIC_INCH));
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_GALLON, symbols.getString("us_gallon.name"),
					symbols.getString("us_gallon.symbol"), symbols.getString("us_gallon.desc"),
					symbols.getString("us_gallon.unified"));
			uom.setConversion(conversion);
			break;

		case US_BARREL:
			// barrel
			conversion = new Conversion(Quantity.createAmount("42"), getUOM(Unit.US_GALLON));
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_BARREL, symbols.getString("us_bbl.name"),
					symbols.getString("us_bbl.symbol"), symbols.getString("us_bbl.desc"),
					symbols.getString("us_bbl.unified"));
			uom.setConversion(conversion);
			break;

		case US_BUSHEL:
			// bushel
			conversion = new Conversion(Quantity.createAmount("2150.42058"), getUOM(Unit.CUBIC_INCH));
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_BUSHEL, symbols.getString("us_bu.name"),
					symbols.getString("us_bu.symbol"), symbols.getString("us_bu.desc"),
					symbols.getString("us_bu.unified"));
			uom.setConversion(conversion);
			break;

		case US_FLUID_OUNCE:
			// fluid ounce
			conversion = new Conversion(Quantity.createAmount("0.0078125"), getUOM(Unit.US_GALLON));
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_FLUID_OUNCE, symbols.getString("us_fl_oz.name"),
					symbols.getString("us_fl_oz.symbol"), symbols.getString("us_fl_oz.desc"),
					symbols.getString("us_fl_oz.unified"));
			uom.setConversion(conversion);
			break;

		case US_CUP:
			// cup
			conversion = new Conversion(Quantity.createAmount("8"), getUOM(Unit.US_FLUID_OUNCE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_CUP, symbols.getString("us_cup.name"),
					symbols.getString("us_cup.symbol"), symbols.getString("us_cup.desc"),
					symbols.getString("us_cup.unified"));
			uom.setConversion(conversion);
			break;

		case US_PINT:
			// pint
			conversion = new Conversion(Quantity.createAmount("16"), getUOM(Unit.US_FLUID_OUNCE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_PINT, symbols.getString("us_pint.name"),
					symbols.getString("us_pint.symbol"), symbols.getString("us_pint.desc"),
					symbols.getString("us_pint.unified"));
			uom.setConversion(conversion);
			break;

		case US_QUART:
			// quart
			conversion = new Conversion(Quantity.createAmount("32"), getUOM(Unit.US_FLUID_OUNCE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_QUART, symbols.getString("us_quart.name"),
					symbols.getString("us_quart.symbol"), symbols.getString("us_quart.desc"),
					symbols.getString("us_quart.unified"));
			uom.setConversion(conversion);
			break;

		case US_TABLESPOON:
			// tablespoon
			conversion = new Conversion(Quantity.createAmount("0.5"), getUOM(Unit.US_FLUID_OUNCE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_TABLESPOON, symbols.getString("us_tbsp.name"),
					symbols.getString("us_tbsp.symbol"), symbols.getString("us_tbsp.desc"),
					symbols.getString("us_tbsp.unified"));
			uom.setConversion(conversion);
			break;

		case US_TEASPOON:
			// teaspoon
			factor = Quantity.divide("1", "6");
			conversion = new Conversion(factor, getUOM(Unit.US_FLUID_OUNCE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_TEASPOON, symbols.getString("us_tsp.name"),
					symbols.getString("us_tsp.symbol"), symbols.getString("us_tsp.desc"),
					symbols.getString("us_tsp.unified"));
			uom.setConversion(conversion);
			break;
			
		case US_TON:
			// ton
			conversion = new Conversion(Quantity.createAmount("2000"), getUOM(Unit.POUND_MASS));
			uom = createScalarUOM(UnitType.MASS, Unit.US_TON, symbols.getString("us_ton.name"),
					symbols.getString("us_ton.symbol"), symbols.getString("us_ton.desc"),
					symbols.getString("us_ton.unified"));
			uom.setConversion(conversion);
			break;

		default:
			break;
		}

		return uom;
	}

	private UnitOfMeasure createBRUnit(Unit unit) throws Exception {

		UnitOfMeasure uom = null;
		Conversion conversion = null;
		BigDecimal factor = null;

		switch (unit) {
		case BR_GALLON:
			// gallon
			conversion = new Conversion(Quantity.createAmount("277.4194327916215"), getUOM(Unit.CUBIC_INCH));
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_GALLON, symbols.getString("br_gallon.name"),
					symbols.getString("br_gallon.symbol"), symbols.getString("br_gallon.desc"),
					symbols.getString("br_gallon.unified"));
			uom.setConversion(conversion);
			break;

		case BR_BUSHEL:
			// bushel
			conversion = new Conversion(Quantity.createAmount("8"), getUOM(Unit.BR_GALLON));
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_BUSHEL, symbols.getString("br_bu.name"),
					symbols.getString("br_bu.symbol"), symbols.getString("br_bu.desc"),
					symbols.getString("br_bu.unified"));
			uom.setConversion(conversion);
			break;

		case BR_FLUID_OUNCE:
			// fluid ounce
			conversion = new Conversion(Quantity.createAmount("0.00625"), getUOM(Unit.BR_GALLON));
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_FLUID_OUNCE, symbols.getString("br_fl_oz.name"),
					symbols.getString("br_fl_oz.symbol"), symbols.getString("br_fl_oz.desc"),
					symbols.getString("br_fl_oz.unified"));
			uom.setConversion(conversion);
			break;

		case BR_CUP:
			// cup
			conversion = new Conversion(Quantity.createAmount("8"), getUOM(Unit.BR_FLUID_OUNCE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_CUP, symbols.getString("br_cup.name"),
					symbols.getString("br_cup.symbol"), symbols.getString("br_cup.desc"),
					symbols.getString("br_cup.unified"));
			uom.setConversion(conversion);
			break;

		case BR_PINT:
			// pint
			conversion = new Conversion(Quantity.createAmount("20"), getUOM(Unit.BR_FLUID_OUNCE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_PINT, symbols.getString("br_pint.name"),
					symbols.getString("br_pint.symbol"), symbols.getString("br_pint.desc"),
					symbols.getString("br_pint.unified"));
			uom.setConversion(conversion);
			break;

		case BR_QUART:
			// quart
			conversion = new Conversion(Quantity.createAmount("40"), getUOM(Unit.BR_FLUID_OUNCE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_QUART, symbols.getString("br_quart.name"),
					symbols.getString("br_quart.symbol"), symbols.getString("br_quart.desc"),
					symbols.getString("br_quart.unified"));
			uom.setConversion(conversion);
			break;

		case BR_TABLESPOON:
			// tablespoon
			conversion = new Conversion(Quantity.createAmount("0.625"), getUOM(Unit.BR_FLUID_OUNCE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_TABLESPOON, symbols.getString("br_tbsp.name"),
					symbols.getString("br_tbsp.symbol"), symbols.getString("br_tbsp.desc"),
					symbols.getString("br_tbsp.unified"));
			uom.setConversion(conversion);
			break;

		case BR_TEASPOON:
			// teaspoon
			factor = Quantity.divide("5", "24");
			conversion = new Conversion(factor, getUOM(Unit.BR_FLUID_OUNCE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_TEASPOON, symbols.getString("br_tsp.name"),
					symbols.getString("br_tsp.symbol"), symbols.getString("br_tsp.desc"),
					symbols.getString("br_tsp.unified"));
			uom.setConversion(conversion);
			break;
			
		case BR_TON:
			// ton
			conversion = new Conversion(Quantity.createAmount("2240"), getUOM(Unit.POUND_MASS));
			uom = createScalarUOM(UnitType.MASS, Unit.BR_TON, symbols.getString("br_ton.name"),
					symbols.getString("br_ton.symbol"), symbols.getString("br_ton.desc"),
					symbols.getString("br_ton.unified"));
			uom.setConversion(conversion);
			break;

		default:
			break;
		}

		return uom;
	}

	/**
	 * Get the unit of measure with this unique enumerated type
	 * 
	 * @param enumeration
	 *            {@link Unit}
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception Exception
	 */
	public UnitOfMeasure getUOM(Unit enumeration) throws Exception {
		UnitOfMeasure uom = unitRegistry.get(enumeration);

		if (uom == null && enumeration instanceof Unit) {
			uom = createUOM((Unit) enumeration);
		}
		return uom;
	}

	/**
	 * Get the fundamental unit of measure of time
	 * 
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception Exception
	 */
	public UnitOfMeasure getSecond() throws Exception {
		return getUOM(Unit.SECOND);
	}

	/**
	 * Get the unit of measure for a minute (60 seconds)
	 * 
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure getMinute() throws Exception {
		return getUOM(Unit.MINUTE);
	}

	/**
	 * Get the unit of measure for an hour (60 minutes)
	 * 
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure getHour() throws Exception {
		return getUOM(Unit.HOUR);
	}

	/**
	 * Get the unit of measure for one day (24 hours)
	 * 
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure getDay() throws Exception {
		return getUOM(Unit.DAY);
	}

	/**
	 * Get the unit of measure for unity 'one'
	 * 
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure getOne() throws Exception {
		return getUOM(Unit.ONE);
	}

	/**
	 * Get the unit of measure with this unique symbol
	 * 
	 * @param symbol
	 *            Symbol
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getUOM(String symbol) {
		return symbolRegistry.get(symbol);
	}

	private void checkExistance(String symbol, Unit id) throws Exception {
		if (symbol == null || symbol.length() == 0) {
			throw new Exception(MeasurementSystem.getMessage("symbol.cannot.be.null"));
		}

		if (symbolRegistry.containsKey(symbol)) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("already.created"), symbol,
					symbolRegistry.get(symbol).toString());
			throw new Exception(msg);
		}

		if (unitRegistry.containsKey(id)) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("already.created"), symbol,
					unitRegistry.get(id).toString());
			throw new Exception(msg);
		}
	}

	/**
	 * Remove all cached units of measure
	 */
	public void clearCache() {
		symbolRegistry.clear();
		unitRegistry.clear();
	}

	/**
	 * Get all units currently cached by this measurement system
	 * 
	 * @return List of {@link UnitOfMeasure}
	 */
	public List<UnitOfMeasure> getRegisteredUnits() {
		Collection<UnitOfMeasure> units = symbolRegistry.values();
		List<UnitOfMeasure> list = new ArrayList<>(units);

		Collections.sort(list, new Comparator<UnitOfMeasure>() {
			public int compare(UnitOfMeasure unit1, UnitOfMeasure unit2) {

				return unit1.getSymbol().compareTo(unit2.getSymbol());
			}
		});

		return list;
	}

	/**
	 * Remove a unit from the cache
	 * 
	 * @param uom
	 *            {@link UnitOfMeasure} to remove
	 */
	public void unregisterUnit(UnitOfMeasure uom) {
		if (uom != null) {
			unitRegistry.remove(uom.getEnumeration());
			symbolRegistry.remove(uom.getSymbol());
		}
	}

	ResourceBundle getSymbols() {
		return symbols;
	}

	private void registerUnit(UnitOfMeasure uom) throws Exception {
		Unit id = uom.getEnumeration();

		if (id != null) {
			UnitOfMeasure current = unitRegistry.get(id);

			if (current != null) {
				String msg = MessageFormat.format(getMessage("already.registered"), uom.toString(), id.toString(),
						current.toString());
				throw new Exception(msg);
			}

			unitRegistry.put(id, uom);
		}
	}

	private void cacheUnit(UnitOfMeasure uom) throws Exception {

		String key = uom.getSymbol();

		// register first by symbol
		UnitOfMeasure current = symbolRegistry.get(key);

		if (current != null) {
			String msg = MessageFormat.format(getMessage("already.registered"), uom.toString(), key,
					current.toString());
			throw new Exception(msg);
		}
		symbolRegistry.put(key, uom);

		// next by unit enumeration
		registerUnit(uom);

		// finally by base symbol
		key = uom.getBaseSymbol();

		if (symbolRegistry.get(key) == null) {
			symbolRegistry.put(key, uom);
		}
	}

	private void checkType(UnitType type) throws Exception {
		if (type == null) {
			throw new Exception(getMessage("unit.type.cannot.be.null"));
		}
	}

	private UnitOfMeasure createUOM(Class<?> clazz, UnitType type, Unit id, String name, String symbol,
			String description) throws Exception {

		checkType(type);
		checkExistance(symbol, id);

		UnitOfMeasure uom = null;

		if (clazz.equals(ScalarUOM.class)) {
			uom = new ScalarUOM(type, name, symbol, description, this);

		} else if (clazz.equals(QuotientUOM.class)) {
			uom = new QuotientUOM(type, name, symbol, description, this);

		} else if (clazz.equals(ProductUOM.class)) {
			uom = new ProductUOM(type, name, symbol, description, this);

		} else if (clazz.equals(PowerUOM.class)) {
			uom = new PowerUOM(type, name, symbol, description, this);

		} else {
			String msg = MessageFormat.format(getMessage("unsupported.class"), clazz.toString());
			throw new Exception(msg);
		}

		return uom;
	}

	/**
	 * Create a unit of measure that is not a power, product or quotient
	 * 
	 * @param type
	 *            {@link UnitType}
	 * @param id
	 *            {@link Unit}
	 * @param name
	 *            Name of unit of measure
	 * @param symbol
	 *            Symbol (must be unique)
	 * @param description
	 *            Description of unit of measure
	 * @param unified
	 *            UCUM symbol
	 * @return {@link ScalarUOM}
	 * @throws Exception
	 *             Exception
	 */
	public ScalarUOM createScalarUOM(UnitType type, Unit id, String name, String symbol, String description,
			String unified) throws Exception {

		ScalarUOM uom = (ScalarUOM) createUOM(ScalarUOM.class, type, id, name, symbol, description);
		uom.setEnumeration(id);
		uom.setUnifiedSymbol(unified);
		cacheUnit(uom);

		return uom;
	}

	/**
	 * Create a unit of measure that is not a power, product or quotient
	 * 
	 * @param type
	 *            {@link UnitType}
	 * @param name
	 *            Name of unit of measure
	 * @param symbol
	 *            Symbol (must be unique)
	 * @param description
	 *            Description of unit of measure
	 * @return {@link ScalarUOM}
	 * @throws Exception
	 *             Exception
	 */
	public ScalarUOM createScalarUOM(UnitType type, String name, String symbol, String description) throws Exception {
		return createScalarUOM(type, null, name, symbol, description, null);
	}

	/**
	 * Create a unit of measure that is a unit divided by another unit
	 * 
	 * @param type
	 *            {@link UnitType}
	 * @param id
	 *            {@link Unit}
	 * @param name
	 *            Name of unit of measure
	 * @param symbol
	 *            Symbol (must be unique)
	 * @param description
	 *            Description of unit of measure
	 * @param unified
	 *            UCUM symbol
	 * @param dividend
	 *            {@link UnitOfMeasure}
	 * @param divisor
	 *            {@link UnitOfMeasure}
	 * @return {@link QuotientUOM}
	 * @throws Exception
	 *             Exception
	 */
	public QuotientUOM createQuotientUOM(UnitType type, Unit id, String name, String symbol, String description,
			String unified, UnitOfMeasure dividend, UnitOfMeasure divisor) throws Exception {

		QuotientUOM uom = (QuotientUOM) createUOM(QuotientUOM.class, type, id, name, symbol, description);
		uom.setUnits(dividend, divisor);
		uom.setEnumeration(id);
		uom.setUnifiedSymbol(unified);
		cacheUnit(uom);
		return uom;
	}

	/**
	 * Create a unit of measure that is a unit divided by another unit
	 * 
	 * @param type
	 *            {@link UnitType}
	 * @param name
	 *            Name of unit of measure
	 * @param symbol
	 *            Symbol (must be unique)
	 * @param description
	 *            Description of unit of measure
	 * @param dividend
	 *            {@link UnitOfMeasure}
	 * @param divisor
	 *            {@link UnitOfMeasure}
	 * @return {@link QuotientUOM}
	 * @throws Exception
	 *             Exception
	 */
	public QuotientUOM createQuotientUOM(UnitType type, String name, String symbol, String description,
			UnitOfMeasure dividend, UnitOfMeasure divisor) throws Exception {
		return this.createQuotientUOM(type, null, name, symbol, description, null, dividend, divisor);
	}

	/**
	 * Create a unit of measure that is the product of two other units of
	 * measure
	 * 
	 * @param type
	 *            {@link UnitType}
	 * @param id
	 *            {@link Unit}
	 * @param name
	 *            Name of unit of measure
	 * @param symbol
	 *            Symbol (must be unique)
	 * @param description
	 *            Description of unit of measure
	 * @param unified
	 *            UCUM symbol
	 * @param multiplier
	 *            {@link UnitOfMeasure} multiplier
	 * @param multiplicand
	 *            {@link UnitOfMeasure} multiplicand
	 * @return {@link ProductUOM}
	 * @throws Exception
	 *             Exception
	 */
	public ProductUOM createProductUOM(UnitType type, Unit id, String name, String symbol, String description,
			String unified, UnitOfMeasure multiplier, UnitOfMeasure multiplicand) throws Exception {

		ProductUOM uom = (ProductUOM) createUOM(ProductUOM.class, type, id, name, symbol, description);
		uom.setUnits(multiplier, multiplicand);
		uom.setEnumeration(id);
		uom.setUnifiedSymbol(unified);
		cacheUnit(uom);
		return uom;
	}

	/**
	 * Create a unit of measure that is the product of two other units of
	 * measure
	 * 
	 * @param type
	 *            {@link UnitType}
	 * @param name
	 *            Name of unit of measure
	 * @param symbol
	 *            Symbol (must be unique)
	 * @param description
	 *            Description of unit of measure
	 * @param multiplier
	 *            {@link UnitOfMeasure} multiplier
	 * @param multiplicand
	 *            {@link UnitOfMeasure} multiplicand
	 * @return {@link ProductUOM}
	 * @throws Exception
	 *             Exception
	 */
	public ProductUOM createProductUOM(UnitType type, String name, String symbol, String description,
			UnitOfMeasure multiplier, UnitOfMeasure multiplicand) throws Exception {
		return createProductUOM(type, null, name, symbol, description, null, multiplier, multiplicand);
	}

	/**
	 * Create a unit of measure with a base raised to an integral power
	 * 
	 * @param type
	 *            {@link UnitType}
	 * @param id
	 *            {@link Unit}
	 * @param name
	 *            Name of unit of measure
	 * @param symbol
	 *            Symbol (must be unique)
	 * @param description
	 *            Description of unit of measure
	 * @param unified
	 *            UCUM symbol
	 * @param base
	 *            {@link UnitOfMeasure}
	 * @param power
	 *            Exponent
	 * @return {@link PowerUOM}
	 * @throws Exception
	 *             Exception
	 */
	public PowerUOM createPowerUOM(UnitType type, Unit id, String name, String symbol, String description,
			String unified, UnitOfMeasure base, int power) throws Exception {

		PowerUOM uom = (PowerUOM) createUOM(PowerUOM.class, type, id, name, symbol, description);
		uom.setUnits(base, power);
		uom.setEnumeration(id);
		uom.setUnifiedSymbol(unified);
		cacheUnit(uom);
		return uom;
	}

	/**
	 * Create a unit of measure with a base raised to an integral power
	 * 
	 * @param type
	 *            {@link UnitType}
	 * @param name
	 *            Name of unit of measure
	 * @param symbol
	 *            Symbol (must be unique)
	 * @param description
	 *            Description of unit of measure
	 * @param base
	 *            {@link UnitOfMeasure}
	 * @param power
	 *            Exponent
	 * @return {@link PowerUOM}
	 * @throws Exception
	 *             Exception
	 */
	public PowerUOM createPowerUOM(UnitType type, String name, String symbol, String description, UnitOfMeasure base,
			int power) throws Exception {
		return createPowerUOM(type, null, name, symbol, description, null, base, power);
	}

}
