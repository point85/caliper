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
 * Customary, SI, US and British Imperial systems.
 * 
 * @author Kent Randall
 *
 */
public enum Unit {
	// common units
	// dimension-less "1" or unity
	ONE,
	// time
	MILLISECOND, SECOND, MINUTE, HOUR, DAY, WEEK, JULIAN_YEAR, SQUARE_SECOND,
	// angle
	RADIAN, STERADIAN,
	// degree of arc
	DEGREE,
	// ratio
	DECIBEL,

	// SI units follow
	// length
	NANOMETRE, MICROMETRE, MILLIMETRE, CENTIMETRE, METRE, KILOMETRE, ANGSTROM, LIGHT_YEAR, DIOPTER,
	// area
	SQUARE_METRE,
	// temperature
	KELVIN, CELSIUS,
	// mass
	MICROGRAM, MILLIGRAM, GRAM, KILOGRAM, CARAT, TONNE,
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
	JOULE, KILOJOULE, ELECTRON_VOLT, CALORIE, KILOWATT_HOUR,
	// force
	NEWTON,
	// power
	WATT, KILOWATT,
	// frequency
	HERTZ, RAD_PER_SEC,
	// pressure
	PASCAL, KILOPASCAL, BAR,
	// electrical
	AMPERE, COULOMB, ELEMENTARY_CHARGE, VOLT, OHM, FARAD, WEBER, TESLA, HENRY, SIEMENS,
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
	// IT
	BIT, BYTE,

	// Customary Units follow
	// length
	INCH, FOOT, YARD, MILE, NAUTICAL_MILE, FATHOM, MIL, POINT,
	// temperature
	FAHRENHEIT, RANKINE,
	// mass
	POUND_MASS, OUNCE, SLUG, GRAIN,
	// force
	POUND_FORCE,
	// torque (moment of force)
	FOOT_POUND_FORCE,
	// area
	SQUARE_INCH, SQUARE_FOOT, SQUARE_YARD, ACRE,
	// volume
	CUBIC_INCH, CUBIC_FOOT, CUBIC_YARD, CORD,
	// velocity
	FEET_PER_SECOND, KNOT, MILES_PER_HOUR, 
	// frequency
	REV_PER_MIN,
	// acceleration
	FEET_PER_SECOND_SQUARED,
	// power
	HP,
	// energy
	BTU,
	// pressure
	PSI, IN_HG,

	// US Units follow
	// volume
	US_TEASPOON, US_TABLESPOON, US_FLUID_OUNCE, US_CUP, US_PINT, US_QUART, US_GALLON, US_BARREL, US_BUSHEL, 
	// mass
	US_TON,

	// British units follow
	// volume
	BR_TEASPOON, BR_TABLESPOON, BR_FLUID_OUNCE, BR_CUP, BR_PINT, BR_QUART, BR_GALLON, BR_BUSHEL, 
	// mass
	BR_TON

}
