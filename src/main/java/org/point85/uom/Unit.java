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
	MILLISECOND, SECOND, MINUTE, HOUR, DAY, WEEK, SQUARE_SECOND,
	// angle
	RADIAN, STERADIAN,
	// degree of arc
	DEGREE,
	// ratio
	DECIBEL,

	// SI units follow
	// length
	NANOMETRE, MICROMETRE, MILLIMETRE, CENTIMETRE, METRE, KILOMETRE, ANGSTROM,
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
	JOULE, KILOJOULE, CALORIE, KILOWATT_HOUR,
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
