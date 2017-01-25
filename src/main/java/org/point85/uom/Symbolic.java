package org.point85.uom;

/**
 * This class represents a fundamental physical quantity that is invariant, for
 * example "speed of light"
 * 
 * @author Kent Randall
 *
 */
abstract class Symbolic {
	// name, for example "speed of light"
	private String name;

	// symbol or abbreviation, e.g. "Vc"
	private String symbol;

	// description
	private String description;
	
	protected Symbolic() {
		
	}

	protected Symbolic(String name, String symbol, String description) {
		this.name = name;
		this.symbol = symbol;
		this.description = description;
	}
	
	/**
	 * Set the name, symbol and description
	 * @param name Name
	 * @param symbol Symbol
	 * @param description Description
	 */
	public void setId(String name, String symbol, String description) {
		this.name = name;
		this.symbol = symbol;
		this.description = description;
	}

	/**
	 * Get the symbol
	 * 
	 * @return Symbol
	 */
	public String getSymbol() {
		return symbol;
	}
	
	protected void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * Get the name
	 * 
	 * @return Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the description
	 * 
	 * @return Description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Create a String representation
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

		return sb.toString();
	}
}
