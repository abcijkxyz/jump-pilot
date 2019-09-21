package org.openjump.ext.setattributes;

import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.feature.AttributeType;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.util.FlexibleDateParser;
import com.vividsolutions.jump.workbench.model.FeatureEventType;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.model.UndoableCommand;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.SelectionManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Contains information of an attribute set.
 */
@XmlRootElement (name="button")
public class SetOfAttributes {

    final Logger LOG = Logger.getLogger(SetOfAttributes.class);

    I18N I18N_ = I18N.getInstance("set_attributes");

    FlexibleDateParser dateParser = new FlexibleDateParser();

    @XmlAttribute
    String icon;

    @XmlAttribute
    String text = "";

    @XmlAttribute
    String backgroundColor;

    @XmlAttribute
    String tooltip;

    @XmlAttribute
    boolean atomic = false;

    @XmlAttribute
    String layer;

    @XmlAttribute (name="dimension", required=false)
    int dimension = -1;

    @XmlElement (name="attribute")
    List<SetAttribute> attributes;

    public String getIcon() {
        return icon;
    }

    public Color getBackgroundColor() {
        Color c = null;
        if (backgroundColor == null) return null;
        try {
            c = Color.decode(backgroundColor);
        } catch(NumberFormatException e) {}
        return c;
    }

    public String getTooltip() {
        return tooltip;
    }

    public boolean isAtomic() {
        return atomic;
    }

    public String getLayer() {
        return layer;
    }

    public int getDimension() {
        return dimension;
    }

    public List<SetAttribute> getAttributes() {
        return attributes;
    }

    /**
     * Returns a map from source features to modified features
     * @param features to be modified
     * @throws Exception
     */
    public Map<Feature,Feature> setAttributes(Collection<Feature> features, String layerName) throws Exception {
        // map original feature to modified features
        Map<Feature,Feature> map = new HashMap();

        for (Feature feature : features) {
            FeatureSchema schema = feature.getSchema();
            Feature newFeature = feature.clone(false);
            try {
                for (SetAttribute setAtt : attributes) {
                    String name = setAtt.getName();
                    String value = setAtt.getValue();

                    if (!schema.hasAttribute(name)) {
                        if (isAtomic()) {
                            throw new Exception(I18N.getMessage("set_attributes",
                                    "SetAttributesPlugIn.not-consistent-with-schema",
                                    new Object[]{name, layerName, feature.getID()}));
                        } else {
                            continue;
                        }
                    }
                    // This attribute value has not the prerequisite, don't change it
                    if (!setAtt.checkPrerequisite(feature.getAttribute(name))) {
                        continue;
                    }
                    if (!checkDimension(feature)) {
                        continue;
                    }
                    AttributeType type = schema.getAttributeType(name);
                    if (value == null) {
                        newFeature.setAttribute(name, null);
                    }
                    else if (type == AttributeType.STRING) {
                        newFeature.setAttribute(name, value);
                    }
                    else if (type == AttributeType.INTEGER) {
                        newFeature.setAttribute(name, Integer.parseInt(value));
                    }
                    else if (type == AttributeType.DOUBLE) {
                        newFeature.setAttribute(name, Double.parseDouble(value));
                    }
                    else if (type == AttributeType.DATE) {
                        newFeature.setAttribute(name, dateParser.parse(value, false));
                    }
                    else if (type == AttributeType.OBJECT) {
                        newFeature.setAttribute(name, value);
                    }
                    else if (AttributeType.class.getField("BOOLEAN") != null && type == AttributeType.BOOLEAN) {
                        newFeature.setAttribute(name, Boolean.parseBoolean(value));
                    }
                    else if (AttributeType.class.getField("LONG") != null && type == AttributeType.LONG) {
                        newFeature.setAttribute(name, Long.parseLong(value));
                    }
                }
                map.put(feature,newFeature);
            } catch(Exception e) {
                LOG.warn(e.getMessage());
                throw e;
            }

        }
        return map;
    }

    public void setAttributes(final PlugInContext pluginContext, final boolean unselect) {
        SelectionManager selectionManager = pluginContext.getLayerViewPanel().getSelectionManager();
        pluginContext.getLayerManager().getUndoableEditReceiver().startReceiving();
        try {
            int editableLayers = 0;
            int editableFeatures = 0;
            Pattern layerPattern = null;
            // If layer is not null
            if (getLayer() != null) {
                try {
                    // create a pattern to read this specific layer name
                    layerPattern = Pattern.compile(Pattern.quote(getLayer()));
                    // then try to interpret * as glob
                    if (getLayer().contains("*")) {
                        layerPattern = Pattern.compile(getLayer().replaceAll("\\*", ".*"));
                    }
                    // and finally, try to read layer name as a regex if it starts and ends with a /
                    if (getLayer().startsWith("/") && getLayer().endsWith("/")) {
                        layerPattern = Pattern.compile(getLayer().substring(1, getLayer().length() - 1));
                    }
                } catch (PatternSyntaxException pse) {
                    pse.printStackTrace();
                }
            }
            final Map<Layer, Map<Feature, Feature>> mapSource = new HashMap<Layer, Map<Feature, Feature>>();
            final Map<Layer, Map<Feature, Feature>> mapTarget = new HashMap<Layer, Map<Feature, Feature>>();
            for (Layer lyr : selectionManager.getLayersWithSelectedItems()) {
                if (!lyr.isEditable()) continue;
                if (layerPattern != null && !layerPattern.matcher(lyr.getName()).matches()) continue;
                editableLayers++;
                Map<Feature, Feature> srcLayerMap = new HashMap<Feature, Feature>();
                Map<Feature, Feature> tgtLayerMap = new HashMap<Feature, Feature>();
                Collection<Feature> features = selectionManager.getFeaturesWithSelectedItems(lyr);
                editableFeatures += features.size();
                for (Feature feature : features) {
                    srcLayerMap.put(feature, feature.clone(false));
                }
                tgtLayerMap.putAll(setAttributes(selectionManager.getFeaturesWithSelectedItems(lyr), lyr.getName()));
                mapSource.put(lyr, srcLayerMap);
                mapTarget.put(lyr, tgtLayerMap);
            }
            if (unselect) {
                for (Layer lyr : selectionManager.getLayersWithSelectedItems()) {
                    selectionManager.unselectItems(lyr);
                }
            }
            if (editableLayers == 0 && getLayer() == null) {
                pluginContext.getWorkbenchFrame().warnUser(I18N_.getText("set_attributes",
                        "SetAttributesPlugIn.no-feature-found"));
            } else if (editableLayers == 0) {
                pluginContext.getWorkbenchFrame().warnUser(I18N_.getMessage("set_attributes",
                        "SetAttributesPlugIn.no-feature-found-in-layer",
                        new Object[]{getLayer()}));
            } else if (editableFeatures == 0 && getLayer() == null) {
                pluginContext.getWorkbenchFrame().warnUser(I18N_.getText("set_attributes",
                        "SetAttributesPlugIn.no-feature-found"));
            } else if (editableFeatures == 0) {
                pluginContext.getWorkbenchFrame().warnUser(I18N_.getMessage("set_attributes",
                        "SetAttributesPlugIn.no-feature-found-in-layer",
                        new Object[]{getLayer()}));
            } else {
                UndoableCommand command =
                        new UndoableCommand(I18N.get(SetAttributesPlugIn.class.getName())) {
                            public void execute() {
                                for (Layer lyr : mapTarget.keySet()) {
                                    Map<Feature,Feature> mapTgt = mapTarget.get(lyr);
                                    Map<Feature,Feature> mapSrc = mapSource.get(lyr);
                                    for (Feature feature : mapTgt.keySet()) {
                                        Feature newFeature = mapTgt.get(feature);
                                        FeatureSchema schema = feature.getSchema();
                                        for (SetAttribute setAtt : attributes) {
                                            String name = setAtt.getName();
                                            if (schema.hasAttribute(name)) {
                                                feature.setAttribute(name, newFeature.getAttribute(name));
                                            }
                                        }
                                    }
                                    pluginContext.getLayerManager().fireFeaturesAttChanged(mapTgt.keySet(), FeatureEventType.ATTRIBUTES_MODIFIED, lyr, mapSrc.values());
                                    //pluginContext.getLayerManager().fireFeaturesChanged(map.keySet(), FeatureEventType.ATTRIBUTES_MODIFIED, lyr);
                                }
                                pluginContext.getLayerViewPanel().repaint();
                            }

                            public void unexecute() {
                                for (Layer lyr : mapSource.keySet()) {
                                    Map<Feature, Feature> mapSrc = mapSource.get(lyr);
                                    Map<Feature, Feature> mapTgt = mapTarget.get(lyr);
                                    for (Feature feature : mapSrc.keySet()) {
                                        Feature newFeature = mapSrc.get(feature);
                                        FeatureSchema schema = feature.getSchema();
                                        for (SetAttribute setAtt : attributes) {
                                            String name = setAtt.getName();
                                            if (schema.hasAttribute(name)) {
                                                feature.setAttribute(name, newFeature.getAttribute(name));
                                            }
                                        }
                                        pluginContext.getLayerManager().fireFeaturesAttChanged(mapSrc.keySet(), FeatureEventType.ATTRIBUTES_MODIFIED, lyr, mapTgt.values());
                                        //pluginContext.getLayerManager().fireFeaturesChanged(mapSrc.keySet(), FeatureEventType.ATTRIBUTES_MODIFIED, lyr);
                                    }
                                }
                                pluginContext.getLayerViewPanel().repaint();
                            }
                        };

                command.execute();
                pluginContext.getLayerManager().getUndoableEditReceiver().receive(command.toUndoableEdit());
            }
        } catch (Exception exc) {
            LOG.warn(null, exc);
            pluginContext.getWorkbenchFrame().warnUser(exc.getMessage());
        } finally {
            pluginContext.getLayerManager().getUndoableEditReceiver().stopReceiving();
        }
    }

    boolean checkDimension(Feature feature) {
        if (dimension > -1) {
            return feature.getGeometry().getDimension() == dimension;
        } else return true;
    }

}
