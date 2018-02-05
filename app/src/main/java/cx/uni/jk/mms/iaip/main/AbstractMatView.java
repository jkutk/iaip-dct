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

package cx.uni.jk.mms.iaip.main;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import org.opencv.core.CvException;
import org.opencv.core.Mat;

import cx.uni.jk.mms.iaip.examples.ExampleManager;
import cx.uni.jk.mms.iaip.image.ImageSizeDialog;
import cx.uni.jk.mms.iaip.mat.IllegalSizeException;
import cx.uni.jk.mms.iaip.mat.MatModel;

/**
 * Common set of methods for a view of a {@link Mat}
 */
public abstract class AbstractMatView extends JFrame {
	private static final long serialVersionUID = 272493587608693723L;
	private final Logger logger = Logger.getGlobal();

	private static final int NEW_VIEW_LOCATION_OFFSET = 50;

	protected static final JFileChooser fc = new JFileChooser();

	protected final AbstractMatController controller;
	protected final MatModel model;
	protected MatViewMenuBar menuBar;

	protected Dimension dimension;

	/**
	 * note: it is the subclass' duty to call initUI();
	 * 
	 * @param controller
	 * @param model
	 */
	public AbstractMatView(AbstractMatController controller, MatModel model) {
		super();
		this.controller = controller;
		this.model = model;
	}

	protected void initUI() {
		/** behaviour */
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.addWindowListener(this.windowListener);

		/** menu */
		this.menuBar = new MatViewMenuBar();
		this.setJMenuBar(this.menuBar);
	}

	protected class MatViewMenuBar extends JMenuBar {
		private static final long serialVersionUID = -4267130216278193894L;
		public final JMenu fileMenu;
		public final JMenu windowMenu;

		public MatViewMenuBar() {
			super();

			/** file menu */
			this.fileMenu = new JMenu("File");
			this.fileMenu.setMnemonic('F');

			final JMenuItem newMatricesItem = new JMenuItem(AbstractMatView.this.newMatricesAction);
			newMatricesItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
			newMatricesItem.setMnemonic('N');
			this.fileMenu.add(newMatricesItem);

			this.fileMenu.add(new ExamplesMenu());

			final JMenuItem openImageItem = new JMenuItem(AbstractMatView.this.openImageAction);
			openImageItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
			openImageItem.setMnemonic('O');
			this.fileMenu.add(openImageItem);

			final JMenuItem reopenImageItem = new JMenuItem(AbstractMatView.this.reopenImageAction);
			reopenImageItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
			reopenImageItem.setMnemonic('R');
			this.fileMenu.add(reopenImageItem);

			this.fileMenu.addSeparator();

			final JMenuItem saveMatrixAsCsvItem = new JMenuItem(AbstractMatView.this.saveMatrixAsCsvAction);
			saveMatrixAsCsvItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
			saveMatrixAsCsvItem.setMnemonic('M');
			this.fileMenu.add(saveMatrixAsCsvItem);

			this.fileMenu.addSeparator();

			final JMenuItem aboutItem = new JMenuItem(AbstractMatView.this.aboutAction);
			aboutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
			aboutItem.setMnemonic('A');
			this.fileMenu.add(aboutItem);

			final JMenuItem exitItem = new JMenuItem(AbstractMatView.this.exitAction);
			exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
			exitItem.setMnemonic('Q');
			this.fileMenu.add(exitItem);

			this.add(this.fileMenu);
			this.fileMenu.setMinimumSize(this.fileMenu.getPreferredSize());

			/** window menu */
			this.windowMenu = new JMenu("Window");
			this.windowMenu.setMnemonic('W');

			final JMenuItem newSpatialImageViewItem = new JMenuItem(AbstractMatView.this.newSpatialImageViewAction);
			newSpatialImageViewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK));
			newSpatialImageViewItem.setMnemonic('S');
			this.windowMenu.add(newSpatialImageViewItem);

			final JMenuItem newSpatialTableViewItem = new JMenuItem(AbstractMatView.this.newSpatialTableViewAction);
			newSpatialTableViewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.CTRL_MASK));
			newSpatialTableViewItem.setMnemonic('P');
			this.windowMenu.add(newSpatialTableViewItem);

			final JMenuItem newFrequencyImageViewItem = new JMenuItem(AbstractMatView.this.newFrequencyImageViewAction);
			newFrequencyImageViewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.CTRL_MASK));
			newFrequencyImageViewItem.setMnemonic('F');
			this.windowMenu.add(newFrequencyImageViewItem);

			final JMenuItem newFrequencyTableViewItem = new JMenuItem(AbstractMatView.this.newFrequencyTableViewAction);
			newFrequencyTableViewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.CTRL_MASK));
			newFrequencyTableViewItem.setMnemonic('R');
			this.windowMenu.add(newFrequencyTableViewItem);

			this.windowMenu.addSeparator();

			final JMenuItem closeViewItem = new JMenuItem(AbstractMatView.this.closeViewAction);
			closeViewItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
			closeViewItem.setMnemonic('W');
			this.windowMenu.add(closeViewItem);

			this.add(this.windowMenu);
			this.windowMenu.setMinimumSize(this.windowMenu.getPreferredSize());
		}
	}

	protected Action openImageAction = new AbstractAction("Open Image File ...") {
		private static final long serialVersionUID = -5517469969781202512L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (fc.showOpenDialog(AbstractMatView.this) == JFileChooser.APPROVE_OPTION) {
				try {
					AbstractMatView.this.controller.loadImage(fc.getSelectedFile().toPath());
				} catch (CvException | IllegalSizeException | IOException e1) {
					AbstractMatView.this.showExceptionDialog(e1);
				}
			}
		}
	};
	protected Action reopenImageAction = new AbstractAction("Re-Open Last Image") {
		private static final long serialVersionUID = -5517469969781202512L;

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				AbstractMatView.this.controller.reloadImage();
			} catch (CvException | IllegalSizeException | IOException e1) {
				AbstractMatView.this.showExceptionDialog(e1);
			}
		}
	};

	protected Action saveMatrixAsCsvAction = new AbstractAction("Save Matrix As CSV ...") {
		private static final long serialVersionUID = 3867216186667215685L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (fc.showSaveDialog(AbstractMatView.this) == JFileChooser.APPROVE_OPTION) {
				try {
					AbstractMatView.this.controller.saveMatrixAsCsv(fc.getSelectedFile());
				} catch (CvException e1) {
					AbstractMatView.this.showExceptionDialog(e1);
				}
			}
		}
	};

	protected Action newSpatialImageViewAction = new AbstractAction("New Spatial Image Window") {
		private static final long serialVersionUID = -6273581369100540974L;

		@Override
		public void actionPerformed(ActionEvent e) {
			Point here = AbstractMatView.this.getLocationOnScreen();
			AbstractMatView.this.controller.addNewSpatialImageView().setLocation(here.x + NEW_VIEW_LOCATION_OFFSET,
					here.y + NEW_VIEW_LOCATION_OFFSET);
		}
	};

	protected Action newFrequencyImageViewAction = new AbstractAction("New Frequency Image Window") {
		private static final long serialVersionUID = -6812173496719428208L;

		@Override
		public void actionPerformed(ActionEvent e) {
			Point here = AbstractMatView.this.getLocationOnScreen();
			AbstractMatView.this.controller.addNewFrequencyImageView().setLocation(here.x + NEW_VIEW_LOCATION_OFFSET,
					here.y + NEW_VIEW_LOCATION_OFFSET);
		}
	};

	protected Action newSpatialTableViewAction = new AbstractAction("New Spatial Table Window") {
		private static final long serialVersionUID = -6273581369100540974L;

		@Override
		public void actionPerformed(ActionEvent e) {
			Point here = AbstractMatView.this.getLocationOnScreen();
			AbstractMatView.this.controller.addNewSpatialTableView().setLocation(here.x + NEW_VIEW_LOCATION_OFFSET,
					here.y + NEW_VIEW_LOCATION_OFFSET);
		}
	};

	protected Action newFrequencyTableViewAction = new AbstractAction("New Frequency Table Window") {
		private static final long serialVersionUID = -6812173496719428208L;

		@Override
		public void actionPerformed(ActionEvent e) {
			Point here = AbstractMatView.this.getLocationOnScreen();
			AbstractMatView.this.controller.addNewFrequencyTableView().setLocation(here.x + NEW_VIEW_LOCATION_OFFSET,
					here.y + NEW_VIEW_LOCATION_OFFSET);
		}
	};

	protected Action newMatricesAction = new AbstractAction("New Matrices") {
		private static final long serialVersionUID = 5381737270039079951L;

		@Override
		public void actionPerformed(ActionEvent e) {
			Dimension size = ImageSizeDialog.show(AbstractMatView.this, new Dimension(AbstractMatView.this.model.getWidth(),
					AbstractMatView.this.model.getHeight()));
			if (size != null) {
				AbstractMatView.this.logger.finest("Size " + size.getWidth() + "/" + size.getHeight());
				try {
					AbstractMatView.this.controller.clear((int) size.getWidth(), (int) size.getHeight());
				} catch (IllegalSizeException e1) {
					AbstractMatView.this.showExceptionDialog(e1);
				}
			}
		}
	};

	protected Action exitAction = new AbstractAction("Quit") {
		private static final long serialVersionUID = 8881001580314308426L;

		@Override
		public void actionPerformed(ActionEvent e) {
			AbstractMatView.this.controller.exit();
		}
	};

	protected WindowListener windowListener = new WindowAdapter() {

		@Override
		public void windowClosing(WindowEvent e) {
			super.windowClosing(e);
			AbstractMatView.this.controller.removeView(AbstractMatView.this);
		}
	};

	protected Action aboutAction = new AbstractAction("About") {
		private static final long serialVersionUID = -8008992146033936710L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(AbstractMatView.this,
					"InterActive Image Processing / Discrete Cosine Transformation (DCT)\n"
							+ "\n"
							+ "Copyright (C) 2015 Peter Plaimer <dct-tool@tk.jku.at>\n"
							+ "\n"
							+ "This program source code is distributed under the GNU GPL 3.0 license, see below.\n"
							+ "Its binary form contains the OpenCV libraries from https://opencv.org as well as corresponding Java bindings and\n"
							+ "packaging from https://github.com/PatternConsulting/opencv which are licensed under the 3-clause BSD License, see below.\n"
							+ "\n"
							+ "------------------------------------------\n"
							+ "GNU GPL 3.0\n"
							+ "This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of\n"
							+ "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.\n"
							+ "You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.\n"
							+ "------------------------------------------\n"
							+ "3-clause BSD License\n"
							+"By downloading, copying, installing or using the software you agree to this license.\n"
							+"If you do not agree to this license, do not download, install, copy or use the software.\n"
							+"                          License Agreement\n"
							+"               For Open Source Computer Vision Library\n"
							+"                       (3-clause BSD License)\n"
							+"Redistribution and use in source and binary forms, with or without modification,\n"
							+"are permitted provided that the following conditions are met:\n"
							+"  * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.\n"
							+"  * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer \n"
							+"    in the documentation and/or other materials provided with the distribution.\n"
							+"  * Neither the names of the copyright holders nor the names of the contributors may be used to endorse or promote products derived \n"
							+"    from this software without specific prior written permission.\n"
							+"This software is provided by the copyright holders and contributors \"as is\" and any express or implied warranties, including, but not limited to, the implied\n"
							+"warranties of merchantability and fitness for a particular purpose are disclaimed. In no event shall copyright holders or contributors be liable for any direct,\n"
							+"indirect, incidental, special, exemplary, or consequential damages (including, but not limited to, procurement of substitute goods or services;\n"
							+"loss of use, data, or profits; or business interruption) however caused and on any theory of liability, whether in contract, strict liability,\n"
							+"or tort (including negligence or otherwise) arising in any way out of the use of this software, even if advised of the possibility of such damage.\n"
							+ "\n"
							,
					"About IAIP/DCT Version 1.0.0-pre", JOptionPane.INFORMATION_MESSAGE);
		}
	};

	protected void showExceptionDialog(Exception e1) {
		JOptionPane.showMessageDialog(AbstractMatView.this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	}

	protected class ExamplesMenu extends JMenu {
		private static final long serialVersionUID = 2649315105388795048L;

		public ExamplesMenu() {
			super("Open Example Image");

			this.setMnemonic('E');

			for (Path path : ExampleManager.getInstance().getPaths()) {
				this.add(new OpenExampleAction(path));
			}
		}
	}

	private class OpenExampleAction extends AbstractAction {
		private static final long serialVersionUID = -7766524826698369246L;
		private final Path path;

		public OpenExampleAction(Path path) {
			super(path.getFileName().toString());
			this.path = path;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				AbstractMatView.this.controller.loadImage(this.path);
			} catch (Exception e1) {
				AbstractMatView.this.showExceptionDialog(e1);
			}
		}
	}

	protected Action closeViewAction = new AbstractAction("Close Window") {
		private static final long serialVersionUID = 6672794483673288882L;

		@Override
		public void actionPerformed(ActionEvent e) {
			AbstractMatView.this.setVisible(false);
			AbstractMatView.this.dispose();
			AbstractMatView.this.controller.removeView(AbstractMatView.this);
		}

	};
}