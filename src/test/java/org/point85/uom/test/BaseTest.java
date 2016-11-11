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

import org.junit.Before;
import org.point85.uom.MeasurementService;

public class BaseTest {

	protected static final BigDecimal DELTA6 = new BigDecimal("0.000001");
	protected static final BigDecimal DELTA5 = new BigDecimal("0.00001");
	protected static final BigDecimal DELTA4 = new BigDecimal("0.0001");
	protected static final BigDecimal DELTA3 = new BigDecimal("0.001");
	protected static final BigDecimal DELTA2 = new BigDecimal("0.01");
	protected static final BigDecimal DELTA1 = new BigDecimal("0.1");
	protected static final BigDecimal DELTA0 = new BigDecimal("1");

	protected MeasurementService uomService;

	@Before
	public void createService() throws Exception {
		if (uomService == null) {
			uomService = MeasurementService.getInstance();
		}
	}
}
