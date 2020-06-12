package com.cadplan.jump.plugins;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.saig.core.gui.swing.sldeditor.util.FormUtils;

import com.cadplan.jump.language.I18NPlug;
import com.cadplan.jump.plugins.panel.ColorPanel;
import com.cadplan.jump.plugins.panel.VertexSymbologyPanel;
import com.cadplan.jump.utils.VertexParams;
import com.cadplan.vertices.renderer.style.ExternalSymbolsImplType;
import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureDataset;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.ui.GUIUtil;
import com.vividsolutions.jump.workbench.ui.OKCancelApplyPanel;
import com.vividsolutions.jump.workbench.ui.renderer.style.BasicStyle;

public class VertexSymbolsClassificationDialog extends JDialog implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	private String value = "";
	private ExternalSymbolsImplType style;
	boolean debug = false;
	OKCancelApplyPanel okCancelApplyPanel = new OKCancelApplyPanel();
	Layer layer;
	Feature selectedFeature;
	FeatureDataset dataset;
	boolean allowEdit = true;
	VertexSymbologyPanel symbologyPanel;
	String symbolName = "";
	int symbolType;
	int symbolNumber;
	Integer dimension;
	Color line;
	Color inner;
	private ColorPanel colorPanel;

	public JFormattedTextField sizeField;
	public JLabel sizeLabel;

	public String name;
	private JLabel styleLabel,lineLabel;
	private JLabel attribLabel;
	private JTextField styleField;
	private JTextField attribField;
	public JLabel distanceLabel;
	public JLabel offsetLabel;
	public JCheckBox rotationCB;
	public JFormattedTextField distanceField;
	public JFormattedTextField offsetField;

	private int distance;

	private double offset ;

	private   boolean rotate ;

	public VertexSymbolsClassificationDialog(String value, 
			ExternalSymbolsImplType style, 
			Integer dimension, 
			Integer distance, 
			double offset, 
			boolean rotate) {
		super(new JFrame(), true);
		this.value = value;
		this.style = style;
		this.layer = VertexParams.selectedLayer;
		this.dimension = dimension;
		this.distance = distance;
		this.offset= offset;
		this.rotate=rotate;

		this.init();
	}

	public void init() {
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setLightWeightPopupEnabled(false);
		this.setValues();

		symbologyPanel = new VertexSymbologyPanel(this.line, this.inner);

		styleLabel = new JLabel(I18NPlug.getI18N("VertexSymbols.SymbolName"));
		attribLabel = new JLabel(I18N.get("ui.renderer.style.ColorThemingTableModel.attribute-value") + ": ");
		styleField = new JTextField();
		styleField.setMinimumSize(new Dimension(75, 20));
		styleField.setText( name);
		styleField.setEditable(false);
		attribField = new JTextField();
		attribField.setMinimumSize(new Dimension(75, 20));
		attribField.setPreferredSize(new Dimension(90, 20));
		attribField.setText( value);
		attribField.setEditable(false);

		sizeLabel = new JLabel(I18NPlug.getI18N("VertexSymbols.Dialog.Size") + ": ");
		sizeField = new JFormattedTextField();
		sizeField.setColumns(5); 
		sizeField.setValue(dimension);


		colorPanel = new ColorPanel(this.line, this.inner);
		setLayout(new BorderLayout());
		add(symbologyPanel, BorderLayout.NORTH);

		//	gb.addComponent(this.symbologyPanel);
		this.symbologyPanel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if ( symbologyPanel.getSelectedIndex() == 2) {
					colorPanel.fillColorButton.setEnabled(false);
					colorPanel.lineColorButton.setEnabled(false);
					colorPanel.synchronizeCheckBox.setEnabled(false);
					colorPanel.fillColorLabel.setEnabled(false);
					colorPanel.lineColorLabel.setEnabled(false);
				} else {
					colorPanel.fillColorButton.setEnabled(true);
					colorPanel.lineColorButton.setEnabled(true);
					colorPanel.synchronizeCheckBox.setEnabled(true);
					colorPanel.fillColorLabel.setEnabled(true);
					colorPanel.lineColorLabel.setEnabled(true);
				}

			}
		});
		JPanel jPanel = new JPanel(new GridBagLayout());
		JPanel sizePanel = new JPanel(new GridBagLayout());
		FormUtils.addRowInGBL(sizePanel, 0, 0,  styleLabel,  styleField, false);
		FormUtils.addRowInGBL(sizePanel, 0, 2,  sizeLabel, sizeField, false);
		FormUtils.addFiller(sizePanel, 0, 4);
		FormUtils.addRowInGBL(jPanel, 1, 0, sizePanel, true, true);
		String line ="<html><font color=black size=3>"
				+ "<b>" + I18NPlug.getI18N("VertexSymbols.Dialog.line-decoration") + "</b></html>";
		lineLabel = new JLabel(line);

		distanceField = new JFormattedTextField();
		offsetField = new JFormattedTextField();
		distanceField.setColumns(5); 
		offsetField.setColumns(5); 

		offsetLabel= new JLabel(I18NPlug.getI18N("VertexSymbols.Dialog.line-offset") + ": ");
		distanceLabel= new JLabel(I18NPlug.getI18N("VertexSymbols.Dialog.line-distance") + ": ");
		rotationCB = new JCheckBox(I18NPlug.getI18N("VertexSymbols.Dialog.line-rotate"));
		rotationCB.setToolTipText(I18NPlug.getI18N("VertexSymbols.Dialog.line-rotate-tooltip"));
		distanceLabel.setToolTipText(I18NPlug.getI18N("VertexSymbols.Dialog.line-distance-tooltip"));
		offsetLabel.setToolTipText(I18NPlug.getI18N("VertexSymbols.Dialog.line-offset-tooltip"));


		distanceField.setValue( distance);
		offsetField.setValue( offset);
		rotationCB.setSelected( rotate);

		lineLabel.setEnabled(VertexParams.lineDecoration);
		distanceField.setEnabled(VertexParams.lineDecoration);
		offsetField.setEnabled(VertexParams.lineDecoration);
		distanceLabel.setEnabled(VertexParams.lineDecoration);
		offsetLabel.setEnabled(VertexParams.lineDecoration);
		rotationCB.setEnabled(VertexParams.lineDecoration); 

		JPanel linePanel = new JPanel(new GridBagLayout());
		FormUtils.addRowInGBL(linePanel, 0, 0, lineLabel, false, false); 
		FormUtils.addRowInGBL(linePanel, 0, 1, distanceLabel,  distanceField, false); 
		FormUtils.addRowInGBL(linePanel, 0, 3, offsetLabel, offsetField, false); 
		FormUtils.addRowInGBL(linePanel, 0,5, rotationCB);

		FormUtils.addRowInGBL(jPanel, 2, 0, linePanel, true, true);

		FormUtils.addRowInGBL(jPanel, 3, 0, this.colorPanel, true, true);
		JPanel endPanel = new JPanel(new GridBagLayout());
		FormUtils.addRowInGBL(endPanel, 0, 1, attribLabel, attribField, false); 
		this.okCancelApplyPanel = new OKCancelApplyPanel();
		this.okCancelApplyPanel.addActionListener(this);
		FormUtils.addRowInGBL(endPanel, 0, 3,new JLabel(), okCancelApplyPanel, true);
		FormUtils.addRowInGBL(jPanel, 4, 0, endPanel);
		add(jPanel,BorderLayout.SOUTH);
		setJRadioButtonSelection();
		pack();
	}

	public void setValues() {
		name =  style.getActualSymbolName();

		try {
			Map<Object, BasicStyle> attributeValueToBasicStyleMap = VertexParams.classificationStyle.getAttributeValueToBasicStyleMap();
			BasicStyle style = attributeValueToBasicStyleMap.get( value);
			line = GUIUtil.alphaColor(style.getLineColor(), style.getAlpha());
			inner = GUIUtil.alphaColor(style.getFillColor(), style.getAlpha());
		} catch (Exception var3) {
			line = GUIUtil.alphaColor( layer.getBasicStyle().getLineColor(), layer.getBasicStyle().getAlpha());
			inner = GUIUtil.alphaColor( layer.getBasicStyle().getFillColor(),  layer.getBasicStyle().getAlpha());
		}

	}

	public void setJRadioButtonSelection() {
		int b;
		for(b = 0; b <  symbologyPanel.vectorPanel.symbolPanel.vertexRB.length; ++b) {
			String side = String.valueOf(this.symbologyPanel.vectorPanel.symbolPanel.getSides()[b]);
			if (b < 7 &  name.equals("@poly" + side)) {
				symbologyPanel.vectorPanel.symbolPanel.vertexRB[b].setSelected(true);
				symbologyPanel.setSelectedComponent(symbologyPanel.getComponent(0));
			} else if (b < 14 & name.equals("@star" + side)) {
				symbologyPanel.vectorPanel.symbolPanel.vertexRB[b].setSelected(true);
				symbologyPanel.setSelectedComponent(this.symbologyPanel.getComponent(0));
			} else if (name.equals("@any" + side)) {
				symbologyPanel.vectorPanel.symbolPanel.vertexRB[b].setSelected(true);
				symbologyPanel.setSelectedComponent(symbologyPanel.getComponent(0));
			}
		}

		for(b = 0; b < symbologyPanel.imagePanel.getImageRB().length; ++b) {
			if (name.equals(VertexParams.imageNames[b])) {
				symbologyPanel.imagePanel.getImageRB()[b].setSelected(true);
				symbologyPanel.setSelectedComponent(symbologyPanel.getComponent(2));
			}
		}

		for(b = 0; b < symbologyPanel.wktPanel.getImageRB().length; ++b) {
			if (name.equals(VertexParams.wktNames[b])) {
				symbologyPanel.wktPanel.getImageRB()[b].setSelected(true);
				symbologyPanel.setSelectedComponent(symbologyPanel.getComponent(1));
			}
		}

	}

	public void setOKEnabled(boolean okEnabled) {
		okCancelApplyPanel.setOKEnabled(okEnabled);
	}

	public boolean wasOKPressed() {
		return okCancelApplyPanel.wasOKPressed();
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		if (this.wasOKPressed()) {
			Map<Object, BasicStyle> attributeValueToBasicStyleMap = VertexParams.classificationStyle.getAttributeValueToBasicStyleMap();
			BasicStyle style = attributeValueToBasicStyleMap.get(value);
			style.setLineColor(colorPanel.getLineColor());
			style.setFillColor(colorPanel.getFillColor());
			this.removeJRadioButtonSelection();
			this.dispose();
		} else {
			this.removeJRadioButtonSelection();
			this.dispose();
		}

		layer.fireAppearanceChanged();
		layer.setFeatureCollectionModified(true);
	}

	public void removeJRadioButtonSelection() {
		int b;
		for(b = 0; b < symbologyPanel.vectorPanel.symbolPanel.vertexRB.length; ++b) {
			symbologyPanel.vectorPanel.symbolPanel.vertexRB[b].setSelected(false);
		}
		for(b = 0; b < symbologyPanel.imagePanel.getImageRB().length; ++b) {
			symbologyPanel.imagePanel.getImageRB()[b].setSelected(false);
		}
		for(b = 0; b < symbologyPanel.wktPanel.getImageRB().length; ++b) {
			symbologyPanel.wktPanel.getImageRB()[b].setSelected(false);
		}
	}

	public ExternalSymbolsImplType getLegendSymbol() {
		ExternalSymbolsImplType newStyle = new ExternalSymbolsImplType();
		newStyle.setSymbolName(this.getSymbolName());
		newStyle.setShowFill(VertexParams.showFill);
		newStyle.setShowLine(VertexParams.showLine);
		newStyle.setColors(colorPanel.lineColorButton.getBackground(), colorPanel.fillColorButton.getBackground());
		newStyle.setSize(((Number) sizeField.getValue()).intValue());
		newStyle.setDistance(((Number) distanceField.getValue()).intValue());
		newStyle.setOffset(((Number) offsetField.getValue()).doubleValue());
		newStyle.setRotate(rotationCB.isSelected());
		newStyle.setEnabled(true);
		return newStyle;
	}

	public String getSymbolName() {
		try {
			int i;
			for(i = 0; i < symbologyPanel.vectorPanel.symbolPanel.vertexRB.length; ++i) {
				if (symbologyPanel.vectorPanel.symbolPanel.vertexRB[i].isSelected()) {
					if (i < 7) {
						symbolName = "@poly" + String.valueOf(symbologyPanel.vectorPanel.symbolPanel.getSides()[i]);
					} else if (i < 14) {
						symbolName = "@star" + String.valueOf(symbologyPanel.vectorPanel.symbolPanel.getSides()[i]);
					} else {
						symbolName = "@any" + String.valueOf(symbologyPanel.vectorPanel.symbolPanel.getSides()[i]);
					}
				}
			}

			for(i = 0; i < this.symbologyPanel.imagePanel.getImageRB().length; ++i) {
				if (this.symbologyPanel.imagePanel.getImageRB()[i].isSelected()) {
					this.symbolName = VertexParams.imageNames[i];
				}
			}

			for(i = 0; i < this.symbologyPanel.wktPanel.getImageRB().length; ++i) {
				if (this.symbologyPanel.wktPanel.getImageRB()[i].isSelected()) {
					this.symbolName = VertexParams.wktNames[i];
				}
			}
		} catch (Exception var2) {
			this.symbolName = this.style.getActualSymbolName();
		}

		return this.symbolName;
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
	}
}
