package com.cadplan.vertices.renderer.style;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.geom.RectangularShape;
import java.awt.image.ImageObserver;
import java.util.Map;
import java.util.TreeMap;

import com.cadplan.jump.utils.DataWrapper;
import com.cadplan.jump.utils.VertexParams;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.ui.Viewport;

public class ImageVertexStyle extends ExternalSymbolsType {
	public Image image;
	private String name;
	private int width;
	private int height;
	private double scale = 1.0D;
	private double orientation = 0.0D;
	private String attributeName = "";
	private int attributeIndex = -1;
	private double attributeValue = 0.0D;
	private boolean byValue = true;
	//	private Color lineColor;
	//	private Color fillColor;
	private boolean checkStyleIsActivated;
	private String classification;
	private Map<String, DataWrapper> classificationMap;

	//Distance of symbols along the line
	private   int distance;
	//Distance of a symbol to the line
	private    double offset;
	private   boolean rotate;

	public ImageVertexStyle() {
		super(new Double());
		//	this.lineColor = Color.BLACK;
		//	this.fillColor = Color.WHITE;
		this.checkStyleIsActivated = true;
		this.classification = "";
		this.classificationMap = new TreeMap();
	}

	public void setcheckStyleIsActivated(boolean byValue) {
		this.checkStyleIsActivated = byValue;
	}

	public boolean getcheckStyleIsActivated() {
		return this.checkStyleIsActivated;
	}

	/*	public void setColors(Color lineColor, Color fillColor) {
		this.lineColor = lineColor;
		this.fillColor = fillColor;
	}*/

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
		return scale;
	}

	@Override
	public int getSize() {
		return size;
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

	public void setName(String name) {
		this.name = name;
		this.image = null;
		boolean found = false;

		for(int i = 0; i < VertexParams.images.length; ++i) {
			if (VertexParams.imageNames[i].equals(name)) {
				found = true;
				this.image = VertexParams.images[i];
				if (this.image != null) {
					this.width = this.image.getWidth((ImageObserver)null);
					this.height = this.image.getHeight((ImageObserver)null);
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
			}
		}

		if (!found) {
			this.image = null;
		}

	}

	public String getName() {
		return this.name;
	}

	@Override
	public void initialize(Layer layer) {
		//	BasicStyle style = layer.getBasicStyle();
		//	this.lineColor = GUIUtil.alphaColor(style.getLineColor(), style.getAlpha());
		//	this.fillColor = GUIUtil.alphaColor(style.getFillColor(), style.getAlpha());
		if (layer != null) {
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
		Image image = r.getImage(ExternalSymbolsRenderer.IMAGES, this.name, this.size, this.shape);
		int x = (int)this.shape.getBounds2D().getX();
		int y = (int)this.shape.getBounds2D().getY();
		AffineTransform currentTransform = g.getTransform();
		double angle = this.orientation;
		if (!this.byValue) {
			angle = this.attributeValue;
		}

		r.x0 = (float)(x + r.width / r.scale / 2.0D);
		r.y0 = (float)(y + r.height / r.scale / 2.0D);
		//	int sizex = this.size;
		//	int sizey = this.size * this.height / this.width;
		g.rotate(Math.toRadians(angle), r.x0, r.y0);
		r.paint(g, ExternalSymbolsRenderer.IMAGES, image);
		g.setTransform(currentTransform);
		this.drawTextLabel(g, r.x0, r.y0);
	}
}
