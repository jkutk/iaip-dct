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

package cx.uni.jk.mms.iaip.main;

import cx.uni.jk.mms.iaip.brush.BrushController;
import cx.uni.jk.mms.iaip.brush.BrushModel;
import cx.uni.jk.mms.iaip.image.ImageController;
import cx.uni.jk.mms.iaip.mat.IllegalSizeException;
import cx.uni.jk.mms.iaip.table.MatTableController;
import cx.uni.jk.mms.iaip.tools.MatTool;

/**
 * The application's main controller. Sets up {@link ImageController}
 * sub-controllers and provides central application functions.
 * 
 */
public class MainController {

	private final MainModel model;
	private final ImageController spatialImageController;
	private final ImageController frequencyImageController;
	private final MatTableController spatialTableController;
	private final MatTableController frequencyTableController;

	public MainController(MainModel model) throws IllegalSizeException {
		super();
		this.model = model;

		this.spatialImageController = new ImageController(this, this.model.getSpatialMatModel());
		this.frequencyImageController = new ImageController(this, this.model.getFrequencyMatModel());

		this.spatialTableController = new MatTableController(this, this.model.getSpatialMatModel());
		this.frequencyTableController = new MatTableController(this, this.model.getFrequencyMatModel());

		/** brush */
		BrushController brushController = new BrushController(this, this.model.getBrushModel());
		brushController.setMode(BrushModel.Mode.SET);

		/** create and spread views */
		brushController.getView().setLocation(10, 32);
		this.addNewFrequencyImageView().setLocation(780, 32);
		this.addNewSpatialImageView().setLocation(240, 32);

		/** create empty image */
		this.spatialImageController.clear(8, 8);
	}

	/**
	 * closes the application
	 * 
	 * as per recommendation from
	 * https://docs.oracle.com/javase/tutorial/uiswing
	 * /components/frame.html#windowevents
	 */
	public void exit() {
		System.exit(0);
	}

	public BrushModel getBrushModel() {
		return this.model.getBrushModel();
	}

	public MatTool getTool() {
		return this.model.getTool();
	}

	public AbstractMatView addNewSpatialImageView() {
		return this.spatialImageController.addNewView();
	}

	public AbstractMatView addNewSpatialTableView() {
		return this.spatialTableController.addNewView();
	}

	public AbstractMatView addNewFrequencyImageView() {
		return this.frequencyImageController.addNewView();
	}

	public AbstractMatView addNewFrequencyTableView() {
		return this.frequencyTableController.addNewView();
	}

	/**
	 * Exits the application when the last of all AbstractMatViews is closed.
	 * 
	 * @param view
	 */
	public void viewRemoved(AbstractMatView view) {
		if (this.spatialImageController.getViews().size() + this.spatialTableController.getViews().size()
				+ this.frequencyImageController.getViews().size() + this.frequencyTableController.getViews().size() == 0) {
			this.exit();
		}
	}
}
