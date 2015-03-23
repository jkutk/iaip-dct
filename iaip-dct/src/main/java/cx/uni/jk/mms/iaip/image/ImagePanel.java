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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import javax.swing.JPanel;

import org.opencv.core.Mat;

import cx.uni.jk.mms.iaip.filter.MatFilter;
import cx.uni.jk.mms.iaip.filter.MatFilterManager;
import cx.uni.jk.mms.iaip.filter.MatHelper;
import cx.uni.jk.mms.iaip.mat.MatChangeEvent;
import cx.uni.jk.mms.iaip.mat.MatChangeListener;
import cx.uni.jk.mms.iaip.mat.MatModel;

/**
 * A {@link Panel} showing a scaled {@link Mat} and doing all the math for the mouse 
 * 
 */
public class ImagePanel extends JPanel {
	private static final long serialVersionUID = -415249989833860937L;

	private final Logger logger = Logger.getGlobal();

	/** steps of scale / zoom to double the size of the image */
	private static final int SCALE_STEPS = 2;
	private static final double SCALE_STEP_FACTOR = Math.pow(2.0d, 1.0d / SCALE_STEPS);

	private final ImageController controller;
	private final MatModel matModel;
	private BufferedImage image = null;
	private double scale = 1.0d;

	private MatFilter matFilter;

	private List<PixelChangeListener> pixelChangeListeners = new CopyOnWriteArrayList<>();
	private MouseAdapterScaling mouseScaler;

	public ImagePanel(ImageController controller, MatModel matModel, Dimension dimension) {
		super();
		this.controller = controller;
		this.matModel = matModel;

		this.setPreferredSize(new Dimension(512, 512));

		this.matModel.addMatChangeListener(this.matChangeListener);
		this.matFilter = MatFilterManager.getDefaultFilter();

		/**
		 * all mouse event coordinates will be scaled down, reversing the
		 * transformation applied when painting the component
		 */
		this.mouseScaler = new MouseAdapterScaling();
		this.addMouseListener(this.mouseScaler);
		this.addMouseMotionListener(this.mouseScaler);
		this.addMouseWheelListener(this.mouseScaler);

		this.mouseScaler.addMouseAdapter(this.mouseAdapter);

		/** make correct state in case this is not the first panel or view */
		if (this.matModel.getHeight() > 0) {
			this.updateImageFromMat();
		}
	}

	public static double getScaleStepFactor() {
		return ImagePanel.SCALE_STEP_FACTOR;
	}

	public MatFilter getMatConverter() {
		return this.matFilter;
	}

	public void setMatFilter(MatFilter matFilter) {
		this.matFilter = matFilter;
		this.updateImageFromMat();
	}

	@Override
	public Dimension getPreferredSize() {
		if (this.image == null) {
			return super.getPreferredSize();
		} else {
			return new Dimension((int) (this.matModel.getWidth() * this.scale), (int) (this.matModel.getHeight() * this.scale));
		}
	}

	@Override
	public Dimension getMinimumSize() {
		return this.getPreferredSize();
	}

	@Override
	public Dimension getMaximumSize() {
		return this.getPreferredSize();
	}

	private void setImage(BufferedImage image) {
		this.image = image;

		this.revalidate();
		this.repaint();
	}

	public void setScale(double scale) {
		this.scale = scale;
		this.mouseScaler.setScale(scale);

		this.revalidate();
		this.repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.GRAY);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		if (this.image != null) {
			AffineTransform at = new AffineTransform();
			at.scale(this.scale, this.scale);
			g2d.drawImage(this.image, at, null);
		}
	}

	public void updateImageFromMat() {
		this.setImage(MatHelper.convertMatTo8BitBufferedImage(this.matFilter.convert(this.matModel.getMat())));
	}

	public double getFitScale() {
		return ((Math.min((double) this.getParent().getWidth() / (double) this.matModel.getWidth(), (double) this.getParent().getHeight()
				/ (double) this.matModel.getHeight())));
	}

	private MatChangeListener matChangeListener = new MatChangeListener() {
		@Override
		public void matModified(MatChangeEvent e) {
			ImagePanel.this.updateImageFromMat();
		}

		@Override
		public void matLoaded(MatChangeEvent e) {
			ImagePanel.this.zoomFit();
			ImagePanel.this.updateImageFromMat();

			ImagePanel.this.logger.finer(String.format("image loaded: %dx%d pixel, parent %dx%d, scale %f, panel %dx%d", ImagePanel.this.image.getWidth(),
					ImagePanel.this.image.getHeight(), ImagePanel.this.getParent().getWidth(), ImagePanel.this.getParent().getHeight(), ImagePanel.this.scale,
					ImagePanel.this.getWidth(), ImagePanel.this.getHeight()));
		}
	};

	private void firePixelEvent(Point point) {
		if (this.image != null && point.getX() < this.image.getWidth() && point.getY() < this.image.getHeight()) {
			float value = this.matModel.getValue((int) point.getX(), (int) point.getY());
			PixelChangeEvent e = new PixelChangeEvent(this, point, value);
			for (PixelChangeListener l : this.pixelChangeListeners) {
				l.pixelUnderMouseChanged(e);
			}
		}
	}

	public boolean addPixelChangeListener(PixelChangeListener l) {
		return (!this.pixelChangeListeners.contains(l) && this.pixelChangeListeners.add(l));
	}

	public boolean removePixelChangeListener(PixelChangeListener l) {
		return this.pixelChangeListeners.remove(l);
	}

	private MouseAdapter mouseAdapter = new MouseAdapter() {
		static final int MB_NORMAL = MouseEvent.BUTTON1_DOWN_MASK;
		static final int MB_INVERSE = MouseEvent.BUTTON3_DOWN_MASK;

		@Override
		public void mouseMoved(MouseEvent e) {
			super.mouseMoved(e);
			ImagePanel.this.firePixelEvent(e.getPoint());
		}

		@Override
		public void mousePressed(MouseEvent e) {
			this.useTool(e);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			this.useTool(e);
		}

		/**
		 * @param e
		 */
		private void useTool(MouseEvent e) {
			if ((e.getModifiersEx() & MB_NORMAL) == MB_NORMAL) {
				ImagePanel.this.controller.useTool(e.getX(), e.getY(), false);
			} else if ((e.getModifiersEx() & MB_INVERSE) == MB_INVERSE) {
				ImagePanel.this.controller.useTool(e.getX(), e.getY(), true);
			}
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			// TODO Auto-generated method stub
			super.mouseWheelMoved(e);

			if (e.getWheelRotation() < 0) {
				ImagePanel.this.zoomIn();
			} else {
				ImagePanel.this.zoomOut();
			}
		}
	};

	public void zoomIn() {
		this.setScale(this.scale * SCALE_STEP_FACTOR);
	}

	public void zoomOut() {
		this.setScale(this.scale / SCALE_STEP_FACTOR);
	}

	public void zoomFit() {
		this.setScale(this.getFitScale());
	}

	public BufferedImage getImage() {
		return this.image;
	}
}
