package org.openjump.ojworldwind;

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

import com.vividsolutions.jump.util.Blackboard;
import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.OptionsPanel;
import com.vividsolutions.jump.workbench.ui.plugin.PersistentBlackboardPlugIn;
import org.openjump.ojworldwind.language.I18NPlug;
import org.saig.core.gui.swing.sldeditor.util.FormUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 *
 * Implements an {@link OptionsPanel} to allow skin selection.
 *
 */

public class WorldWindOptionsPanel extends JPanel implements OptionsPanel {
    // Strings from I18N
    public final static String WWOptions = I18NPlug
            .getI18N("org.openjump.ojworldwind.config.WorldWindOptionsPanel");
    public final static String ConvFormat = I18NPlug
            .getI18N("org.openjump.ojworldwind.config.WorldWindOptionsPanel.DefaultConversionFormat");
    public final static String MiscOptions = I18NPlug
            .getI18N("org.openjump.ojworldwind.config.WorldWindOptionsPanel.MiscOptions");
    public final static String GeoJSONTooltip = I18NPlug
            .getI18N("org.openjump.ojworldwind.config.WorldWindOptionsPanel.GeoJSONTooltip");
    public final static String ShapefileTooltip = I18NPlug
            .getI18N("org.openjump.ojworldwind.config.WorldWindOptionsPanel.ShapefileTooltip");

    // Constants
    private final static String HEIGHT = "height";
    public final static String GEOJSON= "GeoJSON";
    public final static String SHAPEFILE= "Shapefile";

    // Keys to store these options
    /** Key for height attribute name */
    public final static String HEIGHT_ATTRIBUTE_KEY = WorldWindOptionsPanel.class
            .getName() + " - HEIGHT ATTRIBUTE";
    public final static String CONV_FORMAT_KEY = WorldWindOptionsPanel.class
            .getName() + " - CONVERSION FORMAT";

    private JTextField heightAttrText = new JTextField();
    private JPanel fillerPanel = new JPanel();
    private JPanel convPanel;
    private JPanel miscPanel;
    private JLabel heightAttrLabel = new JLabel(I18NPlug
            .getI18N("org.openjump.ojworldwind.config.WorldWindOptionsPanel.HeightAttribute"));
    private ButtonGroup radioGroup = new ButtonGroup();
    private JRadioButton jsonRadio = new JRadioButton(I18NPlug
            .getI18N("org.openjump.ojworldwind.config.WorldWindOptionsPanel.GeoJSON") + " (" +
            GeoJSONTooltip + ")");
    private JRadioButton shpRadio = new JRadioButton(I18NPlug
            .getI18N("org.openjump.ojworldwind.config.WorldWindOptionsPanel.Shapefile") + " (" +
            ShapefileTooltip + ")");

    public WorldWindOptionsPanel() {
        this.setLayout(new GridBagLayout());
        FormUtils.addRowInGBL(this, 0, 0, getMiscOptionsPanel());
        FormUtils.addRowInGBL(this, 1, 0, getConversionOptionsPanel());
        FormUtils.addFiller(this, 2, 0);
    }

    public JPanel getConversionOptionsPanel() {
        if (convPanel == null) {
            convPanel = new JPanel(new GridBagLayout());
            TitledBorder titledBorder2 = new TitledBorder(
                    BorderFactory.createEtchedBorder(Color.white, new Color(
                            148, 145, 140)), ConvFormat);
            convPanel.setBorder(titledBorder2);
            radioGroup.add(jsonRadio);
            radioGroup.add(shpRadio);
            jsonRadio.setSelected(true);

            FormUtils.addRowInGBL(convPanel, 0, 0, jsonRadio);
            FormUtils.addRowInGBL(convPanel, 1, 0, shpRadio);
        }
        return convPanel;
    }

    public JPanel getMiscOptionsPanel() {
        if (miscPanel == null) {
            miscPanel = new JPanel(new GridBagLayout());
            TitledBorder titledBorder = new TitledBorder(
                    BorderFactory.createEtchedBorder(Color.white, new Color(
                            148, 145, 140)), MiscOptions);
            miscPanel.setBorder(titledBorder);

            FormUtils.addRowInGBL(miscPanel, 0, 0, heightAttrLabel, heightAttrText);
        }
        return miscPanel;
    }
    

    // all initialization is done in constructor
    @Override
    public void init() {
        heightAttrText.setPreferredSize(new Dimension(100, 25));
        heightAttrText.setText(getHeightAttributeName());
        jsonRadio.setSelected(GEOJSON.equals(getConverterFormat()));
    }

    public void okPressed() {
        PersistentBlackboardPlugIn.get(
                JUMPWorkbench.getInstance().getFrame().getContext()).put(
                HEIGHT_ATTRIBUTE_KEY, heightAttrText.getText());
        // conv format:
        PersistentBlackboardPlugIn.get(
                JUMPWorkbench.getInstance().getFrame().getContext()).put(
                CONV_FORMAT_KEY, jsonRadio.isSelected() ? GEOJSON : SHAPEFILE);
    }

    public String validateInput() {
        return null;
    }

    // static accessors:
    public static String getHeightAttributeName() {
        return (String)PersistentBlackboardPlugIn.get(
                JUMPWorkbench.getInstance().getFrame().getContext()).get(
                HEIGHT_ATTRIBUTE_KEY, HEIGHT);
    }

    public static String getConverterFormat() {
        return (String)PersistentBlackboardPlugIn.get(
                JUMPWorkbench.getInstance().getFrame().getContext()).get(
                CONV_FORMAT_KEY, GEOJSON);
    }

}
