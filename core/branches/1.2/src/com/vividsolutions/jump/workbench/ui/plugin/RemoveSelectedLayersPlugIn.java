
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

package com.vividsolutions.jump.workbench.ui.plugin;

import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.model.Layerable;
import com.vividsolutions.jump.workbench.plugin.AbstractPlugIn;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;


public class RemoveSelectedLayersPlugIn extends AbstractPlugIn {
    public RemoveSelectedLayersPlugIn() {
    }

    public boolean execute(PlugInContext context) throws Exception {
        // Changed on 2007-05-21 to use the new LayerManager()[Michael Michaud]
        remove(context, (Layerable[]) (context.getLayerNamePanel()).selectedNodes(
                Layerable.class).toArray(new Layerable[] {  }));
        System.gc();
        return true;
    }

    public void remove(PlugInContext context, Layerable[] selectedLayers) {
        for (int i = 0; i < selectedLayers.length; i++) {
            // SIGLE start [obedel]
            // dispose layer immediately
            //selectedLayers[i].getLayerManager().remove(selectedLayers[i]);
            // ... becomes ...
            // Changed again by [Michael Michaud] on 2007-05-21 to solve the memory leak
            // thanks to the new LayerManager.dispose(PlugInContext, Layerable) method
            selectedLayers[i].getLayerManager()
                             .dispose(context.getWorkbenchFrame(), selectedLayers[i]);
            // SIGLE end
        }
        //Don't call LayerManager#setFiringEvents and
        //LayerManager#fireModelChanged, so that each
        //removed node is individually removed from the tree. [Jon Aquino]
    }

    public MultiEnableCheck createEnableCheck(WorkbenchContext workbenchContext) {
        EnableCheckFactory checkFactory = new EnableCheckFactory(workbenchContext);

        return new MultiEnableCheck().add(checkFactory.createWindowWithLayerNamePanelMustBeActiveCheck())
                                     .add(checkFactory.createAtLeastNLayerablesMustBeSelectedCheck(
                1, Layerable.class));
    }
}
