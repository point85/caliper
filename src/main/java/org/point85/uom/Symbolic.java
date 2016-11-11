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
import java.math.MathContext;

/**
 * Symbolic is the base class for named entities
 * 
 * @author Kent Randall
 *
 */
abstract class Symbolic implements Serializable {

	private static final long serialVersionUID = 549468378059268646L;

	// BigDecimal math. A MathContext object with a precision setting matching
	// the IEEE 754R Decimal64 format, 16 digits, and a rounding mode of
	// HALF_EVEN, the IEEE 754R default.
	static final MathContext MATH_CONTEXT = MathContext.DECIMAL64;

	// name, e.g. "kilogram"
	private String name;

	// symbol or abbreviation, e.g. "kg"
	private String symbol;

	// description
	private String description;

	protected Symbolic() {

	}

	protected Symbolic(String name, String symbol, String description) {
		this.setName(name);
		this.setSymbol(symbol);
		this.setDescription(description);
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

	protected void setName(String name) {
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

	protected void setDescription(String description) {
		this.description = description;
	}
}
