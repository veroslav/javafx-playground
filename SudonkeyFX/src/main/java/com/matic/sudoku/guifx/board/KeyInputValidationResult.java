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

public class KeyInputValidationResult {

	public enum ValidationResult {
		DIRECTION_CHANGE, SYMBOL_ENTRY, SYMBOL_DELETION, INVALID_INPUT
	}
	
	private final ValidationResult validationResult;
	private final String value;
	
	public KeyInputValidationResult(final ValidationResult validationResult, 
			final String value) {
		this.validationResult = validationResult;
		this.value = value;
	}

	public ValidationResult getValidationResult() {
		return validationResult;
	}

	public String getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((validationResult == null) ? 0 : validationResult.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyInputValidationResult other = (KeyInputValidationResult) obj;
		if (validationResult != other.validationResult)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "KeyInputValidator [validationResult=" + validationResult
				+ ", value=" + value + "]";
	}
}
