package com.cadplan.jump.plugins.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.cadplan.jump.utils.StyleUtils;
import com.cadplan.jump.utils.VertexParams;
import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.workbench.ui.GUIUtil;
import com.vividsolutions.jump.workbench.ui.TransparencyPanel;
import com.vividsolutions.jump.workbench.ui.style.StylePanel;

public class TransparPanel extends JPanel implements StylePanel {
	JLabel transparencyLabel;
	protected TransparencyPanel transparencyPanel = new TransparencyPanel();
	private static final long serialVersionUID = 1L;
	static final boolean shouldFill = true;
	static final boolean shouldWeightX = true;
	public JPanel lowerPane;
	public JPanel sizePane;
	boolean addSyncronized;
	boolean addTransaprencyPane;



	public TransparPanel(Color color) {
		GridLayout layout = new GridLayout(0, 1);
		this.setLayout(layout);
		this.transparencyLabel = new JLabel(I18N.get("ui.style.BasicStylePanel.transparency"));
		this.transparencyPanel.getSlider().getModel().addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				StyleUtils.setAlpha(VertexParams.selectedLayer, 255 - TransparPanel.this.transparencyPanel.getSlider().getValue());
				VertexParams.selectedLayer.fireAppearanceChanged();
			}
		});
		JPanel fillPane = new JPanel();
		this.transparencyPanel.setPreferredSize(new Dimension(200, 15));
		this.transparencyPanel.setMaximumSize(new Dimension(200, 15));
		this.transparencyPanel.setMinimumSize(new Dimension(200, 15));
		this.transparencyPanel.setColor(color);
		this.lowerPane = new JPanel(new GridBagLayout());
		this.lowerPane.add(this.transparencyLabel, new GridBagConstraints(0, 1, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0));
		this.lowerPane.add(this.transparencyPanel, new GridBagConstraints(1, 1, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0));
		this.lowerPane.add(GUIUtil.createSyncdTextField(this.transparencyPanel.getSlider(), 3), new GridBagConstraints(2, 1, 1, 1, 0.0D, 0.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0));
		this.lowerPane.add(fillPane, new GridBagConstraints(3, 1, 0, 0, 1000.0D, 1000.0D, 17, 0, new Insets(2, 2, 2, 2), 0, 0));

		this.add(this.lowerPane);
		this.setValues();
	}

	public JPanel getTrasparencyPanel() {
		return this.lowerPane;
	}

	public void setValues() {
		this.transparencyPanel.getSlider().setValue(255 - StyleUtils.getAlpha(VertexParams.selectedLayer));
	}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public void updateStyles() {
	}

	@Override
	public String validateInput() {
		return null;
	}
}
