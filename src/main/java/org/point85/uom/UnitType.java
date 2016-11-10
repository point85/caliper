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