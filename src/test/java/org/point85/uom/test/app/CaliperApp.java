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

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The main application
 * 
 * @author Kent Randall
 *
 */
public class CaliperApp extends Application {
	// the main stage
	private Stage primaryStage;

	// parent layout
	private AnchorPane conversionLayout;

	// start the app
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Caliper Units of Measure");

		// Set the application icon.
		Image appIcon = new Image(getImagesPath() + "Point85.png");
		this.primaryStage.getIcons().add(appIcon);

		// Load root layout from fxml file.
		FXMLLoader loader = new FXMLLoader();
		String path = getFxmlPath() + "UnitConverter.fxml";
		URL url = CaliperApp.class.getResource(path);
		loader.setLocation(url);
		conversionLayout = (AnchorPane) loader.load();

		// Show the scene containing the root layout.
		Scene scene = new Scene(conversionLayout);
		primaryStage.setScene(scene);

		// Give the controller access to the main app.
		ConversionController controller = loader.getController();
		controller.initCaliperApp(this);

		// show the converter
		primaryStage.show();
	}

	// path to images
	private String getImagesPath() {
		return "images/";
	}

	// path to FXML
	private String getFxmlPath() {
		// path relative to "bin" folder
		return "../app/";
	}

	// display the UOM editor as a dialog
	void showEditorDialog() throws Exception {
		// Load the fxml file and create a new stage for the pop-up dialog.
		FXMLLoader loader = new FXMLLoader();
		String path = getFxmlPath() + "UnitEditor.fxml";
		loader.setLocation(CaliperApp.class.getResource(path));
		AnchorPane page = (AnchorPane) loader.load();

		// Create the dialog Stage.
		Stage dialogStage = new Stage();
		dialogStage.setTitle("Edit Unit Of Measure");
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initOwner(primaryStage);
		Scene scene = new Scene(page);
		dialogStage.setScene(scene);

		// Set the person into the controller.
		UnitOfMeasureController controller = loader.getController();
		controller.setDialogStage(dialogStage);
		controller.initEditor(this);

		// Show the dialog and wait until the user closes it
		dialogStage.showAndWait();
	}

	Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * Main entry point
	 * 
	 * @param args Arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
