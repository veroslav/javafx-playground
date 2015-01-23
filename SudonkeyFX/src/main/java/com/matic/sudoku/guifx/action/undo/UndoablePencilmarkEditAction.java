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

package com.matic.sudoku.guifx.action.undo;

import com.matic.sudoku.Resources;
import com.matic.sudoku.guifx.board.ClassicGameBoard;

public class UndoablePencilmarkEditAction extends UndoableGameBoardAction {

	public static final String INSERT_PENCILMARK_PRESENTATION_NAME =
			Resources.getTranslation("action.add_pencilmark");
	public static final String DELETE_PENCILMARK_PRESENTATION_NAME =
			Resources.getTranslation("action.delete_pencilmark");
	
	/**
	* Undoable action generated when a pencilmark is added/removed by the player
	* 
	* @param presentationName Friendly action description name used in menus
	* @param board Board that was target of this action
	* @param row Action target row index
	* @param column Action target column index
	* @param deleted true if pencilmarks were deleted, false if a pencilmark was added
	* @param oldValues Pencilmark values prior to this modification
	*/
	public UndoablePencilmarkEditAction(final String presentationName, final ClassicGameBoard board, 
			final int row, final int column, final boolean deleted, final int... oldValues) {	
	}
}
