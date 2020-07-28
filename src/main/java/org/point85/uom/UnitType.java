/*
MIT License

Copyright (c) 2018 Kent Randall

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

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.Map;

/**
 * UnitType is an enumeration of unit of measure types. Only units of measure
 * with the same type can be converted.
 * 
 * @author Kent Randall
 *
 */
public enum UnitType {
	// dimension-less "1"
	UNITY(),

	// fundamental
	LENGTH(), MASS(), TIME(), ELECTRIC_CURRENT(), TEMPERATURE(), SUBSTANCE_AMOUNT(), LUMINOSITY(),

	// currency
	CURRENCY(),

	// computer science
	COMPUTER_SCIENCE(),

	// other physical
	AREA(new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, 2)),
	VOLUME(new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, 3)),
	DENSITY(new SimpleImmutableEntry<UnitType, Integer>(UnitType.MASS, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, -3)),
	VELOCITY(new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -1)),
	VOLUMETRIC_FLOW(new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, 3),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -1)),
	MASS_FLOW(new SimpleImmutableEntry<UnitType, Integer>(UnitType.MASS, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -1)),
	FREQUENCY(new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -1)),
	ACCELERATION(new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -2)),
	FORCE(new SimpleImmutableEntry<UnitType, Integer>(UnitType.MASS, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -2)),
	PRESSURE(new SimpleImmutableEntry<UnitType, Integer>(UnitType.MASS, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, -1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -2)),
	ENERGY(new SimpleImmutableEntry<UnitType, Integer>(UnitType.MASS, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, 2),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -2)),
	POWER(new SimpleImmutableEntry<UnitType, Integer>(UnitType.MASS, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, 2),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -3)),
	ELECTRIC_CHARGE(new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.ELECTRIC_CURRENT, 1)),
	ELECTROMOTIVE_FORCE(new SimpleImmutableEntry<UnitType, Integer>(UnitType.MASS, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, 2),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.ELECTRIC_CURRENT, -1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -3)),
	ELECTRIC_RESISTANCE(new SimpleImmutableEntry<UnitType, Integer>(UnitType.MASS, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, -3),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.ELECTRIC_CURRENT, 2),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, 4)),
	ELECTRIC_CAPACITANCE(new SimpleImmutableEntry<UnitType, Integer>(UnitType.MASS, -1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, 2),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.ELECTRIC_CURRENT, -2),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -3)),
	ELECTRIC_PERMITTIVITY(new SimpleImmutableEntry<UnitType, Integer>(UnitType.MASS, -1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, -3),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.ELECTRIC_CURRENT, 2),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, 4)),
	ELECTRIC_FIELD_STRENGTH(new SimpleImmutableEntry<UnitType, Integer>(UnitType.ELECTRIC_CURRENT, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, -1)),
	MAGNETIC_FLUX(new SimpleImmutableEntry<UnitType, Integer>(UnitType.MASS, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, 2),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.ELECTRIC_CURRENT, -1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -2)),
	MAGNETIC_FLUX_DENSITY(new SimpleImmutableEntry<UnitType, Integer>(UnitType.MASS, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.ELECTRIC_CURRENT, -1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -2)),
	ELECTRIC_INDUCTANCE(new SimpleImmutableEntry<UnitType, Integer>(UnitType.MASS, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, 2),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.ELECTRIC_CURRENT, -2),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -2)),
	ELECTRIC_CONDUCTANCE(new SimpleImmutableEntry<UnitType, Integer>(UnitType.MASS, -1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, -2),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.ELECTRIC_CURRENT, 2),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, 3)),
	LUMINOUS_FLUX(new SimpleImmutableEntry<UnitType, Integer>(UnitType.LUMINOSITY, 1)),
	ILLUMINANCE(new SimpleImmutableEntry<UnitType, Integer>(UnitType.LUMINOSITY, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, -2)),
	RADIATION_DOSE_ABSORBED(new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, 2),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -2)),
	RADIATION_DOSE_EFFECTIVE(new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, 2),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -2)),
	RADIATION_DOSE_RATE(new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, 2),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -3)),
	RADIOACTIVITY(new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -1)),
	CATALYTIC_ACTIVITY(new SimpleImmutableEntry<UnitType, Integer>(UnitType.SUBSTANCE_AMOUNT, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -1)),
	DYNAMIC_VISCOSITY(new SimpleImmutableEntry<UnitType, Integer>(UnitType.MASS, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -1)),
	KINEMATIC_VISCOSITY(new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, 2),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -1)),
	RECIPROCAL_LENGTH(new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, -1)), PLANE_ANGLE(), SOLID_ANGLE(),
	INTENSITY(), TIME_SQUARED(new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, 2)),
	MOLAR_CONCENTRATION(new SimpleImmutableEntry<UnitType, Integer>(UnitType.SUBSTANCE_AMOUNT, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.LENGTH, -3)),
	IRRADIANCE(new SimpleImmutableEntry<UnitType, Integer>(UnitType.MASS, 1),
			new SimpleImmutableEntry<UnitType, Integer>(UnitType.TIME, -3)),

	// unclassified. Reserved for use when creating custom units of measure.
	UNCLASSIFIED();

	private Map<UnitType, Integer> typeMap = new HashMap<>();

	@SafeVarargs
	private UnitType(SimpleImmutableEntry<UnitType, Integer>... entries) {
		for (SimpleImmutableEntry<UnitType, Integer> entry : entries) {
			typeMap.put(entry.getKey(), entry.getValue());
		}
	}

	public Map<UnitType, Integer> getTypeMap() {
		return this.typeMap;
	}
}
