package com.cadplan.vertices.renderer.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D.Double;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.util.Map;
import java.util.TreeMap;

import com.cadplan.jump.ui.WKTshape;
import com.cadplan.jump.utils.DataWrapper;
import com.cadplan.jump.utils.StyleUtils;
import com.cadplan.jump.utils.VertexParams;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.ui.GUIUtil;
import com.vividsolutions.jump.workbench.ui.Viewport;
import com.vividsolutions.jump.workbench.ui.renderer.style.BasicStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.ColorThemingStyle;

public class ExternalSymbolsImplType extends ExternalSymbolsType {
	boolean debug = false;
	private BasicStroke stroke;
	private int numSides = 4;
	private boolean showLine = true;
	private boolean showFill = true;
	private double orientation = 0.0D;
	private Color lineColor;
	private Color fillColor;
	private boolean dotted;
	private String attributeName;
	private int attributeIndex;
	private double attributeValue;
	private boolean byValue;
	private String symbolName;
	private int symbolType;
	private int symbolNumber;
	private String defaultSymbolName;
	private int defaultSymbolType;
	private int defaultSymbolNumber;
	private Layer selectedLayer;
	private String classification;
	private Map<String, DataWrapper> classificationMap;
	private DataWrapper classificationWrapper;

	//Distance of symbols along the line
	private   int distance;
	//Distance of a symbol to the line
	private    double offset;
	//symbol is rotated along the line
	private   boolean rotate;

	//activate line decoration
	private boolean lineDecoration;

	String featValue;
	ColorThemingStyle CStyle;
	BasicStyle style;
	Integer sizeClass;

	@Override
	public DataWrapper getDataWrapper() {
		return this.classificationWrapper;
	}

	@Override
	public void setDataWrapper(DataWrapper classificationWrapper) {
		this.classificationWrapper = classificationWrapper;
	}

	public ExternalSymbolsImplType() {
		super(new Double());
		this.lineColor = Color.BLACK;
		this.fillColor = Color.WHITE;
		this.attributeName = "";
		this.attributeIndex = -1;
		this.attributeValue = 0.0D;
		this.byValue = true;
		this.classification = "";
		this.classificationMap = new TreeMap<String, DataWrapper>();
		((RectangularShape)this.shape).setFrame(0.0D, 0.0D, this.getSize(), this.getSize());
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
	public Layer getLayer() {
		return this.selectedLayer;
	}

	@Override
	public void setLayer(Layer selectedLayer) {
		this.selectedLayer = selectedLayer;
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

	public String getSymbolName() {
		return this.defaultSymbolName;
	}

	public String getActualSymbolName() {
		return this.symbolName;
	}

	@Override
	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	@Override
	public Color getLineColor() {
		return this.lineColor;
	}

	@Override
	public double getOffset() {
		return this.offset;
	}

	@Override
	public void setOffset(double offset) {
		this.offset = offset;
	}

	@Override
	public boolean getRotate() {
		return this.rotate;
	}

	@Override
	public void setRotate(boolean rotate) {
		this.rotate = rotate;
	}

	@Override
	public int getDistance() {
		return this.distance;
	}

	@Override
	public void setDistance(int distance) {
		this.distance=distance;
	}
	@Override
	public boolean getLineDecoration() {
		return this.lineDecoration;
	}

	@Override
	public void setLineDecoration(boolean lineDecoration) {
		this.lineDecoration = lineDecoration;
		VertexParams.lineDecoration = lineDecoration;
	}

	public void setSymbolName(String name) {
		boolean debug = false;
		if (debug) {
			System.out.println("setSymbolName - initial call: " + name);
		}
		this.symbolName = name;
		this.defaultSymbolName = name;
		if (name != null) {
			this.setSelectedSymbolName(name);
			if (debug) {
				System.out.println("setSymbolName: " + this.symbolName + "  type:" + this.symbolType + "  symbolNumber" + this.symbolNumber + "  numSides=" + this.numSides);
			}
			VertexParams.symbolName = this.defaultSymbolName;
			VertexParams.symbolType = this.symbolType;
			VertexParams.symbolNumber = this.symbolNumber;
			this.defaultSymbolType = this.symbolType;
			this.defaultSymbolNumber = this.symbolNumber;
		}
	}

	private int getNumber(String s) {
		int n;
		try {
			n = Integer.parseInt(s);
		} catch (NumberFormatException var4) {
			n = -1;
		}
		return n;
	}

	public int getSymbolNumber() {
		return this.symbolNumber;
	}

	public void setSymbolType(int type) {
		this.symbolType = type;
	}

	public int getSymbolType() {
		return this.defaultSymbolType;
	}

	public void setSelectedSymbolSize(Integer dimension) {
		if (this.classification != null & !this.classificationMap.isEmpty()) {
			dimension = this.classificationWrapper.getdimension();
		} else {
			dimension = this.size;
		}
	}

	public void setSelectedSymbolName(String name) {
		int number = 0;
		boolean debug = false;
		if (debug) {
			System.out.println("Setting symbol Name for:" + name);
		}
		byte type;
		if (name.startsWith("@poly")) {
			type = 0;
			number = this.getNumber(name.substring(5));
		} else if (name.startsWith("@star")) {
			type = 1;
			number = this.getNumber(name.substring(5));
		} else if (name.startsWith("@any")) {
			type = 2;
			number = this.getNumber(name.substring(4));
		} else if (name.toLowerCase().endsWith(".wkt")) {
			type = 3;
			number = -1;
		} else {
			type = 4;
			number = -1;
		}
		this.symbolName = name;
		this.symbolType = type;
		this.symbolNumber = number;
		if (debug) {
			System.out.println("SymbolName = " + name + "  SymbolType = " + this.symbolType + "  symbolNumber=" + this.symbolNumber);
		}
	}

	public void setSelectedSymbolName(Feature feature) {
		String name = null;
		this.featValue = "@poly4";
		if (this.classification != null & !this.classificationMap.isEmpty()) {
			try {
				this.featValue = feature.getAttribute(this.classification).toString();
				if (this.classificationMap.containsKey(this.featValue)) {
					this.classificationWrapper = this.classificationMap.get(this.featValue);
					name = this.classificationWrapper.getSimbol();
					if (this.CStyle.isEnabled()) {
						Map<Object, BasicStyle> attributeValueToBasicStyleMap = this.CStyle.getAttributeValueToBasicStyleMap();
						BasicStyle style = attributeValueToBasicStyleMap.get(this.featValue);
						this.lineColor = GUIUtil.alphaColor(style.getLineColor(), style.getAlpha());
						this.fillColor = GUIUtil.alphaColor(style.getFillColor(), style.getAlpha());
						this.size = this.classificationWrapper.getdimension();
						this.distance= this.classificationWrapper.getDistance();
						this.offset=this.classificationWrapper.getOffset();
						this.rotate=this.classificationWrapper.getRotate();
					}
				} else {
					name = VertexParams.symbolName;
					BasicStyle bstyle =VertexParams.selectedLayer.getBasicStyle();
					this.lineColor = GUIUtil.alphaColor(bstyle.getLineColor(), bstyle.getAlpha());
					this.fillColor = GUIUtil.alphaColor(bstyle.getFillColor(), bstyle.getAlpha());
					this.size = VertexParams.size;
					this.distance=VertexParams.distance;
					this.offset=VertexParams.offset;
					this.rotate=VertexParams.rotate;
				}
			} catch (Exception var5) {
				BasicStyle bstyle =VertexParams.selectedLayer.getBasicStyle();
				this.lineColor = GUIUtil.alphaColor(bstyle.getLineColor(), bstyle.getAlpha());
				this.fillColor = GUIUtil.alphaColor(bstyle.getFillColor(), bstyle.getAlpha());
				name = VertexParams.symbolName;
				this.size = VertexParams.size;
				this.distance=VertexParams.distance;
				this.offset=VertexParams.offset;
				this.rotate=VertexParams.rotate;
			}
		} else {
			try {
				name = (String)feature.getAttribute("SymbolName");
				if (name == null || name.equals("") || name.equals("0")) {
					name = null;
				}
			} catch (IllegalArgumentException var6) {
				System.out.println("SymbolName.... not found");
				System.out.println("Exception   using default symbol :" + this.symbolName + "  number=" + this.symbolNumber + "  type=" + this.symbolType);
			} catch (ClassCastException var7) {
				name = null;
			}
		}
		if (name != null) {
			this.setSelectedSymbolName(name);
		} else {
			this.symbolName = this.defaultSymbolName;
			this.symbolNumber = this.defaultSymbolNumber;
			this.symbolType = this.defaultSymbolType;
		}
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

	public void setColors(Color lineColor, Color fillColor) {
		this.lineColor = lineColor;
		this.fillColor = fillColor;
	}


	public void paintVertex(Feature f, Graphics2D graphics, Viewport viewport) throws Exception {
		this.viewport = viewport;
		if (!this.byValue && this.attributeIndex >= 0) {
			try {
				this.attributeValue = f.getDouble(this.attributeIndex);
			} catch (Exception var8) {
				try {
					this.attributeValue = f.getInteger(this.attributeIndex);
				} catch (Exception var7) {
					this.attributeValue = 0.0D;
				}
			}
		}
		this.setTextAttributeValue(f);
		this.setSelectedSymbolName(f);
	}



	@Override
	public void paint(Feature f, Graphics2D graphics, Viewport viewport) throws Exception {
		paintVertex(f, graphics,viewport);
		Geometry geometry = f.getGeometry();

		if (this.lineDecoration) {
			if (geometry instanceof Point) {
				try {
					super.paint(f, graphics, viewport);
				} catch (Exception e) {
					System.out.println("Painting ERROR:" + e);
					e.printStackTrace();
				}
			}
			if (geometry instanceof MultiPoint) {
				MultiPoint multipoint = (MultiPoint) f.getGeometry();
				for (int i = 0; i < multipoint.getNumGeometries(); i++) {
					try {
						super.paint(f, graphics, viewport);
					} catch (Exception e) {
						System.out.println("Painting ERROR:" + e);
						e.printStackTrace();
					}
				}
			}
			if (geometry instanceof LineString) {
				LineString lineString  = (LineString) f.getGeometry();
				if (lineString.getNumPoints() < 2) {
					return;
				} else {
					paintLineString(lineString,  viewport,graphics);	
				}
			} if (geometry instanceof MultiLineString) {
				MultiLineString multiLine = (MultiLineString) f.getGeometry();
				for (int i = 0; i < multiLine.getNumGeometries(); i++) {
					LineString line = (LineString) multiLine.getGeometryN(i);
					if (line.getNumPoints() < 2) {
						return;
					} else {
						paintLineString(line,  viewport,graphics);	
					}
				}
			} if (geometry instanceof Polygon) {
				Polygon polygon = (Polygon) f.getGeometry();
				paintLineString(polygon.getExteriorRing(),  viewport,graphics);
			}else {
				return;
			}
		} else   {
			try {
				super.paint(f, graphics, viewport);
			} catch (Exception e) {
				System.out.println("Painting ERROR:" + e);
				e.printStackTrace();
			}
		}
	}

	double angle2;
	GeometryFactory geomFac = new GeometryFactory();

	protected void paintLineString(LineString lineString,
			Viewport viewport, Graphics2D graphics) throws Exception{
		Coordinate mid = null, previous = null;

		final double scale = viewport.getScale();
		final CoordinateList coord = new CoordinateList(lineString.getCoordinates());
		for (int i = 0; i < coord.size()- 1; i++) {
			final Coordinate CoordA = coord.getCoordinate(i);
			final Coordinate CoordB = coord.getCoordinate(i + 1);
			Point2D p0 = viewport.toViewPoint(CoordA);
			Point2D p1 = viewport.toViewPoint(CoordB);
			if (p0.equals(p1)) {
				return;
			}
			double dx = p1.getX()-p0.getX();
			double dy = p1.getY()-p0.getY();  
			angle2 = Math.atan2( dy, dx );

			Coordinate[] cords = new Coordinate[2];
			cords[0] = CoordA;
			cords[1] = CoordB;
			Geometry newGeometry= geomFac.createLineString(cords);	

			final CoordinateList coord2 =  StyleUtils.Coordinates(newGeometry, 
					this.distance/scale+this.size/scale , this.offset/scale);
			for (int j = 0; j < coord2.size()- 1; j++) {
				Point2D center = viewport.toViewPoint(coord2.getCoordinate(j));
				final Point p = new GeometryFactory().createPoint(coord2.getCoordinate(j));

				if (previous != null && previous.distance(mid) * scale < 
						size
						&& !p.intersects(lineString)
						) {
					continue;
				}


				/*	final Coordinate c0 = coord2.getCoordinate(j);
				final Coordinate c1 = coord2.getCoordinate(j+ 1);	
				mid = new Coordinate((c0.x + c1.x) / 2, (c0.y + c1.y) / 2);
				final Point p = new GeometryFactory().createPoint(mid);
				if (previous != null && previous.distance(mid) * scale < 
						getSize()+4
						&& !p.intersects(lineString)
						) {
					continue;
				}
				Point2D center = viewport.toViewPoint(new Point2D.Double(p.getX(), p.getY()));
				 */

				paint(graphics,center);
				previous = mid;
			}
		}
	}; 


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
		this.setSelectedSymbolName(feature);
		System.out.println("feature: " + feature.getID() + "  attIndex:" + this.attributeIndex + "  value=" + this.attributeValue);
		this.paint(g, p);
	}



	@Override
	protected void render(Graphics2D g)
	{
		ExternalSymbolsRenderer r = new ExternalSymbolsRenderer();
		GeneralPath path = null;
		WKTshape wktShape = null;
		Image image = null;
		double viewScale = 1.0;
		try
		{
			viewScale = VertexParams.context.getLayerViewPanel().getViewport().getScale();
		}
		catch(Exception ex)
		{
			System.out.println("Deleting attribute from schema");
		}
		int newSize = size;
		double scaleFactor = viewScale*VertexParams.baseScale;
		if(VertexParams.sizeByScale) newSize = (int) (size * scaleFactor);
		if(symbolType == ExternalSymbolsRenderer.POLYS)
		{
			path = r.buildShape(ExternalSymbolsRenderer.POLYS, shape, byValue, orientation, newSize,
					symbolNumber);
		}
		else if(symbolType == ExternalSymbolsRenderer.STARS)
		{
			path = r.buildShape(ExternalSymbolsRenderer.STARS, shape, byValue, orientation, newSize,
					symbolNumber);
		}
		else if(symbolType == ExternalSymbolsRenderer.ANYS)
		{
			path = r.buildShape(ExternalSymbolsRenderer.ANYS, shape, byValue, orientation, newSize,
					symbolNumber);
		}
		else if(symbolType == ExternalSymbolsRenderer.WKTS)
		{
			wktShape = r.getWKTShape(ExternalSymbolsRenderer.WKTS, symbolName, newSize, shape);
		}
		else if(symbolType == ExternalSymbolsRenderer.IMAGES)
		{
			image = r.getImage(ExternalSymbolsRenderer.IMAGES,symbolName,newSize, shape);
		}

		float width = 1.0f;
		stroke  = new BasicStroke(width);
		g.setStroke(stroke);
		g.setColor(fillColor);
		AffineTransform currentTransform = g.getTransform();
		double orientationTheta = Math.toRadians(orientation);
		double attributeValueTheta =Math.toRadians(attributeValue);
		double angle=0.0D;
		if(!byValue) {
			angle= attributeValueTheta;
		} else if(byValue) {
			angle =orientationTheta;
		}
		if(rotate) {
			if((symbolType == ExternalSymbolsRenderer.POLYS) ||
					(symbolType == ExternalSymbolsRenderer.STARS)) {
				angle=0.0+angle2;}
			else {
				angle=angle+angle2;}
		}

		if(symbolType == ExternalSymbolsRenderer.POLYS)
		{

			g.rotate(angle, r.x0, r.y0);
			if(VertexParams.sizeByScale) g.translate((size-newSize)/2, (size-newSize)/2);
			//if(!byValue) g.rotate(angle,r.x0, r.y0);
			//g.rotate(angle,r.x0, r.y0);
			r.paint(g, ExternalSymbolsRenderer.POLYS, path, showLine, lineColor, showFill, fillColor, dotted);
			g.setTransform(currentTransform); 
		}
		else if(symbolType == ExternalSymbolsRenderer.STARS)
		{
			g.rotate(angle, r.x0, r.y0);
			if(VertexParams.sizeByScale) g.translate((size-newSize)/2, (size-newSize)/2);
			//if(!byValue) g.rotate(angle,r.x0, r.y0);
			//g.rotate(angle,r.x0, r.y0);
			r.paint(g, ExternalSymbolsRenderer.STARS, path, showLine, lineColor, showFill, fillColor, dotted);
			g.setTransform(currentTransform);

		}
		else if(symbolType == ExternalSymbolsRenderer.ANYS)
		{
			if(VertexParams.sizeByScale) g.translate((size-newSize)/2, (size-newSize)/2);
			g.rotate(angle, r.x0, r.y0);
			r.paint(g, ExternalSymbolsRenderer.ANYS, path, showLine, lineColor, showFill, fillColor, dotted);
			g.setTransform(currentTransform);

		}
		else if(symbolType == ExternalSymbolsRenderer.WKTS)
		{
			g.translate(r.x0, r.y0);
			if(VertexParams.sizeByScale) g.translate((size-newSize)/2, (size-newSize)/2);
			g.rotate(angle, 0, 0);
			r.paint(g, ExternalSymbolsRenderer.WKTS, wktShape, r.size, showLine, lineColor, showFill, fillColor, dotted);
			g.setTransform(currentTransform);
		}
		else if(symbolType == ExternalSymbolsRenderer.IMAGES)
		{
			if(VertexParams.sizeByScale) g.translate((size-newSize)/2, (size-newSize)/2);
			g.rotate(angle, r.x0, r.y0);
			r.paint(g, ExternalSymbolsRenderer.IMAGES, image);
			g.setTransform(currentTransform);

		}


		if(VertexParams.sizeByScale) g.translate((size-newSize)/2, (size-newSize)/2);
		drawTextLabel(g, r.x0, r.y0);
		g.setTransform(currentTransform);
	}

}
