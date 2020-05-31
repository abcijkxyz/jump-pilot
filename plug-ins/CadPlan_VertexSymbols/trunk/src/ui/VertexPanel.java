package com.cadplan.jump.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import com.cadplan.jump.utils.VertexParams;
import com.cadplan.vertices.renderer.style.AnyShapeVertexStyle;
import com.cadplan.vertices.renderer.style.PolygonVertexStyle;
import com.cadplan.vertices.renderer.style.StarVertexStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.VertexStyle;

public class VertexPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private int sides;
	private int type;
	Color lineColor, fillColor;
	VertexStyle symbol;

	public VertexPanel(int sides, int type, Color lineColor, Color fillColor) {
		this.sides = sides;
		this.type = type;
		this.setPreferredSize(new Dimension(30, 30));
		this.lineColor = lineColor;
		this.fillColor = fillColor;
		this.init();
	}

	public void init() {
		if (this.type == VertexParams.POLYGON) {
			this.symbol = new PolygonVertexStyle();
			((PolygonVertexStyle)this.symbol).setNumSides(this.sides);
			((PolygonVertexStyle)this.symbol).setColors(lineColor, fillColor);
		} else if (this.type == VertexParams.STAR) {
			this.symbol = new StarVertexStyle();
			((StarVertexStyle)this.symbol).setNumSides(this.sides);
			((StarVertexStyle)this.symbol).setColors(lineColor, fillColor);
		} else {
			this.symbol = new AnyShapeVertexStyle();
			((AnyShapeVertexStyle)this.symbol).setType(this.sides);
			((AnyShapeVertexStyle)this.symbol).setColors(lineColor, fillColor);
		}

		this.symbol.setSize(20);
	}

	@Override
	public void paint(Graphics g) {
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (this.type == VertexParams.POLYGON) {
			((PolygonVertexStyle)this.symbol).render((Graphics2D)g);

		} else if (this.type == VertexParams.STAR) {
			((StarVertexStyle)this.symbol).render((Graphics2D)g);

		} else {
			((AnyShapeVertexStyle)this.symbol).render((Graphics2D)g);

		}

	}
}
