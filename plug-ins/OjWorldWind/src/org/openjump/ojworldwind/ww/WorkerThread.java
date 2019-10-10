package org.openjump.ojworldwind.ww;


import com.vividsolutions.jump.workbench.Logger;
import com.vividsolutions.jump.workbench.model.Layerable;
import gov.nasa.worldwind.layers.Layer;
import org.openjump.ojworldwind.LayerConverter;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;

public class WorkerThread extends Thread {
    protected Layerable[] ojLayers;
    protected ApplicationTemplate.AppFrame appFrame;
    // keeps trace of displayed layers, to remove them if needed
    private static HashSet<String> displayedLayers = new HashSet<>();
    private LayerConverter layerConverter;



    public WorkerThread(Layerable[] ojLayers,
                        ApplicationTemplate.AppFrame appFrame) {
        this.ojLayers = ojLayers;
        this.appFrame = appFrame;
        this.layerConverter = new LayerConverter();

    }

    public void run() {
        try {
            // removes already added layers, based on name: todo: id ?,
            // and add other layers
            SwingUtilities.invokeLater(() -> {
                try {
                    // removes layer with same name as given one
                    for (Layerable ojLayer : this.ojLayers) {
                        if (displayedLayers.contains(ojLayer.getName())) {
                            displayedLayers.remove(ojLayer.getName());
                            this.removeLayer(ojLayer.getName());
                        } else {
                            displayedLayers.add(ojLayer.getName());
                            // force another name for display: shortened one:
                            Layer wwLayer = this.layerConverter.convert(ojLayer);
                            wwLayer.setName(this.makeDisplayName(wwLayer));
                            ApplicationTemplate.insertBeforePlacenames(appFrame.getWwd(), wwLayer);
                            appFrame.layers.add(wwLayer);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace(); //TODO
                }
                appFrame.getLayerPanel().update(appFrame.getWwd());
            });
        } catch (Exception e) {
            Logger.error(String.format("WorkerThread: exception when adding/removing layers in/from WW: %s",
                    e.getMessage()));
        } finally {
            SwingUtilities.invokeLater(
                    () -> ((Component) appFrame.getWwd()).setCursor(Cursor.getDefaultCursor())
            );
        }
    }
    /**
     * Name of the layer: 50 first chars
     * @return
     */
    protected String makeDisplayName(Layer wwLayer) {
        return wwLayer.getName().length() <= 50 ? wwLayer.getName() :
                wwLayer.getName().substring(0, 50) + "...";
    }

    protected void removeLayer(String layerName) {
        for (Layer wwLayer : appFrame.getWwd().getModel().getLayers()) {
            if (wwLayer.getName().equals(layerName)) {
                appFrame.getWwd().getModel().getLayers().remove(wwLayer);
            }
        }
    }

    /**
     * Returns true if the given name is in displayedLayers, meaning a layer with this name
     * is currently displayed in WW
     * @param name
     * @return
     */
    public static boolean isDisplayed(String name) {
        if (displayedLayers == null) {
            return false;
        }
        return displayedLayers.contains(name);
    }
}