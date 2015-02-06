/*
* This file is part of SuDonkey, an open-source Sudoku puzzle game generator and solver.
* Copyright (C) 2015 Vedran Matic
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*
*/

package com.matic.sudoku.guifx.window;

import java.io.File;
import java.util.Optional;

import org.controlsfx.tools.Borders;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import com.matic.sudoku.Resources;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

public class PdfExporterWindow {
	
	private final ComboBox<String> pageFormatCombo;
	private final TextField outputPathField;	
	private final Button browseButton;
	
	private final Dialog<ButtonType> window;

	public PdfExporterWindow(final Window owner) {
		browseButton = new Button("Browse...");
		outputPathField = new TextField();
		pageFormatCombo = new ComboBox<>();		
		
		window = new Dialog<>();
		window.initOwner(owner);
		
		initComponents();
	}
	
	/**
	 * Open and show PDF exporter window and wait for and return user input, if any
	 * 
	 * @return PDF exporter options chosen by the player, or null if cancelled
	 */
	public PdfExporterOptions showAndWait() {
		final Optional<ButtonType> result = window.showAndWait();
		
		if(result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {
			
		}
		
		return null;
	}
	
	private void initComponents() {
		pageFormatCombo.setMaxWidth(Resources.Gui.COMBOBOX_MAX_WIDTH);
		
		outputPathField.setPromptText("Path to the file to be used as a destination for PDF-export");
		outputPathField.setPrefColumnCount(30);		
		outputPathField.setEditable(false);	
		
		browseButton.setOnAction(event -> onBrowse());
		
		pageFormatCombo.getItems().addAll("1/4", "1/2", "1/1");
		pageFormatCombo.getSelectionModel().select(0);
		
		window.setHeaderText(null);
		window.setTitle("Export to PDF");
		
		final ButtonType exportButtonType = new ButtonType(
				Resources.getTranslation("export.title"), ButtonData.OK_DONE);
		
		final ButtonType cancelButtonType = new ButtonType(
				Resources.getTranslation("button.cancel"), ButtonData.CANCEL_CLOSE);				
		
		window.getDialogPane().getButtonTypes().addAll(exportButtonType, cancelButtonType);
		
		final Button okButton = (Button)window.getDialogPane().lookupButton(exportButtonType);	
		okButton.setDisable(!validateInput());		
		okButton.addEventFilter(ActionEvent.ACTION, event -> {
			//Prevent window from closing until player input has been validated
			if(!validateInput()) {
				event.consume();
			}
		});
		
		setupInputValidation(okButton);		
		
		window.setResizable(true);		
		window.getDialogPane().setContent(layoutContent());
	}
	
	private Node layoutContent() {
		final Pane pageFormatPane = buildPageFormatPane();
		final Pane browsePane = buildBrowsePane();
		
		BorderPane.setMargin(outputPathField, new Insets(0, Resources.Gui.LAYOUT_PADDING,
				0, Resources.Gui.LAYOUT_PADDING));
		BorderPane.setMargin(browseButton, new Insets(0, Resources.Gui.LAYOUT_PADDING,
				0, Resources.Gui.LAYOUT_PADDING));
		
		final VBox mainPane = new VBox();		
		mainPane.getChildren().addAll(browsePane, pageFormatPane);		
		mainPane.setPadding(new Insets(Resources.Gui.LAYOUT_PADDING, 
				Resources.Gui.LAYOUT_PADDING, Resources.Gui.LAYOUT_PADDING,
				2 * Resources.Gui.LAYOUT_PADDING));	
		
		final Node borderedMainPane = Borders.wrap(
				mainPane).etchedBorder().title("PDF export options").buildAll();
		
		return borderedMainPane;
	}
	
	private Pane buildBrowsePane() {
		final BorderPane browsePane = new BorderPane();				
		browsePane.setPadding(new Insets(Resources.Gui.LAYOUT_PADDING, 
				Resources.Gui.LAYOUT_PADDING, Resources.Gui.LAYOUT_PADDING,
				Resources.Gui.LAYOUT_PADDING));		
		browsePane.setCenter(outputPathField);
		browsePane.setRight(browseButton);
		
		return browsePane;
	}
	
	private Pane buildPageFormatPane() {
		final HBox pageFormatPane = new HBox();
		pageFormatPane.setPadding(new Insets(Resources.Gui.LAYOUT_PADDING, 
				Resources.Gui.LAYOUT_PADDING, Resources.Gui.LAYOUT_PADDING,
				2* Resources.Gui.LAYOUT_PADDING));
		
		final Label pageFormatLabel = new Label("Page format: ");	
		pageFormatPane.setAlignment(Pos.CENTER_LEFT);
		
		pageFormatPane.getChildren().addAll(pageFormatLabel, pageFormatCombo);
		
		return pageFormatPane;
	}
	
	private void setupInputValidation(final Button exportButton) {
		outputPathField.textProperty().addListener((observable, oldValue, newValue) ->
			exportButton.setDisable(!validateInput())
		);
		
		final ValidationSupport validationSupport = new ValidationSupport();
		validationSupport.registerValidator(outputPathField, 
				Validator.createEmptyValidator("Output path must be specified."));
	}
	
	private boolean validateInput() {
		if("".equals(outputPathField.getText().trim())) {
			return false;
		}
		return true;
	}
	
	private void onBrowse() {
		final FileChooser pathChooser = new FileChooser();
		pathChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		
		pathChooser.getExtensionFilters().addAll(                
                new FileChooser.ExtensionFilter("PDF files", "*.pdf"));
		pathChooser.setTitle("Browse");
		
		final File selectedFile = pathChooser.showSaveDialog(window.getOwner());
		
		if(selectedFile != null) {
			outputPathField.setText(selectedFile.getAbsolutePath());
		}		
	}
}