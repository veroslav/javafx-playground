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

import com.matic.sudoku.generator.Generator.Symmetry;
import com.matic.sudoku.guifx.board.GameBoard.SymbolType;
import com.matic.sudoku.logic.LogicSolver.Grading;

/**
 * Puzzle creation options selected by player when creating a new puzzle
 * 
 * @author vedran
 *
 */
public class PuzzleCreatorOptions {
	
	private final SymbolType symbolType;
	private final Symmetry symmetry;
	private final Grading grading;
		
	private final boolean isEmptyGrid;
	private final int gridDimension;

	public PuzzleCreatorOptions(final boolean isEmptyGrid, final Grading grading,
			final SymbolType symbolType, final Symmetry symmetry,
			final int gridDimension) {
		this.gridDimension = gridDimension;
		this.isEmptyGrid = isEmptyGrid;
		this.symbolType = symbolType;
		this.symmetry = symmetry;
		this.grading = grading;
	}

	public SymbolType getSymbolType() {
		return symbolType;
	}

	public Symmetry getSymmetry() {
		return symmetry;
	}

	public Grading getGrading() {
		return grading;
	}

	public boolean isEmptyGrid() {
		return isEmptyGrid;
	}

	public int getGridDimension() {
		return gridDimension;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((grading == null) ? 0 : grading.hashCode());
		result = prime * result + gridDimension;
		result = prime * result + (isEmptyGrid ? 1231 : 1237);
		result = prime * result
				+ ((symbolType == null) ? 0 : symbolType.hashCode());
		result = prime * result
				+ ((symmetry == null) ? 0 : symmetry.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PuzzleCreatorOptions other = (PuzzleCreatorOptions) obj;
		if (grading != other.grading)
			return false;
		if (gridDimension != other.gridDimension)
			return false;
		if (isEmptyGrid != other.isEmptyGrid)
			return false;
		if (symbolType != other.symbolType)
			return false;
		if (symmetry != other.symmetry)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PuzzleCreatorResult [symbolType=" + symbolType + ", symmetry="
				+ symmetry + ", grading=" + grading + ", isEmptyGrid="
				+ isEmptyGrid + ", gridDimension=" + gridDimension + "]";
	}	
}