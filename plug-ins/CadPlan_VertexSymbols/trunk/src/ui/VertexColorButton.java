package com.cadplan.jump.ui;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JColorChooser;

import com.vividsolutions.jump.I18N;

public class VertexColorButton extends Button implements ActionListener {
	private static final long serialVersionUID = 1L;
	Color color;

	public VertexColorButton(Color color) {
		super("      ");
		this.color = color;
		this.setBackground(color);
		this.setPreferredSize(new Dimension(60, 20));
		this.setMaximumSize(new Dimension(60, 20));
		this.setMinimumSize(new Dimension(60, 20));
		this.addActionListener(this);
	}

	public Color getColor() {
		return color;
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		Color newColor = JColorChooser.showDialog(this, I18N.get("org.openjump.core.ui.plugin.raster.color.RasterColorEditorPlugIn.Select-color"), this.color);
		if (newColor != null) {
			color = newColor;
			setBackground(this.color);
		}

	}

	public void setColor(Color color) {
		this.color = color;
	}
}
