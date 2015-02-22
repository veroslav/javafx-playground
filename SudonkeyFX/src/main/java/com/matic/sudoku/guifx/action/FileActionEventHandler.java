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

import java.io.File;

import javafx.stage.FileChooser;
import javafx.stage.Window;

import com.matic.sudoku.guifx.window.GameWindowFX;
import com.matic.sudoku.guifx.window.PdfExporterOptions;
import com.matic.sudoku.guifx.window.PdfExporterWindow;

public class FileActionEventHandler {

	public void onOpenFileAction(final Window parent, final GameWindowFX gameWindow) {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("SudoCue puzzles", "*.sdk"));
		fileChooser.setTitle("Open puzzle");
		
		final File selectedFile = fileChooser.showOpenDialog(parent);
		
		if(selectedFile != null) {
			//TODO: Open selected file
			gameWindow.onUpdateRecentFileList(selectedFile.getAbsolutePath());
		}
	}
	
	public void onExportToPdfAction(final Window parent) {
		final PdfExporterWindow pdfExporterWindow = new PdfExporterWindow(parent);
		final PdfExporterOptions pdfExporterOptions = pdfExporterWindow.showAndWait();
		
		System.out.println(pdfExporterOptions);
	}
	
	public void onExportToImageAction(final Window parent) {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().addAll(                
				new FileChooser.ExtensionFilter("PNG images", "*.png"),
                new FileChooser.ExtensionFilter("JPG images", "*.jpeg", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF images", "*.gif"));
		fileChooser.setTitle("Export as Image");
		
		final File selectedFile = fileChooser.showSaveDialog(parent);
		
		if(selectedFile != null) {
			//Export puzzle to image file
			System.out.println(selectedFile.getAbsolutePath());
		}
	}
}