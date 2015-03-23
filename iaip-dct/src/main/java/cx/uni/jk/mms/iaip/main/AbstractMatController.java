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

package cx.uni.jk.mms.iaip.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

import org.opencv.core.Mat;

import cx.uni.jk.mms.iaip.mat.IllegalSizeException;
import cx.uni.jk.mms.iaip.mat.MatModel;
import cx.uni.jk.mms.iaip.mat.UnsupportedImageTypeException;

/**
 * Common methods for all controllers handling a {@link Mat} model and multiple {@link AbstractMatView}s  
 */
public abstract class AbstractMatController {

	protected final MainController mainController;
	protected final MatModel model;
	private final List<AbstractMatView> views = new LinkedList<>();
	private int viewNumber = 1;

	public AbstractMatController(MainController mainController, MatModel model) {
		super();
		this.mainController = mainController;
		this.model = model;
	}

	public int getAndIncViewNumber() {
		return this.viewNumber++;
	}

	public void addView(AbstractMatView view) {
		this.views.add(view);
	}

	public abstract AbstractMatView addNewView();

	public boolean removeView(AbstractMatView view) {
		final boolean result = this.views.remove(view);
		this.mainController.viewRemoved(view);
		return result;
	}

	public List<AbstractMatView> getViews() {
		return this.views;
	}

	public AbstractMatView addNewSpatialImageView() {
		return this.mainController.addNewSpatialImageView();
	}

	public AbstractMatView addNewSpatialTableView() {
		return this.mainController.addNewSpatialTableView();
	}

	public AbstractMatView addNewFrequencyImageView() {
		return this.mainController.addNewFrequencyImageView();
	}

	public AbstractMatView addNewFrequencyTableView() {
		return this.mainController.addNewFrequencyTableView();
	}

	/**
	 * loads an image file into the model
	 * 
	 * @param path
	 * @throws IllegalSizeException
	 * @throws IOException
	 */
	public void loadImage(Path path) throws IllegalSizeException, IOException {
		this.model.loadImage(path);
		this.model.fireMatLoadedEvent();
	}

	public void reloadImage() throws IllegalSizeException, IOException, UnsupportedImageTypeException {
		Path lastPath = this.model.getLastPath();
		if (lastPath != null) {
			this.model.loadImage(lastPath);
			this.model.fireMatLoadedEvent();
		}
	}

	/**
	 * saves the model's mat to an image file
	 * 
	 * @param file
	 */
	public void saveMat(File file) {
		this.model.saveMat(file);
	}

	/**
	 * save the model's mat as csv file
	 * 
	 * @param file
	 */
	public void saveMatrixAsCsv(File file) {
		this.model.saveMatAsCsv(file);
	}

	/**
	 * clears the mat and resizes to given width an height
	 * 
	 * @param width
	 * @param height
	 * @throws IllegalSizeException
	 */
	public void clear(int width, int height) throws IllegalSizeException {
		this.model.clear(width, height);
		this.model.fireMatLoadedEvent();
	}

	/**
	 * ends the application
	 */
	public void exit() {
		this.mainController.exit();
	}

}