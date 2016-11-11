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
 * UnitType is an enumeration of unit of measure types.
 * 
 * @author Kent Randall
 *
 */
public enum UnitType {
	// dimension-less "1"
	UNITY,
	// SI fundamental
	LENGTH, MASS, TIME, ELECTRIC_CURRENT, TEMPERATURE, SUBSTANCE_AMOUNT, LUMINOSITY,
	// derived
	AREA, VOLUME, DENSITY, VELOCITY, VOLUMETRIC_FLOW, MASS_FLOW, FREQUENCY, ACCELERATION, FORCE, PRESSURE, ENERGY, POWER, ELECTRIC_CHARGE, ELECTROMOTIVE_FORCE, ELECTRICAL_RESISTANCE, CAPACITANCE, MAGNETIC_FLUX, MAGNETIC_FLUX_DENSITY, INDUCTANCE, ELECTRICAL_CONDUCTANCE, LUMINOUS_FLUX, ILLUMINANCE, RADIATION_DOSE, CATALYTIC_ACTIVITY, DYNAMIC_VISCOSITY, KINEMATIC_VISCOSITY,
	// angle
	PLANE_ANGLE, SOLID_ANGLE,
	// intensity (power)
	INTENSITY,
	// other custom
	CUSTOM;
}
