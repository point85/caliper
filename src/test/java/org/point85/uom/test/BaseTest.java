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
