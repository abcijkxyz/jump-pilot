package com.cadplan.jump.ui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import com.cadplan.vertices.renderer.style.ImageVertexStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.VertexStyle;

public class ImageVertexPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	VertexStyle symbol = new ImageVertexStyle();

	public ImageVertexPanel(String imageName)
	{
		symbol = new ImageVertexStyle();
		((ImageVertexStyle)symbol).setName(imageName);
		double scale = ((ImageVertexStyle)symbol).getScale();
		int size = ((ImageVertexStyle)symbol).image.getWidth(null);

		((ImageVertexStyle)symbol).setSize((int)(size/scale));




	}

	@Override
	public void paint(Graphics g) {
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		((ImageVertexStyle)this.symbol).render((Graphics2D)g);
	}
}
