package org.point85.uom;

import java.math.BigDecimal;

/**
 * This class represents a fundamental physical quantity that is invariant, for
 * example "speed of light"
 * 
 * @author Kent Randall
 *
 */
public class NamedQuantity extends Quantity {
	// name, for example "speed of light"
	private String name;

	// symbol or abbreviation, e.g. "Vc"
	private String symbol;

	// description
	private String description;

	/**
	 * Construct a named quantity from another quantity
	 * 
	 * @param quantity
	 *            {@link Quantity}
	 */
	public NamedQuantity(final Quantity quantity) {
		super(quantity.getAmount(), quantity.getUOM());
	}

	/**
	 * Construct a named quantity from a decimal amount and unit of measure
	 * 
	 * @param amount
	 *            Amount
	 * @param uom
	 *            {@link UnitOfMeasure}
	 */
	public NamedQuantity(final BigDecimal amount, final UnitOfMeasure uom) {
		super(amount, uom);
	}

	/**
	 * Identity this named quantity
	 * 
	 * @param name
	 *            Name
	 * @param symbol
	 *            Symbol
	 * @param description
	 *            Description
	 */
	public void setId(String name, String symbol, String description) {
		this.name = name;
		this.symbol = symbol;
		this.description = description;
	}

	/**
	 * Get the unit of measure's symbol
	 * 
	 * @return Symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * Get the name of the unit of measure.
	 * 
	 * @return Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the description of the unit of measure.
	 * 
	 * @return Description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Create a String representation of this NamedQuantity
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		// symbol
		String symbol = getSymbol();
		if (symbol != null) {
			sb.append(" (").append(symbol);
		}

		// name
		String name = getName();
		if (name != null) {
			sb.append(", ").append(name);
		}

		// description
		String description = getDescription();
		if (description != null) {
			sb.append(", ").append(description).append(')');
		}

		return super.toString() + sb.toString();
	}
}
