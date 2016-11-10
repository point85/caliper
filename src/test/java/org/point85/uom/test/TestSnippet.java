package org.point85.uom.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.point85.uom.MeasurementSystem;

public class TestSnippet extends BaseTest {

	@Test
	public void testCase() throws Exception {
		MeasurementSystem sys = uomService.getUnifiedSystem();
		assertNotNull(sys);

	}

}
