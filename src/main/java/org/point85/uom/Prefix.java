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
 * The Prefix enumeration defines SI unit of measure prefixes as well as those
 * found in computer science.
 */
public enum Prefix {
	// SI prefix 10^24
	YOTTA("yotta", "Y", "1.0E+24"),
	// SI prefix 10^21
	ZETTA("zetta", "Z", "1.0E+21"),
	// SI prefix 10^18
	EXA("exa", "E", "1.0E+18"),
	// SI prefix 10^15
	PETA("petta", "P", "1.0E+15"),
	// SI prefix 10^12
	TERA("tera", "T", "1.0E+12"),
	// SI prefix 10^9
	GIGA("giga", "G", "1.0E+09"),
	// SI prefix 10^6
	MEGA("mega", "M", "1.0E+06"),
	// SI prefix 10^3
	KILO("kilo", "k", "1.0E+03"),
	// SI prefix 10^2
	HECTO("hecto", "h", "1.0E+02"),
	// SI prefix 10
	DEKA("deka", "da", "1.0E+01"),
	// SI prefix 10^-1
	DECI("deci", "d", "1.0E-01"),
	// SI prefix 10^-2
	CENTI("centi", "c", "1.0E-02"),
	// SI prefix 10^-3
	MILLI("milli", "m", "1.0E-03"),
	// SI prefix 10^-6
	MICRO("micro", "u", "1.0E-06"),
	// SI prefix 10^-9
	NANO("nano", "n", "1.0E-09"),
	// SI prefix 10^-12
	PICO("pico", "p", "1.0E-12"),
	// SI prefix 10^-15
	FEMTO("femto", "f", "1.0E-15"),
	// SI prefix 10^-18
	ATTO("atto", "a", "1.0E-18"),
	// SI prefix 10^-21
	ZEPTO("zepto", "z", "1.0E-21"),
	// SI prefix 10^-24
	YOCTO("yocto", "y", "1.0E-24"),

	// Digital information prefixes for bytes established by the International
	// Electrotechnical Commission (IEC) in 1998
	KIBI("kibi", "Ki", "1024"),
	//
	MEBI("mebi", "Mi", "1.048576E+06"),
	//
	GIBI("gibi", "Gi", "1.073741824E+09");

	// name
	private String prefixName;

	// symbol
	private String symbol;

	// BigDecimal factor
	private BigDecimal decimalFactor;

	private Prefix(String prefixName, String symbol, String factor) {
		this.prefixName = prefixName;
		this.symbol = symbol;
		this.decimalFactor = new BigDecimal(factor);
	}

	/**
	 * Get the name of the prefix
	 * 
	 * @return prefix name
	 */
	public String getPrefixName() {
		return this.prefixName;
	}

	/**
	 * Get the symbol for the prefix
	 * 
	 * @return symbol
	 */
	public String getSymbol() {
		return this.symbol;
	}

	/**
	 * Get the scaling factor
	 * 
	 * @return Scaling factor
	 */
	public BigDecimal getScalingFactor() {
		return decimalFactor;
	}

	/**
	 * Create a String representation of this Prefix
	 */
	@Override
	public String toString() {
		return prefixName + ", " + symbol + ", " + decimalFactor;
	}
}
