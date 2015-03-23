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

import java.util.logging.Logger;

import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import cx.uni.jk.mms.iaip.brush.BrushModel;
import cx.uni.jk.mms.iaip.mat.MatModel;

/**
 * A {@link MatTool} applying a {@link BrushModel} on a {@link MatModel}. 
 */
public class SimpleBrushTool implements MatTool {

	private final Logger logger = Logger.getGlobal();

	/*
	 * (non-Javadoc)
	 * 
	 * @see tools.Tool#getName()
	 */
	@Override
	public String getName() {
		return "Simple Brush Tool";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see tools.Tool#apply(model.MatModel, int, int)
	 */
	@Override
	public Rect apply(Mat mat, BrushModel brush, int x, int y, boolean inverseEffect) {

		Rect changedArea = null;

		try {
			this.logger.finer(String.format("apply mode=\"%s\" inverse=%s, size=%d, strength=%d", brush.getMode(),
					inverseEffect, brush.getSize(), brush.getValue()));

			this.logger.finest("mat    = " + mat.toString());

			/** where is brush going to work? this may reach outside the mat! */
			int brushColStart = x - (brush.getSize() - 1) / 2;
			int brushColEnd = x + brush.getSize() / 2;
			int brushRowStart = y - (brush.getSize() - 1) / 2;
			int brushRowEnd = y + brush.getSize() / 2;

			if (brushColEnd >= 0 && brushColStart < mat.cols() && brushRowEnd >= 0 && brushRowStart < mat.rows()) {

				/** calculate bounds for roiMat to fit into original mat */
				int subColStart = Math.max(0, brushColStart);
				int subColEnd = Math.min(brushColEnd, mat.cols() - 1);
				int subRowStart = Math.max(0, brushRowStart);
				int subRowEnd = Math.min(brushRowEnd, mat.rows() - 1);

				/**
				 * the caller may want to know. Rect constructor interprets the
				 * second point being outside of the Rect! a one pixel rectangle
				 * Rect(Point(a,b), Point(a+1,b+1)) has height and width 1. see
				 * 
				 * @link{http://docs.opencv.org/java/org/opencv/core/Rect.html
				 */
				changedArea = new Rect(new Point(subColStart, subRowStart), new Point(subColEnd + 1, subRowEnd + 1));

				/**
				 * get the part of original mat which going to be affected by
				 * change
				 */
				Mat roiMat = mat.submat(subRowStart, subRowEnd + 1, subColStart, subColEnd + 1);
				this.logger.finest("matRoi = " + roiMat.toString());

				/** does the brush fit into the roiMat we shall work on ? */
				boolean brushFits = brushColStart == subColStart && brushColEnd == subColEnd && brushRowStart == subRowStart
						&& brushRowEnd == subRowEnd;

				this.logger.finest("brush fits = " + brushFits);

				/**
				 * make sure to have a working mat which matches the full brush
				 * size
				 */
				Mat workMat, workRoi = null;
				if (brushFits) {
					/** just work in the original mat area defined by roi */
					workMat = roiMat;
				} else {
					/** create a new mat as big as the brush */
					workMat = Mat.zeros(brush.getSize(), brush.getSize(), MatModel.MAT_TYPE);
					this.logger.finest("workMat= " + workMat.toString());
					/**
					 * create an ROI in the workMat as big as the subMat,
					 * correct offset for brushing in the middle
					 */
					int roiColStart = subColStart - brushColStart;
					int roiColEnd = roiColStart + roiMat.cols();
					int roiRowStart = subRowStart - brushRowStart;
					int roiRowEend = roiRowStart + roiMat.rows();

					workRoi = workMat.submat(roiRowStart, roiRowEend, roiColStart, roiColEnd);
					this.logger.finest("workRoi= " + workRoi.toString());
					roiMat.copyTo(workRoi);
					this.logger.finest("workRoi= " + workRoi.toString());

					// workRoi.put(0, 0, 1333.0d);
					this.logger.finest("roiMat  dump1 " + roiMat.dump());
					this.logger.finest("workRoi dump1 " + workRoi.dump());
					this.logger.finest("workMat dump1 " + workMat.dump());
				}

				/** the real action */
				this.applyToWorkMat(brush, inverseEffect, workMat);

				this.logger.finest("workMat dump2 " + workMat.dump());
				this.logger.finest("matRoi  dump2 " + roiMat.dump());

				if (brushFits) {
					/**
					 * nothing to do, we have been working directly in original
					 * mat
					 */
				} else {
					/** copy workMat back into original mat */
					this.logger.finest("workRoi dump2 " + workRoi.dump());
					// workRoi.put(0, 0, 1338);
					this.logger.finest("workRoi dump3 " + workRoi.dump());
					/**
					 * copy roi of changed workmat back into roi of original mat
					 */
					this.logger.finest("matRoi = " + roiMat.toString());
					workRoi.copyTo(roiMat);
					this.logger.finest("matRoi = " + roiMat.toString());
				}
				this.logger.finest("matRoi  dump3 " + roiMat.dump());
			}

		} catch (CvException e) {
			/** nevermind if the user does not notice */
			this.logger.fine(e.getStackTrace().toString());
		}

		/** let the caller know caller which area has potentially been changed */
		return changedArea;
	}

	/**
	 * @param brush
	 * @param inverseEffect
	 * @param workMat
	 */
	private void applyToWorkMat(BrushModel brush, boolean inverseEffect, Mat workMat) {
		switch (brush.getMode()) {
		case SET: {
			if (!inverseEffect) {
				/** set */
				Core.add(workMat.mul(brush.getOneMinusAlphaMat()), brush.getMultipliedMat(), workMat);
			} else {
				/** clear */
				Core.multiply(workMat, brush.getOneMinusAlphaMat(), workMat);
			}
		}
			break;
		case ADD: {
			if (!inverseEffect) {
				/** add */
				Core.add(workMat, brush.getMultipliedMat(), workMat);
			} else {
				/** subtract */
				Core.subtract(workMat, brush.getMultipliedMat(), workMat);
			}
		}
			break;
		case MULTIPLY: {
			if (!inverseEffect) {
				/** multiply */
				Core.add(workMat.mul(brush.getOneMinusAlphaMat()), workMat.mul(brush.getMultipliedMat(), 0.01d), workMat);
			} else {
				/** multiply by 1/alpha. trick: divide twice ;) */
				Core.divide(1.0d, workMat, workMat);
				Core.add(workMat.mul(brush.getOneMinusAlphaMat()), workMat.mul(brush.getMultipliedMat(), 0.01d), workMat);
				Core.divide(1.0d, workMat, workMat);
			}
		}
			break;
		default:
			/** do nothing */
		}
	}
}
