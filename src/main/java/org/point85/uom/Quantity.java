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

import java.math.BigInteger;
import java.util.Objects;

/**
 * The Quantity class represents an amount and {@link UnitOfMeasure}. A constant
 * quantity can be named and given a symbol, e.g. the speed of light.
 * 
 * @author Kent Randall
 *
 */
public class Quantity extends Symbolic {

	// the amount
	private double amount;

	// and its unit of measure
	private UnitOfMeasure uom;

	/**
	 * Default constructor
	 */
	public Quantity() {
		super();
	}

	/**
	 * Create a quantity with an amount and unit of measure
	 * 
	 * @param amount
	 *            Amount
	 * @param uom
	 *            {@link UnitOfMeasure}
	 */
	public Quantity(double amount, UnitOfMeasure uom) {
		this.amount = amount;
		this.uom = uom;
	}

	/**
	 * Create a quantity with an amount, prefix and unit
	 * 
	 * @param amount
	 *            Amount
	 * @param prefix
	 *            {@link Prefix}
	 * @param unit
	 *            {@link Unit}
	 * @throws Exception
	 *             Exception
	 */
	public Quantity(double amount, Prefix prefix, Unit unit) throws Exception {
		this(amount, MeasurementSystem.getSystem().getUOM(prefix, unit));
	}

	/**
	 * Create a quantity with a String amount and unit of measure
	 * 
	 * @param amount
	 *            Amount
	 * @param uom
	 *            {@link UnitOfMeasure}
	 * @throws Exception
	 *             Exception
	 */
	public Quantity(String amount, UnitOfMeasure uom) throws Exception {
		this.amount = createAmount(amount);
		this.uom = uom;
	}

	/**
	 * Create a quantity with an amount and unit
	 * 
	 * @param amount
	 *            Amount
	 * @param unit
	 *            {@link Unit}
	 * @throws Exception
	 *             Exception
	 */
	public Quantity(double amount, Unit unit) throws Exception {
		this(amount, MeasurementSystem.getSystem().getUOM(unit));
	}

	/**
	 * Create a quantity with a String amount and unit
	 * 
	 * @param amount
	 *            Amount
	 * @param unit
	 *            {@link Unit}
	 * @throws Exception
	 *             Exception
	 */
	public Quantity(String amount, Unit unit) throws Exception {
		this(createAmount(amount), MeasurementSystem.getSystem().getUOM(unit));
	}

	/**
	 * Create a hash code
	 * 
	 * @return hash code
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getAmount(), getUOM());
	}

	/**
	 * Compare this Quantity to another one
	 * 
	 * @param other
	 *            Quantity
	 * @return true if equal
	 */
	@Override
	public boolean equals(Object other) {
		boolean answer = false;

		if (other != null && other instanceof Quantity) {
			Quantity otherQuantity = (Quantity) other;

			// same amount and same unit of measure
			if (getAmount() == otherQuantity.getAmount() && getUOM().equals(otherQuantity.getUOM())) {
				answer = true;
			}
		}
		return answer;
	}

	/**
	 * Create an amount of a quantity from a String
	 * 
	 * @param value
	 *            Text value of amount
	 * @return Amount
	 * @throws Exception
	 *             Exception
	 */
	public static double createAmount(String value) throws Exception {
		if (value == null) {
			throw new Exception(MeasurementSystem.getMessage("amount.cannot.be.null"));
		}
		return Double.valueOf(value);
	}

	/**
	 * Create an amount of a quantity that adheres to precision and rounding
	 * settings from a Number
	 * 
	 * @param number
	 *            Value
	 * @return Amount
	 */
	public static double createAmount(Number number) {
		double result = 0d;

		if (number instanceof Double) {
			result = (Double) number;
		} else if (number instanceof BigInteger) {
			result = new Double(((BigInteger) number).doubleValue());
		} else if (number instanceof Float) {
			result = new Double((Float) number);
		} else if (number instanceof Long) {
			result = new Double((Long) number);
		} else if (number instanceof Integer) {
			result = new Double((Integer) number);
		} else if (number instanceof Short) {
			result = new Double((Short) number);
		}

		return result;
	}

	/**
	 * Get the amount of this quantity
	 * 
	 * @return amount
	 */
	public double getAmount() {
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
		double amount = getAmount() - toSubtract.getAmount();
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
		double amount = getAmount() + toAdd.getAmount();
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
		
		if (toDivide.getAmount() == 0.0d) {
			throw new Exception(MeasurementSystem.getMessage("divisor.cannot.be.zero"));
		}

		double amount = getAmount() / toDivide.getAmount();
		UnitOfMeasure newUOM = getUOM().divide(toDivide.getUOM());

		Quantity quantity = new Quantity(amount, newUOM);
		return quantity;
	}

	/**
	 * Divide this quantity by the specified amount
	 * 
	 * @param divisor
	 *            Amount
	 * @return Quantity {@link Quantity}
	 * @throws Exception
	 *             Exception
	 */
	public Quantity divide(double divisor) throws Exception {
		double amount = getAmount() / divisor;
		Quantity quantity = new Quantity(amount, getUOM());
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

		double amount = getAmount() * toMultiply.getAmount();
		UnitOfMeasure newUOM = getUOM().multiply(toMultiply.getUOM());

		Quantity quantity = new Quantity(amount, newUOM);
		return quantity;
	}

	/**
	 * Raise this quantity to the specified power
	 * 
	 * @param exponent
	 *            Exponent
	 * @return new Quantity
	 * @throws Exception
	 *             Exception
	 */
	public Quantity power(int exponent) throws Exception {
		double amount = Math.pow(getAmount(), exponent);
		UnitOfMeasure newUOM = MeasurementSystem.getSystem().createPowerUOM(getUOM(), exponent);

		Quantity quantity = new Quantity(amount, newUOM);
		return quantity;
	}

	/**
	 * Multiply this quantity by the specified amount
	 * 
	 * @param multiplier
	 *            Amount
	 * @return new Quantity
	 * @throws Exception
	 *             Exception
	 */
	public Quantity multiply(double multiplier) throws Exception {
		double amount = getAmount() * multiplier;
		Quantity quantity = new Quantity(amount, getUOM());
		return quantity;
	}

	/**
	 * Invert this quantity, i.e. 1 divided by this quantity to create another
	 * quantity
	 * 
	 * @return {@link Quantity}
	 * @throws Exception
	 *             Exception
	 */
	public Quantity invert() throws Exception {
		double amount = 1.0d / getAmount();
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

		double multiplier = getUOM().getConversionFactor(toUOM);
		double thisOffset = getUOM().getOffset();
		double targetOffset = toUOM.getOffset();

		// adjust for a non-zero "this" offset
		double offsetAmount = getAmount() + thisOffset;

		// new path amount
		double newAmount = offsetAmount * multiplier;

		// adjust for non-zero target offset
		newAmount = newAmount - targetOffset;

		// create the quantity now
		return new Quantity(newAmount, toUOM);
	}

	/**
	 * Convert this quantity with a product or quotient unit of measure to the
	 * specified units of measure.
	 * 
	 * @param uom1
	 *            Multiplier or dividend {@link UnitOfMeasure}
	 * @param uom2
	 *            Multiplicand or divisor {@link UnitOfMeasure}
	 * @return Converted quantity
	 * @throws Exception
	 *             Exception
	 */
	public Quantity convertToPowerProduct(UnitOfMeasure uom1, UnitOfMeasure uom2) throws Exception {
		UnitOfMeasure newUOM = getUOM().clonePowerProduct(uom1, uom2);

		return convert(newUOM);
	}

	/**
	 * Convert this quantity of a power unit using the specified base unit of
	 * measure.
	 * 
	 * @param uom
	 *            Base {@link UnitOfMeasure}
	 * @return Converted quantity
	 * @throws Exception
	 *             exception
	 */
	public Quantity convertToPower(UnitOfMeasure uom) throws Exception {
		UnitOfMeasure newUOM = getUOM().clonePower(uom);

		return convert(newUOM);
	}

	/**
	 * Convert this quantity to the target unit
	 * 
	 * @param unit
	 *            {@link Unit}
	 * @return {@link Quantity}
	 * @throws Exception
	 *             Exception
	 */
	public Quantity convert(Unit unit) throws Exception {
		return convert(MeasurementSystem.getSystem().getUOM(unit));
	}

	/**
	 * Convert this quantity to the target unit with the specified prefix
	 * 
	 * @param prefix
	 *            {@link Prefix}
	 * @param unit
	 *            {@link Unit}
	 * @return {@link Quantity}
	 * @throws Exception
	 *             Exception
	 */
	public Quantity convert(Prefix prefix, Unit unit) throws Exception {
		return convert(MeasurementSystem.getSystem().getUOM(prefix, unit));
	}

	/**
	 * Create a String representation of this Quantity
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getAmount()).append(", [").append(getUOM().toString()).append("] ");
		sb.append(super.toString());
		return sb.toString();
	}

	/**
	 * Compare this quantity to the other quantity
	 * 
	 * @param other
	 *            Quantity
	 * @return -1 if less than, 0 if equal and 1 if greater than
	 * @throws Exception
	 *             If the quantities cannot be compared.
	 */
	public int compare(Quantity other) throws Exception {
		Quantity toCompare = other;

		if (!getUOM().equals(other.getUOM())) {
			// first try converting the units
			toCompare = other.convert(this.getUOM());
		}

		return Double.valueOf(getAmount()).compareTo(Double.valueOf(toCompare.getAmount()));
	}
}
