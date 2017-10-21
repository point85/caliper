package org.point85.uom.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.point85.uom.Quantity;
import org.point85.uom.Unit;
import org.point85.uom.UnitOfMeasure;
import org.point85.uom.UnitType;

public class TestFinancial extends BaseTest {

	@Test
	public void testStocks() throws Exception {
		// John has 100 shares of Alphabet Class A stock. How much is his
		// portfolio worth in euros when the last trade was $838.96 and a US
		// dollar is worth 0.94 euros?
		UnitOfMeasure euro = sys.getUOM(Unit.EURO);
		UnitOfMeasure usd = sys.getUOM(Unit.US_DOLLAR);
		usd.setConversion(0.94, euro);

		UnitOfMeasure googl = sys.createScalarUOM(UnitType.CURRENCY, "Alphabet A", "GOOGL",
				"Alphabet (formerly Google) Class A shares");
		googl.setConversion(838.96, usd);
		Quantity portfolio = new Quantity(100d, googl);
		Quantity value = portfolio.convert(euro);
		assertTrue(isCloseTo(value.getAmount(), 78862.24, DELTA6));
	}

}
