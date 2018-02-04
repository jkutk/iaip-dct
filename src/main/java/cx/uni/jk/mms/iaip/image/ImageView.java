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

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import org.opencv.core.Mat;

import cx.uni.jk.mms.iaip.filter.MatFilter;
import cx.uni.jk.mms.iaip.filter.MatFilterManager;
import cx.uni.jk.mms.iaip.main.AbstractMatView;
import cx.uni.jk.mms.iaip.mat.MatModel;

/**
 * View showing a {@link Mat} on an {@link ImagePanel} and providing a menu   
 */
public class ImageView extends AbstractMatView {
	private static final long serialVersionUID = 2762608585952443553L;
	final Logger logger = Logger.getGlobal();

	private final ImageController controller;
	ImagePanel imagePanel;
	private JLabel statusbar;

	public ImageView(ImageController controller, MatModel matModel) throws HeadlessException {
		super(controller, matModel);

		this.controller = controller; // subclass!
		this.initUI();
	}

	@Override
	protected void initUI() {
		super.initUI();

		/** look */
		this.setTitle(String.format("%s Image: %d", this.model.getName(), this.controller.getAndIncViewNumber()));
		this.setLayout(new BorderLayout());

		/** image */
		this.imagePanel = new ImagePanel(this.controller, this.model, this.dimension);
		this.getContentPane().add(new JScrollPane(this.imagePanel), BorderLayout.CENTER);

		/** status bar */
		this.statusbar = new JLabel("Hover image to get pixel info.");
		this.statusbar.setFont(new Font(Font.MONOSPACED, NORMAL, this.statusbar.getFont().getSize()));
		this.getContentPane().add(this.statusbar, BorderLayout.SOUTH);

		/** event listeners */
		this.imagePanel.addPixelChangeListener(this.pixelChangeListener);

		/** enhance menu */
		this.enhanceMenu();

		/** done */
		this.pack();
		this.setVisible(true);

		/** imagePanel needs some late rework in case this is not the first view */
		this.imagePanel.zoomFit();
	}

	private PixelChangeListener pixelChangeListener = new PixelChangeListener() {
		@Override
		public void pixelUnderMouseChanged(PixelChangeEvent e) {
			ImageView.this.statusbar.setText(String.format("%4d / %4d = %4.1f", (int) e.getPoint().getX(), (int) e.getPoint().getY(),
					e.getValue()));
		}
	};

	protected void enhanceMenu() {

		/** enhance file menu */
		int insertAt = 6;
		this.menuBar.fileMenu.insertSeparator(insertAt++);

		final JMenuItem saveVisibleImageItem = new JMenuItem(this.saveVisibleImageAction);
		saveVisibleImageItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveVisibleImageItem.setMnemonic('S');
		this.menuBar.fileMenu.insert(saveVisibleImageItem, insertAt++);

		final JMenuItem copyVisibleImageItem = new JMenuItem(this.copyVisibleImageToClipboardAction);
		copyVisibleImageItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		copyVisibleImageItem.setMnemonic('C');
		this.menuBar.fileMenu.insert(copyVisibleImageItem, insertAt++);

		/** filter menu and radio buttons */
		final JMenu valuesMenu = new JMenu("Values");
		valuesMenu.setMnemonic('V');

		boolean setSelected = true;
		final ButtonGroup filterGroup = new ButtonGroup();
		for (final MatFilter filter : MatFilterManager.getFiltersArray()) {
			final JRadioButtonMenuItem filterRadioItem = new JRadioButtonMenuItem(new AbstractAction(filter.toString()) {
				private static final long serialVersionUID = -6074013046309720156L;

				@Override
				public void actionPerformed(ActionEvent e) {
					ImageView.this.imagePanel.setMatFilter(filter);
				}
			});
			filterRadioItem.setSelected(setSelected);
			setSelected = false; // select only the first item
			filterGroup.add(filterRadioItem);
			valuesMenu.add(filterRadioItem);
		}
		valuesMenu.setMinimumSize(valuesMenu.getPreferredSize());
		this.menuBar.add(valuesMenu);

		/** zoom menu */
		final JMenu zoomMenu = new JMenu("Zoom");
		zoomMenu.setMnemonic('Z');

		final JMenuItem zoomInItem = new JMenuItem(this.zoomInAction);
		zoomInItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, ActionEvent.CTRL_MASK));
		zoomInItem.setMnemonic('+');
		zoomMenu.add(zoomInItem);

		final JMenuItem zoomOutItem = new JMenuItem(this.zoomOutAction);
		zoomOutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK));
		zoomOutItem.setMnemonic('-');
		zoomMenu.add(zoomOutItem);

		final JMenuItem zoomFitItem = new JMenuItem(this.zoomFitAction);
		zoomFitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		zoomFitItem.setMnemonic('F');
		zoomMenu.add(zoomFitItem);

		final JMenuItem zoomOriginalItem = new JMenuItem(this.zoomOriginalAction);
		zoomOriginalItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
		zoomOriginalItem.setMnemonic('L');
		zoomMenu.add(zoomOriginalItem);

		zoomMenu.setMinimumSize(zoomMenu.getPreferredSize());
		this.menuBar.add(zoomMenu);

		/** zoom buttons */
		final JButton zoomInButton = new JButton(this.zoomInAction);
		zoomInButton.setText("+");
		zoomInButton.setToolTipText((String) this.zoomInAction.getValue(Action.NAME));
		this.menuBar.add(zoomInButton);

		final JButton zoomOutButton = new JButton(this.zoomOutAction);
		zoomOutButton.setText("-");
		zoomOutButton.setToolTipText((String) this.zoomOutAction.getValue(Action.NAME));
		this.menuBar.add(zoomOutButton);

		final JButton zoomFitButton = new JButton(this.zoomFitAction);
		zoomFitButton.setText("=");
		zoomFitButton.setToolTipText((String) this.zoomFitAction.getValue(Action.NAME));
		this.menuBar.add(zoomFitButton);

		final JButton zoomOriginalButton = new JButton(this.zoomOriginalAction);
		zoomOriginalButton.setText("1");
		zoomOriginalButton.setToolTipText((String) this.zoomOriginalAction.getValue(Action.NAME));
		this.menuBar.add(zoomOriginalButton);
	}

	private Action zoomInAction = new AbstractAction(String.format("Zoom In by %.0f %%",
			(ImagePanel.getScaleStepFactor() - 1.0f) * 100.0F)) {
		private static final long serialVersionUID = -9125232768358291161L;

		@Override
		public void actionPerformed(ActionEvent e) {
			ImageView.this.imagePanel.zoomIn();
		}
	};

	private Action zoomOutAction = new AbstractAction(String.format("Zoom Out by %.0f %%",
			(ImagePanel.getScaleStepFactor() - 1.0f) * 100.0F)) {
		private static final long serialVersionUID = -3583693734678853600L;

		@Override
		public void actionPerformed(ActionEvent e) {
			ImageView.this.imagePanel.zoomOut();
		}
	};

	private Action zoomFitAction = new AbstractAction("Zoom to Fit Window") {
		private static final long serialVersionUID = -6453158641784095639L;

		@Override
		public void actionPerformed(ActionEvent e) {
			ImageView.this.imagePanel.zoomFit();
		}
	};

	private Action zoomOriginalAction = new AbstractAction("Zoom to Original Size") {
		private static final long serialVersionUID = -6090975392685012477L;

		@Override
		public void actionPerformed(ActionEvent e) {
			ImageView.this.imagePanel.setScale(1.0d);
		}
	};

	protected Action saveVisibleImageAction = new AbstractAction("Save Visible Image As PNG ...") {
		private static final long serialVersionUID = 3867216186667215685L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (fc.showSaveDialog(ImageView.this) == JFileChooser.APPROVE_OPTION) {
				try {
					ImageView.this.controller.saveImage(ImageView.this.imagePanel.getImage(), fc.getSelectedFile());
				} catch (IOException e1) {
					ImageView.this.showExceptionDialog(e1);
				}
			}
		}
	};
	protected Action copyVisibleImageToClipboardAction = new AbstractAction("Copy Visible Image to Clipboard") {
		private static final long serialVersionUID = -8131117888948799031L;

		@Override
		public void actionPerformed(ActionEvent e) {
			ImageView.this.controller.copyImageToClipboard(ImageView.this.imagePanel.getImage());
		}
	};
}