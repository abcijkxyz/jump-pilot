package com.cadplan.vertices.renderer.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.util.Map;
import java.util.TreeMap;

import com.cadplan.jump.utils.DataWrapper;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.ui.GUIUtil;
import com.vividsolutions.jump.workbench.ui.Viewport;
import com.vividsolutions.jump.workbench.ui.renderer.style.BasicStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.ColorThemingStyle;

public class StarVertexStyle extends ExternalSymbolsType {
	private BasicStroke stroke;
	private int numSides = 4;
	private boolean showLine = true;
	private boolean showFill = true;
	private float width = 1.0F;
	private double orientation = 0.0D;
	private Color lineColor;
	private Color fillColor;
	private boolean dotted;
	private float x0;
	private float y0;
	private String attributeName;
	private int attributeIndex;
	private double attributeValue;
	private boolean byValue;
	private boolean checkStyleIsActivated;
	private String classification;
	private Map<String, DataWrapper> classificationMap;
	ColorThemingStyle CStyle;
	BasicStyle style;

	//Distance of symbols along the line
	private   int distance;
	//Distance of a symbol to the line
	private    double offset;
	private   boolean rotate;

	public StarVertexStyle() {
		super(new Double());
		this.lineColor = Color.BLACK;
		this.fillColor = Color.WHITE;
		this.attributeName = "";
		this.attributeIndex = -1;
		this.attributeValue = 0.0D;
		this.byValue = true;
		this.checkStyleIsActivated = true;
		this.classification = "";
		this.classificationMap = new TreeMap<String, DataWrapper>();
		((RectangularShape)this.shape).setFrame(0.0D, 0.0D, this.getSize(), this.getSize());
	}

	public void setcheckStyleIsActivated(boolean byValue) {
		this.checkStyleIsActivated = byValue;
	}

	public boolean getcheckStyleIsActivated() {
		return this.checkStyleIsActivated;
	}

	@Override
	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public int getSize() {
		return this.size;
	}

	public void setNumSides(int n) {
		this.numSides = n;
	}

	public int getNumSides() {
		return this.numSides;
	}

	public void setShowLine(boolean show) {
		this.showLine = show;
	}

	public boolean getShowLine() {
		return this.showLine;
	}

	public void setShowFill(boolean show) {
		this.showFill = show;
	}

	public boolean getShowFill() {
		return this.showFill;
	}

	public void setOrientation(double orientation) {
		this.orientation = orientation;
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
	@Override
	public void initialize(Layer layer) {
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

	public void setColors(Color lineColor, Color fillColor) {
		this.lineColor = lineColor;
		this.fillColor = fillColor;
	}

	private GeneralPath buildShape() {
		GeneralPath path = new GeneralPath();
		double baseAngle = this.orientation;
		if (!this.byValue) {
			baseAngle = 0.0D;
		}

		double s = this.size / 2.0D;
		int n = this.numSides;
		double angle = Math.toRadians(baseAngle) - 1.5707963267948966D + 3.141592653589793D / n;
		double halfAngle = 3.141592653589793D / n;
		this.x0 = (float)this.shape.getBounds2D().getX() + (float)s;
		this.y0 = (float)this.shape.getBounds2D().getY() + (float)s;

		float x;
		float y;
		float xm;
		float ym;
		for(int i = 0; i < n; ++i) {
			x = (float)(s * Math.cos(angle));
			y = (float)(s * Math.sin(angle));
			xm = (float)(s / 3.0D * Math.cos(angle - halfAngle));
			ym = (float)(s / 3.0D * Math.sin(angle - halfAngle));
			if (i == 0) {
				path.moveTo(this.x0 + x, this.y0 - y);
			} else {
				path.lineTo(this.x0 + xm, this.y0 - ym);
				path.lineTo(this.x0 + x, this.y0 - y);
			}

			angle += 6.283185307179586D / n;
		}

		angle = Math.toRadians(baseAngle) - 1.5707963267948966D + 3.141592653589793D / n;
		xm = (float)(s / 3.0D * Math.cos(angle - halfAngle));
		ym = (float)(s / 3.0D * Math.sin(angle - halfAngle));
		x = (float)(s * Math.cos(angle));
		y = (float)(s * Math.sin(angle));
		path.lineTo(this.x0 + xm, this.y0 - ym);
		path.lineTo(this.x0 + x, this.y0 - y);
		return path;
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
	public void render(Graphics2D g) {
		ExternalSymbolsRenderer r = new ExternalSymbolsRenderer();
		GeneralPath path = r.buildShape(1, this.shape, this.byValue, this.orientation, this.size, this.numSides);
		float width = 1.0F;
		this.stroke = new BasicStroke(width);
		g.setStroke(this.stroke);
		g.setColor(this.fillColor);
		AffineTransform currentTransform = g.getTransform();
		double angle = 0.0D;
		if (!this.byValue) {
			angle = this.attributeValue;
		}

		g.rotate(Math.toRadians(angle), r.x0, r.y0);
		r.paint(g, 1, path, this.showLine, this.lineColor, this.showFill, this.fillColor, this.dotted);
		g.setTransform(currentTransform);
		this.drawTextLabel(g, r.x0, r.y0);
	}
}
