/**
 *    Copyright (C) 2015  Peter Plaimer <dct-tool@tk.jku.at>
 *
 *    This file is part of the program
 *    Inter-Active Image Processing / Discrete Cosine Transformation (DCT) 
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

package cx.uni.jk.mms.iaip.rectangularJTable;

import javax.swing.event.TableModelEvent;
import javax.swing.table.TableModel;

/**
 * Enhanced {@link TableModelEvent} to communicate data changes in a rectangular
 * region of cells.
 */
public class RectangularTableModelEvent extends TableModelEvent {
	private static final long serialVersionUID = -5351735007438589344L;

	protected int firstColumn, lastColumn;

	public RectangularTableModelEvent(TableModel source, int firstRow, int lastRow, int firstColumn, int lastColumn, int type) {
		super(source, firstRow, lastRow, firstColumn, type);
		this.firstColumn = firstColumn;
		this.lastColumn = lastColumn;
	}

	public int getFirstColumn() {
		return this.firstColumn;
	}

	public int getLastColumn() {
		return this.lastColumn;
	}

}
