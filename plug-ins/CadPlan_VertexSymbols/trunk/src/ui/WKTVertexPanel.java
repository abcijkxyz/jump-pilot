package com.cadplan.jump.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.cadplan.vertices.renderer.style.WKTVertexStyle;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.util.Assert;
import com.vividsolutions.jump.feature.AttributeType;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.feature.FeatureUtil;
import com.vividsolutions.jump.workbench.model.LayerManager;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;
import com.vividsolutions.jump.workbench.ui.LayerViewPanelContext;
import com.vividsolutions.jump.workbench.ui.Viewport;
import com.vividsolutions.jump.workbench.ui.renderer.style.VertexStyle;

public class WKTVertexPanel extends JPanel {
	VertexStyle symbol;
	private Color color;
	private static final long serialVersionUID = 1L;
	private final LayerViewPanel dummyLayerViewPanel;
	private final Viewport viewport;

	public WKTVertexPanel(String imageName, Color lineColor, Color fillColor) {
		this.setBackground(new Color(0, 0, 0, 0));
		this.setBorder(BorderFactory.createEmptyBorder());
		/*	this.setMaximumSize(new Dimension(80, 50));
		this.setMinimumSize(new Dimension(80, 50));
		this.setPreferredSize(new Dimension(80, 50));*/
		this.setPreferredSize(new Dimension(40, 40));
		this.setMaximumSize(new Dimension(40, 40));
		this.dummyLayerViewPanel = new LayerViewPanel(new LayerManager(), new LayerViewPanelContext() {
			@Override
			public void setStatusMessage(String message) {
			}

			@Override
			public void warnUser(String warning) {
			}

			@Override
			public void handleThrowable(Throwable t) {
			}
		});
		this.viewport = new Viewport(this.dummyLayerViewPanel) {
			private final AffineTransform transform = new AffineTransform();

			@Override
			public Envelope getEnvelopeInModelCoordinates() {
				return new Envelope(0.0D, 80.0D, 0.0D, 50.0D);
			}

			@Override
			public AffineTransform getModelToViewTransform() {
				return this.transform;
			}

			@Override
			public Point2D toViewPoint(Coordinate modelCoordinate) {
				return new Double(modelCoordinate.x, modelCoordinate.y);
			}
		};
		this.symbol = new WKTVertexStyle();
		((WKTVertexStyle)this.symbol).setName(imageName);
		((WKTVertexStyle)this.symbol).setColors(lineColor, fillColor);
	}

	public void removeSymbol() {
		this.getGraphics().setColor(Color.WHITE);
		this.revalidate();
		this.repaint();
	}

	public void setSymbol(VertexStyle symbol) {
		this.symbol = symbol;
	}

	public Viewport getViewport() {
		return this.viewport;
	}

	private void paint(VertexStyle symbol, Graphics2D g) {
		Stroke originalStroke = g.getStroke();
		//	this.setPreferredSize(new Dimension(80, 50));
		//	this.setMaximumSize(new Dimension(80, 50));
		this.setPreferredSize(new Dimension(40, 40));
		this.setMaximumSize(new Dimension(40, 40));
		try {
			((WKTVertexStyle)symbol).paint(this.pointFeature(), g, this.viewport);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			g.setStroke(originalStroke);
			g.setColor(this.color);
		}

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		((WKTVertexStyle)this.symbol).setSize(18);
		g.setColor(this.color);
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		this.paint((WKTVertexStyle)this.symbol, (Graphics2D)g);
	}

	private Feature pointFeature() {
		try {
			//Feature feat = FeatureUtil.toFeature((new WKTReader()).read("POINT (40 25)"), new FeatureSchema() {
			Feature feat = FeatureUtil.toFeature((new WKTReader()).read("POINT (20 20)"), new FeatureSchema() {
				private static final long serialVersionUID = -8627306219650589202L;

				{
					this.addAttribute("GEOMETRY", AttributeType.GEOMETRY);
				}
			});
			return feat;
		} catch (ParseException var2) {
			Assert.shouldNeverReachHere();
			return null;
		}
	}
}
