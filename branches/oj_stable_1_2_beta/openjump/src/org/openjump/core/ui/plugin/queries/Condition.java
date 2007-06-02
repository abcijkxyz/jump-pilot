package org.openjump.core.ui.plugin.queries;

import java.util.Date;
import java.util.regex.Pattern;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import java.math.BigDecimal;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureCollection;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import buoy.widget.BComboBox;

/**
 * Condition
 * @author Michaël MICHAUD
 * @version 0.2 (16 Oct 2005)
 */ 
public class Condition  {
    QueryDialog query;
    Function ft;
    Operator op;
    Pattern pattern;   // only used for match & find functions
    PlugInContext context;
    
     public Condition(QueryDialog query, PlugInContext context) {
        this.query = query;
        this.ft=query.function;
        this.op=query.operator;
        if (op==Operator.MATC || op==Operator.FIND) {
            if (query.caseSensitive.getState())
                pattern = Pattern.compile((String)query.valueCB.getSelectedValue());
            else 
                pattern = Pattern.compile((String)query.valueCB.getSelectedValue(), Pattern.CASE_INSENSITIVE);
        }
        this.context = context;
    }
    
    public String toString() {
        String att = query.attribute.trim().equals("")?"GEOMETRY":query.attribute;
        String func = ft.toString().trim().equals("")?"":"."+ft;
        return "" + att + func + " " + op + " \"" +
               query.valueCB.getSelectedValue() + "\"";
    }
    
    public boolean test(Feature feature) throws Exception {
        Object o = null;
        //System.out.print("Nature de l'attribut : ");
        if(query.attributeType=='G') {
            //System.out.println(" géométrique");
            //System.out.println("Operator = " + op);
            o = feature.getGeometry();
            if(ft.type=='G') return test(gfunction((Geometry)o));
            else if(ft.type=='N') return test(nfunction((Geometry)o));
            else if(ft.type=='B') return test(bfunction((Geometry)o));
            else return false;
        }
        else {
            // System.out.println(" sémantique");
            // attributes which does not exist for this feature must have
            // been eliminated before the test procedure
            // (see QueryDialog#executeQuery())
            o = feature.getAttribute(query.attribute);
            if(o instanceof Boolean) return test(((Boolean)o).booleanValue());
            else if(o instanceof Integer) return test(((Integer)o).doubleValue());
            else if(o instanceof Long) return test(((Long)o).doubleValue());
            else if(o instanceof Double) return test(((Double)o).doubleValue());
            else if(o instanceof BigDecimal) return test(((BigDecimal)o).doubleValue());
            else if(o instanceof Date) return test((double)((Date)o).getTime());
            else if(o instanceof String && ft.type=='S') return test(sfunction((String)o));
            else if(o instanceof String && ft.type=='N') return test(nfunction((String)o));
            else return false;
        }
    }
    
    private boolean test(boolean b) throws Exception {
        boolean value=query.valueCB.getSelectedIndex()==0?true:false;
        if (b==value && op==Operator.BEQ) return true;
        else return false;
    }
    
    private boolean test(double d) throws Exception {
        double value = Double.parseDouble((String)query.valueCB.getSelectedValue());
        if (op==Operator.EQ && d==value) return true;
        else if (op==Operator.NE && d!=value) return true;
        else if (op==Operator.LT && d<value) return true;
        else if (op==Operator.GT && d>value) return true;
        else if (op==Operator.LE && d<=value) return true;
        else if (op==Operator.GE && d>=value) return true;
        else return false;
    }
    
    private boolean test(String s) throws Exception {
        String value = (String)query.valueCB.getSelectedValue();
        if (query.caseSensitive.getState()) {
            if (op==Operator.EQUA) return s.equals(value);
            else if (op==Operator.DIFF) return !s.equals(value);
            else if (op==Operator.STAR) return s.startsWith(value);
            else if (op==Operator.ENDS) return s.endsWith(value);
            else if (op==Operator.MATC) return pattern.matcher(s).matches();
            else if (op==Operator.FIND) return pattern.matcher(s).find();
            else if (op==Operator.BEFO) return s.compareTo(value)<=0;
            else if (op==Operator.AFTE) return s.compareTo(value)>=0;
            else return false;
        }
        else {
            if (op==Operator.EQUA) return s.equalsIgnoreCase(value);
            else if (op==Operator.DIFF) return !s.equalsIgnoreCase(value);
            else if (op==Operator.STAR) return s.toUpperCase().startsWith(value.toUpperCase());
            else if (op==Operator.ENDS) return s.toUpperCase().endsWith(value.toUpperCase());
            else if (op==Operator.MATC) return pattern.matcher(s).matches();
            else if (op==Operator.FIND) return pattern.matcher(s).find();
            else if (op==Operator.BEFO) return s.compareToIgnoreCase(value)<=0;
            else if (op==Operator.AFTE) return s.compareToIgnoreCase(value)>=0;
            else return false;
        }
    }
    
    private boolean test(Geometry g) throws Exception {
        int pos = query.valueCB.getSelectedIndex();
        // Target Geometry is the selection
        // System.out.println("position de la valeur sélectionnée : " + pos);
        if (pos==0) {
            for (Iterator it = query.selection.iterator() ; it.hasNext() ;) {
                Geometry p = (Geometry)it.next();
                if (op==Operator.INTER && g.intersects(p)) return true;
                else if (op==Operator.CONTA && g.contains(p)) return true;
                else if (op==Operator.WITHI && g.within(p)) return true;
                else if (op==Operator.WDIST && g.distance(p)<op.arg) return true;
                else if (op==Operator.TOUCH && g.touches(p)) return true;
                else if (op==Operator.CROSS && g.crosses(p)) return true;
                else if (op==Operator.OVERL && g.overlaps(p)) return true;
                else if (op==Operator.DISJO && g.disjoint(p)) return true;
                else;
            }
            return false;
        }
        else if (pos==1) {
            Layer[] ll = context.getLayerNamePanel().getSelectedLayers();
            for (int i = 0 ; i < ll.length ; i++) {
                FeatureCollection fc = ll[i].getFeatureCollectionWrapper();
                for (Iterator it = fc.iterator() ; it.hasNext() ;) {
                    Geometry p = ((Feature)it.next()).getGeometry();
                    if (op==Operator.INTER && g.intersects(p)) return true;
                    else if (op==Operator.CONTA && g.contains(p)) return true;
                    else if (op==Operator.WITHI && g.within(p)) return true;
                    else if (op==Operator.WDIST && g.distance(p)<op.arg) return true;
                    else if (op==Operator.TOUCH && g.touches(p)) return true;
                    else if (op==Operator.CROSS && g.crosses(p)) return true;
                    else if (op==Operator.OVERL && g.overlaps(p)) return true;
                    else if (op==Operator.DISJO && g.disjoint(p)) return true;
                    else;
                }
                return false;
            }
        }
        else if (pos==2) {
            List ll = context.getLayerManager().getLayers();
            for (int i = 0 ; i < ll.size() ; i++) {
                FeatureCollection fc = ((Layer)ll.get(i)).getFeatureCollectionWrapper();
                for (Iterator it = fc.iterator() ; it.hasNext() ;) {
                    Geometry p = ((Feature)it.next()).getGeometry();
                    if (op==Operator.INTER && g.intersects(p)) return true;
                    else if (op==Operator.CONTA && g.contains(p)) return true;
                    else if (op==Operator.WITHI && g.within(p)) return true;
                    else if (op==Operator.WDIST && g.distance(p)<op.arg) return true;
                    else if (op==Operator.TOUCH && g.touches(p)) return true;
                    else if (op==Operator.CROSS && g.crosses(p)) return true;
                    else if (op==Operator.OVERL && g.overlaps(p)) return true;
                    else if (op==Operator.DISJO && g.disjoint(p)) return true;
                    else;
                }
                return false;
            }
        }
        else {
            Layer layer = context.getLayerManager().getLayer((String)query.valueCB.getSelectedValue());
            FeatureCollection fc = layer.getFeatureCollectionWrapper();
            for (Iterator it = fc.iterator() ; it.hasNext() ;) {
                Geometry p = ((Feature)it.next()).getGeometry();
                if (op==Operator.INTER && g.intersects(p)) return true;
                else if (op==Operator.CONTA && g.contains(p)) return true;
                else if (op==Operator.WITHI && g.within(p)) return true;
                else if (op==Operator.WDIST && g.distance(p)<op.arg) return true;
                else if (op==Operator.TOUCH && g.touches(p)) return true;
                else if (op==Operator.CROSS && g.crosses(p)) return true;
                else if (op==Operator.OVERL && g.overlaps(p)) return true;
                else if (op==Operator.DISJO && g.disjoint(p)) return true;
                else;
            }
            return false;
        }
        return false;
    }
    
    //**************************************************************************
    // apply functions
    //**************************************************************************
    
    private String sfunction(String s) {
        if (ft==Function.SNOF) return s;
        else if (ft==Function.TRIM) return s.trim();
        else if (ft==Function.SUBS && ft.args.length==1) {
            return s.substring(ft.args[0]);
        }
        else if (ft==Function.SUBS && ft.args.length==2) {
            return s.substring(ft.args[0], ft.args[1]);
        }
        else return s;
    }
    
    private double nfunction(String s) {
        if (ft==Function.LENG) return (double)s.length();
        else return 0.0;
    }
    
    private Geometry gfunction(Geometry g) {
        //System.out.println("geometric function");
        if (ft==Function.GNOF) return g;
        else if (ft==Function.CENT) return g.getInteriorPoint();
        else if (ft==Function.BUFF) return g.buffer(ft.args[0]);
        else return g;
    }
    
    private double nfunction(Geometry g) {
        //System.out.println("numeric function");
        if (ft==Function.LENG) return g.getLength();
        else if (ft==Function.AREA) return g.getArea();
        else if (ft==Function.NBPT) return (double)g.getNumPoints();
        else if (ft==Function.NBPA) {
            if (g.isEmpty()) return 0;
            else if (g instanceof GeometryCollection)
                return ((GeometryCollection)g).getNumGeometries();
            else return 1;
        }
        else return 0.0;
    }
    
    private boolean bfunction(Geometry g) {
        //System.out.println("boolean function");
        if (ft==Function.EMPT) return g.isEmpty();
        else if (ft==Function.SIMP) return g.isSimple();
        else if (ft==Function.VALI) return g.isValid();
        else return false;
    }

}
