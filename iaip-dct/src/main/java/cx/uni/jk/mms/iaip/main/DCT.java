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

package cx.uni.jk.mms.iaip.main;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencv.core.Core;
import org.opencv.core.CvException;

import cx.uni.jk.mms.iaip.mat.IllegalSizeException;

/**
 * Main class of the program
 * Inter-Active Image Processing / Discrete Cosine Transformation (DCT)
 */

public class DCT {

	/**
	 * loads OpenCV native library loaders and the libraries. @see <a
	 * href="https://github.com/patternconsulting/opencv">documentation</a>.
	 */
	static {
		nu.pattern.OpenCV.loadShared();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static void main(String[] args) {
		Logger logger = Logger.getGlobal();
		/** enable logging for debugging purposes: set to ALL */
		logger.setLevel(Level.INFO);
		ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(Level.ALL);
		logger.addHandler(consoleHandler);

		/** start application */
		try {
			new MainController(new MainModel(0, 0));
		} catch (CvException | IllegalSizeException e) {
			logger.severe(e.getStackTrace().toString());
			System.exit(e.hashCode());
		}
	}
}
