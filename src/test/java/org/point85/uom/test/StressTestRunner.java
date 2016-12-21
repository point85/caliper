package org.point85.uom.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StressTestRunner {
	// thread pool service
	private ExecutorService executorService = Executors.newCachedThreadPool();

	// run each test in a thread
	public void runTests() throws Exception {
		for (int i = 0; i < 10; i++) {
			final TestPerformance test = new TestPerformance();
			executorService.execute(() -> {
				try {
					test.runSingleTest();
					System.out.println("Executed test pass ");
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}

	public static void main(String[] args) throws Exception {
		StressTestRunner runner = new StressTestRunner();
		runner.runTests();
	}
}
