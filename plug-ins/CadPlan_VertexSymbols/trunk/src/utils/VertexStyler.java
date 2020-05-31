package com.cadplan.jump.utils;

import java.util.Map;
import java.util.Map.Entry;

import com.cadplan.vertices.renderer.style.AnyShapeVertexStyle;
import com.cadplan.vertices.renderer.style.ExternalSymbolsImplType;
import com.cadplan.vertices.renderer.style.ExternalSymbolsType;
import com.cadplan.vertices.renderer.style.ImageVertexStyle;
import com.cadplan.vertices.renderer.style.PolygonVertexStyle;
import com.cadplan.vertices.renderer.style.StarVertexStyle;
import com.cadplan.vertices.renderer.style.WKTVertexStyle;
import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;
import com.vividsolutions.jump.workbench.ui.renderer.style.BasicStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.ColorThemingStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.SquareVertexStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.VertexStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.XBasicStyle;

import de.latlon.deejump.plugin.style.BitmapVertexStyle;
import de.latlon.deejump.plugin.style.CircleVertexStyle;
import de.latlon.deejump.plugin.style.CrossVertexStyle;

public class VertexStyler {
	Layer layer;

	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	public boolean checkStyleIsActivated() {
		VertexStyle vertexStyle = this.layer.getVertexStyle();
		return vertexStyle instanceof ExternalSymbolsImplType || vertexStyle instanceof PolygonVertexStyle || vertexStyle instanceof StarVertexStyle || vertexStyle instanceof AnyShapeVertexStyle || vertexStyle instanceof ImageVertexStyle || vertexStyle instanceof WKTVertexStyle;
	}

	public String styleName() {
		VertexStyle vertexStyle = VertexParams.selectedLayer.getVertexStyle();
		String styleName = "";

		try {
			styleName = ((ExternalSymbolsImplType)vertexStyle).getSymbolName();
		} catch (Exception var4) {
			styleName = VertexParams.symbolName;
		}

		return styleName;
	}

	public void setColorThemingStyle(ColorThemingStyle classificationStyle) {
		Layer layer = VertexParams.selectedLayer;
		layer.removeStyle(layer.getStyle(ColorThemingStyle.class));
		layer.addStyle(classificationStyle);
	}

	public void readStyle() {
		VertexParams.singleLayer = true;
		VertexParams.selectedLayer = this.layer;
		VertexStyle vertexStyle = this.layer.getVertexStyle();
		if (ColorThemingStyle.get(this.layer).isEnabled()) {
			VertexParams.classification = ColorThemingStyle.get(this.layer).getAttributeName();
			VertexParams.classificationStyle = ColorThemingStyle.get(this.layer);
		}

		if (vertexStyle instanceof ExternalSymbolsImplType) {
			VertexParams.size = vertexStyle.getSize();
			VertexParams.attName = ((ExternalSymbolsImplType)vertexStyle).getAttributeName();
			VertexParams.byValue = ((ExternalSymbolsImplType)vertexStyle).getByValue();
			VertexParams.showLine = ((ExternalSymbolsImplType)vertexStyle).getShowLine();
			VertexParams.showFill = ((ExternalSymbolsImplType)vertexStyle).getShowFill();
			VertexParams.dotted = ((ExternalSymbolsImplType)vertexStyle).getDotted();
			VertexParams.orientation = ((ExternalSymbolsImplType)vertexStyle).getOrientation();
			VertexParams.sides = ((ExternalSymbolsImplType)vertexStyle).getNumSides();
			VertexParams.type = VertexParams.EXTERNAL;
			VertexParams.symbolName = ((ExternalSymbolsImplType)vertexStyle).getSymbolName();
			VertexParams.symbolType = ((ExternalSymbolsImplType)vertexStyle).getSymbolType();
			VertexParams.sizeByScale = ((ExternalSymbolsImplType)vertexStyle).getSizeByScale();
			VertexParams.classification = ((ExternalSymbolsImplType)vertexStyle).getClassification();
			VertexParams.setClassificationMap(((ExternalSymbolsImplType)vertexStyle).getClassificationMap());
			VertexParams.distance=((ExternalSymbolsImplType)vertexStyle).getDistance();
			VertexParams.offset=((ExternalSymbolsImplType)vertexStyle).getOffset();
			VertexParams.rotate=((ExternalSymbolsImplType)vertexStyle).getRotate();
			VertexParams.lineDecoration=((ExternalSymbolsImplType)vertexStyle).getLineDecoration();

		} else if (vertexStyle instanceof PolygonVertexStyle) {
			VertexParams.size = vertexStyle.getSize();
			VertexParams.attName = ((PolygonVertexStyle)vertexStyle).getAttributeName();
			VertexParams.byValue = ((PolygonVertexStyle)vertexStyle).getByValue();
			VertexParams.showLine = ((PolygonVertexStyle)vertexStyle).getShowLine();
			VertexParams.showFill = ((PolygonVertexStyle)vertexStyle).getShowFill();
			VertexParams.dotted = ((PolygonVertexStyle)vertexStyle).getDotted();
			VertexParams.orientation = ((PolygonVertexStyle)vertexStyle).getOrientation();
			VertexParams.sides = ((PolygonVertexStyle)vertexStyle).getNumSides();
			VertexParams.type = VertexParams.POLYGON;
			VertexParams.sizeByScale = ((PolygonVertexStyle)vertexStyle).getSizeByScale();
			VertexParams.classification = ((PolygonVertexStyle)vertexStyle).getClassification();
			VertexParams.setClassificationMap(((PolygonVertexStyle)vertexStyle).getClassificationMap());
			VertexParams.distance=((PolygonVertexStyle)vertexStyle).getDistance();
			VertexParams.offset=((PolygonVertexStyle)vertexStyle).getOffset();
			VertexParams.rotate=((PolygonVertexStyle)vertexStyle).getRotate();
			VertexParams.lineDecoration=((PolygonVertexStyle)vertexStyle).getLineDecoration();

		} else if (vertexStyle instanceof StarVertexStyle) {
			VertexParams.size = vertexStyle.getSize();
			VertexParams.attName = ((StarVertexStyle)vertexStyle).getAttributeName();
			VertexParams.byValue = ((StarVertexStyle)vertexStyle).getByValue();
			VertexParams.showLine = ((StarVertexStyle)vertexStyle).getShowLine();
			VertexParams.showFill = ((StarVertexStyle)vertexStyle).getShowFill();
			VertexParams.dotted = ((StarVertexStyle)vertexStyle).getDotted();
			VertexParams.orientation = ((StarVertexStyle)vertexStyle).getOrientation();
			VertexParams.sides = ((StarVertexStyle)vertexStyle).getNumSides();
			VertexParams.type = VertexParams.STAR;
			VertexParams.sizeByScale = ((StarVertexStyle)vertexStyle).getSizeByScale();
			VertexParams.classification = ((StarVertexStyle)vertexStyle).getClassification();
			VertexParams.setClassificationMap(((StarVertexStyle)vertexStyle).getClassificationMap());
			VertexParams.distance=((StarVertexStyle)vertexStyle).getDistance();
			VertexParams.offset=((StarVertexStyle)vertexStyle).getOffset();
			VertexParams.rotate=((StarVertexStyle)vertexStyle).getRotate();
			VertexParams.lineDecoration=((StarVertexStyle)vertexStyle).getLineDecoration();

		} else if (vertexStyle instanceof AnyShapeVertexStyle) {
			VertexParams.size = vertexStyle.getSize();
			VertexParams.attName = ((AnyShapeVertexStyle)vertexStyle).getAttributeName();
			VertexParams.byValue = ((AnyShapeVertexStyle)vertexStyle).getByValue();
			VertexParams.showLine = ((AnyShapeVertexStyle)vertexStyle).getShowLine();
			VertexParams.showFill = ((AnyShapeVertexStyle)vertexStyle).getShowFill();
			VertexParams.dotted = ((AnyShapeVertexStyle)vertexStyle).getDotted();
			VertexParams.orientation = ((AnyShapeVertexStyle)vertexStyle).getOrientation();
			VertexParams.sides = ((AnyShapeVertexStyle)vertexStyle).getType();
			VertexParams.type = VertexParams.ANYSHAPE;
			VertexParams.sizeByScale = ((AnyShapeVertexStyle)vertexStyle).getSizeByScale();
			VertexParams.classification = ((AnyShapeVertexStyle)vertexStyle).getClassification();
			VertexParams.setClassificationMap(((AnyShapeVertexStyle)vertexStyle).getClassificationMap());
			VertexParams.distance=((AnyShapeVertexStyle)vertexStyle).getDistance();
			VertexParams.offset=((AnyShapeVertexStyle)vertexStyle).getOffset();
			VertexParams.rotate=((AnyShapeVertexStyle)vertexStyle).getRotate();
			VertexParams.lineDecoration=((AnyShapeVertexStyle)vertexStyle).getLineDecoration();

		} else {
			String imageName;
			int i;
			if (vertexStyle instanceof ImageVertexStyle) {
				VertexParams.size = vertexStyle.getSize();
				VertexParams.attName = ((ImageVertexStyle)vertexStyle).getAttributeName();
				VertexParams.byValue = ((ImageVertexStyle)vertexStyle).getByValue();
				VertexParams.orientation = ((ImageVertexStyle)vertexStyle).getOrientation();
				imageName = ((ImageVertexStyle)vertexStyle).getName();
				VertexParams.type = VertexParams.IMAGE;
				VertexParams.sizeByScale = ((ImageVertexStyle)vertexStyle).getSizeByScale();
				VertexParams.classification = ((ImageVertexStyle)vertexStyle).getClassification();
				VertexParams.setClassificationMap(((ImageVertexStyle)vertexStyle).getClassificationMap());
				VertexParams.distance=((ImageVertexStyle)vertexStyle).getDistance();
				VertexParams.offset=((ImageVertexStyle)vertexStyle).getOffset();
				VertexParams.rotate=((ImageVertexStyle)vertexStyle).getRotate();
				VertexParams.lineDecoration=((ImageVertexStyle)vertexStyle).getLineDecoration();
				try {
					for(i = 0; i < VertexParams.imageNames.length; ++i) {
						if (VertexParams.imageNames[i].equals(imageName)) {
							VertexParams.selectedImage = i;
						}
					}
				} catch (NullPointerException ex) {
					StyleUtils.Logger(this.getClass(), ex);
				}
			} else if (vertexStyle instanceof WKTVertexStyle) {
				VertexParams.size = vertexStyle.getSize();
				VertexParams.attName = ((WKTVertexStyle)vertexStyle).getAttributeName();
				VertexParams.byValue = ((WKTVertexStyle)vertexStyle).getByValue();
				VertexParams.orientation = ((WKTVertexStyle)vertexStyle).getOrientation();
				VertexParams.showLine = ((WKTVertexStyle)vertexStyle).getShowLine();
				VertexParams.showFill = ((WKTVertexStyle)vertexStyle).getShowFill();
				VertexParams.dotted = ((WKTVertexStyle)vertexStyle).getDotted();
				imageName = ((WKTVertexStyle)vertexStyle).getName();
				VertexParams.type = VertexParams.WKT;
				VertexParams.sizeByScale = ((WKTVertexStyle)vertexStyle).getSizeByScale();
				VertexParams.classification = ((WKTVertexStyle)vertexStyle).getClassification();
				VertexParams.setClassificationMap(((WKTVertexStyle)vertexStyle).getClassificationMap());
				VertexParams.distance=((WKTVertexStyle)vertexStyle).getDistance();
				VertexParams.offset=((WKTVertexStyle)vertexStyle).getOffset();
				VertexParams.rotate=((WKTVertexStyle)vertexStyle).getRotate();
				VertexParams.lineDecoration=((WKTVertexStyle)vertexStyle).getLineDecoration();

				try {
					for(i = 0; i < VertexParams.wktNames.length; ++i) {
						if (VertexParams.wktNames[i].equals(imageName)) {
							VertexParams.selectedWKT = i;
						}
					}

					System.out.println("Selecting WKT:" + VertexParams.selectedWKT);
				} catch (NullPointerException ex) {
					StyleUtils.Logger(this.getClass(), ex);
				}
			}
		}

		int i;
		try {
			for(i = 0; i < VertexParams.imageNames.length; ++i) {
				if (VertexParams.imageNames[i].equals(VertexParams.symbolName)) {
					VertexParams.selectedImage = i;
				}
			}
		} catch (NullPointerException ex) {
			StyleUtils.Logger(this.getClass(), ex);
		}

		try {
			for(i = 0; i < VertexParams.wktNames.length; ++i) {
				if (VertexParams.wktNames[i].equals(VertexParams.symbolName)) {
					VertexParams.selectedWKT = i;
				}
			}
		} catch (NullPointerException ex) {
			StyleUtils.Logger(this.getClass(), ex);
		}

		if (vertexStyle instanceof ExternalSymbolsType) {
			((ExternalSymbolsType)vertexStyle).presetTextParameters();
		}

	}



	public  void changeStyle()
	{
		layer.removeStyle(layer.getVertexStyle());
		ColorThemingStyle styles = ColorThemingStyle.get(layer);
		final Map<Object, BasicStyle> attributeValueToBasicStyleMap = styles.getAttributeValueToBasicStyleMap();
		for (Entry<Object, BasicStyle> entry : attributeValueToBasicStyleMap.entrySet()) {

			if (entry instanceof XBasicStyle || entry instanceof SquareVertexStyle
					|| entry instanceof BitmapVertexStyle 
					|| entry instanceof CrossVertexStyle 
					|| entry instanceof CircleVertexStyle
					|| entry instanceof StarVertexStyle) {
				final VertexStyle vStyle = ((XBasicStyle) layer.getBasicStyle()).getVertexStyle();
				layer.removeStyle(vStyle);
			}
		}

		VertexStyle newStyle = null;
		if(VertexParams.type == VertexParams.EXTERNAL)
		{
			newStyle = new ExternalSymbolsImplType();
			((ExternalSymbolsImplType) newStyle).setNumSides(VertexParams.sides);
			((ExternalSymbolsImplType) newStyle).setOrientation(VertexParams.orientation);
			((ExternalSymbolsImplType) newStyle).setDotted(VertexParams.dotted);
			((ExternalSymbolsImplType) newStyle).setShowLine(VertexParams.showLine);
			((ExternalSymbolsImplType) newStyle).setShowFill(VertexParams.showFill);
			((ExternalSymbolsImplType) newStyle).setByValue(VertexParams.byValue);
			((ExternalSymbolsImplType) newStyle).setAttributeName(VertexParams.attName);
			if(VertexParams.symbolName ==null & !VertexParams.getClassificationMap().isEmpty()) {
				Map.Entry<String,DataWrapper> entry = VertexParams.getClassificationMap().entrySet().iterator().next();
				String value=entry.getValue().getSimbol();
				((ExternalSymbolsImplType) newStyle).setSymbolName(value);
			} else {

				((ExternalSymbolsImplType) newStyle).setSymbolName(VertexParams.symbolName);
			}


			((ExternalSymbolsImplType) newStyle).setSymbolType(VertexParams.symbolType);
			((ExternalSymbolsImplType) newStyle).setSizeByScale(VertexParams.sizeByScale);
			((ExternalSymbolsImplType) newStyle).setClassification(VertexParams.classification);
			((ExternalSymbolsImplType) newStyle).setClassificationMap(VertexParams.getClassificationMap()); 
			((ExternalSymbolsImplType) newStyle).setDistance(VertexParams.distance);
			((ExternalSymbolsImplType) newStyle).setOffset(VertexParams.offset);
			((ExternalSymbolsImplType) newStyle).setRotate(VertexParams.rotate);
			((ExternalSymbolsImplType) newStyle).setLineDecoration(VertexParams.lineDecoration);

			((ExternalSymbolsType)newStyle).setupTextParameters();
			newStyle.setSize(VertexParams.size);
		}
		else if(VertexParams.type == VertexParams.POLYGON )
		{
			newStyle = new PolygonVertexStyle();
			((PolygonVertexStyle) newStyle).setNumSides(VertexParams.sides);
			((PolygonVertexStyle) newStyle).setOrientation(VertexParams.orientation);
			((PolygonVertexStyle) newStyle).setDotted(VertexParams.dotted);
			((PolygonVertexStyle) newStyle).setShowLine(VertexParams.showLine);
			((PolygonVertexStyle) newStyle).setShowFill(VertexParams.showFill);
			((PolygonVertexStyle) newStyle).setByValue(VertexParams.byValue);
			((PolygonVertexStyle) newStyle).setAttributeName(VertexParams.attName);
			((PolygonVertexStyle) newStyle).setSizeByScale(VertexParams.sizeByScale);
			((PolygonVertexStyle) newStyle).setClassification(VertexParams.classification);
			((PolygonVertexStyle) newStyle).setClassificationMap(VertexParams.getClassificationMap()); 
			((PolygonVertexStyle) newStyle).setDistance(VertexParams.distance);
			((PolygonVertexStyle) newStyle).setOffset(VertexParams.offset);
			((PolygonVertexStyle) newStyle).setRotate(VertexParams.rotate);
			((PolygonVertexStyle) newStyle).setLineDecoration(VertexParams.lineDecoration);
			((PolygonVertexStyle)newStyle).setupTextParameters();
			newStyle.setSize(VertexParams.size);
		}
		else if(VertexParams.type == VertexParams.STAR)
		{
			newStyle = new StarVertexStyle();
			((StarVertexStyle) newStyle).setNumSides(VertexParams.sides);
			((StarVertexStyle) newStyle).setOrientation(VertexParams.orientation);
			((StarVertexStyle) newStyle).setDotted(VertexParams.dotted);
			((StarVertexStyle) newStyle).setShowLine(VertexParams.showLine);
			((StarVertexStyle) newStyle).setShowFill(VertexParams.showFill);
			((StarVertexStyle) newStyle).setByValue(VertexParams.byValue);
			((StarVertexStyle) newStyle).setAttributeName(VertexParams.attName);
			((StarVertexStyle) newStyle).setSizeByScale(VertexParams.sizeByScale);
			((StarVertexStyle) newStyle).setClassification(VertexParams.classification);
			((StarVertexStyle) newStyle).setClassificationMap(VertexParams.getClassificationMap()); 
			((StarVertexStyle) newStyle).setDistance(VertexParams.distance);
			((StarVertexStyle) newStyle).setOffset(VertexParams.offset);
			((StarVertexStyle) newStyle).setRotate(VertexParams.rotate);
			((StarVertexStyle) newStyle).setLineDecoration(VertexParams.lineDecoration);
			((StarVertexStyle)newStyle).setupTextParameters();
			newStyle.setSize(VertexParams.size);
		}
		else if(VertexParams.type == VertexParams.ANYSHAPE)
		{
			newStyle = new AnyShapeVertexStyle();
			((AnyShapeVertexStyle) newStyle).setType(VertexParams.sides);
			((AnyShapeVertexStyle) newStyle).setOrientation(VertexParams.orientation);
			((AnyShapeVertexStyle) newStyle).setDotted(VertexParams.dotted);
			((AnyShapeVertexStyle) newStyle).setShowLine(VertexParams.showLine);
			((AnyShapeVertexStyle) newStyle).setShowFill(VertexParams.showFill);
			((AnyShapeVertexStyle) newStyle).setByValue(VertexParams.byValue);
			((AnyShapeVertexStyle) newStyle).setAttributeName(VertexParams.attName);
			((AnyShapeVertexStyle) newStyle).setSizeByScale(VertexParams.sizeByScale);
			((AnyShapeVertexStyle) newStyle).setClassification(VertexParams.classification);
			((AnyShapeVertexStyle) newStyle).setClassificationMap(VertexParams.getClassificationMap()); 
			((AnyShapeVertexStyle) newStyle).setDistance(VertexParams.distance);
			((AnyShapeVertexStyle) newStyle).setOffset(VertexParams.offset);
			((AnyShapeVertexStyle) newStyle).setRotate(VertexParams.rotate);
			((AnyShapeVertexStyle) newStyle).setLineDecoration(VertexParams.lineDecoration);
			((AnyShapeVertexStyle)newStyle).setupTextParameters();
			newStyle.setSize(VertexParams.size);
		}
		else if(VertexParams.type == VertexParams.IMAGE)
		{
			newStyle = new ImageVertexStyle();
			int n = VertexParams.selectedImage;
			((ImageVertexStyle) newStyle).setSize(VertexParams.images[n].getWidth(null),VertexParams.images[n].getHeight(null));
			((ImageVertexStyle) newStyle).setName(VertexParams.imageNames[n]);
			((ImageVertexStyle) newStyle).setOrientation(VertexParams.orientation);
			((ImageVertexStyle) newStyle).setByValue(VertexParams.byValue);
			((ImageVertexStyle) newStyle).setAttributeName(VertexParams.attName);
			((ImageVertexStyle) newStyle).setSizeByScale(VertexParams.sizeByScale);
			((ImageVertexStyle) newStyle).setClassification(VertexParams.classification);
			((ImageVertexStyle) newStyle).setClassificationMap(VertexParams.getClassificationMap()); 
			((ImageVertexStyle) newStyle).setDistance(VertexParams.distance);
			((ImageVertexStyle) newStyle).setOffset(VertexParams.offset);
			((ImageVertexStyle) newStyle).setRotate(VertexParams.rotate);
			((ImageVertexStyle) newStyle).setLineDecoration(VertexParams.lineDecoration);
			((ImageVertexStyle)newStyle).setupTextParameters();
			newStyle.setSize(VertexParams.size);
		}
		else if(VertexParams.type == VertexParams.WKT)
		{
			newStyle = new WKTVertexStyle();
			int n = VertexParams.selectedWKT;
			((WKTVertexStyle) newStyle).setSize(VertexParams.wktShapes[n].extent,VertexParams.wktShapes[n].extent);
			((WKTVertexStyle) newStyle).setName(VertexParams.wktNames[n]);
			((WKTVertexStyle) newStyle).setOrientation(VertexParams.orientation);
			((WKTVertexStyle) newStyle).setDotted(VertexParams.dotted);
			((WKTVertexStyle) newStyle).setShowLine(VertexParams.showLine);
			((WKTVertexStyle) newStyle).setShowFill(VertexParams.showFill);            
			((WKTVertexStyle) newStyle).setByValue(VertexParams.byValue);
			((WKTVertexStyle) newStyle).setAttributeName(VertexParams.attName);
			((WKTVertexStyle) newStyle).setSizeByScale(VertexParams.sizeByScale);
			((WKTVertexStyle) newStyle).setClassification(VertexParams.classification);
			((WKTVertexStyle) newStyle).setClassificationMap(VertexParams.getClassificationMap()); 
			((WKTVertexStyle) newStyle).setDistance(VertexParams.distance);
			((WKTVertexStyle) newStyle).setOffset(VertexParams.offset);
			((WKTVertexStyle) newStyle).setRotate(VertexParams.rotate);
			((WKTVertexStyle) newStyle).setLineDecoration(VertexParams.lineDecoration);
			((WKTVertexStyle)newStyle).setupTextParameters();
			newStyle.setSize(VertexParams.size);
		}
		LayerViewPanel panel = JUMPWorkbench.getInstance().getFrame().getContext().getLayerViewPanel();
		Double viewScale = 1.0/panel.getViewport().getScale();
		VertexParams.actualScale = viewScale;
		newStyle.setEnabled(true);
		newStyle.initialize(layer);
		layer.addStyle(newStyle);

		layer.setFeatureCollectionModified(true);
		layer.fireAppearanceChanged();
	}


}
