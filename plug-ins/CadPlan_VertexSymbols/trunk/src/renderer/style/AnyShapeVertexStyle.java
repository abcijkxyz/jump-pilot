package com.cadplan.vertices.renderer.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
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

public class AnyShapeVertexStyle extends ExternalSymbolsType {
	private int type = 0;
	private Double orientation = 0.0D;
	private boolean showLine = true;
	private boolean showFill = true;
	private boolean dotted = false;
	private Color lineColor;
	private Color fillColor;
	private float x0;
	private float y0;
	private String attributeName;
	private int attributeIndex;
	private double attributeValue;
	private boolean byValue;
	private boolean checkStyleIsActivated;
	private String classification;
	private Map<String, DataWrapper> classificationMap;

	//Distance of symbols along the line
	private   int distance;
	//Distance of a symbol to the line
	private    double offset;
	private   boolean rotate;

	public AnyShapeVertexStyle() {
		super(new java.awt.geom.Ellipse2D.Double());
		this.lineColor = Color.BLACK;
		this.fillColor = Color.WHITE;
		this.x0 = 0.0F;
		this.y0 = 0.0F;
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

	public void setType(int n) {
		this.type = n;
	}

	public int getType() {
		return this.type;
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
		if (layer != null) {
			BasicStyle style = layer.getBasicStyle();
			this.lineColor = GUIUtil.alphaColor(style.getLineColor(), style.getAlpha());
			this.fillColor = GUIUtil.alphaColor(style.getFillColor(), style.getAlpha());

			try {
				FeatureSchema featureSchema = layer.getFeatureCollectionWrapper().getFeatureSchema();
				if (this.attributeName != null && !this.attributeName.equals("")) {
					this.attributeIndex = featureSchema.getAttributeIndex(this.attributeName);
				}
			} catch (Exception var4) {
				this.attributeIndex = -1;
			}

			this.initializeText(layer);
		}
	}

	public void setColors(Color lineColor, Color fillColor) {
		this.lineColor = lineColor;
		this.fillColor = fillColor;
	}

	private GeneralPath buildShape() {
		GeneralPath path = new GeneralPath();
		float s = (float)(this.size / 2.0D);
		float s2 = s;
		this.x0 = (float)this.shape.getBounds2D().getX() + s;
		this.y0 = (float)this.shape.getBounds2D().getY() + s;
		int i;
		double angle;
		switch(this.type) {
		case 0:
			path.moveTo(this.x0 - s, this.y0);
			path.lineTo(this.x0 + s, this.y0);
			path.moveTo(this.x0, this.y0);
			path.lineTo(this.x0 - s / 2.0F, this.y0 - s / 2.0F);
			path.moveTo(this.x0, this.y0);
			path.lineTo(this.x0, this.y0 - s);
			path.moveTo(this.x0, this.y0);
			path.lineTo(this.x0 + s / 2.0F, this.y0 - s / 2.0F);
			break;
		case 1:
			path.moveTo(this.x0 - s, this.y0);
			path.lineTo(this.x0 + s, this.y0);
			path.moveTo(this.x0 + s / 2.0F, this.y0 + s / 2.0F);
			path.lineTo(this.x0 - s / 2.0F, this.y0 - s / 2.0F);
			path.moveTo(this.x0, this.y0 + s);
			path.lineTo(this.x0, this.y0 - s);
			path.moveTo(this.x0 - s / 2.0F, this.y0 + s / 2.0F);
			path.lineTo(this.x0 + s / 2.0F, this.y0 - s / 2.0F);
			break;
		case 2:
			path.moveTo(this.x0 - s, this.y0);
			path.lineTo(this.x0 + s, this.y0);
			path.moveTo(this.x0, this.y0 - s);
			path.lineTo(this.x0, this.y0 + s);
			path.moveTo(this.x0 + s / 2.0F, this.y0);

			for(i = 0; i <= 16; ++i) {
				angle = i * 2.0D * 3.141592653589793D / 16.0D;
				path.lineTo((float)(this.x0 + s / 2.0F * Math.cos(angle)), (float)(this.y0 - s / 2.0F * Math.sin(angle)));
			}

			return path;
		case 3:
			path.moveTo(this.x0 - s, this.y0);
			path.lineTo(this.x0 + s, this.y0);
			path.moveTo(this.x0, this.y0 - s);
			path.lineTo(this.x0, this.y0 + s);
			path.moveTo(this.x0 - s / 2.0F, this.y0 - s / 2.0F);
			path.lineTo(this.x0 + s / 2.0F, this.y0 - s / 2.0F);
			path.lineTo(this.x0 + s / 2.0F, this.y0 + s / 2.0F);
			path.lineTo(this.x0 - s / 2.0F, this.y0 + s / 2.0F);
			path.lineTo(this.x0 - s / 2.0F, this.y0 - s / 2.0F);
			break;
		case 4:
			path.moveTo(this.x0, this.y0);
			path.lineTo(this.x0, this.y0 - s / 2.0F);
			path.moveTo(this.x0 + s / 4.0F, this.y0 - 3.0F * s / 4.0F);

			for(i = 0; i <= 16; ++i) {
				angle = i * 2.0D * 3.141592653589793D / 16.0D;
				path.lineTo((float)(this.x0 + s2 / 4.0F * Math.cos(angle)), (float)(this.y0 - 3.0F * s2 / 4.0F - s2 / 4.0F * Math.sin(angle)));
			}

			return path;
		case 5:
			path.moveTo(this.x0, this.y0);
			path.lineTo(this.x0, this.y0 - s / 2.0F);
			path.lineTo(this.x0, this.y0 - s);
			path.lineTo(this.x0 + s / 2.0F, this.y0 - 3.0F * s / 4.0F);
			path.lineTo(this.x0, this.y0 - s / 2.0F);
			break;
		case 6:
			path.moveTo(this.x0, this.y0);
			path.lineTo(this.x0, this.y0 - s);
			path.moveTo(this.x0 - s / 2.0F, this.y0);
			path.lineTo(this.x0, this.y0 - s / 2.0F);
			path.lineTo(this.x0 + s / 2.0F, this.y0);
			path.lineTo(this.x0 - s / 2.0F, this.y0);
		}

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
		GeneralPath path = r.buildShape(2, this.shape, this.byValue, this.orientation, this.size, this.type);
		float width = 1.0F;
		BasicStroke stroke = new BasicStroke(width);
		g.setStroke(stroke);
		g.setColor(this.fillColor);
		AffineTransform currentTransform = g.getTransform();
		double angle = this.orientation;
		if (!this.byValue) {
			angle = this.attributeValue;
		}

		g.rotate(Math.toRadians(angle), r.x0, r.y0);
		r.paint(g, 2, path, this.showLine, this.lineColor, this.showFill, this.fillColor, this.dotted);
		g.setTransform(currentTransform);
		this.drawTextLabel(g, r.x0, r.y0);
	}
}
