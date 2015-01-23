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

import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Window;

import org.controlsfx.control.CheckComboBox;

import com.matic.sudoku.Resources;
import com.matic.sudoku.logic.LogicSolver.Grading;

/**
 * A window shown when the player wants to create and export puzzles to PDF.
 * It offers various puzzle creation and export options to choose from.
 * 
 * @author vedran
 *
 */
public class PuzzleExporterWindow {
	
	private final CheckComboBox<String> gradingCheckCombo;
	
	final Dialog<ButtonType> window;

	public PuzzleExporterWindow(final Window owner) {
		gradingCheckCombo = new CheckComboBox<>();
		
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
		if(result.isPresent() && result.get() == ButtonType.OK) {
			return new PuzzleExporterOptions();
		}
		return null;
	}
	
	private void initComponents() {
		for(final Grading grading : Grading.values()) {
			gradingCheckCombo.getItems().add(grading.getDescription());
		}
		gradingCheckCombo.getCheckModel().checkAll();
		
		window.setHeaderText(null);
		window.setTitle(Resources.getTranslation("generate.export.title"));
		
		final ButtonType exportButtonType = new ButtonType(
				Resources.getTranslation("export.title"), ButtonData.OK_DONE);
		
		window.getDialogPane().getButtonTypes().addAll(exportButtonType, ButtonType.CANCEL);
		
		final Button okButton = (Button)window.getDialogPane().lookupButton(exportButtonType);		
		okButton.addEventFilter(ActionEvent.ACTION, event -> {
			//Prevent window from closing until player input has been validated
			if(!validateInput()) {
				event.consume();
			}
		});
		
		window.getDialogPane().setContent(gradingCheckCombo);
	}
	
	private boolean validateInput() {
		return true;
	}
}