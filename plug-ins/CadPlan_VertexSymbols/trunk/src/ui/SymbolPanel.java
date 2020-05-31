package com.cadplan.jump.ui;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.cadplan.designer.GridBagDesigner;
import com.cadplan.jump.plugins.panel.VertexSymbologyPanel;
import com.cadplan.jump.utils.VertexParams;

public class SymbolPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	int numSymbols = 21;
	public JRadioButton[] vertexRB;
	JPanel[] symbol;
	public int[] sides;
	ButtonGroup group;
	String name;
	Color lineColor, fillColor;

	public SymbolPanel(ButtonGroup group,  Color lineColor, Color fillColor) {
		this.vertexRB = new JRadioButton[this.numSymbols];
		this.symbol = new VertexPanel[this.numSymbols];
		this.sides = new int[]{2, 3, 4, 5, 6, 8, 16, 2, 3, 4, 5, 6, 8, 16, 0, 1, 2, 3, 4, 5, 6};
		this.group = group;
		this.lineColor = lineColor;
		this.fillColor = fillColor;
		this.init();
	}

	public int getIndex(int n, int type) {
		for(int i = 0; i < this.getSides().length; ++i) {
			if (i < 7 && type == VertexParams.POLYGON && this.getSides()[i] == n) {
				return i;
			}

			if (i >= 7 && type == VertexParams.STAR && this.getSides()[i] == n) {
				return i;
			}

			if (i >= 14 && type == VertexParams.ANYSHAPE && this.getSides()[i] == n) {
				return i;
			}
		}

		return -1;
	}

	public int getTypeIndex(int n, int type) {
		for(int i = 0; i < this.getSides().length; ++i) {
			if (i < 7 && type == 0 && this.getSides()[i] == n) {
				return i;
			}

			if (i >= 7 && type == 1 && this.getSides()[i] == n) {
				return i;
			}

			if (i >= 14 && type == 2 && this.getSides()[i] == n) {
				return i;
			}
		}

		return -1;
	}

	public int getImageIndex(String name) {
		int index = -1;

		for(int i = 0; i < VertexParams.imageNames.length; ++i) {
			if (VertexParams.imageNames[i].equals(name)) {
				return i;
			}
		}

		return index;
	}

	public int getWKTIndex(String name) {
		int index = -1;

		for(int i = 0; i < VertexParams.wktNames.length; ++i) {
			if (VertexParams.wktNames[i].equals(name)) {
				return i;
			}
		}

		return index;
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

	public void init() {
		GridBagDesigner gb = new GridBagDesigner(this);
		int col = 0;
		int row = 0;

		for(int i = 0; i < this.numSymbols; ++i) {
			if (i < 7) {
				this.name = "@poly" + this.getSides()[i];
			} else if (i != 7 && !(i > 7 & i < 14)) {
				if (i == 14 || i > 14) {
					this.name = "@any" + this.getSides()[i];
				}
			} else {
				this.name = "@star" + this.getSides()[i];
			}

			col = 2 * (i / 7);
			row = i % 7;
			this.vertexRB[i] = new JRadioButton(this.name);
			this.vertexRB[i].setBackground(Color.WHITE);
			this.group.add(this.vertexRB[i]);
			gb.setPosition(col + 1, row);
			gb.setInsets(0, 10, 10, 30);
			gb.setAnchor(17);
			gb.addComponent(this.vertexRB[i]);
			if (i < 7) {
				this.symbol[i] = new VertexPanel(this.getSides()[i], VertexParams.POLYGON, lineColor, fillColor);
			} else if (i < 14) {
				this.symbol[i] = new VertexPanel(this.getSides()[i], VertexParams.STAR,lineColor, fillColor);
			} else {
				this.symbol[i] = new VertexPanel(this.getSides()[i], VertexParams.ANYSHAPE, lineColor, fillColor);
			}

			gb.setPosition(col, row);
			JPanel pan = this.symbol[i];
			this.vertexRB[i].addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent ev) {
					if (ev.getStateChange() == 1) {
						pan.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.red));
					} else if (ev.getStateChange() == 2) {
						pan.setBorder(BorderFactory.createEmptyBorder());
					}

				}
			});
			gb.addComponent(pan);




		}

	}

	public int[] getSides() {
		return this.sides;
	}

	public void setSides(int[] sides) {
		this.sides = sides;
	}
}
