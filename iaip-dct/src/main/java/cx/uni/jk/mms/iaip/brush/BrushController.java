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

package cx.uni.jk.mms.iaip.brush;

import cx.uni.jk.mms.iaip.main.MainController;

/**
 * Controller for {@link BrushView} and {@link BrushModel}
 */
public class BrushController {

	private final BrushModel model;
	private final BrushView view;

	public BrushController(MainController controller, BrushModel model) {
		super();
		this.model = model;
		this.view = new BrushView(this, model);
	}

	public BrushView getView() {
		return this.view;
	}

	public void setSize(int size) {
		this.model.setSize(size);
		this.model.fireBrushChanged();
	}

	public void setValue(int value) {
		this.model.setValue(value);
		this.model.fireBrushChanged();
	}

	public void setMode(BrushModel.Mode mode) {
		this.model.setMode(mode);
		this.model.fireBrushChanged();
	}

	public void setShape(BrushModel.Shape shape) {
		this.model.setShape(shape);
		this.model.fireBrushChanged();
	}
}
