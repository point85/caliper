package org.point85.uom;

/**
 * Unit is an enumeration of units of measure types in the International
 * Customary, SI, US and British Imperial systems
 * 
 * @author Kent Randall
 *
 */
public enum Unit implements UnitEnumeration {
	// common units
	// dimension-less "1" or unity
	ONE,
	// time
	MILLISECOND, SECOND, MINUTE, HOUR, DAY, SQUARE_SECOND,
	// angle
	RADIAN, STERADIAN,
	// degree of arc
	DEGREE,
	// ratio
	DECIBEL,

	// SI units follow
	// length
	NANOMETRE, MICROMETRE, MILLIMETRE, CENTIMETRE, METRE, KILOMETRE,
	// area
	SQUARE_METRE,
	// temperature
	KELVIN, CELSIUS,
	// mass
	MICROGRAM, MILLIGRAM, GRAM, KILOGRAM, CARAT,
	// volume
	CUBIC_METRE, MILLILITRE, CENTILITRE, DECILITRE, LITRE,
	// volumetric flow
	CUBIC_METRE_PER_SECOND,
	// mass flow
	KILOGRAM_PER_SECOND,
	// viscosity
	PASCAL_SECOND, SQUARE_METRE_PER_SECOND,
	// velocity
	METRE_PER_SECOND,
	// acceleration
	METRE_PER_SECOND_SQUARED,
	// energy
	JOULE, KILOJOULE, CALORIE,
	// force
	NEWTON,
	// power
	WATT, KILOWATT,
	// frequency
	HERTZ,
	// pressure
	PASCAL, KILOPASCAL, BAR,
	// electrical
	AMPERE, COULOMB, VOLT, OHM, FARAD, WEBER, TESLA, HENRY, SIEMENS,
	// substance
	MOLE,
	// luminosity
	CANDELA, LUMEN, LUX,
	// radioactivity
	BECQUEREL, GRAY, SIEVERT,
	// catalytic activity
	KATAL,
	// density
	KILOGRAM_PER_CUBIC_METRE,
	// torque (moment of force and energy)
	NEWTON_METRE,
	// constants
	GRAVITY, LIGHT_VELOCITY,

	// Customary Units follow
	// length
	INCH, FOOT, YARD, MILE, NAUTICAL_MILE,
	// temperature
	FAHRENHEIT, RANKINE,
	// mass
	POUND_MASS, OUNCE, SLUG, GRAIN,
	// force
	POUND_FORCE,
	// torque (moment of force)
	FOOT_POUND_FORCE,
	// area
	SQUARE_INCH, SQUARE_FOOT, ACRE,
	// volume
	CUBIC_INCH, CUBIC_FOOT,
	// velocity
	FEET_PER_SECOND, KNOT, MILES_PER_HOUR,
	// acceleration
	FEET_PER_SECOND_SQUARED,
	// power
	HP,
	// energy
	BTU,
	// pressure
	PSI,

	// US Units follow
	// volume
	US_TEASPOON, US_TABLESPOON, US_FLUID_OUNCE, US_CUP, US_PINT, US_QUART, US_GALLON,

	// British units follow
	// volume
	BR_TEASPOON, BR_TABLESPOON, BR_FLUID_OUNCE, BR_CUP, BR_PINT, BR_QUART, BR_GALLON

}
