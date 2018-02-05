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

package cx.uni.jk.mms.iaip.image;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.opencv.core.Mat;

/**
 * Dialog asking the size for a new {@link Mat} 
 */
public class ImageSizeDialog {

	public static Dimension show(JFrame frame, Dimension size) {
		JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel((int) size.getWidth(), 0, 4096, 2));
		JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel((int) size.getHeight(), 0, 4096, 2));

		Object[] message = { "Width:", widthSpinner, "Height:", heightSpinner };

		int option = JOptionPane.showConfirmDialog(frame, message, "Create New Matrix", JOptionPane.OK_CANCEL_OPTION);

		if (option == JOptionPane.OK_OPTION) {
			return new Dimension((Integer) widthSpinner.getValue(), (Integer) heightSpinner.getValue());
		} else {
			return null;
		}
	}
}
