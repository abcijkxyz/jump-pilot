package com.cadplan.jump.plugins;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.saig.core.gui.swing.sldeditor.util.FormUtils;

import com.cadplan.jump.icon.IconLoader;
import com.cadplan.jump.language.I18NPlug;
import com.cadplan.jump.plugins.panel.ColorPanel;
import com.cadplan.jump.plugins.panel.TextLabelPanel;
import com.cadplan.jump.plugins.panel.TransparPanel;
import com.cadplan.jump.plugins.panel.VertexColorThemingPanel;
import com.cadplan.jump.plugins.panel.VertexParametersPanel;
import com.cadplan.jump.plugins.panel.VertexSymbologyPanel;
import com.cadplan.jump.utils.StyleUtils;
import com.cadplan.jump.utils.VertexParams;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.ui.GenericNames;
import com.vividsolutions.jump.workbench.ui.MultiInputDialog;
import com.vividsolutions.jump.workbench.ui.renderer.style.ColorThemingStyle;

public class VertexSymbolsDialog extends MultiInputDialog implements ItemListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	public JCheckBox activateLineDecorationCB;
	private JTabbedPane tabbedPane;
	ButtonGroup group;
	TextLabelPanel labelPanel;
	ColorPanel colorPanel;
	TransparPanel transparency;
	VertexSymbologyPanel symbologyPanel;
	VertexParametersPanel parametersPanel;
	VertexColorThemingPanel colorThemingPanel;
	public boolean cancelled = false;
	public static ImageIcon ICON = IconLoader.icon("vsicon.gif");

	public VertexSymbolsDialog() {
		super(new JFrame(), I18NPlug.getI18N("VertexSymbols.Dialog") + 
				" - "
				+GenericNames.LAYER+" :"
				+ VertexParams.selectedLayer, true);
		setDefaultCloseOperation(0);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent param1WindowEvent) {
				VertexSymbolsDialog.this. cancelled = true;
				VertexSymbolsDialog. this.dispose();
			}
		});
		setIconImage(ICON.getImage());
		setResizable(true);
		init();
	}

	public Component getTabbedPane() {
		return  tabbedPane;
	}


	public void init() {
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
		toolTipManager.setLightWeightPopupEnabled(false);

		tabbedPane = new JTabbedPane(1);
		symbologyPanel = new VertexSymbologyPanel(VertexParams.selectedLayer.getBasicStyle().getLineColor(), 
				VertexParams.selectedLayer.getBasicStyle().getFillColor());
		parametersPanel = new VertexParametersPanel();
		colorPanel = new ColorPanel(VertexParams.selectedLayer.getBasicStyle().getLineColor(), 
				VertexParams.selectedLayer.getBasicStyle().getFillColor());
		transparency = new TransparPanel((Color)null);
		colorThemingPanel = new VertexColorThemingPanel();
		labelPanel = new TextLabelPanel( group);


		String lineDecorationString ="<html><font color=black size=3>"
				+ "<b>" + I18NPlug.getI18N("VertexSymbols.Dialog.activate-line-decoration") + "</b></html>";
		activateLineDecorationCB =  addCheckBox(lineDecorationString, false);
		activateLineDecorationCB.setSelected(VertexParams.lineDecoration );
		String lineDecorationTooltip = StyleUtils.getName(I18NPlug.getI18N("VertexSymbols.Dialog.activate-line-decoration"), I18NPlug.getI18N("VertexSymbols.Dialog.activate-line-decoration-tooltip"));
		activateLineDecorationCB.setToolTipText(lineDecorationTooltip);
		activateLineDecorationCB.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateGUI();
			}
		});
		JPanel rendererPan = new JPanel(new GridBagLayout());
		JPanel classPan = new JPanel(new GridBagLayout());
		FormUtils.addRowInGBL(rendererPan, 0, 0,  symbologyPanel);
		FormUtils.addRowInGBL(rendererPan, 1, 0,  parametersPanel, true, true);
		FormUtils.addRowInGBL(rendererPan, 2, 0,  colorPanel, true, true);
		//	FormUtils.addRowInGBL(rendererPan, 3, 0, transparency, true, true); 
		symbologyPanel.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateColorPanel();
			}
		});
		FormUtils.addRowInGBL(classPan, 0, 0,  colorThemingPanel);

		tabbedPane.addTab( symbologyPanel.getTitle(), rendererPan);
		tabbedPane.addTab( colorThemingPanel.getTitle(), classPan);
		tabbedPane.addTab( labelPanel.getTitle(),  labelPanel);
		okCancelApplyPanel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent param1ActionEvent) {
				if (VertexSymbolsDialog. this.okCancelApplyPanel.wasOKPressed()) {
					boolean OK = StyleUtils.getValues(VertexSymbolsDialog.this, VertexSymbolsDialog.this. parametersPanel,
							VertexSymbolsDialog. this.symbologyPanel, 
							VertexSymbolsDialog.this. labelPanel, 
							VertexSymbolsDialog. this.colorThemingPanel);
					if (OK) {
						VertexSymbolsDialog. this.changeStyle();
					}
				} else {
					VertexSymbolsDialog. this.cancelled = true;
				}
			}
		});
		addRow(tabbedPane);
		addRow(transparency);
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize =  getPreferredSize();
		setLocation(screenSize.width / 2 - labelSize.width / 2, screenSize.height / 2 - labelSize.height / 2);
		setVisible(true);
	}


	//[Giuseppe Aruta 2020-05-28] While embedded shape and WKT file shape can be colorized according
	// to layer/xbasestyle values/color. Image files have their own colors. This part deactivate Color
	//Panel if user select Image files panel
	public void updateColorPanel() {
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

	//[Giuseppe Aruta 2020-05-28] if Line decoration is activated, only line parameters (distance,
	//offset, and rotate) are activated. Deactivated rotation parameters for vertex (fix value or value
	//by attribute) as it creates confusion for the user

	public void updateGUI() {
		VertexParams.lineDecoration =activateLineDecorationCB.isSelected();
		//	[Giuseppe Aruta 2020-05-28]  updateSideBarIconAndDescription();
		if (VertexParams.lineDecoration) {
			parametersPanel.lineLabel.setEnabled(true);
			parametersPanel.distanceField.setEnabled(true);
			parametersPanel.offsetField.setEnabled(true);
			parametersPanel.distanceLabel.setEnabled(true);
			parametersPanel.offsetLabel.setEnabled(true);
			parametersPanel.rotationCB.setEnabled(true);
			parametersPanel.orienLabel.setEnabled(false);
			parametersPanel.absValueRB.setEnabled(false);
			parametersPanel.byAttributeRB.setEnabled(false);
			parametersPanel.orienField.setText(String.valueOf(0.0));
			parametersPanel.orienField.setEnabled(false);
			parametersPanel.attributeCB.setEnabled(false);
			colorThemingPanel.mainPanel.revalidate();
			revalidate();
			repaint();

		} else {
			parametersPanel.lineLabel.setEnabled(false);
			parametersPanel.distanceField.setEnabled(false);
			parametersPanel.offsetField.setEnabled(false);
			parametersPanel.distanceLabel.setEnabled(false);
			parametersPanel.offsetLabel.setEnabled(false);
			parametersPanel.rotationCB.setEnabled(false);
			parametersPanel.orienLabel.setEnabled(true);
			parametersPanel.orienField.setText(String.valueOf(VertexParams.orientation));
			parametersPanel.orienField.setEnabled(true);
			parametersPanel.absValueRB.setEnabled(true);
			if (VertexParams.byValue) {
				parametersPanel.absValueRB.setSelected(true);
			} else {
				parametersPanel.byAttributeRB.setSelected(true);
			}
			if (!VertexParams.singleLayer) {
				parametersPanel.absValueRB.setSelected(true);
			}
			parametersPanel.byAttributeRB.setEnabled(parametersPanel.bool);
			parametersPanel.attributeCB.setEnabled(parametersPanel.bool);
			colorThemingPanel.mainPanel.revalidate();
			revalidate();
			repaint();
		}
	}


	public void changeStyle() {
		Layer layer = VertexParams.selectedLayer;
		List<String> attributeNameList = layer.getFeatureCollectionWrapper().getFeatureSchema().getAttributeNames();
		layer.getBasicStyle().setFillColor( colorPanel.getFillColor());
		layer.getBasicStyle().setLineColor( colorPanel.getLineColor());
		layer.setSynchronizingLineColor( colorPanel.synchronizeCheckBox.isSelected());
		ColorThemingStyle style = VertexParams.classificationStyle;
		if (style.isEnabled() & attributeNameList.contains(style.getAttributeName())) {
			VertexParams.classification = style.getAttributeName();
		}
		VertexParams.selectedLayer.fireAppearanceChanged();
		VertexParams.selectedLayer.setFeatureCollectionModified(true);
		dispose();
	}

	@Override
	public void stateChanged(ChangeEvent paramChangeEvent) {
	}

	@Override
	public void itemStateChanged(ItemEvent paramItemEvent) {
	}
}
