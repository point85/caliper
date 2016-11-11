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

import java.io.Serializable;
import java.text.MessageFormat;

/**
 * The PowerUOM represents a unit of measure with an exponent, for example
 * square metres.
 * 
 * @author Kent Randall
 *
 */
public class PowerUOM extends AbstractUnitOfMeasure implements Serializable {

	private static final long serialVersionUID = 2233946797577576713L;

	// exponent, e.g. "2"
	private int power = 0;

	PowerUOM(UnitType type, String name, String symbol, String description, MeasurementSystem system) {
		super(type, name, symbol, description, system);
	}

	void setUnits(UnitOfMeasure base, int power) throws Exception {
		if (base == null) {
			String msg = MessageFormat.format(MeasurementService.getMessage("base.cannot.be.null"), getSymbol());
			throw new Exception(msg);
		}

		this.setBase(base);
		this.setPower(power);
	}

	/**
	 * Get the exponent
	 * 
	 * @return Exponent
	 */
	public int getPower() {
		return power;
	}

	private void setPower(int power) {
		this.power = power;
	}

	/**
	 * Get the base unit of measure
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getBase() {
		return uom1;
	}

	private void setBase(UnitOfMeasure base) {
		this.uom1 = base;
	}
}
