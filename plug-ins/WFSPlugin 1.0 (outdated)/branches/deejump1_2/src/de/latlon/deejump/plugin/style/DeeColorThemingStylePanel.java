/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */
package de.latlon.deejump.plugin.style;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.util.CollectionUtil;
import com.vividsolutions.jump.util.Range;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.model.LayerManager;
import com.vividsolutions.jump.workbench.plugin.AbstractPlugIn;
import com.vividsolutions.jump.workbench.plugin.EnableCheck;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.ColorPanel;
import com.vividsolutions.jump.workbench.ui.EnableableToolBar;
import com.vividsolutions.jump.workbench.ui.GUIUtil;
import com.vividsolutions.jump.workbench.ui.OKCancelPanel;
import com.vividsolutions.jump.workbench.ui.images.IconLoader;
import com.vividsolutions.jump.workbench.ui.renderer.style.BasicStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.BasicStyleListCellRenderer;
import com.vividsolutions.jump.workbench.ui.renderer.style.ColorScheme;
import com.vividsolutions.jump.workbench.ui.renderer.style.ColorSchemeListCellRenderer;
import com.vividsolutions.jump.workbench.ui.renderer.style.ColorThemingStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.ColorThemingStylePanel;
import com.vividsolutions.jump.workbench.ui.renderer.style.DiscreteColorThemingState;
import com.vividsolutions.jump.workbench.ui.renderer.style.SquareVertexStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.Style;
import com.vividsolutions.jump.workbench.ui.renderer.style.VertexStyle;
import com.vividsolutions.jump.workbench.ui.renderer.style.ColorThemingStylePanel.State;
import com.vividsolutions.jump.workbench.ui.style.BasicStylePanel;
import com.vividsolutions.jump.workbench.ui.style.StylePanel;


public class DeeColorThemingStylePanel extends JPanel implements StylePanel {
//	GH 2005.09.22
	VertexStyleChooser vertexStyleChooser = new VertexStyleChooser();
	private static final String CUSTOM_ENTRY = I18N.get("ui.renderer.style.ColorThemingPanel.custom");
    public static final String TITLE = I18N.get("ui.renderer.style.ColorThemingPanel.colour-theming");
    public static final String COLOR_SCHEME_KEY = DeeColorThemingStylePanel.class.getName() +
        " - COLOR SCHEME";
    private WorkbenchContext workbenchContext;
    private Layer layer;
    private GridBagLayout gridBagLayout1 = new GridBagLayout();
    private JPanel jPanel1 = new JPanel();
    private JScrollPane scrollPane = new JScrollPane();
    private DefaultTableCellRenderer allOtherValuesRenderer = new DefaultTableCellRenderer();
    private JTable table = new JTable() {
            public TableCellRenderer getCellRenderer(int row, int column) {
                TableCellRenderer renderer = getCellRendererProper(row, column);

                if (renderer instanceof JComponent) {
                    updateBackground((JComponent) renderer);
                }

                return renderer;
            }

            private TableCellRenderer getCellRendererProper(int row, int column) {
                if ((row == 0) && (column == attributeColumn())) {
                    return allOtherValuesRenderer;
                }
                if ((row == 0) && (column == labelColumn())) {
                    return allOtherValuesRenderer;
                }
                TableCellRenderer renderer = super.getCellRenderer(row, column);

                if (renderer instanceof JLabel) {
                    ((JLabel) renderer).setHorizontalAlignment(JLabel.LEFT);
                }

                return renderer;
            }
        };

    private JPanel jPanel3 = new JPanel();
    private GridBagLayout gridBagLayout2 = new GridBagLayout();
    private GridBagLayout gridBagLayout4 = new GridBagLayout();
    private JComboBox attributeNameComboBox = new JComboBox();
    private JLabel attributeLabel = new JLabel();
    private JLabel statusLabel = new JLabel() {
            public void setText(String text) {
                super.setText(text);
                setToolTipText(text);
            }
        };

    private EnableableToolBar toolBar = new EnableableToolBar();
    private JPanel jPanel4 = new JPanel();
    private GridBagLayout gridBagLayout5 = new GridBagLayout();
    private JComboBox colorSchemeComboBox = new JComboBox();
    private JLabel colorSchemeLabel = new JLabel();
    private boolean updatingComponents = false;
    private boolean initializing = false;
    private BasicStyleListCellRenderer basicStyleListCellRenderer = new BasicStyleListCellRenderer();
    public BasicStylePanel basicStylePanel;
    private TableCellEditor basicStyleTableCellEditor = new TableCellEditor() {
            private DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
            private BasicStyle originalStyle;
            private DefaultCellEditor editor;
            private JComboBox comboBox = new JComboBox(comboBoxModel) {
                    public void setSelectedItem(Object anObject) {
                        if (anObject != CUSTOM_ENTRY) {
                            super.setSelectedItem(anObject);

                            return;
                        }
                        Style [] styles = promptBasicStyle(originalStyle);
                        VertexStyle vertexStyle = (VertexStyle) styles[1];
                        BasicStyle  style = (BasicStyle) styles[0];
                        if (style == null) {
                            return;
                        }
                                                
                        // this is never ever going to cause anything
                        //ColorThemingTableModel obj = new ColorThemingTableModel();
                        
                    	((DeeColorThemingTableModel)table.getModel())
                    		.setVertexStyleAt(vertexStyle, table.getSelectedRow());

                       

                        comboBox.addItem(style);
                        super.setSelectedItem(style);
                    }

					
                };

            {
                comboBox.setRenderer(basicStyleListCellRenderer);
                editor = new DefaultCellEditor(comboBox);
            }

            public Component getTableCellEditorComponent(JTable table,
                Object  value, boolean isSelected, int row, int column) {
            	
                originalStyle = (BasicStyle) value;
                comboBoxModel.removeAllElements();
                comboBoxModel.addElement(CUSTOM_ENTRY);

                //Include the current BasicStyle; otherwise, the current BasicStyle
                //will not show up as the selected item in the combo box. [Jon Aquino]
                comboBoxModel.addElement(value);

                for (Iterator i = ColorScheme.create(
                            (String) colorSchemeComboBox.getSelectedItem())
                                             .getColors().iterator();
                        i.hasNext();) {
                    Color color = (Color) i.next();
                    comboBoxModel.addElement(new BasicStyle(color));
                }

                comboBoxModel.setSelectedItem(value);

                return editor.getTableCellEditorComponent(table, value,
                    isSelected, row, column);
            }

            public Object getCellEditorValue() {
                return editor.getCellEditorValue();
            }

            public boolean isCellEditable(EventObject anEvent) {
                return editor.isCellEditable(anEvent);
            }

            public boolean shouldSelectCell(EventObject anEvent) {
                return editor.shouldSelectCell(anEvent);
            }

            public boolean stopCellEditing() {
                return editor.stopCellEditing();
            }

            public void cancelCellEditing() {
                editor.cancelCellEditing();
            }

            public void addCellEditorListener(CellEditorListener l) {
                editor.addCellEditorListener(l);
            }

            public void removeCellEditorListener(CellEditorListener l) {
                editor.removeCellEditorListener(l);
            }
        };

    private DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
    private JPanel fillerPanel = new JPanel();
    private String lastAttributeName;
    private ColorScheme colorSchemeForInserts = null;
    private MyPlugIn insertPlugIn = new MyPlugIn() {
            public String getName() {
                return I18N.get("ui.renderer.style.ColorThemingPanel.insert-row");
            }

            public Icon getIcon() {
                return GUIUtil.toSmallIcon(IconLoader.icon("Plus.gif"));
            }

            public boolean execute(PlugInContext context)
                throws Exception {
                reportNothingToUndoYet(context);
                stopCellEditing();
                tableModel().insertAttributeValue((table.getSelectedRowCount() > 0)
                    ? table.getSelectedRows()[0] : table.getRowCount(),
                    getColorSchemeForInserts());

                if (table.getSelectedRowCount() == 0) {
                    table.scrollRectToVisible(table.getCellRect(table.getRowCount() -
                            1, 0, true));
                }

                if (table.getSelectedRowCount() != 0) {
                    int firstSelectedRow = table.getSelectedRows()[0];
                    table.clearSelection();
                    table.addRowSelectionInterval(firstSelectedRow,
                        firstSelectedRow);
                    
                }

                return true;
            }
        };

    private MyPlugIn deletePlugIn = new MyPlugIn() {
            public String getName() {
                return I18N.get("ui.renderer.style.ColorThemingPanel.delete-row");
            }

            public Icon getIcon() {
                return GUIUtil.toSmallIcon(IconLoader.icon("Delete.gif"));
            }

            public boolean execute(PlugInContext context)
                throws Exception {
                reportNothingToUndoYet(context);
                stopCellEditing();
                tableModel().removeAttributeValues(table.getSelectedRows());

                return true;
            }
        };

    //Maintain a blackboard for error messages instead of running all
    //validations whenever we need to check for messages -- some validations
    //may be expensive. [Jon Aquino]
    private HashSet errorMessages = new HashSet();
    private DiscreteColorThemingState discreteColorThemingState = new DiscreteColorThemingState(table);
    private DeeRangeColorThemingState rangeColorThemingState;
    private ColorThemingStylePanel.State state = discreteColorThemingState;
    private JPanel jPanel5 = new JPanel();
    private GridBagLayout gridBagLayout3 = new GridBagLayout();
    private JCheckBox enableColorThemingCheckBox = new JCheckBox();
    private JCheckBox byRangeCheckBox = new JCheckBox();
    private JSlider transparencySlider = new JSlider();

    public DeeColorThemingStylePanel(Layer layer, WorkbenchContext workbenchContext) {
        initializing = true;

        try {
            basicStylePanel = new BasicStylePanel(workbenchContext.getWorkbench()
                                                                  .getBlackboard(),
                    ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
            this.layer = layer;
            this.workbenchContext = workbenchContext;
            rangeColorThemingState = new DeeRangeColorThemingState(this);
            jbInit();
            byRangeCheckBox.setSelected(colorThemingStyleHasRanges(layer));

            //Must set state early so that #initTable can call #fromExternalFormat. [Jon Aquino]
            state = byRangeCheckBox.isSelected()
                ? (ColorThemingStylePanel.State) rangeColorThemingState : discreteColorThemingState;

            //Init the table before calling #setState, in which
            //RangeColorThemingState will add itself as a listener
            //to the table model. [Jon Aquino]
            initTable(layer);
            setState(state);
            initAttributeNameComboBox(layer);
            initColorSchemeComboBox(layer.getLayerManager());
            initTransparencySlider(layer);
            initToolBar();
            enableColorThemingCheckBox.setSelected(DeeColorThemingStyle.get(layer)
                                                                    .isEnabled());
            updateComponents();
            GUIUtil.sync(basicStylePanel.getTransparencySlider(),
                transparencySlider);
            basicStylePanel.setSynchronizingLineColor(layer.isSynchronizingLineColor());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            initializing = false;
        }
    }

    private void updateBackground(JComponent component) {
        component.setBackground(enableColorThemingCheckBox.isSelected()
            ? Color.white : jPanel1.getBackground());
    }

    private int attributeColumn() {
        return table.convertColumnIndexToView(DeeColorThemingTableModel.ATTRIBUTE_COLUMN);
    }
    private int labelColumn() {
        return table.convertColumnIndexToView(DeeColorThemingTableModel.LABEL_COLUMN);
    }
    private int colorColumn() {
        return table.convertColumnIndexToView(DeeColorThemingTableModel.COLOR_COLUMN);
    }

    public String getTitle() {
        return TITLE;
    }

    public void updateStyles() {
        boolean firingEvents = layer.getLayerManager().isFiringEvents();
        layer.getLayerManager().setFiringEvents(false);

        try {
            ////[UT] 31.10.2005 must remove ColorThemingStyle here, because panel is not
            // being called for points and ColorThem Style was first of list
            // xsl doesn't want it so: 	<xsl:param name="defaultFillColor" 
            //select="xslutil:toHexColor(/layer/styles/style[1]/fill/color)"/>

            layer.removeStyle(ColorThemingStyle.get(layer));
            
            layer.removeStyle(DeeColorThemingStyle.get(layer));
            layer.addStyle(new DeeColorThemingStyle(getAttributeName(), state
                    .toExternalFormat(tableModel()
                            .getAttributeValueToBasicStyleMap()), state
                    .toExternalFormat(tableModel()
                            .getAttributeValueToLabelMap()), state
					.toExternalFormat(tableModel()
							.getAttributeValueToVertexStyleMap()), tableModel()		
                    .getDefaultStyle()));
            DeeColorThemingStyle.get(layer).setAlpha(getAlpha());
            DeeColorThemingStyle.get(layer).setEnabled(
                    enableColorThemingCheckBox.isSelected());
            layer.getBasicStyle().setEnabled(
                    !enableColorThemingCheckBox.isSelected());
        } finally {
            layer.getLayerManager().setFiringEvents(firingEvents);
        }
        layer.fireAppearanceChanged();
    }

    private String getAttributeName() {
        return (String) attributeNameComboBox.getSelectedItem();
    }

    private void stopCellEditing() {
        if (table.getCellEditor() instanceof DefaultCellEditor) {
            ((DefaultCellEditor) table.getCellEditor()).stopCellEditing();
        }
    }

    public JCheckBox getSynchronizeCheckBox() {
        return basicStylePanel.getSynchronizeCheckBox();
    }

    public Layer getLayer() {
        return layer;
    }

    private void initTransparencySlider(Layer layer) {
        transparencySlider.setValue(transparencySlider.getMaximum() -
            DeeColorThemingStyle.get(layer).getDefaultStyle().getAlpha());
        transparencySlider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    basicStyleListCellRenderer.setAlpha(getAlpha());
                }
            });
        basicStyleListCellRenderer.setAlpha(getAlpha());
    }

    private boolean colorThemingStyleHasRanges(Layer layer) {
        return !DeeColorThemingStyle.get(layer).getAttributeValueToBasicStyleMap()
                                 .isEmpty() &&
        DeeColorThemingStyle.get(layer).getAttributeValueToBasicStyleMap().keySet()
                         .iterator().next() instanceof Range;
    }

    private void initToolBar() {
        EnableCheck atLeast1RowMustBeSelectedCheck = new EnableCheck() {
                public String check(JComponent component) {
                    return (table.getSelectedRowCount() == 0)
                    ? I18N.get("ui.renderer.style.ColorThemingPanel.at-least-1-row-must-be-selected") : null;
                }
            };

        EnableCheck layerMustHaveAtLeast1AttributeCheck = new EnableCheck() {
                public String check(JComponent component) {
                    return (attributeNameComboBox.getItemCount() == 0)
                    ? I18N.get("ui.renderer.style.ColorThemingPanel.layer-must-have-at-least-1-attribute") : null;
                }
            };

        EnableCheck colorThemingMustBeEnabledCheck = new EnableCheck() {
                public String check(JComponent component) {
                    return (!enableColorThemingCheckBox.isSelected())
                    ? I18N.get("ui.renderer.style.ColorThemingPanel.colour-theming-must-be-enabled") : null;
                }
            };

        addPlugIn(insertPlugIn,
            new MultiEnableCheck().add(layerMustHaveAtLeast1AttributeCheck).add(colorThemingMustBeEnabledCheck));
        addPlugIn(deletePlugIn,
            new MultiEnableCheck().add(layerMustHaveAtLeast1AttributeCheck)
                                  .add(atLeast1RowMustBeSelectedCheck).add(colorThemingMustBeEnabledCheck));
    }

    private void addPlugIn(MyPlugIn plugIn, EnableCheck enableCheck) {
        JButton button = new JButton();
        toolBar.add(button, plugIn.getName(), plugIn.getIcon(),
            AbstractPlugIn.toActionListener(plugIn, workbenchContext, null),
            enableCheck);
    }

    private void updateComponents() {
        if (updatingComponents) {
            return;
        }

        updatingComponents = true;

        try {
            attributeLabel.setEnabled(enableColorThemingCheckBox.isSelected());
            attributeNameComboBox.setEnabled(enableColorThemingCheckBox.isSelected());
            state.getPanel().setEnabled(enableColorThemingCheckBox.isSelected() &&
                (attributeNameComboBox.getItemCount() > 0));
            colorSchemeLabel.setEnabled(enableColorThemingCheckBox.isSelected() &&
                (attributeNameComboBox.getItemCount() > 0));
            byRangeCheckBox.setEnabled(enableColorThemingCheckBox.isSelected() &&
                (attributeNameComboBox.getItemCount() > 0));
            colorSchemeComboBox.setEnabled(enableColorThemingCheckBox.isSelected() &&
                (attributeNameComboBox.getItemCount() > 0));
            table.setEnabled(enableColorThemingCheckBox.isSelected() &&
                (attributeNameComboBox.getItemCount() > 0));
            scrollPane.setEnabled(enableColorThemingCheckBox.isSelected() &&
                (attributeNameComboBox.getItemCount() > 0));
            transparencySlider.setEnabled(enableColorThemingCheckBox.isSelected() &&
                (attributeNameComboBox.getItemCount() > 0));
            statusLabel.setEnabled(enableColorThemingCheckBox.isSelected());
            toolBar.updateEnabledState();

            //Give the "Cannot colour-theme layer" message priority. [Jon Aquino]
            if (!setErrorMessage(
                        new ErrorMessage(
                                I18N.get("ui.renderer.style.ColorThemingPanel.cannot-colour-theme-layer-with-no-attributes")),
                        attributeNameComboBox.getItemCount() == 0)) {
                setErrorMessage(new ErrorMessage(I18N.get("ui.renderer.style.ColorThemingPanel.table-must-not-be-empty")),
                    table.getRowCount() == 0);
            }

            updateErrorDisplay();

            if (table.getColumnCount() > 0) {
                //Column count == 0 during initialization. [Jon Aquino]
                table.getColumnModel()
                     .getColumn(table.convertColumnIndexToView(
                         DeeColorThemingTableModel.ATTRIBUTE_COLUMN))
                     .setHeaderValue(state.getAttributeValueColumnTitle());
            }
        } finally {
            updatingComponents = false;
        }
    }

    /**
     * @return null if user hits Cancel
     */
    private Style[] promptBasicStyle(BasicStyle basicStyle) {
        int originalTransparencySliderValue = transparencySlider.getValue();
        basicStylePanel.setBasicStyle(basicStyle);
        basicStylePanel.getTransparencySlider().setValue(originalTransparencySliderValue);

        OKCancelPanel okCancelPanel = new OKCancelPanel();
        final JDialog dialog = new JDialog((JDialog) SwingUtilities.windowForComponent(
                    this), I18N.get("ui.renderer.style.ColorThemingPanel.custom"), true);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(basicStylePanel, BorderLayout.NORTH);
        dialog.getContentPane().add( vertexStyleChooser  , BorderLayout.WEST);
        dialog.getContentPane().add(okCancelPanel, BorderLayout.SOUTH);
        okCancelPanel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                }
            });
        dialog.pack();
        GUIUtil.centreOnWindow(dialog);
        dialog.setVisible(true);

        if (!okCancelPanel.wasOKPressed()) {
            transparencySlider.setValue(originalTransparencySliderValue);
        }
        Style style [] = new Style[2];
        style[0] = basicStylePanel.getBasicStyle();style[1] = vertexStyleChooser.getSelectedStyle();
        return okCancelPanel.wasOKPressed() ? style
        		: null;
    }
    //GH 2005.10.17


    private void initTable(Layer layer) {
        table.setModel(new DeeColorThemingTableModel(DeeColorThemingStyle.get(layer)
                                                                   .getDefaultStyle(),
                DeeColorThemingStyle.get(layer).getAttributeName(),
                attributeValueToBasicStyleMap(layer),
                attributeValueToLabelMap(layer), 
                attributeValueToVertexStyleMap(layer),
                layer.getFeatureCollectionWrapper().getFeatureSchema()) {
                public Object getValueAt(int rowIndex, int columnIndex) {
                    //Don't use #attributeColumn() here because this is in the *model*,
                    //not the view. Otherwise, an exception will be thrown when
                    //the user tries to move the columns. [Jon Aquino]
                    if ((rowIndex == 0) &&
                            (columnIndex == DeeColorThemingTableModel.ATTRIBUTE_COLUMN)) {
                        return state.getAllOtherValuesDescription();
                    }
                    if ((rowIndex == 0) &&
                            (columnIndex == DeeColorThemingTableModel.LABEL_COLUMN)) {
                        return "";
                    }
                    return super.getValueAt(rowIndex, columnIndex);
                }
            });
        table.createDefaultColumnsFromModel();
        table.setRowSelectionAllowed(true);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    updateComponents();
                }
            });
        table.getTableHeader().addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e) &&
                            (e.getClickCount() == 1) && table.isEnabled()) {
                        tableModel().sort();
                    }
                }
            });
        table.getColumnModel().getColumn(colorColumn()).setCellRenderer(new TableCellRenderer() {
                public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                	
                    JComponent renderer = (JComponent) basicStyleListCellRenderer.getListCellRendererComponent(new JList(),
                            value, row, isSelected, hasFocus);
                    
                    
                    
                    if (!isSelected) {
                        updateBackground(renderer);
                    }

                    return renderer;
                }
            });
        table.getColumnModel().getColumn(colorColumn()).setCellEditor(basicStyleTableCellEditor);
        table.getModel().addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent e) {
                    updateComponents();

                    Object duplicateAttributeValue = tableModel()
                                                         .findDuplicateAttributeValue();
                    setErrorMessage(new ErrorMessage(
                            I18N.get("ui.renderer.style.ColorThemingPanel.table-must-not-have-duplicate-attribute-values"),
                            "(" + duplicateAttributeValue + ")"),
                        duplicateAttributeValue != null);
                    setErrorMessage(new ErrorMessage(
                            I18N.get("ui.renderer.style.ColorThemingPanel.table-must-not-have-null-attribute-values")),
                        tableModel().containsNullAttributeValues());
                }
            });

        int colorWidth = 10 +
            (int) basicStyleListCellRenderer.getListCellRendererComponent(new JList(),
                new BasicStyle(), 0, false, false).getPreferredSize().getWidth();
        table.getColumnModel().getColumn(colorColumn()).setPreferredWidth(colorWidth);
        table.getColumnModel().getColumn(colorColumn()).setMinWidth(colorWidth);
        table.getColumnModel().getColumn(colorColumn()).setMaxWidth(colorWidth);
    }

    /**
	 * @param layer2
	 * @return
	 */
	protected Map attributeValueToVertexStyleMap(Layer layer2) {
		if (!colorThemingAttributeValid(layer)) {
            return new TreeMap();
        }
        return state.fromExternalFormat(DeeColorThemingStyle.get(layer)
                                                         .getAttributeValueToVertexStyleMap());
	}

	private Map attributeValueToBasicStyleMap(Layer layer) {
        if (!colorThemingAttributeValid(layer)) {
            return new TreeMap();
        }
        return state.fromExternalFormat(DeeColorThemingStyle.get(layer)
                                                         .getAttributeValueToBasicStyleMap());
    }
    private Map attributeValueToLabelMap(Layer layer) {
        if (!colorThemingAttributeValid(layer)) {
            return new TreeMap();
        }
        return state.fromExternalFormat(DeeColorThemingStyle.get(layer)
                                                         .getAttributeValueToLabelMap());
    }
    private boolean colorThemingAttributeValid(Layer layer) {
        if (DeeColorThemingStyle.get(layer).getAttributeName() == null) { return false;}
        //Schema won't have attribute name if user has deleted the attribute.
        //[Jon Aquino]
        if (!layer.getFeatureCollectionWrapper().getFeatureSchema()
          .hasAttribute(DeeColorThemingStyle.get(layer)
                                             .getAttributeName())) { return false;}
        return true;
    }

    private void initColorSchemeComboBox(LayerManager layerManager) {
        colorSchemeComboBox.setRenderer(new ColorSchemeListCellRenderer() {
                protected void color(ColorPanel colorPanel, Color fillColor,
                    Color lineColor) {
                    super.color(colorPanel,
                        GUIUtil.alphaColor(fillColor, getAlpha()),
                        GUIUtil.alphaColor(lineColor, getAlpha()));
                }

                protected ColorScheme colorScheme(String name) {
                    return state.filterColorScheme(super.colorScheme(name));
                }
            });
    }

    private int getAlpha() {
        return transparencySlider.getMaximum() - transparencySlider.getValue();
    }

    private void initAttributeNameComboBox(Layer layer) {
        DefaultComboBoxModel model = new DefaultComboBoxModel();

        for (int i = 0;
                i < layer.getFeatureCollectionWrapper().getFeatureSchema()
                             .getAttributeCount(); i++) {
            if (i == layer.getFeatureCollectionWrapper().getFeatureSchema()
                              .getGeometryIndex()) {
                continue;
            }

            model.addElement(layer.getFeatureCollectionWrapper()
                                  .getFeatureSchema().getAttributeName(i));
        }

        attributeNameComboBox.setModel(model);

        if (model.getSize() == 0) {
            //Can get here if the only attribute is the geometry. [Jon Aquino]
            return;
        }

        attributeNameComboBox.setSelectedItem(DeeColorThemingStyle.get(layer)
                                                               .getAttributeName());
    }

    private void jbInit() throws Exception {
        this.setLayout(gridBagLayout1);
        jPanel1.setLayout(gridBagLayout2);
        jPanel3.setLayout(gridBagLayout4);
        attributeLabel.setText(I18N.get("ui.renderer.style.ColorThemingPanel.attribute")+" ");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        statusLabel.setText(" ");
        jPanel4.setLayout(gridBagLayout5);
        colorSchemeLabel.setText(I18N.get("ui.renderer.style.ColorThemingPanel.colour-scheme")+" ");
        attributeNameComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    attributeNameComboBox_actionPerformed(e);
                }
            });
        colorSchemeComboBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    colorSchemeComboBox_actionPerformed(e);
                }
            });
        jPanel5.setLayout(gridBagLayout3);
        enableColorThemingCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    enableColorThemingCheckBox_actionPerformed(e);
                }
            });
        enableColorThemingCheckBox.setText(I18N.get("ui.renderer.style.ColorThemingPanel.enable-colour-theming"));
        byRangeCheckBox.setText(I18N.get("ui.renderer.style.ColorThemingPanel.by-range"));
        byRangeCheckBox.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    byRangeCheckBox_actionPerformed(e);
                }
            });
        transparencySlider.setMaximum(255);
        transparencySlider.setPreferredSize(new Dimension(75, 24));

        //Don't get squished by overlong status messages. [Jon Aquino]
        transparencySlider.setMinimumSize(new Dimension(75, 24));
        transparencySlider.setToolTipText(I18N.get("ui.renderer.style.ColorThemingPanel.transparency"));
        transparencySlider.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    transparencySlider_stateChanged(e);
                }
            });
        this.add(jPanel1,
            new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        jPanel1.add(attributeNameComboBox,
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(2, 2, 2, 2), 0, 0));
        jPanel1.add(attributeLabel,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(2, 2, 2, 2), 0, 0));
        jPanel1.add(fillerPanel,
            new GridBagConstraints(4, 0, 1, 1, 1, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(2, 2, 2, 2), 0, 0));
        this.add(scrollPane,
            new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(2, 2, 2, 2), 0, 0));
        this.add(jPanel3,
            new GridBagConstraints(0, 5, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        jPanel3.add(statusLabel,
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        jPanel3.add(toolBar,
            new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        jPanel3.add(transparencySlider,
            new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        this.add(jPanel4,
            new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0));
        jPanel4.add(colorSchemeComboBox,
            new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(2, 2, 2, 2), 0, 0));
        jPanel4.add(colorSchemeLabel,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(2, 2, 2, 2), 0, 0));
        this.add(jPanel5,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        jPanel5.add(enableColorThemingCheckBox,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(2, 2, 2, 2), 0, 0));
        jPanel5.add(byRangeCheckBox,
            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(2, 2, 2, 2), 0, 0));
        scrollPane.getViewport().add(table);
    }

    protected void enableColorThemingCheckBox_actionPerformed(ActionEvent e) {
        if (table.getRowCount() == 1) {
            //1 == just the "All other values" row. [Jon Aquino]
            populateTable();
        }

        updateComponents();
    }

    void attributeNameComboBox_actionPerformed(ActionEvent e) {
        try {
            if (initializing) {
                return;
            }

            if (attributeNameComboBox.getItemCount() == 0) {
                return;
            }
            Object selectedItem = attributeNameComboBox.getSelectedItem();
            if ( selectedItem != null && selectedItem.equals( lastAttributeName ) ) {
                //Don't want to clear the table if the attribute hasn't changed. [Jon Aquino]
                return;
            }

            stopCellEditing();
            populateTable();
        } finally {
            //Want to set lastAttributeName even during initialization. Otherwise, the following
            //undesirable behaviour occurs: Open dialog box; press Get All Values
            // -- the table is populated; edit the table; click on attribute combo box but click
            //on the same attribute -- the table is repopulated! [Jon Aquino]
            lastAttributeName = getAttributeName();

            if (table.getModel() instanceof DeeColorThemingTableModel) {
                //Model will be DefaultTableModel during early initialization. [Jon Aquino]
                tableModel().setAttributeName(getAttributeName());
            }
        }
    }

    public DeeColorThemingTableModel tableModel() {
        return (DeeColorThemingTableModel) table.getModel();
    }

    private SortedSet getNonNullAttributeValues() {
        TreeSet values = new TreeSet();

        for (Iterator i = layer.getFeatureCollectionWrapper().getFeatures()
                               .iterator(); i.hasNext();) {
            Feature feature = (Feature) i.next();

            if (feature.getAttribute(getAttributeName()) != null) {
                values.add(DeeColorThemingStyle.trimIfString(feature.getAttribute(getAttributeName())));
            }
        }

        return values;
    }

    public void populateTable() {
        if (!(enableColorThemingCheckBox.isSelected() && (attributeNameComboBox
                .getItemCount() > 0))) {
            return;
        }
        stopCellEditing();
        tableModel().clear();
        tableModel().setMaps(
                toAttributeValueToBasicStyleMap(filteredAttributeValues()),
                toAttributeValueToLabelMap(filteredAttributeValues()),
				toAttributeValueToVertexStyleMap(filteredAttributeValues()));
        tableModel().sort(tableModel().wasLastSortAscending());
        applyColorScheme();
    }

    /**
	 * @param collection
	 * @return
	 */
	protected Map toAttributeValueToVertexStyleMap(Collection attributeValues) {
		Map attributeValueToVertexStyleMap = new TreeMap();

		// FIXME need here to get vertex style from layer???
		//ColorThemingStyle style = (ColorThemingStyle)layer.getStyle( ColorThemingStyle.class );
		
        for (Iterator i = attributeValues.iterator(); i.hasNext();) {
            Object attributeValue = i.next();
            
            attributeValueToVertexStyleMap.put(attributeValue, new SquareVertexStyle());
        }
        return attributeValueToVertexStyleMap;
	}

	private Collection filteredAttributeValues() {
        return state.filterAttributeValues(getNonNullAttributeValues());
    }
	
    private Map toAttributeValueToLabelMap(Collection attributeValues) {
        Map attributeValueToAttributeValueMap = new TreeMap();
        for (Iterator i = attributeValues.iterator(); i.hasNext(); ) {
            Object attributeValue = i.next();
            attributeValueToAttributeValueMap.put(attributeValue, attributeValue);
        }
        Map attributeValueToLabelMap = CollectionUtil.inverse(state.toExternalFormat(attributeValueToAttributeValueMap));
        for (Iterator i = attributeValueToLabelMap.keySet().iterator(); i.hasNext(); ) {
            Object attributeValue = i.next();
            attributeValueToLabelMap.put(attributeValue, attributeValueToLabelMap.get(attributeValue).toString());
        }
        return attributeValueToLabelMap;
    }
    
    private Map toAttributeValueToBasicStyleMap(Collection attributeValues) {
        Map attributeValueToBasicStyleMap = new TreeMap();
        for (Iterator i = attributeValues.iterator(); i.hasNext();) {
            Object attributeValue = i.next();
            attributeValueToBasicStyleMap.put(attributeValue, new BasicStyle());
        }
        return attributeValueToBasicStyleMap;
    }    

    void colorSchemeComboBox_actionPerformed(ActionEvent e) {
        if (initializing) {
            return;
        }

        stopCellEditing();
        layer.getLayerManager().getBlackboard().put(COLOR_SCHEME_KEY,
            colorSchemeComboBox.getSelectedItem());
        applyColorScheme();
        colorSchemeForInserts = null;
    }

    private ColorScheme getColorSchemeForInserts() {
        //Cache the colour-scheme; otherwise the first color will always
        //be used for inserts. [Jon Aquino]
        if ((colorSchemeForInserts == null) ||
                !colorSchemeForInserts.getName().equalsIgnoreCase((String) colorSchemeComboBox.getSelectedItem())) {
            colorSchemeForInserts = ColorScheme.create((String) colorSchemeComboBox.getSelectedItem());
        }

        return colorSchemeForInserts;
    }

    public void applyColorScheme() {
        stopCellEditing();
        state.applyColorScheme(state.filterColorScheme(ColorScheme.create(
                    (String) colorSchemeComboBox.getSelectedItem())));
    }

    private void cancelCellEditing() {
        if (table.getCellEditor() instanceof DefaultCellEditor) {
            ((DefaultCellEditor) table.getCellEditor()).cancelCellEditing();
        }
    }

    public String validateInput() {
        stopCellEditing();

        //Split into #validateInput and #internalValidateInput to avoid
        //infinite recursion (#stopCellEditing causes #fireTableChanged
        //which causes #validateInput which causes #stopCellEditing...)
        //[Jon Aquino]
        return internalValidateInput();
    }

    private String internalValidateInput() {
        if (!enableColorThemingCheckBox.isSelected()) {
            return null;
        }

        if (errorMessages.isEmpty()) {
            return null;
        }

        return errorMessages.iterator().next().toString();
    }

    /**
     * @return enabled
     */
    private boolean setErrorMessage(ErrorMessage message, boolean enabled) {
        //Always remove the original, because #add doesn't replace, but is
        //ignored if the message already exists. Need to replace because the
        //"specific part" of the error message may be new. [Jon Aquino]
        errorMessages.remove(message);

        if (enabled) {
            errorMessages.add(message);
        }

        updateErrorDisplay();

        return enabled;
    }

    private void updateErrorDisplay() {
        String errorMessage = internalValidateInput();

        if (errorMessage != null) {
            statusLabel.setText(errorMessage);
            statusLabel.setIcon(GUIUtil.toSmallIcon(IconLoader.icon(
                        "Delete.gif")));
        } else {
            statusLabel.setText(" ");
            statusLabel.setIcon(null);
        }
    }

    private void setState(State state) {
        this.state.deactivate();
        jPanel1.remove(this.state.getPanel());

        //Need to call #revalidate; otherwise, the component won't get
        //removed. [Jon Aquino]
        this.state = state;
        jPanel1.add(state.getPanel(),
            new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(2, 2, 2, 2), 0, 0));
        initializing = true;

        try {
            colorSchemeComboBox.setModel(new DefaultComboBoxModel(
                    new Vector(state.getColorSchemeNames())));

            //The colour scheme may not be present if it was taken from the
            //set of discrete colour schemes but the combo box currently contains
            //the range colour schemes. [Jon Aquino]
            colorSchemeComboBox.setSelectedItem(layer.getLayerManager()
                                                     .getBlackboard().get(COLOR_SCHEME_KEY,
                    colorSchemeComboBox.getItemAt(0)));
        } finally {
            initializing = false;
        }

        updateComponents();
        this.state.activate();
        jPanel1.repaint();
    }

    void byRangeCheckBox_actionPerformed(ActionEvent e) {
        setState(byRangeCheckBox.isSelected() ? (ColorThemingStylePanel.State) rangeColorThemingState
                                              : discreteColorThemingState);
        populateTable();
    }

    void transparencySlider_stateChanged(ChangeEvent e) {
        repaint();
    }

    public JSlider getTransparencySlider() {
        return transparencySlider;
    }

    public JTable getTable() {
        return table;
    }

    private abstract class MyPlugIn extends AbstractPlugIn {
        public abstract Icon getIcon();
    }

    private class ErrorMessage {
        private String commonPart;
        private String specificPart;

        public ErrorMessage(String commonPart) {
            this(commonPart, "");
        }

        public ErrorMessage(String commonPart, String specificPart) {
            this.commonPart = commonPart;
            this.specificPart = specificPart;
        }

        public int hashCode() {
            return commonPart.hashCode();
        }

        public boolean equals(Object obj) {
            return commonPart.equals(((ErrorMessage) obj).commonPart);
        }

        public String toString() {
            return commonPart + specificPart;
        }
    }
}
