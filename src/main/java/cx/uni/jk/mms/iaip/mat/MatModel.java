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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;

/**
 * Application model for one OpenCV {@link Mat} providing basic methods
 */
public class MatModel {
	public static final int MAT_TYPE = CvType.CV_32FC1;

	private final Logger logger = Logger.getGlobal();

	private String name;
	private Mat mat;
	private List<MatChangeListener> matChangeListeners = new CopyOnWriteArrayList<>();

	private Path lastPath;

	public MatModel(String name, int width, int height) {
		super();
		this.name = name;
		try {
			this.clear(width, height);
		} catch (IllegalSizeException e) {
			this.logger.severe(e.getStackTrace().toString());
			System.exit(e.hashCode());
		}
	}

	public MatModel(MatModel mat) {
		this(mat.getName(), mat.getWidth(), mat.getHeight());
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * loads and decodes an image with Java ImageIO.
	 * 
	 * drawback: only gray scale images allowed, fewer file types
	 * supported. @see <a href=
	 * "http://docs.oracle.com/javase/tutorial/2d/images/loadimage.html"> java
	 * tutorials</a> for details.
	 * 
	 * @throws UnsupportedImageTypeException
	 *             if the image is not an 8 bit gray scale image.
	 */
	protected Mat loadAndDecodeImageWithJavaImageIO(Path path) throws IOException {
		this.logger.finer("Loading and decoding image with Java ImageIO.");
		BufferedImage img = ImageIO.read(path.toUri().toURL());

		int cvType;
		switch (img.getType()) {
		case BufferedImage.TYPE_BYTE_GRAY:
			cvType = CvType.CV_8U;
			break;
		default:
			throw new UnsupportedImageTypeException("The image is not an 8 bit gray scale image.");
		}

		Mat mat = new Mat(img.getHeight(), img.getWidth(), cvType);
		mat.put(0, 0, ((DataBufferByte) img.getRaster().getDataBuffer()).getData());
		return mat;
	}

	/**
	 * loads an image with Java ImageIO and decodes it with OpenCV built-in
	 * functions.
	 * 
	 * gotcha: OpenCV may run into a library version mismatch. @see <a href=
	 * "http://www.answers.opencv.org/question/34412/solved-opencv-libpng-version-mismatch/"
	 * >posting on OpenCV forums</a>.
	 */
	@Deprecated
	protected Mat loadImageWithJavaImageIOAndDecodeWithOpenCV(Path path) throws IOException {
		this.logger.finer("Loading image with Java ImageIO, decoding with OpenCV library.");
		/**
		 * Note: can not use Highgui.imread() with a Path. Must read whole file
		 * into memory before copying into Mat.
		 */
		BufferedImage img = ImageIO.read(path.toUri().toURL());
		Mat bufMat = new MatOfByte(((DataBufferByte) img.getRaster().getDataBuffer()).getData());
		return Highgui.imdecode(bufMat, Highgui.IMREAD_GRAYSCALE);
	}

	/**
	 * loads and decodes an image with OpenCV built-in functions.
	 * 
	 * gotcha: OpenCV may run into a library version mismatch. @see <a href=
	 * "http://www.answers.opencv.org/question/34412/solved-opencv-libpng-version-mismatch/"
	 * >posting on OpenCV forums</a>.
	 */
	@Deprecated
	protected Mat loadAndDecodeImageWithOpenCV(Path path) throws IOException {
		this.logger.finer("Loading and decoding image with OpenCV library.");
		/**
		 * Note: can not use Highgui.imread() with a Path. Must read whole file
		 * into memory before decoding with Highgui.imdecode().
		 */
		Mat bufMat = new MatOfByte(Files.readAllBytes(path));
		return Highgui.imdecode(bufMat, Highgui.IMREAD_GRAYSCALE);
	}

	/**
	 * Loads an image from a file into this model.
	 * 
	 * The image file type must be supported by ImageIO and must be 8 bit gray
	 * scale due to limitations of the used methods. The image must be of even
	 * width and even height in order to be processed by OpenCV's DCT/IDCT
	 * methods.
	 * 
	 * This implementation uses {@link Path} instead of {@link File} in order to
	 * read the jar from the inside.
	 * 
	 * @param path
	 * @throws IllegalSizeException
	 * @throws IOException
	 * @throws UnsupportedImageTypeException
	 */
	public void loadImage(Path path) throws IllegalSizeException, IOException, UnsupportedImageTypeException {
		this.logger.fine(String.format("MatModel \"%s\" loading iamge from path %s", this.getName(), path.toString()));

		Mat matRead = null;

		matRead = this.loadAndDecodeImageWithJavaImageIO(path);
		// matRead = loadImageWithJavaImageIOAndDecodeWithOpenCV(path);
		// matRead = loadImageWithOpenCV(path);

		this.logger.finer("image type = " + matRead.type());
		this.logger.finer("image channels = " + matRead.channels());
		this.logger.finer("image depth = " + matRead.depth());

		/** images must have size larger than 0x0 */
		if (matRead.width() <= 0 || matRead.height() <= 0) {
			throw new IllegalSizeException("Image must have width and height > 0.");
		}

		/** dct images must have odd width or height */
		if (matRead.width() % 2 == 1 || matRead.height() % 2 == 1) {
			throw new IllegalSizeException("Image must have even width and even height to perform DCT/IDCT.");
		}

		/** we need a float mat to do DCT/IDCT */
		this.mat = matRead; // just a reference
		this.logger.finer("convert to internal format");
		this.mat.convertTo(this.mat, MAT_TYPE);
		this.logger.finer("image type = " + this.mat.type());
		this.logger.finer("image channels = " + this.mat.channels());
		this.logger.finer("image depth = " + this.mat.depth());

		/** remember last file loaded successfully */
		this.lastPath = path;
	}

	public Path getLastPath() {
		return this.lastPath;
	}

	public void saveMat(File file) throws CvException {
		Mat cloneMat = new MatOfByte();
		MinMaxLocResult mmlr = Core.minMaxLoc(this.mat);
		double min = Math.min(mmlr.minVal, 0);
		double max = Math.max(mmlr.maxVal, 255);
		double alpha = 256.0d / (max - min);
		double beta = -min * alpha;
		this.mat.convertTo(cloneMat, CvType.CV_8U, alpha, beta);

		Highgui.imwrite(file.getPath(), cloneMat);
	}

	public void saveMatAsCsv(File file) {
		int width = this.mat.width();
		int height = this.mat.height();
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));) {
			for (int row = 0; row < height; row++) {
				for (int col = 0; col < width; col++) {
					bw.write(Double.toString(this.mat.get(row, col)[0]));
					bw.write(';');
				}
				bw.newLine();
			}
			bw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void clear(int width, int height) throws IllegalSizeException {
		if (width % 2 == 1 || height % 2 == 1) {
			throw new IllegalSizeException("Images must have even width and even height in order to perform DCT/IDCT.");
		}

		this.mat = Mat.zeros(new Size(width, height), MAT_TYPE);
	}

	public boolean addMatChangeListener(MatChangeListener l) {
		return this.matChangeListeners.add(l);
	}

	public boolean removeMatChangeListener(MatChangeListener l) {
		return this.matChangeListeners.remove(l);
	}

	public void fireMatChangedEvent() {
		MatChangeEvent e = new MatChangeEvent(this);
		for (MatChangeListener l : this.matChangeListeners) {
			l.matModified(e);
		}
	}

	public void fireMatChangedEvent(Rect changedArea) {
		MatChangeEvent e = new MatChangeEvent(this, changedArea);
		for (MatChangeListener l : this.matChangeListeners) {
			l.matModified(e);
		}
	}

	public void fireMatLoadedEvent() {
		MatChangeEvent e = new MatChangeEvent(this);
		for (MatChangeListener l : this.matChangeListeners) {
			l.matLoaded(e);
		}
	}

	protected void setMat(Mat mat) {
		this.mat = mat;
	}

	public Mat getMat() {
		return this.mat;
	}

	public int getWidth() {
		return this.mat.width();
	}

	public int getHeight() {
		return this.mat.height();
	}

	public float getValue(int x, int y) {
		float[] values = new float[1];
		this.mat.get(y, x, values);
		return values[0];
	}
}
