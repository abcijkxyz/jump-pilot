package fr.michaelm.jump.bsheditor;

import com.vividsolutions.jump.workbench.plugin.Extension;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;

// v 0.2.4 (2012-11-12) fix the plugin name in the menu
// v 0.2.3 (2012-mm-jj)
public class BshEditorPlugInExtension extends Extension {
    public void configure(PlugInContext context) throws Exception {
        new BeanShellEditorPlugIn().initialize(context);
    }
    public String getName() {return "BeanShell Script Editor";}
    public String getVersion() {return "0.3.0 (2020-06-12)";}
}
