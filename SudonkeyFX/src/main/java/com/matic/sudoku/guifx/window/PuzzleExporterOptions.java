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

import java.util.List;

import com.matic.sudoku.Resources;
import com.matic.sudoku.generator.Generator.Symmetry;
import com.matic.sudoku.io.KeyInputManager.SymbolType;
import com.matic.sudoku.logic.LogicSolver.Grading;

/**
 * Puzzle creation and export options selected by player when creating
 * and exporting multiple puzzles to PDF
 * 
 * @author vedran
 *
 */
public class PuzzleExporterOptions {
	
	public enum Ordering {
		RANDOM(Resources.getTranslation("generate.random")), 
		DIFFICULTY(Resources.getTranslation("generate.difficulty"));
		
		private final String description;

		Ordering(final String type) {
			this.description = type;
		}

		public String getDescription() {
			return description;
		}

		public static Ordering fromString(final String ordering) {
			if(ordering != null) {
				for(final Ordering type : Ordering.values()) {
					if(type.description.equals(ordering)) {
						return type;
					}
				}
			}
			throw new IllegalArgumentException(
					"No ordering type with description " + ordering + " found");
		}
	}
	
	private final List<SymbolType> symbolTypes;
	private final List<Symmetry> symmetries;
	private final List<Grading> gradings;
	
	private final Ordering puzzleOrder;

	private final boolean printPuzzleNumberings;
	private final boolean printPuzzleGradings;
	private final boolean fillPencilmarks;
	private final boolean appendSolutions;
	private final boolean isEmptyGrid;	
	
	private final String outputPath;
	
	private final int puzzlesPerPage;
	private final int puzzleCount;
	
	public PuzzleExporterOptions(final List<SymbolType> symbolTypes,
			final List<Symmetry> symmetries, final List<Grading> gradings,
			final boolean printPuzzleNumberings, final boolean printPuzzleGradings,
			final boolean fillPencilmarks, final boolean appendSolutions,
			final boolean isEmptyGrid, final Ordering puzzleOrder, final String outputPath,
			final int puzzlesPerPage, final int puzzleCount) {
		this.symbolTypes = symbolTypes;
		this.symmetries = symmetries;
		this.gradings = gradings;
		this.printPuzzleNumberings = printPuzzleNumberings;
		this.printPuzzleGradings = printPuzzleGradings;
		this.fillPencilmarks = fillPencilmarks;
		this.appendSolutions = appendSolutions;
		this.isEmptyGrid = isEmptyGrid;
		this.puzzleOrder = puzzleOrder;
		this.outputPath = outputPath;
		this.puzzlesPerPage = puzzlesPerPage;
		this.puzzleCount = puzzleCount;
	}
	
	public List<SymbolType> getSymbolTypes() {
		return symbolTypes;
	}
	
	public List<Symmetry> getSymmetries() {
		return symmetries;
	}
	
	public List<Grading> getGradings() {
		return gradings;
	}
	
	public boolean isPrintPuzzleNumberings() {
		return printPuzzleNumberings;
	}
	
	public boolean isPrintPuzzleGradings() {
		return printPuzzleGradings;
	}
	
	public boolean isFillPencilmarks() {
		return fillPencilmarks;
	}
	
	public boolean isAppendSolutions() {
		return appendSolutions;
	}
	
	public boolean isEmptyGrid() {
		return isEmptyGrid;
	}
	
	public Ordering getPuzzleOrder() {
		return puzzleOrder;
	}
	
	public String getOutputPath() {
		return outputPath;
	}
	
	public int getPuzzlesPerPage() {
		return puzzlesPerPage;
	}
	
	public int getPuzzleCount() {
		return puzzleCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (appendSolutions ? 1231 : 1237);
		result = prime * result + (fillPencilmarks ? 1231 : 1237);
		result = prime * result
				+ ((gradings == null) ? 0 : gradings.hashCode());
		result = prime * result + (isEmptyGrid ? 1231 : 1237);
		result = prime * result
				+ ((outputPath == null) ? 0 : outputPath.hashCode());
		result = prime * result + (printPuzzleGradings ? 1231 : 1237);
		result = prime * result + (printPuzzleNumberings ? 1231 : 1237);
		result = prime * result + puzzleCount;
		result = prime * result
				+ ((puzzleOrder == null) ? 0 : puzzleOrder.hashCode());
		result = prime * result + puzzlesPerPage;
		result = prime * result
				+ ((symbolTypes == null) ? 0 : symbolTypes.hashCode());
		result = prime * result
				+ ((symmetries == null) ? 0 : symmetries.hashCode());
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
		PuzzleExporterOptions other = (PuzzleExporterOptions) obj;
		if (appendSolutions != other.appendSolutions)
			return false;
		if (fillPencilmarks != other.fillPencilmarks)
			return false;
		if (gradings == null) {
			if (other.gradings != null)
				return false;
		} else if (!gradings.equals(other.gradings))
			return false;
		if (isEmptyGrid != other.isEmptyGrid)
			return false;
		if (outputPath == null) {
			if (other.outputPath != null)
				return false;
		} else if (!outputPath.equals(other.outputPath))
			return false;
		if (printPuzzleGradings != other.printPuzzleGradings)
			return false;
		if (printPuzzleNumberings != other.printPuzzleNumberings)
			return false;
		if (puzzleCount != other.puzzleCount)
			return false;
		if (puzzleOrder == null) {
			if (other.puzzleOrder != null)
				return false;
		} else if (!puzzleOrder.equals(other.puzzleOrder))
			return false;
		if (puzzlesPerPage != other.puzzlesPerPage)
			return false;
		if (symbolTypes == null) {
			if (other.symbolTypes != null)
				return false;
		} else if (!symbolTypes.equals(other.symbolTypes))
			return false;
		if (symmetries == null) {
			if (other.symmetries != null)
				return false;
		} else if (!symmetries.equals(other.symmetries))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PuzzleExporterOptions [symbolTypes=" + symbolTypes
				+ ", symmetries=" + symmetries + ", gradings=" + gradings
				+ ", printPuzzleNumberings=" + printPuzzleNumberings
				+ ", printPuzzleGradings=" + printPuzzleGradings
				+ ", fillPencilmarks=" + fillPencilmarks + ", appendSolutions="
				+ appendSolutions + ", isEmptyGrid=" + isEmptyGrid
				+ ", puzzleOrder=" + puzzleOrder + ", outputPath=" + outputPath
				+ ", puzzlesPerPage=" + puzzlesPerPage + ", puzzleCount="
				+ puzzleCount + "]";
	}	
}
