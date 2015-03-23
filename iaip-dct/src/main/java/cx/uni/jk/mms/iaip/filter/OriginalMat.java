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

package cx.uni.jk.mms.iaip.filter;

import org.opencv.core.Mat;

/**
 * A no-op {@link MatFilter} 
 */
public class OriginalMat implements MatFilter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see filter.MatFilter#MatToBufferedImage(org.opencv.core.Mat)
	 */
	@Override
	public Mat convert(Mat mat) {
		return mat;
	}

	/**
	 * @return description for UI
	 */

	@Override
	public String toString() {
		return "Original mat clipped grey";
	}
}
