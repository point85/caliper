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
 * Quantity represents an amount and {@link UnitOfMeasure}.
 * The amount is expressed as a BigDecimal to control the precision of floating
 * point arithmetic.
 * 
 * @author Kent Randall
 *
 */
public class Quantity {

	// the amount
	private BigDecimal amount;

	// and its unit of measure
	private UnitOfMeasure uom;

	/**
	 * Create a quantity with an amount and unit of measure
	 * 
	 * @param amount
	 *            Amount
	 * @param uom
	 *            {@link UnitOfMeasure}
	 */
	public Quantity(BigDecimal amount, UnitOfMeasure uom) {
		this.amount = amount;
		this.uom = uom;
	}

	/**
	 * Create a quantity with an amount and unit of measure
	 * 
	 * @param amount
	 *            Amount
	 * @param uom
	 *            {@link UnitOfMeasure}
	 */
	public Quantity(String amount, UnitOfMeasure uom) {
		this.amount = createAmount(amount);
		this.uom = uom;
	}

	@Override
	public int hashCode() {
		return getAmount().hashCode() ^ getUOM().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		boolean answer = false;

		if (other != null && other instanceof Quantity) {
			Quantity otherQuantity = (Quantity) other;

			// same amount and same unit of measure
			if (getAmount().equals(otherQuantity.getAmount()) && getUOM().equals(otherQuantity.getUOM())) {
				answer = true;
			}
		}
		return answer;
	}

	/**
	 * Create an amount of a quantity that adheres to precision and rounding
	 * settings
	 * 
	 * @param value
	 *            Text value of amount
	 * @return Amount
	 */
	public static BigDecimal createAmount(String value) {
		// use String constructor for exact precision with rounding mode in math
		// context
		return new BigDecimal(value, UnitOfMeasure.MATH_CONTEXT);
	}

	/**
	 * Create an amount by dividing two amounts represented by strings
	 * 
	 * @param dividendAmount
	 *            Dividend
	 * @param divisorAmount
	 *            Divisor
	 * @return Ratio of two amounts
	 */
	static public BigDecimal divide(String dividendAmount, String divisorAmount) {
		BigDecimal dividend = Quantity.createAmount(dividendAmount);
		BigDecimal divisor = Quantity.createAmount(divisorAmount);
		return dividend.divide(divisor, UnitOfMeasure.MATH_CONTEXT);
	}

	/**
	 * Create an amount by multiplying two amounts represented by strings
	 * 
	 * @param multiplierAmount
	 *            Multiplier
	 * @param multiplicandAmount
	 *            Multiplicand
	 * @return Product of two amounts
	 */
	static public BigDecimal multiply(String multiplierAmount, String multiplicandAmount) {
		BigDecimal multiplier = Quantity.createAmount(multiplierAmount);
		BigDecimal multiplicand = Quantity.createAmount(multiplicandAmount);
		return multiplier.multiply(multiplicand, UnitOfMeasure.MATH_CONTEXT);
	}

	/**
	 * Get the amount of this quantity
	 * 
	 * @return amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * Get the unit of measure of this quantity
	 * 
	 * @return {@link UnitOfMeasure}
	 */
	public UnitOfMeasure getUOM() {
		return uom;
	}

	/**
	 * Subtract a quantity from this quantity
	 * 
	 * @param other
	 *            quantity
	 * @return New quantity
	 * @throws Exception
	 *             Exception
	 */
	public Quantity subtract(Quantity other) throws Exception {
		Quantity toSubtract = other.convert(getUOM());
		BigDecimal amount = getAmount().subtract(toSubtract.getAmount(), UnitOfMeasure.MATH_CONTEXT);
		Quantity quantity = new Quantity(amount, this.getUOM());
		return quantity;
	}

	/**
	 * Add two quantities
	 * 
	 * @param other
	 *            {@link Quantity}
	 * @return Sum {@link Quantity}
	 * @throws Exception
	 *             Exception
	 */
	public Quantity add(Quantity other) throws Exception {
		Quantity toAdd = other.convert(getUOM());
		BigDecimal amount = getAmount().add(toAdd.getAmount(), UnitOfMeasure.MATH_CONTEXT);
		Quantity quantity = new Quantity(amount, this.getUOM());
		return quantity;
	}

	/**
	 * Divide two quantities to create a third quantity
	 * 
	 * @param other
	 *            {@link Quantity}
	 * @return Quotient {@link Quantity}
	 * @throws Exception
	 *             Exception
	 */
	public Quantity divide(Quantity other) throws Exception {
		Quantity toDivide = other;

		BigDecimal amount = getAmount().divide(toDivide.getAmount(), UnitOfMeasure.MATH_CONTEXT);
		UnitOfMeasure uom = getUOM().divide(toDivide.getUOM());

		Quantity quantity = new Quantity(amount, uom);
		return quantity;
	}

	/**
	 * Multiply this quantity by another quantity to create a third quantity
	 * 
	 * @param other
	 *            Quantity
	 * @return Multiplied quantity
	 * @throws Exception
	 *             Exception
	 */
	public Quantity multiply(Quantity other) throws Exception {
		Quantity toMultiply = other;

		BigDecimal amount = getAmount().multiply(toMultiply.getAmount(), UnitOfMeasure.MATH_CONTEXT);
		UnitOfMeasure uom = getUOM().multiply(toMultiply.getUOM());

		Quantity quantity = new Quantity(amount, uom);
		return quantity;
	}

	/**
	 * Invert this quantity, i.e. 1/quantity to create another quantity
	 * 
	 * @return {@link Quantity}
	 * @throws Exception
	 *             Exception
	 */
	public Quantity invert() throws Exception {
		BigDecimal amount = BigDecimal.ONE.divide(this.getAmount(), UnitOfMeasure.MATH_CONTEXT);
		UnitOfMeasure uom = getUOM().invert();

		Quantity quantity = new Quantity(amount, uom);
		return quantity;
	}

	/**
	 * Convert this quantity to the target UOM
	 * 
	 * @param toUOM
	 *            {@link UnitOfMeasure}
	 * @return Converted quantity
	 * @throws Exception
	 *             Exception
	 */
	public Quantity convert(UnitOfMeasure toUOM) throws Exception {
		if (getUOM().equals(toUOM)) {
			return this;
		}

		BigDecimal multiplier = getUOM().getConversionFactor(toUOM);
		BigDecimal thisOffset = getUOM().getOffset();
		BigDecimal targetOffset = toUOM.getOffset();

		// adjust for a non-zero "this" offset
		BigDecimal offsetAmount = getAmount();
		if (thisOffset.compareTo(BigDecimal.ZERO) != 0) {
			offsetAmount = getAmount().add(thisOffset, UnitOfMeasure.MATH_CONTEXT);
		}

		// new path amount
		BigDecimal newAmount = offsetAmount.multiply(multiplier, UnitOfMeasure.MATH_CONTEXT);

		// adjust for non-zero target offset
		if (targetOffset.compareTo(BigDecimal.ZERO) != 0) {
			newAmount = newAmount.subtract(targetOffset, UnitOfMeasure.MATH_CONTEXT);
		}

		// create the quantity now
		return new Quantity(newAmount, toUOM);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getAmount()).append(", [").append(getUOM().toString()).append(']');
		return sb.toString();
	}

	/**
	 * Get the precision and rounding specification for amounts
	 * 
	 * @return Math context
	 */
	public static MathContext getMathContext() {
		return UnitOfMeasure.MATH_CONTEXT;
	}
}
