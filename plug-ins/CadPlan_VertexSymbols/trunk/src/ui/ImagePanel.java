package com.cadplan.jump.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.ImageObserver;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.cadplan.designer.GridBagDesigner;
import com.cadplan.jump.utils.VertexParams;
import com.cadplan.vertices.renderer.style.ImageVertexStyle;

public class ImagePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	ButtonGroup group;
	private JRadioButton[] imageRB;
	int numImages;

	public ImagePanel(ButtonGroup group) {
		this.group = group;
		this.init();
	}

	public void init() {
		if (VertexParams.images != null) {
			this.numImages = VertexParams.images.length;
			this.setImageRB(new JRadioButton[this.numImages]);
			GridBagDesigner gb = new GridBagDesigner(this);

			for(int i = 0; i < this.numImages; ++i) {
				ImageVertexPanel imageVertexPanel = new ImageVertexPanel(VertexParams.imageNames[i]);
				imageVertexPanel.setBackground(Color.WHITE);
				double scale = ((ImageVertexStyle)imageVertexPanel.symbol).getScale();
				if (VertexParams.images[i] != null) {
					imageVertexPanel.setPreferredSize(new Dimension((int)(VertexParams.images[i].getWidth((ImageObserver)null) / scale) + 4, (int)(VertexParams.images[i].getHeight((ImageObserver)null) / scale) + 4));
				} else {
					imageVertexPanel.setPreferredSize(new Dimension(40, 20));
				}

				int row = i % 5;
				int col = i / 5 * 2;
				gb.setPosition(col, row);
				gb.setInsets(5, 10, 5, 0);
				gb.addComponent(imageVertexPanel);
				String name = VertexParams.imageNames[i];
				int k = name.lastIndexOf(".");
				if (k > 0) {
					name = name.substring(0, k);
				}

				this.getImageRB()[i] = new JRadioButton(name);
				this.getImageRB()[i].setBackground(Color.WHITE);
				this.getImageRB()[i].addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent ev) {
						if (ev.getStateChange() == 1) {
							imageVertexPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.red));
						} else if (ev.getStateChange() == 2) {
							imageVertexPanel.setBorder(BorderFactory.createEmptyBorder());
						}

					}
				});
				this.group.add(this.getImageRB()[i]);
				gb.setPosition(col + 1, row);
				gb.setInsets(5, 0, 5, 10);
				gb.setAnchor(17);
				gb.setWeight(1.0D, 1.0D);
				gb.setFill(1);
				gb.addComponent(this.getImageRB()[i]);
			}

		}
	}

	public int getSelectedImage() {
		for(int i = 0; i < this.numImages; ++i) {
			if (this.getImageRB()[i].isSelected()) {
				return i;
			}
		}

		return -1;
	}

	public JRadioButton[] getImageRB() {
		return this.imageRB;
	}

	public void setImageRB(JRadioButton[] imageRB) {
		this.imageRB = imageRB;
	}
}
