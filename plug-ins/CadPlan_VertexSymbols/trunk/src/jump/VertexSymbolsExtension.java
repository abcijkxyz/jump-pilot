package com.cadplan.jump;

import com.cadplan.jump.plugins.StylerMenuPlugIn;
import com.vividsolutions.jump.workbench.plugin.Extension;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;

public class VertexSymbolsExtension extends Extension {
	@Override
	public void configure(PlugInContext context) throws Exception {
		//	new VertexSymbolsPlugIn().initialize(context);
		//	new VertexNotePlugin().initialize(context);
		new StylerMenuPlugIn().initialize(context);


	}

	@Override
	public String getVersion() {
		return "0.20 (2020-05-30)";
	}
	
	@Override
	public String getName() {
		return "VertexSymbol - © 2005 Geoffrey G Roy. Modified version by Giuseppe Aruta 2020";
	}
}
