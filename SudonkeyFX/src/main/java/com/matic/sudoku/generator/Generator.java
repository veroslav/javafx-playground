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

package com.matic.sudoku.generator;

import com.matic.sudoku.Resources;;

public interface Generator {

	public enum Symmetry {
		NONE(Resources.getTranslation("symmetry.none")), 
		ROTATIONAL_180(Resources.getTranslation("symmetry.rotational")), 
		VERTICAL_MIRRORING(Resources.getTranslation("symmetry.vertical_mirroring")), 
		HORIZONTAL_MIRRORING(Resources.getTranslation("symmetry.horizontal_mirroring")), 
		DIAGONAL(Resources.getTranslation("symmetry.diagonal")), 
		ANTI_DIAGONAL(Resources.getTranslation("symmetry.anti_diagonal"));
		
		private final String description;

		Symmetry(final String type) {
			this.description = type;
		}

		public String getDescription() {
			return description;
		}

		public static Symmetry fromString(final String symmetry) {
			if(symmetry != null) {
				for(final Symmetry type : Symmetry.values()) {
					if(type.description.equals(symmetry)) {
						return type;
					}
				}
			}
			throw new IllegalArgumentException(
					"No symmetry type with description " + symmetry + " found");
		}

		public static Symmetry getRandom() {
			return values()[(int)(Math.random() * values().length)];
		}
	}
}
