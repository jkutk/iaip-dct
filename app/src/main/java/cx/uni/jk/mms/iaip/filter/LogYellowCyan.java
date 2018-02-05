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

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Scalar;

/**
 * A {@link MatFilter} which applies automatic logarithmic scaling to the
 * absolute values of the mat, giving distinct colors yellow/cyan according to
 * the sign of the original mat value
 */
public class LogYellowCyan implements MatFilter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see filter.MatFilter#MatToBufferedImage(org.opencv.core.Mat)
	 */
	@Override
	public Mat convert(Mat mat) {

		MinMaxLocResult negativeMmlr, positiveMmlr;
		double min, max, alpha, beta;

		/** negative values to positive and log */
		Mat negativeMat = mat.clone();
		Core.min(negativeMat, new Scalar(0.0d), negativeMat);
		Core.multiply(negativeMat, new Scalar(-1.0d), negativeMat);
		Core.add(negativeMat, new Scalar(1.0d), negativeMat);
		Core.log(negativeMat, negativeMat);

		/** positve values log */
		Mat positiveMat = mat.clone();
		Core.max(positiveMat, new Scalar(0.0d), positiveMat);
		Core.add(positiveMat, new Scalar(1.0d), positiveMat);
		Core.log(positiveMat, positiveMat);

		/** find common contrast and brightness to fit into 8 bit */
		negativeMmlr = Core.minMaxLoc(negativeMat);
		positiveMmlr = Core.minMaxLoc(positiveMat);
		min = 0;
		max = Math.max(negativeMmlr.maxVal, positiveMmlr.maxVal);
		alpha = 256.0d / (max - min);
		beta = -min * alpha;

		/** conversion of both matrices to 8 bit */
		negativeMat.convertTo(negativeMat, CvType.CV_8UC1, alpha, beta);
		positiveMat.convertTo(positiveMat, CvType.CV_8UC1, alpha, beta);

		/** create additional mat for saturated green */
		Mat brightMat = negativeMat.clone();
		Core.max(negativeMat, positiveMat, brightMat);
		// Core.absdiff(brightMat, new Scalar(255.0d), brightMat);
		// Core.multiply(brightMat, new Scalar(1.0d/3.0d), brightMat);

		/** combine all matrices into one 8 bit 3 channel rgb picture */
		Mat tempMat = new Mat(mat.rows(), mat.cols(), CvType.CV_8UC3);
		List<Mat> mixSrcMats = new ArrayList<>();
		mixSrcMats.add(negativeMat); // 1 channel: 0
		mixSrcMats.add(positiveMat); // 1 channel: 1
		mixSrcMats.add(brightMat); // 1 channel: 2
		List<Mat> mixDstMats = new ArrayList<>();
		mixDstMats.add(tempMat); // 3 channels: 0-2
		MatOfInt fromToMat = new MatOfInt(0, 0 /* neg->red */, 2, 1/*
																	 * avg->green
																	 */, 1, 2 /*
																			 * pos-
																			 * >
																			 * blue
																			 */);
		Core.mixChannels(mixSrcMats, mixDstMats, fromToMat);

		return tempMat;
	}

	@Override
	public String toString() {
		return "Log(1+v) to +yellow/-cyan";
	}

}
