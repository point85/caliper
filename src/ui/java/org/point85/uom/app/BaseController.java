/*
MIT License

Copyright (c) 2017 Kent Randall

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

package org.point85.uom.app;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.TreeMap;

import org.point85.uom.Prefix;
import org.point85.uom.UnitOfMeasure;
import org.point85.uom.UnitType;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * Base class for the UOM conversion and editor controllers
 * 
 * @author Kent Randall
 *
 */
abstract class BaseController {
	// max and min number of decimal places to show
	private static final int MAX_DIGITS = 9;
	private static final int MIN_DIGITS = 0;

	// no text
	protected static final String EMPTY_STRING = "";

	// persistent measurement system
	private PersistentMeasurementSystem sys = PersistentMeasurementSystem.getSystem();

	// list of UnitTypes
	private ObservableList<String> unitTypes = FXCollections.observableArrayList();

	// list of Prefixes
	private ObservableList<String> prefixes = FXCollections.observableArrayList();

	// Reference to the main application
	private CaliperApp app;

	// the persistent measurement system
	protected PersistentMeasurementSystem getPersistentSystem() {
		return this.sys;
	}

	// Get display strings for UOMs of the specified type
	protected ObservableList<String> getUnitsOfMeasure(String type) throws Exception {
		ObservableList<String> displayStrings = FXCollections.observableArrayList();

		// UnitType
		UnitType unitType = UnitType.valueOf(type);

		List<UnitOfMeasure> uoms = sys.getUnitsOfMeasure(unitType);

		for (UnitOfMeasure uom : uoms) {
			String displayString = toDisplayString(uom);
			displayStrings.add(displayString);
		}
		Collections.sort(displayStrings);

		return displayStrings;
	}

	// get the display strings for all UOM types
	protected ObservableList<String> getUnitTypes() {
		if (unitTypes.size() == 0) {
			for (UnitType unitType : UnitType.values()) {
				unitTypes.add(unitType.toString());
			}
			Collections.sort(unitTypes);
		}
		return unitTypes;
	}

	// get the display strings for all prefixes
	protected ObservableList<String> getPrefixes() {
		if (prefixes.size() == 0) {
			for (Prefix prefix : Prefix.getDefinedPrefixes()) {
				prefixes.add(prefix.getName());
			}
			prefixes.add(EMPTY_STRING);
			Collections.sort(prefixes);
		}
		return prefixes;
	}

	// get the display strings for custom symbols defined for the specified UOM
	// type
	protected ObservableList<String> getCustomSymbols(UnitType unitType) {

		List<Object[]> rows = getPersistentSystem().fetchSymbolsAndNames(unitType);

		List<String> displayStrings = new ArrayList<>(rows.size());

		for (Object[] row : rows) {
			String symbol = (String) row[0];
			String name = (String) row[1];
			displayStrings.add(toDisplayString(symbol, name));
		}

		return FXCollections.observableArrayList(displayStrings);
	}

	protected CaliperApp getApp() {
		return this.app;
	}

	protected void setApp(CaliperApp app) {
		this.app = app;
	}

	// display a general alert
	protected ButtonType showAlert(Stage dialogStage, AlertType type, String title, String header,
			String errorMessage) {
		// Show the error message.
		Alert alert = new Alert(type);
		alert.initOwner(dialogStage);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(errorMessage);

		Optional<ButtonType> result = alert.showAndWait();

		ButtonType buttonType = null;
		try {
			buttonType = result.get();
		} catch (NoSuchElementException e) {

		}
		return buttonType;
	}

	// display an error dialog
	protected void showErrorDialog(Stage dialogStage, String message) {
		showAlert(dialogStage, AlertType.ERROR, "Application Error", "Exception", message);
	}

	// display an error dialog
	protected void showErrorDialog(Stage dialogStage, Exception e) {
		String message = e.getMessage();

		if (message == null) {
			message = e.getClass().getSimpleName();
		}
		showAlert(dialogStage, AlertType.ERROR, "Application Error", "Exception", message);
	}

	// display an ok/cancel dialog
	protected ButtonType showConfirmationDialog(Stage dialogStage, String message) {
		return showAlert(dialogStage, AlertType.CONFIRMATION, "Confirmation", "Confirm Action", message);
	}

	// get the UOM from cache first, then from the database if not found
	protected UnitOfMeasure getUOMForConversion(Prefix prefix, String symbol) throws Exception {
		UnitOfMeasure uom = null;

		if (symbol == null || symbol.length() == 0) {
			return uom;
		}

		// look in cache first
		uom = getPersistentSystem().getUOM(symbol);

		if (uom == null) {
			// database next
			uom = getPersistentSystem().fetchUOMBySymbol(symbol, false);

			if (uom != null) {
				// cache it
				getPersistentSystem().registerUnit(uom);
			}
		}

		if (uom != null && prefix != null) {
			uom = getPersistentSystem().getUOM(prefix, uom);
		}

		return uom;
	}

	// get the UOM from the database first, then from cache if not found
	protected UnitOfMeasure getUOMForEditing(String symbol) throws Exception {
		UnitOfMeasure uom = null;

		if (symbol == null || symbol.length() == 0) {
			return uom;
		}

		// look in database first
		uom = getPersistentSystem().fetchUOMBySymbol(symbol, true);

		if (uom != null) {
			// cache it
			getPersistentSystem().registerUnit(uom);
		}

		if (uom == null) {
			// get from cache next
			uom = getPersistentSystem().getUOM(symbol);
		}

		// bring referenced units into persistence context
		getPersistentSystem().fetchReferencedUnits(uom);

		return uom;
	}

	// create a string to represent the UOM
	static String toDisplayString(UnitOfMeasure uom) {
		StringBuffer sb = new StringBuffer();
		sb.append(uom.getSymbol()).append(" (").append(uom.getName()).append(')');
		return sb.toString();
	}

	// create a String from the UOM symbol and name
	protected String toDisplayString(String symbol, String name) {
		StringBuffer sb = new StringBuffer();
		sb.append(symbol).append(" (").append(name).append(')');
		return sb.toString();
	}

	// parse the UOM symbol out of the display string
	protected String parseSymbol(String displayString) {
		String symbol = null;

		if (displayString != null) {
			int idx = displayString.indexOf('(');
			symbol = displayString.substring(0, idx - 1);
		}
		return symbol;
	}

	// removed formatting from decimal string
	protected String removeThousandsSeparator(String formattedString) {
		if (formattedString == null) {
			return null;
		}
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();

		StringBuffer sb = new StringBuffer();
		sb.append(decimalFormatSymbols.getGroupingSeparator());
		String separator = sb.toString();

		String[] thousands = formattedString.split(separator);

		sb = new StringBuffer();

		for (String thousand : thousands) {
			sb.append(thousand);
		}
		return sb.toString();
	}

	// format a double nicely
	protected String formatDouble(double decimal) {
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
		numberFormat.setGroupingUsed(true);
		numberFormat.setMaximumFractionDigits(MAX_DIGITS);
		numberFormat.setMinimumFractionDigits(MIN_DIGITS);
		return numberFormat.format(decimal);
	}

	// print the symbol cache
	protected void snapshotSymbolCache() {
		Map<String, UnitOfMeasure> treeMap = new TreeMap<>(sys.getSymbolCache());

		System.out.println("Symbol cache ...");
		int count = 0;
		for (Entry<String, UnitOfMeasure> entry : treeMap.entrySet()) {
			count++;
			System.out.println("(" + count + ") " + entry.getKey() + (", ") + entry.getValue());
		}
	}
}
