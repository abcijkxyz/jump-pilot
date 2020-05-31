package com.cadplan.jump.plugins;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.cadplan.designer.GridBagDesigner;
import com.cadplan.jump.language.I18NPlug;
import com.cadplan.jump.plugins.panel.TextLabelPanel;
import com.cadplan.jump.ui.ImagePanel;
import com.cadplan.jump.ui.VectorPanel;
import com.cadplan.jump.ui.WKTPanel;
import com.cadplan.jump.utils.AttributeManagerUtils;
import com.cadplan.jump.utils.VertexParams;
import com.cadplan.vertices.renderer.style.ExternalSymbolsImplType;
import com.cadplan.vertices.renderer.style.ExternalSymbolsType;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.feature.AttributeType;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureDataset;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.renderer.style.ColorThemingStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.VertexStyle;

public class VertexNoteDialog extends JDialog implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	boolean debug = false;
	PlugInContext context;
	JCheckBox showLabelCB;
	TextArea textArea;
	JButton cancelButton;
	JButton clearButton;
	JButton acceptButton;
	JButton resetButton;
	Layer[] layers;
	String textValue;
	boolean showLabel;
	VertexStyle vertexStyle;
	Feature selectedFeature;
	int textAttributeIndex;
	FeatureDataset dataset;
	boolean allowEdit = true;
	JTabbedPane tabbedPane;
	VectorPanel vectorPanel;
	ImagePanel imagePanel;
	WKTPanel wktPanel;
	TextLabelPanel labelPanel;
	ButtonGroup group;
	ButtonGroup rotateGroup;
	JRadioButton absValueRB;
	JRadioButton byAttributeRB;
	JComboBox attributeCB;
	JScrollPane scrollPane;
	JScrollPane scrollPane2;
	String symbolName = "";
	int symbolType;
	int symbolNumber;
	AttributeManagerUtils manager = new AttributeManagerUtils();

	public VertexNoteDialog(PlugInContext context) {
		super(new JFrame(), I18NPlug.getI18N("VertexNote.Dialog.Editor"), true);
		this.context = context;
		this.layers = context.getSelectedLayers();
		if (this.layers.length <= 1) {
			if (this.layers != null && this.layers.length != 0) {
				if (ColorThemingStyle.get(this.layers[0]).isEnabled()) {
					JOptionPane.showMessageDialog((Component)null, I18NPlug.getI18N("VertexNote.Dialog.Message8"), 
							I18N.get("ui.WorkbenchFrame.warning"), 2);	
					return;
				}

				boolean isEditable = this.layers[0].isEditable();
				if (!isEditable) {
					JOptionPane.showMessageDialog((Component)null, I18NPlug.getI18N("VertexNote.Dialog.Message3"), 
							I18N.get("ui.WorkbenchFrame.warning"), 2);
					return;
				} else {
					this.vertexStyle = this.layers[0].getVertexStyle();
					VertexParams.selectedLayer = this.layers[0];

					try {
						String textAttributeName = null;

						try {
							textAttributeName = ((ExternalSymbolsType)this.vertexStyle).getTextAttributeName();
						} catch (ClassCastException var9) {
							JOptionPane.showMessageDialog((Component)null, I18NPlug.getI18N("VertexNote.Dialog.Message4"), 
									I18N.get("ui.WorkbenchFrame.warning"), 2);
							return;
						}

						this.selectedFeature = this.getSelectedPoint();
						if (this.selectedFeature == null) {
							JOptionPane.showMessageDialog((Component)null, I18NPlug.getI18N("VertexNote.Dialog.Message1"), 
									I18N.get("ui.WorkbenchFrame.warning"), 2);
							return;
						}

						if (this.debug) {
							System.out.println("Feature=" + this.selectedFeature);
						}

						FeatureSchema featureSchema = this.selectedFeature.getSchema();
						if (this.debug) {
							System.out.println("Initial feature size: " + featureSchema.getAttributeCount());
						}

						int i;
						try {
							i = featureSchema.getAttributeIndex("ShowLabel");
							if (this.debug) {
								System.out.println("ShowLabel found at i=" + i);
							}
						} catch (Exception var8) {
							this.manager.addAttribute(this.layers[0], "ShowLabel", AttributeType.INTEGER);
						}

						try {
							i = featureSchema.getAttributeIndex("SymbolName");
							if (this.debug) {
								System.out.println("SymbolName found at i=" + i);
							}
						} catch (Exception var7) {
							this.manager.addAttribute(this.layers[0], "SymbolName", AttributeType.STRING);
						}

						if (textAttributeName.equals("$FID")) {
							this.textAttributeIndex = -1;
						} else if (textAttributeName.equals("$POINT")) {
							this.textAttributeIndex = -2;
						} else {
							try {
								this.textAttributeIndex = featureSchema.getAttributeIndex(textAttributeName);
							} catch (IllegalArgumentException var6) {
								this.textAttributeIndex = -1;
							}
						}

						if (this.textAttributeIndex < 0) {
							this.allowEdit = false;
						}

						((ExternalSymbolsType)this.vertexStyle).setTextAttributeValue(this.selectedFeature);
						this.textValue = ((ExternalSymbolsType)this.vertexStyle).getTextAttributeValue();
						this.showLabel = ((ExternalSymbolsType)this.vertexStyle).getShowNote();
						if (this.vertexStyle instanceof ExternalSymbolsImplType) {
							this.symbolName = ((ExternalSymbolsImplType)this.vertexStyle).getActualSymbolName();
							this.symbolType = ((ExternalSymbolsImplType)this.vertexStyle).getSymbolType();
							this.symbolNumber = ((ExternalSymbolsImplType)this.vertexStyle).getSymbolNumber();
							if (this.debug) {
								System.out.println("Getting current symbol: name=" + this.symbolName + "  number=" + this.symbolNumber + "  type=" + this.symbolType);
							}
						}

						this.init();
					} catch (Exception var10) {
						System.out.println("ERROR: " + var10);
						var10.printStackTrace();
					}

				}
			} else {
				JOptionPane.showMessageDialog((Component)null, I18NPlug.getI18N("VertexNote.Dialog.Message2"), "Warning...", 2);
			}
		}
	}

	public void init() {
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setLightWeightPopupEnabled(false);
		GridBagDesigner gb = new GridBagDesigner(this);
		this.showLabelCB = new JCheckBox(I18NPlug.getI18N("VertexNote.Dialog.ShowLabel"));
		gb.setPosition(0, 0);
		gb.setInsets(10, 10, 0, 0);
		gb.addComponent(this.showLabelCB);
		this.showLabelCB.setSelected(this.showLabel);
		this.textArea = new TextArea("", 5, 50, 1);
		gb.setPosition(1, 0);
		gb.setInsets(10, 0, 0, 10);
		gb.setFill(1);
		gb.setWeight(1.0D, 1.0D);
		gb.addComponent(this.textArea);
		this.textArea.setText(this.textValue);
		this.textArea.setEditable(this.allowEdit);
		JLabel vertexLabel = new JLabel(I18NPlug.getI18N("VertexNote.Dialog.SelectSymbol"));
		gb.setPosition(0, 1);
		gb.setInsets(10, 10, 10, 0);
		gb.setAnchor(11);
		gb.addComponent(vertexLabel);
		this.group = new ButtonGroup();
		this.tabbedPane = new JTabbedPane(1);
		this.tabbedPane.addChangeListener(this);
		this.vectorPanel = new VectorPanel(this.group, VertexParams.selectedLayer.getBasicStyle().getLineColor(), VertexParams.selectedLayer.getBasicStyle().getFillColor());
		this.vectorPanel.setBackground(Color.WHITE);
		this.tabbedPane.addTab(I18NPlug.getI18N("VertexSymbols.Dialog.Vector"), this.vectorPanel);
		this.wktPanel = new WKTPanel(this.group, VertexParams.selectedLayer.getBasicStyle().getLineColor(), VertexParams.selectedLayer.getBasicStyle().getFillColor());
		this.wktPanel.setBackground(Color.WHITE);
		this.scrollPane2 = new JScrollPane(this.wktPanel);
		this.scrollPane2.setPreferredSize(new Dimension(400, 300));
		this.tabbedPane.addTab(I18NPlug.getI18N("VertexSymbols.Dialog.WKTshapes"), this.scrollPane2);
		this.imagePanel = new ImagePanel(this.group);
		this.imagePanel.setBackground(Color.WHITE);
		this.scrollPane = new JScrollPane(this.imagePanel);
		this.scrollPane.setPreferredSize(new Dimension(400, 300));
		this.tabbedPane.addTab(I18NPlug.getI18N("VertexSymbols.Dialog.Image"), this.scrollPane);
		gb.setPosition(1, 1);
		gb.setFill(1);
		gb.setInsets(10, 0, 10, 10);
		gb.addComponent(this.tabbedPane);
		JPanel bottomPanel = new JPanel();
		GridBagDesigner gbb = new GridBagDesigner(bottomPanel);
		this.cancelButton = new JButton(I18NPlug.getI18N("VertexSymbols.Dialog.Cancel"));
		gbb.setPosition(0, 0);
		gbb.setInsets(10, 10, 10, 0);
		gbb.addComponent(this.cancelButton);
		this.cancelButton.addActionListener(this);
		this.clearButton = new JButton(I18NPlug.getI18N("VertexSymbols.Dialog.Clear"));
		gbb.setPosition(1, 0);
		gbb.setInsets(10, 10, 10, 0);
		gbb.addComponent(this.clearButton);
		this.clearButton.addActionListener(this);
		this.resetButton = new JButton(I18NPlug.getI18N("VertexSymbols.Dialog.Reset"));
		gbb.setPosition(2, 0);
		gbb.setInsets(10, 10, 10, 10);
		gbb.addComponent(this.resetButton);
		this.resetButton.addActionListener(this);
		this.acceptButton = new JButton(I18NPlug.getI18N("VertexSymbols.Dialog.Accept"));
		gbb.setPosition(3, 0);
		gbb.setInsets(10, 10, 10, 10);
		gbb.setAnchor(13);
		gbb.addComponent(this.acceptButton);
		this.acceptButton.addActionListener(this);
		gb.setPosition(0, 2);
		gb.setSpan(2, 1);
		gb.addComponent(bottomPanel);
		this.setValues();
		this.pack();
		this.setVisible(true);
	}

	private void setValues() {
		this.setCurrentSymbolName();
	}

	private void getValues() {
		this.symbolName = this.getSymbolName();
		this.symbolType = this.getSymbolType();
	}

	public void setCurrentSymbolName() {
		if (this.debug) {
			System.out.println("Setting current symbol: name=" + this.symbolName + "  number=" + this.symbolNumber + "  type=" + this.symbolType);
		}

		int n = this.vectorPanel.symbolPanel.getTypeIndex(this.symbolNumber, this.symbolType);
		if (n >= 0) {
			this.vectorPanel.symbolPanel.vertexRB[n].setSelected(true);
		} else {
			if (this.symbolType == 4) {
				n = this.vectorPanel.symbolPanel.getImageIndex(this.symbolName);
				if (this.debug) {
					System.out.println("Image update: " + this.symbolName + "  n=" + n);
				}

				if (n >= 0) {
					this.imagePanel.getImageRB()[n].setSelected(true);
					return;
				}
			} else if (this.symbolType == 3) {
				n = this.vectorPanel.symbolPanel.getWKTIndex(this.symbolName);
				if (n >= 0) {
					this.wktPanel.getImageRB()[n].setSelected(true);
				}
			}

		}
	}

	public String getSymbolName() {
		int i;
		for(i = 0; i < this.vectorPanel.symbolPanel.vertexRB.length; ++i) {
			if (this.vectorPanel.symbolPanel.vertexRB[i].isSelected()) {
				if (i < 7) {
					return "@poly" + String.valueOf(this.vectorPanel.symbolPanel.getSides()[i]);
				}

				if (i < 14) {
					return "@star" + String.valueOf(this.vectorPanel.symbolPanel.getSides()[i]);
				}

				return "@any" + String.valueOf(this.vectorPanel.symbolPanel.getSides()[i]);
			}
		}

		for(i = 0; i < this.imagePanel.getImageRB().length; ++i) {
			if (this.imagePanel.getImageRB()[i].isSelected()) {
				return VertexParams.imageNames[i];
			}
		}

		for(i = 0; i < this.wktPanel.getImageRB().length; ++i) {
			if (this.wktPanel.getImageRB()[i].isSelected()) {
				return VertexParams.wktNames[i];
			}
		}

		return null;
	}

	public int getSymbolType() {
		int i;
		for(i = 0; i < this.vectorPanel.symbolPanel.vertexRB.length; ++i) {
			if (this.vectorPanel.symbolPanel.vertexRB[i].isSelected()) {
				if (i < 7) {
					return 0;
				}

				if (i < 14) {
					return 1;
				}

				return 2;
			}
		}

		for(i = 0; i < this.imagePanel.getImageRB().length; ++i) {
			if (this.imagePanel.getImageRB()[i].isSelected()) {
				return 4;
			}
		}

		for(i = 0; i < this.wktPanel.getImageRB().length; ++i) {
			if (this.wktPanel.getImageRB()[i].isSelected()) {
				return 3;
			}
		}

		return -1;
	}

	private Feature getSelectedPoint() {
		Feature feature = null;
		Collection selectedItems = this.context.getLayerViewPanel().getSelectionManager().getFeaturesWithSelectedItems();
		if (this.debug) {
			System.out.println("Number of selected items: " + selectedItems.size());
		}

		if (selectedItems.size() != 1) {
			return null;
		} else {
			Iterator i = selectedItems.iterator();
			if (i.hasNext()) {
				feature = (Feature)i.next();
				Geometry geometry = feature.getGeometry();
				if (this.debug) {
					System.out.println("Geometry: " + geometry.toString());
				}

				return feature;
			} else {
				return null;
			}
		}
	}

	@Override
	public void stateChanged(ChangeEvent ev) {
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		if (ev.getSource() == this.cancelButton) {
			this.dispose();
		}

		FeatureSchema featureSchema;
		if (ev.getSource() == this.acceptButton) {
			int show = 0;
			this.getValues();
			if (this.showLabelCB.isSelected()) {
				show = 1;
			}

			featureSchema = this.layers[0].getFeatureCollectionWrapper().getFeatureSchema();
			if (this.debug) {
				System.out.println("Updated schema: num=" + featureSchema.getAttributeCount());
			}

			int numAtt = this.selectedFeature.getAttributes().length;
			if (this.debug) {
				System.out.println("Num feature att: " + numAtt);
			}

			try {
				this.selectedFeature.setAttribute("ShowLabel", Integer.valueOf(show));
				this.selectedFeature.setAttribute("SymbolName", this.symbolName);
			} catch (IllegalArgumentException var8) {
				JOptionPane.showMessageDialog((Component)null, I18NPlug.getI18N("VertexNote.Dialog.Message3"), "Warning...", 2);
			}

			if (this.textAttributeIndex >= 0) {
				this.selectedFeature.setAttribute(this.textAttributeIndex, this.textArea.getText());
			}

			this.layers[0].fireAppearanceChanged();
			this.dispose();
		}

		if (ev.getSource() == this.clearButton) {
			this.textArea.setText("");
		}

		if (ev.getSource() == this.resetButton) {
			int response = JOptionPane.showConfirmDialog(this, I18NPlug.getI18N("VertexSymbols.Dialog.Warning5"), "Warning...", 2);
			if (response == 2) {
				return;
			}

			featureSchema = this.selectedFeature.getSchema();

			try {
				this.manager.delAttribute(this.layers[0], "ShowLabel");
			} catch (Exception var7) {
			}

			try {
				this.manager.delAttribute(this.layers[0], "SymbolName");
			} catch (Exception var6) {
			}

			this.dispose();
		}

	}
}
