package org.openjump.ext.setattributes;

import com.vividsolutions.jump.workbench.plugin.PlugInContext;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class SetAttributesComboItemListener implements ItemListener {

    final PlugInContext pluginContext;
    final boolean unselect;

    SetAttributesComboItemListener(final PlugInContext pluginContext,
                                   final boolean unselect) {
        this.pluginContext = pluginContext;
        this.unselect = unselect;
    }

    public void itemStateChanged(ItemEvent itemEvent) {
        int state = itemEvent.getStateChange();
        if (state == ItemEvent.SELECTED) {
            SetOfAttributes setOfAttributes = (SetOfAttributes)itemEvent.getItem();
            setOfAttributes.setAttributes(pluginContext, unselect);
        }
        ((JComboBox)itemEvent.getSource()).setPopupVisible(false);

    }
}
