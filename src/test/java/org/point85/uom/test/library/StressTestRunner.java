package org.point85.uom.test.library;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Test concurrency. This test is not a JUnit test.
 * 
 * @author Kent Randall
 *
 */
public class StressTestRunner {
	// thread pool service
	private ExecutorService executorService = Executors.newCachedThreadPool();

	private static final int NUM_THREADS = 50;

	private long totalMillis = 0;

	private class TestRunner implements Runnable {

		private TestPerformance test;

		private int threadNumber = 0;

		TestRunner(TestPerformance test, int passCount) {
			this.test = test;
			this.threadNumber = passCount;
		}

		@Override
		public void run() {
			try {
				System.out.println("Executing test on thread " + threadNumber);
				long milli1 = System.currentTimeMillis();
				test.runSingleTest();
				long milli2 = System.currentTimeMillis();
				long delta = milli2 - milli1;
				totalMillis += delta;
				System.out.println("Time (ms) for thread " + threadNumber + ": " + delta + ", total: " + totalMillis);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// run each test in a thread
	public void runTests() throws Exception {
		for (int i = 0; i < NUM_THREADS; i++) {
			TestRunner runner = new TestRunner(new TestPerformance(), i + 1);
			executorService.execute(runner);
		}
		System.out.println("Launched all " + NUM_THREADS + " threads.");
	}

	public void runSingleTest() throws Exception {
		TestRunner runner = new TestRunner(new TestPerformance(), 1);
		runner.run();
	}

	public static void main(String[] args) throws Exception {
		StressTestRunner runner = new StressTestRunner();
		System.out.println("Running all tests");
		runner.runTests();
		
		System.out.println("Running single test");
		runner.runSingleTest();
	}
	
}
