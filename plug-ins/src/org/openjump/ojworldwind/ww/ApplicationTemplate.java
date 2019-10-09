package org.openjump.ojworldwind.ww;

/*
Copyright (C) 2001, 2011 United States Government
as represented by the Administrator of the
National Aeronautics and Space Administration.
All Rights Reserved.
*/

import gov.nasa.worldwind.*;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.awt.*;
import gov.nasa.worldwind.event.*;
import gov.nasa.worldwind.exception.WWAbsentRequirementException;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.layers.*;
import gov.nasa.worldwind.layers.placename.PlaceNameLayer;
import gov.nasa.worldwind.render.GLRuntimeCapabilities;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwind.view.orbit.OrbitView;
import gov.nasa.worldwindx.examples.FlatWorldPanel;
import gov.nasa.worldwindx.examples.util.*;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides a base application framework for simple WorldWind examples. Examine other examples in this package to see
 * how it's used.
 *
 * @version $Id$
 */
public class ApplicationTemplate {

    public static class AppPanel extends JPanel {
        protected WorldWindow wwd;
        protected StatusBar statusBar;
        protected ToolTipController toolTipController;
        protected HighlightController highlightController;

        public AppPanel(Dimension canvasSize, boolean includeStatusBar) {
            super(new BorderLayout());

            this.wwd = this.createWorldWindow();
            ((Component) this.wwd).setPreferredSize(canvasSize);

            // Create the default model as described in the current worldwind properties.
            Model m = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
            this.wwd.setModel(m);

            // Setup a select listener for the worldmap click-and-go feature
            this.wwd.addSelectListener(new ClickAndGoSelectListener(this.getWwd(), WorldMapLayer.class));

            this.add((Component) this.wwd, BorderLayout.CENTER);
            if (includeStatusBar) {
                this.statusBar = new StatusBar();
                this.add(statusBar, BorderLayout.PAGE_END);
                this.statusBar.setEventSource(wwd);
            }


            // Add controllers to manage highlighting and tool tips.
            this.toolTipController = new ToolTipController(this.getWwd(), AVKey.DISPLAY_NAME, null);
            this.highlightController = new HighlightController(this.getWwd(), SelectEvent.ROLLOVER);
        }

        protected WorldWindow createWorldWindow() {
            return new WorldWindowGLCanvas();
        }

        public WorldWindow getWwd() {
            return wwd;
        }

        public StatusBar getStatusBar() {
            return statusBar;
        }
    }

    public static class AppFrame extends JFrame {
        protected List<Layer> layers = new ArrayList<Layer>();
        private Dimension canvasSize = new Dimension(800, 600);

        protected AppPanel wwjPanel;
        protected LayerPanel layerPanel;
        protected StatisticsPanel statsPanel;
        protected JPanel controlPanel;

        // terrain profiler
        private String follow;
        private boolean showEyePosition;
        private boolean keepProportions;
        private boolean zeroBased;
        private Dimension graphDimension;
        private double profileLengthFactor;

        private JLabel helpLabel;
        private JSlider lengthSlider;
        private JCheckBox showEyeCheck;
        private TerrainProfileLayer tpl;


        public AppFrame() {
            this.initialize(true, true, false);
        }

        public AppFrame(boolean includeStatusBar, boolean includeLayerPanel, boolean includeStatsPanel) {
            this.initialize(includeStatusBar, includeLayerPanel, includeStatsPanel);
        }

        protected void initialize(boolean includeStatusBar, boolean includeLayerPanel, boolean includeStatsPanel) {
            // Create the WorldWindow.
            this.wwjPanel = this.createAppPanel(this.canvasSize, includeStatusBar);
            this.wwjPanel.setPreferredSize(canvasSize);

            // from forum: to avoid errors logged on each move
            GLRuntimeCapabilities glrc = this.getWwd().getSceneController().getGLRuntimeCapabilities();
            glrc.setFramebufferObjectEnabled(false);

            // a control panel to add layers and flat panels:
            this.controlPanel = new JPanel(new BorderLayout(10, 10));

            // Put the pieces together.
            this.getContentPane().add(wwjPanel, BorderLayout.CENTER);
            if (includeLayerPanel) {
                this.layerPanel = new LayerPanel(this.wwjPanel.getWwd());
                this.controlPanel.add(this.layerPanel, BorderLayout.CENTER);
                this.controlPanel.add(new FlatWorldPanel(this.getWwd()), BorderLayout.NORTH);
//                this.getContentPane().add(this.layerPanel, BorderLayout.WEST);
                this.getContentPane().add(this.controlPanel, BorderLayout.WEST);
            }

            if (includeStatsPanel || System.getProperty("gov.nasa.worldwind.showStatistics") != null) {
                this.statsPanel = new StatisticsPanel(this.wwjPanel.getWwd(), new Dimension(250, canvasSize.height));
                this.getContentPane().add(this.statsPanel, BorderLayout.EAST);
            }

            // ------------- terrain profiler:
            // Add TerrainProfileLayer
            this.tpl = new TerrainProfileLayer();
            this.tpl.setEventSource(this.getWwd());
            this.tpl.setStartLatLon(LatLon.fromDegrees(0, -10));
            this.tpl.setEndLatLon(LatLon.fromDegrees(0, 65));
            insertBeforeCompass(this.getWwd(), tpl);

            // retreive default values
            this.follow = this.tpl.getFollow();
            this.showEyePosition = this.tpl.getShowEyePosition();
            this.keepProportions = this.tpl.getKeepProportions();
            this.zeroBased = this.tpl.getZeroBased();
            this.graphDimension = tpl.getSize();
            this.profileLengthFactor = tpl.getProfileLenghtFactor();

            this.controlPanel.add(makeControlPanel(), BorderLayout.SOUTH);

            // Create and install the view controls layer and register a controller for it with the World Window.
            ViewControlsLayer viewControlsLayer = new ViewControlsLayer();
            insertBeforeCompass(getWwd(), viewControlsLayer);
            this.getWwd().addSelectListener(new ViewControlsSelectListener(this.getWwd(), viewControlsLayer));

            // Register a rendering exception listener that's notified when exceptions occur during rendering.
            this.wwjPanel.getWwd().addRenderingExceptionListener(new RenderingExceptionListener() {
                public void exceptionThrown(Throwable t) {
                    if (t instanceof WWAbsentRequirementException) {
                        String message = "Computer does not meet minimum graphics requirements.\n";
                        message += "Please install up-to-date graphics driver and try again.\n";
                        message += "Reason: " + t.getMessage() + "\n";
                        message += "This program will end when you press OK.";

                        JOptionPane.showMessageDialog(AppFrame.this, message, "Unable to Start Program",
                                JOptionPane.ERROR_MESSAGE);
                        System.exit(-1);
                    }
                }
            });

            // Search the layer list for layers that are also select listeners and register them with the World
            // Window. This enables interactive layers to be included without specific knowledge of them here.
            for (Layer layer : this.wwjPanel.getWwd().getModel().getLayers()) {
                if (layer instanceof SelectListener) {
                    this.getWwd().addSelectListener((SelectListener) layer);
                }
            }

            this.pack();

            // Center the application on the screen.
            WWUtil.alignComponent(null, this, AVKey.CENTER);
            this.setResizable(true);
        }

        // Update worldwind
        private void update() {
            this.tpl.setFollow(this.follow);
            this.tpl.setKeepProportions(this.keepProportions);
            this.tpl.setZeroBased(this.zeroBased);
            this.tpl.setSize(this.graphDimension);
            this.tpl.setShowEyePosition(this.showEyePosition);
            this.tpl.setProfileLengthFactor(this.profileLengthFactor);
            this.getWwd().redraw();
        }


        private JPanel makeControlPanel() {
            JPanel controlPanel = new JPanel(new GridLayout(0, 1, 0, 4));

            // Show eye position check box
            JPanel buttonsPanel = new JPanel(new GridLayout(0, 2, 0, 0));
            this.showEyeCheck = new JCheckBox("Show eye");
            this.showEyeCheck.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                    showEyePosition = ((JCheckBox) actionEvent.getSource()).isSelected();
                    update();
                }
            });
            this.showEyeCheck.setSelected(this.showEyePosition);
            this.showEyeCheck.setEnabled(this.follow.equals(TerrainProfileLayer.FOLLOW_EYE));
            buttonsPanel.add(this.showEyeCheck);
            // Keep proportions check box
            JCheckBox cbKeepProportions = new JCheckBox("Keep proportions");
            cbKeepProportions.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                    keepProportions = ((JCheckBox) actionEvent.getSource()).isSelected();
                    update();
                }
            });
            cbKeepProportions.setSelected(this.keepProportions);
            buttonsPanel.add(cbKeepProportions);

            // Zero based graph check box
            JPanel buttonsPanel2 = new JPanel(new GridLayout(0, 2, 0, 0));
            JCheckBox cb = new JCheckBox("Zero based");
            cb.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                    zeroBased = ((JCheckBox) actionEvent.getSource()).isSelected();
                    update();
                }
            });
            cb.setSelected(this.zeroBased);
            buttonsPanel2.add(new JLabel("")); // Dummy
            buttonsPanel2.add(cb);

            // Dimension combo
            JPanel dimensionPanel = new JPanel(new GridLayout(0, 2, 0, 0));
            dimensionPanel.add(new JLabel("  Dimension:"));
            final JComboBox cbDimension = new JComboBox(new String[] {"Small", "Medium", "Large"});
            cbDimension.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                    String size = (String) cbDimension.getSelectedItem();
                    if (size.equals("Small"))
                    {
                        graphDimension = new Dimension(250, 100);
                    }
                    else if (size.equals("Medium"))
                    {
                        graphDimension = new Dimension(450, 140);
                    }
                    else if (size.equals("Large"))
                    {
                        graphDimension = new Dimension(655, 240);
                    }
                    update();
                }
            });
            cbDimension.setSelectedItem("Small");
            dimensionPanel.add(cbDimension);

            // Profile length factor slider
            JPanel sliderPanel = new JPanel(new GridLayout(0, 1, 0, 0));
            JSlider s = new JSlider(JSlider.HORIZONTAL, 0, 30,
                    (int) (this.profileLengthFactor * 10));  // -5 - 5 in tenth
            s.setMajorTickSpacing(10);
            s.setMinorTickSpacing(1);
            //s.setPaintTicks(true);
            //s.setPaintLabels(true);
            s.setToolTipText("Profile length");
            s.addChangeListener(new ChangeListener()
            {
                public void stateChanged(ChangeEvent event)
                {
                    JSlider s = (JSlider) event.getSource();
                    if (!s.getValueIsAdjusting())
                    {
                        profileLengthFactor = (double) s.getValue() / 10;
                        update();
                    }
                }
            });
            sliderPanel.add(s);
            this.lengthSlider = s;

            // Help label
            JPanel textPanel = new JPanel(new GridLayout(0, 1, 0, 0));
            this.helpLabel = new JLabel("Tip: move mouse over the graph.");
            this.helpLabel.setHorizontalAlignment(SwingConstants.CENTER);
            textPanel.add(this.helpLabel);

            // Follow behavior combo
            JPanel followPanel = new JPanel(new GridLayout(0, 2, 0, 0));
            followPanel.add(new JLabel("  Follow:"));
            final JComboBox cbFollow = new JComboBox(new String[] {"View", "Cursor", "Eye", "None", "Object"});
            cbFollow.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent actionEvent)
                {
                    String size = (String) cbFollow.getSelectedItem();
                    if (size.equals("View"))
                    {
                        follow = TerrainProfileLayer.FOLLOW_VIEW;
                        helpLabel.setEnabled(true);
                        showEyeCheck.setEnabled(false);
                        lengthSlider.setEnabled(true);
                    }
                    else if (size.equals("Cursor"))
                    {
                        follow = TerrainProfileLayer.FOLLOW_CURSOR;
                        helpLabel.setEnabled(false);
                        showEyeCheck.setEnabled(false);
                        lengthSlider.setEnabled(true);
                    }
                    else if (size.equals("Eye"))
                    {
                        follow = TerrainProfileLayer.FOLLOW_EYE;
                        helpLabel.setEnabled(true);
                        showEyeCheck.setEnabled(true);
                        lengthSlider.setEnabled(true);
                    }
                    else if (size.equals("None"))
                    {
                        follow = TerrainProfileLayer.FOLLOW_NONE;
                        helpLabel.setEnabled(true);
                        showEyeCheck.setEnabled(false);
                        lengthSlider.setEnabled(false);
                    }
                    else if (size.equals("Object"))
                    {
                        follow = TerrainProfileLayer.FOLLOW_OBJECT;
                        helpLabel.setEnabled(true);
                        showEyeCheck.setEnabled(true);
                        lengthSlider.setEnabled(true);
                        OrbitView view = (OrbitView) getWwd().getView();
                        tpl.setObjectPosition(getWwd().getView().getEyePosition());
                        tpl.setObjectHeading(view.getHeading());
                    }
                    update();
                }
            });
            cbFollow.setSelectedItem("View");
            followPanel.add(cbFollow);

            // Assembly
            controlPanel.add(dimensionPanel);
            controlPanel.add(followPanel);
            controlPanel.add(buttonsPanel);
            controlPanel.add(buttonsPanel2);
            controlPanel.add(sliderPanel);
            controlPanel.add(textPanel);
            controlPanel.setBorder(
                    new CompoundBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9), new TitledBorder("Terrain profile")));
            controlPanel.setToolTipText("Terrain profile controls");
            return controlPanel;
        }


        protected AppPanel createAppPanel(Dimension canvasSize, boolean includeStatusBar) {
            return new AppPanel(canvasSize, includeStatusBar);
        }

        public Dimension getCanvasSize() {
            return canvasSize;
        }

        public AppPanel getWwjPanel() {
            return wwjPanel;
        }

        public WorldWindow getWwd() {
            return this.wwjPanel.getWwd();
        }

        public StatusBar getStatusBar() {
            return this.wwjPanel.getStatusBar();
        }

        public LayerPanel getLayerPanel() {
            return layerPanel;
        }

        public StatisticsPanel getStatsPanel() {
            return statsPanel;
        }

        public void setToolTipController(ToolTipController controller) {
            if (this.wwjPanel.toolTipController != null)
                this.wwjPanel.toolTipController.dispose();

            this.wwjPanel.toolTipController = controller;
        }

        public void setHighlightController(HighlightController controller) {
            if (this.wwjPanel.highlightController != null)
                this.wwjPanel.highlightController.dispose();

            this.wwjPanel.highlightController = controller;
        }
    }

    public static void insertBeforeCompass(WorldWindow wwd, Layer layer) {
        // Insert the layer into the layer list just before the compass.
        int compassPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers) {
            if (l instanceof CompassLayer)
                compassPosition = layers.indexOf(l);
        }
        layers.add(compassPosition, layer);
    }

    public static void insertBeforePlacenames(WorldWindow wwd, Layer layer) {
        // Insert the layer into the layer list just before the placenames.
        int compassPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers) {
            if (l instanceof PlaceNameLayer)
                compassPosition = layers.indexOf(l);
        }
        layers.add(compassPosition, layer);
    }

    public static void insertAfterPlacenames(WorldWindow wwd, Layer layer) {
        // Insert the layer into the layer list just after the placenames.
        int compassPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers) {
            if (l instanceof PlaceNameLayer)
                compassPosition = layers.indexOf(l);
        }
        layers.add(compassPosition + 1, layer);
    }

    public static void insertBeforeLayerName(WorldWindow wwd, Layer layer, String targetName) {
        // Insert the layer into the layer list just before the target layer.
        int targetPosition = 0;
        LayerList layers = wwd.getModel().getLayers();
        for (Layer l : layers) {
            if (l.getName().indexOf(targetName) != -1) {
                targetPosition = layers.indexOf(l);
                break;
            }
        }
        layers.add(targetPosition, layer);
    }

    static {
        System.setProperty("java.net.useSystemProxies", "true");
        if (Configuration.isMacOS()) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "World Wind Application");
            System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
            System.setProperty("apple.awt.brushMetalLook", "true");
        } else if (Configuration.isWindowsOS()) {
            System.setProperty("sun.awt.noerasebackground", "true"); // prevents flashing during window resizing
        }
    }

    public static AppFrame start(String appName, Class appFrameClass) {
        if (Configuration.isMacOS() && appName != null) {
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", appName);
        }

        try {
            final AppFrame frame = (AppFrame) appFrameClass.newInstance();
            frame.setTitle(appName);
//            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    frame.setVisible(true);
                }
            });

            return frame;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args) {
        // Call the static start method like this from the main method of your derived class.
        // Substitute your application's name for the first argument.
        ApplicationTemplate.start("World Wind Application", AppFrame.class);
    }
}