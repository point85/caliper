package org.point85.uom;

import java.math.BigDecimal;

/**
 * UnitOfMeasure is the interface for a unit Of measure.
 * 
 * @author Kent Randall
 *
 */
public interface UnitOfMeasure {

	String getSymbol();

	/**
	 * Get the unit of measure's symbol in the fundamental units for that
	 * system. For example a Newton is a kg.m/s2.
	 * @return Base symbol
	 * @throws Exception Exception
	 */
	String getBaseSymbol() throws Exception;

	/**
	 * Get the base of the power unit of measure. For example square metres the
	 * base is metre.
	 * 
	 * @return {@link UnitOfMeasure}
	 * @throws Exception Exception
	 */
	UnitOfMeasure getBaseUOM() throws Exception;

	/**
	 * Get the name of the unit of measure.
	 * @return Name
	 */
	String getName();

	/**
	 * Get the description of the unit of measure.
	 * @return Description
	 */
	String getDescription();

	/**
	 * Get the type of the unit.
	 * 
	 * @return {@link UnitType}
	 */
	UnitType getUnitType();

	/**
	 * Get the system that the unit of measure was created in
	 * 
	 * @return {@link MeasurementSystem}
	 */
	MeasurementSystem getMeasurementSystem();

	/**
	 * Multiply two units of measure to create a third one.
	 * 
	 * @param other
	 *            {@link UnitOfMeasure}
	 * @return {@link UnitOfMeasure}
	 * @throws Exception Exception
	 */
	UnitOfMeasure multiply(UnitOfMeasure other) throws Exception;

	/**
	 * Divide two units of measure to create a third one.
	 * 
	 * @param other
	 *            {@link UnitOfMeasure}
	 * @return {@link UnitOfMeasure}
	 * @throws Exception Exception
	 */
	UnitOfMeasure divide(UnitOfMeasure other) throws Exception;

	/**
	 * Invert a unit of measure to create a new one
	 * 
	 * @return {@link UnitOfMeasure}
	 * @throws Exception Exception
	 */
	UnitOfMeasure invert() throws Exception;

	/**
	 * Get the conversion between the international customary and corresponding
	 * SI unit, e.g. feet to metres.
	 * 
	 * @return {@link Conversion}
	 */
	Conversion getBridge();

	/**
	 * Specify a conversion to a unit of measure in a different system, e.g.
	 * International Customary to SI
	 * 
	 * @param toUOM
	 *            Target {@link UnitOfMeasure}
	 * @param factor
	 *            Conversion factor
	 */

	/**
	 * Set the conversion between the international customary and corresponding
	 * SI unit, e.g. feet to metres.
	 * 
	 * @param conversion
	 *            {@link Conversion}
	 */
	void setBridge(Conversion conversion);

	/**
	 * Get the unit's enumerated type
	 * 
	 * @return {@link UnitEnumeration}
	 */
	UnitEnumeration getEnumeration();

	/**
	 * Set the unit's enumerated type
	 * 
	 * @param unitEnumeration
	 *            {@link UnitEnumeration}
	 */
	void setEnumeration(UnitEnumeration unitEnumeration);

	/**
	 * Get the y-axis unit of measure's x-axis unit of measure for the relation
	 * y = ax + b.
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	UnitOfMeasure getAbscissaUnit();

	/**
	 * Get the y-axis unit of measure's 'a' factor (slope) for the relation y =
	 * ax + b.
	 * 
	 * @return Factor
	 */
	BigDecimal getScalingFactor();

	/**
	 * Set the y-axis unit of measure's 'a' factor (slope) for the relation y =
	 * ax + b.
	 * 
	 * @param factor Scaling factor
	 */
	void setScalingFactor(BigDecimal factor);

	/**
	 * Get the y-axis unit of measure's 'b' offset (intercept) for the relation
	 * y = ax + b.
	 * 
	 * @return Offset
	 */
	BigDecimal getOffset();

	/**
	 * Set the y-axis unit of measure's 'b' offset (intercept) for the relation
	 * y = ax + b.
	 * 
	 * @param offset Offset
	 */
	void setOffset(BigDecimal offset);

	/**
	 * Get the factor to convert to the unit of measure
	 * 
	 * @param toUOM
	 *            Target {@link UnitOfMeasure}
	 * @return conversion factor
	 * @throws Exception Exception
	 */
	BigDecimal getConversionFactor(UnitOfMeasure toUOM) throws Exception;

	/**
	 * Set the conversion to another unit of measure and conversion factor
	 * 
	 * @param conversion
	 *            {@link Conversion}
	 */
	void setConversion(Conversion conversion);

	/**
	 * Get the conversion to another unit of measure and conversion factor
	 * 
	 * @return {@link Conversion}
	 */
	Conversion getConversion();

	/**
	 * Set the Unified Code for Unit of Measure Conversion (UCUM)
	 * 
	 * @param unifiedSymbol
	 *            UCUM symbol
	 */
	void setUnifiedSymbol(String unifiedSymbol);

	/**
	 * Get the Unified Code for Unit of Measure Conversion (UCUM)
	 * 
	 * @return Symbol
	 */
	String getUnifiedSymbol();
}