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
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.point85.uom.MeasurementSystem;
import org.point85.uom.Quantity;
import org.point85.uom.Unit;
import org.point85.uom.UnitOfMeasure;

public class BaseTest {

	protected static BigDecimal DELTA6;
	protected static BigDecimal DELTA5;
	protected static BigDecimal DELTA4;
	protected static BigDecimal DELTA3;
	protected static BigDecimal DELTA2;
	protected static BigDecimal DELTA1;
	protected static BigDecimal DELTA0;

	protected final static MeasurementSystem sys = initializeSystem();

	private static MeasurementSystem initializeSystem() {
		MeasurementSystem system = MeasurementSystem.getSystem();

		try {
			DELTA6 = Quantity.createAmount("0.000001");
			DELTA5 = Quantity.createAmount("0.00001");
			DELTA4 = Quantity.createAmount("0.0001");
			DELTA3 = Quantity.createAmount("0.001");
			DELTA2 = Quantity.createAmount("0.01");
			DELTA1 = Quantity.createAmount("0.1");
			DELTA0 = Quantity.createAmount("1");

		} catch (Exception e) {

		}

		return system;
	}

	protected BaseTest() {

	}

	protected void snapshotSymbolCache() {

		Map<String, UnitOfMeasure> treeMap = new TreeMap<>(sys.getSymbolCache());

		System.out.println("Symbol cache ...");
		int count = 0;
		for (Entry<String, UnitOfMeasure> entry : treeMap.entrySet()) {
			count++;
			System.out.println("(" + count + ") " + entry.getKey() + (", ") + entry.getValue());
		}
	}

	protected void snapshotBaseSymbolCache() {
		Map<String, UnitOfMeasure> treeMap = new TreeMap<>(sys.getBaseSymbolCache());

		System.out.println("Base symbol cache ...");
		int count = 0;
		for (Entry<String, UnitOfMeasure> entry : treeMap.entrySet()) {
			count++;
			System.out.println("(" + count + ") " + entry.getKey() + (", ") + entry.getValue());
		}
	}

	protected void snapshotUnitEnumerationCache() {
		Map<Unit, UnitOfMeasure> treeMap = new TreeMap<>(sys.getEnumerationCache());

		System.out.println("Enumeration cache ...");
		int count = 0;
		for (Entry<Unit, UnitOfMeasure> entry : treeMap.entrySet()) {
			count++;
			System.out.println("(" + count + ") " + entry.getKey() + (", ") + entry.getValue());
		}
	}
}
