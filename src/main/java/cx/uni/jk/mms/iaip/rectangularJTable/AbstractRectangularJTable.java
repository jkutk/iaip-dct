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

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * A {@link JTable} enhanced with methods for easy handling of one rectangular selection 
 */
public abstract class AbstractRectangularJTable extends JTable implements RectangularTableModelListener {
	private static final long serialVersionUID = -4837464536116050193L;

	/**
	 * @param dm
	 */
	public AbstractRectangularJTable(AbstractRectangularTableModel dm) {
		this(dm, null, null);
	}

	/**
	 * @param dm
	 * @param cm
	 */
	public AbstractRectangularJTable(AbstractRectangularTableModel dm, TableColumnModel cm) {
		this(dm, cm, null);
	}

	/**
	 * @param numRows
	 * @param numColumns
	 */
	public AbstractRectangularJTable(int numRows, int numColumns) {
		super(numRows, numColumns);
	}

	/**
	 * @param dm
	 * @param cm
	 * @param sm
	 */
	public AbstractRectangularJTable(AbstractRectangularTableModel dm, TableColumnModel cm, ListSelectionModel sm) {
		super(dm, cm, sm);
		dm.addRectangularTableModelListener(this);
	}

	@Override
	public void tableRectangleChanged(RectangularTableModelEvent e) {
		for (int col = e.firstColumn; col <= e.lastColumn; col++) {
			super.tableChanged(new TableModelEvent((TableModel) e.getSource(), e.getFirstRow(), e.getLastRow(), col, e
					.getType()));
		}
	}

	/**
	 * @param firstRow
	 * @param lastRow
	 * @param firstColumn
	 * @param lastColumn
	 */
	public void setRectangularSelection(int firstRow, int lastRow, int firstColumn, int lastColumn) {
		ListSelectionModel rowSelModel = this.getSelectionModel();
		ListSelectionModel colSelModel = this.getColumnModel().getSelectionModel();

		rowSelModel.setValueIsAdjusting(true);
		colSelModel.setValueIsAdjusting(true);

		rowSelModel.clearSelection();
		rowSelModel.setSelectionInterval(firstRow, lastRow);

		colSelModel.clearSelection();
		colSelModel.setSelectionInterval(firstColumn, lastColumn);

		rowSelModel.setValueIsAdjusting(false);
		colSelModel.setValueIsAdjusting(false);
	}

}