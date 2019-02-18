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

package org.point85.uom.test.app;

import org.point85.uom.Prefix;
import org.point85.uom.Quantity;
import org.point85.uom.UnitOfMeasure;
import org.point85.uom.UnitType;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller for converting Units of Measure
 * 
 * @author Kent Randall
 *
 */
public class ConversionController extends BaseController {

	@FXML
	private ComboBox<String> cbUnitTypes;

	@FXML
	private ComboBox<String> cbFromPrefixes;

	@FXML
	private ComboBox<String> cbFromUnits;

	@FXML
	private ComboBox<String> cbToPrefixes;

	@FXML
	private ComboBox<String> cbToUnits;

	@FXML
	private TextField tfFromAmount;

	@FXML
	private TextField tfToAmount;

	@FXML
	private Button btConvert;

	@FXML
	private Button btEditor;

	// initialize app
	void initCaliperApp(CaliperApp app) {
		this.setApp(app);

		// set unit types
		cbUnitTypes.getItems().addAll(getUnitTypes());

		// set prefixes
		ObservableList<String> prefixes = getPrefixes();
		cbFromPrefixes.getItems().addAll(prefixes);
		cbToPrefixes.getItems().addAll(prefixes);

		// button images
		setButtonImages();
	}

	// Populate from and to conversion comboBoxes. Called when user selects a
	// UOM type
	@FXML
	private void setPossibleConversions() throws Exception {
		String unitType = cbUnitTypes.getSelectionModel().getSelectedItem();

		if (unitType == null) {
			return;
		}

		// get all units of this type
		ObservableList<String> units = getUnitsOfMeasure(unitType);

		// get custom units
		ObservableList<String> customDisplayStrings = getCustomSymbols(UnitType.valueOf(unitType));

		// set from units
		cbFromUnits.getItems().clear();
		cbFromUnits.getItems().addAll(units);
		cbFromUnits.getItems().addAll(customDisplayStrings);

		// set to units
		cbToUnits.getItems().clear();
		cbToUnits.getItems().addAll(units);
		cbToUnits.getItems().addAll(customDisplayStrings);

		// set prefixes
		cbFromPrefixes.getSelectionModel().select(EMPTY_STRING);
		cbToPrefixes.getSelectionModel().select(EMPTY_STRING);

		// clear amounts
		tfFromAmount.clear();
		tfToAmount.clear();
	}

	// get the Prefix from its name
	private Prefix getPrefix(String name) {
		Prefix prefix = null;

		if (name != null && name.length() > 0) {
			prefix = Prefix.fromName(name);
		}
		return prefix;
	}

	@FXML
	private void handleConvertButton() {
		try {
			tfToAmount.clear();

			// from amount
			String amount = removeThousandsSeparator(tfFromAmount.getText().trim());

			if (amount == null || amount.length() == 0) {
				this.showErrorDialog(getApp().getPrimaryStage(), "An amount to convert from is required.");
				return;
			}

			// from amount
			double fromAmount = Quantity.createAmount(amount);

			// from prefix
			Prefix fromPrefix = getPrefix(cbFromPrefixes.getSelectionModel().getSelectedItem());

			// to prefix
			Prefix toPrefix = getPrefix(cbToPrefixes.getSelectionModel().getSelectedItem());

			// from UOM
			String symbol = parseSymbol(cbFromUnits.getSelectionModel().getSelectedItem());
			UnitOfMeasure fromUOM = getUOMForConversion(fromPrefix, symbol);

			if (fromUOM == null) {
				showErrorDialog(getApp().getPrimaryStage(), "A unit of measure to convert from is required");
				return;
			}

			// to UOM
			symbol = parseSymbol(cbToUnits.getSelectionModel().getSelectedItem());
			UnitOfMeasure toUOM = getUOMForConversion(toPrefix, symbol);

			if (toUOM == null) {
				showErrorDialog(getApp().getPrimaryStage(), "A unit of measure to convert to is required");
				return;
			}

			// from quantity
			Quantity fromQuantity = new Quantity(fromAmount, fromUOM);

			// converted quantity
			Quantity toQuantity = fromQuantity.convert(toUOM);

			// converted amount
			double toAmount = toQuantity.getAmount();
			String toShow = formatDouble(toAmount);
			tfToAmount.setText(toShow);
		} catch (Exception e) {
			showErrorDialog(getApp().getPrimaryStage(), e);
		}
	}

	// show the editor dialog
	@FXML
	private void handleEditorButton() {
		try {
			getApp().showEditorDialog();

			// UOMs could have been changed
			getPersistentSystem().clearCache();

			// refresh units
			setPossibleConversions();
		} catch (Exception e) {
			showErrorDialog(getApp().getPrimaryStage(), e);
		}
	}

	// images for buttons
	private void setButtonImages() {
		// editor
		ImageView editorView = new ImageView(new Image("images/UOMs.png", 16, 16, true, true));
		btEditor.setGraphic(editorView);
		btEditor.setContentDisplay(ContentDisplay.LEFT);

		// converter
		ImageView convertView = new ImageView(new Image("images/Convert.png", 16, 16, true, true));
		btConvert.setGraphic(convertView);
		btConvert.setContentDisplay(ContentDisplay.LEFT);
	}
}
