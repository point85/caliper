package org.point85.uom.test;

import static org.hamcrest.number.BigDecimalCloseTo.closeTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.point85.uom.Conversion;
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
		usd.setConversion(new Conversion("0.94", euro));

		UnitOfMeasure googl = sys.createScalarUOM(UnitType.FINANCIAL, "Alphabet A", "GOOGL",
				"Alphabet (formerly Google) Class A shares");
		googl.setConversion(new Conversion("838.96", usd));
		Quantity portfolio = new Quantity("100", googl);
		Quantity value = portfolio.convert(euro);
		assertThat(value.getAmount(), closeTo(Quantity.createAmount("78862.24"), DELTA6));
	}

}
