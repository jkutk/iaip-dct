/**
 *    Copyright (C) 2015  Peter Plaimer <dct-tool@tk.jku.at>
 *
 *    This file is part of the program
 *    InterActive Image Processing / Discrete Cosine Transformation (DCT) 
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * This package's sole purpose is to enhance JTable and its TableModel with a way to
 * inform listeners about a data change event concerning a rectangular region of cells,
 * instead of just single cells, a range of rows in one column, or all cells.
 * 
 * WARNING: This will most probably produce undesired results if the order of columns is changed for any reason. 
 * 
 * @author peter
 */
package cx.uni.jk.mms.iaip.rectangularJTable;