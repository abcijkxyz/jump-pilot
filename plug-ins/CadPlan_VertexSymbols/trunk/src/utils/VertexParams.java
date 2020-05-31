package com.cadplan.jump.utils;

import java.awt.Color;
import java.awt.Image;
import java.util.Map;
import java.util.TreeMap;

import com.cadplan.jump.ui.WKTshape;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.ui.renderer.style.ColorThemingStyle;

public class VertexParams {
	public static String version = "0.20";
	public static int POLYGON = 0;
	public static int STAR = 1;
	public static int IMAGE = 2;
	public static int ANYSHAPE = 3;
	public static int WKT = 4;
	public static int EXTERNAL = 5;
	public static int type;
	public static double orientation;
	public static int size;
	public static boolean showLine;
	public static boolean showFill;
	public static boolean dotted;
	public static int sides;
	public static Image[] images;
	public static String[] imageNames;
	public static WKTshape[] wktShapes;
	public static String[] wktNames;
	public static int selectedImage;
	public static int selectedWKT;
	public static boolean byValue;
	public static String attName;
	public static boolean singleLayer;
	public static Layer selectedLayer;
	public static boolean sizeByScale;
	public static double baseScale;
	public static double actualScale;
	public static String attTextName;
	public static boolean textEnabled;
	public static String textFontName;
	public static int textFontSize;
	public static int textStyle;
	public static int textJustification;
	public static int textPosition;
	public static int textOffset;
	public static int textOffsetValue;
	public static int textScope;
	public static int textBorder;
	public static boolean textFill;
	public static Color textBackgroundColor;
	public static Color textForegroundColor;
	public static String symbolName;
	public static int symbolType;
	public static int symbolNumber;
	public static Color lineColor;
	public static Color fillColor;
	public static String classification;
	public static Map<String, DataWrapper> classificationMap;
	public static ColorThemingStyle classificationStyle;
	public static WorkbenchContext context;
	public static int distance;

	public static double offset;

	public static   boolean rotate;
	public static   boolean lineDecoration;

	static {
		type = POLYGON;
		orientation = 0.0D;
		size = 32;
		showLine = true;
		showFill = true;
		dotted = false;
		sides = 4;
		selectedImage = -1;
		selectedWKT = -1;
		byValue = true;
		attName = "";
		singleLayer = true;
		selectedLayer = null;
		sizeByScale = false;
		baseScale = 1.0D;
		actualScale = 1.0D;
		attTextName = "";
		textEnabled = false;
		textFontName = "SansSerif";
		textFontSize = 10;
		textStyle = 0;
		textJustification = 0;
		textPosition = 0;
		textOffset = 1;
		textOffsetValue = 0;
		textScope = 1;
		textBorder = 0;
		textFill = false;
		textBackgroundColor = Color.WHITE;
		textForegroundColor = Color.BLACK;
		symbolName = "@poly4";
		lineColor = Color.BLACK;
		fillColor = Color.WHITE;
		classification = null;
		classificationMap = new TreeMap();
		classificationStyle = new ColorThemingStyle();
		distance=1;
		offset = 0;
		rotate=false;
		lineDecoration=false;

	}

	public static ColorThemingStyle getColorThemingStyle() {
		return classificationStyle;
	}

	public static void setColorThemingStyle(ColorThemingStyle classificationStyle) {
		VertexParams.classificationStyle = classificationStyle;
	}

	public static Map<String, DataWrapper> getClassificationMap() {
		return classificationMap;
	}

	public static void setClassificationMap(Map<String, DataWrapper> classificationMap) {
		VertexParams.classificationMap = classificationMap;
	}

	public int getDistance()
	{
		return distance;
	}
	public void setDistance(int distance) {
		VertexParams.distance = distance;
	}
	public double getOffset()
	{
		return offset;
	}
	public void setOffset(double offset) {
		VertexParams.offset = offset;
	}
	public boolean getRotate()
	{
		return rotate;
	}

	public void setRotate(boolean rotate) {
		VertexParams.rotate = rotate;
	}

	public boolean getLineDecoration()
	{
		return lineDecoration;
	}

	public void setLineDecoration(boolean lineDecoration) {
		VertexParams.lineDecoration = lineDecoration;
	}
}
