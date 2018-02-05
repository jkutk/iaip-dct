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

package cx.uni.jk.mms.iaip.brush;

import java.util.EventObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import cx.uni.jk.mms.iaip.mat.MatModel;

/**
 * A set of characteristics to be used as parameters when painting to the mat.
 */
public class BrushModel {
	public enum Shape {

		SQUARE("Square"), CIRCLE("Hard Circle"), SOFT_CIRCLE("Soft Circle");

		private final String label;

		private Shape(String label) {
			this.label = label;
		}

		@Override
		public String toString() {
			return this.label;
		}
	};

	public enum Mode {
		SET("Set / Zero", "Set pixel to value", "Set pixel to 0"), ADD("Add / Subtract", "Add value to pixel", "Subtract value"), MULTIPLY(
				"Multiply / Divide", "Multiply by value %", "Divide by value %");

		private final String label, mb1Label, mb2Label;

		private Mode(String label, String mb1Label, String mb2Label) {
			this.label = label;
			this.mb1Label = mb1Label;
			this.mb2Label = mb2Label;
		}

		@Override
		public String toString() {
			return this.label;
		}

		public String getLabel() {
			return this.label;
		}

		public String getMb1Label() {
			return this.mb1Label;
		}

		public String getMb2Label() {
			return this.mb2Label;
		}

	};

	private List<BrushListener> listeners = new CopyOnWriteArrayList<>();
	private int size;
	private Shape shape;
	private int value;
	private Mode mode;
	private Mat alphaMat, oneMinusAlphaMat, valueMat, multipliedMat;

	public BrushModel() {
		this(1, Shape.SQUARE, 128, Mode.ADD);
	}

	public BrushModel(int size, Shape shape, int value, Mode mode) {
		super();
		this.size = size;
		this.shape = shape;
		this.value = value;
		this.mode = mode;
		this.updateAllMats();
	}

	/**
	 * 
	 */
	private void updateAllMats() {
		this.updateValueMat();
		this.updateAlphaMat();
		this.updateOneMinusAlphaMat();
		this.updateMultipliedMat();
	}

	private void updateValueMat() {
		this.valueMat = Mat.zeros(this.size, this.size, MatModel.MAT_TYPE);
		Core.add(this.valueMat, new Scalar(this.value), this.valueMat);
	}

	private void updateAlphaMat() {
		this.alphaMat = Mat.zeros(this.size, this.size, MatModel.MAT_TYPE);
		switch (this.shape) {
		case SQUARE:
			Core.add(this.alphaMat, new Scalar(1.0f), this.alphaMat);
			break;
		case CIRCLE:
			Core.circle(this.alphaMat, new Point((this.size - 1) * 0.5d, (this.size - 1) * 0.5d), (this.size - 1) / 2,
					new Scalar(1.0f), -1);
			break;
		case SOFT_CIRCLE: {
			Mat temp = this.alphaMat.clone();
			Core.circle(temp, new Point((this.size - 1) * 0.5d, (this.size - 1) * 0.5d), (this.size - 1) / 4, new Scalar(1.0f),
					-1);
			Imgproc.blur(temp, this.alphaMat, new Size(this.size * 0.5d, this.size * 0.5d));
		}
			break;
		default:
			/** this must not happen, die */
			throw new RuntimeException("unexpected BrushModel.Shape = " + this.shape);
		}
	}

	private void updateMultipliedMat() {
		this.multipliedMat = this.valueMat.mul(this.alphaMat);
	}

	public Mat getAlphaMat() {
		return this.alphaMat;
	}

	public Mat getOneMinusAlphaMat() {
		return this.oneMinusAlphaMat;
	}

	private void updateOneMinusAlphaMat() {
		this.oneMinusAlphaMat = this.alphaMat.clone();
		Core.subtract(new Mat(this.alphaMat.rows(), this.alphaMat.cols(), MatModel.MAT_TYPE, new Scalar(1.0d)), this.alphaMat,
				this.oneMinusAlphaMat);
	}

	public Mat getMultipliedMat() {
		return this.multipliedMat;
	}

	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		if (!(size >= 1)) {
			throw new IllegalArgumentException("Size must be at least 1.");
		}
		this.size = size;
		this.updateAllMats();
	}

	public Mat getValueMat() {
		return this.valueMat;
	}

	public Shape getShape() {
		return this.shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
		this.updateAllMats();
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		this.value = value;
		this.updateAllMats();
	}

	public Mode getMode() {
		return this.mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
		this.updateAllMats();
	}

	public boolean addListener(BrushListener l) {
		return this.listeners.contains(l) || this.listeners.add(l);
	}

	public boolean removeListener(BrushListener l) {
		return this.listeners.contains(l) && this.listeners.remove(l);
	}

	public void fireBrushChanged() {
		EventObject e = new EventObject(this);
		for (BrushListener l : this.listeners) {
			l.brushChanged(e);
		}
	}
}
