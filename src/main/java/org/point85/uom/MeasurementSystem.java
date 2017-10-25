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
 * <li>7 SI fundamental units of measure</li>
 * <li>20 SI units derived from these fundamental units</li>
 * <li>other units in the International Customary, US and British Imperial
 * systems</li>
 * <li>any number of custom units of measure</li>
 * </ul>
 *
 */

public class MeasurementSystem {
	// name of resource bundle with translatable strings for exception messages
	private static final String MESSAGE_BUNDLE_NAME = "Message";

	// resource bundle for exception messages
	private static ResourceBundle messages;

	// standard unified system
	private static MeasurementSystem unifiedSystem = new MeasurementSystem();

	// name of resource bundle with translatable strings for UOMs (e.g. time)
	private static final String UNIT_BUNDLE_NAME = "Unit";

	// unit resource bundle (e.g. time units)
	private static ResourceBundle units;

	// UOM cache manager
	private CacheManager cacheManager = new CacheManager();

	protected MeasurementSystem() {
		// common unit strings
		units = ResourceBundle.getBundle(UNIT_BUNDLE_NAME, Locale.getDefault());
		messages = ResourceBundle.getBundle(MESSAGE_BUNDLE_NAME, Locale.getDefault());
	}

	// get a particular message by its key
	static String getMessage(String key) {
		return messages.getString(key);
	}

	// get a particular unit string by its key
	static String getUnitString(String key) {
		return units.getString(key);
	}

	/**
	 * Get the unified system of units of measure for International Customary,
	 * SI, US, British Imperial as well as custom systems
	 * 
	 * @return {@link MeasurementSystem}
	 */
	public static MeasurementSystem getSystem() {
		return unifiedSystem;
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

		// currency
		uom = createFinancialUnit(enumeration);

		return uom;
	}

	/**
	 * Get the quantity defined as a contant value
	 * 
	 * @param constant
	 *            {@link Constant}
	 * @return {@link Quantity}
	 * @throws Exception
	 *             Exception
	 */
	public final Quantity getQuantity(Constant constant) throws Exception {
		Quantity named = null;

		switch (constant) {
		case LIGHT_VELOCITY:
			named = new Quantity(299792458d, getUOM(Unit.METRE_PER_SEC));
			named.setName(units.getString("light.name"));
			named.setSymbol(units.getString("light.symbol"));
			named.setDescription(units.getString("light.desc"));
			break;

		case LIGHT_YEAR:
			Quantity year = new Quantity(1.0, getUOM(Unit.JULIAN_YEAR));
			named = getQuantity(Constant.LIGHT_VELOCITY).multiply(year);
			named.setName(units.getString("ly.name"));
			named.setSymbol(units.getString("ly.symbol"));
			named.setDescription(units.getString("ly.desc"));
			break;

		case GRAVITY:
			named = new Quantity(9.80665, getUOM(Unit.METRE_PER_SEC_SQUARED));
			named.setName(units.getString("gravity.name"));
			named.setSymbol(units.getString("gravity.symbol"));
			named.setDescription(units.getString("gravity.desc"));
			break;

		case PLANCK_CONSTANT:
			UnitOfMeasure js = createProductUOM(getUOM(Unit.JOULE), getSecond());
			named = new Quantity(6.62607015E-34, js);
			named.setName(units.getString("planck.name"));
			named.setSymbol(units.getString("planck.symbol"));
			named.setDescription(units.getString("planck.desc"));
			break;

		case BOLTZMANN_CONSTANT:
			UnitOfMeasure jk = createQuotientUOM(getUOM(Unit.JOULE), getUOM(Unit.KELVIN));
			named = new Quantity(1.380649E-23, jk);
			named.setName(units.getString("boltzmann.name"));
			named.setSymbol(units.getString("boltzmann.symbol"));
			named.setDescription(units.getString("boltzmann.desc"));
			break;

		case AVAGADRO_CONSTANT:
			// NA
			named = new Quantity(6.02214076E+23, getOne());
			named.setName(units.getString("avo.name"));
			named.setSymbol(units.getString("avo.symbol"));
			named.setDescription(units.getString("avo.desc"));
			break;

		case GAS_CONSTANT:
			// R
			named = getQuantity(Constant.BOLTZMANN_CONSTANT).multiply(getQuantity(Constant.AVAGADRO_CONSTANT));
			named.setName(units.getString("gas.name"));
			named.setSymbol(units.getString("gas.symbol"));
			named.setDescription(units.getString("gas.desc"));
			break;

		case ELEMENTARY_CHARGE:
			// e
			named = new Quantity(1.602176634E-19, getUOM(Unit.COULOMB));
			named.setName(units.getString("e.name"));
			named.setSymbol(units.getString("e.symbol"));
			named.setDescription(units.getString("e.desc"));
			break;

		case FARADAY_CONSTANT:
			// F = e.NA
			Quantity qe = getQuantity(Constant.ELEMENTARY_CHARGE);
			named = qe.multiply(getQuantity(Constant.AVAGADRO_CONSTANT));
			named.setName(units.getString("faraday.name"));
			named.setSymbol(units.getString("faraday.symbol"));
			named.setDescription(units.getString("faraday.desc"));
			break;

		case ELECTRIC_PERMITTIVITY:
			// epsilon0 = 1/(mu0*c^2)
			Quantity vc = getQuantity(Constant.LIGHT_VELOCITY);
			named = getQuantity(Constant.MAGNETIC_PERMEABILITY).multiply(vc).multiply(vc).invert();
			named.setName(units.getString("eps0.name"));
			named.setSymbol(units.getString("eps0.symbol"));
			named.setDescription(units.getString("eps0.desc"));
			break;

		case MAGNETIC_PERMEABILITY:
			// mu0
			UnitOfMeasure hm = createQuotientUOM(getUOM(Unit.HENRY), getUOM(Unit.METRE));
			double fourPi = 4.0 * Math.PI * 1.0E-07;
			named = new Quantity(fourPi, hm);
			named.setName(units.getString("mu0.name"));
			named.setSymbol(units.getString("mu0.symbol"));
			named.setDescription(units.getString("mu0.desc"));
			break;

		case ELECTRON_MASS:
			// me
			named = new Quantity(9.1093835611E-28, getUOM(Unit.GRAM));
			named.setName(units.getString("me.name"));
			named.setSymbol(units.getString("me.symbol"));
			named.setDescription(units.getString("me.desc"));
			break;

		case PROTON_MASS:
			// mp
			named = new Quantity(1.67262189821E-24, getUOM(Unit.GRAM));
			named.setName(units.getString("mp.name"));
			named.setSymbol(units.getString("mp.symbol"));
			named.setDescription(units.getString("mp.desc"));
			break;

		case STEFAN_BOLTZMANN:
			UnitOfMeasure k4 = createPowerUOM(getUOM(Unit.KELVIN), 4);
			UnitOfMeasure sb = createQuotientUOM(getUOM(Unit.WATTS_PER_SQ_METRE), k4);
			named = new Quantity(5.67036713E-08, sb);
			named.setName(units.getString("sb.name"));
			named.setSymbol(units.getString("sb.symbol"));
			named.setDescription(units.getString("sb.desc"));
			break;

		case HUBBLE_CONSTANT:
			UnitOfMeasure kps = getUOM(Prefix.KILO, getUOM(Unit.METRE_PER_SEC));
			UnitOfMeasure mpc = getUOM(Prefix.MEGA, getUOM(Unit.PARSEC));
			UnitOfMeasure hubble = createQuotientUOM(kps, mpc);
			named = new Quantity(71.9, hubble);
			named.setName(units.getString("hubble.name"));
			named.setSymbol(units.getString("hubble.symbol"));
			named.setDescription(units.getString("hubble.desc"));
			break;
			
		case CAESIUM_FREQUENCY:
			named = new Quantity(9192631770d, getUOM(Unit.HERTZ));
			named.setName(units.getString("caesium.name"));
			named.setSymbol(units.getString("caesium.symbol"));
			named.setDescription(units.getString("caesium.desc"));
			break;
			
		case LUMINOUS_EFFICACY:
			UnitOfMeasure kcd = createQuotientUOM(getUOM(Unit.LUMEN), getUOM(Unit.WATT));
			named = new Quantity(683d, kcd);
			named.setName(units.getString("kcd.name"));
			named.setSymbol(units.getString("kcd.symbol"));
			named.setDescription(units.getString("kcd.desc"));
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

		switch (unit) {

		case ONE:
			// unity
			uom = createScalarUOM(UnitType.UNITY, Unit.ONE, units.getString("one.name"), units.getString("one.symbol"),
					units.getString("one.desc"));
			break;

		case PERCENT:
			uom = createScalarUOM(UnitType.UNITY, Unit.PERCENT, units.getString("percent.name"),
					units.getString("percent.symbol"), units.getString("percent.desc"));
			uom.setConversion(0.01, getOne());
			break;

		case SECOND:
			// second
			uom = createScalarUOM(UnitType.TIME, Unit.SECOND, units.getString("sec.name"),
					units.getString("sec.symbol"), units.getString("sec.desc"));
			break;

		case MINUTE:
			// minute
			uom = createScalarUOM(UnitType.TIME, Unit.MINUTE, units.getString("min.name"),
					units.getString("min.symbol"), units.getString("min.desc"));
			uom.setConversion(60d, getUOM(Unit.SECOND));
			break;

		case HOUR:
			// hour
			uom = createScalarUOM(UnitType.TIME, Unit.HOUR, units.getString("hr.name"), units.getString("hr.symbol"),
					units.getString("hr.desc"));
			uom.setConversion(3600d, getUOM(Unit.SECOND));
			break;

		case DAY:
			// day
			uom = createScalarUOM(UnitType.TIME, Unit.DAY, units.getString("day.name"), units.getString("day.symbol"),
					units.getString("day.desc"));
			uom.setConversion(86400d, getUOM(Unit.SECOND));
			break;

		case WEEK:
			// week
			uom = createScalarUOM(UnitType.TIME, Unit.WEEK, units.getString("week.name"),
					units.getString("week.symbol"), units.getString("week.desc"));
			uom.setConversion(604800d, getUOM(Unit.SECOND));
			break;

		case JULIAN_YEAR:
			// Julian year
			uom = createScalarUOM(UnitType.TIME, Unit.JULIAN_YEAR, units.getString("jyear.name"),
					units.getString("jyear.symbol"), units.getString("jyear.desc"));
			uom.setConversion(3.1557600E+07, getUOM(Unit.SECOND));
			break;

		case SQUARE_SECOND:
			// square second
			uom = createPowerUOM(UnitType.TIME_SQUARED, Unit.SQUARE_SECOND, units.getString("s2.name"),
					units.getString("s2.symbol"), units.getString("s2.desc"), getUOM(Unit.SECOND), 2);
			break;

		case MOLE:
			// substance amount
			uom = createScalarUOM(UnitType.SUBSTANCE_AMOUNT, Unit.MOLE, units.getString("mole.name"),
					units.getString("mole.symbol"), units.getString("mole.desc"));
			break;

		case EQUIVALENT:
			// substance amount
			uom = createScalarUOM(UnitType.SUBSTANCE_AMOUNT, Unit.EQUIVALENT, units.getString("equivalent.name"),
					units.getString("equivalent.symbol"), units.getString("equivalent.desc"));
			break;

		case DECIBEL:
			// decibel
			uom = createScalarUOM(UnitType.INTENSITY, Unit.DECIBEL, units.getString("db.name"),
					units.getString("db.symbol"), units.getString("db.desc"));
			break;

		case RADIAN:
			// plane angle radian (rad)
			uom = createScalarUOM(UnitType.PLANE_ANGLE, Unit.RADIAN, units.getString("radian.name"),
					units.getString("radian.symbol"), units.getString("radian.desc"));
			uom.setConversion(getOne());
			break;

		case STERADIAN:
			// solid angle steradian (sr)
			uom = createScalarUOM(UnitType.SOLID_ANGLE, Unit.STERADIAN, units.getString("steradian.name"),
					units.getString("steradian.symbol"), units.getString("steradian.desc"));
			uom.setConversion(getOne());
			break;

		case DEGREE:
			// degree of arc
			uom = createScalarUOM(UnitType.PLANE_ANGLE, Unit.DEGREE, units.getString("degree.name"),
					units.getString("degree.symbol"), units.getString("degree.desc"));
			uom.setConversion(Math.PI / 180d, getUOM(Unit.RADIAN));
			break;

		case ARC_SECOND:
			// degree of arc
			uom = createScalarUOM(UnitType.PLANE_ANGLE, Unit.ARC_SECOND, units.getString("arcsec.name"),
					units.getString("arcsec.symbol"), units.getString("arcsec.desc"));
			uom.setConversion(Math.PI / 648000d, getUOM(Unit.RADIAN));
			break;

		case METRE:
			// fundamental length
			uom = createScalarUOM(UnitType.LENGTH, Unit.METRE, units.getString("m.name"), units.getString("m.symbol"),
					units.getString("m.desc"));
			break;

		case DIOPTER:
			// per metre
			uom = createQuotientUOM(UnitType.RECIPROCAL_LENGTH, Unit.DIOPTER, units.getString("diopter.name"),
					units.getString("diopter.symbol"), units.getString("diopter.desc"), getOne(), getUOM(Unit.METRE));
			break;

		case KILOGRAM:
			// fundamental mass
			uom = createScalarUOM(UnitType.MASS, Unit.KILOGRAM, units.getString("kg.name"),
					units.getString("kg.symbol"), units.getString("kg.desc"));
			break;

		case TONNE:
			// mass
			uom = createScalarUOM(UnitType.MASS, Unit.TONNE, units.getString("tonne.name"),
					units.getString("tonne.symbol"), units.getString("tonne.desc"));
			uom.setConversion(Prefix.KILO.getFactor(), getUOM(Unit.KILOGRAM));
			break;

		case KELVIN:
			// fundamental temperature
			uom = createScalarUOM(UnitType.TEMPERATURE, Unit.KELVIN, units.getString("kelvin.name"),
					units.getString("kelvin.symbol"), units.getString("kelvin.desc"));
			break;

		case AMPERE:
			// electric current
			uom = createScalarUOM(UnitType.ELECTRIC_CURRENT, Unit.AMPERE, units.getString("amp.name"),
					units.getString("amp.symbol"), units.getString("amp.desc"));
			break;

		case CANDELA:
			// luminosity
			uom = createScalarUOM(UnitType.LUMINOSITY, Unit.CANDELA, units.getString("cd.name"),
					units.getString("cd.symbol"), units.getString("cd.desc"));
			break;

		case PH:
			// molar concentration
			uom = createScalarUOM(UnitType.MOLAR_CONCENTRATION, Unit.PH, units.getString("ph.name"),
					units.getString("ph.symbol"), units.getString("ph.desc"));
			break;

		case GRAM: // gram
			uom = createScalarUOM(UnitType.MASS, Unit.GRAM, units.getString("gram.name"),
					units.getString("gram.symbol"), units.getString("gram.desc"));
			uom.setConversion(Prefix.MILLI.getFactor(), getUOM(Unit.KILOGRAM));
			break;

		case CARAT:
			// carat
			uom = createScalarUOM(UnitType.MASS, Unit.CARAT, units.getString("carat.name"),
					units.getString("carat.symbol"), units.getString("carat.desc"));
			uom.setConversion(0.2, getUOM(Unit.GRAM));
			break;

		case SQUARE_METRE:
			// square metre
			uom = createPowerUOM(UnitType.AREA, Unit.SQUARE_METRE, units.getString("m2.name"),
					units.getString("m2.symbol"), units.getString("m2.desc"), getUOM(Unit.METRE), 2);
			break;

		case HECTARE:
			// hectare
			uom = createScalarUOM(UnitType.AREA, Unit.HECTARE, units.getString("hectare.name"),
					units.getString("hectare.symbol"), units.getString("hectare.desc"));
			uom.setConversion(10000d, getUOM(Unit.SQUARE_METRE));
			break;

		case METRE_PER_SEC:
			// velocity
			uom = createQuotientUOM(UnitType.VELOCITY, Unit.METRE_PER_SEC, units.getString("mps.name"),
					units.getString("mps.symbol"), units.getString("mps.desc"), getUOM(Unit.METRE), getSecond());
			break;

		case METRE_PER_SEC_SQUARED:
			// acceleration
			uom = createQuotientUOM(UnitType.ACCELERATION, Unit.METRE_PER_SEC_SQUARED, units.getString("mps2.name"),
					units.getString("mps2.symbol"), units.getString("mps2.desc"), getUOM(Unit.METRE),
					getUOM(Unit.SQUARE_SECOND));
			break;

		case CUBIC_METRE:
			// cubic metre
			uom = createPowerUOM(UnitType.VOLUME, Unit.CUBIC_METRE, units.getString("m3.name"),
					units.getString("m3.symbol"), units.getString("m3.desc"), getUOM(Unit.METRE), 3);
			break;

		case LITRE:
			// litre
			uom = createScalarUOM(UnitType.VOLUME, Unit.LITRE, units.getString("litre.name"),
					units.getString("litre.symbol"), units.getString("litre.desc"));
			uom.setConversion(Prefix.MILLI.getFactor(), getUOM(Unit.CUBIC_METRE));
			break;

		case CUBIC_METRE_PER_SEC:
			// flow (volume)
			uom = createQuotientUOM(UnitType.VOLUMETRIC_FLOW, Unit.CUBIC_METRE_PER_SEC,
					units.getString("m3PerSec.name"), units.getString("m3PerSec.symbol"),
					units.getString("m3PerSec.desc"), getUOM(Unit.CUBIC_METRE), getSecond());
			break;

		case KILOGRAM_PER_SEC:
			// flow (mass)
			uom = createQuotientUOM(UnitType.MASS_FLOW, Unit.KILOGRAM_PER_SEC, units.getString("kgPerSec.name"),
					units.getString("kgPerSec.symbol"), units.getString("kgPerSec.desc"), getUOM(Unit.KILOGRAM),
					getSecond());
			break;

		case KILOGRAM_PER_CU_METRE:
			// kg/m^3
			uom = createQuotientUOM(UnitType.DENSITY, Unit.KILOGRAM_PER_CU_METRE, units.getString("kg_m3.name"),
					units.getString("kg_m3.symbol"), units.getString("kg_m3.desc"), getUOM(Unit.KILOGRAM),
					getUOM(Unit.CUBIC_METRE));
			break;

		case PASCAL_SECOND:
			// dynamic viscosity
			uom = createProductUOM(UnitType.DYNAMIC_VISCOSITY, Unit.PASCAL_SECOND, units.getString("pascal_sec.name"),
					units.getString("pascal_sec.symbol"), units.getString("pascal_sec.desc"), getUOM(Unit.PASCAL),
					getSecond());
			break;

		case SQUARE_METRE_PER_SEC:
			// kinematic viscosity
			uom = createQuotientUOM(UnitType.KINEMATIC_VISCOSITY, Unit.SQUARE_METRE_PER_SEC,
					units.getString("m2PerSec.name"), units.getString("m2PerSec.symbol"),
					units.getString("m2PerSec.desc"), getUOM(Unit.SQUARE_METRE), getSecond());
			break;

		case CALORIE:
			// thermodynamic calorie
			uom = createScalarUOM(UnitType.ENERGY, Unit.CALORIE, units.getString("calorie.name"),
					units.getString("calorie.symbol"), units.getString("calorie.desc"));
			uom.setConversion(4.184, getUOM(Unit.JOULE));
			break;

		case NEWTON:
			// force F = m·A (newton)
			uom = createProductUOM(UnitType.FORCE, Unit.NEWTON, units.getString("newton.name"),
					units.getString("newton.symbol"), units.getString("newton.desc"), getUOM(Unit.KILOGRAM),
					getUOM(Unit.METRE_PER_SEC_SQUARED));
			break;

		case NEWTON_METRE:
			// newton-metre
			uom = createProductUOM(UnitType.ENERGY, Unit.NEWTON_METRE, units.getString("n_m.name"),
					units.getString("n_m.symbol"), units.getString("n_m.desc"), getUOM(Unit.NEWTON),
					getUOM(Unit.METRE));
			break;

		case JOULE:
			// energy (joule)
			uom = createProductUOM(UnitType.ENERGY, Unit.JOULE, units.getString("joule.name"),
					units.getString("joule.symbol"), units.getString("joule.desc"), getUOM(Unit.NEWTON),
					getUOM(Unit.METRE));
			break;

		case ELECTRON_VOLT:
			// ev
			Quantity e = this.getQuantity(Constant.ELEMENTARY_CHARGE);
			uom = createProductUOM(UnitType.ENERGY, Unit.ELECTRON_VOLT, units.getString("ev.name"),
					units.getString("ev.symbol"), units.getString("ev.desc"), e.getUOM(), getUOM(Unit.VOLT));
			uom.setScalingFactor(e.getAmount());
			break;

		case WATT_HOUR:
			// watt-hour
			uom = createProductUOM(UnitType.ENERGY, Unit.WATT_HOUR, units.getString("wh.name"),
					units.getString("wh.symbol"), units.getString("wh.desc"), getUOM(Unit.WATT), getHour());
			break;

		case WATT:
			// power (watt)
			uom = createQuotientUOM(UnitType.POWER, Unit.WATT, units.getString("watt.name"),
					units.getString("watt.symbol"), units.getString("watt.desc"), getUOM(Unit.JOULE), getSecond());
			break;

		case HERTZ:
			// frequency (hertz)
			uom = createQuotientUOM(UnitType.FREQUENCY, Unit.HERTZ, units.getString("hertz.name"),
					units.getString("hertz.symbol"), units.getString("hertz.desc"), getOne(), getSecond());
			break;

		case RAD_PER_SEC:
			// angular frequency
			uom = createQuotientUOM(UnitType.FREQUENCY, Unit.RAD_PER_SEC, units.getString("radpers.name"),
					units.getString("radpers.symbol"), units.getString("radpers.desc"), getUOM(Unit.RADIAN),
					getSecond());
			uom.setConversion(1.0 / (2.0d * Math.PI), getUOM(Unit.HERTZ));
			break;

		case PASCAL:
			// pressure
			uom = createQuotientUOM(UnitType.PRESSURE, Unit.PASCAL, units.getString("pascal.name"),
					units.getString("pascal.symbol"), units.getString("pascal.desc"), getUOM(Unit.NEWTON),
					getUOM(Unit.SQUARE_METRE));
			break;

		case ATMOSPHERE:
			// pressure
			uom = createScalarUOM(UnitType.PRESSURE, Unit.ATMOSPHERE, units.getString("atm.name"),
					units.getString("atm.symbol"), units.getString("atm.desc"));
			uom.setConversion(101325d, getUOM(Unit.PASCAL));
			break;

		case BAR:
			// pressure
			uom = createScalarUOM(UnitType.PRESSURE, Unit.BAR, units.getString("bar.name"),
					units.getString("bar.symbol"), units.getString("bar.desc"));
			uom.setConversion(1.0, getUOM(Unit.PASCAL), 1.0E+05);
			break;

		case COULOMB:
			// charge (coulomb)
			uom = createProductUOM(UnitType.ELECTRIC_CHARGE, Unit.COULOMB, units.getString("coulomb.name"),
					units.getString("coulomb.symbol"), units.getString("coulomb.desc"), getUOM(Unit.AMPERE),
					getSecond());
			break;

		case VOLT:
			// voltage (volt)
			uom = createQuotientUOM(UnitType.ELECTROMOTIVE_FORCE, Unit.VOLT, units.getString("volt.name"),
					units.getString("volt.symbol"), units.getString("volt.desc"), getUOM(Unit.WATT),
					getUOM(Unit.AMPERE));
			break;

		case OHM:
			// resistance (ohm)
			uom = createQuotientUOM(UnitType.ELECTRIC_RESISTANCE, Unit.OHM, units.getString("ohm.name"),
					units.getString("ohm.symbol"), units.getString("ohm.desc"), getUOM(Unit.VOLT), getUOM(Unit.AMPERE));
			break;

		case FARAD:
			// capacitance (farad)
			uom = createQuotientUOM(UnitType.ELECTRIC_CAPACITANCE, Unit.FARAD, units.getString("farad.name"),
					units.getString("farad.symbol"), units.getString("farad.desc"), getUOM(Unit.COULOMB),
					getUOM(Unit.VOLT));
			break;

		case FARAD_PER_METRE:
			// electric permittivity (farad/metre)
			uom = createQuotientUOM(UnitType.ELECTRIC_PERMITTIVITY, Unit.FARAD_PER_METRE, units.getString("fperm.name"),
					units.getString("fperm.symbol"), units.getString("fperm.desc"), getUOM(Unit.FARAD),
					getUOM(Unit.METRE));
			break;

		case AMPERE_PER_METRE:
			// electric field strength(ampere/metre)
			uom = createQuotientUOM(UnitType.ELECTRIC_FIELD_STRENGTH, Unit.AMPERE_PER_METRE,
					units.getString("aperm.name"), units.getString("aperm.symbol"), units.getString("aperm.desc"),
					getUOM(Unit.AMPERE), getUOM(Unit.METRE));
			break;

		case WEBER:
			// magnetic flux (weber)
			uom = createProductUOM(UnitType.MAGNETIC_FLUX, Unit.WEBER, units.getString("weber.name"),
					units.getString("weber.symbol"), units.getString("weber.desc"), getUOM(Unit.VOLT), getSecond());
			break;

		case TESLA:
			// magnetic flux density (tesla)
			uom = createQuotientUOM(UnitType.MAGNETIC_FLUX_DENSITY, Unit.TESLA, units.getString("tesla.name"),
					units.getString("tesla.symbol"), units.getString("tesla.desc"), getUOM(Unit.WEBER),
					getUOM(Unit.SQUARE_METRE));
			break;

		case HENRY:
			// inductance (henry)
			uom = createQuotientUOM(UnitType.ELECTRIC_INDUCTANCE, Unit.HENRY, units.getString("henry.name"),
					units.getString("henry.symbol"), units.getString("henry.desc"), getUOM(Unit.WEBER),
					getUOM(Unit.AMPERE));
			break;

		case SIEMENS:
			// electrical conductance (siemens)
			uom = createQuotientUOM(UnitType.ELECTRIC_CONDUCTANCE, Unit.SIEMENS, units.getString("siemens.name"),
					units.getString("siemens.symbol"), units.getString("siemens.desc"), getUOM(Unit.AMPERE),
					getUOM(Unit.VOLT));
			break;

		case CELSIUS:
			// °C = °K - 273.15
			uom = createScalarUOM(UnitType.TEMPERATURE, Unit.CELSIUS, units.getString("celsius.name"),
					units.getString("celsius.symbol"), units.getString("celsius.desc"));
			uom.setConversion(1.0, getUOM(Unit.KELVIN), 273.15);
			break;

		case LUMEN:
			// luminous flux (lumen)
			uom = createProductUOM(UnitType.LUMINOUS_FLUX, Unit.LUMEN, units.getString("lumen.name"),
					units.getString("lumen.symbol"), units.getString("lumen.desc"), getUOM(Unit.CANDELA),
					getUOM(Unit.STERADIAN));
			break;

		case LUX:
			// illuminance (lux)
			uom = createQuotientUOM(UnitType.ILLUMINANCE, Unit.LUX, units.getString("lux.name"),
					units.getString("lux.symbol"), units.getString("lux.desc"), getUOM(Unit.LUMEN),
					getUOM(Unit.SQUARE_METRE));
			break;

		case BECQUEREL:
			// radioactivity (becquerel). Same base symbol as Hertz
			uom = createScalarUOM(UnitType.RADIOACTIVITY, Unit.BECQUEREL, units.getString("becquerel.name"),
					units.getString("becquerel.symbol"), units.getString("becquerel.desc"));
			break;

		case GRAY:
			// gray (Gy)
			uom = createQuotientUOM(UnitType.RADIATION_DOSE_ABSORBED, Unit.GRAY, units.getString("gray.name"),
					units.getString("gray.symbol"), units.getString("gray.desc"), getUOM(Unit.JOULE),
					getUOM(Unit.KILOGRAM));
			break;

		case SIEVERT:
			// sievert (Sv)
			uom = createQuotientUOM(UnitType.RADIATION_DOSE_EFFECTIVE, Unit.SIEVERT, units.getString("sievert.name"),
					units.getString("sievert.symbol"), units.getString("sievert.desc"), getUOM(Unit.JOULE),
					getUOM(Unit.KILOGRAM));
			break;

		case SIEVERTS_PER_HOUR:
			uom = createQuotientUOM(UnitType.RADIATION_DOSE_RATE, Unit.SIEVERTS_PER_HOUR, units.getString("sph.name"),
					units.getString("sph.symbol"), units.getString("sph.desc"), getUOM(Unit.SIEVERT), getHour());
			break;

		case KATAL:
			// katal (kat)
			uom = createQuotientUOM(UnitType.CATALYTIC_ACTIVITY, Unit.KATAL, units.getString("katal.name"),
					units.getString("katal.symbol"), units.getString("katal.desc"), getUOM(Unit.MOLE), getSecond());
			break;

		case UNIT:
			// Unit (U)
			uom = createScalarUOM(UnitType.CATALYTIC_ACTIVITY, Unit.UNIT, units.getString("unit.name"),
					units.getString("unit.symbol"), units.getString("unit.desc"));
			uom.setConversion(1.0E-06 / 60d, getUOM(Unit.KATAL));
			break;

		case INTERNATIONAL_UNIT:
			uom = createScalarUOM(UnitType.SUBSTANCE_AMOUNT, Unit.INTERNATIONAL_UNIT, units.getString("iu.name"),
					units.getString("iu.symbol"), units.getString("iu.desc"));
			break;

		case ANGSTROM:
			// length
			uom = createScalarUOM(UnitType.LENGTH, Unit.ANGSTROM, units.getString("angstrom.name"),
					units.getString("angstrom.symbol"), units.getString("angstrom.desc"));
			uom.setConversion(0.1, getUOM(Prefix.NANO, getUOM(Unit.METRE)));
			break;

		case BIT:
			// computer bit
			uom = createScalarUOM(UnitType.COMPUTER_SCIENCE, Unit.BIT, units.getString("bit.name"),
					units.getString("bit.symbol"), units.getString("bit.desc"));
			break;

		case BYTE:
			// computer byte
			uom = createScalarUOM(UnitType.COMPUTER_SCIENCE, Unit.BYTE, units.getString("byte.name"),
					units.getString("byte.symbol"), units.getString("byte.desc"));
			uom.setConversion(8d, getUOM(Unit.BIT));
			break;

		case WATTS_PER_SQ_METRE:
			uom = createQuotientUOM(UnitType.IRRADIANCE, Unit.WATTS_PER_SQ_METRE, units.getString("wsm.name"),
					units.getString("wsm.symbol"), units.getString("wsm.desc"), getUOM(Unit.WATT),
					getUOM(Unit.SQUARE_METRE));
			break;

		case PARSEC:
			uom = createScalarUOM(UnitType.LENGTH, Unit.PARSEC, units.getString("parsec.name"),
					units.getString("parsec.symbol"), units.getString("parsec.desc"));
			uom.setConversion(3.08567758149137E+16, getUOM(Unit.METRE));
			break;

		case ASTRONOMICAL_UNIT:
			uom = createScalarUOM(UnitType.LENGTH, Unit.ASTRONOMICAL_UNIT, units.getString("au.name"),
					units.getString("au.symbol"), units.getString("au.desc"));
			uom.setConversion(1.49597870700E+11, getUOM(Unit.METRE));
			break;

		default:
			break;
		}

		return uom;
	}

	private UnitOfMeasure createCustomaryUnit(Unit unit) throws Exception {
		UnitOfMeasure uom = null;

		switch (unit) {

		case RANKINE:
			// Rankine (base)
			uom = createScalarUOM(UnitType.TEMPERATURE, Unit.RANKINE, units.getString("rankine.name"),
					units.getString("rankine.symbol"), units.getString("rankine.desc"));

			// create bridge to SI
			uom.setBridgeConversion(5d / 9d, getUOM(Unit.KELVIN), 0.0d);
			break;

		case FAHRENHEIT:
			// Fahrenheit
			uom = createScalarUOM(UnitType.TEMPERATURE, Unit.FAHRENHEIT, units.getString("fahrenheit.name"),
					units.getString("fahrenheit.symbol"), units.getString("fahrenheit.desc"));
			uom.setConversion(1.0, getUOM(Unit.RANKINE), 459.67);
			break;

		case POUND_MASS:
			// lb mass (base)
			uom = createScalarUOM(UnitType.MASS, Unit.POUND_MASS, units.getString("lbm.name"),
					units.getString("lbm.symbol"), units.getString("lbm.desc"));

			// create bridge to SI
			uom.setBridgeConversion(0.45359237, getUOM(Unit.KILOGRAM), 0.0d);
			break;

		case OUNCE:
			// ounce
			uom = createScalarUOM(UnitType.MASS, Unit.OUNCE, units.getString("ounce.name"),
					units.getString("ounce.symbol"), units.getString("ounce.desc"));
			uom.setConversion(0.0625, getUOM(Unit.POUND_MASS));
			break;

		case TROY_OUNCE:
			// troy ounce
			uom = createScalarUOM(UnitType.MASS, Unit.TROY_OUNCE, units.getString("troy_oz.name"),
					units.getString("troy_oz.symbol"), units.getString("troy_oz.desc"));
			uom.setConversion(31.1034768, getUOM(Unit.GRAM));
			break;

		case SLUG:
			// slug
			uom = createScalarUOM(UnitType.MASS, Unit.SLUG, units.getString("slug.name"),
					units.getString("slug.symbol"), units.getString("slug.desc"));
			Quantity g = getQuantity(Constant.GRAVITY).convert(getUOM(Unit.FEET_PER_SEC_SQUARED));
			uom.setConversion(g.getAmount(), getUOM(Unit.POUND_MASS));
			break;

		case FOOT:
			// foot (foot is base conversion unit)
			uom = createScalarUOM(UnitType.LENGTH, Unit.FOOT, units.getString("foot.name"),
					units.getString("foot.symbol"), units.getString("foot.desc"));

			// bridge to SI
			uom.setBridgeConversion(0.3048, getUOM(Unit.METRE), 0);
			break;

		case INCH:
			// inch
			uom = createScalarUOM(UnitType.LENGTH, Unit.INCH, units.getString("inch.name"),
					units.getString("inch.symbol"), units.getString("inch.desc"));
			uom.setConversion(1d / 12d, getUOM(Unit.FOOT));
			break;

		case MIL:
			// inch
			uom = createScalarUOM(UnitType.LENGTH, Unit.MIL, units.getString("mil.name"), units.getString("mil.symbol"),
					units.getString("mil.desc"));
			uom.setConversion(Prefix.MILLI.getFactor(), getUOM(Unit.INCH));
			break;

		case POINT:
			// point
			uom = createScalarUOM(UnitType.LENGTH, Unit.POINT, units.getString("point.name"),
					units.getString("point.symbol"), units.getString("point.desc"));
			uom.setConversion(1d / 72d, getUOM(Unit.INCH));
			break;

		case YARD:
			// yard
			uom = createScalarUOM(UnitType.LENGTH, Unit.YARD, units.getString("yard.name"),
					units.getString("yard.symbol"), units.getString("yard.desc"));
			uom.setConversion(3d, getUOM(Unit.FOOT));
			break;

		case MILE:
			// mile
			uom = createScalarUOM(UnitType.LENGTH, Unit.MILE, units.getString("mile.name"),
					units.getString("mile.symbol"), units.getString("mile.desc"));
			uom.setConversion(5280d, getUOM(Unit.FOOT));
			break;

		case NAUTICAL_MILE:
			// nautical mile
			uom = createScalarUOM(UnitType.LENGTH, Unit.NAUTICAL_MILE, units.getString("NM.name"),
					units.getString("NM.symbol"), units.getString("NM.desc"));
			uom.setConversion(6080d, getUOM(Unit.FOOT));
			break;

		case FATHOM:
			// fathom
			uom = createScalarUOM(UnitType.LENGTH, Unit.FATHOM, units.getString("fth.name"),
					units.getString("fth.symbol"), units.getString("fth.desc"));
			uom.setConversion(6d, getUOM(Unit.FOOT));

			break;

		case PSI:
			// psi
			uom = createQuotientUOM(UnitType.PRESSURE, Unit.PSI, units.getString("psi.name"),
					units.getString("psi.symbol"), units.getString("psi.desc"), getUOM(Unit.POUND_FORCE),
					getUOM(Unit.SQUARE_INCH));
			break;

		case IN_HG:
			// inches of Mercury
			uom = createScalarUOM(UnitType.PRESSURE, Unit.IN_HG, units.getString("inhg.name"),
					units.getString("inhg.symbol"), units.getString("inhg.desc"));
			uom.setConversion(0.4911531047, getUOM(Unit.PSI));
			break;

		case SQUARE_INCH:
			// square inch
			uom = createPowerUOM(UnitType.AREA, Unit.SQUARE_INCH, units.getString("in2.name"),
					units.getString("in2.symbol"), units.getString("in2.desc"), getUOM(Unit.INCH), 2);
			uom.setConversion(1d / 144d, getUOM(Unit.SQUARE_FOOT));
			break;

		case SQUARE_FOOT:
			// square foot
			uom = createPowerUOM(UnitType.AREA, Unit.SQUARE_FOOT, units.getString("ft2.name"),
					units.getString("ft2.symbol"), units.getString("ft2.desc"), getUOM(Unit.FOOT), 2);
			break;

		case SQUARE_YARD:
			// square yard
			uom = createPowerUOM(UnitType.AREA, Unit.SQUARE_YARD, units.getString("yd2.name"),
					units.getString("yd2.symbol"), units.getString("yd2.desc"), getUOM(Unit.YARD), 2);
			break;

		case ACRE:
			// acre
			uom = createScalarUOM(UnitType.AREA, Unit.ACRE, units.getString("acre.name"),
					units.getString("acre.symbol"), units.getString("acre.desc"));
			uom.setConversion(43560d, getUOM(Unit.SQUARE_FOOT));
			break;

		case CUBIC_INCH:
			// cubic inch
			uom = createPowerUOM(UnitType.VOLUME, Unit.CUBIC_INCH, units.getString("in3.name"),
					units.getString("in3.symbol"), units.getString("in3.desc"), getUOM(Unit.INCH), 3);
			uom.setConversion(1d / 1728d, getUOM(Unit.CUBIC_FOOT));
			break;

		case CUBIC_FOOT:
			// cubic feet
			uom = createPowerUOM(UnitType.VOLUME, Unit.CUBIC_FOOT, units.getString("ft3.name"),
					units.getString("ft3.symbol"), units.getString("ft3.desc"), getUOM(Unit.FOOT), 3);
			break;

		case CUBIC_FEET_PER_SEC:
			// flow (volume)
			uom = createQuotientUOM(UnitType.VOLUMETRIC_FLOW, Unit.CUBIC_FEET_PER_SEC,
					units.getString("ft3PerSec.name"), units.getString("ft3PerSec.symbol"),
					units.getString("ft3PerSec.desc"), getUOM(Unit.CUBIC_FOOT), getSecond());
			break;

		case CORD:
			// cord
			uom = createScalarUOM(UnitType.VOLUME, Unit.CORD, units.getString("cord.name"),
					units.getString("cord.symbol"), units.getString("cord.desc"));
			uom.setConversion(128d, getUOM(Unit.CUBIC_FOOT));
			break;

		case CUBIC_YARD:
			// cubic yard
			uom = createPowerUOM(UnitType.VOLUME, Unit.CUBIC_YARD, units.getString("yd3.name"),
					units.getString("yd3.symbol"), units.getString("yd3.desc"), getUOM(Unit.YARD), 3);
			break;

		case FEET_PER_SEC:
			// feet/sec
			uom = createQuotientUOM(UnitType.VELOCITY, Unit.FEET_PER_SEC, units.getString("fps.name"),
					units.getString("fps.symbol"), units.getString("fps.desc"), getUOM(Unit.FOOT), getSecond());
			break;

		case KNOT:
			// knot
			uom = createScalarUOM(UnitType.VELOCITY, Unit.KNOT, units.getString("knot.name"),
					units.getString("knot.symbol"), units.getString("knot.desc"));
			uom.setConversion(6080d / 3600d, getUOM(Unit.FEET_PER_SEC));
			break;

		case FEET_PER_SEC_SQUARED:
			// acceleration
			uom = createQuotientUOM(UnitType.ACCELERATION, Unit.FEET_PER_SEC_SQUARED, units.getString("ftps2.name"),
					units.getString("ftps2.symbol"), units.getString("ftps2.desc"), getUOM(Unit.FOOT),
					getUOM(Unit.SQUARE_SECOND));
			break;

		case HP:
			// HP (mechanical)
			uom = createProductUOM(UnitType.POWER, Unit.HP, units.getString("hp.name"), units.getString("hp.symbol"),
					units.getString("hp.desc"), getUOM(Unit.POUND_FORCE), getUOM(Unit.FEET_PER_SEC));
			uom.setScalingFactor(550d);
			break;

		case BTU:
			// BTU = 1055.056 Joules (778.169 ft-lbf)
			uom = createScalarUOM(UnitType.ENERGY, Unit.BTU, units.getString("btu.name"), units.getString("btu.symbol"),
					units.getString("btu.desc"));
			uom.setConversion(778.1692622659652, getUOM(Unit.FOOT_POUND_FORCE));
			break;

		case FOOT_POUND_FORCE:
			// ft-lbf
			uom = createProductUOM(UnitType.ENERGY, Unit.FOOT_POUND_FORCE, units.getString("ft_lbf.name"),
					units.getString("ft_lbf.symbol"), units.getString("ft_lbf.desc"), getUOM(Unit.FOOT),
					getUOM(Unit.POUND_FORCE));
			break;

		case POUND_FORCE:
			// force F = m·A (lbf)
			uom = createProductUOM(UnitType.FORCE, Unit.POUND_FORCE, units.getString("lbf.name"),
					units.getString("lbf.symbol"), units.getString("lbf.desc"), getUOM(Unit.POUND_MASS),
					getUOM(Unit.FEET_PER_SEC_SQUARED));

			// factor is acceleration of gravity
			Quantity gravity = getQuantity(Constant.GRAVITY).convert(getUOM(Unit.FEET_PER_SEC_SQUARED));
			uom.setScalingFactor(gravity.getAmount());
			break;

		case GRAIN:
			// mass
			uom = createScalarUOM(UnitType.MASS, Unit.GRAIN, units.getString("grain.name"),
					units.getString("grain.symbol"), units.getString("grain.desc"));
			uom.setConversion(1d / 7000d, getUOM(Unit.POUND_MASS));
			break;

		case MILES_PER_HOUR:
			// velocity
			uom = createScalarUOM(UnitType.VELOCITY, Unit.MILES_PER_HOUR, units.getString("mph.name"),
					units.getString("mph.symbol"), units.getString("mph.desc"));
			uom.setConversion(5280d / 3600d, getUOM(Unit.FEET_PER_SEC));
			break;

		case REV_PER_MIN:
			// rpm
			uom = createQuotientUOM(UnitType.FREQUENCY, Unit.REV_PER_MIN, units.getString("rpm.name"),
					units.getString("rpm.symbol"), units.getString("rpm.desc"), getOne(), getMinute());
			break;

		default:
			break;
		}

		return uom;
	}

	private UnitOfMeasure createUSUnit(Unit unit) throws Exception {
		UnitOfMeasure uom = null;

		switch (unit) {

		case US_GALLON:
			// gallon
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_GALLON, units.getString("us_gallon.name"),
					units.getString("us_gallon.symbol"), units.getString("us_gallon.desc"));
			uom.setConversion(231d, getUOM(Unit.CUBIC_INCH));
			break;

		case US_BARREL:
			// barrel
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_BARREL, units.getString("us_bbl.name"),
					units.getString("us_bbl.symbol"), units.getString("us_bbl.desc"));
			uom.setConversion(42d, getUOM(Unit.US_GALLON));
			break;

		case US_BUSHEL:
			// bushel
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_BUSHEL, units.getString("us_bu.name"),
					units.getString("us_bu.symbol"), units.getString("us_bu.desc"));
			uom.setConversion(2150.42058, getUOM(Unit.CUBIC_INCH));
			break;

		case US_FLUID_OUNCE:
			// fluid ounce
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_FLUID_OUNCE, units.getString("us_fl_oz.name"),
					units.getString("us_fl_oz.symbol"), units.getString("us_fl_oz.desc"));
			uom.setConversion(0.0078125, getUOM(Unit.US_GALLON));
			break;

		case US_CUP:
			// cup
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_CUP, units.getString("us_cup.name"),
					units.getString("us_cup.symbol"), units.getString("us_cup.desc"));
			uom.setConversion(8d, getUOM(Unit.US_FLUID_OUNCE));
			break;

		case US_PINT:
			// pint
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_PINT, units.getString("us_pint.name"),
					units.getString("us_pint.symbol"), units.getString("us_pint.desc"));
			uom.setConversion(16d, getUOM(Unit.US_FLUID_OUNCE));
			break;

		case US_QUART:
			// quart
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_QUART, units.getString("us_quart.name"),
					units.getString("us_quart.symbol"), units.getString("us_quart.desc"));
			uom.setConversion(32d, getUOM(Unit.US_FLUID_OUNCE));
			break;

		case US_TABLESPOON:
			// tablespoon
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_TABLESPOON, units.getString("us_tbsp.name"),
					units.getString("us_tbsp.symbol"), units.getString("us_tbsp.desc"));
			uom.setConversion(0.5, getUOM(Unit.US_FLUID_OUNCE));
			break;

		case US_TEASPOON:
			// teaspoon
			uom = createScalarUOM(UnitType.VOLUME, Unit.US_TEASPOON, units.getString("us_tsp.name"),
					units.getString("us_tsp.symbol"), units.getString("us_tsp.desc"));
			uom.setConversion(1d / 6d, getUOM(Unit.US_FLUID_OUNCE));
			break;

		case US_TON:
			// ton
			uom = createScalarUOM(UnitType.MASS, Unit.US_TON, units.getString("us_ton.name"),
					units.getString("us_ton.symbol"), units.getString("us_ton.desc"));
			uom.setConversion(2000d, getUOM(Unit.POUND_MASS));
			break;

		default:
			break;
		}

		return uom;
	}

	private UnitOfMeasure createBRUnit(Unit unit) throws Exception {

		UnitOfMeasure uom = null;

		switch (unit) {
		case BR_GALLON:
			// gallon
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_GALLON, units.getString("br_gallon.name"),
					units.getString("br_gallon.symbol"), units.getString("br_gallon.desc"));
			uom.setConversion(277.4194327916215, getUOM(Unit.CUBIC_INCH));
			break;

		case BR_BUSHEL:
			// bushel
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_BUSHEL, units.getString("br_bu.name"),
					units.getString("br_bu.symbol"), units.getString("br_bu.desc"));
			uom.setConversion(8d, getUOM(Unit.BR_GALLON));
			break;

		case BR_FLUID_OUNCE:
			// fluid ounce
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_FLUID_OUNCE, units.getString("br_fl_oz.name"),
					units.getString("br_fl_oz.symbol"), units.getString("br_fl_oz.desc"));
			uom.setConversion(0.00625, getUOM(Unit.BR_GALLON));
			break;

		case BR_CUP:
			// cup
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_CUP, units.getString("br_cup.name"),
					units.getString("br_cup.symbol"), units.getString("br_cup.desc"));
			uom.setConversion(8d, getUOM(Unit.BR_FLUID_OUNCE));
			break;

		case BR_PINT:
			// pint
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_PINT, units.getString("br_pint.name"),
					units.getString("br_pint.symbol"), units.getString("br_pint.desc"));
			uom.setConversion(20d, getUOM(Unit.BR_FLUID_OUNCE));
			break;

		case BR_QUART:
			// quart
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_QUART, units.getString("br_quart.name"),
					units.getString("br_quart.symbol"), units.getString("br_quart.desc"));
			uom.setConversion(40d, getUOM(Unit.BR_FLUID_OUNCE));
			break;

		case BR_TABLESPOON:
			// tablespoon
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_TABLESPOON, units.getString("br_tbsp.name"),
					units.getString("br_tbsp.symbol"), units.getString("br_tbsp.desc"));
			uom.setConversion(0.625, getUOM(Unit.BR_FLUID_OUNCE));
			break;

		case BR_TEASPOON:
			// teaspoon
			uom = createScalarUOM(UnitType.VOLUME, Unit.BR_TEASPOON, units.getString("br_tsp.name"),
					units.getString("br_tsp.symbol"), units.getString("br_tsp.desc"));
			uom.setConversion(5d / 24d, getUOM(Unit.BR_FLUID_OUNCE));
			break;

		case BR_TON:
			// ton
			uom = createScalarUOM(UnitType.MASS, Unit.BR_TON, units.getString("br_ton.name"),
					units.getString("br_ton.symbol"), units.getString("br_ton.desc"));
			uom.setConversion(2240d, getUOM(Unit.POUND_MASS));
			break;

		default:
			break;
		}

		return uom;
	}

	private UnitOfMeasure createFinancialUnit(Unit unit) throws Exception {
		UnitOfMeasure uom = null;

		switch (unit) {

		case US_DOLLAR:
			uom = createScalarUOM(UnitType.CURRENCY, Unit.US_DOLLAR, units.getString("us_dollar.name"),
					units.getString("us_dollar.symbol"), units.getString("us_dollar.desc"));
			break;

		case EURO:
			uom = createScalarUOM(UnitType.CURRENCY, Unit.EURO, units.getString("euro.name"),
					units.getString("euro.symbol"), units.getString("euro.desc"));
			break;

		case YUAN:
			uom = createScalarUOM(UnitType.CURRENCY, Unit.YUAN, units.getString("yuan.name"),
					units.getString("yuan.symbol"), units.getString("yuan.desc"));
			break;

		default:
			break;
		}

		return uom;
	}

	/**
	 * Get the unit of measure with this unique enumerated type
	 * 
	 * @param unit
	 *            {@link Unit}
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception Exception
	 */
	public UnitOfMeasure getUOM(Unit unit) throws Exception {
		UnitOfMeasure uom = cacheManager.getUOM(unit);

		if (uom == null) {
			uom = createUOM(unit);
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
		return cacheManager.getUOM(symbol);
	}

	/**
	 * Get the unit of measure with this base symbol
	 * 
	 * @param symbol
	 *            Base symbol
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getBaseUOM(String symbol) {
		return cacheManager.getBaseUOM(symbol);
	}

	/**
	 * Remove all cached units of measure
	 */
	public void clearCache() {
		cacheManager.clearCache();
	}

	/**
	 * Get all units currently cached by this measurement system
	 * 
	 * @return List of {@link UnitOfMeasure}
	 */
	public List<UnitOfMeasure> getRegisteredUnits() {
		Collection<UnitOfMeasure> units = cacheManager.getCachedUnits();
		List<UnitOfMeasure> list = new ArrayList<UnitOfMeasure>(units);

		Collections.sort(list, new Comparator<UnitOfMeasure>() {
			public int compare(UnitOfMeasure unit1, UnitOfMeasure unit2) {

				return unit1.getSymbol().compareTo(unit2.getSymbol());
			}
		});

		return list;
	}

	/**
	 * Get the units of measure cached by their symbol
	 * 
	 * @return Symbol cache
	 */
	public Map<String, UnitOfMeasure> getSymbolCache() {
		return cacheManager.getSymbolCache();
	}

	/**
	 * Get the units of measure cached by their base symbol
	 * 
	 * @return Base symbol cache
	 */
	public Map<String, UnitOfMeasure> getBaseSymbolCache() {
		return cacheManager.getBaseSymbolCache();
	}

	/**
	 * Get the units of measure cached by their {@link Unit} enumeration
	 * 
	 * @return Enumeration cache
	 */
	public Map<Unit, UnitOfMeasure> getEnumerationCache() {
		return cacheManager.getEnumerationCache();
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
		cacheManager.unregisterUnit(uom);
	}

	ResourceBundle getSymbols() {
		return units;
	}

	/**
	 * Cache this unit of measure
	 * 
	 * @param uom
	 *            {@link UnitOfMeasure} to cache
	 * @throws Exception
	 *             Exception
	 */
	public void registerUnit(UnitOfMeasure uom) throws Exception {
		cacheManager.registerUnit(uom);
	}

	private UnitOfMeasure createUOM(UnitType type, Unit id, String name, String symbol, String description)
			throws Exception {

		if (symbol == null || symbol.length() == 0) {
			throw new Exception(MeasurementSystem.getMessage("symbol.cannot.be.null"));
		}

		if (type == null) {
			throw new Exception(getMessage("unit.type.cannot.be.null"));
		}

		UnitOfMeasure uom = cacheManager.getUOM(symbol);

		if (uom == null) {
			// create a new one
			uom = new UnitOfMeasure(type, name, symbol, description);
		}
		return uom;
	}

	private UnitOfMeasure createScalarUOM(UnitType type, Unit id, String name, String symbol, String description)
			throws Exception {

		UnitOfMeasure uom = createUOM(type, id, name, symbol, description);
		uom.setEnumeration(id);
		registerUnit(uom);

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
		registerUnit(uom);
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
	 * Create a unit of measure that is a unit divided by another unit
	 * 
	 * @param dividend
	 *            {@link UnitOfMeasure}
	 * @param divisor
	 *            {@link UnitOfMeasure}
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure createQuotientUOM(UnitOfMeasure dividend, UnitOfMeasure divisor) throws Exception {
		if (dividend == null) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("dividend.cannot.be.null"), "");
			throw new Exception(msg);
		}

		if (divisor == null) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("divisor.cannot.be.null"), "");
			throw new Exception(msg);
		}

		String symbol = UnitOfMeasure.generateQuotientSymbol(dividend, divisor);
		return createQuotientUOM(UnitType.UNCLASSIFIED, null, null, symbol, null, dividend, divisor);
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
		registerUnit(uom);
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
	 * Create a unit of measure that is the product of two other units of
	 * measure
	 * 
	 * @param multiplier
	 *            {@link UnitOfMeasure} multiplier
	 * @param multiplicand
	 *            {@link UnitOfMeasure} multiplicand
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure createProductUOM(UnitOfMeasure multiplier, UnitOfMeasure multiplicand) throws Exception {
		if (multiplier == null) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("multiplier.cannot.be.null"), "");
			throw new Exception(msg);
		}

		if (multiplicand == null) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("multiplicand.cannot.be.null"), "");
			throw new Exception(msg);
		}

		String symbol = UnitOfMeasure.generateProductSymbol(multiplier, multiplicand);
		return createProductUOM(UnitType.UNCLASSIFIED, null, null, symbol, null, multiplier, multiplicand);
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
	 * @param exponent
	 *            Exponent
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure createPowerUOM(UnitType type, Unit id, String name, String symbol, String description,
			UnitOfMeasure base, int exponent) throws Exception {

		UnitOfMeasure uom = createUOM(type, id, name, symbol, description);
		uom.setPowerUnit(base, exponent);
		uom.setEnumeration(id);
		registerUnit(uom);
		return uom;
	}

	/**
	 * Create a unit of measure with a base raised to an integral exponent
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
	 * @param exponent
	 *            Exponent
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure createPowerUOM(UnitType type, String name, String symbol, String description,
			UnitOfMeasure base, int exponent) throws Exception {
		return createPowerUOM(type, null, name, symbol, description, base, exponent);
	}

	/**
	 * Create a unit of measure with a base raised to an integral exponent
	 * 
	 * @param base
	 *            {@link UnitOfMeasure}
	 * @param exponent
	 *            Exponent
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public UnitOfMeasure createPowerUOM(UnitOfMeasure base, int exponent) throws Exception {
		if (base == null) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("base.cannot.be.null"), "");
			throw new Exception(msg);
		}

		String symbol = UnitOfMeasure.generatePowerSymbol(base, exponent);
		return createPowerUOM(UnitType.UNCLASSIFIED, null, null, symbol, null, base, exponent);
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
			// generate a name and description
			String name = prefix.getName() + targetUOM.getName();
			String description = prefix.getFactor() + " " + targetUOM.getName();

			// scaling factor
			double scalingFactor = targetUOM.getScalingFactor() * prefix.getFactor();

			// create the unit of measure and set conversion
			scaled = createScalarUOM(targetUOM.getUnitType(), null, name, symbol, description);
			scaled.setConversion(scalingFactor, targetUOM.getAbscissaUnit());
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

	/**
	 * Get all the units of measure of the specified type
	 * 
	 * @param type
	 *            {@link UnitType}
	 * @return List of {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public List<UnitOfMeasure> getUnitsOfMeasure(UnitType type) throws Exception {
		List<UnitOfMeasure> units = new ArrayList<UnitOfMeasure>();

		switch (type) {
		case LENGTH:
			// SI
			units.add(getUOM(Unit.METRE));
			units.add(getUOM(Unit.ANGSTROM));
			units.add(getUOM(Unit.PARSEC));
			units.add(getUOM(Unit.ASTRONOMICAL_UNIT));

			// customary
			units.add(getUOM(Unit.FOOT));
			units.add(getUOM(Unit.INCH));
			units.add(getUOM(Unit.MIL));
			units.add(getUOM(Unit.POINT));
			units.add(getUOM(Unit.YARD));
			units.add(getUOM(Unit.MILE));
			units.add(getUOM(Unit.NAUTICAL_MILE));
			units.add(getUOM(Unit.FATHOM));
			break;

		case MASS:
			// SI
			units.add(getUOM(Unit.KILOGRAM));
			units.add(getUOM(Unit.TONNE));
			units.add(getUOM(Unit.CARAT));

			// customary
			units.add(getUOM(Unit.POUND_MASS));
			units.add(getUOM(Unit.OUNCE));
			units.add(getUOM(Unit.TROY_OUNCE));
			units.add(getUOM(Unit.SLUG));
			units.add(getUOM(Unit.GRAIN));

			// US
			units.add(getUOM(Unit.US_TON));

			// British
			units.add(getUOM(Unit.BR_TON));
			break;

		case TIME:
			units.add(getUOM(Unit.SECOND));
			units.add(getUOM(Unit.MINUTE));
			units.add(getUOM(Unit.HOUR));
			units.add(getUOM(Unit.DAY));
			units.add(getUOM(Unit.WEEK));
			units.add(getUOM(Unit.JULIAN_YEAR));

			break;

		case ACCELERATION:
			units.add(getUOM(Unit.METRE_PER_SEC_SQUARED));
			units.add(getUOM(Unit.FEET_PER_SEC_SQUARED));
			break;

		case AREA:
			// customary
			units.add(getUOM(Unit.SQUARE_INCH));
			units.add(getUOM(Unit.SQUARE_FOOT));
			units.add(getUOM(Unit.SQUARE_YARD));
			units.add(getUOM(Unit.ACRE));

			// SI
			units.add(getUOM(Unit.SQUARE_METRE));
			units.add(getUOM(Unit.HECTARE));

			break;

		case CATALYTIC_ACTIVITY:
			units.add(getUOM(Unit.KATAL));
			units.add(getUOM(Unit.UNIT));
			break;

		case COMPUTER_SCIENCE:
			units.add(getUOM(Unit.BIT));
			units.add(getUOM(Unit.BYTE));
			break;

		case DENSITY:
			units.add(getUOM(Unit.KILOGRAM_PER_CU_METRE));
			break;

		case DYNAMIC_VISCOSITY:
			units.add(getUOM(Unit.PASCAL_SECOND));
			break;

		case ELECTRIC_CAPACITANCE:
			units.add(getUOM(Unit.FARAD));
			break;

		case ELECTRIC_CHARGE:
			units.add(getUOM(Unit.COULOMB));
			break;

		case ELECTRIC_CONDUCTANCE:
			units.add(getUOM(Unit.SIEMENS));
			break;

		case ELECTRIC_CURRENT:
			units.add(getUOM(Unit.AMPERE));
			break;

		case ELECTRIC_FIELD_STRENGTH:
			units.add(getUOM(Unit.AMPERE_PER_METRE));
			break;

		case ELECTRIC_INDUCTANCE:
			units.add(getUOM(Unit.HENRY));
			break;

		case ELECTRIC_PERMITTIVITY:
			units.add(getUOM(Unit.FARAD_PER_METRE));
			break;

		case ELECTRIC_RESISTANCE:
			units.add(getUOM(Unit.OHM));
			break;

		case ELECTROMOTIVE_FORCE:
			units.add(getUOM(Unit.VOLT));
			break;

		case ENERGY:
			// customary
			units.add(getUOM(Unit.BTU));
			units.add(getUOM(Unit.FOOT_POUND_FORCE));

			// SI
			units.add(getUOM(Unit.CALORIE));
			units.add(getUOM(Unit.NEWTON_METRE));
			units.add(getUOM(Unit.JOULE));
			units.add(getUOM(Unit.WATT_HOUR));
			units.add(getUOM(Unit.ELECTRON_VOLT));
			break;

		case CURRENCY:
			units.add(getUOM(Unit.US_DOLLAR));
			units.add(getUOM(Unit.EURO));
			units.add(getUOM(Unit.YUAN));
			break;

		case FORCE:
			// customary
			units.add(getUOM(Unit.POUND_FORCE));

			// SI
			units.add(getUOM(Unit.NEWTON));
			break;

		case FREQUENCY:
			units.add(getUOM(Unit.REV_PER_MIN));
			units.add(getUOM(Unit.HERTZ));
			units.add(getUOM(Unit.RAD_PER_SEC));
			break;

		case ILLUMINANCE:
			units.add(getUOM(Unit.LUX));
			break;

		case INTENSITY:
			units.add(getUOM(Unit.DECIBEL));
			break;

		case IRRADIANCE:
			units.add(getUOM(Unit.WATTS_PER_SQ_METRE));
			break;

		case KINEMATIC_VISCOSITY:
			units.add(getUOM(Unit.SQUARE_METRE_PER_SEC));
			break;

		case LUMINOSITY:
			units.add(getUOM(Unit.CANDELA));
			break;

		case LUMINOUS_FLUX:
			units.add(getUOM(Unit.LUMEN));
			break;

		case MAGNETIC_FLUX:
			units.add(getUOM(Unit.WEBER));
			break;

		case MAGNETIC_FLUX_DENSITY:
			units.add(getUOM(Unit.TESLA));
			break;

		case MASS_FLOW:
			units.add(getUOM(Unit.KILOGRAM_PER_SEC));
			break;

		case MOLAR_CONCENTRATION:
			units.add(getUOM(Unit.PH));
			break;

		case PLANE_ANGLE:
			units.add(getUOM(Unit.DEGREE));
			units.add(getUOM(Unit.RADIAN));
			units.add(getUOM(Unit.ARC_SECOND));
			break;

		case POWER:
			units.add(getUOM(Unit.HP));
			units.add(getUOM(Unit.WATT));
			break;

		case PRESSURE:
			// customary
			units.add(getUOM(Unit.PSI));
			units.add(getUOM(Unit.IN_HG));

			// SI
			units.add(getUOM(Unit.PASCAL));
			units.add(getUOM(Unit.ATMOSPHERE));
			units.add(getUOM(Unit.BAR));
			break;

		case RADIATION_DOSE_ABSORBED:
			units.add(getUOM(Unit.GRAY));
			break;

		case RADIATION_DOSE_EFFECTIVE:
			units.add(getUOM(Unit.SIEVERT));
			break;

		case RADIATION_DOSE_RATE:
			units.add(getUOM(Unit.SIEVERTS_PER_HOUR));
			break;

		case RADIOACTIVITY:
			units.add(getUOM(Unit.BECQUEREL));
			break;

		case RECIPROCAL_LENGTH:
			units.add(getUOM(Unit.DIOPTER));
			break;

		case SOLID_ANGLE:
			units.add(getUOM(Unit.STERADIAN));
			break;

		case SUBSTANCE_AMOUNT:
			units.add(getUOM(Unit.MOLE));
			units.add(getUOM(Unit.EQUIVALENT));
			units.add(getUOM(Unit.INTERNATIONAL_UNIT));
			break;

		case TEMPERATURE:
			// customary
			units.add(getUOM(Unit.RANKINE));
			units.add(getUOM(Unit.FAHRENHEIT));

			// SI
			units.add(getUOM(Unit.KELVIN));
			units.add(getUOM(Unit.CELSIUS));
			break;

		case TIME_SQUARED:
			units.add(getUOM(Unit.SQUARE_SECOND));
			break;

		case UNCLASSIFIED:
			break;

		case UNITY:
			units.add(getUOM(Unit.ONE));
			units.add(getUOM(Unit.PERCENT));
			break;

		case VELOCITY:
			// customary
			units.add(getUOM(Unit.FEET_PER_SEC));
			units.add(getUOM(Unit.MILES_PER_HOUR));
			units.add(getUOM(Unit.KNOT));

			// SI
			units.add(getUOM(Unit.METRE_PER_SEC));
			break;

		case VOLUME:
			// British
			units.add(getUOM(Unit.BR_BUSHEL));
			units.add(getUOM(Unit.BR_CUP));
			units.add(getUOM(Unit.BR_FLUID_OUNCE));
			units.add(getUOM(Unit.BR_GALLON));
			units.add(getUOM(Unit.BR_PINT));
			units.add(getUOM(Unit.BR_QUART));
			units.add(getUOM(Unit.BR_TABLESPOON));
			units.add(getUOM(Unit.BR_TEASPOON));

			// customary
			units.add(getUOM(Unit.CUBIC_FOOT));
			units.add(getUOM(Unit.CUBIC_YARD));
			units.add(getUOM(Unit.CUBIC_INCH));
			units.add(getUOM(Unit.CORD));

			// SI
			units.add(getUOM(Unit.CUBIC_METRE));
			units.add(getUOM(Unit.LITRE));

			// US
			units.add(getUOM(Unit.US_BARREL));
			units.add(getUOM(Unit.US_BUSHEL));
			units.add(getUOM(Unit.US_CUP));
			units.add(getUOM(Unit.US_FLUID_OUNCE));
			units.add(getUOM(Unit.US_GALLON));
			units.add(getUOM(Unit.US_PINT));
			units.add(getUOM(Unit.US_QUART));
			units.add(getUOM(Unit.US_TABLESPOON));
			units.add(getUOM(Unit.US_TEASPOON));
			break;

		case VOLUMETRIC_FLOW:
			units.add(getUOM(Unit.CUBIC_METRE_PER_SEC));
			units.add(getUOM(Unit.CUBIC_FEET_PER_SEC));
			break;

		default:
			break;
		}

		return units;
	}

	private class CacheManager {
		// registry by unit symbol
		private Map<String, UnitOfMeasure> symbolRegistry = new ConcurrentHashMap<String, UnitOfMeasure>();

		// registry by base symbol
		private Map<String, UnitOfMeasure> baseRegistry = new ConcurrentHashMap<String, UnitOfMeasure>();

		// registry for units by enumeration
		private Map<Unit, UnitOfMeasure> unitRegistry = new ConcurrentHashMap<Unit, UnitOfMeasure>();

		private UnitOfMeasure getUOM(Unit unit) {
			return unitRegistry.get(unit);
		}

		private UnitOfMeasure getUOM(String symbol) {
			return symbolRegistry.get(symbol);
		}

		private UnitOfMeasure getBaseUOM(String baseSymbol) {
			return baseRegistry.get(baseSymbol);
		}

		private void clearCache() {
			symbolRegistry.clear();
			baseRegistry.clear();
			unitRegistry.clear();
		}

		private Collection<UnitOfMeasure> getCachedUnits() {
			return symbolRegistry.values();
		}

		private Map<String, UnitOfMeasure> getSymbolCache() {
			return symbolRegistry;
		}

		private Map<String, UnitOfMeasure> getBaseSymbolCache() {
			return baseRegistry;
		}

		private Map<Unit, UnitOfMeasure> getEnumerationCache() {
			return unitRegistry;
		}

		private void unregisterUnit(UnitOfMeasure uom) throws Exception {
			if (uom.getEnumeration() != null) {
				unitRegistry.remove(uom.getEnumeration());
			}

			// remove by symbol and base symbol
			symbolRegistry.remove(uom.getSymbol());
			baseRegistry.remove(uom.getBaseSymbol());
		}

		private void registerUnit(UnitOfMeasure uom) throws Exception {
			String key = uom.getSymbol();

			// get first by symbol
			UnitOfMeasure current = symbolRegistry.get(key);

			if (current != null) {
				// already cached
				return;
			}

			// cache it
			symbolRegistry.put(key, uom);

			// next by unit enumeration
			Unit id = uom.getEnumeration();

			if (id != null) {
				unitRegistry.put(id, uom);
			}

			// finally by base symbol
			key = uom.getBaseSymbol();

			if (baseRegistry.get(key) == null) {
				baseRegistry.put(key, uom);
			}
		}
	}
}
