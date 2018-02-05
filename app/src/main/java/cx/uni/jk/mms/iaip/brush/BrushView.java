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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cx.uni.jk.mms.iaip.brush.BrushModel.Mode;
import cx.uni.jk.mms.iaip.brush.BrushModel.Shape;

/**
 * 
 */
public class BrushView extends JFrame {
	private static final long serialVersionUID = 311376288196370571L;

	private final BrushController controller;
	private final BrushModel model;
	private JSpinner sizeSpinner;
	private JSpinner valueSpinner;
	private JComboBox<BrushModel.Mode> modeCombo;
	private JComboBox<BrushModel.Shape> shapeCombo;
	private JLabel mb1Label;
	private JLabel mb2Label;

	private GridBagLayout gridBag;
	private int gridBagRow = 0;

	public BrushView(BrushController controller, BrushModel model) {
		super();
		this.controller = controller;
		this.model = model;

		this.initUI();
	}

	/**
	 * 
	 */
	private void initUI() {
		/** setup */
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setTitle("Brush Settings");

		/** create controls */

		this.sizeSpinner = new JSpinner(new SpinnerNumberModel(this.model.getSize(), 1, 256, 1));
		this.sizeSpinner.setName("Size");
		this.sizeSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner comp = (JSpinner) e.getSource();
				BrushView.this.controller.setSize((int) comp.getValue());
			}
		});

		this.valueSpinner = new JSpinner(new SpinnerNumberModel(this.model.getValue(), -(1 << 20), +(1 << 20), 1));
		this.valueSpinner.setName("Value");
		this.valueSpinner.setPreferredSize(new Dimension(64, this.valueSpinner.getPreferredSize().height));
		this.valueSpinner.setMinimumSize(this.valueSpinner.getPreferredSize());
		this.valueSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner comp = (JSpinner) e.getSource();
				BrushView.this.controller.setValue((int) comp.getValue());
			}
		});

		this.modeCombo = new JComboBox<BrushModel.Mode>(BrushModel.Mode.values());
		this.modeCombo.setName("Mode");
		this.modeCombo.setSelectedItem(this.model.getMode());
		this.modeCombo.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<BrushModel.Mode> jComboBox = (JComboBox<BrushModel.Mode>) e.getSource();
				BrushView.this.controller.setMode((Mode) jComboBox.getSelectedItem());
			}
		});
		;

		this.mb1Label = new JLabel();
		this.mb1Label.setName("1st MB");
		this.mb1Label.setText("dummy");
		this.mb1Label.setPreferredSize(new Dimension(120, this.mb1Label.getPreferredSize().height));
		this.mb2Label = new JLabel();
		this.mb2Label.setName("2nd MB");
		this.mb2Label.setPreferredSize(new Dimension(120, this.mb1Label.getPreferredSize().height));

		this.shapeCombo = new JComboBox<BrushModel.Shape>(BrushModel.Shape.values());
		this.shapeCombo.setName("Shape");
		this.shapeCombo.setSelectedItem(this.model.getShape());
		this.shapeCombo.addActionListener(new ActionListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox<BrushModel.Shape> jComboBox = (JComboBox<BrushModel.Shape>) e.getSource();
				BrushView.this.controller.setShape((Shape) jComboBox.getSelectedItem());
			}
		});
		;

		/** set up listener. oops, creates a lot of feedback. */
		this.model.addListener(new BrushListener() {

			@Override
			public void brushChanged(EventObject e) {
				BrushModel model = (BrushModel) e.getSource();
				BrushView.this.sizeSpinner.getModel().setValue(model.getSize());
				BrushView.this.shapeCombo.getModel().setSelectedItem(model.getShape());
				BrushView.this.valueSpinner.getModel().setValue(model.getValue());
				BrushView.this.modeCombo.getModel().setSelectedItem(model.getMode());
				BrushView.this.mb1Label.setText(model.getMode().getMb1Label());
				BrushView.this.mb2Label.setText(model.getMode().getMb2Label());
			}
		});

		this.gridBag = new GridBagLayout();
		this.setLayout(this.gridBag);

		this.addWithLabel(this.sizeSpinner);
		this.addWithLabel(this.shapeCombo);
		this.addWithLabel(this.valueSpinner);
		this.addWithLabel(this.modeCombo);
		this.addWithLabel(this.mb1Label);
		this.addWithLabel(this.mb2Label);

		/** done */
		this.pack();
		this.setVisible(true);
	}

	protected void addWithLabel(Component component) {
		GridBagConstraints c;

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = this.gridBagRow;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.0;
		c.weighty = 1.0;
		JLabel label = new JLabel(component.getName() + ":");
		label.setLabelFor(component);
		this.getContentPane().add(label, c);

		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = this.gridBagRow;
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		c.weighty = 1.0;
		component.setFont(Font.getFont(Font.DIALOG_INPUT));
		this.getContentPane().add(component, c);

		this.gridBagRow++;
	}
}
