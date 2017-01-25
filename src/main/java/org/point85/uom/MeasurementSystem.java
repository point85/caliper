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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A MeasurementSystem is a collection of units of measure that have a linear
 * relationship to each other: y = ax + b where x is the unit to be converted, y
 * is the converted unit, a is the scaling factor and b is the offset. <br>
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
 * <li>JSR 363: <i><a href=
 * "https://java.net/downloads/unitsofmeasurement/JSR363Specification_EDR.pdf">JSR
 * 363 Specification</a></i></li>
 * </ul>
 * <br>
 * The MeasurementSystem class creates:
 * <ul>
 * <li>the 7 SI fundamental units of measure</li>
 * <li>20 SI units derived from these fundamental units</li>
 * <li>other units in the International Customary, US and British Imperial
 * systems</li>
 * <li>any number of custom units of measure</li>
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
	private ResourceBundle symbols;

	// registry by unit symbol
	private Map<String, UnitOfMeasure> symbolRegistry = new ConcurrentHashMap<String, UnitOfMeasure>();

	// registry for units by enumeration
	private Map<Unit, UnitOfMeasure> unitRegistry = new ConcurrentHashMap<Unit, UnitOfMeasure>();

	private MeasurementSystem() {
		initialize();
	}

	void initialize() {
		// common unit strings
		symbols = ResourceBundle.getBundle(UNIT_BUNDLE_NAME, Locale.getDefault());
		messages = ResourceBundle.getBundle(MESSAGES_BUNDLE_NAME, Locale.getDefault());
	}

	// get a particular message by its key
	static String getMessage(String key) {
		return messages.getString(key);
	}

	/**
	 * Get the unified system of units of measure for International Customary,
	 * SI, US, British Imperial as well as custom systems
	 * 
	 * @return {@link MeasurementSystem}
	 */
	public static MeasurementSystem getSystem() {
		if (unifiedSystem == null) {
			createUnifiedSystem();
		}

		return unifiedSystem;
	}

	private static void createUnifiedSystem() {
		unifiedSystem = new MeasurementSystem();
	}

	private UnitOfMeasure createUOM(Unit enumeration) throws Exception {
		UnitOfMeasure uom = null;

		// SI
		uom = createSIUnit(enumeration);

		if (uom != null) {
			return uom;
		}

		// Customary
		uom = createCustomaryUnit(enumeration);

		if (uom != null) {
			return uom;
		}

		// US
		uom = createUSUnit(enumeration);

		if (uom != null) {
			return uom;
		}

		// British
		uom = createBRUnit(enumeration);

		if (uom != null) {
			return uom;
		}

		return uom;
	}

	public NamedQuantity getQuantity(Constant constant) throws Exception {
		NamedQuantity named = null;

		switch (constant) {
		case LIGHT_VELOCITY:
			named = new NamedQuantity(Quantity.createAmount("299792458"), getUOM(Unit.METRE_PER_SECOND));
			named.setId(symbols.getString("light.name"), symbols.getString("light.symbol"),
					symbols.getString("light.desc"));
			break;

		case LIGHT_YEAR:
			NamedQuantity year = new NamedQuantity(BigDecimal.ONE, getUOM(Unit.JULIAN_YEAR));
			named = new NamedQuantity(getQuantity(Constant.LIGHT_VELOCITY).multiply(year));
			named.setId(symbols.getString("ly.name"), symbols.getString("ly.symbol"), symbols.getString("ly.desc"));
			break;

		case GRAVITY:
			named = new NamedQuantity(Quantity.createAmount("9.80665"), getUOM(Unit.METRE_PER_SECOND_SQUARED));
			named.setId(symbols.getString("gravity.name"), symbols.getString("gravity.symbol"),
					symbols.getString("gravity.desc"));
			break;

		case PLANCK_CONSTANT:
			UnitOfMeasure js = createProductUOM(UnitType.UNCLASSIFIED, symbols.getString("js.name"),
					symbols.getString("js.symbol"), symbols.getString("js.desc"), getUOM(Unit.JOULE), getSecond());

			named = new NamedQuantity(Quantity.createAmount("6.62607004081E-34"), js);
			named.setId(symbols.getString("planck.name"), symbols.getString("planck.symbol"),
					symbols.getString("planck.desc"));
			break;

		case BOLTZMANN_CONSTANT:
			UnitOfMeasure jk = createQuotientUOM(UnitType.UNCLASSIFIED, symbols.getString("jk.name"),
					symbols.getString("jk.symbol"), symbols.getString("jk.desc"), getUOM(Unit.JOULE),
					getUOM(Unit.KELVIN));

			named = new NamedQuantity(Quantity.createAmount("1.3806485279E-23"), jk);
			named.setId(symbols.getString("boltzmann.name"), symbols.getString("boltzmann.symbol"),
					symbols.getString("boltzmann.desc"));
			break;

		case AVAGADRO_CONSTANT:
			// NA
			named = new NamedQuantity(Quantity.createAmount("6.02214085774E+23"), getOne());
			named.setId(symbols.getString("avo.name"), symbols.getString("avo.symbol"), symbols.getString("avo.desc"));
			break;

		case GAS_CONSTANT:
			// R
			named = new NamedQuantity(
					getQuantity(Constant.BOLTZMANN_CONSTANT).multiply(getQuantity(Constant.AVAGADRO_CONSTANT)));
			named.setId(symbols.getString("gas.name"), symbols.getString("gas.symbol"), symbols.getString("gas.desc"));
			break;

		case ELEMENTARY_CHARGE:
			// e
			named = new NamedQuantity(Quantity.createAmount("1.602176620898E-19"), getUOM(Unit.COULOMB));
			named.setId(symbols.getString("e.name"), symbols.getString("e.symbol"), symbols.getString("e.desc"));
			break;

		case FARADAY_CONSTANT:
			// F = e.NA
			Quantity qe = getQuantity(Constant.ELEMENTARY_CHARGE);
			named = new NamedQuantity(qe.multiply(getQuantity(Constant.AVAGADRO_CONSTANT)));
			named.setId(symbols.getString("faraday.name"), symbols.getString("faraday.symbol"),
					symbols.getString("faraday.desc"));
			break;

		case ELECTRIC_PERMITTIVITY:
			// epsilon0 = 1/(mu0*c^2)
			NamedQuantity vc = getQuantity(Constant.LIGHT_VELOCITY);
			Quantity eps0 = getQuantity(Constant.MAGNETIC_PERMEABILITY).multiply(vc).multiply(vc).invert();
			named = new NamedQuantity(eps0);
			named.setId(symbols.getString("eps0.name"), symbols.getString("eps0.symbol"),
					symbols.getString("eps0.desc"));
			break;

		case MAGNETIC_PERMEABILITY:
			// mu0
			UnitOfMeasure hm = createQuotientUOM(UnitType.UNCLASSIFIED, symbols.getString("hm.name"),
					symbols.getString("hm.symbol"), symbols.getString("hm.desc"), getUOM(Unit.HENRY),
					getUOM(Unit.METRE));

			BigDecimal fourPi = new BigDecimal(4.0 * Math.PI).multiply(new BigDecimal("1.0E-07"),
					UnitOfMeasure.MATH_CONTEXT);
			named = new NamedQuantity(fourPi, hm);
			named.setId(symbols.getString("mu0.name"), symbols.getString("mu0.symbol"), symbols.getString("mu0.desc"));
			break;

		case ELECTRON_MASS:
			// me
			named = new NamedQuantity(Quantity.createAmount("9.1093835611E-28"), getUOM(Unit.GRAM));
			named.setId(symbols.getString("me.name"), symbols.getString("me.symbol"), symbols.getString("me.desc"));
			break;

		case PROTON_MASS:
			// mp
			named = new NamedQuantity(Quantity.createAmount("1.67262189821E-24"), getUOM(Unit.GRAM));
			named.setId(symbols.getString("mp.name"), symbols.getString("mp.symbol"), symbols.getString("mp.desc"));
			break;

		default:
			break;
		}

		return named;
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
					symbols.getString("one.symbol"), symbols.getString("one.desc"));
			break;

		case SECOND:
			// second
			uom = createScalarUOM(UnitType.TIME, Unit.SECOND, symbols.getString("sec.name"),
					symbols.getString("sec.symbol"), symbols.getString("sec.desc"));
			break;

		case MINUTE:
			// minute
			conversion = new Conversion(Quantity.createAmount("60"), getUOM(Unit.SECOND));
			uom = createScalarUOM(UnitType.TIME, Unit.MINUTE, symbols.getString("min.name"),
					symbols.getString("min.symbol"), symbols.getString("min.desc"));
			uom.setConversion(conversion);
			break;

		case HOUR:
			// hour
			conversion = new Conversion(Quantity.createAmount("60"), getUOM(Unit.MINUTE));
			uom = createScalarUOM(UnitType.TIME, Unit.HOUR, symbols.getString("hr.name"),
					symbols.getString("hr.symbol"), symbols.getString("hr.desc"));
			uom.setConversion(conversion);
			break;

		case DAY:
			// day
			conversion = new Conversion(Quantity.createAmount("24"), getUOM(Unit.HOUR));
			uom = createScalarUOM(UnitType.TIME, Unit.DAY, symbols.getString("day.name"),
					symbols.getString("day.symbol"), symbols.getString("day.desc"));
			uom.setConversion(conversion);
			break;

		case WEEK:
			// week
			conversion = new Conversion(Quantity.createAmount("7"), getUOM(Unit.DAY));
			uom = createScalarUOM(UnitType.TIME, Unit.WEEK, symbols.getString("week.name"),
					symbols.getString("week.symbol"), symbols.getString("week.desc"));
			uom.setConversion(conversion);
			break;

		case JULIAN_YEAR:
			// Julian year
			conversion = new Conversion(Quantity.createAmount("365.25"), getUOM(Unit.DAY));
			uom = createScalarUOM(UnitType.TIME, Unit.JULIAN_YEAR, symbols.getString("jyear.name"),
					symbols.getString("jyear.symbol"), symbols.getString("jyear.desc"));
			uom.setConversion(conversion);

			break;

		case SQUARE_SECOND:
			// square second
			uom = createPowerUOM(UnitType.TIME_SQUARED, Unit.SQUARE_SECOND, symbols.getString("s2.name"),
					symbols.getString("s2.symbol"), symbols.getString("s2.desc"), getUOM(Unit.SECOND), 2);
			break;

		case MOLE:
			// substance amount
			conversion = new Conversion(Quantity.createAmount("6.02214085774E+23"), getOne());
			uom = createScalarUOM(UnitType.SUBSTANCE_AMOUNT, Unit.MOLE, symbols.getString("mole.name"),
					symbols.getString("mole.symbol"), symbols.getString("mole.desc"));
			uom.setConversion(conversion);
			break;

		case DECIBEL:
			// decibel
			uom = createScalarUOM(UnitType.INTENSITY, Unit.DECIBEL, symbols.getString("db.name"),
					symbols.getString("db.symbol"), symbols.getString("db.desc"));
			break;

		case RADIAN:
			// plane angle radian (rad)
			conversion = new Conversion(getOne());
			uom = createScalarUOM(UnitType.PLANE_ANGLE, Unit.RADIAN, symbols.getString("radian.name"),
					symbols.getString("radian.symbol"), symbols.getString("radian.desc"));
			uom.setConversion(conversion);
			break;

		case STERADIAN:
			// solid angle steradian (sr)
			conversion = new Conversion(getOne());
			uom = createScalarUOM(UnitType.SOLID_ANGLE, Unit.STERADIAN, symbols.getString("steradian.name"),
					symbols.getString("steradian.symbol"), symbols.getString("steradian.desc"));
			uom.setConversion(conversion);
			break;

		case DEGREE:
			// degree of arc
			factor = Quantity.divideAmounts(String.valueOf(Math.PI), "180");
			conversion = new Conversion(factor, getUOM(Unit.RADIAN));
			uom = createScalarUOM(UnitType.PLANE_ANGLE, Unit.DEGREE, symbols.getString("degree.name"),
					symbols.getString("degree.symbol"), symbols.getString("degree.desc"));
			uom.setConversion(conversion);
			break;

		case METRE:
			// fundamental length
			uom = createScalarUOM(UnitType.LENGTH, Unit.METRE, symbols.getString("m.name"),
					symbols.getString("m.symbol"), symbols.getString("m.desc"));
			break;

		case DIOPTER:
			// per metre
			uom = createQuotientUOM(UnitType.RECIPROCAL_LENGTH, Unit.DIOPTER, symbols.getString("diopter.name"),
					symbols.getString("diopter.symbol"), symbols.getString("diopter.desc"), getOne(),
					getUOM(Unit.METRE));
			break;

		case KILOGRAM:
			// fundamental mass
			uom = createScalarUOM(UnitType.MASS, Unit.KILOGRAM, symbols.getString("kg.name"),
					symbols.getString("kg.symbol"), symbols.getString("kg.desc"));
			break;

		case TONNE:
			// mass
			conversion = new Conversion(Prefix.KILO.getScalingFactor(), getUOM(Unit.KILOGRAM));
			uom = createScalarUOM(UnitType.MASS, Unit.TONNE, symbols.getString("tonne.name"),
					symbols.getString("tonne.symbol"), symbols.getString("tonne.desc"));
			uom.setConversion(conversion);
			break;

		case KELVIN:
			// fundamental temperature
			uom = createScalarUOM(UnitType.TEMPERATURE, Unit.KELVIN, symbols.getString("kelvin.name"),
					symbols.getString("kelvin.symbol"), symbols.getString("kelvin.desc"));
			break;

		case AMPERE:
			// electric current
			uom = createScalarUOM(UnitType.ELECTRIC_CURRENT, Unit.AMPERE, symbols.getString("amp.name"),
					symbols.getString("amp.symbol"), symbols.getString("amp.desc"));
			break;

		case CANDELA:
			// luminosity
			uom = createScalarUOM(UnitType.LUMINOSITY, Unit.CANDELA, symbols.getString("cd.name"),
					symbols.getString("cd.symbol"), symbols.getString("cd.desc"));
			break;

		case PH:
			// molar concentration
			uom = createScalarUOM(UnitType.MOLAR_CONCENTRATION, Unit.PH, symbols.getString("ph.name"),
					symbols.getString("ph.symbol"), symbols.getString("ph.desc"));
			break;

		case GRAM:
			// gram
			conversion = new Conversion(Prefix.MILLI.getScalingFactor(), getUOM(Unit.KILOGRAM));
			uom = createScalarUOM(UnitType.MASS, Unit.GRAM, symbols.getString("gram.name"),
					symbols.getString("gram.symbol"), symbols.getString("gram.desc"));
			uom.setConversion(conversion);
			break;

		case CARAT:
			// carat
			conversion = new Conversion(Quantity.createAmount("0.2"), getUOM(Unit.GRAM));
			uom = createScalarUOM(UnitType.MASS, Unit.CARAT, symbols.getString("carat.name"),
					symbols.getString("carat.symbol"), symbols.getString("carat.desc"));
			uom.setConversion(conversion);
			break;

		case SQUARE_METRE:
			// square metre
			uom = createPowerUOM(UnitType.AREA, Unit.SQUARE_METRE, symbols.getString("m2.name"),
					symbols.getString("m2.symbol"), symbols.getString("m2.desc"), getUOM(Unit.METRE), 2);
			break;

		case METRE_PER_SECOND:
			// velocity
			uom = createQuotientUOM(UnitType.VELOCITY, Unit.METRE_PER_SECOND, symbols.getString("mps.name"),
					symbols.getString("mps.symbol"), symbols.getString("mps.desc"), getUOM(Unit.METRE), getSecond());
			break;

		case METRE_PER_SECOND_SQUARED:
			// acceleration
			uom = createQuotientUOM(UnitType.ACCELERATION, Unit.METRE_PER_SECOND_SQUARED,
					symbols.getString("mps2.name"), symbols.getString("mps2.symbol"), symbols.getString("mps2.desc"),
					getUOM(Unit.METRE), getUOM(Unit.SQUARE_SECOND));
			break;

		case CUBIC_METRE:
			// cubic metre
			uom = createPowerUOM(UnitType.VOLUME, Unit.CUBIC_METRE, symbols.getString("m3.name"),
					symbols.getString("m3.symbol"), symbols.getString("m3.desc"), getUOM(Unit.METRE), 3);
			break;

		case LITRE:
			// litre
			conversion = new Conversion(Prefix.MILLI.getScalingFactor(), getUOM(Unit.CUBIC_METRE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.LITRE, symbols.getString("litre.name"),
					symbols.getString("litre.symbol"), symbols.getString("litre.desc"));
			uom.setConversion(conversion);
			break;

		case CUBIC_METRE_PER_SECOND:
			// flow (volume)
			uom = createQuotientUOM(UnitType.VOLUMETRIC_FLOW, Unit.CUBIC_METRE_PER_SECOND,
					symbols.getString("m3PerSec.name"), symbols.getString("m3PerSec.symbol"),
					symbols.getString("m3PerSec.desc"), getUOM(Unit.CUBIC_METRE), getSecond());
			break;

		case KILOGRAM_PER_SECOND:
			// flow (mass)
			uom = createQuotientUOM(UnitType.MASS_FLOW, Unit.KILOGRAM_PER_SECOND, symbols.getString("kgPerSec.name"),
					symbols.getString("kgPerSec.symbol"), symbols.getString("kgPerSec.desc"), getUOM(Unit.KILOGRAM),
					getSecond());
			break;

		case KILOGRAM_PER_CUBIC_METRE:
			// kg/m^3
			uom = createQuotientUOM(UnitType.DENSITY, Unit.KILOGRAM_PER_CUBIC_METRE, symbols.getString("kg_m3.name"),
					symbols.getString("kg_m3.symbol"), symbols.getString("kg_m3.desc"), getUOM(Unit.KILOGRAM),
					getUOM(Unit.CUBIC_METRE));
			break;

		case PASCAL_SECOND:
			// dynamic viscosity
			uom = createProductUOM(UnitType.DYNAMIC_VISCOSITY, Unit.PASCAL_SECOND, symbols.getString("pascal_sec.name"),
					symbols.getString("pascal_sec.symbol"), symbols.getString("pascal_sec.desc"), getUOM(Unit.PASCAL),
					getSecond());
			break;

		case SQUARE_METRE_PER_SECOND:
			// kinematic viscosity
			uom = createQuotientUOM(UnitType.KINEMATIC_VISCOSITY, Unit.SQUARE_METRE_PER_SECOND,
					symbols.getString("m2PerSec.name"), symbols.getString("m2PerSec.symbol"),
					symbols.getString("m2PerSec.desc"), getUOM(Unit.SQUARE_METRE), getSecond());
			break;

		case CALORIE:
			// thermodynamic calorie
			conversion = new Conversion(Quantity.createAmount("4.184"), getUOM(Unit.JOULE));
			uom = createScalarUOM(UnitType.ENERGY, Unit.CALORIE, symbols.getString("calorie.name"),
					symbols.getString("calorie.symbol"), symbols.getString("calorie.desc"));
			uom.setConversion(conversion);
			break;

		case NEWTON:
			// force F = m·A (newton)
			uom = createProductUOM(UnitType.FORCE, Unit.NEWTON, symbols.getString("newton.name"),
					symbols.getString("newton.symbol"), symbols.getString("newton.desc"), getUOM(Unit.KILOGRAM),
					getUOM(Unit.METRE_PER_SECOND_SQUARED));
			break;

		case NEWTON_METRE:
			// newton-metre
			uom = createProductUOM(UnitType.ENERGY, Unit.NEWTON_METRE, symbols.getString("n_m.name"),
					symbols.getString("n_m.symbol"), symbols.getString("n_m.desc"), getUOM(Unit.NEWTON),
					getUOM(Unit.METRE));
			break;

		case JOULE:
			// energy (joule)
			uom = createProductUOM(UnitType.ENERGY, Unit.JOULE, symbols.getString("joule.name"),
					symbols.getString("joule.symbol"), symbols.getString("joule.desc"), getUOM(Unit.NEWTON),
					getUOM(Unit.METRE));
			break;

		case ELECTRON_VOLT:
			// ev
			NamedQuantity e = this.getQuantity(Constant.ELEMENTARY_CHARGE);
			uom = createProductUOM(UnitType.ENERGY, Unit.ELECTRON_VOLT, symbols.getString("ev.name"),
					symbols.getString("ev.symbol"), symbols.getString("ev.desc"), e.getUOM(), getUOM(Unit.VOLT));
			uom.setScalingFactor(e.getAmount());
			break;

		case WATT_HOUR:
			// watt-hour
			uom = createProductUOM(UnitType.ENERGY, Unit.WATT_HOUR, symbols.getString("wh.name"),
					symbols.getString("wh.symbol"), symbols.getString("wh.desc"), getUOM(Unit.WATT), getHour());
			break;

		case WATT:
			// power (watt)
			uom = createQuotientUOM(UnitType.POWER, Unit.WATT, symbols.getString("watt.name"),
					symbols.getString("watt.symbol"), symbols.getString("watt.desc"), getUOM(Unit.JOULE), getSecond());
			break;

		case HERTZ:
			// frequency (hertz)
			uom = createQuotientUOM(UnitType.FREQUENCY, Unit.HERTZ, symbols.getString("hertz.name"),
					symbols.getString("hertz.symbol"), symbols.getString("hertz.desc"), getOne(), getSecond());
			break;

		case RAD_PER_SEC:
			// angular frequency
			BigDecimal twoPi = new BigDecimal("2").multiply(new BigDecimal(Math.PI), UnitOfMeasure.MATH_CONTEXT);
			conversion = new Conversion(BigDecimal.ONE.divide(twoPi, UnitOfMeasure.MATH_CONTEXT), getUOM(Unit.HERTZ));
			uom = createQuotientUOM(UnitType.FREQUENCY, Unit.RAD_PER_SEC, symbols.getString("radpers.name"),
					symbols.getString("radpers.symbol"), symbols.getString("radpers.desc"), getUOM(Unit.RADIAN),
					getSecond());
			uom.setConversion(conversion);
			break;

		case PASCAL:
			// pressure (pascal)
			uom = createQuotientUOM(UnitType.PRESSURE, Unit.PASCAL, symbols.getString("pascal.name"),
					symbols.getString("pascal.symbol"), symbols.getString("pascal.desc"), getUOM(Unit.NEWTON),
					getUOM(Unit.SQUARE_METRE));
			break;

		case ATMOSPHERE:
			// pressure
			conversion = new Conversion(Quantity.createAmount("101325"), getUOM(Unit.PASCAL));
			uom = createScalarUOM(UnitType.PRESSURE, Unit.ATMOSPHERE, symbols.getString("atm.name"),
					symbols.getString("atm.symbol"), symbols.getString("atm.desc"));
			uom.setConversion(conversion);
			break;

		case BAR:
			// pressure (bar)
			conversion = new Conversion(BigDecimal.ONE, getUOM(Unit.PASCAL), Quantity.createAmount("1.0E+05"));
			uom = createScalarUOM(UnitType.PRESSURE, Unit.BAR, symbols.getString("bar.name"),
					symbols.getString("bar.symbol"), symbols.getString("bar.desc"));
			uom.setConversion(conversion);
			break;

		case COULOMB:
			// charge (coulomb)
			uom = createProductUOM(UnitType.ELECTRIC_CHARGE, Unit.COULOMB, symbols.getString("coulomb.name"),
					symbols.getString("coulomb.symbol"), symbols.getString("coulomb.desc"), getUOM(Unit.AMPERE),
					getSecond());
			break;

		case VOLT:
			// voltage (volt)
			uom = createQuotientUOM(UnitType.ELECTROMOTIVE_FORCE, Unit.VOLT, symbols.getString("volt.name"),
					symbols.getString("volt.symbol"), symbols.getString("volt.desc"), getUOM(Unit.WATT),
					getUOM(Unit.AMPERE));
			break;

		case OHM:
			// resistance (ohm)
			uom = createQuotientUOM(UnitType.ELECTRIC_RESISTANCE, Unit.OHM, symbols.getString("ohm.name"),
					symbols.getString("ohm.symbol"), symbols.getString("ohm.desc"), getUOM(Unit.VOLT),
					getUOM(Unit.AMPERE));
			break;

		case FARAD:
			// capacitance (farad)
			uom = createQuotientUOM(UnitType.ELECTRIC_CAPACITANCE, Unit.FARAD, symbols.getString("farad.name"),
					symbols.getString("farad.symbol"), symbols.getString("farad.desc"), getUOM(Unit.COULOMB),
					getUOM(Unit.VOLT));
			break;

		case FARAD_PER_METRE:
			// electric permittivity (farad/metre)
			uom = createQuotientUOM(UnitType.ELECTRIC_PERMITTIVITY, Unit.FARAD_PER_METRE,
					symbols.getString("fperm.name"), symbols.getString("fperm.symbol"), symbols.getString("fperm.desc"),
					getUOM(Unit.FARAD), getUOM(Unit.METRE));
			break;

		case AMPERE_PER_METRE:
			// electric field strength(ampere/metre)
			uom = createQuotientUOM(UnitType.ELECTRIC_FIELD_STRENGTH, Unit.AMPERE_PER_METRE,
					symbols.getString("aperm.name"), symbols.getString("aperm.symbol"), symbols.getString("aperm.desc"),
					getUOM(Unit.AMPERE), getUOM(Unit.METRE));
			break;

		case WEBER:
			// magnetic flux (weber)
			uom = createProductUOM(UnitType.MAGNETIC_FLUX, Unit.WEBER, symbols.getString("weber.name"),
					symbols.getString("weber.symbol"), symbols.getString("weber.desc"), getUOM(Unit.VOLT), getSecond());
			break;

		case TESLA:
			// magnetic flux density (tesla)
			uom = createQuotientUOM(UnitType.MAGNETIC_FLUX_DENSITY, Unit.TESLA, symbols.getString("tesla.name"),
					symbols.getString("tesla.symbol"), symbols.getString("tesla.desc"), getUOM(Unit.WEBER),
					getUOM(Unit.SQUARE_METRE));
			break;

		case HENRY:
			// inductance (henry)
			uom = createQuotientUOM(UnitType.ELECTRIC_INDUCTANCE, Unit.HENRY, symbols.getString("henry.name"),
					symbols.getString("henry.symbol"), symbols.getString("henry.desc"), getUOM(Unit.WEBER),
					getUOM(Unit.AMPERE));
			break;

		case SIEMENS:
			// electrical conductance (siemens)
			uom = createQuotientUOM(UnitType.ELECTRIC_CONDUCTANCE, Unit.SIEMENS, symbols.getString("siemens.name"),
					symbols.getString("siemens.symbol"), symbols.getString("siemens.desc"), getUOM(Unit.AMPERE),
					getUOM(Unit.VOLT));
			break;

		case CELSIUS:
			// °C = °K - 273.15
			conversion = new Conversion(BigDecimal.ONE, getUOM(Unit.KELVIN), Quantity.createAmount("273.15"));
			uom = createScalarUOM(UnitType.TEMPERATURE, Unit.CELSIUS, symbols.getString("celsius.name"),
					symbols.getString("celsius.symbol"), symbols.getString("celsius.desc"));
			uom.setConversion(conversion);
			break;

		case LUMEN:
			// luminous flux (lumen)
			uom = createProductUOM(UnitType.LUMINOUS_FLUX, Unit.LUMEN, symbols.getString("lumen.name"),
					symbols.getString("lumen.symbol"), symbols.getString("lumen.desc"), getUOM(Unit.CANDELA),
					getUOM(Unit.STERADIAN));
			break;

		case LUX:
			// illuminance (lux)
			uom = createQuotientUOM(UnitType.ILLUMINANCE, Unit.LUX, symbols.getString("lux.name"),
					symbols.getString("lux.symbol"), symbols.getString("lux.desc"), getUOM(Unit.LUMEN),
					getUOM(Unit.SQUARE_METRE));
			break;

		case BECQUEREL:
			// radioactivity (becquerel). Same base symbol as Hertz
			uom = createScalarUOM(UnitType.RADIOACTIVITY, Unit.BECQUEREL, symbols.getString("becquerel.name"),
					symbols.getString("becquerel.symbol"), symbols.getString("becquerel.desc"));
			break;

		case GRAY:
			// gray (Gy)
			uom = createQuotientUOM(UnitType.RADIATION_DOSE_ABSORBED, Unit.GRAY, symbols.getString("gray.name"),
					symbols.getString("gray.symbol"), symbols.getString("gray.desc"), getUOM(Unit.JOULE),
					getUOM(Unit.KILOGRAM));
			break;

		case SIEVERT:
			// sievert (Sv)
			uom = createQuotientUOM(UnitType.RADIATION_DOSE_EFFECTIVE, Unit.SIEVERT, symbols.getString("sievert.name"),
					symbols.getString("sievert.symbol"), symbols.getString("sievert.desc"), getUOM(Unit.JOULE),
					getUOM(Unit.KILOGRAM));
			break;

		case KATAL:
			// katal (kat)
			uom = createQuotientUOM(UnitType.CATALYTIC_ACTIVITY, Unit.KATAL, symbols.getString("katal.name"),
					symbols.getString("katal.symbol"), symbols.getString("katal.desc"), getUOM(Unit.MOLE), getSecond());
			break;

		case ANGSTROM:
			// length
			UnitOfMeasure nm = this.getUOM(Prefix.NANO, getUOM(Unit.METRE));
			conversion = new Conversion(Quantity.createAmount("0.1"), nm);
			uom = createScalarUOM(UnitType.LENGTH, Unit.ANGSTROM, symbols.getString("angstrom.name"),
					symbols.getString("angstrom.symbol"), symbols.getString("angstrom.desc"));
			uom.setConversion(conversion);
			break;

		case BIT:
			// computer bit
			uom = createScalarUOM(UnitType.CS, Unit.BIT, symbols.getString("bit.name"), symbols.getString("bit.symbol"),
					symbols.getString("bit.desc"));
			break;

		case BYTE:
			// computer byte
			conversion = new Conversion(Quantity.createAmount("8"), getUOM(Unit.BIT));
			uom = createScalarUOM(UnitType.CS, Unit.BYTE, symbols.getString("byte.name"),
					symbols.getString("byte.symbol"), symbols.getString("byte.desc"));
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
					symbols.getString("rankine.symbol"), symbols.getString("rankine.desc"));

			// create bridge to SI
			factor = Quantity.divideAmounts("5", "9");
			conversion = new Conversion(factor, getUOM(Unit.KELVIN));
			uom.setBridge(conversion);

			break;

		case FAHRENHEIT:
			// Fahrenheit
			conversion = new Conversion(BigDecimal.ONE, getUOM(Unit.RANKINE), Quantity.createAmount("459.67"));
			uom = createScalarUOM(UnitType.TEMPERATURE, Unit.FAHRENHEIT, symbols.getString("fahrenheit.name"),
					symbols.getString("fahrenheit.symbol"), symbols.getString("fahrenheit.desc"));
			uom.setConversion(conversion);
			break;

		case POUND_MASS:
			// lb mass (base)
			uom = createScalarUOM(UnitType.MASS, Unit.POUND_MASS, symbols.getString("lbm.name"),
					symbols.getString("lbm.symbol"), symbols.getString("lbm.desc"));

			// create bridge to SI
			conversion = new Conversion(Quantity.createAmount("0.45359237"), getUOM(Unit.KILOGRAM));
			uom.setBridge(conversion);
			break;

		case OUNCE:
			// ounce
			conversion = new Conversion(Quantity.createAmount("0.0625"), getUOM(Unit.POUND_MASS));
			uom = createScalarUOM(UnitType.MASS, Unit.OUNCE, symbols.getString("ounce.name"),
					symbols.getString("ounce.symbol"), symbols.getString("ounce.desc"));
			uom.setConversion(conversion);
			break;

		case SLUG:
			// slug
			Quantity g = getQuantity(Constant.GRAVITY).convert(getUOM(Unit.FEET_PER_SECOND_SQUARED));
			conversion = new Conversion(g.getAmount(), getUOM(Unit.POUND_MASS));
			uom = createScalarUOM(UnitType.MASS, Unit.SLUG, symbols.getString("slug.name"),
					symbols.getString("slug.symbol"), symbols.getString("slug.desc"));
			uom.setConversion(conversion);
			break;

		case FOOT:
			// foot (foot is base conversion unit)
			uom = createScalarUOM(UnitType.LENGTH, Unit.FOOT, symbols.getString("foot.name"),
					symbols.getString("foot.symbol"), symbols.getString("foot.desc"));

			// bridge to SI
			conversion = new Conversion(Quantity.createAmount("0.3048"), getUOM(Unit.METRE));
			uom.setBridge(conversion);
			break;

		case INCH:
			// inch
			factor = Quantity.divideAmounts("1", "12");
			conversion = new Conversion(factor, getUOM(Unit.FOOT));
			uom = createScalarUOM(UnitType.LENGTH, Unit.INCH, symbols.getString("inch.name"),
					symbols.getString("inch.symbol"), symbols.getString("inch.desc"));
			uom.setConversion(conversion);
			break;

		case MIL:
			// inch
			conversion = new Conversion(Prefix.MILLI.getScalingFactor(), getUOM(Unit.INCH));
			uom = createScalarUOM(UnitType.LENGTH, Unit.MIL, symbols.getString("mil.name"),
					symbols.getString("mil.symbol"), symbols.getString("mil.desc"));
			uom.setConversion(conversion);
			break;

		case POINT:
			// point
			conversion = new Conversion(Quantity.divideAmounts("1", "72"), getUOM(Unit.INCH));
			uom = createScalarUOM(UnitType.LENGTH, Unit.POINT, symbols.getString("point.name"),
					symbols.getString("point.symbol"), symbols.getString("point.desc"));
			uom.setConversion(conversion);
			break;

		case YARD:
			// yard
			conversion = new Conversion(Quantity.createAmount("3"), getUOM(Unit.FOOT));
			uom = createScalarUOM(UnitType.LENGTH, Unit.YARD, symbols.getString("yard.name"),
					symbols.getString("yard.symbol"), symbols.getString("yard.desc"));
			uom.setConversion(conversion);
			break;

		case MILE:
			// mile
			conversion = new Conversion(Quantity.createAmount("5280"), getUOM(Unit.FOOT));
			uom = createScalarUOM(UnitType.LENGTH, Unit.MILE, symbols.getString("mile.name"),
					symbols.getString("mile.symbol"), symbols.getString("mile.desc"));
			uom.setConversion(conversion);
			break;

		case NAUTICAL_MILE:
			// nautical mile
			conversion = new Conversion(Quantity.createAmount("6080"), getUOM(Unit.FOOT));
			uom = createScalarUOM(UnitType.LENGTH, Unit.NAUTICAL_MILE, symbols.getString("NM.name"),
					symbols.getString("NM.symbol"), symbols.getString("NM.desc"));
			uom.setConversion(conversion);
			break;

		case FATHOM:
			// fathom
			conversion = new Conversion(Quantity.createAmount("6"), getUOM(Unit.FOOT));
			uom = createScalarUOM(UnitType.LENGTH, Unit.FATHOM, symbols.getString("fth.name"),
					symbols.getString("fth.symbol"), symbols.getString("fth.desc"));
			uom.setConversion(conversion);

			break;

		case PSI:
			// psi
			uom = createQuotientUOM(UnitType.PRESSURE, Unit.PSI, symbols.getString("psi.name"),
					symbols.getString("psi.symbol"), symbols.getString("psi.desc"), getUOM(Unit.POUND_FORCE),
					getUOM(Unit.SQUARE_INCH));
			break;

		case IN_HG:
			// inches of Mercury
			conversion = new Conversion(Quantity.createAmount("0.4911531047"), getUOM(Unit.PSI));
			uom = createScalarUOM(UnitType.PRESSURE, Unit.IN_HG, symbols.getString("inhg.name"),
					symbols.getString("inhg.symbol"), symbols.getString("inhg.desc"));
			uom.setConversion(conversion);
			break;

		case SQUARE_INCH:
			// square inch
			factor = Quantity.divideAmounts("1", "144");
			conversion = new Conversion(factor, getUOM(Unit.SQUARE_FOOT));
			uom = createPowerUOM(UnitType.AREA, Unit.SQUARE_INCH, symbols.getString("in2.name"),
					symbols.getString("in2.symbol"), symbols.getString("in2.desc"), getUOM(Unit.INCH), 2);
			uom.setConversion(conversion);
			break;

		case SQUARE_FOOT:
			// square foot
			uom = createPowerUOM(UnitType.AREA, Unit.SQUARE_FOOT, symbols.getString("ft2.name"),
					symbols.getString("ft2.symbol"), symbols.getString("ft2.desc"), getUOM(Unit.FOOT), 2);
			break;

		case SQUARE_YARD:
			// square yard
			uom = createPowerUOM(UnitType.AREA, Unit.SQUARE_YARD, symbols.getString("yd2.name"),
					symbols.getString("yd2.symbol"), symbols.getString("yd2.desc"), getUOM(Unit.YARD), 2);
			break;

		case ACRE:
			// acre
			conversion = new Conversion(Quantity.createAmount("43560"), getUOM(Unit.SQUARE_FOOT));
			uom = createScalarUOM(UnitType.AREA, Unit.ACRE, symbols.getString("acre.name"),
					symbols.getString("acre.symbol"), symbols.getString("acre.desc"));
			uom.setConversion(conversion);
			break;

		case CUBIC_INCH:
			// cubic inch
			factor = Quantity.divideAmounts("1", "1728");
			conversion = new Conversion(factor, getUOM(Unit.CUBIC_FOOT));
			uom = createPowerUOM(UnitType.VOLUME, Unit.CUBIC_INCH, symbols.getString("in3.name"),
					symbols.getString("in3.symbol"), symbols.getString("in3.desc"), getUOM(Unit.INCH), 3);
			uom.setConversion(conversion);
			break;

		case CUBIC_FOOT:
			// cubic feet
			uom = createPowerUOM(UnitType.VOLUME, Unit.CUBIC_FOOT, symbols.getString("ft3.name"),
					symbols.getString("ft3.symbol"), symbols.getString("ft3.desc"), getUOM(Unit.FOOT), 3);
			break;

		case CORD:
			// cord
			conversion = new Conversion(Quantity.createAmount("128"), getUOM(Unit.CUBIC_FOOT));
			uom = createScalarUOM(UnitType.VOLUME, Unit.CORD, symbols.getString("cord.name"),
					symbols.getString("cord.symbol"), symbols.getString("cord.desc"));
			uom.setConversion(conversion);
			break;

		case CUBIC_YARD:
			// cubic yard
			uom = createPowerUOM(UnitType.VOLUME, Unit.CUBIC_YARD, symbols.getString("yd3.name"),
					symbols.getString("yd3.symbol"), symbols.getString("yd3.desc"), getUOM(Unit.YARD), 3);
			break;

		case FEET_PER_SECOND:
			// feet/sec
			uom = createQuotientUOM(UnitType.VELOCITY, Unit.FEET_PER_SECOND, symbols.getString("fps.name"),
					symbols.getString("fps.symbol"), symbols.getString("fps.desc"), getUOM(Unit.FOOT), getSecond());
			break;

		case KNOT:
			// knot
			factor = Quantity.divideAmounts("6080", "3600");
			conversion = new Conversion(factor, getUOM(Unit.FEET_PER_SECOND));
			uom = createScalarUOM(UnitType.VELOCITY, Unit.KNOT, symbols.getString("knot.name"),
					symbols.getString("knot.symbol"), symbols.getString("knot.desc"));
			uom.setConversion(conversion);
			break;

		case FEET_PER_SECOND_SQUARED:
			// acceleration
			uom = createQuotientUOM(UnitType.ACCELERATION, Unit.FEET_PER_SECOND_SQUARED,
					symbols.getString("ftps2.name"), symbols.getString("ftps2.symbol"), symbols.getString("ftps2.desc"),
					getUOM(Unit.FOOT), getUOM(Unit.SQUARE_SECOND));
			break;

		case HP:
			// HP (mechanical)
			uom = createProductUOM(UnitType.POWER, Unit.HP, symbols.getString("hp.name"),
					symbols.getString("hp.symbol"), symbols.getString("hp.desc"), getUOM(Unit.POUND_FORCE),
					getUOM(Unit.FEET_PER_SECOND));
			uom.setScalingFactor(Quantity.createAmount("550"));
			break;

		case BTU:
			// BTU = 1055.056 Joules (778.169 ft-lbf)
			conversion = new Conversion(Quantity.createAmount("778.1692622659652"), getUOM(Unit.FOOT_POUND_FORCE));
			uom = createScalarUOM(UnitType.ENERGY, Unit.BTU, symbols.getString("btu.name"),
					symbols.getString("btu.symbol"), symbols.getString("btu.desc"));
			uom.setConversion(conversion);
			break;

		case FOOT_POUND_FORCE:
			// ft-lbf
			uom = createProductUOM(UnitType.ENERGY, Unit.FOOT_POUND_FORCE, symbols.getString("ft_lbf.name"),
					symbols.getString("ft_lbf.symbol"), symbols.getString("ft_lbf.desc"), getUOM(Unit.FOOT),
					getUOM(Unit.POUND_FORCE));
			break;

		case POUND_FORCE:
			// force F = m·A (lbf)
			uom = createProductUOM(UnitType.FORCE, Unit.POUND_FORCE, symbols.getString("lbf.name"),
					symbols.getString("lbf.symbol"), symbols.getString("lbf.desc"), getUOM(Unit.POUND_MASS),
					getUOM(Unit.FEET_PER_SECOND_SQUARED));

			// factor is acceleration of gravity
			Quantity gravity = getQuantity(Constant.GRAVITY).convert(getUOM(Unit.FEET_PER_SECOND_SQUARED));
			uom.setScalingFactor(gravity.getAmount());
			break;

		case GRAIN:
			// mass
			amount = Quantity.divideAmounts("1", "7000");
			conversion = new Conversion(amount, getUOM(Unit.POUND_MASS));
			uom = createScalarUOM(UnitType.MASS, Unit.GRAIN, symbols.getString("grain.name"),
					symbols.getString("grain.symbol"), symbols.getString("grain.desc"));
			uom.setConversion(conversion);
			break;

		case MILES_PER_HOUR:
			// velocity
			amount = Quantity.divideAmounts("5280", "3600");
			conversion = new Conversion(amount, getUOM(Unit.FEET_PER_SECOND));
			uom = createScalarUOM(UnitType.VELOCITY, Unit.MILES_PER_HOUR, symbols.getString("mph.name"),
					symbols.getString("mph.symbol"), symbols.getString("mph.desc"));
			uom.setConversion(conversion);
			break;

		case REV_PER_MIN:
			// rpm
			uom = createQuotientUOM(UnitType.FREQUENCY, Unit.REV_PER_MIN, symbols.getString("rpm.name"),
					symbols.getString("rpm.symbol"), symbols.getString("rpm.desc"), getOne(), getMinute());
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
					symbols.getString("us_gallon.symbol"), symbols.getString("us_gallon.desc"));
			uom.setConversion(conversion);
			break;

		case US_BARREL:
			// barrel
			conversion = new Conversion(Quantity.createAmount("42"), getUOM(Unit.US_GALLON));
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_BARREL, symbols.getString("us_bbl.name"),
					symbols.getString("us_bbl.symbol"), symbols.getString("us_bbl.desc"));
			uom.setConversion(conversion);
			break;

		case US_BUSHEL:
			// bushel
			conversion = new Conversion(Quantity.createAmount("2150.42058"), getUOM(Unit.CUBIC_INCH));
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_BUSHEL, symbols.getString("us_bu.name"),
					symbols.getString("us_bu.symbol"), symbols.getString("us_bu.desc"));
			uom.setConversion(conversion);
			break;

		case US_FLUID_OUNCE:
			// fluid ounce
			conversion = new Conversion(Quantity.createAmount("0.0078125"), getUOM(Unit.US_GALLON));
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_FLUID_OUNCE, symbols.getString("us_fl_oz.name"),
					symbols.getString("us_fl_oz.symbol"), symbols.getString("us_fl_oz.desc"));
			uom.setConversion(conversion);
			break;

		case US_CUP:
			// cup
			conversion = new Conversion(Quantity.createAmount("8"), getUOM(Unit.US_FLUID_OUNCE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_CUP, symbols.getString("us_cup.name"),
					symbols.getString("us_cup.symbol"), symbols.getString("us_cup.desc"));
			uom.setConversion(conversion);
			break;

		case US_PINT:
			// pint
			conversion = new Conversion(Quantity.createAmount("16"), getUOM(Unit.US_FLUID_OUNCE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_PINT, symbols.getString("us_pint.name"),
					symbols.getString("us_pint.symbol"), symbols.getString("us_pint.desc"));
			uom.setConversion(conversion);
			break;

		case US_QUART:
			// quart
			conversion = new Conversion(Quantity.createAmount("32"), getUOM(Unit.US_FLUID_OUNCE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_QUART, symbols.getString("us_quart.name"),
					symbols.getString("us_quart.symbol"), symbols.getString("us_quart.desc"));
			uom.setConversion(conversion);
			break;

		case US_TABLESPOON:
			// tablespoon
			conversion = new Conversion(Quantity.createAmount("0.5"), getUOM(Unit.US_FLUID_OUNCE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_TABLESPOON, symbols.getString("us_tbsp.name"),
					symbols.getString("us_tbsp.symbol"), symbols.getString("us_tbsp.desc"));
			uom.setConversion(conversion);
			break;

		case US_TEASPOON:
			// teaspoon
			factor = Quantity.divideAmounts("1", "6");
			conversion = new Conversion(factor, getUOM(Unit.US_FLUID_OUNCE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_TEASPOON, symbols.getString("us_tsp.name"),
					symbols.getString("us_tsp.symbol"), symbols.getString("us_tsp.desc"));
			uom.setConversion(conversion);
			break;

		case US_TON:
			// ton
			conversion = new Conversion(Quantity.createAmount("2000"), getUOM(Unit.POUND_MASS));
			uom = createScalarUOM(UnitType.MASS, Unit.US_TON, symbols.getString("us_ton.name"),
					symbols.getString("us_ton.symbol"), symbols.getString("us_ton.desc"));
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
					symbols.getString("br_gallon.symbol"), symbols.getString("br_gallon.desc"));
			uom.setConversion(conversion);
			break;

		case BR_BUSHEL:
			// bushel
			conversion = new Conversion(Quantity.createAmount("8"), getUOM(Unit.BR_GALLON));
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_BUSHEL, symbols.getString("br_bu.name"),
					symbols.getString("br_bu.symbol"), symbols.getString("br_bu.desc"));
			uom.setConversion(conversion);
			break;

		case BR_FLUID_OUNCE:
			// fluid ounce
			conversion = new Conversion(Quantity.createAmount("0.00625"), getUOM(Unit.BR_GALLON));
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_FLUID_OUNCE, symbols.getString("br_fl_oz.name"),
					symbols.getString("br_fl_oz.symbol"), symbols.getString("br_fl_oz.desc"));
			uom.setConversion(conversion);
			break;

		case BR_CUP:
			// cup
			conversion = new Conversion(Quantity.createAmount("8"), getUOM(Unit.BR_FLUID_OUNCE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_CUP, symbols.getString("br_cup.name"),
					symbols.getString("br_cup.symbol"), symbols.getString("br_cup.desc"));
			uom.setConversion(conversion);
			break;

		case BR_PINT:
			// pint
			conversion = new Conversion(Quantity.createAmount("20"), getUOM(Unit.BR_FLUID_OUNCE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_PINT, symbols.getString("br_pint.name"),
					symbols.getString("br_pint.symbol"), symbols.getString("br_pint.desc"));
			uom.setConversion(conversion);
			break;

		case BR_QUART:
			// quart
			conversion = new Conversion(Quantity.createAmount("40"), getUOM(Unit.BR_FLUID_OUNCE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_QUART, symbols.getString("br_quart.name"),
					symbols.getString("br_quart.symbol"), symbols.getString("br_quart.desc"));
			uom.setConversion(conversion);
			break;

		case BR_TABLESPOON:
			// tablespoon
			conversion = new Conversion(Quantity.createAmount("0.625"), getUOM(Unit.BR_FLUID_OUNCE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_TABLESPOON, symbols.getString("br_tbsp.name"),
					symbols.getString("br_tbsp.symbol"), symbols.getString("br_tbsp.desc"));
			uom.setConversion(conversion);
			break;

		case BR_TEASPOON:
			// teaspoon
			factor = Quantity.divideAmounts("5", "24");
			conversion = new Conversion(factor, getUOM(Unit.BR_FLUID_OUNCE));
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_TEASPOON, symbols.getString("br_tsp.name"),
					symbols.getString("br_tsp.symbol"), symbols.getString("br_tsp.desc"));
			uom.setConversion(conversion);
			break;

		case BR_TON:
			// ton
			conversion = new Conversion(Quantity.createAmount("2240"), getUOM(Unit.POUND_MASS));
			uom = createScalarUOM(UnitType.MASS, Unit.BR_TON, symbols.getString("br_ton.name"),
					symbols.getString("br_ton.symbol"), symbols.getString("br_ton.desc"));
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

		if (uom == null) {
			uom = createUOM(enumeration);
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
		List<UnitOfMeasure> list = new ArrayList<UnitOfMeasure>(units);

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
	 * @throws Exception
	 *             Exception
	 */
	public synchronized void unregisterUnit(UnitOfMeasure uom) throws Exception {
		if (uom == null) {
			return;
		}

		if (uom.getEnumeration() != null) {
			unitRegistry.remove(uom.getEnumeration());
		}

		// remove by symbol and base symbol
		symbolRegistry.remove(uom.getSymbol());
		symbolRegistry.remove(uom.getBaseSymbol());
	}

	ResourceBundle getSymbols() {
		return symbols;
	}

	private void cacheUnit(UnitOfMeasure uom) throws Exception {
		String key = uom.getSymbol();

		// get first by symbol
		UnitOfMeasure current = symbolRegistry.get(key);

		if (current != null) {
			// already cached
			return;
		}

		symbolRegistry.put(key, uom);

		// next by unit enumeration
		Unit id = uom.getEnumeration();

		if (id != null) {
			current = unitRegistry.get(id);

			if (current != null) {
				String msg = MessageFormat.format(getMessage("already.registered"), uom.toString(), id.toString(),
						current.toString());
				throw new Exception(msg);
			}

			unitRegistry.put(id, uom);
		}

		// finally by base symbol too
		key = uom.getBaseSymbol();

		if (symbolRegistry.get(key) == null) {
			symbolRegistry.put(key, uom);
		}
	}

	private UnitOfMeasure createUOM(UnitType type, Unit id, String name, String symbol, String description)
			throws Exception {

		if (symbol == null || symbol.length() == 0) {
			throw new Exception(MeasurementSystem.getMessage("symbol.cannot.be.null"));
		}

		if (type == null) {
			throw new Exception(getMessage("unit.type.cannot.be.null"));
		}

		UnitOfMeasure uom = null;

		if (symbolRegistry.containsKey(symbol)) {
			uom = symbolRegistry.get(symbol);
		}

		else if (id != null && unitRegistry.containsKey(id)) {
			uom = unitRegistry.get(id);
		} else {
			// create a new one
			uom = new UnitOfMeasure(type, name, symbol, description);
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
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure createScalarUOM(UnitType type, Unit id, String name, String symbol, String description)
			throws Exception {

		UnitOfMeasure uom = createUOM(type, id, name, symbol, description);
		uom.setEnumeration(id);
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
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure createScalarUOM(UnitType type, String name, String symbol, String description)
			throws Exception {
		return createScalarUOM(type, null, name, symbol, description);
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
	 * @param dividend
	 *            {@link UnitOfMeasure}
	 * @param divisor
	 *            {@link UnitOfMeasure}
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure createQuotientUOM(UnitType type, Unit id, String name, String symbol, String description,
			UnitOfMeasure dividend, UnitOfMeasure divisor) throws Exception {

		UnitOfMeasure uom = createUOM(type, id, name, symbol, description);
		uom.setQuotientUnits(dividend, divisor);
		uom.setEnumeration(id);
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
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure createQuotientUOM(UnitType type, String name, String symbol, String description,
			UnitOfMeasure dividend, UnitOfMeasure divisor) throws Exception {
		return this.createQuotientUOM(type, null, name, symbol, description, dividend, divisor);
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
	 * @param multiplier
	 *            {@link UnitOfMeasure} multiplier
	 * @param multiplicand
	 *            {@link UnitOfMeasure} multiplicand
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure createProductUOM(UnitType type, Unit id, String name, String symbol, String description,
			UnitOfMeasure multiplier, UnitOfMeasure multiplicand) throws Exception {

		UnitOfMeasure uom = createUOM(type, id, name, symbol, description);
		uom.setProductUnits(multiplier, multiplicand);
		uom.setEnumeration(id);
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
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure createProductUOM(UnitType type, String name, String symbol, String description,
			UnitOfMeasure multiplier, UnitOfMeasure multiplicand) throws Exception {
		return createProductUOM(type, null, name, symbol, description, multiplier, multiplicand);
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
	 * @param base
	 *            {@link UnitOfMeasure}
	 * @param power
	 *            Exponent
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure createPowerUOM(UnitType type, Unit id, String name, String symbol, String description,
			UnitOfMeasure base, int power) throws Exception {

		UnitOfMeasure uom = createUOM(type, id, name, symbol, description);
		uom.setPowerUnits(base, power);
		uom.setEnumeration(id);
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
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure createPowerUOM(UnitType type, String name, String symbol, String description,
			UnitOfMeasure base, int power) throws Exception {
		return createPowerUOM(type, null, name, symbol, description, base, power);
	}

	/**
	 * Create or fetch a unit of measure linearly scaled by the {@link Prefix}
	 * against the target unit of measure.
	 * 
	 * @param prefix
	 *            {@link Prefix} Scaling prefix with the scaling factor, e.g.
	 *            1000
	 * @param targetUOM
	 *            abscissa {@link UnitOfMeasure}
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure getUOM(Prefix prefix, UnitOfMeasure targetUOM) throws Exception {
		String symbol = prefix.getSymbol() + targetUOM.getSymbol();

		UnitOfMeasure scaled = getUOM(symbol);

		// if not found, create it
		if (scaled == null) {
			String name = prefix.getPrefixName() + targetUOM.getName();
			String description = prefix.getScalingFactor() + " " + targetUOM.getName();
			Conversion conversion = new Conversion(prefix.getScalingFactor(), targetUOM);
			scaled = createScalarUOM(targetUOM.getUnitType(), null, name, symbol, description);
			scaled.setConversion(conversion);
		}
		return scaled;
	}

	/**
	 * Create or fetch a unit of measure linearly scaled by the {@link Prefix}
	 * against the target unit of measure.
	 * 
	 * @param prefix
	 *            {@link Prefix} Scaling prefix with the scaling factor, e.g.
	 *            1000
	 * @param unit
	 *            {@link Unit}
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure getUOM(Prefix prefix, Unit unit) throws Exception {
		return getUOM(prefix, MeasurementSystem.getSystem().getUOM(unit));
	}
}
