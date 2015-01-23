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

public interface GameBoard {	
	
	// Dimension of a 4x4 puzzle
	public static final int DIMENSION_4x4 = 2;
	
	// Dimension of a classic 9x9 puzzle
	public static final int DIMENSION_9x9 = 3;
	
	// Dimension of a 16x16 puzzle
	public static final int DIMENSION_16x16 = 4;

	int getDimension();	
}