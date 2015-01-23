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

import com.matic.sudoku.guifx.action.undo.UndoableGameBoardAction;
import com.matic.sudoku.guifx.board.ClassicGameBoard;

import javafx.scene.input.KeyEvent;

/**
 * A handler for taking care of detected key events, such as when a key is typed.
 * We need a separate event handler for these type of events, because there is
 * a need for handling the resulting undoable events, if any.
 * 
 * @author vedran
 *
 */
public class KeyActionEventHandler {
	
	private final ClassicGameBoard gameBoard;
	
	public KeyActionEventHandler(final ClassicGameBoard gameBoard) {
		this.gameBoard = gameBoard;
	}

	public UndoableGameBoardAction onKeyTyped(final KeyEvent event) {						
		final boolean editAllowed = true;
		final boolean focusOn = false;
		
		final UndoableGameBoardAction action = gameBoard.onKeyTyped(
				event.getCode(), editAllowed, focusOn);
		
		return action;
	}
}
