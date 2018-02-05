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

package cx.uni.jk.mms.iaip.filter;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Scalar;

/**
 * A {@link MatFilter} which applies automatic logarithmic scaling to the absolute
 * values of the mat
 */
public class LogOfOnePlusAbs implements MatFilter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see filter.MatFilter#MatToBufferedImage(org.opencv.core.Mat)
	 */
	@Override
	public Mat convert(Mat mat) {

		/** make absolute values and log */
		Mat tempMat = mat.clone();
		Core.absdiff(tempMat, new Scalar(0.0d), tempMat);
		Core.add(tempMat, new Scalar(1.0d), tempMat);
		Core.log(tempMat, tempMat);

		/** find contrast and brightness to fit into 8 bit */
		MinMaxLocResult mmlr = Core.minMaxLoc(tempMat);
		double min = Math.min(mmlr.minVal, 0);
		double max = mmlr.maxVal;
		double alpha = 256.0d / (max - min);
		double beta = -min * alpha;

		/** conversion to 8 bit Mat applying contrast alpha and brightness beta */
		Mat byteMat = new MatOfByte();
		tempMat.convertTo(byteMat, CvType.CV_8U, alpha, beta);

		return byteMat;
	}

	@Override
	public String toString() {
		return "Log(1+abs(v)) to grey";
	}

}
