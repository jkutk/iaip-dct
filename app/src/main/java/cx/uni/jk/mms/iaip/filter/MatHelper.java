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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * This class provides helper methods for converting to and from {@link Mat} 
 */
public class MatHelper {
	public static final Logger logger = Logger.getGlobal();

	/**
	 * converts any mat with 1/3/4 channels to an 8 bit BufferedImage with the
	 * same number of channels. if the input mat is not CvType.CV_8U it is
	 * converted to such with truncation of values to [0..255].
	 * 
	 * @param mat
	 * @return the image
	 */
	public static BufferedImage convertMatTo8BitBufferedImage(Mat mat) {
		Mat byteMat;
		if (mat.depth() != CvType.CV_8U) {
			/** conversion to 8 bit Mat */
			byteMat = new MatOfByte();
			mat.convertTo(byteMat, CvType.CV_8U);
		} else {
			byteMat = mat; // just a reference!
		}

		/** encode to .bmp file in memory */
		MatOfByte fileMat = new MatOfByte();
		Imgcodecs.imencode(".bmp", byteMat, fileMat);

		/** use file as input stream for BufferdImage */
		byte[] byteArray = fileMat.toArray();
		BufferedImage bufferedImage = null;
		try {
			InputStream in = new ByteArrayInputStream(byteArray);
			bufferedImage = ImageIO.read(in);
		} catch (Exception e) {
			logger.severe(e.getStackTrace().toString());
			System.exit(e.hashCode());
		}

		return bufferedImage;
	}
}
