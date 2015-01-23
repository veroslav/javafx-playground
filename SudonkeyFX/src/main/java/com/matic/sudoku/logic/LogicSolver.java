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

package com.matic.sudoku.logic;

import com.matic.sudoku.Resources;;

public class LogicSolver {

	// Puzzle grading constants
	public enum Grading {
		EASY(Resources.getTranslation("puzzle.easy")), 
		MODERATE(Resources.getTranslation("puzzle.moderate")), 
		HARD(Resources.getTranslation("puzzle.hard")), 
		EXPERT(Resources.getTranslation("puzzle.expert")), 
		DIABOLIC(Resources.getTranslation("puzzle.diabolic"));
		
		private final String description;

		Grading(final String description) {
			this.description = description;
		}

		public String getDescription() {
			return description;
		}

		public static Grading fromString(final String grading) {
			if (grading != null) {
				for (final Grading g : Grading.values()) {
					if (g.description.equals(grading)) {
						return g;
					}
				}
			}
			throw new IllegalArgumentException("No grading with description "
					+ grading + " found");
		}

		public static Grading getRandom() {
			return values()[(int)(Math.random() * values().length)];
		}
	}
}
