package org.openjump.ext.setattributes;

import com.vividsolutions.jump.I18N;
import com.vividsolutions.jump.feature.Feature;
import com.vividsolutions.jump.feature.FeatureSchema;
import com.vividsolutions.jump.workbench.model.FeatureEventType;
import com.vividsolutions.jump.workbench.model.Layer;
import com.vividsolutions.jump.workbench.model.UndoableCommand;
import com.vividsolutions.jump.workbench.plugin.PlugInContext;
import com.vividsolutions.jump.workbench.ui.SelectionManager;
import org.apache.log4j.Logger;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Defines what happens when the user click on a SetAttributes button
 */
public class SetAttributesButtonActionListener implements ActionListener {

    final Logger LOG = Logger.getLogger(SetAttributesToolbox.class);
    final I18N I18N_ = I18N.getInstance("set_attributes");

    final PlugInContext pluginContext;
    final SetOfAttributes setOfAttributes;
    final boolean unselect;

    SetAttributesButtonActionListener(final PlugInContext pluginContext,
                                     final SetOfAttributes setOfAttributes,
                                     final boolean unselect) {
        this.pluginContext = pluginContext;
        this.setOfAttributes = setOfAttributes;
        this.unselect = unselect;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setOfAttributes.setAttributes(pluginContext, unselect);
    }
}
