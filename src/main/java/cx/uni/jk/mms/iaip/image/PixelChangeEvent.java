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

package cx.uni.jk.mms.iaip.image;

import java.awt.Point;
import java.util.EventObject;

/**
 * Carries information which Pixel has changed to which value
 */
public class PixelChangeEvent extends EventObject {
	private static final long serialVersionUID = 6459448167528755926L;

	private final Point point;
	private final float value;

	public PixelChangeEvent(Object source, Point point, float value) {
		super(source);
		this.point = point;
		this.value = value;
	}

	public Point getPoint() {
		return this.point;
	}

	public float getValue() {
		return this.value;
	}
}
