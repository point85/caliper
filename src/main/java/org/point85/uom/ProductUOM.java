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

/**
 * ProductUOM represents a unit of measure that is the product of two other
 * units of measure, for example Newton-metres.
 * 
 * @author Kent Randall
 *
 */
public class ProductUOM extends AbstractUnitOfMeasure {

	// left of "times"
	private UnitOfMeasure multiplier;

	// right of "times"
	private UnitOfMeasure multiplicand;

	ProductUOM(UnitType type, String name, String symbol, String description, MeasurementSystem system) {
		super(type, name, symbol, description, system);
	}

	String generateSymbol() {
		StringBuilder sb = new StringBuilder();

		if (getMultiplier().equals(getMultiplicand())) {
			sb.append(getMultiplier().getSymbol()).append(SQ);
		} else {
			sb.append(getMultiplier().getSymbol()).append(MULT).append(getMultiplicand().getSymbol());
		}
		return sb.toString();
	}

	void setUnits(UnitOfMeasure multiplier, UnitOfMeasure multiplicand) throws Exception {
		if (multiplier == null) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("multiplier.cannot.be.null"), getSymbol());
			throw new Exception(msg);
		}

		if (multiplicand == null) {
			String msg = MessageFormat.format(MeasurementSystem.getMessage("multiplicand.cannot.be.null"),
					getSymbol());
			throw new Exception(msg);
		}

		this.setMultiplier(multiplier);
		this.setMultiplicand(multiplicand);
	}

	/**
	 * Get the multiplier
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getMultiplier() {
		return multiplier;
	}

	private void setMultiplier(UnitOfMeasure multiplier) {
		this.multiplier = multiplier;
	}

	/**
	 * Get the multiplicand
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getMultiplicand() {
		return multiplicand;
	}

	private void setMultiplicand(UnitOfMeasure multiplicand) {
		this.multiplicand = multiplicand;
	}
}
