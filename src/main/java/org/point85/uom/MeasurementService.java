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
package org.point85.uom;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The MeasurementService provides access to the four internationally
 * recognized unit of measure systems:
 * <ul>
 * <li>International System (SI)</li>
 * <li>International Customary</li>
 * <li>United States (US)</li>
 * <li>British Imperial (BR)</li>
 * </ul>
 * through a unified {@link MeasurementSystem}, and as many custom systems as
 * needed.
 * Once the unified measurement system is created, units of measure can be created for any
 * recognized or custom system. Existing units can be accessed by symbol or by
 * enumerated type.
 * 
 * @author Kent Randall
 *
 */
public class MeasurementService {

	// name of resource bundle with translatable strings for exception messages
	static final String MESSAGES_BUNDLE_NAME = "Message";

	// resource bundle for exception messages
	private static ResourceBundle messages;

	// standard unified system
	private MeasurementSystem unifiedSystem;

	// singleton
	private static MeasurementService serviceInstance;

	private MeasurementService() {
		initialize();
	}

	/**
	 * Get an instance of a UnitOfMeasureService
	 * 
	 * @return {@link MeasurementService}
	 */
	public static MeasurementService getInstance() {
		if (serviceInstance == null) {
			serviceInstance = new MeasurementService();
		}
		return serviceInstance;
	}

	private void initialize() {
		messages = ResourceBundle.getBundle(MESSAGES_BUNDLE_NAME, Locale.getDefault());
	}

	// get a particular message by its key
	static String getMessage(String key) {
		return messages.getString(key);
	}

	/**
	 * Get the standard system of units of measure from International Customary,
	 * SI, US and British Imperial
	 * 
	 * @return {@link MeasurementSystem}
	 * @throws Exception
	 *             Exception
	 */
	public MeasurementSystem getUnifiedSystem() throws Exception {
		if (unifiedSystem == null) {
			createUnifiedSystem();
		}

		return unifiedSystem;
	}

	private void createUnifiedSystem() throws Exception {
		unifiedSystem = new MeasurementSystem();
		unifiedSystem.initialize();
	}
}
