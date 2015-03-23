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

import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

/**
 * An {@link AbstractTableModel} enhanced with methods for easy handling of one rectangular selection 
 */
public abstract class AbstractRectangularTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 7704186346084715184L;

	private final CopyOnWriteArrayList<RectangularTableModelListener> rectangularTableModelListeners = new CopyOnWriteArrayList<>();

	public AbstractRectangularTableModel() {
		super();
	}

	public void fireTableRectangleUpdated(int firstRow, int firstColumn, int lastRow, int lastColumn) {
		RectangularTableModelEvent e = new RectangularTableModelEvent(this, firstRow, lastRow, firstColumn, lastColumn,
				TableModelEvent.UPDATE);
		for (RectangularTableModelListener l : this.rectangularTableModelListeners) {
			l.tableRectangleChanged(e);
		}
	}

	public boolean addRectangularTableModelListener(RectangularTableModelListener e) {
		return this.rectangularTableModelListeners.add(e);
	}

	public boolean removeRectangularTableModelListener(Object o) {
		return this.rectangularTableModelListeners.remove(o);
	}
}