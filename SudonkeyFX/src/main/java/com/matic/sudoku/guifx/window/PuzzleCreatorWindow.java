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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import org.controlsfx.tools.Borders;

import com.matic.sudoku.Resources;
import com.matic.sudoku.generator.Generator.Symmetry;
import com.matic.sudoku.guifx.board.GameBoard;
import com.matic.sudoku.guifx.board.GameBoard.SymbolType;
import com.matic.sudoku.logic.LogicSolver.Grading;

/**
 * A window shown when the player wants to create a new puzzle.
 * It offers various puzzle creation options to choose from.
 * 
 * @author vedran
 *
 */
public class PuzzleCreatorWindow {
	
	private static final String RANDOM_SYMMETRY =
			Resources.getTranslation("generate.random");
	private static final String RANDOM_GRADING = 
			Resources.getTranslation("generate.random");
	
	private static final int[] GRID_DIMENSIONS = {GameBoard.DIMENSION_4x4,
		GameBoard.DIMENSION_9x9, GameBoard.DIMENSION_16x16};
	
	private final ComboBox<String> creationModeCombo;
	private final ComboBox<String> gradingCombo;
	private final ComboBox<String> symbolTypeCombo;
	private final ComboBox<String> symmetryCombo;
	private final ComboBox<String> gridDimensionCombo;
	
	private final Dialog<ButtonType> window;
	
	public PuzzleCreatorWindow(final Window owner) {		
		creationModeCombo = new ComboBox<>();
		gradingCombo = new ComboBox<>();
		symbolTypeCombo = new ComboBox<>();
		symmetryCombo = new ComboBox<>();
		gridDimensionCombo = new ComboBox<>();
		
		window = new Dialog<>();
		window.initOwner(owner);
		
		initComponents();
	}
	
	/**
	 * Open and show puzzle creator window and wait for and return user input, if any
	 * 
	 * @return Puzzle creation options selected by player, or null if cancelled
	 */
	public PuzzleCreatorOptions showAndWait() {		
		final Optional<ButtonType> result = window.showAndWait();
		 if(result.isPresent() && result.get() == ButtonType.OK) {
			 final boolean isEmptyGrid = creationModeCombo.getSelectionModel()
					 .getSelectedIndex() == 1;
			 final int gridDimensionIndex = gridDimensionCombo.getSelectionModel()
					 .getSelectedIndex(); 
			 
		     return new PuzzleCreatorOptions(isEmptyGrid, getGrading(), getSymbolType(),
		    		 getSymmetry(), GRID_DIMENSIONS[gridDimensionIndex]);
		 }
		 return null;
	}
	
	private void initComponents() {
		setMaxComboWidths(Resources.Gui.COMBOBOX_MAX_WIDTH);
		
		creationModeCombo.getItems().addAll(Resources.getTranslation("generate.new_puzzle"),
				Resources.getTranslation("generate.blank_puzzle"));
		creationModeCombo.setOnAction(event -> onCreationModeChanged());
		creationModeCombo.getSelectionModel().select(0);		
		
		gridDimensionCombo.getItems().addAll("4x4", "9x9", "16x16");
		gridDimensionCombo.setOnAction(event -> onGridDimensionChanged());
		gridDimensionCombo.getSelectionModel().select(1);
		
		symbolTypeCombo.getItems().addAll(Resources.getTranslation("symbols.digits"),
				Resources.getTranslation("symbols.letters"));
		symbolTypeCombo.getSelectionModel().select(0);
		
		for(final Grading grading : Grading.values()) {
			gradingCombo.getItems().add(grading.getDescription());
		}
		
		gradingCombo.getItems().add(RANDOM_GRADING);
		gradingCombo.getSelectionModel().select(0);
		
		for(final Symmetry symmetry : Symmetry.values()) {
			symmetryCombo.getItems().add(symmetry.getDescription());
		}
		
		symmetryCombo.getItems().add(RANDOM_GRADING);
		symmetryCombo.getSelectionModel().select(0);
		
		window.setHeaderText(null);
		window.setTitle(Resources.getTranslation("puzzle.new"));
		
		window.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		
		final BorderPane windowContent = new BorderPane();
		windowContent.setTop(buildContent());
		window.setResizable(true);
		window.getDialogPane().setContent(windowContent);		
	}

	private Pane buildContent() {
		final GridPane gridOptionsPane = new GridPane();
		Resources.Gui.configurePadding(gridOptionsPane);
		
		final ColumnConstraints labelColumnConstraints = new ColumnConstraints(
				Resources.Gui.MAX_LABEL_COLUMN_WIDTH);		
		final ColumnConstraints fieldColumnConstraints = new ColumnConstraints();		
		
		gridOptionsPane.getColumnConstraints().addAll(labelColumnConstraints, fieldColumnConstraints);
		
		final List<Label> gridOptionLabels = Arrays.asList(
				new Label(Resources.getTranslation("puzzle.create") + ":"),
				new Label(Resources.getTranslation("puzzle.type") + ":"));
		
		gridOptionLabels.stream().forEach(label -> GridPane.setHalignment(label, HPos.RIGHT));
				
		GridPane.setHalignment(creationModeCombo, HPos.LEFT);
		GridPane.setHalignment(gridDimensionCombo, HPos.LEFT);
		
		gridOptionsPane.add(gridOptionLabels.get(0), 0, 0);
		gridOptionsPane.add(creationModeCombo, 1, 0);
		
		gridOptionsPane.add(gridOptionLabels.get(1), 0, 1);	
		gridOptionsPane.add(gridDimensionCombo, 1, 1);
		
		final GridPane puzzleOptionsPane = new GridPane();
		Resources.Gui.configurePadding(puzzleOptionsPane);
		
		puzzleOptionsPane.getColumnConstraints().addAll(labelColumnConstraints, fieldColumnConstraints);
		
		final List<Label> puzzleOptionLabels = Arrays.asList(
				new Label(Resources.getTranslation("symmetry.name") + ":"),
				new Label(Resources.getTranslation("generate.difficulty") + ":"),
				new Label(Resources.getTranslation("symbols.label") + ":"));
		
		puzzleOptionLabels.stream().forEach(label -> GridPane.setHalignment(label, HPos.RIGHT));
				
		GridPane.setHalignment(symmetryCombo, HPos.LEFT);
		GridPane.setHalignment(gradingCombo, HPos.LEFT);
		GridPane.setHalignment(symbolTypeCombo, HPos.LEFT);
		
		puzzleOptionsPane.add(puzzleOptionLabels.get(0), 0, 0);
		puzzleOptionsPane.add(symmetryCombo, 1, 0);
		
		puzzleOptionsPane.add(puzzleOptionLabels.get(1), 0, 1);
		puzzleOptionsPane.add(gradingCombo, 1, 1);
		
		puzzleOptionsPane.add(puzzleOptionLabels.get(2), 0, 2);
		puzzleOptionsPane.add(symbolTypeCombo, 1, 2);
		
		final VBox contentPane = new VBox();
		
		final Node borderedGridOptionsPane = Borders.wrap(
				gridOptionsPane).etchedBorder().title("Grid options").buildAll();
		
		final Node borderedPuzzleOptionsPane = Borders.wrap(
				puzzleOptionsPane).etchedBorder().title("Puzzle options").buildAll();		
		
		contentPane.getChildren().addAll(borderedGridOptionsPane, borderedPuzzleOptionsPane);
		
		return contentPane;
	}
	
	private void setMaxComboWidths(final double width) {				
		creationModeCombo.setMaxWidth(width);
		gradingCombo.setMaxWidth(width);
		symbolTypeCombo.setMaxWidth(width);
		symmetryCombo.setMaxWidth(width);
		gridDimensionCombo.setMaxWidth(width);
	}
	
	private void onCreationModeChanged() {
		final int selectedModeIndex = creationModeCombo.getSelectionModel().getSelectedIndex();
		
		//Enable symmetry and difficulty options if creating a new puzzle
		if(selectedModeIndex == 0) {
			symmetryCombo.setDisable(false);
			gradingCombo.setDisable(false);
		}
		else {
			symmetryCombo.setDisable(true);
			gradingCombo.setDisable(true);
		}
	}
	
	private void onGridDimensionChanged() {
		final int gridDimension = GRID_DIMENSIONS[gridDimensionCombo.getSelectionModel()
				 .getSelectedIndex()];
		
		if(gridDimension == GameBoard.DIMENSION_16x16) {
			symbolTypeCombo.setDisable(true);
			symbolTypeCombo.getSelectionModel().select(
					Resources.getTranslation("symbols.letters"));
		}
		else {
			symbolTypeCombo.setDisable(false);
		}
	}
	
	private Grading getGrading() {
		String grading = gradingCombo.getSelectionModel().getSelectedItem();
		
		if(RANDOM_GRADING.equals(grading)) {
			final int randomGrading = Resources.RANDOM_INSTANCE.nextInt(
					gradingCombo.getItems().size() - 1);
			grading = gradingCombo.getItems().get(randomGrading);
		}
		return Grading.fromString(grading);
	}
	
	public SymbolType getSymbolType() {
		final String symbolType = symbolTypeCombo.getSelectionModel().getSelectedItem();
		return SymbolType.fromString(symbolType);
	}
	
	public Symmetry getSymmetry() {
		String symmetry = symmetryCombo.getSelectionModel().getSelectedItem();
		
		if(RANDOM_SYMMETRY.equals(symmetry)) {
			final int randomSymmetry = Resources.RANDOM_INSTANCE.nextInt(symmetryCombo.getItems().size() - 1);
			symmetry = symmetryCombo.getItems().get(randomSymmetry);
		}
		return Symmetry.fromString(symmetry);
	}
}