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

import javax.swing.table.TableModel;

import org.opencv.core.Rect;

import cx.uni.jk.mms.iaip.mat.MatChangeEvent;
import cx.uni.jk.mms.iaip.mat.MatChangeListener;
import cx.uni.jk.mms.iaip.mat.MatModel;
import cx.uni.jk.mms.iaip.rectangularJTable.AbstractRectangularTableModel;

/**
 * The {@link TableModel} for {@link MatTable}
 */
public class MatTableModel extends AbstractRectangularTableModel {
	private static final long serialVersionUID = -7651696787730993187L;
	private static final int HEADER_COLUMNS = 1;

	private MatModel mat;

	public MatTableModel(MatModel mat) {
		super();
		this.mat = mat;
		this.mat.addMatChangeListener(this.matChangeListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return this.mat.getHeight();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return this.mat.getWidth() + 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex < HEADER_COLUMNS) {
			return new Integer(rowIndex);
		} else {
			return new Double(this.mat.getValue(columnIndex - HEADER_COLUMNS, rowIndex));
		}
	}

	private MatChangeListener matChangeListener = new MatChangeListener() {

		@Override
		public void matLoaded(MatChangeEvent e) {
			MatTableModel.this.fireTableStructureChanged();
		}

		@Override
		/**
		 * converts changes of the MatModel into TableModel changes.
		 * 
		 * Unfortunately, one can neither fire nor create a TableModelEvent spanning a
		 * rectangular area of cells, because TableModelEvent does not support that.
		 * One would have to extend the TableModelChange, the Listeners interface, and
		 * add an implementation in the custom class derived from JTable.
		 * So we do the trick here by looping over the columns.
		 */
		public void matModified(MatChangeEvent e) {
			Rect changedArea = e.getChangedArea();
			if (changedArea == null) {
				MatTableModel.this.fireTableDataChanged();
			} else {
				MatTableModel.this.fireTableRectangleUpdated(changedArea.y, changedArea.x + HEADER_COLUMNS, changedArea.y
						+ changedArea.height - 1, changedArea.x + changedArea.width - 1 + HEADER_COLUMNS);
			}
		}
	};

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return Double.class;
	}

	@Override
	public String getColumnName(int column) {
		if (column == 0) {
			return null;
		} else {
			return Integer.toString(column - 1);
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex < HEADER_COLUMNS) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO: this should be implemented in some controller?
		this.mat.getMat().put(rowIndex, columnIndex - HEADER_COLUMNS, (Double) aValue);
		this.mat.fireMatChangedEvent(new Rect(columnIndex, rowIndex, 1, 1));
	}

}
