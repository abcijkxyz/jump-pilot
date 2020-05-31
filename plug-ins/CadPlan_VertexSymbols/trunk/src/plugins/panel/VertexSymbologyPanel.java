package com.cadplan.jump.plugins.panel;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.openjump.core.ui.plugin.layer.LayerPropertiesPlugIn.PropertyPanel;

import com.cadplan.jump.language.I18NPlug;
import com.cadplan.jump.ui.ImagePanel;
import com.cadplan.jump.ui.VectorPanel;
import com.cadplan.jump.ui.WKTPanel;
import com.cadplan.jump.utils.VertexParams;
import com.vividsolutions.jump.I18N;

public class VertexSymbologyPanel extends JTabbedPane implements PropertyPanel, ChangeListener {
	private static final long serialVersionUID = 1L;
	ButtonGroup group;
	JScrollPane scrollPane;
	public VectorPanel vectorPanel;
	public ImagePanel imagePanel;
	public WKTPanel wktPanel;
	JScrollPane scrollPane2;
	Color lineColor;
	Color fillColor;

	public VertexSymbologyPanel(Color lineColor, Color fillColor) {
		this.fillColor = fillColor;
		this.lineColor = lineColor;
		this.group = new ButtonGroup();
		this.scrollPane = new JScrollPane();
		this.scrollPane2 = new JScrollPane();
		this.addChangeListener(this);
		this.vectorPanel = new VectorPanel(this.group, lineColor, fillColor);
		this.vectorPanel.setBackground(Color.WHITE);
		this.addTab(I18NPlug.getI18N("VertexSymbols.Dialog.Vector"), this.vectorPanel);
		this.wktPanel = new WKTPanel(this.group, lineColor, fillColor);
		this.wktPanel.setBackground(Color.WHITE);
		this.scrollPane2 = new JScrollPane(this.wktPanel);
		this.scrollPane2.setPreferredSize(new Dimension(500, 250));
		this.addTab(I18NPlug.getI18N("VertexSymbols.Dialog.WKTshapes"), this.scrollPane2);
		this.imagePanel = new ImagePanel(this.group);
		this.imagePanel.setBackground(Color.WHITE);
		this.scrollPane = new JScrollPane(this.imagePanel);
		//this.scrollPane.setPreferredSize(new Dimension(400, 300));
		this.addTab(I18NPlug.getI18N("VertexSymbols.Dialog.Image"), this.scrollPane);
		this.setValues();
	}

	public void setValues() {
		int i = this.vectorPanel.symbolPanel.getTypeIndex(VertexParams.symbolNumber, VertexParams.symbolType);
		if (i >= 0) {
			this.vectorPanel.symbolPanel.vertexRB[i].setSelected(true);
			this.setSelectedComponent(this.getComponent(0));
		} else if (VertexParams.selectedImage >= 0) {
			this.imagePanel.getImageRB()[VertexParams.selectedImage].setSelected(true);
			this.setSelectedComponent(this.getComponent(2));
		} else if (VertexParams.selectedWKT >= 0) {
			this.wktPanel.getImageRB()[VertexParams.selectedWKT].setSelected(true);
			this.setSelectedComponent(this.getComponent(1));
		}

	}

	@Override
	public String getTitle() {
		return I18N.get("ui.style.RenderingStylePanel.rendering");
	}

	@Override
	public void updateStyles() {
	}

	@Override
	public String validateInput() {
		return null;
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
	}
}
