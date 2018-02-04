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

package cx.uni.jk.mms.iaip.mat;

import java.util.logging.Logger;

import org.opencv.core.Core;
import org.opencv.core.Mat;

/**
 * Transfer changes in one {@link MatModel} to the other using DCT or IDCT.
 */
public class CrossMatListener implements MatChangeListener {
	private final Logger logger = Logger.getGlobal();

	private MatModel target;
	private boolean inverse;
	private static boolean ignoreMatEvent = false;
	private final Object lockIgnoreMatEvent = new Object();

	public CrossMatListener(MatModel target, boolean inverse) {
		super();
		this.target = target;
		this.inverse = inverse;
	}

	@Override
	public void matModified(MatChangeEvent e) {
		synchronized (this.lockIgnoreMatEvent) {
			if (ignoreMatEvent) {
				return;
			}
			ignoreMatEvent = true;
		}

		MatModel source = (MatModel) e.getSource();
		this.transfer(source);
		this.target.fireMatChangedEvent();

		synchronized (this.lockIgnoreMatEvent) {
			ignoreMatEvent = false;
		}
	}

	@Override
	public void matLoaded(MatChangeEvent e) {
		synchronized (this.lockIgnoreMatEvent) {
			if (ignoreMatEvent) {
				return;
			}
			ignoreMatEvent = true;
		}

		MatModel source = (MatModel) e.getSource();
		try {
			this.target.clear(source.getWidth(), source.getHeight());
		} catch (IllegalSizeException e1) {
			/** this should never happen, die now. */
			this.logger.severe(e1.getStackTrace().toString());
			System.exit(e1.hashCode());
		}
		this.transfer(source);
		this.target.fireMatLoadedEvent();

		synchronized (this.lockIgnoreMatEvent) {
			ignoreMatEvent = false;
		}
	}

	/**
	 * do the transfer from source to this.target, using DCT or IDCT
	 * 
	 * @param source
	 */
	private void transfer(MatModel source) {
		Mat matSource = source.getMat();
		Mat matTarget = new Mat(matSource.size(), MatModel.MAT_TYPE);
		if (this.inverse) {
			Core.idct(matSource, matTarget);
		} else {
			Core.dct(matSource, matTarget);
		}
		this.target.setMat(matTarget);
	}
}
