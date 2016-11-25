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
import java.math.MathContext;

/**
 * UnitOfMeasure is the interface for a unit of measure.
 * 
 * @author Kent Randall
 *
 */
public interface UnitOfMeasure {

	// BigDecimal math. A MathContext object with a precision setting matching
	// the IEEE 754R Decimal64 format, 16 digits, and a rounding mode of
	// HALF_EVEN, the IEEE 754R default.
	static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;

	/**
	 * Get the unit of measure's symbol
	 * 
	 * @return Symbol
	 */
	String getSymbol();

	/**
	 * Get the unit of measure's symbol in the fundamental units for that
	 * system. For example a Newton is a kg.m/s2.
	 * 
	 * @return Base symbol
	 * @throws Exception
	 *             Exception
	 */
	String getBaseSymbol() throws Exception;

	/**
	 * Get the base of the power unit of measure. For example square metres the
	 * base is metre.
	 * 
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	UnitOfMeasure getBaseUOM() throws Exception;

	/**
	 * Get the name of the unit of measure.
	 * 
	 * @return Name
	 */
	String getName();

	/**
	 * Get the description of the unit of measure.
	 * 
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
	 * Get the system that owns the unit of measure
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
	 * @throws Exception
	 *             Exception
	 */
	UnitOfMeasure multiply(UnitOfMeasure other) throws Exception;

	/**
	 * Divide two units of measure to create a third one.
	 * 
	 * @param other
	 *            {@link UnitOfMeasure}
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	UnitOfMeasure divide(UnitOfMeasure other) throws Exception;

	/**
	 * Invert a unit of measure to create a new one
	 * 
	 * @return {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	UnitOfMeasure invert() throws Exception;

	/**
	 * Get the conversion between the international customary and corresponding
	 * SI unit. For example feet to metres.
	 * 
	 * @return {@link Conversion}
	 */
	Conversion getBridge();

	/**
	 * Specify a conversion to a unit of measure in a different system, for
	 * example International Customary to SI
	 * 
	 * @param toUOM
	 *            Target {@link UnitOfMeasure}
	 * @param factor
	 *            Conversion factor
	 */

	/**
	 * Set the conversion between the international customary and corresponding
	 * SI unit. For example feet to metres.
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
	 * Get the unit of measure's x-axis unit of measure for the relation y = ax
	 * + b.
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	UnitOfMeasure getAbscissaUnit();

	/**
	 * Get the unit of measure's 'a' factor (slope) for the relation y = ax + b.
	 * 
	 * @return Factor
	 */
	BigDecimal getScalingFactor();

	/**
	 * Set the unit of measure's 'a' factor (slope) for the relation y = ax + b.
	 * 
	 * @param factor
	 *            Scaling factor
	 */
	void setScalingFactor(BigDecimal factor);

	/**
	 * Get the unit of measure's 'b' offset (intercept) for the relation y = ax
	 * + b.
	 * 
	 * @return Offset
	 */
	BigDecimal getOffset();

	/**
	 * Set the unit of measure's 'b' offset (intercept) for the relation y = ax
	 * + b.
	 * 
	 * @param offset
	 *            Offset
	 */
	void setOffset(BigDecimal offset);

	/**
	 * Get the factor to convert to the unit of measure
	 * 
	 * @param toUOM
	 *            Target {@link UnitOfMeasure}
	 * @return conversion factor
	 * @throws Exception
	 *             Exception
	 */
	BigDecimal getConversionFactor(UnitOfMeasure toUOM) throws Exception;

	/**
	 * Set the conversion to another unit of measure
	 * 
	 * @param conversion
	 *            {@link Conversion}
	 */
	void setConversion(Conversion conversion);

	/**
	 * Get the conversion to another unit of measure
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
	 * Get the Unified Code for Unit of Measure Conversion's (UCUM) symbol
	 * 
	 * @return Symbol
	 */
	String getUnifiedSymbol();
}
