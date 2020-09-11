package org.openjump.core.ui.plugin.layer.raster;

import com.vividsolutions.jump.workbench.plugin.Extension;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;

public class ImageExtension extends Extension
{
  private static final String NAME = "Save Sextante Raster to Image (Giuseppe Aruta - http://sourceforge.net/projects/opensit/)";
  private static final String VERSION = "0.2 (2013-09-24)";

  @Override
public String getName()
  {
    return NAME;
}

  @Override
public String getVersion()
  {
    return VERSION;
  }

  @Override
public void configure(PlugInContext context)
    throws Exception
  {
 
    new SaveImageToRasterPlugIn().initialize(context);
   
    
   
    
  }
}