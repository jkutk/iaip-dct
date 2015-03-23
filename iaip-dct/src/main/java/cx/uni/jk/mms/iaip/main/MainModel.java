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

import java.awt.Dimension;

import cx.uni.jk.mms.iaip.brush.BrushModel;
import cx.uni.jk.mms.iaip.mat.CrossMatListener;
import cx.uni.jk.mms.iaip.mat.IllegalSizeException;
import cx.uni.jk.mms.iaip.mat.MatModel;
import cx.uni.jk.mms.iaip.tools.MatTool;
import cx.uni.jk.mms.iaip.tools.SimpleBrushTool;

/**
 * Contains one spatial and one frequency {@link MatModel}
 */
public class MainModel {
	private final MatModel spatialMat;
	private final MatModel frequencyMat;
	private final BrushModel brushModel = new BrushModel();
	private SimpleBrushTool tool = new SimpleBrushTool();

	public MainModel(int width, int height) {
		super();

		/** parts of the model */
		this.spatialMat = new MatModel("Spatial", width, height);
		this.frequencyMat = new MatModel(this.spatialMat);
		this.frequencyMat.setName("Frequency");

		/** set up listeners */
		this.spatialMat.addMatChangeListener(new CrossMatListener(this.frequencyMat, false));
		this.frequencyMat.addMatChangeListener(new CrossMatListener(this.spatialMat, true));
	}

	public void clearMats(Dimension dim) throws IllegalSizeException {
		this.spatialMat.clear((int) dim.getWidth(), (int) dim.getHeight());
		/** frequency mat is cleared by CrossMatListener link */
	}

	public MatModel getSpatialMatModel() {
		return this.spatialMat;
	}

	public MatModel getFrequencyMatModel() {
		return this.frequencyMat;
	}

	public BrushModel getBrushModel() {
		return this.brushModel;
	}

	public MatTool getTool() {
		return this.tool;
	}
}
