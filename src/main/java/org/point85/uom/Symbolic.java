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

/**
 * This class represents an object that is identified by a name and symbol with
 * an optional description. Units of measure are such objects.
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
	 * Get the symbol
	 * 
	 * @return Symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * Set the symbol
	 * 
	 * @param symbol
	 *            Symbol
	 */
	public void setSymbol(String symbol) {
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
	 * Set the name
	 * 
	 * @param name Name
	 */
	public void setName(String name) {
		this.name = name;
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
	 * Set the description
	 * 
	 * @param description
	 *            Description
	 */
	public void setDescription(String description) {
		this.description = description;
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
