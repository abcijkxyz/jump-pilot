package com.cadplan.vertices.renderer.style;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.geom.RectangularShape;
import java.util.Map;
import java.util.TreeMap;

import com.cadplan.jump.ui.WKTshape;
import com.cadplan.jump.utils.DataWrapper;
import com.cadplan.jump.utils.VertexParams;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.ui.GUIUtil;
import com.vividsolutions.jump.workbench.ui.Viewport;
import com.vividsolutions.jump.workbench.ui.renderer.style.BasicStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.ColorThemingStyle;

public class WKTVertexStyle extends ExternalSymbolsType {
	public WKTshape wktShape;
	private String name;
	private int width;
	private int height;
	private double scale = 1.0D;
	private double orientation = 0.0D;
	private String attributeName = "";
	private int attributeIndex = -1;
	private double attributeValue = 0.0D;
	private boolean byValue = true;
	private boolean showLine = true;
	private boolean showFill = true;
	private Color lineColor;
	private Color fillColor;
	private boolean dotted;
	private double baseScale;
	private String classification;
	private Map<String, DataWrapper> classificationMap;
	ColorThemingStyle CStyle;
	BasicStyle style;

	//Distance of symbols along the line
	private   int distance;
	//Distance of a symbol to the line
	private    double offset;
	private   boolean rotate;

	public WKTVertexStyle() {
		super(new Double());
		this.lineColor = Color.BLACK;
		this.fillColor = Color.WHITE;
		this.baseScale = 1.0D;
		this.classification = "";
		this.classificationMap = new TreeMap<String, DataWrapper>();
	}

	private void setBaseScale(double baseScale) {
		this.baseScale = baseScale;
	}

	private double getbaseScale() {
		return this.baseScale;
	}

	public void setShowFill(boolean showFill) {
		this.showFill = showFill;
	}

	public boolean getShowFill() {
		return this.showFill;
	}

	public void setShowLine(boolean showLine) {
		this.showLine = showLine;
	}

	public boolean getShowLine() {
		return this.showLine;
	}

	public void setColors(Color lineColor, Color fillColor) {
		this.lineColor = lineColor;
		this.fillColor = fillColor;
	}

	public void setSize(int width, int height) {
		this.size = Math.max(width, height);
		this.width = width;
		this.height = height;
		((RectangularShape)this.shape).setFrame(0.0D, 0.0D, width, height);
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public double getScale() {
		return this.scale;
	}

	@Override
	public int getSize() {
		return this.size;
	}

	@Override
	public void setSize(int size) {
		this.size = size;
	}

	public void setOrientation(double angle) {
		this.orientation = angle;
	}

	public double getOrientation() {
		return this.orientation;
	}

	public void setDotted(boolean dotted) {
		this.dotted = dotted;
	}

	public boolean getDotted() {
		return this.dotted;
	}

	public void setByValue(boolean byValue) {
		this.byValue = byValue;
	}

	public boolean getByValue() {
		return this.byValue;
	}

	public void setAttributeName(String attName) {
		this.attributeName = attName;
	}

	public String getAttributeName() {
		return this.attributeName;
	}

	@Override
	public String getClassification() {
		return this.classification;
	}

	@Override
	public void setClassification(String classification) {
		this.classification = classification;
		VertexParams.classification = classification;
	}

	@Override
	public Map<String, DataWrapper> getClassificationMap() {
		return this.classificationMap;
	}

	@Override
	public void setClassificationMap(Map<String, DataWrapper> classificationMap) {
		this.classificationMap = classificationMap;
	}


	@Override
	public double getOffset() {
		return this.offset;
	}


	@Override
	public void setOffset(double offset) {
		//this.offset = LineParams.offset ;
		this.offset = offset;
		//VertexParams.offset = offset;
	}


	@Override
	public boolean getRotate() {
		return this.rotate;
	}


	@Override
	public void setRotate(boolean rotate) {
		this.rotate = rotate;
		//VertexParams.rotate = rotate;
	}


	@Override
	public int getDistance() {
		return this.distance;
	}


	@Override
	public void setDistance(int distance) {
		this.distance=distance;
		//VertexParams.distance =distance;
		//Params.distance = distance;
	}

	public void setName(String name) {
		this.name = name;
		this.wktShape = null;
		boolean found = false;

		for(int i = 0; i < VertexParams.wktShapes.length; ++i) {
			if (VertexParams.wktNames[i].equals(name)) {
				found = true;
				this.wktShape = VertexParams.wktShapes[i];
				if (this.wktShape != null) {
					this.width = this.wktShape.extent;
					this.height = this.wktShape.extent;
					int k = name.lastIndexOf("_x");
					int j = name.lastIndexOf(".");
					this.scale = 1.0D;
					if (k > 0) {
						String scaleS = name.substring(k + 2, j);
						int n = scaleS.lastIndexOf("x");
						if (n > 0) {
							scaleS = scaleS.substring(0, n);
						}

						try {
							this.scale = Integer.parseInt(scaleS);
						} catch (NumberFormatException var9) {
							this.scale = 1.0D;
						}
					}
				}

				this.size = (int)(this.size / this.scale);
			}
		}

		if (!found) {
			this.wktShape = null;
		}

	}

	public String getName() {
		return this.name;
	}

	@Override
	public void initialize(Layer layer) {
		if (layer != null) {
			this.CStyle = ColorThemingStyle.get(layer);
			this.style = layer.getBasicStyle();

			try {
				this.lineColor = GUIUtil.alphaColor(this.style.getLineColor(), this.style.getAlpha());
				this.fillColor = GUIUtil.alphaColor(this.style.getFillColor(), this.style.getAlpha());
			} catch (Exception var4) {
				this.lineColor = GUIUtil.alphaColor(layer.getBasicStyle().getLineColor(), layer.getBasicStyle().getAlpha());
				this.fillColor = GUIUtil.alphaColor(layer.getBasicStyle().getFillColor(), layer.getBasicStyle().getAlpha());
			}

			try {
				FeatureSchema featureSchema = layer.getFeatureCollectionWrapper().getFeatureSchema();
				if (this.attributeName != null && !this.attributeName.equals("")) {
					this.attributeIndex = featureSchema.getAttributeIndex(this.attributeName);
				}
			} catch (Exception var3) {
				this.attributeIndex = -1;
			}

			this.initializeText(layer);
		}
	}

	@Override
	public void paint(Feature feature, Graphics2D g2, Viewport viewport) {
		this.viewport = viewport;
		if (!this.byValue && this.attributeIndex >= 0) {
			try {
				this.attributeValue = feature.getDouble(this.attributeIndex);
			} catch (Exception var8) {
				try {
					this.attributeValue = feature.getInteger(this.attributeIndex);
				} catch (Exception var7) {
					this.attributeValue = 0.0D;
				}
			}
		}

		this.setTextAttributeValue(feature);

		try {
			super.paint(feature, g2, viewport);
		} catch (Exception var6) {
			System.out.println("Painting ERROR:" + var6);
			var6.printStackTrace();
		}

	}

	public void paint(Feature feature, Graphics2D g, Point2D p) {
		if (!this.byValue && this.attributeIndex >= 0) {
			try {
				this.attributeValue = feature.getDouble(this.attributeIndex);
			} catch (Exception var7) {
				try {
					this.attributeValue = feature.getInteger(this.attributeIndex);
				} catch (Exception var6) {
					this.attributeValue = 0.0D;
				}
			}
		}

		this.setTextAttributeValue(feature);
		this.paint(g, p);
	}

	@Override
	public void paint(Graphics2D g, Point2D p) {
		this.setFrame(p);
		this.render(g);
	}

	private void setFrame(Point2D p) {
		((RectangularShape)this.shape).setFrame(p.getX() - this.width / (2.0D * this.scale), p.getY() - this.height / (2.0D * this.scale), this.width / this.scale, this.height / this.scale);
	}

	@Override
	public void render(Graphics2D g) {
		ExternalSymbolsRenderer r = new ExternalSymbolsRenderer();
		int x = (int)this.shape.getBounds2D().getX();
		int y = (int)this.shape.getBounds2D().getY();
		int shapeSize = this.size;
		WKTshape wktShape = r.getWKTShape(3, this.name, shapeSize, this.shape);
		AffineTransform currentTransform = g.getTransform();
		double angle = this.orientation;
		if (!this.byValue) {
			angle = this.attributeValue;
		}

		double x0 = x + r.width / r.scale / 2.0D;
		double y0 = y + r.height / r.scale / 2.0D;
		int sizex = this.size;
		int sizey = this.size * this.height / this.width;
		g.translate(r.x0, r.y0);
		g.rotate(Math.toRadians(angle), 0.0D, 0.0D);
		r.paint(g, 3, wktShape, r.size, this.showLine, this.lineColor, this.showFill, this.fillColor, this.dotted);
		g.setTransform(currentTransform);
		this.drawTextLabel(g, r.x0, r.y0);
	}
}
