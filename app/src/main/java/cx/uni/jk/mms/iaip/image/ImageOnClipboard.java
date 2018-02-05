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

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * A {@link Clipboard} holder for a {@link BufferedImage}
 */
public class ImageOnClipboard implements ClipboardOwner {

	public ImageOnClipboard(BufferedImage image) {
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new TransferableImage(image), this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer
	 * .Clipboard, java.awt.datatransfer.Transferable)
	 */
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		/** nevermind */
	}

	private class TransferableImage implements Transferable {
		private Image image;

		public TransferableImage(Image image) {
			this.image = image;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
		 */
		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { DataFlavor.imageFlavor };
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt
		 * .datatransfer.DataFlavor)
		 */
		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			for (DataFlavor f : this.getTransferDataFlavors()) {
				if (flavor.equals(f)) {
					return true;
				}
			}
			return false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer
		 * .DataFlavor)
		 */
		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if (this.isDataFlavorSupported(flavor) && this.image != null) {
				return this.image;
			} else {
				throw new UnsupportedFlavorException(flavor);
			}
		}
	}

}