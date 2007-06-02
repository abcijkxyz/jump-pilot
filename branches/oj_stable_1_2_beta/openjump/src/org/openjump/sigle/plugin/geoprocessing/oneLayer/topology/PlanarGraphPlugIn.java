package org.openjump.sigle.plugin.geoprocessing.oneLayer.topology;

/*Cet outil a �t� developp� par Michael Michaud Juin 2005
 * Erwan Bocher a ajout� la r�cup�ration des attributs lors du calcul de la topologie
 */


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openjump.sigle.utilities.geom.FeatureCollectionUtil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.util.LinearComponentExtracter;
import com.vividsolutions.jts.operation.linemerge.LineMerger;
import com.vividsolutions.jts.operation.polygonize.Polygonizer;
import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.feature.AttributeType;
import com.vividsolutions.jump.feature.BasicFeature;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jump.feature.FeatureDataset;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.feature.IndexedFeatureCollection;
import com.vividsolutions.jump.task.TaskMonitor;
import com.vividsolutions.jump.tools.AttributeMapping;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.plugin.EnableCheckFactory;
import com.vividsolutions.jump.workbench.plugin.MultiEnableCheck;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.plugin.ThreadedBasePlugIn;
import com.vividsolutions.jump.workbench.ui.MenuNames;
import com.vividsolutions.jump.workbench.ui.MultiInputDialog;

/**
 * PlanarGraphPlugIn compute a planar graph from a set of features.
 * The user can choose to produce the nodes, the edges and the faces, or only
 * some of those features.
 * The following relations are kept by edge features as integer attributes
 * containing node and/or faces identifiers :
 * Edges :<br>
 *     Initial node identifier<br>
 *     Final node identifier<br>
 *     Right face<br>
 *     Left face<br>
 * @author Michael Michaud and Erwan Bocher (2005-06)
 * Comments added by Michael Michaud on 2006-05-01
 */
public class PlanarGraphPlugIn extends ThreadedBasePlugIn {
    
       
    GeometryFactory gf = new GeometryFactory();        
        
    //   Calcul des noeuds
    private static boolean nodeb = true;
    // Calcul des faces
    public static boolean faceb = true;
    // Calcul des relations arcs/noeuds et/ou arcs/faces
    private static boolean relb = true;
    
    //Options pour rappatrier les attributs de la couche d'entr�e
    private static boolean attributesb = true;
    
    private static String LEFT_FACE = "LeftFace";
    private static String RIGHT_FACE = "RightFace";
    private static String INITIAL_NODE = "StartNode";
    private static String FINAL_NODE = "EndNode";
    
    private String layerName;    
        
    //-- strings are replaced later
    private String sNode="Node";
    private String sFace="Face";
    private String sEdge="Edge";
    private String sCategoryName="Graph";
    private String sMapping="Mapping";
    	
    public Collection edges;
    
    private MultiInputDialog mid;
    
        
    // Dans le run je fais les traitements 
    
    public void run(TaskMonitor monitor, PlugInContext context)
        throws Exception {
        
        // Ici je declare fcFace pour y avoir acces dans le run 
        FeatureCollection fcFace = null;        
    
        // recuperation de la couche et des options coch�es
        Layer layer = mid.getLayer(I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Select-layer-to-analyse"));
        FeatureCollection fcSource = layer.getFeatureCollectionWrapper();
        layerName = layer.getName();
        nodeb = mid.getBoolean(I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Calculate-nodes"));
        faceb = mid.getBoolean(I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Calculate-faces"));
        relb = mid.getBoolean(I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Calculate-the-relations-arcs-nodes-and-/or-arcs-faces"));
        attributesb = mid.getBoolean(I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Keep-attributes")); 
        
        // Get linear elements from all geometries in the layer
        monitor.report(I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Searching-for-linear-elements"));
        List list = getLines(fcSource);
        monitor.report(I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Number-of-found-elements") + ": " + list.size());
        
        // Union the lines (unioning is the most expensive operation)
        monitor.report(I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Generate-layer-of-arcs"));
        FeatureCollection fcEdge = createEdgeLayer(
            layer.getFeatureCollectionWrapper(), nodeb, faceb, relb, context);
        monitor.report(I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Arc-layer-generated"));
        
        // Create the node Layer
        monitor.report(I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Create-nodes"));
        if (nodeb) {
            FeatureCollection fcNode = createNodeLayer(fcEdge, context, relb);
        }
        monitor.report(I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Layer-with-nodes-generated"));
        
        // Create face Layer from edges with Polygonizer
        monitor.report(I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Create-faces"));
        if (faceb) {
            fcFace = createFaceLayer(fcEdge, context, relb);
        }
        monitor.report(I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Layer-of-faces-generated"));
    
        //Erwan aout 2005
        //Ici on applique la proc�dure pour r�cuperer les attributs de la couche d'origine
        //Les attributs sont rappatri�s si l'entit� produite est contenue dans l'entit� source
        // Si la couche d'entr�e est une couche de polygones alors les attributs sont rappatri�s pour la couche de faces
        // Si la couche d'entr�e est une couche de linestring alors les attributs sont rappatri�s pour la couche d'arcs
       
        if (faceb){
            Feature fWithin = null;
            AttributeMapping mapping = null;
            
            if (attributesb) {
                //J'exploite la methode mapping pour recuperer les attributs
                mapping = new AttributeMapping(new FeatureSchema(), new FeatureSchema());
                List aFeatures = null;
                monitor.report(I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Transfer-of-attributes"));
                if (FeatureCollectionUtil.getFeatureCollectionDimension(fcSource)==2){
                    mapping = new AttributeMapping(fcSource.getFeatureSchema(), fcFace.getFeatureSchema());
                    aFeatures = fcFace.getFeatures();
                }
                else if (FeatureCollectionUtil.getFeatureCollectionDimension(fcSource)==1) {
                    mapping = new AttributeMapping(fcSource.getFeatureSchema(), fcFace.getFeatureSchema());
                    aFeatures = fcEdge.getFeatures();
                }
                        
                FeatureDataset fcRecup = new FeatureDataset(mapping.createSchema("GEOMETRY"));
                IndexedFeatureCollection indexedB = new IndexedFeatureCollection(fcSource);
            
                for (int i = 0; (i < aFeatures.size());i++) {
                    Feature aFeature = (Feature) aFeatures.get(i);
                    Feature feature = new BasicFeature(fcRecup.getFeatureSchema());
                    int nbFeatureWithin = 0;
                    for (Iterator j = indexedB.query(aFeature.getGeometry().getEnvelopeInternal()).iterator();
                        j.hasNext() && !monitor.isCancelRequested();) {
                        
                        Feature bFeature = (Feature) j.next();
                        if (aFeature.getGeometry().within(bFeature.getGeometry())) {
                            nbFeatureWithin++;
                            fWithin = bFeature;
                        }
                    }
                    // on ne transfere les attributs que lorsque la geometry resultat 
                    // n'est contenue que une seule geometry source
                    if (nbFeatureWithin == 1 && attributesb) {
                        mapping.transferAttributes(fWithin, aFeature, feature);
                    }
                    // on clone la geometry pour que les modifs sur la geometry source 
                    // ne soient pas transferees sur la geometry resultat
                    feature.setGeometry((Geometry) aFeature.getGeometry().clone()); 
                    fcRecup.add(feature);
                }                           
                context.getLayerManager().addLayer(this.sCategoryName, layerName + "_" + this.sMapping, fcRecup);
            }
            else {
                // Michael Michaud : Debug : gcFace is not in this else statement
                //context.getLayerManager().addLayer("Graph", layerName + "_Face", fcFace);
            }
            context.getLayerManager().addLayer(this.sCategoryName, layerName + "_" + this.sFace, fcFace);
        }
    }         
    
    /**
     * @param featureCollectionWrapper
     * @param attributesb2
     * @param context
     * @return
     */

    public void initialize(PlugInContext context) throws Exception {
        context.getFeatureInstaller().addMainMenuItem(this,new String[] { MenuNames.TOOLS, MenuNames.TOOLS_ANALYSIS }, 
                this.getName(), false, null, 
                new MultiEnableCheck().add(new EnableCheckFactory(context.getWorkbenchContext()).createTaskWindowMustBeActiveCheck())
                .add(new EnableCheckFactory(context.getWorkbenchContext()).createAtLeastNLayersMustExistCheck(1))
                ); 
    }
   
    public boolean execute(PlugInContext context) throws Exception {
    	this.sEdge = I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Edge");
    	this.sFace = I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Face");
    	this.sNode = I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Node");
    	this.sCategoryName = I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Graph");
    	this.sMapping = I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Mapping");
    	
        initDialog(context);
        mid.setVisible(true);
        mid.wasOKPressed();
        return mid.wasOKPressed();
    }

    public String getName(){
    	return I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Planar-Graph");	
    }
    

    private void initDialog(PlugInContext context) {
        
        mid = new MultiInputDialog(context.getWorkbenchFrame(), I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Topologic-Analysis"), true);
        //-- note: the strings must be similar to those used in #run() to get the correct variables
        mid.addLayerComboBox(I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Select-layer-to-analyse"), context.getLayerManager().getLayer(0), context.getLayerManager());
        mid.addLabel(I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.The-layer-of-arcs-is-always-generated"));
        mid.addCheckBox(I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Calculate-nodes"), nodeb);
        mid.addCheckBox(I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Calculate-faces"), faceb);
        mid.addCheckBox(I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Calculate-the-relations-arcs-nodes-and-/or-arcs-faces"), relb);
        mid.addCheckBox(I18N.get("org.openjump.sigle.plugin.PlanarGraphPlugIn.Keep-attributes"), attributesb);
        mid.pack();
        //mid.show();
    }

    // ************************************************
    // extract lines from a feature collection
    // ************************************************
    public List getLines(FeatureCollection fc) {
        List linesList = new ArrayList();
        LinearComponentExtracter filter = new LinearComponentExtracter(linesList);
        int count = 0;
        for (Iterator i = fc.iterator(); i.hasNext(); ) {
            Geometry g = ((Feature)i.next()).getGeometry();
            g.apply(filter);
        }
        return linesList;
    }
    
    // ************************************************
    // Create edge layer
    // ************************************************
    public FeatureCollection createEdgeLayer(FeatureCollection fc,
        boolean nodeb, boolean faceb, boolean relations,
        PlugInContext context) {
        // Schema edge
        FeatureSchema fsEdge = new FeatureSchema();
          fsEdge.addAttribute("GEOMETRY", AttributeType.GEOMETRY);
          fsEdge.addAttribute("ID", AttributeType.INTEGER);
          // Edge - Node relation
          if (nodeb && relations) {
              fsEdge.addAttribute(INITIAL_NODE, AttributeType.INTEGER);
              fsEdge.addAttribute(FINAL_NODE, AttributeType.INTEGER);
          }
          // Edge - Face relation
          if (faceb && relations) {
              fsEdge.addAttribute(RIGHT_FACE, AttributeType.INTEGER);
              fsEdge.addAttribute(LEFT_FACE, AttributeType.INTEGER);
          }
        FeatureDataset fcEdge = new FeatureDataset(fsEdge);
        
        // Get linear elements from all geometries in the layer
        List list = getLines(fc);
        
        // Union the lines (unioning is the most expensive operation)
        Geometry geom = gf.createMultiLineString(gf.toLineStringArray(list));
        geom = gf.createMultiLineString(null).union(geom);
        GeometryCollection gc = geom instanceof GeometryCollection ?
            (GeometryCollection)geom:
            gf.createGeometryCollection(new Geometry[]{geom});
        
        // Create the edge layer by merging lines between 3+ order nodes
        // (Merged lines are multilines)
        LineMerger lineMerger = new LineMerger();
        for (int i = 0 ; i < gc.getNumGeometries() ; i++) {
            lineMerger.add(gc.getGeometryN(i));
        }
        edges = lineMerger.getMergedLineStrings();
        int no = 0;
        for (Iterator it = edges.iterator() ; it.hasNext() ;) {
            Feature f = new BasicFeature(fsEdge);
            f.setGeometry((Geometry)it.next());
            f.setAttribute("ID", new Integer(++no));
            fcEdge.add(f);
        }
        context.getLayerManager().addLayer(this.sCategoryName, layerName+"_"+this.sEdge, fcEdge);
        return fcEdge;
    }
    
    // ************************************************
    // Create node layer
    // ************************************************
    public FeatureCollection createNodeLayer(FeatureCollection fcEdge,
        PlugInContext context, boolean relations) {
        FeatureSchema fsNode = new FeatureSchema();
        fsNode.addAttribute("GEOMETRY", AttributeType.GEOMETRY);
        fsNode.addAttribute("ID", AttributeType.INTEGER);
        FeatureDataset fcNode = new FeatureDataset(fsNode);
        
        // Create the node Layer
        Map nodes = new HashMap();
        //List edges = geometriesFromFeatures(fcEdge);
        for (Iterator it = edges.iterator() ; it.hasNext() ;) {
            Coordinate[] cc = ((Geometry)it.next()).getCoordinates();
            nodes.put(cc[0], gf.createPoint(cc[0]));
            nodes.put(cc[cc.length-1], gf.createPoint(cc[cc.length-1]));
        }
        int no = 0;
        for (Iterator it = nodes.values().iterator() ; it.hasNext() ; ) {
            Feature f = new BasicFeature(fsNode);
            f.setGeometry((Geometry)it.next());
            f.setAttribute("ID", new Integer(++no));
            nodes.put(f.getGeometry().getCoordinate(), f);
            fcNode.add(f);
        }
        context.getLayerManager().addLayer(this.sCategoryName, layerName+"_"+this.sNode, fcNode);
        
        // Compute the relation between edges and nodes
        if (relations) {
            for (Iterator it = fcEdge.iterator() ; it.hasNext() ;) {
                Feature f = (Feature)it.next();
                Coordinate[] cc = f.getGeometry().getCoordinates();
                f.setAttribute(INITIAL_NODE, ((Feature)nodes.get(cc[0])).getAttribute("ID"));
                f.setAttribute(FINAL_NODE, ((Feature)nodes.get(cc[cc.length-1])).getAttribute("ID"));
            }
        }
        return fcNode;
    }
    
    // ************************************************
    // Create face layer
    // ************************************************
    public FeatureCollection createFaceLayer(FeatureCollection fcEdge,
        PlugInContext context, boolean relations) {
        // Create the face layer
        FeatureSchema fsFace = new FeatureSchema();
        fsFace.addAttribute("GEOMETRY", AttributeType.GEOMETRY);
        fsFace.addAttribute("ID", AttributeType.INTEGER);
        FeatureDataset fcFace = new FeatureDataset(fsFace);
        
        Polygonizer polygonizer = new Polygonizer();
        polygonizer.add(edges);
        int no = 0;
        for (Iterator it = polygonizer.getPolygons().iterator() ; it.hasNext() ;) {
            Feature f = new BasicFeature(fsFace);
            f.setGeometry((Geometry)it.next());
            f.setAttribute("ID", new Integer(++no));
            System.out.println(this.sFace + ": " + f.getID() + " : " + f.getAttribute("ID"));
            fcFace.add(f);
        }
        //context.getLayerManager().addLayer("Graph", layerName+"_Face", fcFace);
        
        // inscrit les num�ros de face dans les arcs
        // Les arcs qui sont en bords de face sont cod�s � -1.
        if(relations) {
            for (Iterator it = fcEdge.getFeatures().iterator() ; it.hasNext() ; ) {
                Feature edge = (Feature)it.next();
                Geometry g1 = edge.getGeometry();
                List list = fcFace.query(g1.getEnvelopeInternal());
                for (int i = 0 ; i < list.size() ; i++) {
                    Feature face = (Feature)list.get(i);
                    Geometry g2 = face.getGeometry();
                    Geometry inters = g2.intersection(g1);
                    // Michael Michaud : added on 2006-05-01
                    // Process properly the case of empty intersection
                    if (inters.isEmpty()) continue;
                    else if (inters.getLength()>0) {
                        Integer idValue = (Integer) face.getAttribute("ID");
                        if (!idValue.equals("")) {
                            if (inters.getCoordinates()[0].equals(g1.getCoordinates()[0])) {
                                edge.setAttribute(RIGHT_FACE, face.getAttribute("ID"));
                            }
                            else {edge.setAttribute(LEFT_FACE, face.getAttribute("ID"));}
                        }
                    }
                    else {
                        if (inters.getCoordinates()[0].equals(g1.getCoordinates()[0])) {
                            edge.setAttribute(RIGHT_FACE, new Integer(-1));
                        }
                        else {edge.setAttribute(LEFT_FACE, new Integer(-1));}
                    }  
                        
                }
            }
        }
        return fcFace;
    }
     
}


