/*----------------    FILE HEADER  ------------------------------------------

Copyright (C) 2001-2005 by:
lat/lon GmbH
http://www.lat-lon.de

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

Contact:

Andreas Poth
lat/lon GmbH
Aennchenstraße 19
53177 Bonn
Germany


 ---------------------------------------------------------------------------*/

package de.latlon.deejump.plugin.wfs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.deegree.model.geometry.GM_Envelope;
import org.deegree.services.wfs.protocol.WFSGetFeatureRequest;
import org.deegree.xml.DOMPrinter;
import org.deegree.xml.XMLTools;
import org.deegree_impl.model.geometry.GM_Envelope_Impl;
import org.deegree_impl.model.geometry.GeometryFactory;
import org.w3c.dom.Document;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.task.TaskMonitor;
import com.vividsolutions.jump.util.StringUtil;
import com.vividsolutions.jump.workbench.WorkbenchContext;
import com.vividsolutions.jump.workbench.model.FeatureEventType;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.model.LayerManager;
import com.vividsolutions.jump.workbench.model.StandardCategoryNames;
import com.vividsolutions.jump.workbench.plugin.EnableCheck;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.plugin.ThreadedBasePlugIn;
import com.vividsolutions.jump.workbench.ui.HTMLFrame;
import com.vividsolutions.jump.workbench.ui.images.IconLoader;

import de.latlon.deejump.DeeJUMPWorkbench;
import de.latlon.deejump.util.data.JUMPFeatureFactory;

/**
 * Plug-in to update a wfs layer
 * 
 * @author <a href="mailto:taddei@lat-lon.de">Ugo Taddei</a>
 * 
 */
public class UpdateWFSLayerPlugIn extends ThreadedBasePlugIn {

    public static final String RELOAD_LAYER_KEY = "RELOAD_LAYER";

    
    //used to check when there are mixed geometries.
    // not used here yet
    private static final Map GEOMETRIES;
    
    private static Logger LOG = Logger.getLogger( DeeJUMPWorkbench.class );
	    
    static{
        HashMap tmpGEOMETRIES = new HashMap(3);
        //TODO internationalize this
        tmpGEOMETRIES.put( LineString.class, "Lines" );
        tmpGEOMETRIES.put( Polygon.class, "Polygons" );
        tmpGEOMETRIES.put( Point.class, "Points" );
        
        GEOMETRIES = Collections.unmodifiableMap( tmpGEOMETRIES );
    }
    
    public static final FeatureEventType UPDATE_GEOM = FeatureEventType.GEOMETRY_MODIFIED; 

    public static final FeatureEventType UPDATE_ATTR = FeatureEventType.ATTRIBUTES_MODIFIED; 
    
    public static final FeatureEventType DELETE = FeatureEventType.DELETED; 

    public static final FeatureEventType INSERT = FeatureEventType.ADDED; 
    
    
    
    private StringBuffer updateRequest = null;
    private StringBuffer updateGeomRequest = null;
    private StringBuffer updateAttrRequest = null;
    private StringBuffer deleteRequest = null;
    private StringBuffer insertRequest = null;

    private String wfsUrl;

    private String layerName;

    private WFSLayer layer;

    // if user added new geoms, need to reload from DB
    private boolean hasInserted = false;
    
    public void install( PlugInContext context ) throws Exception {

        context.getWorkbenchContext().getWorkbench().getFrame().getToolBar().addPlugIn(
            getIcon(),
            this, 
            createEnableCheck(context.getWorkbenchContext()),
            context.getWorkbenchContext()
        );        	
    }
    
    public boolean execute(PlugInContext context) throws Exception {
        
        if (wfsUrl == null ){
            //hu! TODO WFS layer must keep track of it server.
            wfsUrl = null;
        }

        Collection collec =  context.getLayerManager()
        						.getLayersWithModifiedFeatureCollections();
        
        //TODO srs from WFS too?
//        TransactionFactory.setSrs( null );
        
        Layer candidatelayer = context.getSelectedLayer( 0 );
        
        if ( candidatelayer instanceof WFSLayer ) {
            layer = (WFSLayer)candidatelayer;
            layerName = layer.getOriginalName();
        } else {
            return false;
        }
        
        
        /*
        
        // because of enable check there can be only one layer, 
        //but make it safe, anyway
        //[UT] 25.08.2005 
        //FIXME not quite working this was
        Iterator iter = collec.iterator();
        if ( iter.hasNext() ){
            Object o = iter.next();
            if ( o instanceof WFSLayer ) {
                WFSLayer layer = (WFSLayer) o;
                layerName = layer.getOriginalName();
            }
        } else {
            return false;
        }
        */
        this.wfsUrl = layer.getServerURL();
        
        
        /* no check needed here, only in 1st project
        if( !checkGeometries( layer.getGeometryClass(),  
                layer.getFeatureCollectionWrapper())){
            // there are mixed geometries
            
            Object[] arguments = {GEOMETRIES.get( layer.getGeometryClass() )};

            //TODO internat. mesg
                String result = MessageFormat.format("UpdateWFSLayerPlugIn.mixedGeomError",
                    arguments);
            
            JOptionPane.showMessageDialog(
                    context.getWorkbenchContext().getWorkbench().getFrame(),
                    result,
                    "ERROR", JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        */
        
        
        // get lists and maps with changed, deleted and/or inserted features
		HashMap changedFeaturesMap = layer.getLayerListener().getChangedFeaturesMap(); 
		
        ArrayList updateGeomFeatures = (ArrayList)changedFeaturesMap.get( UPDATE_GEOM );
        ArrayList updateAttrFeatures = (ArrayList)changedFeaturesMap.get( UPDATE_ATTR );
        HashMap oldGeomFeatures = layer.getLayerListener().getOldGeomFeaturesMap();
        HashMap oldAttrFeatures = layer.getLayerListener().getOldAttrFeaturesMap();
        ArrayList delFeatures = (ArrayList)changedFeaturesMap.get( DELETE );
        ArrayList newFeatures = (ArrayList)changedFeaturesMap.get( INSERT );
		
        String geoPropName = layer.getGeoPropertyName();
        
		// UPDATE Geom
		if ( updateGeomFeatures.size() > 0 ){
		    updateGeomRequest = TransactionFactory.createUpdateTransaction( 
		            UPDATE_GEOM, layer.getOriginalName(), geoPropName, 
		            updateGeomFeatures, oldGeomFeatures);
		}
        
		// UPDATE Attr
		if ( updateAttrFeatures.size() > 0 ){
		    updateAttrRequest = TransactionFactory.createUpdateTransaction( 
		            UPDATE_ATTR, layer.getOriginalName(), geoPropName, 
		            updateAttrFeatures, oldAttrFeatures);
		}
		
        // now CONCAT updates into one request
		updateRequest = TransactionFactory.createCommonUpdateTransaction( 
                updateGeomRequest, updateAttrRequest);
            
        // DELETE
        if ( delFeatures.size() > 0 ){
            deleteRequest = TransactionFactory.createTransaction(
                    DELETE,layer.getOriginalName(), null, delFeatures);
        }
        
        // INSERT
        if ( newFeatures.size() > 0 ){
            insertRequest = TransactionFactory.createTransaction( 
                INSERT,layer.getOriginalName(), geoPropName, newFeatures); 
            hasInserted = true; 
        }
        
        // clean up listener and mark layer as saved
        layer.getLayerListener().reset();
        layer.setFeatureCollectionModified( false );
        
        return true;
      
    }
    /** 
     * @see com.vividsolutions.jump.workbench.plugin.ThreadedPlugIn#run(com.vividsolutions.jump.task.TaskMonitor, com.vividsolutions.jump.workbench.plugin.PlugInContext)
     */
    public void run(TaskMonitor monitor, PlugInContext context)
            throws Exception {
        			//TODO
        monitor.report("UpdateWFSLayerPlugIn.message");
        StringBuffer mesg = new StringBuffer();
		
		try {
		    
		    //TODO provide a better way to output a feedback mesg
		    //FIXME can't remember why test was for null only, have now to includ "".euqals -> change this!
		    if ( updateRequest != null && !"".equals( updateRequest.toString() ) ){
			    mesg.append( 
			            doTransaction( "UPDATE", updateRequest.toString() ) );
		    }
		    
		    if ( insertRequest != null && !"".equals( insertRequest.toString() )){
		        mesg.append(
		                doTransaction( "INSERT", insertRequest.toString()));
		    }     
		    
		    if ( deleteRequest != null && !"".equals( deleteRequest.toString() )){
	            mesg.append( 
                    	doTransaction( "DELETE", deleteRequest.toString()) );
		    }
            
            showOutput( context, mesg);
            /*
            if ( hasInserted ){
                context.getLayerManager().remove( layer );
                
                reloadLayer(monitor, context, layerName );
            }*/
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally{
            insertRequest = null;
            updateRequest = null;
            deleteRequest = null;
            updateGeomRequest = null;
            updateAttrRequest = null;
            
            hasInserted = false;
        }
    }
    
    private String doTransaction(  String label, String xmlRequest )
	throws Exception{
        Document doc = null;
		
        String result  = "";
    	LOG.info( "\nWFS-T " + label + " REQUEST: " + xmlRequest + "\n" );
	
		HttpClient httpclient = new HttpClient();
		PostMethod httpMethod = new PostMethod( wfsUrl );
		httpMethod.setRequestBody( xmlRequest );
		httpclient.executeMethod(httpMethod);
		doc = XMLTools.parse( httpMethod.getResponseBodyAsStream() );
		/*result  = label + " " + DOMPrinter.nodeToString(doc);
		result = result.replace( '<', ' ');
		result = result.replaceAll( ">", "<br>");
		*/
		
		LOG.info( "\nWFS-T RESPONSE: "+ XMLTools.toString( doc )+  "\n" );
		
		return result;
	}
    private void showOutput(PlugInContext context, StringBuffer mesg){
        HTMLFrame out = context.getOutputFrame();
        out.createNewDocument();
        out.addHeader(2, "WFS Transaction");
        out.append( mesg.toString() );
    }
//TODO
    public String getName(){return "Update WFSLayer";}
    
    public ImageIcon getIcon() {
        return IconLoader.icon("Data.gif");
    }
    
    private boolean checkGeometries( Class comparisonGeo, FeatureCollection featCollec){
        
        List featList = featCollec.getFeatures();
        for (Iterator iter = featList.iterator(); iter.hasNext();) {
            Feature f = (Feature) iter.next();
            Geometry g = f.getGeometry();
            if( g instanceof GeometryCollection ){
                
                GeometryCollection geoCollec = (GeometryCollection)g;
                int nGeos = geoCollec.getNumGeometries();
                for (int i = 0; i < nGeos; i++) {
                    if (!(comparisonGeo == geoCollec.getGeometryN( i ) .getClass()) ){
                        return false;
                    }  
                }
                
                
            }else if (!(comparisonGeo == g.getClass()) ){
                return false;
            }
        }
        return true;
    }
    
    private void reloadLayer(TaskMonitor monitor, PlugInContext context, String layerName)
    	throws Exception {
        monitor.report( "AddWFSQueryPlugIn.generating" );
        
        Envelope jEnv = context.getLayerViewPanel().getViewport().getEnvelopeInModelCoordinates();

        GM_Envelope env = new GM_Envelope_Impl(
                GeometryFactory.createGM_Position(jEnv.getMinX(), jEnv.getMinY()),
                GeometryFactory.createGM_Position(jEnv.getMaxX(), jEnv.getMaxY())
        );
        
        
        WFSGetFeatureRequest gfr = JUMPFeatureFactory.createFeatureRequest(
                "1.0.0", layerName,env);

        org.deegree.model.feature.FeatureCollection dfc = 
            JUMPFeatureFactory.createDeegreeFCfromWFS( wfsUrl, gfr );
        
        /*HTMLFrame out = context.getOutputFrame();
        out.createNewDocument();
        out.addHeader(2, "Adding Layer '" + wfsLayerName + "' from WFS");
        out.addField("URL:", wfsUrl + ":" + ((WFSGetFeatureRequest_Impl)gfr).exportAsXML() );
        */
        //TODO
//        FeatureCollection dataset = JUMPFeatureFactory.createFromDeegreeFC( dfc, "dummy" );
        /*
        Feature f = (Feature)dataset.getFeatures().iterator().next();
        Geometry g = f.getGeometry();
        Class geoClass = g.getClass(); 
        if( g instanceof GeometryCollection ){
	        // then choose the first geometry as THE geo type 
            geoClass = ((GeometryCollection)g).getGeometryN( 0 ).getClass();            
        }
        
		if (dataset != null) {
		    
	        LayerManager layerManager = context.getLayerManager();
	        
	        String displayName = WFService.WFS_PREFIX + layerName;
	        WFSLayerListener layerListener = new WFSLayerListener( displayName );
	        layerManager.addLayerListener( layerListener );

	        WFSLayer layer = new WFSLayer(	displayName, 
		            						layerManager.generateLayerFillColor(),
		            						dataset,
		            						layerManager,
		            						layerName,
		            						this.layer.getGeoPropertyName());
		    layer.setLayerListener( layerListener );
		    
		    layerManager.addLayer(	StandardCategoryNames.SYSTEM, layer)		    
		    
	           .setDataSourceQuery(null)
	           .setFeatureCollectionModified(false);
		    
		    layer.setEditable( true );
		}
		*/
		        
    }
    public EnableCheck createEnableCheck(final WorkbenchContext workbenchContext) {
        EnableCheckFactory ecf = new EnableCheckFactory(workbenchContext);
        
        MultiEnableCheck mec = new MultiEnableCheck()
        	.add( createExactlyNWfsLayersMustBeSelectedCheck( workbenchContext, 1) )
        	.add( createFeatureMustHaveChangedCheck(workbenchContext) );
        
        return mec;
    }
    
    //TODO move into a factory of its own
    public static EnableCheck 
    	createExactlyNWfsLayersMustBeSelectedCheck( final WorkbenchContext workbenchContext, 
    	                                            final int n) {
              return new EnableCheck() {
                  public String check(JComponent component) {
                      return (
                              
                              workbenchContext.getLayerNamePanel() == null        
                              
                              ||
                              
                          n != (workbenchContext.getLayerNamePanel())
                                  .selectedNodes( WFSLayer.class )
                                  .size())                                
                                  
                          ? ("Exactly " + n + " layer" + StringUtil.s(n) + " must be selected")
                          : null;
                  }
              };
          }
      
      public EnableCheck 
      	createFeatureMustHaveChangedCheck( final WorkbenchContext workbenchContext ) {
          return new EnableCheck() {
              public String check(JComponent component) {
                  
                  return (
                      	workbenchContext.getLayerNamePanel()
                          .selectedNodes(WFSLayer.class)
                          .size() != 1
                          
                          ||
                          
                          !((Layer)workbenchContext.getLayerNamePanel()
                          .selectedNodes(WFSLayer.class)
                          .iterator().next()).isFeatureCollectionModified()
                          
                  )                                
                      ? "FeatureMustHaveChangedCheck"
                      : null;
              }
          };
      }

}
