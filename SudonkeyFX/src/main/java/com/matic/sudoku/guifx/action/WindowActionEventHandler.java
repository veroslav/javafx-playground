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

import java.util.Optional;

import com.matic.sudoku.Resources;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * A handler for taking care of window related events, such as window closing.
 * 
 * @author vedran
 *
 */
public class WindowActionEventHandler {
	
	/**
	 * Handle player choosing to close the game.
	 * 
	 * @param event Originating window event
	 */
	public void onWindowClose(final Event event) {
		final boolean isClosed = handleWindowClosing();
        if(isClosed) {
            //User chose to close the application, quit
            Platform.exit();
        }
        else {
            //User cancelled quit action, don't do anything
            event.consume();
        }		
	}
	
	private boolean handleWindowClosing() {
		final Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
		confirmAlert.setContentText(Resources.getTranslation("game.quit.question"));
		confirmAlert.setTitle(Resources.getTranslation("game.quit.confirm"));				
		confirmAlert.setHeaderText(null);
		
		final Optional<ButtonType> result = confirmAlert.showAndWait();
		return result.get() == ButtonType.OK;
	}
}