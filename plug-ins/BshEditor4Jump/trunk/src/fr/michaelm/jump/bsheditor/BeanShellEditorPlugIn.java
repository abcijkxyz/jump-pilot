package fr.michaelm.jump.bsheditor;

import com.vividsolutions.jump.workbench.plugin.AbstractPlugIn;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.MenuNames;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import fr.michaelm.bsheditor.BeanShellEditor;

public class BeanShellEditorPlugIn extends AbstractPlugIn {
    
    public void initialize(PlugInContext context) {
        context.getFeatureInstaller()
                .addMainMenuPlugin(this,
                        new String[] { MenuNames.CUSTOMIZE },
                        getName() + "...",
                        false,
                        null,
                        null);
    }

    public String getName() {
        ResourceBundle i18n = ResourceBundle.getBundle(
                "BeanShellEditor_i18n",
                Locale.getDefault()
        );
        return i18n.getString("name");
    }

    public boolean execute(PlugInContext context) throws Exception {
        Map map = new HashMap();
        map.put("wc", context.getWorkbenchContext());
        BeanShellEditor e = new BeanShellEditor(map, null);
        e.addInitStatement("import com.vividsolutions.jump.feature.*;");
        e.addInitStatement("import com.vividsolutions.jts.geom.*;");
        e.addInitStatement("import com.vividsolutions.jump.workbench.model.*;");
        return true;
    }
}
