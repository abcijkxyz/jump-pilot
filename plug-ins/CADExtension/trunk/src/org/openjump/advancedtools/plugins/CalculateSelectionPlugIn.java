/* 
 * Kosmo - Sistema Abierto de Informaci�n Geogr�fica
 * Kosmo - Open Geographical Information System
 *
 * http://www.saig.es
 * (C) 2007, SAIG S.L.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * For more information, contact:
 * 
 * Sistemas Abiertos de Informaci�n Geogr�fica, S.L.
 * Avnda. Rep�blica Argentina, 28
 * Edificio Domocenter Planta 2� Oficina 7
 * C.P.: 41930 - Bormujos (Sevilla)
 * Espa�a / Spain
 *
 * Tel�fono / Phone Number
 * +34 954 788876
 * 
 * Correo electr�nico / Email
 * info@saig.es
 *
 */
package org.openjump.advancedtools.plugins;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.openjump.advancedtools.utils.WorkbenchUtils;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.model.Layerable;
import com.vividsolutions.jump.workbench.plugin.AbstractPlugIn;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.AbstractSelection;

/**
 * Executes the selection
 * <p>
 * </p>
 * 
 * @author Sergio Ba&ntilde;os Calvo - sbc@saig.es
 * @since Kosmo 1.1
 * @author Giuseppe Aruta
 * @since OpenJUMP 1.10
 */
public class CalculateSelectionPlugIn extends AbstractPlugIn {

    /** Flag to indicate that the shift button has been pressed */
    protected boolean wasShiftPressed;

    /** Query geometry */
    protected Geometry fence;

    /** Selection type */
    protected AbstractSelection selection;

    /** Layer list to filter by */
    protected List<Layerable> layersToFilter;

    /** Calling tool name */
    protected String name;

    /**
     * @param toolName
     *            Tool name (to show it in the progress dialog)
     * @param shiftPressed
     *            True if the Shift button has been pressed
     * @param fence
     *            Query geometry
     * @param selection
     * @param layersToFilter
     */
    public CalculateSelectionPlugIn(String toolName, boolean shiftPressed,
            Geometry fence, AbstractSelection selection,
            List<Layerable> layersToFilter) {
        this.name = toolName;
        this.wasShiftPressed = shiftPressed;
        this.fence = fence;
        this.selection = selection;
        this.layersToFilter = layersToFilter;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean execute(PlugInContext context) throws Exception {
        reportNothingToUndoYet(context);
        WorkbenchUtils.checkActiveTaskWindow();
        if (!wasShiftPressed) {
            context.getLayerViewPanel().getSelectionManager().clear();
        }
        @SuppressWarnings("unchecked")
        Map<Layer, Collection<Feature>> layerToFeaturesInFenceMap = context
                .getLayerViewPanel().visibleLayerToFeaturesInFenceMap(fence);

        if (!layerToFeaturesInFenceMap.isEmpty()) {
            // monitor.report("Seleccionando elementos" + "...");
            refreshSelection(layerToFeaturesInFenceMap, context);
        } else if (layerToFeaturesInFenceMap.isEmpty()) {
            WorkbenchUtils.discardSelection();
        }

        return false;
    }

    /**
     * Refresca la seleccion a partir del mapa de elementos seleccionados para
     * cada capa
     * 
     * @param layerToFeaturesInFenceMap
     * @param panel
     * @param monitor
     * @throws Exception
     */
    protected void refreshSelection(
            Map<Layer, Collection<Feature>> layerToFeaturesInFenceMap,
            PlugInContext context) throws Exception {
        Layer layer = context.getSelectedLayer(0);
        boolean originalPanelUpdatesEnabled = context.getLayerViewPanel()
                .getSelectionManager().arePanelUpdatesEnabled();
        context.getLayerViewPanel().getSelectionManager()
                .setPanelUpdatesEnabled(false);
        if (!wasShiftPressed) {
            context.getLayerViewPanel().getSelectionManager().clear();
        }

        try {

            Collection<Feature> featuresToSelect = layerToFeaturesInFenceMap
                    .get(layer);

            Collection<Feature> featuresToUnselect = selection
                    .getFeaturesWithSelectedItems(layer);

            if (featuresToSelect == null) {
                featuresToSelect = Collections.emptyList();

            }
            // selection.unselectItems();
            // featuresToUnselect.retainAll(featuresToSelect);
            // featuresToSelect.removeAll(featuresToUnselect);
            selection.selectItems(layer, featuresToSelect);

            if (wasShiftPressed) {
                selection.unselectItems(layer, featuresToUnselect);
            }
        } finally {
            context.getLayerViewPanel().getSelectionManager()
                    .setPanelUpdatesEnabled(originalPanelUpdatesEnabled);
        }

        context.getLayerViewPanel().getSelectionManager()
                .setPanelUpdatesEnabled(originalPanelUpdatesEnabled);
        context.getLayerViewPanel().getSelectionManager().updatePanel();

    }

}