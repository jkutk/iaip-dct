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

package cx.uni.jk.mms.iaip.table;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import cx.uni.jk.mms.iaip.mat.MatModel;
import cx.uni.jk.mms.iaip.rectangularJTable.AbstractRectangularJTable;
import cx.uni.jk.mms.iaip.rectangularJTable.AbstractRectangularTableModel;
import cx.uni.jk.mms.iaip.rectangularJTable.RectangularTableModelEvent;

/**
 * An {@link AbstractRectangularJTable} showing a {@link MatModel}
 */
public class MatTable extends AbstractRectangularJTable {
	private static final long serialVersionUID = 3946681078594425728L;

	// private final Logger logger = Logger.getGlobal();

	public MatTable(AbstractRectangularTableModel dm) {
		super(dm);

		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.setAlignmentX(RIGHT_ALIGNMENT);
		this.getTableHeader().setReorderingAllowed(false);
		this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		this.setCellSelectionEnabled(true);
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);
		this.modifyColumns();
	}

	/**
	 * employs a cheap trick to make the first column appear like a
	 * "row header".
	 * 
	 * unfortunately it will still scroll away horizontally.
	 */
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		if (column == 0) {
			return this.getTableHeader().getDefaultRenderer();
		} else {
			return super.getCellRenderer(row, column);
		}
	}

	/**
	 * changes column width to a value appropriate for the application
	 */
	private void modifyColumns() {
		this.setColumnsWidth(48);
	}

	/** sets the preferred width of all columns to the same value */
	protected void setColumnsWidth(int width) {
		for (int i = this.getColumnModel().getColumnCount() - 1; i >= 0; i--) {
			TableColumn c = this.getColumnModel().getColumn(i);
			c.setPreferredWidth(width);
		}
		this.doLayout();
	}

	@Override
	public void tableRectangleChanged(RectangularTableModelEvent e) {
		super.tableRectangleChanged(e);

		int firstRow = e.getFirstRow();
		if (firstRow == TableModelEvent.HEADER_ROW) {
			// all data or table structure changed, or anything else which is
			// not further specified
			this.modifyColumns();
		} else {
			// data in a rectangular region of cells changed, adapt selection to
			// highlight this area
			this.setRectangularSelection(firstRow, e.getLastRow(), e.getFirstColumn(), e.getLastColumn());
		}
	}

}
