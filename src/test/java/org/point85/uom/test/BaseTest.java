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
package org.point85.uom.test;

import java.math.BigDecimal;

import org.point85.uom.Quantity;

public class BaseTest {

	protected static final BigDecimal DELTA6 = Quantity.createAmount("0.000001");
	protected static final BigDecimal DELTA5 = Quantity.createAmount("0.00001");
	protected static final BigDecimal DELTA4 = Quantity.createAmount("0.0001");
	protected static final BigDecimal DELTA3 = Quantity.createAmount("0.001");
	protected static final BigDecimal DELTA2 = Quantity.createAmount("0.01");
	protected static final BigDecimal DELTA1 = Quantity.createAmount("0.1");
	protected static final BigDecimal DELTA0 = Quantity.createAmount("1");

}
