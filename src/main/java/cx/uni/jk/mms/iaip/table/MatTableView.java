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

package cx.uni.jk.mms.iaip.table;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;

import javax.swing.JScrollPane;

import cx.uni.jk.mms.iaip.main.AbstractMatView;
import cx.uni.jk.mms.iaip.mat.MatModel;
import cx.uni.jk.mms.iaip.rectangularJTable.AbstractRectangularJTable;

/**
 * A view showing a {@link MatModel} in a table and providing a menu 
 */
public class MatTableView extends AbstractMatView {
	private static final long serialVersionUID = 2762608585952443553L;

	private AbstractRectangularJTable matTable;

	public MatTableView(MatTableController controller, MatModel matModel) throws HeadlessException {
		super(controller, matModel);

		this.matTable = new MatTable(new MatTableModel(this.model));
		this.initUI();
	}

	@Override
	protected void initUI() {
		super.initUI();

		/** setup */
		this.setTitle(String.format("%s Table: %d", this.model.getName(), this.controller.getAndIncViewNumber()));
		this.setPreferredSize(new Dimension(480, 240));
		this.setLayout(new BorderLayout());

		/** table */
		this.getContentPane().add(new JScrollPane(this.matTable), BorderLayout.CENTER);

		/** done */
		this.pack();
		this.setVisible(true);
	}
}