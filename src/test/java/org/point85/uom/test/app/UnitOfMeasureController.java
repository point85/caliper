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

import java.util.List;

import org.point85.uom.Prefix;
import org.point85.uom.UnitOfMeasure;
import org.point85.uom.UnitOfMeasure.MeasurementType;
import org.point85.uom.UnitType;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Controller for the UOM editor
 * 
 * @author Kent
 *
 */
public class UnitOfMeasureController extends BaseController {
	// custom UOM category
	private static final String CUSTOM_CATEGORY = "Uncategorized";

	@FXML
	private TreeView<CategoryNode> tvCategory;

	@FXML
	private Button btNew;

	@FXML
	private Button btSave;

	@FXML
	private Button btDelete;

	@FXML
	private Button btRefresh;

	@FXML
	private TextField tfName;

	@FXML
	private TextField tfSymbol;

	@FXML
	private ComboBox<String> cbUnitTypes;

	@FXML
	private ComboBox<String> cbCategories;

	@FXML
	private TextArea taDescription;

	@FXML
	private ComboBox<String> cbScalingFactor;

	@FXML
	private TextField tfOffset;

	@FXML
	private ComboBox<String> cbAbscissaUnits;

	// for product and quotient
	@FXML
	private ComboBox<String> cbUom1Types;

	@FXML
	private ComboBox<String> cbUom1Units;

	@FXML
	private ComboBox<String> cbUom2Types;

	@FXML
	private ComboBox<String> cbUom2Units;

	@FXML
	private RadioButton rbProduct;

	@FXML
	private RadioButton rbQuotient;

	// for power
	@FXML
	private ComboBox<String> cbPowerTypes;

	@FXML
	private ComboBox<String> cbPowerUnits;

	@FXML
	private TextField tfExponent;

	@FXML
	private TabPane tpProductPower;

	@FXML
	private Tab tScalar;

	@FXML
	private Tab tProductQuotient;

	@FXML
	private Tab tPower;

	// UOM being edited
	private UnitOfMeasure currentUom;

	// stage for the dialog editor
	private Stage dialogStage;

	// initialize
	void initEditor(CaliperApp app) {
		// main app
		setApp(app);

		// images for buttons
		setButtonImages();

		// unit types
		ObservableList<String> unitTypes = getUnitTypes();

		// scalar unit type
		cbUnitTypes.getItems().addAll(unitTypes);
		cbUnitTypes.getSelectionModel().select(UnitType.UNCLASSIFIED.toString());

		// UOM1 unit type
		this.cbUom1Types.getItems().addAll(unitTypes);

		// UOM2 unit type
		this.cbUom2Types.getItems().addAll(unitTypes);

		// power type
		this.cbPowerTypes.getItems().addAll(unitTypes);

		// add the tree view listener for UOM selection
		tvCategory.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue != null) {
				try {
					updateEditor(newValue);
				} catch (Exception e) {
					showErrorDialog(dialogStage, e);
				}
			}
		});

		// fill in the top-level category nodes
		populateCategories();

		// set scaling factor prefixes
		ObservableList<String> prefixes = getPrefixes();
		cbScalingFactor.getItems().addAll(prefixes);
	}

	// images for editor buttons
	private void setButtonImages() {
		// new
		ImageView newView = new ImageView(new Image("images/New.png", 16, 16, true, true));
		btNew.setGraphic(newView);
		btNew.setContentDisplay(ContentDisplay.RIGHT);

		// save
		ImageView saveView = new ImageView(new Image("images/Save.png", 16, 16, true, true));
		btSave.setGraphic(saveView);
		btSave.setContentDisplay(ContentDisplay.RIGHT);

		// delete
		ImageView deleteView = new ImageView(new Image("images/Delete.png", 16, 16, true, true));
		btDelete.setGraphic(deleteView);
		btDelete.setContentDisplay(ContentDisplay.RIGHT);

		// refresh
		ImageView refreshView = new ImageView(new Image("images/Refresh.png", 16, 16, true, true));
		btRefresh.setGraphic(refreshView);
		btRefresh.setContentDisplay(ContentDisplay.RIGHT);
	}

	// update the editor upon selection of a UOM node
	private void updateEditor(TreeItem<?> item) throws Exception {
		// set what units can be worked with
		setPossibleAbscissaUnits();
		setPossiblePowerUnits();
		setPossibleUom1Units();
		setPossibleUom2Units();

		// leaf node is UOM
		if (item.getValue() instanceof UomNode) {
			// display UOM properties
			UomNode uomNode = (UomNode) item.getValue();
			showUom(uomNode);
			return;
		}

		// parent category node
		CategoryNode node = (CategoryNode) item.getValue();

		// get UOM symbol and names in that category
		List<Object[]> rows = getPersistentSystem().getSymbolsAndNames(node.getName());

		item.getChildren().clear();

		// build the display strings
		@SuppressWarnings("unchecked")
		TreeItem<UomNode> rootNode = (TreeItem<UomNode>) item;

		for (Object[] row : rows) {
			String symbol = (String) row[0];
			String name = (String) row[1];
			UomNode uomNode = new UomNode(toDisplayString(symbol, name));
			TreeItem<UomNode> uomItem = new TreeItem<>(uomNode);
			rootNode.getChildren().add(uomItem);
		}
	}

	// select the matching display string
	private void selectSymbol(UnitOfMeasure uom, ComboBox<String> combobox) {
		if (combobox == null) {
			return;
		}

		String displayString = toDisplayString(uom);
		combobox.getSelectionModel().select(displayString);
	}

	// show the UOM attributes
	private void displayAttributes(UnitOfMeasure uom) {

		this.tfName.setText(uom.getName());
		this.tfSymbol.setText(uom.getSymbol());
		this.taDescription.setText(uom.getDescription());
		this.cbCategories.setValue(uom.getCategory());
		this.cbUnitTypes.getSelectionModel().select(uom.getUnitType().toString());

		// regular conversion
		double scalingFactor = uom.getScalingFactor();
		UnitOfMeasure abscissaUnit = uom.getAbscissaUnit();
		double offset = uom.getOffset();

		// regular conversion
		String factorText = String.valueOf(scalingFactor);
		UnitOfMeasure displayAbscissa = abscissaUnit;
		String offsetText = formatDouble(offset);

		// scaling
		Prefix prefix = Prefix.fromFactor(scalingFactor);

		if (prefix != null) {
			cbScalingFactor.setValue(prefix.getName());
		} else {
			cbScalingFactor.setValue(factorText);
		}

		// X-axis unit
		selectSymbol(displayAbscissa, cbAbscissaUnits);

		// offset
		tfOffset.setText(offsetText);

		// UOM1
		switch (uom.getMeasurementType()) {
		case PRODUCT: {
			rbProduct.setSelected(true);

			UnitOfMeasure multiplier = uom.getMultiplier();
			UnitOfMeasure multiplicand = uom.getMultiplicand();

			// multiplier UOM
			cbUom1Types.getSelectionModel().select(multiplier.getUnitType().toString());
			selectSymbol(multiplier, cbUom1Units);

			// multiplicand UOM
			cbUom2Types.getSelectionModel().select(multiplicand.getUnitType().toString());
			selectSymbol(multiplicand, cbUom2Units);

			tpProductPower.getSelectionModel().select(tProductQuotient);
			break;
		}

		case QUOTIENT: {
			rbQuotient.setSelected(true);

			UnitOfMeasure dividend = uom.getDividend();
			UnitOfMeasure divisor = uom.getDivisor();

			// dividend UOM
			cbUom1Types.getSelectionModel().select(dividend.getUnitType().toString());
			selectSymbol(dividend, cbUom1Units);

			// divisor UOM
			cbUom2Types.getSelectionModel().select(divisor.getUnitType().toString());
			selectSymbol(divisor, cbUom2Units);

			tpProductPower.getSelectionModel().select(tProductQuotient);
			break;
		}

		case POWER: {
			UnitOfMeasure base = uom.getPowerBase();

			// base of power UOM
			cbPowerTypes.getSelectionModel().select(base.getUnitType().toString());
			selectSymbol(base, cbPowerUnits);

			// exponent
			if (UnitOfMeasure.isValidExponent(uom.getPowerExponent())) {
				tfExponent.setText(String.valueOf(uom.getPowerExponent()));
			}

			tpProductPower.getSelectionModel().select(tPower);
			break;
		}

		case SCALAR: {
			// clear product, quotient and power for a scalar
			rbProduct.setSelected(false);
			rbQuotient.setSelected(false);

			cbUom1Types.getSelectionModel().clearSelection();
			cbUom1Units.getSelectionModel().clearSelection();

			cbUom2Types.getSelectionModel().clearSelection();
			cbUom2Units.getSelectionModel().clearSelection();

			cbPowerTypes.getSelectionModel().clearSelection();
			cbPowerUnits.getSelectionModel().clearSelection();

			tfExponent.setText(null);

			tpProductPower.getSelectionModel().select(tScalar);
			break;
		}

		default:
			break;
		}
	}

	// display UOM
	private void showUom(UomNode uomNode) throws Exception {
		UnitOfMeasure uom = uomNode.getUom();
		displayAttributes(uom);
	}

	// populate the tree view categories
	private void populateCategories() {
		TreeItem<CategoryNode> dummyRoot = new TreeItem<>();

		// fetch the categories
		List<String> categories = getPersistentSystem().fetchCategories();

		for (String category : categories) {
			CategoryNode categoryNode = new CategoryNode(category);
			TreeItem<CategoryNode> categoryItem = new TreeItem<>(categoryNode);
			dummyRoot.getChildren().add(categoryItem);
		}

		// refresh tree view
		tvCategory.setRoot(dummyRoot);
		tvCategory.setShowRoot(false);

		// also in the drop down
		cbCategories.getItems().clear();
		cbCategories.getItems().addAll(categories);
	}

	// reference to the main app stage
	void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	private void resetEditor() throws Exception {
		// main
		this.tfName.clear();
		this.tfSymbol.clear();
		this.cbCategories.getSelectionModel().clearSelection();
		this.cbCategories.setValue(null);
		this.cbUnitTypes.getSelectionModel().select(UnitType.UNCLASSIFIED.toString());
		this.taDescription.clear();

		// conversion
		this.cbScalingFactor.setValue(null);
		this.cbAbscissaUnits.getSelectionModel().clearSelection();
		this.tfOffset.clear();

		// product/quotient
		this.cbUom1Units.getSelectionModel().clearSelection();
		this.cbUom2Units.getSelectionModel().clearSelection();
		this.cbUom1Types.getSelectionModel().clearSelection();
		this.cbUom2Types.getSelectionModel().clearSelection();

		this.rbProduct.setSelected(false);
		this.rbQuotient.setSelected(false);

		// power
		this.cbPowerTypes.getSelectionModel().clearSelection();
		this.cbPowerUnits.getSelectionModel().clearSelection();
		this.tfExponent.clear();

		// scalar by default
		this.tpProductPower.getSelectionModel().select(tScalar);

		// update custom types
		this.setPossibleAbscissaUnits();

		// no current UOM
		this.currentUom = null;
	}

	// New button clicked
	@FXML
	private void handleNewButton() {
		try {
			resetEditor();
		} catch (Exception e) {
			showErrorDialog(dialogStage, e);
		}
	}

	// Save button clicked
	@FXML
	private void handleSaveButton() {
		// current UOM will not be null if updating
		UnitOfMeasure uom = currentUom;

		try {
			// unit attributes
			String name = this.tfName.getText().trim();
			String symbol = this.tfSymbol.getText().trim();
			String category = this.cbCategories.getSelectionModel().getSelectedItem();
			String type = this.cbUnitTypes.getSelectionModel().getSelectedItem();

			if (type == null) {
				showErrorDialog(dialogStage, "The unit type is required.");
				return;
			}

			if (category == null) {
				category = CUSTOM_CATEGORY;
			}

			// type of unit
			UnitType unitType = UnitType.valueOf(type);

			// description
			String description = this.taDescription.getText();

			if (uom != null) {
				uom.setUnitType(unitType);
				uom.setName(name);
				uom.setSymbol(symbol);
				uom.setDescription(description);
			}

			// scalar, product, quotient or power
			if (tPower.isSelected()) {
				// power base
				String baseSymbol = parseSymbol(cbPowerUnits.getSelectionModel().getSelectedItem());

				if (baseSymbol == null) {
					throw new Exception("A base power UOM symbol must be selected.");
				}
				UnitOfMeasure base = getUOMForEditing(baseSymbol);

				if (!base.getMeasurementType().equals(MeasurementType.SCALAR)) {
					throw new Exception("The base unit of measure must be a scalar.");
				}

				// exponent
				if (tfExponent.getText() == null) {
					throw new Exception("An exponent must be provided.");
				}

				String exp = tfExponent.getText().trim();
				Integer exponent = Integer.valueOf(exp);

				if (uom == null) {
					// new
					uom = getPersistentSystem().createPowerUOM(unitType, name, symbol, description, base, exponent);
				} else {
					// update
					uom.setPowerUnit(base, exponent);
				}

			} else if (tProductQuotient.isSelected()) {
				// product or quotient
				String uom1Symbol = parseSymbol(cbUom1Units.getSelectionModel().getSelectedItem());
				UnitOfMeasure uom1 = getUOMForEditing(uom1Symbol);

				if (!uom1.getMeasurementType().equals(MeasurementType.SCALAR)) {
					throw new Exception("The multiplier/dividend unit of measure must be a scalar.");
				}

				String uom2Symbol = parseSymbol(cbUom2Units.getSelectionModel().getSelectedItem());
				UnitOfMeasure uom2 = getUOMForEditing(uom2Symbol);

				if (!uom2.getMeasurementType().equals(MeasurementType.SCALAR)) {
					throw new Exception("The multiplicand/divisor unit of measure must be a scalar.");
				}

				if (rbProduct.isSelected()) {
					// product
					if (uom == null) {
						// new
						uom = getPersistentSystem().createProductUOM(unitType, name, symbol, description, uom1, uom2);
					} else {
						// update
						uom.setProductUnits(uom1, uom2);
					}

				} else if (rbQuotient.isSelected()) {
					// quotient
					if (uom == null) {
						// new
						uom = getPersistentSystem().createQuotientUOM(unitType, name, symbol, description, uom1, uom2);
					} else {
						// update
						uom.setQuotientUnits(uom1, uom2);
					}
				} else {
					throw new Exception("Either product or quotient must be selected.");
				}
			} else if (tScalar.isSelected()) {
				// create scalar UOM
				if (uom == null) {
					// new
					uom = getPersistentSystem().createScalarUOM(unitType, name, symbol, description);
				}
			} else {
				// should not happen
			}

			// category
			uom.setCategory(category);

			// conversion scaling factor
			double scalingFactor = 1.0d;
			Prefix prefix = Prefix.fromName(cbScalingFactor.getValue());

			if (prefix != null) {
				scalingFactor = prefix.getFactor();
			} else {
				String factor = removeThousandsSeparator(cbScalingFactor.getValue());

				if (factor != null && factor.length() > 0) {
					try {
						scalingFactor = Double.valueOf(factor);
					} catch (NumberFormatException e) {
						throw new Exception(factor + " is not a valid number");
					}
				}
			}

			// conversion UOM
			UnitOfMeasure abscissaUnit = null;
			String abscissaSymbol = parseSymbol(cbAbscissaUnits.getSelectionModel().getSelectedItem());
			if (abscissaSymbol != null) {
				abscissaUnit = getUOMForEditing(abscissaSymbol);
			}

			// conversion offset
			String offsetValue = removeThousandsSeparator(tfOffset.getText());
			double offset = 0.0d;

			if (offsetValue != null && offsetValue.length() > 0) {
				try {
					offset = Double.valueOf(offsetValue);
				} catch (NumberFormatException e) {
					throw new Exception(offsetValue + " is not a valid number");
				}
			}

			if (abscissaUnit != null) {
				// regular conversion
				uom.setConversion(scalingFactor, abscissaUnit, offset);
			}

			// save the created or updated UOM
			currentUom = getPersistentSystem().saveUOM(uom);

			// clear its conversion cache
			uom.clearCache();

			// update categories
			populateCategories();

			// update UOM choices by UOM
			setPossibleAbscissaUnits();
			setPossiblePowerUnits();
			setPossibleUom1Units();
			setPossibleUom2Units();

		} catch (Exception e) {
			// remove from persistence unit
			if (uom != null) {
				getPersistentSystem().evictUOM(uom);
			}

			showErrorDialog(dialogStage, e);
		}
	}

	// Delete button clicked
	@FXML
	private void handleDeleteButton() {
		if (this.currentUom == null || this.currentUom.getKey() == null) {
			showErrorDialog(dialogStage, "No unit of measure has been selected for deletion.");
			return;
		}

		// confirm
		String msg = "Do you want to delete " + toDisplayString(currentUom);
		ButtonType type = showConfirmationDialog(dialogStage, msg);

		if (type.equals(ButtonType.CANCEL)) {
			return;
		}

		try {
			// delete
			getPersistentSystem().deleteUOM(currentUom);

			// reset editor
			resetEditor();

			// update category list
			populateCategories();
		} catch (Exception e) {
			showErrorDialog(dialogStage, e);
		}
	}

	// Refresh button clicked
	@FXML
	private void handleRefreshButton() {
		if (currentUom == null) {
			return;
		}

		try {
			// fetch from database
			UnitOfMeasure uom = getPersistentSystem().fetchUOMByKey(currentUom.getKey());
			displayAttributes(uom);

			// update category list
			populateCategories();
		} catch (Exception e) {
			showErrorDialog(dialogStage, e);
		}
	}

	// Find all possible abscissa units for this type. Called by UI.
	@FXML
	private void setPossibleAbscissaUnits() throws Exception {
		String unitType = cbUnitTypes.getSelectionModel().getSelectedItem();

		if (unitType == null) {
			return;
		}

		ObservableList<String> units = getUnitsOfMeasure(unitType);
		ObservableList<String> customDisplayStrings = getCustomSymbols(UnitType.valueOf(unitType));

		cbAbscissaUnits.getItems().clear();
		cbAbscissaUnits.getItems().addAll(units);
		cbAbscissaUnits.getItems().addAll(customDisplayStrings);
	}

	// Find all possible multiplier/dividend units for this type. Called by UI.
	@FXML
	private void setPossibleUom1Units() throws Exception {
		String unitType = cbUom1Types.getSelectionModel().getSelectedItem();

		if (unitType == null) {
			return;
		}

		ObservableList<String> units = getUnitsOfMeasure(unitType);
		ObservableList<String> customDisplayStrings = getCustomSymbols(UnitType.valueOf(unitType));

		cbUom1Units.getItems().clear();
		cbUom1Units.getItems().addAll(units);
		cbUom1Units.getItems().addAll(customDisplayStrings);
	}

	// Find all possible multiplicand/divisor units for this type. Called by UI.
	@FXML
	private void setPossibleUom2Units() throws Exception {
		String unitType = cbUom2Types.getSelectionModel().getSelectedItem();

		if (unitType == null) {
			return;
		}

		ObservableList<String> units = getUnitsOfMeasure(unitType);
		ObservableList<String> customDisplayStrings = getCustomSymbols(UnitType.valueOf(unitType));

		cbUom2Units.getItems().clear();
		cbUom2Units.getItems().addAll(units);
		cbUom2Units.getItems().addAll(customDisplayStrings);
	}

	// Find all possible power units for this type. Called by UI.
	@FXML
	private void setPossiblePowerUnits() throws Exception {
		String unitType = cbPowerTypes.getSelectionModel().getSelectedItem();

		if (unitType == null) {
			return;
		}

		ObservableList<String> units = getUnitsOfMeasure(unitType);
		ObservableList<String> customDisplayStrings = getCustomSymbols(UnitType.valueOf(unitType));

		cbPowerUnits.getItems().clear();
		cbPowerUnits.getItems().addAll(units);
		cbPowerUnits.getItems().addAll(customDisplayStrings);
	}

	// class for holding attributes of UOM category in a tree view node
	private class CategoryNode {
		// name
		private String name;

		private CategoryNode(String name) {
			this.name = name;
		}

		private String getName() {
			return name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	// class for holding attributes of UOM in a tree view leaf node
	private class UomNode {
		// user-visible string
		private String displayString;

		private UomNode(String displayString) {
			this.displayString = displayString;
		}

		@Override
		public String toString() {
			return displayString;
		}

		private UnitOfMeasure getUom() throws Exception {
			String symbol = parseSymbol(displayString);

			UnitOfMeasure uom = getPersistentSystem().fetchUOMBySymbol(symbol, false);

			// set the UOM
			currentUom = uom;

			return uom;
		}
	}
}
