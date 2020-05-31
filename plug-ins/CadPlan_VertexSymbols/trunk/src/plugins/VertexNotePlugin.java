package com.cadplan.jump.plugins;

import javax.swing.ImageIcon;

import com.cadplan.jump.icon.IconLoader;
import com.cadplan.jump.language.I18NPlug;
import com.vividsolutions.jump.workbench.plugin.AbstractPlugIn;
import com.vividsolutions.jump.workbench.plugin.EnableCheck;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.LayerViewPanel;
import com.vividsolutions.jump.workbench.ui.MenuNames;

public class VertexNotePlugin extends AbstractPlugIn {
	public static ImageIcon ICON = IconLoader.icon("noteicon.gif");

	@Override
	public void initialize(PlugInContext paramPlugInContext) throws Exception {
		EnableCheckFactory check = new EnableCheckFactory(paramPlugInContext.getWorkbenchContext());
		EnableCheck scheck = check.createAtLeastNFeaturesMustBeSelectedCheck(1);
		MultiEnableCheck mcheck = new MultiEnableCheck();
		mcheck.add(check.createAtLeastNLayersMustExistCheck(1));
		mcheck.add(check.createAtLeastNLayersMustBeEditableCheck(1));
		String str = MenuNames.PLUGINS;
		paramPlugInContext.getFeatureInstaller().addMainMenuPlugin(this, new String[]{str, MenuNames.STYLE}, this.getName(), false, ICON, mcheck);
		// paramPlugInContext.getWorkbenchFrame().getToolBar().addPlugIn(ICON, this, mcheck, paramPlugInContext.getWorkbenchContext());
		paramPlugInContext.getFeatureInstaller().addPopupMenuPlugin(LayerViewPanel.popupMenu(), this, this.getName(), false, ICON, scheck);
	}

	@Override
	public String getName() {
		return I18NPlug.getI18N("VertexNote.MenuItem");
	}

	@Override
	public boolean execute(PlugInContext paramPlugInContext) throws Exception {
		new VertexNoteDialog(paramPlugInContext);
		return true;
	}
}
