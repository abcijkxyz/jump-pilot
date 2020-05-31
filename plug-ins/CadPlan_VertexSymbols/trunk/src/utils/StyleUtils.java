package com.cadplan.jump.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.openjump.core.ui.util.GeometryUtils;

import com.cadplan.jump.language.I18NPlug;
import com.cadplan.jump.plugins.VertexSymbolsDialog;
import com.cadplan.jump.plugins.panel.TextLabelPanel;
import com.cadplan.jump.plugins.panel.VertexColorThemingPanel;
import com.cadplan.jump.plugins.panel.VertexParametersPanel;
import com.cadplan.jump.plugins.panel.VertexSymbologyPanel;
import com.cadplan.jump.ui.TextAreaEditor;
import com.vividsolutions.jts.awt.ShapeWriter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.linearref.LengthIndexedLine;
import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.feature.AttributeType;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.geom.EnvelopeUtil;
import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.ui.TextEditor;
import com.vividsolutions.jump.workbench.ui.Viewport;
import com.vividsolutions.jump.workbench.ui.WorkbenchFrame;
import com.vividsolutions.jump.workbench.ui.renderer.java2D.Java2DConverter;
import com.vividsolutions.jump.workbench.ui.renderer.style.AlphaSetting;
import com.vividsolutions.jump.workbench.ui.renderer.style.ColorThemingStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.Style;

public class StyleUtils {
	public static WorkbenchFrame frameInstance = JUMPWorkbench.getInstance().getFrame();

	public static List<String> availableValuesList(Layer layer, String attribute, int maxSize) {
		List<String> list = new ArrayList();
		FeatureCollection fc = layer.getFeatureCollectionWrapper();
		Iterator it = fc.iterator();

		while(it.hasNext() && list.size() < maxSize) {
			Feature f = (Feature)it.next();
			list.add(f.getString(attribute));
		}

		Set<String> set1 = new HashSet();
		List<String> newList = new ArrayList();
		Iterator iter = list.iterator();

		while(iter.hasNext()) {
			Object element = iter.next();
			if (set1.add((String)element)) {
				newList.add((String)element);
			}
		}

		list.clear();
		list.addAll(newList);
		return list;
	}

	public static List<String> getAttributeList(Layer layer) {
		List<String> attrNames = new ArrayList();
		if (layer != null) {
			FeatureCollection fc = layer.getFeatureCollectionWrapper();
			FeatureSchema schema = fc.getFeatureSchema();

			for(int i = 0; i < schema.getAttributeCount(); ++i) {
				if (schema.getAttributeType(i) != AttributeType.GEOMETRY) {
					attrNames.add(schema.getAttributeName(i));
				}

				if (schema.getAttributeName(i).equals("R_G_B") || schema.getAttributeName(i).equals("ShowLabel") || schema.getAttributeName(i).equals("SymbolName")) {
					attrNames.remove(schema.getAttributeName(i));
				}
			}
		} else {
			attrNames = new ArrayList();
		}

		return attrNames;
	}

	public static CoordinateList Coordinates(Geometry geometry, double distance, double offset) {
		CoordinateList coordinates = new CoordinateList();
		LengthIndexedLine lengthIndexedLine = new LengthIndexedLine(geometry);
		double length = geometry.getLength();

		for(double runningLength = distance; runningLength <= length + distance; runningLength += distance) {
			Coordinate c = lengthIndexedLine.extractPoint(runningLength, offset);
			coordinates.add(c);
		}

		return coordinates;
	}


	// [Giuseppe Aruta 2018_3_29] this code derives from
	// es.unex.sextante.vectorTools.linesToEquispacedPoints class form Sextante
	// Algorithms. It is used to calculate a List of coordinates along a defined
	// geometry at an equispaced distance (in this case: 10 px)
	@SuppressWarnings("unchecked")
	public static CoordinateList Coordinates(final Geometry geom,  double distance) {
		final CoordinateList coordinates = new CoordinateList();
		int i, j;
		int iPoints;
		double dX1, dX2, dY1, dY2;
		double dAddedPointX, dAddedPointY;
		double dDX, dDY;
		double dRemainingDistFromLastSegment = 0;
		double dDistToNextPoint;
		double dDist;
		final Coordinate[] coords = geom.getCoordinates();
		if (coords.length == 0) {
			return coordinates;
		}

		dAddedPointX = dX1 = coords[0].x;
		dAddedPointY = dY1 = coords[0].y;
		final Coordinate coord1 = new Coordinate(dAddedPointX, dAddedPointY);
		coordinates.add(coord1);
		// m_Output.addFeature(point, record);
		for (i = 0; i < coords.length - 1; i++) {
			dX2 = coords[i + 1].x;
			dX1 = coords[i].x;
			dY2 = coords[i + 1].y;
			dY1 = coords[i].y;
			dDX = dX2 - dX1;
			dDY = dY2 - dY1;
			dDistToNextPoint = Math.sqrt(dDX * dDX + dDY * dDY);

			if (dRemainingDistFromLastSegment + dDistToNextPoint > distance) {
				iPoints = (int) ((dRemainingDistFromLastSegment + dDistToNextPoint) / distance);
				dDist = distance - dRemainingDistFromLastSegment;
				for (j = 0; j < iPoints; j++) {
					dDist = distance - dRemainingDistFromLastSegment;
					dDist += j * distance;
					dAddedPointX = dX1 + dDist * dDX / dDistToNextPoint;
					dAddedPointY = dY1 + dDist * dDY / dDistToNextPoint;
					final Coordinate coord2 = new Coordinate(dAddedPointX,
							dAddedPointY);
					coordinates.add(coord2);
				}
				dDX = dX2 - dAddedPointX;
				dDY = dY2 - dAddedPointY;
				dRemainingDistFromLastSegment = Math
						.sqrt(dDX * dDX + dDY * dDY);
			} else {
				coordinates.add(coords[i + 1]);
				dRemainingDistFromLastSegment += dDistToNextPoint;
			}

		}
		return coordinates;
	}



	public static ColorThemingStyle getColorThemingStyleIfEnabled(Layer layer) {
		ColorThemingStyle someStyle = null;
		Iterator var3 = layer.getStyles().iterator();

		while(var3.hasNext()) {
			Style style = (Style)var3.next();
			if (style instanceof ColorThemingStyle && style.isEnabled()) {
				someStyle = (ColorThemingStyle)style;
			}
		}

		return someStyle;
	}

	public static int getAlpha(Layer layer) {
		ColorThemingStyle cts = getColorThemingStyleIfEnabled(layer);
		if (cts != null) {
			return cts.getDefaultStyle().getAlpha();
		} else {
			List<Style> styles = layer.getStylesIfEnabled(AlphaSetting.class);
			Iterator<Style> localIterator = styles.iterator();
			if (localIterator.hasNext()) {
				Style style = localIterator.next();
				return ((AlphaSetting)style).getAlpha();
			} else {
				return 128;
			}
		}
	}

	public static void setAlpha(Layer layer, int alpha) {
		List<Style> styles = layer.getStyles(AlphaSetting.class);
		Iterator var4 = styles.iterator();

		while(var4.hasNext()) {
			Style style = (Style)var4.next();
			((AlphaSetting)style).setAlpha(alpha);
		}

		ColorThemingStyle cts = getColorThemingStyleIfEnabled(layer);
		if (cts != null) {
			cts.setAlpha(alpha);
		}

	}

	public static boolean getValues(VertexSymbolsDialog dialog, VertexParametersPanel parametersPanel, 
			VertexSymbologyPanel symbologyPanel, 
			TextLabelPanel labelPanel, 
			VertexColorThemingPanel classificationPanel) {
		try {
			VertexParams.size = Integer.parseInt(parametersPanel.sizeField.getText());
		} catch (NumberFormatException var8) {
			JOptionPane.showMessageDialog(frameInstance, I18NPlug.getI18N("VertexSymbols.Dialog.Warning1"), "Warning...", 2);
			return false;
		}

		try {
			VertexParams.orientation = Double.parseDouble(parametersPanel.orienField.getText());
		} catch (NumberFormatException var7) {
			JOptionPane.showMessageDialog(frameInstance, I18NPlug.getI18N("VertexSymbols.Dialog.Warning2"), "Warning...", 2);
			return false;
		}

		try {
			VertexParams.baseScale = Double.parseDouble(parametersPanel.baseScaleField.getText());
		} catch (NumberFormatException var6) {
			JOptionPane.showMessageDialog(frameInstance, I18NPlug.getI18N("VertexSymbols.Dialog.Warning3"), "Warning...", 2);
			return false;
		}

		try {
			VertexParams.symbolName = getSymbolName(symbologyPanel);
		} catch (NumberFormatException var5) {
			JOptionPane.showMessageDialog(frameInstance, I18NPlug.getI18N("VertexSymbols.Dialog.Warning4"), "Warning...", 2);
			return false;
		}


		try {

			VertexParams.distance=((Number)parametersPanel.distanceField.getValue()).intValue();
			//	VertexParams.distance= Integer.parseInt(parametersPanel.distanceField.getText());
		} catch (NumberFormatException var8) {
			JOptionPane.showMessageDialog(frameInstance, I18NPlug.getI18N("VertexSymbols.Dialog.Warning1"), "Warning...", 2);
			return false;
		}

		try {
			VertexParams.offset=((Number)parametersPanel.offsetField.getValue()).doubleValue();
			//	VertexParams.offset= Double.parseDouble(parametersPanel.offsetField.getText());
		} catch (NumberFormatException var6) {
			JOptionPane.showMessageDialog(frameInstance, I18NPlug.getI18N("VertexSymbols.Dialog.Warning3"), "Warning...", 2);
			return false;
		}
		VertexParams.rotate = parametersPanel.rotationCB.isSelected();
		VertexParams.lineDecoration = dialog.activateLineDecorationCB.isSelected();



		VertexParams.showLine = parametersPanel.showLineCB.isSelected();
		VertexParams.showFill = parametersPanel.showFillCB.isSelected();
		VertexParams.dotted = parametersPanel.dottedCB.isSelected();
		VertexParams.sizeByScale = parametersPanel.sizeByScaleCB.isSelected();
		VertexParams.type = getVertexType();
		VertexParams.sides = getSides(VertexParams.type, symbologyPanel);
		VertexParams.selectedImage = symbologyPanel.imagePanel.getSelectedImage();
		VertexParams.selectedWKT = symbologyPanel.wktPanel.getSelectedImage();
		VertexParams.attName = (String)parametersPanel.attributeCB.getSelectedItem();
		if (VertexParams.attName == null) {
			VertexParams.attName = "";
		}

		VertexParams.byValue = parametersPanel.absValueRB.isSelected();
		VertexParams.symbolName = getSymbolName(symbologyPanel);
		VertexParams.symbolType = getSymbolType(symbologyPanel);
		VertexParams.classification = ColorThemingStyle.get(VertexParams.selectedLayer).getAttributeName();
		VertexParams.classificationMap=classificationPanel.getMap();
		//	VertexParams.setClassificationMap(classificationPanel.getMap());
		if (ColorThemingStyle.get(VertexParams.selectedLayer).isEnabled()) {
			VertexParams.classification = ColorThemingStyle.get(VertexParams.selectedLayer).getAttributeName();
		}

		Boolean bool = labelPanel.getValues();
		return bool;
	}




	public static int getSides(int paramInt, VertexSymbologyPanel symbologyPanel) {
		for(byte b = 0; b < symbologyPanel.vectorPanel.symbolPanel.vertexRB.length; ++b) {
			if (b < 7 && paramInt == VertexParams.POLYGON && symbologyPanel.vectorPanel.symbolPanel.vertexRB[b].isSelected()) {
				return symbologyPanel.vectorPanel.symbolPanel.sides[b];
			}

			if (b >= 7 && paramInt == VertexParams.STAR && symbologyPanel.vectorPanel.symbolPanel.vertexRB[b].isSelected()) {
				return symbologyPanel.vectorPanel.symbolPanel.sides[b];
			}

			if (b >= 14 && paramInt == VertexParams.ANYSHAPE && symbologyPanel.vectorPanel.symbolPanel.vertexRB[b].isSelected()) {
				return symbologyPanel.vectorPanel.symbolPanel.sides[b];
			}
		}

		return symbologyPanel.vectorPanel.symbolPanel.sides[2];
	}

	public static int getVertexType() {
		return VertexParams.EXTERNAL;
	}

	public static String getSymbolName(VertexSymbologyPanel symbologyPanel) {
		byte b;
		for(b = 0; b < symbologyPanel.vectorPanel.symbolPanel.vertexRB.length; ++b) {
			if (symbologyPanel.vectorPanel.symbolPanel.vertexRB[b].isSelected()) {
				if (b < 7) {
					return "@poly" + String.valueOf(symbologyPanel.vectorPanel.symbolPanel.getSides()[b]);
				}

				if (b < 14) {
					return "@star" + String.valueOf(symbologyPanel.vectorPanel.symbolPanel.getSides()[b]);
				}

				return "@any" + String.valueOf(symbologyPanel.vectorPanel.symbolPanel.getSides()[b]);
			}
		}

		for(b = 0; b < symbologyPanel.imagePanel.getImageRB().length; ++b) {
			if (symbologyPanel.imagePanel.getImageRB()[b].isSelected()) {
				return VertexParams.imageNames[b];
			}
		}

		for(b = 0; b < symbologyPanel.wktPanel.getImageRB().length; ++b) {
			if (symbologyPanel.wktPanel.getImageRB()[b].isSelected()) {
				return VertexParams.wktNames[b];
			}
		}

		return null;
	}

	public static int getSymbolType(VertexSymbologyPanel symbologyPanel) {
		byte b2;
		for(b2 = 0; b2 < symbologyPanel.vectorPanel.symbolPanel.vertexRB.length; ++b2) {
			if (symbologyPanel.vectorPanel.symbolPanel.vertexRB[b2].isSelected()) {
				if (b2 < 7) {
					return 0;
				}

				if (b2 < 14) {
					return 1;
				}

				return 2;
			}
		}

		JRadioButton[] arrayOfJRadioButton;
		int i = (arrayOfJRadioButton = symbologyPanel.imagePanel.getImageRB()).length;

		JRadioButton jRadioButton;
		for(b2 = 0; b2 < i; ++b2) {
			jRadioButton = arrayOfJRadioButton[b2];
			if (jRadioButton.isSelected()) {
				return 4;
			}
		}

		i = (arrayOfJRadioButton = symbologyPanel.wktPanel.getImageRB()).length;

		for(b2 = 0; b2 < i; ++b2) {
			jRadioButton = arrayOfJRadioButton[b2];
			if (jRadioButton.isSelected()) {
				return 3;
			}
		}

		return -1;
	}

	public static JLabel label(String text, Font font, int increaseSize, boolean editable) {
		final JLabel labelValue = new JLabel();
		labelValue.setToolTipText("");
		editable = true;
		labelValue.setBorder(BorderFactory.createEmptyBorder());
		font = new Font(labelValue.getFont().getName(), labelValue.getFont().getStyle(), labelValue.getFont().getSize() + increaseSize);
		labelValue.setFont(font);
		labelValue.setText(text);
		editable = false;
		if (editable) {
			labelValue.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent evt) {
					if (evt.getClickCount() == 2) {
						TextEditor fc = new TextEditor();
						fc.setSelectedFont(labelValue.getFont());
						fc.setSelectedFontSize(labelValue.getFont().getSize());
						fc.setSelectedFontStyle(labelValue.getFont().getStyle());
						fc.setSelectedFontFamily(labelValue.getFont().getFamily());
						fc.setSampleTextField(labelValue.getText());
						fc.showDialog(labelValue.getParent(), I18N.get("org.openjump.core.ui.plugin.style.LegendPlugIn.modify-label"));
						Font labelFont = fc.getSelectedFont();
						if (fc.wasOKPressed()) {
							labelValue.setFont(labelFont);
							labelValue.setText(fc.getSampleTextField().getText());
						}
					}

				}
			});
		}

		labelValue.setForeground(new Color(20, 24, 25));
		return labelValue;
	}

	public static JTextArea area(String text, Font font, int increaseSize) {
		final JTextArea textArea = new JTextArea();
		textArea.setToolTipText("");
		textArea.setColumns(10);
		textArea.setLineWrap(true);
		textArea.setRows(2);
		textArea.setWrapStyleWord(true);
		textArea.setBorder(BorderFactory.createEmptyBorder());
		font = new Font(textArea.getFont().getName(), textArea.getFont().getStyle(), textArea.getFont().getSize() + increaseSize);
		textArea.setFont(font);
		textArea.setText(text);
		textArea.setEditable(false);
		textArea.setHighlighter(null);
		textArea.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					TextAreaEditor fc = new TextAreaEditor();
					fc.setSelectedFont(textArea.getFont());
					fc.setSelectedFontSize(textArea.getFont().getSize());
					fc.setSelectedFontStyle(textArea.getFont().getStyle());
					fc.setSelectedFontFamily(textArea.getFont().getFamily());
					fc.setSampleTextField(textArea.getText());
					fc.showDialog(textArea.getParent(), I18N.get("org.openjump.core.ui.plugin.style.LegendPlugIn.modify-label"));
					Font labelFont = fc.getSelectedFont();
					if (fc.wasOKPressed()) {
						textArea.setFont(labelFont);
						textArea.setText(fc.getSampleTextField().getText());
					}
				}

			}
		});
		textArea.setForeground(new Color(20, 24, 25));
		return textArea;
	}

	public static JTextField integerField(int column) {
		JTextField jTextField = new JTextField(column);
		jTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char vChar = e.getKeyChar();
				if (!Character.isDigit(vChar) && vChar != '\b' && vChar != 127) {
					e.consume();
				}

			}
		});
		return jTextField;
	}

	public static JTextField doubleField(int column) {
		JTextField jTextField = new JTextField(column);
		jTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char vChar = e.getKeyChar();
				if (!Character.isDigit(vChar) && vChar != '.' && vChar != '\b' && vChar != 127) {
					e.consume();
				}

			}
		});
		return jTextField;
	}

	public static void leftScreen(Component componentToMove) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
		Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
		int newx = rect.width - componentToMove.getWidth() + rect.x - rect.width / 10;
		int newy = rect.height / 10;
		Dimension screendim = Toolkit.getDefaultToolkit().getScreenSize();
		int offset = 30;
		int neww = componentToMove.getWidth();
		int newh = componentToMove.getHeight();
		if (neww > screendim.width - 2 * offset) {
			neww = screendim.width - 2 * offset;
			newx = offset;
		}

		if (newh > screendim.height - 2 * offset) {
			newh = screendim.height - 2 * offset;
			newy = offset;
		}

		componentToMove.setBounds(newx, newy, neww, newh);
	}

	public static Shape getShapeFromGeometry(Geometry geometry, Viewport viewport) throws NoninvertibleTransformException {
		Envelope bufferedEnvelope = EnvelopeUtil.bufferByFraction(
				viewport.getEnvelopeInModelCoordinates(), 0.05D);
		Geometry actualGeometry = geometry;
		Envelope geomEnv = actualGeometry.getEnvelopeInternal();
		if (!bufferedEnvelope.contains(geomEnv))
			if (!(geometry instanceof LineString) && !(geometry instanceof com.vividsolutions.jts.geom.MultiLineString))
				actualGeometry = clipGeometry(geometry, bufferedEnvelope);  
		return viewport.getJava2DConverter().toShape(actualGeometry);
	}










	public static Shape getShapeFromGeometry(Geometry geometry) throws NoninvertibleTransformException {
		final Coordinate c = geometry.getCentroid().getCoordinate();
		Java2DConverter java2DConverter = new Java2DConverter(new Java2DConverter.PointConverter() {
			@Override
			public Point2D toViewPoint(Coordinate modelCoordinate) {
				return new Point2D.Double((modelCoordinate.x-c.x), (modelCoordinate.y-c.y));
			}
			@Override
			public double getScale() { return 0.5;}
			@Override
			public Envelope getEnvelopeInModelCoordinates() {
				return new Envelope(0.0, 20, 0.0, 20);
			}
		});
		Shape shape = java2DConverter.toShape(geometry);
		return  shape;
	}

	public static Shape getShapeFromGeometry(Geometry geometry, int size) throws NoninvertibleTransformException {
		final Coordinate c = geometry.getCentroid().getCoordinate();
		Java2DConverter java2DConverter = new Java2DConverter(new Java2DConverter.PointConverter() {
			@Override
			public Point2D toViewPoint(Coordinate modelCoordinate) {
				return new Point2D.Double((modelCoordinate.x-c.x), (modelCoordinate.y-c.y));
			}
			@Override
			public double getScale() { return 1.0D;}
			@Override
			public Envelope getEnvelopeInModelCoordinates() {
				return new Envelope(0.0, 20, 0.0, 20);
			}
		});
		Shape shape = java2DConverter.toShape(geometry);
		return  shape;
	}



	public static Shape getShapeFromGeometry2(Geometry geometry, int size) throws NoninvertibleTransformException {

		int geometryDimension = geometry.getBoundary().getBoundaryDimension();
		int scale = (size/geometryDimension)/100;
		GeometryUtils.scaleGeometry(geometry, scale);
		ShapeWriter sw = new ShapeWriter();
		Shape shape = sw.toShape(geometry);
		return  shape;
	}



	public static String getName(String name, String description) {

		String tooltip = "";
		tooltip = "<HTML><BODY>";
		tooltip += "<DIV style=\"width: 200px; text-justification: justify;\">";
		tooltip += "<b>" + name + "</b>" + "<br>";
		tooltip += description + "<br>";
		tooltip += "</DIV></BODY></HTML>";
		return tooltip;
	}


	public static Shape toShape(Geometry geometry, Viewport viewport) throws NoninvertibleTransformException {
		Envelope bufferedEnvelope = EnvelopeUtil.bufferByFraction(
				viewport.getEnvelopeInModelCoordinates(), 0.05D);
		Geometry actualGeometry = geometry;
		Envelope geomEnv = actualGeometry.getEnvelopeInternal();
		if (!bufferedEnvelope.contains(geomEnv))
			if (!(geometry instanceof LineString) && !(geometry instanceof com.vividsolutions.jts.geom.MultiLineString))
				actualGeometry = clipGeometry(geometry, bufferedEnvelope);  
		return viewport.getJava2DConverter().toShape(actualGeometry);
	}

	private static Geometry clipGeometry(Geometry geom, Envelope env) {
		try {
			Geometry clipGeom = EnvelopeUtil.toGeometry(env)
					.intersection(geom);
			return clipGeom;
		} catch (Exception exception) {
			return geom;
		} 
	}
	/**
	 * 
	 * @param parent
	 * @param row
	 * @param startCol
	 * @param label
	 * @param component
	 * @param weigthy
	 * @param insets
	 */

	public static void addRowInGBL(JComponent parent, int row, int startCol,
			JComponent label, JComponent component, double weigthy,
			boolean insets) {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = startCol;
		gridBagConstraints.gridy = row;

		if (weigthy > 0.0) {
			gridBagConstraints.anchor = GridBagConstraints.NORTH;
		} else {
			gridBagConstraints.anchor = GridBagConstraints.CENTER;
		}

		if (insets) {
			gridBagConstraints.insets =  new Insets(3, 3, 3, 3);
		}

		parent.add(label, gridBagConstraints);

		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = startCol + 1;
		gridBagConstraints.gridy = row;
		gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.weightx = 1.0;

		if (weigthy > 0.0) {
			gridBagConstraints.weighty = (float) weigthy;
			gridBagConstraints.fill = GridBagConstraints.BOTH;
		}

		if (insets) {
			gridBagConstraints.insets =  new Insets(3, 3, 3, 3);
		}

		parent.add(component, gridBagConstraints);
	}

	/**
	 * 
	 * @param parent
	 * @param row
	 * @param startCol
	 * @param label
	 * @param component
	 */

	public static void addRowInGBL(JComponent parent, int row, int startCol,
			JComponent label, JComponent component) {
		addRowInGBL(parent, row, startCol, label, component, 0.0, true);
	}


	/**
	 * Test Logger
	 * 
	 * @param plugin
	 * @param e
	 */

	public static void Logger(Class<?> plugin, Exception e) {
		Logger LOG = Logger.getLogger(plugin);
		JUMPWorkbench
		.getInstance()
		.getFrame()
		.warnUser(
				plugin.getSimpleName() + " Exception: " + e.toString());
		LOG.error(plugin.getName() + " Exception: ", e);
	}




}
