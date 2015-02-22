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

package com.matic.sudoku.guifx.board;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import com.matic.sudoku.guifx.action.undo.UndoableCellValueEditAction;
import com.matic.sudoku.guifx.action.undo.UndoableColorEditAction;
import com.matic.sudoku.guifx.action.undo.UndoableGameBoardAction;
import com.matic.sudoku.guifx.action.undo.UndoablePencilmarkEditAction;
import com.matic.sudoku.io.KeyInputManager;
import com.matic.sudoku.io.KeyInputManager.SymbolType;
import com.matic.sudoku.io.KeyInputValidationResult;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

public class ClassicGameBoard extends Canvas implements GameBoard {
	
	private static Color THICK_LINE_COLOR = Color.BLACK;
	private static Color INNER_LINE_COLOR = Color.BLACK;
	
	//Color of rectangular area surrounding an active cell
	private static final Color PICKER_COLOR = Color.rgb(220, 0, 0);
	
	private static Color DEFAULT_BACKGROUND_COLOR = Color.rgb(234,184,57);
	private static Color NORMAL_FONT_COLOR = Color.BLACK;
	private static final Color PENCILMARK_FONT_COLOR = Color.rgb(0, 43, 54);
	
	//Available colors the player can use for cell selections
	public static final Color[] CELL_SELECTION_COLORS = {DEFAULT_BACKGROUND_COLOR,
		Color.rgb(253, 188, 75), Color.rgb(255, 144, 150),
		Color.rgb(244, 119, 80), Color.rgb(29, 153, 243),
		Color.rgb(46, 204, 113)};
	
	//Color index of the board's default background color (white)
	private static final int DEFAULT_CELL_COLOR_INDEX = 0;
	
	//How wide a thick line should be relative to the board size (in percent)
	private static final double THICK_LINE_THICKNESS = 0.008; //0.012;
	
	//How wide an inner grid line should be relative to the board size (in percent)
	private static final double INNER_LINE_THICKNESS = 0.004;
	
	//How much space (in percent) around the board we should leave empty
	private static final double DRAWING_AREA_MARGIN = 0.08;
	
	//How big portion of a cell a digit should occupy when drawn (determines the font size)
	private static final double NORMAL_FONT_SIZE_PERCENT = 0.75; //0.8
	
	//How big portion of its allocated piece of a cell a pencilmark should occupy when drawn
	private static final double PENCILMARK_FONT_SIZE_PERCENT = 0.9;
	
	//Digit -> Symbol shown on the board - mapping
	private final Map<Integer, String> digitToSymbolMappings = new HashMap<>();
		
	//Symbol shown on the board -> Digit - mapping
	private final Map<String, Integer> symbolToDigitMappings = new HashMap<>();
		
	private SymbolType symbolType = null;
	
	//The symbol that is put into a cell after a player left-clicks on it
	private String mouseClickInputValue;
	
	//Color index of the color used to paint cells' backgrounds
	private int cellColorIndex;
	
	//How many cells have their background color changed
	private int colorCount;
	
	//How many symbols are on the board (givens + entered by the player)
	private int symbolsFilledCount;
	
	//How many pencilmarks has been entered
	private int pencilmarkCount;
	
	//A mask used for determining whether a candidate should be drawn on not (when focus is ON)
	private int pencilmarkFilterMask;
	
	//Board cells
	private Cell[][] cells;
	
	//Board size, for a 9x9 board, the dimension is 3
	private int dimension = -1;
	
	//Size of a region (box, row or column), 9 for a 9x9 board
	public int unit = -1;
	
	//Width of a board's inner box (in pixels) on screen
	private int boxWidth;
	
	//Width of the board on the screen (in pixels, including the surrounding thick grid lines)
	private int boardWidth;
	
	//Thickness (in pixels) of the thick grid lines (surrounding the board and separating boxes)
	private int thickLineWidth;
	
	//Thickness (in pixels) of the inner grid lines (separating the cells)
	private int innerLineWidth;
	
	//Row index of the currently active cell (indicated by the cell picker)
	private int cellPickerRow;
	
	//Column index of the currently active cell (indicated by the cell picker)
	private int cellPickerCol;
	
	//x-coordinate for the start of the board (including the thick border line)
	private int boardStartX;
	
	//y-coordinate for the start of the board (including the thick border line)
	private int boardStartY;
	
	//Distance (in pixels) between two adjacent inner grid lines
	private int cellWidth;
	
	//Area within a cell available to a pencilmark to draw itself (cellWidth / dimension)
	private int pencilmarkWidth;
	
	//Font used for drawing pencilmarks
	private Font pencilmarkFont;
	
	//Font used for drawing digits enter by the player
	private Font playerDigitFont;
	
	//Font used for drawing givens in a puzzle
	private Font givenDigitFont;
	
	//Currently set color for paint board and cell background
	private Color backgroundColor;
	
	public ClassicGameBoard(final int dimension, final SymbolType symbolType) {		
		this.dimension = dimension;
		unit = dimension * dimension;		
		
		this.widthProperty().addListener(observable -> {			
			updateDimensions();
			draw(true, true);
		});
		this.heightProperty().addListener(observable -> {
			updateDimensions();
			draw(true, true);
		});
		
		cellColorIndex = 1;
		
		//Draw all pencilmarks by default (focus OFF)
		pencilmarkFilterMask = -1;
		
		backgroundColor = DEFAULT_BACKGROUND_COLOR;
		
		symbolsFilledCount = cellPickerCol = cellPickerRow = 0;
		colorCount = pencilmarkCount = 0;
		
		onGridChanged(dimension, symbolType);
	}
	
	@Override
	public double prefWidth(final double width) {
		return this.getWidth();
	};
	
	@Override
	public double prefHeight(final double height) {
		return this.getHeight();
	};
	
	@Override
	public boolean isResizable() {
		return true;
	}

	@Override
	public int getDimension() {		
		return dimension;
	}
	
	/**
	 * Update grid dimensions and allowed key input values based on new grid properties
	 * 
	 * @param dimension New grid dimension
	 * @param symbolType New symbol type
	 */
	public void onGridChanged(final int dimension, final SymbolType symbolType) {
		if(this.dimension == dimension && this.symbolType == symbolType) {
			return;
		}
		
		this.symbolType = dimension > GameBoard.DIMENSION_9x9 && symbolType == SymbolType.DIGITS? 
				SymbolType.LETTERS : symbolType;
		this.dimension = dimension;
		unit = dimension * dimension;
		
		digitToSymbolMappings.clear();
		symbolToDigitMappings.clear();
		
		int symbolCount = 1;
		while(symbolCount <= unit) {
			if(this.symbolType == SymbolType.LETTERS) {
				digitToSymbolMappings.put(symbolCount, KeyInputManager.LETTER_KEY_ACTION_NAMES[symbolCount - 1]);
				symbolToDigitMappings.put(KeyInputManager.LETTER_KEY_ACTION_NAMES[symbolCount - 1], symbolCount);
			}
			else {				
				digitToSymbolMappings.put(symbolCount, String.valueOf(symbolCount));
				symbolToDigitMappings.put(String.valueOf(symbolCount), symbolCount);				
			}
			++symbolCount;
		}		
		
		mouseClickInputValue = digitToSymbolMappings.get(KeyInputManager.DIGIT_KEY_ACTION_VALUES[0]);
		
		initCells(dimension);
		updateDimensions();
		draw(true, true);
	}
	
	/**
	* Update a cell's value
	* @param row Row for the cell to be updated
	* @param column Column for the cell to be updated
	* @param value Value to set
	*/
	public void setCellValue(final int row, final int column, final int value) {				
		if(cells[column][row].getDigit() == 0 && value > 0) {
			//New symbol entered, increase symbols filled count
			++symbolsFilledCount;
		}
		if(cells[column][row].getDigit() > 0 && value == 0) {
			//A symbol has been removed, decrease symbols filled count
			--symbolsFilledCount;
		}
		cells[column][row].setDigit(value);
		draw(true, true);
	}
	
	/**
	* Update the mask used for determining which pencilmarks get to be drawn
	* @param pencilmarkFilterMask New mask filter value
	*/
	public void setPencilmarkMask(final int pencilmarkFilterMask) {
		this.pencilmarkFilterMask = pencilmarkFilterMask;
		draw(true, true);
	}
	
	/**
	* Update a cell's pencilmark values
	* 
	* @param row Row for the cell to be updated
	* @param column Column for the cell to be updated
	* @param pencilmarkEntered Whether to show or hide the pencilmark
	* @param clearOldValues Whether to delete all current pencilmarks in this cell
	* @param values Pencilmark value(s) to set
	*/
	public void setPencilmarkValues(final int row, final int column, final boolean pencilmarkEntered,
			final boolean clearOldValues, final int... values) {
		if(clearOldValues) {
			final int oldCount = cells[column][row].getPencilmarkCount();
			pencilmarkCount -= oldCount;
			cells[column][row].clearPencilmarks();
		}
		for(final int value : values) {
			final boolean isSet = cells[column][row].isPencilmarkSet(value);
			if(!isSet && pencilmarkEntered) {
				++pencilmarkCount;
			}
			else if(isSet && !pencilmarkEntered) {
				--pencilmarkCount;
			}
			cells[column][row].setPencilmark(value, pencilmarkEntered);
		}
		draw(true, true);
	}
	
	/**
	 * Set the background color index to be used when the player clicks on a cell to apply a color
	 * @param colorIndex Cell selection color's index
	 */
	public void setCellColorIndex(final int cellColorIndex) {
		this.cellColorIndex = cellColorIndex;
	}
	
	private void setCellPicker(final double mouseX, final double mouseY) {
		final int boxDistance = boxWidth + thickLineWidth;
		//Find the correct column index
		for(int i = 0, x = boardStartX + boxDistance; i < dimension; ++i, x += boxDistance) {
			if(mouseX < x) {
				//We found the right box, look for right cell's column index
				cellPickerCol = getColumnAt(mouseX, x - boxWidth, i);
				break;
			}
		}
		//Find the correct row index
		for(int i = 0, y = boardStartY + boxDistance; i < dimension; ++i, y += boxDistance) {
			if(mouseY < y) {
				//We found the right box, look for right cell's row index
				cellPickerRow = getRowAt(mouseY, y - boxWidth, i);
				break;
			}
		}
	}
	
	private int getColumnAt(final double mouseX, final int boxBeginX, final int boxIndex) {
		final int index = (int)(boxIndex * dimension + 
				((mouseX - boxBeginX) / (cellWidth + innerLineWidth)));
		return index >= unit? unit - 1 : index;
	}
	
	private int getRowAt(final double mouseY, final int boxBeginY, final int boxIndex) {
		final int index = (int)(boxIndex * dimension + 
				((mouseY - boxBeginY) / (cellWidth + innerLineWidth)));
		return index >= unit? unit - 1 : index;
	}

	public void draw(final boolean paintBackground, final boolean drawPicker) {			
		//Don't draw while the components are still being initialized
		if(boxWidth <= 0) {
			return;
		}
		
		final GraphicsContext context = this.getGraphicsContext2D();
		
		if(paintBackground) {
			drawBackground(context);
		}		
		
		drawThickLines(context);
		drawInnerLines(context);
		
		renderCells(context, drawPicker);
	}
	
	private void drawBackground(final GraphicsContext context) {
		context.clearRect(0, 0, this.getWidth(), this.getHeight());
		
		context.setFill(DEFAULT_BACKGROUND_COLOR);
		context.fillRect(0, 0, this.getWidth(), this.getHeight());
	}
	
	private void drawThickLines(final GraphicsContext context) {		
		context.setFill(THICK_LINE_COLOR);
		
		final int lineDistance = thickLineWidth + boxWidth;
				
		//Draw horizontal board lines
		for(int i = 0, j = boardStartY; i < dimension + 1; ++i, j += lineDistance) {			
			context.fillRect(boardStartX, j, boardWidth, thickLineWidth);
		}
		//Draw vertical board lines
		for(int i = 0, j = boardStartX; i < dimension + 1; ++i, j += lineDistance) {			
			context.fillRect(j, boardStartY, thickLineWidth, boardWidth);
		}		
	}
	
	private void drawInnerLines(final GraphicsContext context) {
		//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		context.setFill(INNER_LINE_COLOR);
		
		final int lineDistance = cellWidth + innerLineWidth;
		final int boxDistance = boxWidth + thickLineWidth;
		
		//Draw horizontal inner lines
		for(int i = 0, j = boardStartY + thickLineWidth; i < dimension; ++i, j += boxDistance) {
			for(int k = 0, m = j + cellWidth; k < dimension - 1; ++k, m += lineDistance) {
				context.fillRect(boardStartX, m, boardWidth, innerLineWidth);
			}
		}
		
		//Draw vertical inner lines
		for(int i = 0, j = boardStartX + thickLineWidth; i < dimension; ++i, j += boxDistance) {
			for(int k = 0, m = j + cellWidth; k < dimension - 1; ++k, m += lineDistance) {
				context.fillRect(m, boardStartY, innerLineWidth, boardWidth);
			}
		}
	}
	
	private void initCells(final int dimension) {
		cells = new Cell[unit][unit];
		
		for(int i = 0; i < unit; ++i) {
			for(int j = 0; j < unit; ++j) {
				cells[i][j] = new Cell(0, NORMAL_FONT_COLOR);
			}
		}
	}
	
	private void renderCells(final GraphicsContext context, final boolean drawPicker) {
		final int cellDistance = cellWidth + innerLineWidth;
		final int boxDistance = boxWidth + thickLineWidth;
		
		int pickerCellX = 0;
		int pickerCellY = 0;
		
		//Iterate over each column
		for(int col = 0, boxX = boardStartX + thickLineWidth, cellX = boxX; col < unit;
				++col, cellX += cellDistance) {
			//Account for each box's surrounding line
			if(col > 0 && col % dimension == 0) {
				boxX += boxDistance;
				cellX = boxX;
			}
		
			//Iterate over each row
			for(int row = 0, boxY = boardStartY + thickLineWidth, cellY = boxY; row < unit;
					++row, cellY += cellDistance) {
				//Account for each box's surrounding line
				if(row > 0 && row % dimension == 0) {
					boxY += boxDistance;
					cellY = boxY;
				}
				renderCellContent(context, cells[col][row], cellX, cellY);
				
				if(cellPickerRow == row && cellPickerCol == col) {
					pickerCellX = cellX;
					pickerCellY = cellY;
				}
			}
		}
		
		if(drawPicker) {
			drawPicker(context, pickerCellX, pickerCellY);
		}
	}
	
	private void renderCellContent(final GraphicsContext context, final Cell cell, final int cellX, final int cellY) {		
		// Set the cell's background color and draw it
		context.setFill(CELL_SELECTION_COLORS[cell.getBackgroundColorIndex()]);
		context.fillRect(cellX, cellY, cellWidth, cellWidth);
		
		final int digit = cell.getDigit();
		if(digit > 0) {
			drawCellValue(context, cell, cellX, cellY, digit);
		} 
		else if(cell.getPencilmarkCount() > 0) {
			// Set pencilmark font and color and draw this cell's pencilmarks
			drawCellPencilmarks(context, cell, cellX, cellY);
		}
	}
	
	private void drawCellValue(final GraphicsContext context, final Cell cell,
			final int cellX, final int cellY, final int value) {
		// Set font and font color for this cell and draw entered digit value
		if(cell.isGiven()) {
			context.setFont(givenDigitFont);
		} 
		else {
			context.setFont(playerDigitFont);
		}		
		context.setFill(cell.getFontColor());
		
		final String symbol = digitToSymbolMappings.get(value);
		
		final FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
		final Font contextFont = context.getFont();
		final FontMetrics fontMetrics = fontLoader.getFontMetrics(contextFont);
		
		final float fontHeight = fontMetrics.getLineHeight();
		final float fontWidth = fontLoader.computeStringWidth(symbol, contextFont);	
		final float fontAscent = fontMetrics.getAscent();		
		
		context.fillText(symbol, cellX + (int) ((cellWidth - fontWidth) / 2.0 + 0.5),
				cellY + (int) ((cellWidth - fontHeight) / 2.0 + 0.5) + fontAscent);
	}
	
	private void drawCellPencilmarks(final GraphicsContext context, final Cell cell, final int cellX, final int cellY) {
		context.setFont(pencilmarkFont);
		context.setFill(PENCILMARK_FONT_COLOR);
					
		final FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
		final Font contextFont = context.getFont();
		final FontMetrics fontMetrics = fontLoader.getFontMetrics(contextFont);
		
		int pencilmark = 1;
		for(int i = 0, y = cellY; i < dimension; ++i, y += pencilmarkWidth) {
			for(int j = 0, x = cellX; j < dimension; ++j, x += pencilmarkWidth) {
				final boolean pencilmarkHasFocus = (pencilmarkFilterMask & (1 << (pencilmark - 1))) != 0;
				if(cell.isPencilmarkSet(pencilmark) && pencilmarkHasFocus) {
					final String symbol = digitToSymbolMappings.get(pencilmark);
					final float fontHeight = fontMetrics.getLineHeight();
					final float fontWidth = fontLoader.computeStringWidth(symbol, contextFont);	
					
					context.fillText(symbol, x + (int)((pencilmarkWidth - fontWidth) / 2.0 + 0.5),
							y + (int)((pencilmarkWidth - fontHeight) / 2.0 + 0.5) + fontMetrics.getAscent());					
				}
				++pencilmark;
			}
		}
	}
	
	private void drawPicker(final GraphicsContext context, final int x, final int y) {		
		context.setStroke(PICKER_COLOR);
		context.setLineWidth(thickLineWidth + 1);
		
		context.strokeRect(x, y, cellWidth, cellWidth);
	}
	
	private void updateDimensions() {		
		// Either width or height is smaller, this is our available drawing area (with margins)
		final int totalDrawArea = Math.min((int)this.getWidth(), (int)this.getHeight());
		
		// Actual usable drawing area, margins not included
		final int usableDrawArea = totalDrawArea - (int) (DRAWING_AREA_MARGIN * totalDrawArea);
		
		thickLineWidth = (int)(THICK_LINE_THICKNESS * usableDrawArea);
		innerLineWidth = (int)(INNER_LINE_THICKNESS * usableDrawArea);
		
		// Prevent line from not being drawn if too thin
		if(thickLineWidth == 0) {
			thickLineWidth = 1;
		}
		
		// Prevent line from not being drawn if too thin
		if(innerLineWidth == 0) {
			innerLineWidth = 1;
		}
		
		// How many horizontal/vertical thick lines there are
		final int thickLinesCount = dimension + 1;
		
		// How many horizontal/vertical inner lines there are
		final int innerLinesCount = dimension * (dimension - 1);
		
		final int innerLinesWidthInBox = (dimension - 1) * innerLineWidth;
		
		// How many pixels of drawing area are occupied by thick and inner lines
		final int totalLineWidth = thickLinesCount * thickLineWidth + innerLinesCount * innerLineWidth;
		
		// Area remaining for a cell to be drawn after subtracting grid lines from drawing area
		cellWidth = (usableDrawArea - totalLineWidth) / unit;		
		pencilmarkWidth = cellWidth / dimension;
		
		boardWidth = cellWidth * unit + totalLineWidth;		
		boardStartX = (int)this.getWidth() / 2 - (boardWidth / 2);
		boardStartY = (int)this.getHeight() / 2 - (boardWidth / 2);
		
		boxWidth = dimension * cellWidth + innerLinesWidthInBox;	
		
		pencilmarkFont = Font.font("Monospaced", FontWeight.BOLD, PENCILMARK_FONT_SIZE_PERCENT * pencilmarkWidth);
		playerDigitFont = Font.font("DejaVu Sans", FontWeight.NORMAL, NORMAL_FONT_SIZE_PERCENT * cellWidth);
		givenDigitFont = Font.font("DejaVu Sans", FontWeight.BOLD, NORMAL_FONT_SIZE_PERCENT * cellWidth);
	}
	
	/**
	 * Handle mouse movement events. This is used for detecting correct picker cell.
	 * 
	 * @param mouseX The mouse X coordinate
	 * @param mouseY The mouse Y coordinate
	 */
	public void onMouseMoved(final double mouseX, final double mouseY) {
		if(isMouseOutsideBoard(mouseX, mouseY)) {
			// Mouse pointer outside of the board, don't do anything
			return;
		}
		// Mouse pointer inside the board, find underlying cell and let cell picker select it
		setCellPicker(mouseX, mouseY);
		draw(true, true);
	}
	
	/**
	 * Handle mouse click events. This is used for cell editing (typing and deleting entries).
	 * 
	 * @param mouseX The mouse X coordinate
	 * @param mouseY The mouse Y coordinate
	 * @param editAllowed Whether the player is allowed to edit cell contents at this moment
	 * @param focusOn If focus is ON, prevent player from deleting pencilmarks
	 * @return Undoable key action, or null if no such is possible for originating click
	 */
	public UndoableGameBoardAction onMouseClicked(final MouseEvent event,
			final boolean editAllowed, final boolean focusOn) {	
		final double mouseX = event.getX();
		final double mouseY = event.getY();
		
		if(isMouseOutsideBoard(mouseX, mouseY)) {
			// Mouse pointer outside of the board, don't do anything			
			return null;
		}
		
		// Mouse pointer inside the board, find underlying cell and let cell picker select it
		setCellPicker(mouseX, mouseY);
		draw(true, true);
		
		switch (event.getButton()) {
			case PRIMARY:
				// Left-button click (either digit or color selection entry)
				if(event.isControlDown()) {
					//Control button was down when mouse was clicked, color selection entry
					return editAllowed? handleColorSelection() : null;
				}
				else {
					//Control button was not pressed, a simple digit entry
					return editAllowed? handleSymbolEntry(mouseClickInputValue, false) : null;
				}
			case SECONDARY:
				// Right-button click (pencil mark entry)
				final boolean pencilmarkAllowed = !focusOn && editAllowed &&
					cells[cellPickerCol][cellPickerRow].getDigit() == 0;
				return pencilmarkAllowed? handleSymbolEntry(mouseClickInputValue, true) : null;
			default:
				return null;
		}
	}
	
	private boolean isMouseOutsideBoard(final double mouseX, final double mouseY) {
		return (mouseX < boardStartX || mouseX > boardStartX + boardWidth)
				|| (mouseY < boardStartY || mouseY > boardStartY + boardWidth);
	}
	
	/**
	 * Perform appropriate repaints depending on the key typed. If the key action is undoable,
	 * return it, otherwise return null
	 * 
	 * @param keyCode Key code of the key typed
	 * @param editAllowed Whether the player is allowed to edit cell contents at this moment
	 * @param focusOn If focus is ON, prevent player from deleting pencilmarks
	 * @return Undoable key action, or null if no such is possible for given key
	 */
	public UndoableGameBoardAction onKeyTyped(final KeyCode keyCode, final boolean editAllowed,
			final boolean focusOn) {		
		final KeyInputValidationResult validationResult = KeyInputManager.validateKeyInput(keyCode, dimension, symbolType);
		
		switch(validationResult.getValidationResult()) {
		case INVALID_INPUT:
			return null;
		case DIRECTION_CHANGE:
			handleDirectionChange(keyCode);
			break;
		case SYMBOL_ENTRY:
			// Check whether the player possibly has entered a digit			
			return !editAllowed? null : handleSymbolEntry(validationResult.getValue(), false);	
		case SYMBOL_DELETION:
			return handleSymbolDeletion(editAllowed, focusOn);
		}
		
		draw(true, true);
		return null;
	}
	
	private UndoableGameBoardAction handleSymbolDeletion(final boolean editAllowed, final boolean focusOn) {		
		if(!editAllowed || cells[cellPickerCol][cellPickerRow].isGiven()) {
			return null;
		}
		final int oldCellValue = cells[cellPickerCol][cellPickerRow].getDigit();
		if(oldCellValue > 0) {
			// Delete previously entered cell digit
			setCellValue(cellPickerRow, cellPickerCol, 0);
			return new UndoableCellValueEditAction(
					UndoableCellValueEditAction.DELETE_SYMBOL_PRESENTATION_NAME,
					this, cellPickerRow, cellPickerCol, oldCellValue, 0);
		} else {
			if (focusOn) {
				return null;
			}
			// Delete all of the pencilmarks in this cell
			final int[] oldPencilmarkValues = cells[cellPickerCol][cellPickerRow]
					.getSetPencilmarks();
			setPencilmarkValues(cellPickerRow, cellPickerCol, false, true);
			return new UndoablePencilmarkEditAction(
					UndoablePencilmarkEditAction.DELETE_PENCILMARK_PRESENTATION_NAME,
					this, cellPickerRow, cellPickerCol, true,
					oldPencilmarkValues);
		}
	}
	
	private void handleDirectionChange(final KeyCode keyInput) {
		switch(keyInput) {
		case UP:
			cellPickerRow = cellPickerRow - 1 > -1 ? --cellPickerRow : unit - 1;
			break;
		case DOWN:
			cellPickerRow = ++cellPickerRow % unit;
			break;
		case LEFT:
			cellPickerCol = cellPickerCol - 1 > -1 ? --cellPickerCol : unit - 1;
			break;
		case RIGHT:
			cellPickerCol = ++cellPickerCol % unit;
			break;
		default:
			return;
		}
	}
	
	private UndoableGameBoardAction handleSymbolEntry(final String entry, final boolean isPencilmark) {			
		if(cells[cellPickerCol][cellPickerRow].isGiven()) {
			//Can't enter digits into cells containing givens, simply return
			return null;
		}
		final Integer mappedDigit = symbolToDigitMappings.get(entry);
		if(mappedDigit == null) {
			return null;
		}		
		final int newValue = mappedDigit.intValue();
		final int oldValue = cells[cellPickerCol][cellPickerRow].getDigit();
		
		UndoableGameBoardAction undoableAction = null;
		if(isPencilmark) {
			final boolean pencilmarkSet = cells[cellPickerCol][cellPickerRow].isPencilmarkSet(newValue);
			final String presentationName = pencilmarkSet? UndoablePencilmarkEditAction.DELETE_PENCILMARK_PRESENTATION_NAME :
				UndoablePencilmarkEditAction.INSERT_PENCILMARK_PRESENTATION_NAME;
			undoableAction = new UndoablePencilmarkEditAction(presentationName,
					this, cellPickerRow, cellPickerCol, pencilmarkSet, newValue);
			setPencilmarkValues(cellPickerRow, cellPickerCol, !pencilmarkSet, false, newValue);
		}
		else {
			//Check if this value has already been entered; if yes, remove it from cell (simple delete)
			final boolean equalToOldValue = newValue == oldValue;
			if(equalToOldValue) {
				//Delete previous cell value
				undoableAction = new UndoableCellValueEditAction(
						UndoableCellValueEditAction.DELETE_SYMBOL_PRESENTATION_NAME, this, cellPickerRow,
						cellPickerCol, oldValue, 0);
				setCellValue(cellPickerRow, cellPickerCol, 0);
			}
			else {
				//Replace old with a new cell value
				undoableAction = new UndoableCellValueEditAction(
						UndoableCellValueEditAction.INSERT_VALUE_PRESENTATION_NAME, this, cellPickerRow,
						cellPickerCol, cells[cellPickerCol][cellPickerRow].getDigit(), newValue);
				setCellValue(cellPickerRow, cellPickerCol, newValue);
			}
		}
		return undoableAction;
	}
	
	/**
	 * Set the background color of a cell
	 * 
	 * @param row Row index of the cell
	 * @param column Column index of the cell
	 * @param colorIndex The index of the background color to set
	 */
	public void setCellColor(final int row, final int column, final int colorIndex) {
		final int currentColor = cells[column][row].getBackgroundColorIndex();
		if(currentColor == DEFAULT_CELL_COLOR_INDEX && colorIndex != DEFAULT_CELL_COLOR_INDEX) {
			++colorCount;
		}
		else if(currentColor != DEFAULT_CELL_COLOR_INDEX && colorIndex == DEFAULT_CELL_COLOR_INDEX) {
			--colorCount;
		}
		cells[column][row].setBackgroundColorIndex(colorIndex);
		draw(true, true);
	}
	
	private UndoableGameBoardAction handleColorSelection() {		
		UndoableGameBoardAction undoableAction = null;
		final int cellBackgroundIndex = cells[cellPickerCol][cellPickerRow].getBackgroundColorIndex();		
		
		if(cellBackgroundIndex == cellColorIndex) {			
			// Player has unselected the cell, paint it in background/normal color
			undoableAction = new UndoableColorEditAction(this, cellPickerRow, cellPickerCol,
					cellBackgroundIndex, DEFAULT_CELL_COLOR_INDEX);
			setCellColor(cellPickerRow, cellPickerCol, DEFAULT_CELL_COLOR_INDEX);
		} 
		else {			
			// Player has selected the cell, paint it in selected background color
			undoableAction = new UndoableColorEditAction(this, cellPickerRow, cellPickerCol,
				cellBackgroundIndex, cellColorIndex);
			setCellColor(cellPickerRow, cellPickerCol, cellColorIndex);
		}
		return undoableAction;
	}
}