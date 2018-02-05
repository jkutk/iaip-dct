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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Rect;

import cx.uni.jk.mms.iaip.main.AbstractMatController;
import cx.uni.jk.mms.iaip.main.AbstractMatView;
import cx.uni.jk.mms.iaip.main.MainController;
import cx.uni.jk.mms.iaip.mat.MatModel;

/**
 * Controller for one {@link ImageView} and one {@link MatModel}
 */
public class ImageController extends AbstractMatController {
	// private final Logger logger = Logger.getGlobal();

	public ImageController(MainController mainController, MatModel model) {
		super(mainController, model);
	}

	/**
	 * use the current tool at the given position.
	 * 
	 * @param x
	 * @param y
	 * @param inverseEffect
	 */
	public void useTool(int x, int y, boolean inverseEffect) {
		Rect changedArea = this.mainController.getTool().apply(this.model.getMat(), this.mainController.getBrushModel(), x, y,
				inverseEffect);
		if (changedArea != null) {
			this.model.fireMatChangedEvent(changedArea);
		}
	}

	/**
	 * saves an image to a .png file, no matter if it fits the extension.
	 * 
	 * @param image
	 * @param file
	 * @throws IOException
	 */
	public void saveImage(BufferedImage image, File file) throws IOException {
		ImageIO.write(image, "png", file);
	}

	/**
	 * @param image
	 */
	public void copyImageToClipboard(BufferedImage image) {
		new ImageOnClipboard(image);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see image.AbstractMatController#addNewMatView()
	 */
	@Override
	public AbstractMatView addNewView() {
		AbstractMatView view = new ImageView(this, this.model);
		this.addView(view);
		return view;
	}

}
