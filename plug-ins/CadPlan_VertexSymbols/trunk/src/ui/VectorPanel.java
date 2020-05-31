package com.cadplan.jump.ui;

import java.awt.Color;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import com.cadplan.designer.GridBagDesigner;

public class VectorPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	public SymbolPanel symbolPanel;
	ButtonGroup group;
	Color lineColor, fillColor;

	public VectorPanel(ButtonGroup group,  Color lineColor, Color fillColor) {
		this.group = group;
		this.lineColor = lineColor;
		this.fillColor = fillColor;
		this.init();
	}

	public void init() {
		GridBagDesigner gb = new GridBagDesigner(this);
		this.symbolPanel = new SymbolPanel(this.group, lineColor, fillColor);
		this.symbolPanel.setBackground(Color.WHITE);
		gb.setPosition(0, 0);
		gb.setInsets(10, 10, 10, 10);
		gb.setWeight(1.0D, 1.0D);
		gb.setAnchor(17);
		gb.setFill(1);
		gb.setSpan(3, 1);
		gb.addComponent(this.symbolPanel);
	}
}
