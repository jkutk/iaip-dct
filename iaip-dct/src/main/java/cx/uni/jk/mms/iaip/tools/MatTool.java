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

package cx.uni.jk.mms.iaip.tools;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import cx.uni.jk.mms.iaip.brush.BrushModel;
import cx.uni.jk.mms.iaip.mat.MatModel;

/**
 * A painting tool to apply a {@link BrushModel} on a {@link MatModel}
 */
public interface MatTool {

	String getName();

	/**
	 * Applies the brush on the mat at x/y
	 * 
	 * @param mat
	 * @param brush
	 * @param x
	 *            in mat coordinates
	 * @param y
	 *            in mat coordinates
	 * @param inverseEffect
	 *            false to use regular effect, true to use inverse effect. for
	 *            example: regular adds value, inverse subtracts value from mat.
	 * @return the rectangle describing the area which has potentially been
	 *         changed. may return an area spanning the whole mat, although this
	 *         is a performance problem. may return null if the tool applied did
	 *         not change the image, for example: the tool was applied outside
	 *         the image.
	 */
	Rect apply(Mat mat, BrushModel brush, int x, int y, boolean inverseEffect);
}
