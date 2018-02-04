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

import cx.uni.jk.mms.iaip.main.AbstractMatController;
import cx.uni.jk.mms.iaip.main.AbstractMatView;
import cx.uni.jk.mms.iaip.main.MainController;
import cx.uni.jk.mms.iaip.mat.MatModel;

/**
 * Controller for one {@link MatTableView} and one {@link MatModel}
 */
public class MatTableController extends AbstractMatController {

	public MatTableController(MainController controller, MatModel model) {
		super(controller, model);
	}

	@Override
	public AbstractMatView addNewView() {
		AbstractMatView view = new MatTableView(this, this.model);
		this.addView(view);
		return view;
	}
}
