package com.cadplan.jump.plugins.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import com.cadplan.jump.ui.VertexColorButton;
import com.cadplan.jump.utils.VertexParams;
import com.vividsolutions.jump.I18N;

public class ColorPanel extends JPanel {
	public VertexColorButton lineColorButton;
	public VertexColorButton fillColorButton;
	public JLabel fillColorLabel;
	public JLabel lineColorLabel;
	public JCheckBox synchronizeCheckBox = new JCheckBox();
	private static final long serialVersionUID = 1L;

	;
	boolean addSyncronized;
	boolean addTransaprencyPane;

	public static SpinnerModel dimensionModel = new SpinnerNumberModel(40, 10, 200, 1);


	public JPanel lowerPane;

	public ColorPanel(Color lineColor, Color fillColor) {

		GridLayout layout = new GridLayout(0, 1);
		this.setLayout(layout);

		this.fillColorLabel = new JLabel(I18N.get("ui.style.BasicStylePanel.fill"));
		this.fillColorButton = new VertexColorButton(fillColor);
		this.fillColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ColorPanel.this.fillColorChooserPanel_actionPerformed(e);
			}
		});
		JPanel fillPane = new JPanel(new GridBagLayout());
		fillPane.add(this.fillColorLabel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
		fillPane.add(this.fillColorButton, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
		this.lineColorLabel = new JLabel(I18N.get("ui.style.BasicStylePanel.line"));
		this.lineColorButton = new VertexColorButton(lineColor);
		this.lineColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ColorPanel.this.lineColorChooserPanel_actionPerformed(e);
			}
		});
		JPanel linePane = new JPanel(new GridBagLayout());
		linePane.add(this.lineColorLabel, new GridBagConstraints(0, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
		linePane.add(this.lineColorButton, new GridBagConstraints(1, 0, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(0, 0, 0, 0), 0, 0));
		this.synchronizeCheckBox.setText(I18N.get("org.openjump.core.ui.plugin.view.ViewOptionsPlugIn.Synchronize"));
		this.synchronizeCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ColorPanel.this.synchronizeCheckBox_actionPerformed(e);
			}
		});
		//GridLayout layout = new GridLayout(0, 2);
		JPanel fillerPane = new JPanel();

		this.lowerPane = new JPanel(new GridBagLayout());
		this.lowerPane.add(linePane, new GridBagConstraints(1, 1, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0));
		this.lowerPane.add(fillPane, new GridBagConstraints(2, 1, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0));
		this.lowerPane.add(this.synchronizeCheckBox, new GridBagConstraints(3, 1, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0));
		this.lowerPane.add(fillerPane, new GridBagConstraints(4, 1, 0, 0, 1000.0D, 1000.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0));
		this.add(this.lowerPane);  
		this.setValues();
	}



	public void setValues() {
		this.synchronizeCheckBox.setSelected(VertexParams.selectedLayer.isSynchronizingLineColor());

	}

	public void setValues2() {
		this.synchronizeCheckBox.setSelected(VertexParams.selectedLayer.isSynchronizingLineColor());

	}


	public Color getLineColor() {
		return this.lineColorButton.getBackground();
	}

	public Color getFillColor() {
		return this.fillColorButton.getBackground();
	}

	private void syncLineColor() {
		this.lineColorButton.setBackground(this.fillColorButton.getBackground().darker());
	}

	void fillColorChooserPanel_actionPerformed(ActionEvent e) {
		if (this.synchronizeCheckBox.isSelected()) {
			this.syncLineColor();
		}

	}

	protected void synchronizeCheckBox_actionPerformed(ActionEvent e) {
		if (this.synchronizeCheckBox.isSelected()) {
			this.syncLineColor();
		}

	}

	void lineColorChooserPanel_actionPerformed(ActionEvent e) {
		if (this.synchronizeCheckBox.isSelected()) {
			this.fillColorButton.setBackground(this.lineColorButton.getBackground().brighter());
		}

	}

}
