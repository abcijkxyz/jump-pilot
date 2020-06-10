package com.cadplan.jump.plugins;

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

import com.cadplan.designer.GridBagDesigner;
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

		this.symbologyPanel = new VertexSymbologyPanel(this.line, this.inner);

		this.styleLabel = new JLabel(I18NPlug.getI18N("VertexSymbols.SymbolName"));
		this.attribLabel = new JLabel(I18N.get("ui.renderer.style.ColorThemingTableModel.attribute-value") + ": ");
		this.styleField = new JTextField();
		this.styleField.setMinimumSize(new Dimension(75, 20));
		this.styleField.setText(this.name);
		this.styleField.setEditable(false);
		this.attribField = new JTextField();
		this.attribField.setMinimumSize(new Dimension(75, 20));
		this.attribField.setPreferredSize(new Dimension(90, 20));
		this.attribField.setText(this.value);
		this.attribField.setEditable(false);

		this.sizeLabel = new JLabel(I18NPlug.getI18N("VertexSymbols.Dialog.Size") + ": ");
		this.sizeField = new JFormattedTextField();
		this.sizeField.setColumns(5); 
		this.sizeField.setValue(dimension);


		this.colorPanel = new ColorPanel(this.line, this.inner);
		//	this.colorPanel.removeDimensionBox();

		GridBagDesigner gb = new GridBagDesigner(this);
		gb.setPosition(0, 0);
		gb.setSpan(4, 1);
		gb.setWeight(1.0D, 1.0D);
		gb.setFill(1);
		gb.addComponent(this.symbologyPanel);
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
		FormUtils.addRowInGBL(jPanel, 0, 0, this.attribLabel, this.attribField, false);
		FormUtils.addRowInGBL(jPanel, 0, 2, this.styleLabel, this.styleField, false);

		FormUtils.addRowInGBL(jPanel, 0, 4, this.sizeLabel, this.sizeField, false);


		String line ="<html><font color=black size=3>"
				+ "<b>" + I18NPlug.getI18N("VertexSymbols.Dialog.line-decoration") + "</b></html>";
		this.lineLabel = new JLabel(line);

		this.distanceField = new JFormattedTextField();
		this.offsetField = new JFormattedTextField();
		this.distanceField.setColumns(5); 
		this.offsetField.setColumns(5); 

		this.offsetLabel= new JLabel(I18NPlug.getI18N("VertexSymbols.Dialog.line-offset") + ": ");
		this.distanceLabel= new JLabel(I18NPlug.getI18N("VertexSymbols.Dialog.line-distance") + ": ");
		this.rotationCB = new JCheckBox(I18NPlug.getI18N("VertexSymbols.Dialog.line-rotate"));
		this.rotationCB.setToolTipText(I18NPlug.getI18N("VertexSymbols.Dialog.line-rotate-tooltip"));
		this.distanceLabel.setToolTipText(I18NPlug.getI18N("VertexSymbols.Dialog.line-distance-tooltip"));
		this.offsetLabel.setToolTipText(I18NPlug.getI18N("VertexSymbols.Dialog.line-offset-tooltip"));


		this.distanceField.setValue(this.distance);
		this.offsetField.setValue(this.offset);
		this.rotationCB.setSelected(this.rotate);

		this.lineLabel.setEnabled(VertexParams.lineDecoration);
		this.distanceField.setEnabled(VertexParams.lineDecoration);
		this.offsetField.setEnabled(VertexParams.lineDecoration);
		this.distanceLabel.setEnabled(VertexParams.lineDecoration);
		this.offsetLabel.setEnabled(VertexParams.lineDecoration);
		this.rotationCB.setEnabled(VertexParams.lineDecoration); 

		JPanel linePanel = new JPanel(new GridBagLayout());
		FormUtils.addRowInGBL(linePanel, 0, 0, this.distanceLabel, this.distanceField, false); 
		FormUtils.addRowInGBL(linePanel, 0, 2, this.offsetLabel, this.offsetField, false); 
		FormUtils.addRowInGBL(linePanel, 0,4, rotationCB);

		FormUtils.addRowInGBL(jPanel, 1, 0, lineLabel, true, true);
		FormUtils.addRowInGBL(jPanel, 1, 1, linePanel, true, true);

		FormUtils.addRowInGBL(jPanel, 2, 0, this.colorPanel, true, true);
		gb.setPosition(0, 1);
		gb.setFill(2);
		gb.setInsets(10, 0, 0, 0);
		gb.setSpan(4, 1);
		gb.addComponent(jPanel);
		this.okCancelApplyPanel = new OKCancelApplyPanel();
		this.okCancelApplyPanel.addActionListener(this);
		gb.setPosition(0, 2);
		gb.setInsets(0, 10, 5, 5);
		gb.setSpan(3, 1);
		gb.addComponent(this.okCancelApplyPanel);
		this.setJRadioButtonSelection();
		this.pack();
	}

	public void setValues() {
		this.name = this.style.getActualSymbolName();

		try {
			Map<Object, BasicStyle> attributeValueToBasicStyleMap = VertexParams.classificationStyle.getAttributeValueToBasicStyleMap();
			BasicStyle style = attributeValueToBasicStyleMap.get(this.value);
			this.line = GUIUtil.alphaColor(style.getLineColor(), style.getAlpha());
			this.inner = GUIUtil.alphaColor(style.getFillColor(), style.getAlpha());
		} catch (Exception var3) {
			this.line = GUIUtil.alphaColor(this.layer.getBasicStyle().getLineColor(), this.layer.getBasicStyle().getAlpha());
			this.inner = GUIUtil.alphaColor(this.layer.getBasicStyle().getFillColor(), this.layer.getBasicStyle().getAlpha());
		}

	}

	public void setJRadioButtonSelection() {
		int b;
		for(b = 0; b < this.symbologyPanel.vectorPanel.symbolPanel.vertexRB.length; ++b) {
			String side = String.valueOf(this.symbologyPanel.vectorPanel.symbolPanel.getSides()[b]);
			if (b < 7 & this.name.equals("@poly" + side)) {
				this.symbologyPanel.vectorPanel.symbolPanel.vertexRB[b].setSelected(true);
				this.symbologyPanel.setSelectedComponent(this.symbologyPanel.getComponent(0));
			} else if (b < 14 & this.name.equals("@star" + side)) {
				this.symbologyPanel.vectorPanel.symbolPanel.vertexRB[b].setSelected(true);
				this.symbologyPanel.setSelectedComponent(this.symbologyPanel.getComponent(0));
			} else if (this.name.equals("@any" + side)) {
				this.symbologyPanel.vectorPanel.symbolPanel.vertexRB[b].setSelected(true);
				this.symbologyPanel.setSelectedComponent(this.symbologyPanel.getComponent(0));
			}
		}

		for(b = 0; b < this.symbologyPanel.imagePanel.getImageRB().length; ++b) {
			if (this.name.equals(VertexParams.imageNames[b])) {
				this.symbologyPanel.imagePanel.getImageRB()[b].setSelected(true);
				this.symbologyPanel.setSelectedComponent(this.symbologyPanel.getComponent(2));
			}
		}

		for(b = 0; b < this.symbologyPanel.wktPanel.getImageRB().length; ++b) {
			if (this.name.equals(VertexParams.wktNames[b])) {
				this.symbologyPanel.wktPanel.getImageRB()[b].setSelected(true);
				this.symbologyPanel.setSelectedComponent(this.symbologyPanel.getComponent(1));
			}
		}

	}

	public void setOKEnabled(boolean okEnabled) {
		this.okCancelApplyPanel.setOKEnabled(okEnabled);
	}

	public boolean wasOKPressed() {
		return this.okCancelApplyPanel.wasOKPressed();
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		if (this.wasOKPressed()) {
			Map<Object, BasicStyle> attributeValueToBasicStyleMap = VertexParams.classificationStyle.getAttributeValueToBasicStyleMap();
			BasicStyle style = attributeValueToBasicStyleMap.get(this.value);
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
		for(b = 0; b < this.symbologyPanel.vectorPanel.symbolPanel.vertexRB.length; ++b) {
			this.symbologyPanel.vectorPanel.symbolPanel.vertexRB[b].setSelected(false);
		}
		for(b = 0; b < this.symbologyPanel.imagePanel.getImageRB().length; ++b) {
			this.symbologyPanel.imagePanel.getImageRB()[b].setSelected(false);
		}
		for(b = 0; b < this.symbologyPanel.wktPanel.getImageRB().length; ++b) {
			this.symbologyPanel.wktPanel.getImageRB()[b].setSelected(false);
		}
	}

	public ExternalSymbolsImplType getLegendSymbol() {
		ExternalSymbolsImplType newStyle = new ExternalSymbolsImplType();
		newStyle.setSymbolName(this.getSymbolName());
		newStyle.setShowFill(VertexParams.showFill);
		newStyle.setShowLine(VertexParams.showLine);
		newStyle.setColors(this.colorPanel.lineColorButton.getBackground(), this.colorPanel.fillColorButton.getBackground());
		newStyle.setSize(((Number) this.sizeField.getValue()).intValue());
		newStyle.setDistance(((Number) this.distanceField.getValue()).intValue());
		newStyle.setOffset(((Number) this.offsetField.getValue()).doubleValue());
		newStyle.setRotate(rotationCB.isSelected());
		newStyle.setEnabled(true);
		return newStyle;
	}

	public String getSymbolName() {
		try {
			int i;
			for(i = 0; i < this.symbologyPanel.vectorPanel.symbolPanel.vertexRB.length; ++i) {
				if (this.symbologyPanel.vectorPanel.symbolPanel.vertexRB[i].isSelected()) {
					if (i < 7) {
						this.symbolName = "@poly" + String.valueOf(this.symbologyPanel.vectorPanel.symbolPanel.getSides()[i]);
					} else if (i < 14) {
						this.symbolName = "@star" + String.valueOf(this.symbologyPanel.vectorPanel.symbolPanel.getSides()[i]);
					} else {
						this.symbolName = "@any" + String.valueOf(this.symbologyPanel.vectorPanel.symbolPanel.getSides()[i]);
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
