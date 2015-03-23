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

package cx.uni.jk.mms.iaip.mat;

import java.util.EventObject;
import java.util.logging.Logger;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

/**
 * Carries information which {@link Mat} changed in which way 
 */
public class MatChangeEvent extends EventObject {
	private final Logger logger = Logger.getGlobal();

	private static final long serialVersionUID = -4255142926816978113L;
	private Rect changedArea = null;

	public MatChangeEvent(Object source) {
		super(source);

		this.logger.finer("no area given");
	}

	public MatChangeEvent(Object source, Rect changedArea) {
		super(source);
		this.changedArea = changedArea;

		this.logger.finer(String.format("area: XY %d/%d WH %d/%d ", changedArea.x, changedArea.y, changedArea.width,
				changedArea.height));

	}

	public Rect getChangedArea() {
		return this.changedArea;
	}
}
