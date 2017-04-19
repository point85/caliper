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

/**
 * A Conversion linearly relates an ordinate unit (y) to an abscissa unit (x) by
 * the equation y = ax + b. Here 'a' is the slope or scaling factor (a.k.a.
 * constant of proportionality) and 'b' is the y-intercept (offset). For
 * example, 1 km = 1000m, factor (a) = 1000 and offset (b) = 0; 1 C = 1K -
 * 273.15, 'a' = 1 and offset (b) = -273.15.
 * 
 * @author Kent Randall
 *
 */
public class Conversion {

	// scaling factor (a)
	private BigDecimal scalingFactor = BigDecimal.ONE;

	// offset (b)
	private BigDecimal offset = BigDecimal.ZERO;

	// x-axis unit
	private UnitOfMeasure abscissaUnit;

	// database primary key
	private Integer primaryKey;

	/**
	 * Construct a default unit of measure conversion
	 */
	public Conversion() {

	}

	/**
	 * Construct a conversion with a scaling factor of 1 and offset of 0 for the
	 * specified abscissa unit of measure.
	 * 
	 * @param abscissaUnit
	 *            {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public Conversion(UnitOfMeasure abscissaUnit) throws Exception {
		if (abscissaUnit == null) {
			throw new Exception(MeasurementSystem.getMessage("unit.cannot.be.null"));
		}
		this.abscissaUnit = abscissaUnit;
	}

	/**
	 * Construct a conversion with an offset of 0 for the specified scaling
	 * factor and abscissa unit of measure.
	 * 
	 * @param scalingFactor
	 *            Factor
	 * @param abscissaUnit
	 *            {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public Conversion(BigDecimal scalingFactor, UnitOfMeasure abscissaUnit) throws Exception {
		this(abscissaUnit);

		if (scalingFactor == null) {
			throw new Exception(MeasurementSystem.getMessage("factor.cannot.be.null"));
		}
		this.scalingFactor = scalingFactor;
	}

	/**
	 * Construct a conversion with the specified scaling factor, abscissa unit
	 * of measure and scaling factor.
	 * 
	 * @param scalingFactor
	 *            Factor
	 * @param abscissaUnit
	 *            {@link UnitOfMeasure}
	 * @param offset
	 *            Offset
	 * @throws Exception
	 *             Exception
	 */
	public Conversion(BigDecimal scalingFactor, UnitOfMeasure abscissaUnit, BigDecimal offset) throws Exception {
		this(scalingFactor, abscissaUnit);

		if (offset == null) {
			throw new Exception(MeasurementSystem.getMessage("offset.cannot.be.null"));
		}
		this.offset = offset;
	}

	/**
	 * Get the 'x' axis unit
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getAbscissaUnit() {
		return this.abscissaUnit;
	}

	public void setAbscissaUnit(UnitOfMeasure abscissaUnit) {
		this.abscissaUnit = abscissaUnit;
	}

	/**
	 * Get the offset (y-intercept)
	 * 
	 * @return Offset
	 */
	public BigDecimal getOffset() {
		return this.offset;
	}

	/**
	 * Set the offset (y-intercept)
	 * 
	 * @param offset
	 *            Offset
	 */
	public void setOffset(BigDecimal offset) {
		this.offset = offset;
	}

	/**
	 * Get the scaling factor (slope)
	 * 
	 * @return Factor
	 */
	public BigDecimal getScalingFactor() {
		return this.scalingFactor;
	}

	/**
	 * Set the scaling factor (slope)
	 * 
	 * @param scalingFactor
	 *            Scaling factor
	 */
	public void setScalingFactor(BigDecimal scalingFactor) {
		this.scalingFactor = scalingFactor;
	}

	/**
	 * Get the database record's primary key
	 * 
	 * @return Key
	 */
	public Integer getKey() {
		return primaryKey;
	}

	/**
	 * Set the database record's primary key
	 * 
	 * @param key
	 *            Key
	 */
	public void setKey(Integer key) {
		this.primaryKey = key;
	}
}
