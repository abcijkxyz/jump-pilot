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

import com.cadplan.jump.utils.VertexParams;
import com.cadplan.vertices.renderer.style.ExternalSymbolsImplType;
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
import com.vividsolutions.jump.workbench.ui.renderer.style.BasicStyle;

public class PreviewPanel extends JPanel {
	private ExternalSymbolsImplType style;
	private  BasicStyle bstyle;
	private double offset =0.0D;
	private Color color;
	private static final long serialVersionUID = 1L;
	private final LayerViewPanel dummyLayerViewPanel;
	private final Viewport viewport;

	public PreviewPanel(ExternalSymbolsImplType style, BasicStyle bstyle, double offset) {
		this.setBackground(new Color(0, 0, 0, 0));
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setMaximumSize(new Dimension(100, 50));
		this.setMinimumSize(new Dimension(100, 50));
		this.setPreferredSize(new Dimension(100, 50));
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
				return new Envelope(0.0D, 100.0D, 0.0D, 50.0D);
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
		this.style = style;
		this.offset = offset;
		this.bstyle=bstyle;
	}
	public void removeSymbol() {
		this.getGraphics().setColor(Color.WHITE);
		this.revalidate();
		this.repaint();
	}

	public void setSymbol(ExternalSymbolsImplType style) {
		this.style = style;
		if(VertexParams.lineDecoration) {
			if (this.style.getRotate()) {
				this.style.setRotate(style.getRotate());
			}
		}
	}
	public void setOffset (double offset) {
		this.offset=offset;
	}



	public Viewport getViewport() {
		return this.viewport;
	}

	private void paintBaseLine(BasicStyle style, Graphics2D g) {
		final Stroke originalStroke = style.getLineStroke();

		try {
			style.paint(lineFeature(), g, viewport);
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			g.setStroke(originalStroke);
		}
	}






	private void paintVertexDecoration(ExternalSymbolsImplType style, Graphics2D g) {
		Stroke originalStroke = g.getStroke();
		try {
			style.setSize(30);
			style.paint(this.pointFeature(50,25), g, this.viewport); 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			g.setStroke(originalStroke);
			g.setColor(this.color);
		}

	}

	private void paintLineDecoration(ExternalSymbolsImplType style, Graphics2D g) {
		Stroke originalStroke = g.getStroke();
		try {
			style.setSize(18);
			double y =25-this.offset;
			style.paint(this.pointFeature(25, y), g, this.viewport); 
			style.paint(this.pointFeature(75,y), g, this.viewport);
			//style.paint(this.pointFeatureCoord(25, this.offset), g, this.viewport); 
			//style.paint(this.pointFeatureCoord(75,this.offset), g, this.viewport);
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
		g.setColor(this.color);
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(VertexParams.lineDecoration) {
			paintLineDecoration(this.style, (Graphics2D)g);
			paintBaseLine(this.bstyle, (Graphics2D)g);

		} else {
			paintVertexDecoration(this.style, (Graphics2D)g);
		}

	}





	private Feature pointFeature(double x, double y) {
		try {
			Feature feat = FeatureUtil.toFeature((new WKTReader()).read("POINT ("+x+" " +y+")"), new FeatureSchema() {
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

	private Feature lineFeature() {
		try {
			Feature feat;

			feat = FeatureUtil.toFeature(
					new WKTReader().read("LINESTRING (0 25, 100 25)"),
					new FeatureSchema() {
						private static final long serialVersionUID = -8627306219650589202L;
						{
							addAttribute("GEOMETRY", AttributeType.GEOMETRY);
						}
					});

			return feat;
		} catch (final ParseException e) {
			Assert.shouldNeverReachHere();
			return null;
		}
	}

}
