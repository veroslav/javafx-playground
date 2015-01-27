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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.tools.Borders;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import com.matic.sudoku.generator.Generator.Symmetry;
import com.matic.sudoku.guifx.board.GameBoard.SymbolType;
import com.matic.sudoku.guifx.window.PuzzleExporterOptions.Ordering;
import com.matic.sudoku.logic.LogicSolver.Grading;
import com.matic.sudoku.Resources;

/**
 * A window shown when the player wants to create and export puzzles to PDF.
 * It offers various puzzle creation and export options to choose from.
 * 
 * @author vedran
 *
 */
public class PuzzleExporterWindow {
	
	private static final String RANDOM = Resources.getTranslation("generate.random");
	private static final String COUNT_VALIDATION_STRING = "0123456789";
	private static final String DEFAULT_OUTPUT_FILE_NAME = "out.pdf";
	private static final int MAX_PUZZLE_EXPORT_DIGITS = 3;
	
	private final CheckComboBox<String> symbolTypeCheckCombo;
	private final CheckComboBox<String> symmetryCheckCombo;
	private final CheckComboBox<String> gradingCheckCombo;		
	private final ComboBox<String> puzzlesPerPageCombo;
	private final ComboBox<String> creationModeCombo;
	private final ComboBox<String> puzzleOrderCombo;
	
	private final CheckBox puzzleDifficultyCheck;
	private final CheckBox puzzleNumberingCheck;
	private final CheckBox fillPencilmarksCheck;
	private final CheckBox appendSolutionsCheck;
	
	private final TextField puzzleCountField;
	private final TextField outputPathField;
	
	private final Button browseButton;
	
	private final Dialog<ButtonType> window;

	public PuzzleExporterWindow(final Window owner) {
		symbolTypeCheckCombo = new CheckComboBox<>();
		symmetryCheckCombo = new CheckComboBox<>();
		gradingCheckCombo = new CheckComboBox<>();		
		
		puzzlesPerPageCombo = new ComboBox<>();
		creationModeCombo = new ComboBox<>();
		puzzleOrderCombo = new ComboBox<>();
		
		puzzleDifficultyCheck = new CheckBox(Resources.getTranslation("generate.show_difficulties"));
		puzzleNumberingCheck = new CheckBox(Resources.getTranslation("generate.show_numberings"));
		fillPencilmarksCheck = new CheckBox(Resources.getTranslation("puzzle.fill_pencilmarks"));
		appendSolutionsCheck = new CheckBox(Resources.getTranslation("export.append_solutions"));
		
		puzzleCountField = new TextField();
		outputPathField = new TextField();
		
		browseButton = new Button(Resources.getTranslation("button.browse"));
		
		window = new Dialog<>();
		window.initOwner(owner);
		
		initComponents();
	}
	
	/**
	 * Open and show puzzle PDF exporter window and wait for and return user input, if any
	 * 
	 * @return Puzzle exporter options selected by player, or null if cancelled
	 */
	public PuzzleExporterOptions showAndWait() {
		final Optional<ButtonType> result = window.showAndWait();
		
		if(result.isPresent() && result.get().getButtonData() == ButtonData.OK_DONE) {				
			final boolean isEmptyGrid = creationModeCombo.getSelectionModel()
					 .getSelectedIndex() == 1;
			final int puzzlesPerPage = Integer.parseInt(puzzlesPerPageCombo
					.getSelectionModel().getSelectedItem());
			final int puzzleCount = Integer.parseInt(puzzleCountField.getText());
			
			return new PuzzleExporterOptions(getSelectedSymbolTypes(), getSelectedSymmetries(),
					getSelectedGradings(), puzzleNumberingCheck.isSelected(),
					puzzleDifficultyCheck.isSelected(), fillPencilmarksCheck.isSelected(),
					appendSolutionsCheck.isSelected(), isEmptyGrid,
					Ordering.fromString(puzzleOrderCombo.getSelectionModel().getSelectedItem()),
					outputPathField.getText(), puzzlesPerPage, puzzleCount);
		}
		return null;
	}
	
	private List<Grading> getSelectedGradings() {
		final ObservableList<String> selectedGradings = gradingCheckCombo.getCheckModel().getCheckedItems();		
		final List<Grading> chosenGradings = selectedGradings.stream().map(
				gradingName -> Grading.fromString(gradingName)).collect(Collectors.toList());
		
		return Collections.unmodifiableList(chosenGradings);
	}
	
	private List<Symmetry> getSelectedSymmetries() {
		final ObservableList<String> selectedSymmetries = symmetryCheckCombo.getCheckModel().getCheckedItems();
		final List<Symmetry> chosenSymmetries = selectedSymmetries.stream().map(
				symmetryName -> Symmetry.fromString(symmetryName)).collect(Collectors.toList());
		
		return Collections.unmodifiableList(chosenSymmetries);
	}
	
	private List<SymbolType> getSelectedSymbolTypes() {
		final ObservableList<String> selectedSymbolTypes = symbolTypeCheckCombo.getCheckModel().getCheckedItems();
		final List<SymbolType> chosenSymbolTypes = selectedSymbolTypes.stream().map(
				symbolType -> SymbolType.fromString(symbolType)).collect(Collectors.toList());
		
		return Collections.unmodifiableList(chosenSymbolTypes);
	}
	
	private void onCreationModeChanged(final Button exportButton) {
		final int selectedModeIndex = creationModeCombo.getSelectionModel().getSelectedIndex();		
		setComponentsDisabled(selectedModeIndex != 0);
		exportButton.setDisable(!validateInput());
	}
	
	//Disable certain options that are usable only when generating new puzzles
	private void setComponentsDisabled(final boolean disabled) {
		puzzleDifficultyCheck.setDisable(disabled);		
		appendSolutionsCheck.setDisable(disabled);
		symmetryCheckCombo.setDisable(disabled);
		gradingCheckCombo.setDisable(disabled);
		puzzleOrderCombo.setDisable(disabled);
		symbolTypeCheckCombo.setDisable(disabled);
	}
	
	private void initComponents() {
		outputPathField.setPrefColumnCount(30);
		outputPathField.setEditable(false);
		outputPathField.setTooltip(new Tooltip("Where to store the generated PDF file"));
		
		puzzleCountField.setPrefColumnCount(MAX_PUZZLE_EXPORT_DIGITS);			
		puzzleCountField.setTooltip(new Tooltip("How many puzzles are going to be generated"));
		
		browseButton.setOnAction(event -> onBrowse());
		
		puzzlesPerPageCombo.getItems().addAll("4", "2", "1");
		puzzlesPerPageCombo.getSelectionModel().select(0);
		
		puzzleDifficultyCheck.setSelected(true);
		puzzleNumberingCheck.setSelected(true);
		
		creationModeCombo.getItems().addAll(Resources.getTranslation("generate.new_puzzle"),
				Resources.getTranslation("generate.blank_puzzle"));		
		creationModeCombo.getSelectionModel().select(0);
		
		for(final Grading grading : Grading.values()) {
			gradingCheckCombo.getItems().add(grading.getDescription());
		}
		gradingCheckCombo.getCheckModel().checkAll();
		
		for(final Symmetry symmetry : Symmetry.values()) {
			symmetryCheckCombo.getItems().add(symmetry.getDescription());
		}		
		symmetryCheckCombo.getCheckModel().checkAll();
		
		symbolTypeCheckCombo.getItems().addAll(Resources.getTranslation("symbols.digits"),
				Resources.getTranslation("symbols.letters"));
		symbolTypeCheckCombo.getCheckModel().checkAll();
		
		puzzleOrderCombo.getItems().addAll(RANDOM, Resources.getTranslation("generate.difficulty"));
		puzzleOrderCombo.getSelectionModel().select(0);
		
		window.setHeaderText(null);
		window.setTitle(Resources.getTranslation("generate.export.title"));
		
		final ButtonType exportButtonType = new ButtonType(
				Resources.getTranslation("export.title"), ButtonData.OK_DONE);
		
		final ButtonType cancelButtonType = new ButtonType(
				Resources.getTranslation("button.cancel"), ButtonData.CANCEL_CLOSE);
		
		window.getDialogPane().getButtonTypes().addAll(exportButtonType, cancelButtonType);
		
		final Button okButton = (Button)window.getDialogPane().lookupButton(exportButtonType);	
		okButton.setDisable(true);		
		okButton.addEventFilter(ActionEvent.ACTION, event -> {
			//Prevent window from closing until player input has been validated
			if(!validateInput() || !onOverwriteExistingFile()) {
				event.consume();
			}
		});
		
		setupInputValidation(okButton);
		
		//window.setResizable(true);
		window.getDialogPane().setContent(layoutContent());
	}
	
	private boolean onOverwriteExistingFile() {
		final boolean outputPathExists = new File(outputPathField.getText()).exists();
		if(outputPathExists) {
			final Alert confirmFileReplaceAlert = new Alert(AlertType.CONFIRMATION);
			confirmFileReplaceAlert.initOwner(window.getOwner());
			confirmFileReplaceAlert.setContentText(Resources.getTranslation("file.exists.message"));
			confirmFileReplaceAlert.setTitle(Resources.getTranslation("file.exists.title"));				
			confirmFileReplaceAlert.setHeaderText(null);
			
			final Optional<ButtonType> playerChoice = confirmFileReplaceAlert.showAndWait();
			if(playerChoice.get() != ButtonType.OK) {
				return false;
			}
		}
		return true;
	}
	
	private void setupInputValidation(final Button exportButton) {
		creationModeCombo.setOnAction(event -> onCreationModeChanged(exportButton));
		outputPathField.textProperty().addListener((observable, oldValue, newValue) ->
			exportButton.setDisable(!validateInput())
		);
		puzzleCountField.textProperty().addListener((observable, oldValue, newValue) -> 
			exportButton.setDisable(!validateInput())
		);			
		puzzleCountField.addEventFilter(KeyEvent.KEY_TYPED, event -> {
			//Disallow typing anything else than numbers
			if (!COUNT_VALIDATION_STRING.contains(event.getCharacter()) ||
					puzzleCountField.getText().length() == MAX_PUZZLE_EXPORT_DIGITS) {
                event.consume();
            }
		});		
		symmetryCheckCombo.getCheckModel().getCheckedItems().addListener(
				(ListChangeListener.Change<? extends String> change) ->
					exportButton.setDisable(!validateInput()));		
		gradingCheckCombo.getCheckModel().getCheckedItems().addListener(
				(ListChangeListener.Change<? extends String> change) ->
					exportButton.setDisable(!validateInput()));
		symbolTypeCheckCombo.getCheckModel().getCheckedItems().addListener(
				(ListChangeListener.Change<? extends String> change) ->
					exportButton.setDisable(!validateInput()));
		
		final ValidationSupport validationSupport = new ValidationSupport();
		validationSupport.registerValidator(puzzleCountField, 
				Validator.createEmptyValidator("Text is required"));
	}
	
	private Pane layoutContent() {
		setComponentWidths(300);
		
		final Pane browsePane = buildBrowsePane();		
		final Pane generatorOptionsPane = buildGeneratorPane();
		final Pane formattingOptionsPane = buildFormattingPane(); 
		final Pane pdfOptionsPane = buildPdfOptionsPane();
		
		BorderPane.setMargin(outputPathField, new Insets(0,5,0,5));
		BorderPane.setMargin(browseButton, new Insets(0,5,0,5));
		
		final Node borderedBrowsePane = Borders.wrap(
				browsePane).etchedBorder().title(
						Resources.getTranslation("export.border.output")).buildAll();
		
		final Node borderedGeneratorOptionsPane = Borders.wrap(
				generatorOptionsPane).etchedBorder().title(
						Resources.getTranslation("export.border.generator")).buildAll();
		
		final Node borderedFormattingOptionsPane = Borders.wrap(
				formattingOptionsPane).etchedBorder().title("Formatting options").buildAll();
		
		final Node borderedPdfOptionsPane = Borders.wrap(
				pdfOptionsPane).etchedBorder().title("PDF printing options").buildAll();
		
		final VBox mainPane = new VBox(8);	
		mainPane.getChildren().addAll(borderedBrowsePane, borderedGeneratorOptionsPane,
				borderedFormattingOptionsPane, borderedPdfOptionsPane);
		
		return mainPane;
	}
	
	private void setComponentWidths(final double width) {
		//mainPane.prefWidthProperty().bind(window.widthProperty());
		//mainPane.prefHeightProperty().bind(window.heightProperty());
		
		symmetryCheckCombo.setMinWidth(width);
		symmetryCheckCombo.setMaxWidth(width);
		
		gradingCheckCombo.setMinWidth(width);
		gradingCheckCombo.setMaxWidth(width);
		
		//creationModeCombo.setMinWidth(width);
		//creationModeCombo.setMaxWidth(width);
	}
	
	private Pane buildPdfOptionsPane() {
		final VBox pdfOptionsPane = new VBox(8);
		
		pdfOptionsPane.getChildren().addAll(puzzleNumberingCheck, puzzleDifficultyCheck,
				fillPencilmarksCheck, appendSolutionsCheck);
		
		return pdfOptionsPane;
	}
	
	private Pane buildFormattingPane() {
		final GridPane formattingPane = new GridPane();
		formattingPane.setPadding(new Insets(10, 10, 10, 10));
		formattingPane.setHgap(5);
		formattingPane.setVgap(10);
		
		formattingPane.add(new Label(Resources.getTranslation("export.puzzle_ordering") + ": "), 0, 0);
		formattingPane.add(puzzleOrderCombo, 1, 0);
		formattingPane.add(new Label(Resources.getTranslation("export.puzzles_per_page") + ": "), 0, 1);
		formattingPane.add(puzzlesPerPageCombo, 1, 1);
		
		return formattingPane;
	}
	
	private Pane buildGeneratorPane() {
		final GridPane generatorOptionsPane = new GridPane();
		generatorOptionsPane.setPadding(new Insets(10, 10, 10, 10));
		generatorOptionsPane.setHgap(5);
		generatorOptionsPane.setVgap(10);
		
		generatorOptionsPane.add(new Label(Resources.getTranslation("puzzle.create") + ": "), 0, 0);
		generatorOptionsPane.add(creationModeCombo, 1, 0);
		generatorOptionsPane.add(new Label(Resources.getTranslation("symbols.label") + ":"), 0, 1);
		generatorOptionsPane.add(symbolTypeCheckCombo, 1, 1);
		generatorOptionsPane.add(new Label(Resources.getTranslation("generate.difficulty") + ":"), 0, 2);
		generatorOptionsPane.add(gradingCheckCombo, 1, 2);
		generatorOptionsPane.add(new Label(Resources.getTranslation("symmetry.name") + ":"), 0, 3);
		generatorOptionsPane.add(symmetryCheckCombo, 1, 3);	
		generatorOptionsPane.add(new Label(Resources.getTranslation("export.puzzle_count") + ":"), 0, 4);
		generatorOptionsPane.add(puzzleCountField, 1, 4);		
		
		return generatorOptionsPane;
	}
	
	private Pane buildBrowsePane() {
		final BorderPane browsePane = new BorderPane();				
		browsePane.setPadding(new Insets(10, 10, 10, 10));		
		browsePane.setCenter(outputPathField);
		browsePane.setRight(browseButton);
		
		return browsePane;
	}
	
	private void onBrowse() {
		final DirectoryChooser pathChooser = new DirectoryChooser();
		pathChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		pathChooser.setTitle("Browse");
		
		final File selectedFile = pathChooser.showDialog(window.getOwner());		
		
		if(selectedFile != null) {
			final String path = selectedFile.getAbsolutePath().concat(
					System.getProperty("file.separator").concat(DEFAULT_OUTPUT_FILE_NAME));
			outputPathField.setText(path);
		}
	}
	
	private boolean validateInput() {
		final String puzzleCount = puzzleCountField.getText().trim();
		if("".equals(puzzleCount) || puzzleCount.length() > MAX_PUZZLE_EXPORT_DIGITS) {
			return false;
		}
		if(Integer.parseInt(puzzleCount) == 0) {
			return false;
		}
		if("".equals(outputPathField.getText().trim())) {
			return false;
		}
		if(creationModeCombo.getSelectionModel().getSelectedIndex() == 0) {
			return symmetryCheckCombo.getCheckModel().getCheckedItems().size() > 0 &&
					gradingCheckCombo.getCheckModel().getCheckedItems().size() > 0 &&
					symbolTypeCheckCombo.getCheckModel().getCheckedItems().size() > 0;
		}
		return true;
	}
}