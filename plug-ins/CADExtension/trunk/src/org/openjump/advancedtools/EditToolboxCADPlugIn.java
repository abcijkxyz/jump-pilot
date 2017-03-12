/* Copyright (2017) Giuseppe Aruta
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.openjump.advancedtools;

/**
 * @since OpenJUMP 1.10
 */
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.openjump.advancedtools.config.CADToolsOptionsPanel;
import org.openjump.advancedtools.language.I18NPlug;
import org.openjump.advancedtools.plugins.ArcPlugIn;
import org.openjump.advancedtools.plugins.CirclePlugIn;
import org.openjump.advancedtools.plugins.ClearLayerSelectionPlugIn;
import org.openjump.advancedtools.plugins.ExtendLinePlugIn;
import org.openjump.advancedtools.plugins.MirrorPlugin;
import org.openjump.advancedtools.plugins.PutVertexInCrossingLinesPlugIn;
import org.openjump.advancedtools.plugins.RegularPolygonPlugIn;
import org.openjump.advancedtools.plugins.ShortenLinePlugIn;
import org.openjump.advancedtools.plugins.SimpleLinePlugIn;
import org.openjump.advancedtools.tools.CopyDraggingTool;
import org.openjump.advancedtools.tools.CuadraticBezierCurveTool;
import org.openjump.advancedtools.tools.DrawConstrainedParallelogramTool;
import org.openjump.advancedtools.tools.EllipseByDraggingTool;
import org.openjump.advancedtools.tools.ExtendLinesAndCutWhereTheyTouchTool;
import org.openjump.advancedtools.tools.ParalelAuxiliarylLineTool;
import org.openjump.advancedtools.tools.ParalelLineTool;
import org.openjump.advancedtools.tools.PerpendicularLineTool;
import org.openjump.advancedtools.tools.RemoveSectionInLineTool;
import org.openjump.advancedtools.tools.SelectEditingFeaturesTool;
import org.openjump.advancedtools.tools.cogo.DrawGeometryCommandsTool;

import com.vividsolutions.jump.workbench.JUMPWorkbench;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.plugin.EnableCheck;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.GUIUtil;
import com.vividsolutions.jump.workbench.ui.MenuNames;
import com.vividsolutions.jump.workbench.ui.OptionsDialog;
import com.vividsolutions.jump.workbench.ui.cursortool.DelegatingTool;
import com.vividsolutions.jump.workbench.ui.cursortool.QuasimodeTool;
import com.vividsolutions.jump.workbench.ui.plugin.FeatureInstaller;
import com.vividsolutions.jump.workbench.ui.toolbox.ToolboxDialog;
import com.vividsolutions.jump.workbench.ui.toolbox.ToolboxPlugIn;

/**
 * @author Giuseppe Aruta
 * @since OpenJUMP 1.10
 */

public class EditToolboxCADPlugIn extends ToolboxPlugIn {

    public static final String ADVANCED_TOOLS = I18NPlug
            .getI18N("org.openjump.core.ui.plugins.Advanced-edit-tools");

    public static final String CAD = I18NPlug
            .getI18N("org.openjump.core.ui.CAD");
    public static final String CAD_OPTIONS_PANE_NAME = I18NPlug
            .getI18N("org.openjump.core.ui.config.CADToolsOptionsPanel");

    PlugInContext context;

    public static ImageIcon ICON = org.openjump.advancedtools.icon.IconLoader
            .icon("cadTools.png");

    @Override
    public void initialize(PlugInContext context) throws Exception {
        WorkbenchContext workbenchContext = context.getWorkbenchContext();
        new FeatureInstaller(workbenchContext);

        context.getWorkbenchFrame().getToolBar().addSpacer();
        context.getWorkbenchFrame()
                .getToolBar()
                .addPlugIn(ICON, this, createEnableCheck(workbenchContext),
                        context.getWorkbenchContext());
        context.getFeatureInstaller().addMainMenuPlugin(this,
                new String[] { MenuNames.PLUGINS }, CAD, false, ICON,
                createEnableCheck(workbenchContext));
        OptionsDialog.instance(context.getWorkbenchContext().getWorkbench())
                .addTab(CAD_OPTIONS_PANE_NAME, GUIUtil.toSmallIcon(ICON),
                        new CADToolsOptionsPanel());

    }

    private MultiEnableCheck createEnableCheck(
            final WorkbenchContext workbenchContext) {
        EnableCheckFactory checkFactory = new EnableCheckFactory(
                workbenchContext);

        MultiEnableCheck mec = new MultiEnableCheck();
        mec.add(checkFactory.createWindowWithLayerViewPanelMustBeActiveCheck());
        mec.add(new EnableCheck() {
            @Override
            public String check(JComponent component) {
                component.setToolTipText(getName());
                return null;
            }
        });

        return mec;
    }

    @Override
    public String getName() {
        return CAD;
    }

    @Override
    protected void initializeToolbox(final ToolboxDialog toolbox) {
        toolbox.setTitle(CAD);
        // toolbox.setIconImage(ICON.getImage());
        EnableCheckFactory checkFactory = new EnableCheckFactory(
                toolbox.getContext());

        /**
         * Selecting tools/plugins
         */
        // Select feature in only one selected layer
        SelectEditingFeaturesTool select = new SelectEditingFeaturesTool();
        toolbox.add(select);
        // Unselect features
        ClearLayerSelectionPlugIn clearSelectionPlugIn2 = new ClearLayerSelectionPlugIn();
        toolbox.addPlugIn(clearSelectionPlugIn2, null,
                ClearLayerSelectionPlugIn.ICON);
        /**
         * Drawing tools/plugIns
         * 
         * Create new geometries giving defined parameters
         */
        // Draw simple line with commands
        SimpleLinePlugIn sl = new SimpleLinePlugIn();
        toolbox.addPlugIn(sl, null, sl.getIcon());

        // Draw Bezier curve tool
        toolbox.add(CuadraticBezierCurveTool.create(toolbox.getContext()));

        // Draw arc plugin
        ArcPlugIn arc = new ArcPlugIn();
        toolbox.addPlugIn(arc, null, arc.getIcon());

        toolbox.addToolBar();
        // Draw Constrained parallelogramme
        toolbox.add(DrawConstrainedParallelogramTool.create(toolbox
                .getContext()));

        // Draw regular polygon
        RegularPolygonPlugIn regular = new RegularPolygonPlugIn();
        toolbox.addPlugIn(regular, null, regular.getIcon());

        // Draw circle plugin
        CirclePlugIn circle = new CirclePlugIn();
        toolbox.addPlugIn(circle, null, circle.getIcon());

        // Simple draw ellipse by dragging
        toolbox.add(EllipseByDraggingTool.create(toolbox.getContext()));

        // Draw with commands
        DelegatingTool delLineCommandTool = (DelegatingTool) DrawGeometryCommandsTool
                .create(toolbox.getContext());
        toolbox.add(delLineCommandTool);

        toolbox.addToolBar();

        /**
         * Generate tools/plugins
         * 
         * Generate new geometries from selected one. Selected geometries are
         * not deleted
         */
        // Draw parallel line to a selected geometry
        ParalelLineTool paralel = new ParalelLineTool(checkFactory);
        toolbox.add(paralel);

        // Draw a parallel auxiliary line to a selected geometry
        ParalelAuxiliarylLineTool auxiliaryParalel = new ParalelAuxiliarylLineTool(
                checkFactory);
        toolbox.add(auxiliaryParalel);

        // Draw a perpendicular line to a selected geometry
        PerpendicularLineTool perpendicular = new PerpendicularLineTool(
                checkFactory);
        toolbox.add(perpendicular);

        // Clone selected geometries
        CopyDraggingTool copy = new CopyDraggingTool(checkFactory);
        toolbox.add(copy);

        // Axial symmetry of selected geometries
        MirrorPlugin mirror = new MirrorPlugin();
        toolbox.addPlugIn(mirror, null, mirror.getIcon());

        toolbox.addToolBar();
        // Extend a lineString
        ExtendLinePlugIn extendLinePlugIn = new ExtendLinePlugIn();
        toolbox.addPlugIn(extendLinePlugIn, null, extendLinePlugIn.getIcon());

        // Shorten a lineString
        ShortenLinePlugIn shortenLinePlugIn = new ShortenLinePlugIn();
        toolbox.addPlugIn(shortenLinePlugIn, null, shortenLinePlugIn.getIcon());

        /**
         * Modify tools/plugins
         * 
         * Modify selected geometries.
         */

        // Extend two convergent lineStrings and cut in the point they touch
        toolbox.add(ExtendLinesAndCutWhereTheyTouchTool.create(toolbox
                .getContext()));
        // Put vertex on crossing point of two lineStrings
        PutVertexInCrossingLinesPlugIn putVertex = new PutVertexInCrossingLinesPlugIn();
        toolbox.addPlugIn(putVertex, null, putVertex.getIcon());

        // Remove a section of a selected lineString
        toolbox.add(QuasimodeTool
                .createWithDefaults(new RemoveSectionInLineTool(checkFactory)));
        // toolbox.addToolBar();

        /*
         * The following working tools/plugins are deactivated as either always
         * they have a correspondence in OpenJUMP or they are used for testing
         * reasons
         */

        // Rotate features by dragging
        // toolbox.add(GeneralUtils.addStandardQuasimodes(new
        // RotateTool(checkFactory)));
        // Rotate features using a dilaog
        // toolbox.add(GeneralUtils.addStandardQuasimodes(new
        // RotateDialogTool(checkFactory)));

        // ForceQuitPlugIn quit = new ForceQuitPlugIn();
        // toolbox.addPlugIn(quit, null, quit.getIcon());

        /**
         * Add lower panel
         */
        JPanel pan = new JPanel();
        toolbox.getCenterPanel().add(pan, GridBagConstraints.RELATIVE);
        toolbox.getCenterPanel().add(optionPanel(toolbox),
                GridBagConstraints.RELATIVE);

        toolbox.finishAddingComponents();
        toolbox.setIconImage(org.openjump.advancedtools.icon.IconLoader
                .image("cadTools.png"));
        toolbox.setResizable(false);
        toolbox.setInitialLocation(new GUIUtil.Location(20, true, 20, true));
        toolbox.validate();

    }

    private JPanel optionPanel(final ToolboxDialog toolbox) {

        JPanel optionPanel = new JPanel();
        optionPanel.setLayout(new BorderLayout());
        JPanel option = new JPanel();
        JButton optionsButton = new JButton(CAD_OPTIONS_PANE_NAME + "...");

        optionsButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OptionsDialog optionsDialog = OptionsDialog
                        .instance(JUMPWorkbench.getInstance());
                JTabbedPane tabbedPane = optionsDialog.getTabbedPane();
                for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                    if (tabbedPane.getComponentAt(i) instanceof CADToolsOptionsPanel) {
                        tabbedPane.setSelectedIndex(i);
                        break;
                    }
                }
                GUIUtil.centreOnWindow(optionsDialog);
                optionsDialog.setVisible(true);
            }
        });
        optionsButton
                .setIcon(com.vividsolutions.jump.workbench.ui.images.IconLoader
                        .icon("fugue/wrench-screwdriver.png"));
        option.add(optionsButton);
        optionPanel.add(option, BorderLayout.SOUTH);

        return optionPanel;
    }

}
