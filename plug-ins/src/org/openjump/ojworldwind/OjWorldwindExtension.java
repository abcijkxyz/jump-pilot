package org.openjump.ojworldwind;

import com.vividsolutions.jump.workbench.plugin.Extension;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;

public class OjWorldwindExtension extends Extension {

    /**
     * calls PlugIn using class method xplugin.initialize()
     */
    public void configure(PlugInContext context) throws Exception{
        new OjWorldwindPlugin().initialize(context);
    }
}
