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
package org.point85.uom.test.library;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.point85.uom.MeasurementSystem;
import org.point85.uom.Unit;
import org.point85.uom.UnitOfMeasure;

public class BaseTest {

	protected static double DELTA6;
	protected static double DELTA5;
	protected static double DELTA4;
	protected static double DELTA3;
	protected static double DELTA2;
	protected static double DELTA1;
	protected static double DELTA0;
	protected static double DELTA_10;

	protected final static MeasurementSystem sys = initializeSystem();

	private static MeasurementSystem initializeSystem() {
		MeasurementSystem system = MeasurementSystem.getSystem();

		try {
			DELTA6 = 0.000001;
			DELTA5 = 0.00001;
			DELTA4 = 0.0001;
			DELTA3 = 0.001;
			DELTA2 = 0.01;
			DELTA1 = 0.1;
			DELTA0 = 1;
			DELTA_10 = 10;

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
			System.out.println("(" + count + ") " + entry.getKey() + ", " + entry.getValue());
		}
	}

	protected void snapshotBaseSymbolCache() {
		Map<String, UnitOfMeasure> treeMap = new TreeMap<>(sys.getBaseSymbolCache());

		System.out.println("Base symbol cache ...");
		int count = 0;
		for (Entry<String, UnitOfMeasure> entry : treeMap.entrySet()) {
			count++;
			System.out.println("(" + count + ") " + entry.getKey() + ", " + entry.getValue());
		}
	}

	protected void snapshotUnitEnumerationCache() {
		Map<Unit, UnitOfMeasure> treeMap = new TreeMap<>(sys.getEnumerationCache());

		System.out.println("Enumeration cache ...");
		int count = 0;
		for (Entry<Unit, UnitOfMeasure> entry : treeMap.entrySet()) {
			count++;
			System.out.println("(" + count + ") " + entry.getKey() + ", " + entry.getValue());
		}
	}
	
	protected boolean isCloseTo(double actualValue, double expectedValue, double delta) {
		double diff = Math.abs(actualValue - expectedValue);
		return (diff <= delta) ? true : false;
	}
}
