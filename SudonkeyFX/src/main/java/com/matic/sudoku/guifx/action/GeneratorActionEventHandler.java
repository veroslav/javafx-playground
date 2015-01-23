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

package com.matic.sudoku.guifx.action;

import com.matic.sudoku.guifx.board.ClassicGameBoard;
import com.matic.sudoku.guifx.window.PuzzleCreatorOptions;
import com.matic.sudoku.guifx.window.PuzzleCreatorWindow;
import com.matic.sudoku.guifx.window.PuzzleExporterOptions;
import com.matic.sudoku.guifx.window.PuzzleExporterWindow;

import javafx.event.ActionEvent;
import javafx.stage.Window;

public class GeneratorActionEventHandler {

	/**
	 * Act on player choosing to generate and show a new puzzle
	 * 
	 * @param event Originating action event
	 * @param owner Dialog owner
	 * @param gameBoard Game board to be populated with generated puzzle
	 */
	public void onGenerateNewPuzzle(final ActionEvent event, final Window owner,
			final ClassicGameBoard gameBoard) {	
		final PuzzleCreatorWindow creatorWindow = new PuzzleCreatorWindow();
		final PuzzleCreatorOptions creatorOptions = creatorWindow.showAndWait();
		
		if(creatorOptions == null) {
			//Player cancelled the creator window
			return;
		}
		
		final int selectedDimension = creatorOptions.getGridDimension();
		if(selectedDimension != gameBoard.getDimension()) {
			gameBoard.setDimension(selectedDimension);
		}
		
		//TODO: Process creatorResult
		System.out.println(creatorOptions);
	}
	
	/**
	 * Act on player choosing to generate and export puzzles to PDF
	 * 
	 *  @param owner Dialog owner
	 */
	public void onExportPuzzles(final Window owner) {
		final PuzzleExporterWindow exporterWindow = new PuzzleExporterWindow(owner);
		final PuzzleExporterOptions exporterOptions = exporterWindow.showAndWait();
		
		if(exporterOptions == null) {
			//Player cancelled the exporter window
			return;
		}
		
		//TODO: Process exporterOptions
		System.out.println(exporterOptions);
	}
}
