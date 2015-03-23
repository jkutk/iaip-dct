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

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;

/**
 * A {@link MatFilter} which applies automatic contrast and brightness
 * correction to the mat
 */
public class GreyAutoContrastBrightness implements MatFilter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see filter.MatFilter#MatToBufferedImage(org.opencv.core.Mat)
	 */
	@Override
	public Mat convert(Mat mat) {
		/** find contrast and brightness to fit into 8 bit */
		MinMaxLocResult mmlr = Core.minMaxLoc(mat);
		double min = mmlr.minVal; // Math.min(mmlr.minVal, 0);
		double max = mmlr.maxVal; // Math.max(mmlr.maxVal, 255);
		double alpha = 256.0d / (max - min);
		double beta = -min * alpha;

		/** conversion to 8 bit Mat */
		Mat byteMat = new MatOfByte();
		mat.convertTo(byteMat, CvType.CV_8U, alpha, beta);

		return byteMat;
	}

	@Override
	public String toString() {
		return "Auto contrast/brightness grey";
	}

}
