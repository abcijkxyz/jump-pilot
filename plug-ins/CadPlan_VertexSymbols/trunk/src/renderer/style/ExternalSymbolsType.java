package com.cadplan.vertices.renderer.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import com.cadplan.jump.HTMLTextComponent;
import com.cadplan.jump.ui.LabelCallout;
import com.cadplan.jump.utils.DataWrapper;
import com.cadplan.jump.utils.VertexParams;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureCollectionWrapper;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.ui.Viewport;
import com.vividsolutions.jump.workbench.ui.renderer.style.VertexStyle;

public class ExternalSymbolsType extends VertexStyle {
	boolean debug = false;
	protected boolean textEnabled = false;
	protected String textAttributeName = null;
	protected String textAttributeValue = "";
	protected int textAttributeIndex = -1;
	protected String textFontName = "SansSerif";
	protected int textFontSize = 10;
	protected int textFontStyle = 0;
	protected int textFontJustification = 0;
	protected int textPosition = 0;
	protected int textOffset = 1;
	protected int textOffsetValue = 0;
	protected int textScope = 0;
	private boolean drawIt = true;
	protected Color textBackgroundColor;
	protected Color textForegroundColor;
	protected boolean textFill;
	protected int textBorder;
	protected Viewport viewport;
	private DecimalFormat df;
	private int showNoteIndex;
	private boolean showNote;
	private double alpha;
	private Color transBackColor;
	private double drawFactor;
	protected boolean sizeByScale;
	private String classification;
	private Map<String, DataWrapper> classificationMap;
	private DataWrapper classificationWrapper;
	//Distance of symbols along the line
	private   int distance;
	//Distance of a symbol to the line
	private    double offset;
	private   boolean rotate;
	private   boolean lineDecoration;
	private Layer selectedLayer;

	public ExternalSymbolsType() {
		this.textBackgroundColor = Color.WHITE;
		this.textForegroundColor = Color.BLACK;
		this.textFill = false;
		this.textBorder = 0;
		this.showNoteIndex = -1;
		this.showNote = true;
		this.alpha = 1.0D;
		this.drawFactor = 1.0D;
		this.sizeByScale = false;
		this.classification = "";
		this.classificationMap = new TreeMap<String, DataWrapper>();
		this.selectedLayer = null;
	}

	public ExternalSymbolsType(Shape shape) {
		super(shape);
		this.textBackgroundColor = Color.WHITE;
		this.textForegroundColor = Color.BLACK;
		this.textFill = false;
		this.textBorder = 0;
		this.showNoteIndex = -1;
		this.showNote = true;
		this.alpha = 1.0D;
		this.drawFactor = 1.0D;
		this.sizeByScale = false;
		this.classification = "";
		this.classificationMap = new TreeMap<String, DataWrapper>();
		this.selectedLayer = null;
		this.rotate = true;
		this.distance=10;
		this.offset = 0;
		this.lineDecoration=false;
	}

	public DataWrapper getDataWrapper() {
		return this.classificationWrapper;
	}

	public void setDataWrapper(DataWrapper classificationWrapper) {
		this.classificationWrapper = classificationWrapper;
	}

	public String getClassification() {
		return this.classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
		VertexParams.classification = classification;
	}

	public Map<String, DataWrapper> getClassificationMap() {
		return this.classificationMap;
	}

	public void setClassificationMap(Map<String, DataWrapper> classificationMap) {
		this.classificationMap = classificationMap;
	}

	public void setSizeByScale(boolean sizeByScale) {
		this.sizeByScale = sizeByScale;
		VertexParams.sizeByScale = sizeByScale;
	}

	public boolean getSizeByScale() {
		return this.sizeByScale;
	}

	@Override
	public void setLineColor(Color lineColor) {
		VertexParams.lineColor = lineColor;
	}

	@Override
	public void setFillColor(Color fillColor) {
		VertexParams.fillColor = fillColor;
	}

	public void setTextEnabled(boolean enabled) {
		this.textEnabled = enabled;
		VertexParams.textEnabled = enabled;
	}

	public boolean getTextEnabled() {
		return this.textEnabled;
	}

	public void setTextAttributeName(String attribute) {
		this.textAttributeName = attribute;
		VertexParams.attTextName = attribute;
	}

	public String getTextAttributeName() {
		return this.textAttributeName;
	}

	public void setTextFontName(String fontName) {
		this.textFontName = fontName;
		VertexParams.textFontName = fontName;
	}

	public String getTextFontName() {
		return this.textFontName;
	}

	public void setTextFontSize(int fontSize) {
		this.textFontSize = fontSize;
		VertexParams.textFontSize = fontSize;
	}

	public int getTextFontSize() {
		return this.textFontSize;
	}

	public void setTextFontStyle(int fontStyle) {
		this.textFontStyle = fontStyle;
		VertexParams.textStyle = fontStyle;
	}

	public int getTextFontStyle() {
		return this.textFontStyle;
	}

	public void setTextFontJustification(int fontJustification) {
		this.textFontJustification = fontJustification;
		VertexParams.textJustification = fontJustification;
	}

	public int getTextFontJustification() {
		return this.textFontJustification;
	}

	public void setTextPosition(int position) {
		this.textPosition = position;
		VertexParams.textPosition = position;
	}

	public int getTextPosition() {
		return this.textPosition;
	}

	public void setTextOffset(int offset) {
		this.textOffset = offset;
		VertexParams.textOffset = offset;
	}

	public int getTextOffset() {
		return this.textOffset;
	}

	public void setTextOffsetValue(int offset) {
		this.textOffsetValue = offset;
		VertexParams.textOffsetValue = offset;
	}

	public int getTextOffsetValue() {
		return this.textOffsetValue;
	}

	public void setTextScope(int scope) {
		this.textScope = scope;
		VertexParams.textScope = scope;
	}

	public int getTextScope() {
		return this.textScope;
	}

	public void setTextBackgroundColor(Color backgroundColor) {
		this.textBackgroundColor = backgroundColor;
		VertexParams.textBackgroundColor = backgroundColor;
	}

	public Color getTextBackgroundColor() {
		return this.textBackgroundColor;
	}

	public void setTextForegroundColor(Color foregroundColor) {
		this.textForegroundColor = foregroundColor;
		VertexParams.textForegroundColor = foregroundColor;
	}

	public Color getTextForegroundColor() {
		return this.textForegroundColor;
	}

	public void setTextFill(boolean fill) {
		this.textFill = fill;
		VertexParams.textFill = fill;
	}

	public boolean getTextFill() {
		return this.textFill;
	}

	public void setTextBorder(int border) {
		this.textBorder = border;
		VertexParams.textBorder = border;
	}

	public int getTextBorder() {
		return this.textBorder;
	}



	public double getOffset() {
		return this.offset;
	}


	public void setOffset(double offset) {
		//this.offset = LineParams.offset ;
		this.offset = offset;
		VertexParams.offset = offset;
	}


	public boolean getRotate() {
		return this.rotate;
	}


	public void setRotate(boolean rotate) {
		this.rotate = rotate;
		VertexParams.rotate = rotate;
	}


	public int getDistance() {
		return this.distance;
	}


	public void setDistance(int distance) {
		this.distance=distance;
		VertexParams.distance =distance;
		//Params.distance = distance;
	}



	public boolean getLineDecoration() {
		return this.lineDecoration;
	}


	public void setLineDecoration(boolean lineDecoration) {
		this.lineDecoration = lineDecoration;
		VertexParams.lineDecoration = lineDecoration;
	}


	public void setDrawFactor(double drawFactor) {
		this.drawFactor = drawFactor;
	}

	public Layer getLayer() {
		return this.selectedLayer;
	}

	public void setLayer(Layer selectedLayer) {
		this.selectedLayer = selectedLayer;
	}

	public void initializeText(Layer layer) {
		FeatureCollectionWrapper fcw = layer.getFeatureCollectionWrapper();
		if (fcw != null) {
			FeatureSchema featureSchema = fcw.getFeatureSchema();

			try {
				if (this.textAttributeName != null && !this.textAttributeName.equals("")) {
					this.textAttributeIndex = featureSchema.getAttributeIndex(this.textAttributeName);
				}
			} catch (Exception var6) {
				if (this.textAttributeName.equals("$FID")) {
					this.textAttributeIndex = -1;
				} else if (this.textAttributeName.equals("$POINT")) {
					this.textAttributeIndex = -2;
				} else {
					this.textAttributeIndex = -1;
				}
			}

			try {
				this.showNoteIndex = featureSchema.getAttributeIndex("ShowLabel");
			} catch (Exception var5) {
				this.showNoteIndex = -1;
			}

			this.alpha = layer.getBasicStyle().getAlpha() / 255.0D;
		}
	}

	private int getNumber(String s) {
		boolean var2 = true;

		int n;
		try {
			n = Integer.parseInt(s);
		} catch (NumberFormatException var4) {
			n = -1;
		}

		return n;
	}

	public boolean getShowNote() {
		return this.showNote;
	}

	public String getTextAttributeValue() {
		return this.textAttributeValue;
	}

	public void setTextAttributeValue(Feature feature) {
		if (this.textAttributeIndex == -1) {
			this.textAttributeValue = String.valueOf(feature.getID());
		}

		if (this.textAttributeIndex == -2) {
			this.textAttributeValue = "$POINT";
		}

		try {
			if (this.showNoteIndex >= 0) {
				this.showNote = feature.getInteger(this.showNoteIndex) == 1;
			}
		} catch (Exception var11) {
			this.showNote = true;
		}

		if (this.textEnabled && this.textAttributeIndex >= 0) {
			Object obj = feature.getAttribute(this.textAttributeIndex);

			try {
				Date date = (Date)obj;
				SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd'|'HH:mm:ss");
				this.textAttributeValue = df.format(date).trim();
				if (this.textAttributeValue.endsWith("|00:00:00")) {
					int k = this.textAttributeValue.indexOf("|");
					this.textAttributeValue = this.textAttributeValue.substring(0, k).trim();
				}
			} catch (Exception var10) {
				try {
					this.textAttributeValue = feature.getString(this.textAttributeIndex);
				} catch (Exception var9) {
					try {
						this.textAttributeValue = String.valueOf(feature.getDouble(this.textAttributeIndex));
					} catch (Exception var8) {
						try {
							this.textAttributeValue = String.valueOf(feature.getInteger(this.textAttributeIndex));
						} catch (Exception var7) {
							this.textAttributeValue = "Error";
						}
					}
				}
			}
		}

		Geometry geometry = feature.getGeometry();
		if (!(geometry instanceof Point) && !(geometry instanceof MultiPoint)) {
			if (!(geometry instanceof LineString) && !(geometry instanceof MultiLineString)) {
				if (geometry instanceof Polygon || geometry instanceof MultiPolygon) {
					this.drawIt = (this.textScope & 4) > 0;
				}
			} else {
				this.drawIt = (this.textScope & 2) > 0;
			}
		} else {
			this.drawIt = (this.textScope & 1) > 0;
		}

	}

	public void drawTextLabel(Graphics2D g, float x0, float y0) {
		Point2D modelPoint = null;
		if (this.textAttributeIndex == -2) {
			try {
				modelPoint = this.viewport.toModelPoint(new Double(x0, y0));
			} catch (Exception var7) {
				modelPoint = new Double(0.0D, 0.0D);
			}

			this.textAttributeValue = "(" + this.format(modelPoint.getX()) + ":" + this.format(modelPoint.getY()) + ")";
		}

		if (this.textEnabled && this.drawIt && this.textAttributeValue.trim().length() > 0) {
			double offset = 0.0D;
			if (this.textOffset == 1) {
				offset = this.size / 2;
			} else if (this.textOffset == 2) {
				offset = this.textOffsetValue;
			}

			this.transBackColor = new Color(this.textBackgroundColor.getRed(), this.textBackgroundColor.getGreen(), this.textBackgroundColor.getBlue(), (int)(this.alpha * 255.0D));
			if (this.textAttributeValue.toUpperCase().startsWith("<HTML>")) {
				this.drawHTMLText(g, this.textAttributeValue, offset, x0, y0);
			} else {
				this.drawPlainText(g, this.textAttributeValue, offset, x0, y0);
			}
		}

	}

	private void drawPlainText(Graphics2D g, String text, double offset, float x0, float y0) {
		java.lang.Double viewScale = 1.0D;

		try {
			viewScale = VertexParams.context.getLayerViewPanel().getViewport().getScale();
		} catch (Exception var41) {
			System.out.println("Deleting attribute from schema");
		}

		int newSize = this.textFontSize;
		double scaleFactor = viewScale * VertexParams.baseScale;
		if (VertexParams.sizeByScale) {
			newSize = (int)(this.textFontSize * scaleFactor);
			offset *= scaleFactor;
		}

		Font font = new Font(this.textFontName, this.textFontStyle, newSize);
		FontRenderContext frc = g.getFontRenderContext();
		TextLayout layout = new TextLayout(this.textAttributeValue.trim(), font, frc);
		int var10000 = (int)(layout.getAscent() + layout.getDescent());
		Rectangle2D bounds = layout.getBounds();
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		StringTokenizer st = new StringTokenizer(this.textAttributeValue, "\\|");
		double textBlockWidth = 0.0D;
		double textBlockHeight = 0.0D;

		int numLines;
		for(numLines = 0; st.hasMoreTokens(); ++numLines) {
			String line = st.nextToken().trim();
			layout = new TextLayout(line, font, frc);
			bounds = layout.getBounds();
			if (bounds.getHeight() > height) {
				height = bounds.getHeight();
			}

			if (bounds.getWidth() > textBlockWidth) {
				textBlockWidth = bounds.getWidth();
			}
		}

		height += 2.0D;
		textBlockHeight = height * numLines + 3.0D * scaleFactor;
		textBlockWidth = textBlockWidth + 10.0D + 5.0D * scaleFactor;
		g.setColor(this.transBackColor);
		Stroke stroke = new BasicStroke(1.0F);
		g.setStroke(stroke);
		g.setFont(font);
		double xp = x0;
		double yp = y0;
		int lineNumber = 0;
		int hs = 6;
		xp = x0;
		yp = y0 - height;
		switch(this.textPosition) {
		case 0:
			xp -= textBlockWidth / 2.0D;
			yp -= textBlockHeight / 2.0D;
			break;
		case 1:
			xp -= textBlockWidth / 2.0D;
			yp = yp - offset - textBlockHeight + hs;
			break;
		case 2:
			xp += offset;
			yp = yp - offset - textBlockHeight + hs;
			break;
		case 3:
			xp += offset;
			yp = yp - textBlockHeight / 2.0D + hs / 2;
			break;
		case 4:
			xp += offset;
			yp += offset;
			break;
		case 5:
			xp -= textBlockWidth / 2.0D;
			yp += offset;
			break;
		case 6:
			xp = xp - textBlockWidth - offset;
			yp += offset;
			break;
		case 7:
			xp = xp - textBlockWidth - offset;
			yp = yp - textBlockHeight / 2.0D + hs / 2;
			break;
		case 8:
			xp = xp - textBlockWidth - offset;
			yp = yp - offset - textBlockHeight + hs;
		}

		yp += height;
		g.setColor(this.textForegroundColor);
		new LabelCallout(g, this.textBorder, this.textPosition, (int)x0, (int)y0, (int)xp, (int)yp, (int)textBlockWidth, (int)textBlockHeight, this.textForegroundColor, this.transBackColor, this.textFill);

		for(st = new StringTokenizer(this.textAttributeValue, "\\|"); st.hasMoreTokens(); ++lineNumber) {
			yp += height;
			double xt = xp + 5.0D;
			g.setColor(this.textForegroundColor);
			String line = st.nextToken().trim();
			layout = new TextLayout(line, font, frc);
			bounds = layout.getBounds();
			if (this.textFontJustification == 1) {
				xt = xp + ((int)((textBlockWidth - bounds.getWidth()) / 2.0D));
			} else if (this.textFontJustification == 2) {
				xt = xp + ((int)(textBlockWidth - bounds.getWidth())) - 5.0D;
			}

			g.drawString(line, (float)xt, (float)yp);
		}

	}

	private void drawHTMLText(Graphics2D g, String text, double offset, float x0, float y0) {
		double xp = x0;
		double yp = y0;
		java.lang.Double viewScale = VertexParams.context.getLayerViewPanel().getViewport().getScale();
		int newSize = this.textFontSize;
		double scaleFactor = viewScale * VertexParams.baseScale;
		if (VertexParams.sizeByScale) {
			newSize = (int)(this.textFontSize * scaleFactor);
			offset *= scaleFactor;
		}

		String s1 = text.substring(0, 6);
		String s2 = text.substring(6);
		String colorCode = "#FF0000";
		String newText = s1 + "<FONT SIZE=\"" + "1" + "\" FACE=\"" + this.textFontName + "\" COLOR=\"" + colorCode + "\">" + s2;
		if (this.debug) {
			System.out.println("text:" + newText);
		}

		HTMLTextComponent textComponent = new HTMLTextComponent(text, offset, x0, y0, this.transBackColor, this.textForegroundColor, this.alpha, this.textBorder, this.textPosition, this.textFill, this.textFontName, newSize);
		int width = textComponent.imageWidth;
		int height = textComponent.imageHeight;
		switch(this.textPosition) {
		case 0:
			xp = x0 - width / 2;
			yp = y0 - height / 2;
			break;
		case 1:
			xp = x0 - width / 2;
			yp = y0 - offset - height;
			break;
		case 2:
			xp = x0 + offset;
			yp = y0 - offset - height;
			break;
		case 3:
			xp = x0 + offset;
			yp = y0 - height / 2;
			break;
		case 4:
			xp = x0 + offset;
			yp = y0 + offset;
			break;
		case 5:
			xp = x0 - width / 2;
			yp = y0 + offset;
			break;
		case 6:
			xp = x0 - width - offset;
			yp = y0 + offset;
			break;
		case 7:
			xp = x0 - width - offset;
			yp = y0 - height / 2;
			break;
		case 8:
			xp = x0 - width - offset;
			yp = y0 - height - offset;
		}

		textComponent.paint(g, (float)xp, (float)yp, this.drawFactor);
	}

	private String format(double v) {
		String pat = "#,##0.0000";
		if (this.viewport != null) {
			double xRange = this.viewport.getEnvelopeInModelCoordinates().getWidth();
			if (xRange >= 10.0D) {
				pat = "#,##0.000";
			}

			if (xRange >= 100.0D) {
				pat = "#,##0.00";
			}

			if (xRange >= 1000.0D) {
				pat = "#,##0.0";
			}
		} else {
			pat = "#,##0.00";
		}

		this.df = new DecimalFormat(pat);
		return this.df.format(v);
	}

	public void setupTextParameters() {
		this.setTextEnabled(VertexParams.textEnabled);
		this.setTextAttributeName(VertexParams.attTextName);
		this.setTextFontName(VertexParams.textFontName);
		this.setTextFontSize(VertexParams.textFontSize);
		this.setTextFontStyle(VertexParams.textStyle);
		this.setTextFontJustification(VertexParams.textJustification);
		this.setTextPosition(VertexParams.textPosition);
		this.setTextOffset(VertexParams.textOffset);
		this.setTextOffsetValue(VertexParams.textOffsetValue);
		this.setTextBackgroundColor(VertexParams.textBackgroundColor);
		this.setTextForegroundColor(VertexParams.textForegroundColor);
		this.setTextBorder(VertexParams.textBorder);
		this.setTextFill(VertexParams.textFill);
		this.setTextScope(VertexParams.textScope);
		this.setSizeByScale(VertexParams.sizeByScale);
	}

	public void presetTextParameters() {
		VertexParams.textEnabled = this.getTextEnabled();
		VertexParams.attTextName = this.getTextAttributeName();
		VertexParams.textFontName = this.getTextFontName();
		VertexParams.textFontSize = this.getTextFontSize();
		VertexParams.textStyle = this.getTextFontStyle();
		VertexParams.textJustification = this.getTextFontJustification();
		VertexParams.textPosition = this.getTextPosition();
		VertexParams.textOffset = this.getTextOffset();
		VertexParams.textOffsetValue = this.getTextOffsetValue();
		VertexParams.textBackgroundColor = this.getTextBackgroundColor();
		VertexParams.textForegroundColor = this.getTextForegroundColor();
		VertexParams.textBorder = this.getTextBorder();
		VertexParams.textFill = this.getTextFill();
		VertexParams.textScope = this.getTextScope();
		VertexParams.sizeByScale = this.getSizeByScale();
	}

	public void copyText(ExternalSymbolsType from) {
		this.textEnabled = from.textEnabled;
		this.textAttributeName = from.textAttributeName;
		this.textAttributeValue = from.textAttributeValue;
		this.textAttributeIndex = from.textAttributeIndex;
		this.textFontName = from.textFontName;
		this.textFontSize = from.textFontSize;
		this.textFontStyle = from.textFontStyle;
		this.textFontJustification = from.textFontJustification;
		this.textPosition = from.textPosition;
		this.textOffset = from.textOffset;
		this.textOffsetValue = from.textOffsetValue;
		this.textBackgroundColor = from.textBackgroundColor;
		this.textForegroundColor = from.textForegroundColor;
		this.textBorder = from.textBorder;
		this.textFill = from.textFill;
		this.textScope = from.textScope;
		this.viewport = from.viewport;
		this.sizeByScale = from.sizeByScale;
	}
}
