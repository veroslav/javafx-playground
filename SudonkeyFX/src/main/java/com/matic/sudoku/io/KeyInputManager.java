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

package com.matic.sudoku.io;

import javafx.scene.input.KeyCode;

import com.matic.sudoku.Resources;
import com.matic.sudoku.io.KeyInputValidationResult.ValidationResult;

/**
 * A validator for key input. It filters known invalid keys immediately, so that only potentially
 * correct key input is passed to the GameBoard for further processing.
 * 
 * @author vedran
 *
 */
public final class KeyInputManager {
	
	public static final String[] LETTER_KEY_ACTION_NAMES = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
		"J", "K", "L", "M", "N", "O", "P" };
	
	public static final int[] DIGIT_KEY_ACTION_VALUES = {1, 2, 3, 4, 5, 6, 7, 8, 9};
	
	private static final String[] NUMPAD_KEY_NAMES = new String[DIGIT_KEY_ACTION_VALUES.length];
	
	private static final String NUMPAD_KEY_CODE_NAME = "Numpad ";
	
	static {
		for(int i = 0; i < DIGIT_KEY_ACTION_VALUES.length; ++i) {
			NUMPAD_KEY_NAMES[i] = NUMPAD_KEY_CODE_NAME + DIGIT_KEY_ACTION_VALUES[i];
		}
	}
	
	public enum SymbolType {
		DIGITS(Resources.getTranslation("symbols.digits")),
		LETTERS(Resources.getTranslation("symbols.letters"));
		
		private final String description;
		
		SymbolType(final String type) {
			this.description = type;
		}
		
		public String getDescription() {
			return description;
		}
		
		public static SymbolType fromString(final String symbols) {			
			if(symbols != null) {
				for(final SymbolType type : SymbolType.values()) {
					if(type.description.equals(symbols)) {
						return type;
					}
				}
			}
			
			throw new IllegalArgumentException("No symbol type with description "
					+ symbols + " found");
		}
		
		public static SymbolType getRandom() {
			return values()[(int)(Math.random() * values().length)];
		}
	}
	
	public static KeyInputValidationResult validateKeyInput(final KeyCode keyCode, final int boardDimension,
			final SymbolType symbolType) {			
		if(keyCode.isArrowKey()) {
			return new KeyInputValidationResult(
					ValidationResult.DIRECTION_CHANGE, keyCode.getName());
		}
		if(keyCode == KeyCode.DELETE || keyCode == KeyCode.BACK_SPACE) {
			return new KeyInputValidationResult(
					ValidationResult.SYMBOL_DELETION, keyCode.getName());
		}				
		final String keyCodeName = keyCode.getName();
		
		if(symbolType == SymbolType.LETTERS && keyCode.isLetterKey()) {			
			return new KeyInputValidationResult(ValidationResult.SYMBOL_ENTRY, keyCodeName);
		}		
		else if(symbolType == SymbolType.DIGITS) {			
			if(keyCode.isDigitKey()) {
				String digitValue = keyCodeName;
				if(keyCode.isKeypadKey()) {
					digitValue = keyCodeName.substring(NUMPAD_KEY_CODE_NAME.length()); 					
				}
				if(!"0".equals(digitValue)) {
					return new KeyInputValidationResult(ValidationResult.SYMBOL_ENTRY, digitValue);
				}
			}
		}
		
		return new KeyInputValidationResult(ValidationResult.INVALID_INPUT, null);
	}
}

