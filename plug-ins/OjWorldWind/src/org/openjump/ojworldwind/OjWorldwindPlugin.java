package org.openjump.ojworldwind;

import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.model.Layerable;
import com.vividsolutions.jump.workbench.plugin.*;
import com.vividsolutions.jump.workbench.ui.*;
import com.vividsolutions.jump.workbench.ui.plugin.FeatureInstaller;
import com.vividsolutions.jump.workbench.ui.plugin.ViewAttributesPlugIn;
import org.openjump.ojworldwind.language.I18NPlug;
import org.openjump.ojworldwind.ww.ApplicationTemplate;
import org.openjump.ojworldwind.ww.WorkerThread;

import javax.swing.*;
import java.awt.*;

/**
 * TODO: threaded or not ?
 * TODO: toolbar icon, layer right menu
 * TODO: i18N, styling from OJ into WW, remove layer from WW
 * TODO: WMS images
 */
public class OjWorldwindPlugin extends AbstractPlugIn implements CheckBoxed {
    // Strings from I18N
    public final static String WWPlugin = I18NPlug
            .getI18N("org.openjump.ojworldwind.OjWorldWindPlugin");

    private ApplicationTemplate.AppFrame appFrame;
    private static String menuTitleAdd = "Display in WorldWind";
    private static String menuTitleRemove = "Remove from WorldWind";

    public OjWorldwindPlugin() {
        // empty constructor
    }

    public void initialize(PlugInContext context) throws Exception {
        FeatureInstaller.getInstance().addMainMenuPlugin(this,
                new String[] { MenuNames.VIEW});

        context.getFeatureInstaller()
                .addPopupMenuPlugin(
                        context.getWorkbenchContext().getWorkbench().getFrame()
                                .getLayerNamePopupMenu(),
                        this,
                        menuTitleAdd,
                        false, getIcon(), createEnableCheck(context.getWorkbenchContext()));

        // also install popup menu for WMS Layers:
        context.getFeatureInstaller()
                .addPopupMenuPlugin(
                        context.getWorkbenchContext().getWorkbench().getFrame()
                                .getWMSLayerNamePopupMenu(),
                        this,
                        menuTitleAdd,
                        false, getIcon(), createEnableCheck(context.getWorkbenchContext()));

        // option panel:
        OptionsDialog.instance(context.getWorkbenchContext().getWorkbench())
                .addTab(WWPlugin, GUIUtil.toSmallIcon(getIcon()),
                        new WorldWindOptionsPanel());

    }

    public ImageIcon getIcon(){
        return new ImageIcon(this.getClass().getResource("earth.png"));
    }

    public static MultiEnableCheck createEnableCheck(WorkbenchContext workbenchContext) {
        EnableCheckFactory checkFactory = new EnableCheckFactory(workbenchContext);
        return new MultiEnableCheck()
                .add(checkFactory.createWindowWithLayerNamePanelMustBeActiveCheck())
                .add(checkFactory.createAtLeastNLayersMustExistCheck(1))
                .add(component -> {
                    JCheckBoxMenuItem cbMenu = ((JCheckBoxMenuItem) component);
                    // Test only the first selected layer: TODO
                    if (getSelectedLayerables(workbenchContext).length > 0) {
                        boolean b = WorkerThread.isDisplayed(getSelectedLayerables(workbenchContext)[0].getName());
                        cbMenu.setText(b ? menuTitleRemove : menuTitleAdd);
                        cbMenu.setSelected(b);
                    } else {
                        cbMenu.setSelected(false);
                    }
                    return null;
                });
    }

    /**
     * Action on menu item selection:
     * creates independant WW Frame (suitable for 2nd screen)
     * TODO: test if called from view menu or layer contextual menu
     */
    public boolean execute(PlugInContext context) throws Exception {
        if (appFrame == null) {
            this.appFrame = ApplicationTemplate.start("Worldwind Plugin", ApplicationTemplate.AppFrame.class);
            //        internal frame bug: gl canvas does not follow JFrame on move :(
            //        context.getWorkbenchFrame().getDesktopPane().add(wwFrame);
        } else if (!appFrame.isVisible()) {
            appFrame.setVisible(true);

        }
        WorkerThread workerThread = new WorkerThread(getSelectedLayerables(context.getWorkbenchContext()), this.appFrame);
        workerThread.start();
        ((Component) appFrame.getWwd()).setCursor(new Cursor(Cursor.WAIT_CURSOR));
        return true;
    }

    public String getName() {
        return "Worldwind plugin";
    }

    private static Layerable[] getSelectedLayerables(WorkbenchContext wbc) {
        Layerable[] layers = new Layerable[] {};

        JInternalFrame frame = wbc.getWorkbench().getFrame()
                .getActiveInternalFrame();
        if (frame instanceof LayerNamePanelProxy) {
            LayerNamePanel layerNamePanel = ((LayerNamePanelProxy) frame).getLayerNamePanel();
            if (frame instanceof InfoFrame) {
                Layer[] lyrs = ((InfoFrame)frame).getModel().getLayers().toArray(new Layer[0]);
                layers = new Layerable[lyrs.length];
                System.arraycopy(lyrs, 0, layers, 0, lyrs.length);
            }
            else if (layerNamePanel instanceof LayerableNamePanel) {
                layers = ((LayerableNamePanel) layerNamePanel).getSelectedLayerables().toArray(
                        new Layerable[]{});
            }
            else {
                layers = layerNamePanel.getSelectedLayers();
            }
        }
        else if (frame instanceof ViewAttributesPlugIn.ViewAttributesFrame) {
            layers = new Layerable[]{
                    ((ViewAttributesPlugIn.ViewAttributesFrame)frame).getOneLayerAttributeTab().getLayer()
            };
        }


        return layers;
    }

    // TODO: I18N
    private boolean confirmNonWgsLayer(PlugInContext context) {
        int opt = JOptionPane.showConfirmDialog(
                context.getWorkbenchContext().getWorkbench().getFrame(),
                "Add a non WGS84 layer ?",
                "WorldWind plugin",
                JOptionPane.YES_NO_OPTION);
        return (opt != JOptionPane.NO_OPTION);
    }
}
