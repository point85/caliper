package org.point85.uom;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The UnitOfMeasureService provides access to the four internationally
 * recognized unit of measure systems:
 * <ul>
 * <li>International System (SI)</li>
 * <li>International Customary</li>
 * <li>United States (US)</li>
 * <li>British Imperial (BR)</li>
 * </ul>
 * through a unified {@link MeasurementSystem}, and as many custom systems as
 * needed. <br>
 * Once a measurement system is created, units of measure can be created for any
 * recognized or custom system. Existing units can be accessed by symbol or by
 * enumerated type if so defined.
 * 
 * @author Kent Randall
 *
 */
public class MeasurementService {
	// name of resource bundle with translatable strings for measurement system
	static final String MEASUREMENT_SYSTEM_BUNDLE_NAME = "MeasurementSystem";

	// name of resource bundle with translatable strings for exception messages
	static final String MESSAGES_BUNDLE_NAME = "Messages";

	// resource bundle for exception messages
	private static ResourceBundle messages;

	// resource bundle for measurement systems
	private static ResourceBundle symbols;

	// standard unified system
	private MeasurementSystem unifiedSystem;

	// custom systems, e.g. packaging, key = system name are registered by name
	private List<MeasurementSystem> systemRegistry = new ArrayList<>();

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
		symbols = ResourceBundle.getBundle(MEASUREMENT_SYSTEM_BUNDLE_NAME, Locale.getDefault());
	}

	// get the resource bundle for messages
	static ResourceBundle getMessages() {
		return messages;
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
		unifiedSystem = new MeasurementSystem(symbols.getString("unified.name"), symbols.getString("unified.symbol"),
				symbols.getString("unified.desc"));
		unifiedSystem.initialize();
	}

	/**
	 * Get the measurement system by its unique symbol
	 * 
	 * @param symbol
	 *            The measurement system symbol
	 * @return {@link MeasurementSystem}
	 * @throws Exception
	 *             Exception
	 */
	public MeasurementSystem getSystem(String symbol) throws Exception {
		MeasurementSystem system = null;
		for (MeasurementSystem registeredSystem : systemRegistry) {
			if (registeredSystem.getSymbol().equals(symbol)) {
				system = registeredSystem;
				break;
			}
		}
		return system;
	}

	/**
	 * Create a custom unit of measure system
	 * 
	 * @param name
	 *            Name of system
	 * @param symbol
	 *            Unique symbol of system
	 * @param description
	 *            Description of system
	 * @return {@link MeasurementSystem}
	 * @throws Exception
	 *             Exception
	 */
	public MeasurementSystem createSystem(String name, String symbol, String description) throws Exception {

		if (symbol == null || symbol.trim().length() == 0) {
			throw new Exception(MeasurementService.getMessage("symbol.cannot.be.null"));
		}

		if (getSystem(symbol) != null) {
			String msg = MessageFormat.format(MeasurementService.getMessage("system.already.created"), symbol);
			throw new Exception(msg);
		}

		MeasurementSystem custom = new MeasurementSystem(name, symbol, description);
		systemRegistry.add(custom);
		custom.initialize();
		return custom;
	}

	/**
	 * Remove this measurement system from the cache
	 * 
	 * @param system
	 *            {@link MeasurementSystem}
	 * @throws Exception
	 *             Exception
	 */
	public void unregisterSystem(MeasurementSystem system) throws Exception {
		if (system != null) {
			MeasurementSystem registeredSystem = getSystem(system.getSymbol());

			if (registeredSystem != null) {
				systemRegistry.remove(system);
			}
		}
	}
}
