package com.cadplan.jump.plugins.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openjump.core.ui.plugin.layer.LayerPropertiesPlugIn.PropertyPanel;

import com.cadplan.designer.GridBagDesigner;
import com.cadplan.jump.language.I18NPlug;
import com.cadplan.jump.ui.VertexColorButton;
import com.cadplan.jump.utils.VertexParams;
import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.feature.AttributeType;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.workbench.ui.renderer.style.LabelStyle;

public class TextLabelPanel extends JPanel implements PropertyPanel {
	private static final long serialVersionUID = 1L;
	private ButtonGroup group;
	private JCheckBox enabledCB;
	private JCheckBox pointsCB;
	private JCheckBox linesCB;
	private JCheckBox polysCB;
	private JCheckBox fillCB;
	private JCheckBox presetTextCB;
	private JLabel attributeLabel;
	private JLabel fontLabel;
	private JLabel backgroundLabel;
	private JLabel foregroundLabel;
	private JLabel scopeLabel;
	private JLabel borderLabel;
	private JLabel positionLabel;
	public JComboBox<String> attributeCombo;
	private JComboBox<?> fontNameCombo;
	private JComboBox<?> fontSizeCombo;
	private JComboBox<?> fontStyleCombo;
	private JComboBox<?> positionCombo;
	private JComboBox<?> offsetCombo;
	private JComboBox<?> fontJustificationCombo;
	private JComboBox<?> borderStyleCombo;
	private JTextField offsetValueField;
	private VertexColorButton fgColorButton;
	private VertexColorButton bgColorButton;
	String[] fontSizes;
	String[] fontStyles;
	String[] fontJust;
	String[] positions;
	String[] offsets;
	String[] borders;

	public TextLabelPanel(ButtonGroup group) {
		this.group = group;
		this.setNames();
		this.init();
	}

	public void setNames() {
		this.fontSizes = new String[]{"7", "8", "9", "10", "12", "14", "16", "18", "20", "24", "32", "48", "72"};
		this.fontStyles = new String[]{I18NPlug.getI18N("VertexSymbols.Dialog.Plain"), I18NPlug.getI18N("VertexSymbols.Dialog.Bold"), I18NPlug.getI18N("VertexSymbols.Dialog.PlainItalic"), I18NPlug.getI18N("VertexSymbols.Dialog.BoldItalic")};
		this.fontJust = new String[]{I18NPlug.getI18N("VertexSymbols.Dialog.Left"), I18NPlug.getI18N("VertexSymbols.Dialog.Centre"), I18NPlug.getI18N("VertexSymbols.Dialog.Right")};
		this.positions = new String[]{I18NPlug.getI18N("VertexSymbols.Dialog.Centre"), I18NPlug.getI18N("VertexSymbols.Dialog.North"), I18NPlug.getI18N("VertexSymbols.Dialog.NorthEast"), I18NPlug.getI18N("VertexSymbols.Dialog.East"), I18NPlug.getI18N("VertexSymbols.Dialog.SouthEast"), I18NPlug.getI18N("VertexSymbols.Dialog.South"), I18NPlug.getI18N("VertexSymbols.Dialog.SouthWest"), I18NPlug.getI18N("VertexSymbols.Dialog.West"), I18NPlug.getI18N("VertexSymbols.Dialog.NorthWest")};
		this.offsets = new String[]{I18NPlug.getI18N("VertexSymbols.Dialog.None"), I18NPlug.getI18N("VertexSymbols.Dialog.Auto"), I18NPlug.getI18N("VertexSymbols.Dialog.Value")};
		this.borders = new String[]{I18NPlug.getI18N("VertexSymbols.Dialog.None"), I18NPlug.getI18N("VertexSymbols.Dialog.Boxed"), I18NPlug.getI18N("VertexSymbols.Dialog.Callout")};
	}

	public void init() {
		JPanel pan = new JPanel();
		GridBagDesigner gb = new GridBagDesigner(pan);
		this.enabledCB = new JCheckBox(I18N.get("ui.style.LabelStylePanel.enable-labelling"));
		this.enabledCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TextLabelPanel.this.updateControls();
			}
		});
		gb.setPosition(0, 0);
		gb.setInsets(10, 10, 0, 0);
		gb.addComponent(this.enabledCB);
		this.attributeLabel = new JLabel(I18NPlug.getI18N("VertexSymbols.Dialog.Attribute"));
		gb.setPosition(0, 1);
		gb.setInsets(10, 10, 0, 0);
		gb.setAnchor(13);
		gb.addComponent(this.attributeLabel);
		this.attributeCombo = new JComboBox();
		gb.setPosition(1, 1);
		gb.setInsets(10, 10, 0, 10);
		gb.setSpan(2, 1);
		gb.setFill(2);
		gb.addComponent(this.attributeCombo);
		this.fontLabel = new JLabel(I18NPlug.getI18N("VertexSymbols.Dialog.Font"));
		gb.setPosition(0, 2);
		gb.setInsets(10, 10, 0, 0);
		gb.setAnchor(13);
		gb.addComponent(this.fontLabel);
		String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		this.fontNameCombo = new JComboBox(fontNames);
		gb.setPosition(1, 2);
		gb.setInsets(10, 10, 0, 10);
		gb.setSpan(2, 1);
		gb.setFill(2);
		gb.addComponent(this.fontNameCombo);
		this.fontSizeCombo = new JComboBox(this.fontSizes);
		gb.setPosition(3, 2);
		gb.setInsets(10, 10, 0, 0);
		gb.setAnchor(13);
		gb.addComponent(this.fontSizeCombo);
		this.fontStyleCombo = new JComboBox(this.fontStyles);
		gb.setPosition(1, 3);
		gb.setInsets(10, 10, 0, 10);
		gb.addComponent(this.fontStyleCombo);
		this.fontJustificationCombo = new JComboBox(this.fontJust);
		gb.setPosition(2, 3);
		gb.setInsets(10, 10, 0, 0);
		gb.addComponent(this.fontJustificationCombo);
		this.positionLabel = new JLabel(I18NPlug.getI18N("VertexSymbols.Dialog.Position"));
		gb.setPosition(0, 4);
		gb.setInsets(10, 10, 0, 0);
		gb.setAnchor(13);
		gb.addComponent(this.positionLabel);
		this.positionCombo = new JComboBox(this.positions);
		gb.setPosition(1, 4);
		gb.setInsets(10, 10, 0, 0);
		gb.addComponent(this.positionCombo);
		this.offsetCombo = new JComboBox(this.offsets);
		gb.setPosition(2, 4);
		gb.setInsets(10, 10, 0, 10);
		gb.addComponent(this.offsetCombo);
		this.offsetValueField = new JTextField(5);
		gb.setPosition(3, 4);
		gb.setInsets(10, 10, 0, 10);
		gb.addComponent(this.offsetValueField);
		this.backgroundLabel = new JLabel(I18NPlug.getI18N("VertexSymbols.Dialog.BackColor"));
		gb.setPosition(0, 5);
		gb.setInsets(10, 10, 0, 0);
		gb.setAnchor(13);
		gb.addComponent(this.backgroundLabel);
		this.bgColorButton = new VertexColorButton(Color.WHITE);
		gb.setPosition(1, 5);
		gb.setInsets(10, 10, 0, 0);
		gb.setAnchor(17);
		gb.addComponent(this.bgColorButton);
		this.foregroundLabel = new JLabel(I18NPlug.getI18N("VertexSymbols.Dialog.ForeColor"));
		gb.setPosition(2, 5);
		gb.setInsets(10, 10, 0, 0);
		gb.setAnchor(13);
		gb.addComponent(this.foregroundLabel);
		this.fgColorButton = new VertexColorButton(Color.BLACK);
		gb.setPosition(3, 5);
		gb.setInsets(10, 10, 0, 0);
		gb.setAnchor(17);
		gb.addComponent(this.fgColorButton);
		this.borderLabel = new JLabel(I18NPlug.getI18N("VertexSymbols.Dialog.Border"));
		gb.setPosition(0, 6);
		gb.setInsets(10, 10, 0, 0);
		gb.setAnchor(13);
		gb.addComponent(this.borderLabel);
		this.borderStyleCombo = new JComboBox(this.borders);
		gb.setPosition(1, 6);
		gb.setInsets(10, 10, 0, 0);
		gb.setAnchor(17);
		gb.addComponent(this.borderStyleCombo);
		this.fillCB = new JCheckBox(I18NPlug.getI18N("VertexSymbols.Dialog.FillBackground"));
		gb.setPosition(2, 6);
		gb.setInsets(10, 10, 0, 0);
		gb.addComponent(this.fillCB);
		this.scopeLabel = new JLabel(I18NPlug.getI18N("VertexSymbols.Dialog.Scope"));
		gb.setPosition(0, 7);
		gb.setInsets(10, 10, 0, 0);
		gb.setAnchor(13);
		gb.addComponent(this.scopeLabel);
		this.pointsCB = new JCheckBox(I18NPlug.getI18N("VertexSymbols.Dialog.Points"));
		gb.setPosition(1, 7);
		gb.setInsets(10, 10, 0, 0);
		gb.setAnchor(17);
		gb.addComponent(this.pointsCB);
		this.linesCB = new JCheckBox(I18NPlug.getI18N("VertexSymbols.Dialog.Lines"));
		gb.setPosition(2, 7);
		gb.setInsets(10, 10, 0, 0);
		gb.setAnchor(17);
		gb.addComponent(this.linesCB);
		this.polysCB = new JCheckBox(I18NPlug.getI18N("VertexSymbols.Dialog.Polygons"));
		gb.setPosition(3, 7);
		gb.setInsets(10, 10, 0, 0);
		gb.setAnchor(17);
		gb.addComponent(this.polysCB);
		this.presetTextCB = new JCheckBox(I18NPlug.getI18N("VertexSymbols.Dialog.ActivateLabels"));
		this.presetTextCB.setToolTipText(I18NPlug.getI18N("VertexSymbols.Dialog.ActivateLabels.tooltip"));
		final LabelStyle labelStyle = VertexParams.selectedLayer.getLabelStyle();
		this.presetTextCB.setSelected(true);
		this.presetTextCB.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (!TextLabelPanel.this.presetTextCB.isSelected()) {
					labelStyle.setEnabled(false);
				}

			}
		});
		if (labelStyle.isEnabled()) {
			gb.setPosition(1, 8);
			gb.setInsets(10, 10, 0, 0);
			gb.setAnchor(13);
			gb.addComponent(this.presetTextCB);
		}
		this.add(pan, BorderLayout.NORTH);
		this.setValues();
	}

	protected void updateControls() {
		if (this.enabledCB.isSelected()) {
			this.attributeLabel.setEnabled(true);
			this.attributeCombo.setEnabled(true);
			this.fontLabel.setEnabled(true);
			this.fontNameCombo.setEnabled(true);
			this.fontSizeCombo.setEnabled(true);
			this.fontStyleCombo.setEnabled(true);
			this.fontJustificationCombo.setEnabled(true);
			this.positionLabel.setEnabled(true);
			this.positionCombo.setEnabled(true);
			this.offsetCombo.setEnabled(true);
			this.offsetValueField.setEnabled(true);
			this.backgroundLabel.setEnabled(true);
			this.bgColorButton.setEnabled(true);
			this.foregroundLabel.setEnabled(true);
			this.fgColorButton.setEnabled(true);
			this.borderLabel.setEnabled(true);
			this.borderStyleCombo.setEnabled(true);
			this.fillCB.setEnabled(true);
			this.scopeLabel.setEnabled(true);
			this.pointsCB.setEnabled(true);
			this.linesCB.setEnabled(true);
			this.polysCB.setEnabled(true);
			this.presetTextCB.setEnabled(true);
			this.repaint();
		} else {
			this.attributeLabel.setEnabled(false);
			this.attributeCombo.setEnabled(false);
			this.fontLabel.setEnabled(false);
			this.fontNameCombo.setEnabled(false);
			this.fontSizeCombo.setEnabled(false);
			this.fontStyleCombo.setEnabled(false);
			this.fontJustificationCombo.setEnabled(false);
			this.positionLabel.setEnabled(false);
			this.positionCombo.setEnabled(false);
			this.offsetCombo.setEnabled(false);
			this.offsetValueField.setEnabled(false);
			this.backgroundLabel.setEnabled(false);
			this.bgColorButton.setEnabled(false);
			this.foregroundLabel.setEnabled(false);
			this.fgColorButton.setEnabled(false);
			this.borderLabel.setEnabled(false);
			this.borderStyleCombo.setEnabled(false);
			this.fillCB.setEnabled(false);
			this.scopeLabel.setEnabled(false);
			this.pointsCB.setEnabled(false);
			this.linesCB.setEnabled(false);
			this.polysCB.setEnabled(false);
			this.presetTextCB.setEnabled(false);
			this.repaint();
		}

	}

	private void setValues() {
		if (VertexParams.singleLayer && VertexParams.selectedLayer != null) {
			this.attributeCombo.removeAllItems();
			this.attributeCombo.addItem("$FID");
			this.attributeCombo.addItem("$POINT");
			FeatureSchema featureSchema = VertexParams.selectedLayer.getFeatureCollectionWrapper().getFeatureSchema();

			for(int i = 0; i < featureSchema.getAttributeCount(); ++i) {
				AttributeType type = featureSchema.getAttributeType(i);
				if (type == AttributeType.DOUBLE || type == AttributeType.INTEGER || type == AttributeType.STRING || type == AttributeType.DATE || type == AttributeType.GEOMETRY) {
					String name = featureSchema.getAttributeName(i);
					this.attributeCombo.addItem(name);
				}
			}

			if (VertexParams.attName != null && VertexParams.attTextName != null) {
			}

			this.attributeCombo.setSelectedItem(VertexParams.attTextName);
		}

		this.enabledCB.setSelected(VertexParams.textEnabled);
		this.fontNameCombo.setSelectedItem(VertexParams.textFontName);
		this.fontSizeCombo.setSelectedItem(String.valueOf(VertexParams.textFontSize));
		this.fontStyleCombo.setSelectedItem(VertexParams.textStyle);
		this.fontJustificationCombo.setSelectedIndex(VertexParams.textJustification);
		this.positionCombo.setSelectedIndex(VertexParams.textPosition);
		this.offsetCombo.setSelectedIndex(VertexParams.textOffset);
		this.offsetValueField.setText(String.valueOf(VertexParams.textOffsetValue));
		this.bgColorButton.setBackground(VertexParams.textBackgroundColor);
		this.fgColorButton.setBackground(VertexParams.textForegroundColor);
		this.borderStyleCombo.setSelectedIndex(VertexParams.textBorder);
		this.fillCB.setSelected(VertexParams.textFill);
		this.pointsCB.setSelected((VertexParams.textScope & 1) > 0);
		this.linesCB.setSelected((VertexParams.textScope & 2) > 0);
		this.polysCB.setSelected((VertexParams.textScope & 4) > 0);
		this.updateControls();
	}

	public boolean getValues() {
		VertexParams.attTextName = (String)this.attributeCombo.getSelectedItem();
		VertexParams.textEnabled = this.enabledCB.isSelected();
		VertexParams.textFontName = (String)this.fontNameCombo.getSelectedItem();
		VertexParams.textFontSize = Integer.parseInt((String)this.fontSizeCombo.getSelectedItem());
		VertexParams.textStyle = this.getFontStyle((String)this.fontStyleCombo.getSelectedItem());
		VertexParams.textJustification = this.fontJustificationCombo.getSelectedIndex();
		VertexParams.textPosition = this.positionCombo.getSelectedIndex();
		VertexParams.textOffset = this.offsetCombo.getSelectedIndex();
		VertexParams.textOffsetValue = Integer.parseInt(this.offsetValueField.getText());
		VertexParams.textBackgroundColor = this.bgColorButton.getBackground();
		VertexParams.textForegroundColor = this.fgColorButton.getBackground();
		VertexParams.textBorder = this.borderStyleCombo.getSelectedIndex();
		VertexParams.textFill = this.fillCB.isSelected();
		int scope = 0;
		if (this.pointsCB.isSelected()) {
			++scope;
		}

		if (this.linesCB.isSelected()) {
			scope += 2;
		}

		if (this.polysCB.isSelected()) {
			scope += 4;
		}

		VertexParams.textScope = scope;
		return true;
	}

	private int getFontStyle(String style) {
		if (style.equals(I18NPlug.getI18N("VertexSymbols.Dialog.Plain"))) {
			return 0;
		} else if (style.equals(I18NPlug.getI18N("VertexSymbols.Dialog.Bold"))) {
			return 1;
		} else if (style.equals(I18NPlug.getI18N("VertexSymbols.Dialog.PlainItalic"))) {
			return 2;
		} else {
			return style.equals(I18NPlug.getI18N("VertexSymbols.Dialog.BoldItalic")) ? 3 : 0;
		}
	}

	@Override
	public String getTitle() {
		return I18N.get("ui.style.LabelStylePanel.labels");
	}

	@Override
	public void updateStyles() {
	}

	@Override
	public String validateInput() {
		return null;
	}
}
