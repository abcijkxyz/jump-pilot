package org.openjump.ext.setattributes;

import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.feature.AttributeType;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.util.FlexibleDateParser;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains information or an attribute set.
 */
@XmlRootElement (name="button")
public class SetOfAttributes {

    final Logger LOG = Logger.getLogger(SetOfAttributes.class);

    FlexibleDateParser dateParser = new FlexibleDateParser();

    @XmlAttribute
    String icon;

    @XmlAttribute
    String text;

    @XmlAttribute
    String tooltip;

    @XmlAttribute
    boolean atomic = false;

    @XmlAttribute
    String layer;

    @XmlElement (name="attribute")
    List<SetAttribute> attributes;

    public String getIcon() {
        return icon;
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

    public List<SetAttribute> getAttributes() {
        return attributes;
    }

    /**
     * Returns a map from source features to modified features
     * @param features to be modified
     * @throws Exception
     */
    public Map<Feature,Feature> setAttributes(Collection<Feature> features) throws Exception {
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
                            throw new Exception("Set attribute " + name + " is not consistent with feature schema of feature " + feature.getID());
                        } else {
                            continue;
                        }
                    }
                    // This attribute value has not the prerequisite, don't change it
                    if (!setAtt.checkPrerequisite(feature.getAttribute(name))) {
                        continue;
                    }
                    //Feature newFeature = map.get(feature);
                    AttributeType type = schema.getAttributeType(name);
                    if (type == AttributeType.STRING) {
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
            }

        }
        return map;
    }

}
