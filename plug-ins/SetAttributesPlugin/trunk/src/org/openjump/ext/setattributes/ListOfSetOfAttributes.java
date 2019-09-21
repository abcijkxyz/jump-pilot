package org.openjump.ext.setattributes;

import javax.swing.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Contains a list of SetOfAttributes.
 * Useful to build ComboBoxes containing sevral buttons
 */
@XmlRootElement(name="combo")
public class ListOfSetOfAttributes {

    @XmlAttribute
    String text = "";

    @XmlElement(name="button")
    List<SetOfAttributes> listOfSets = new ArrayList<>();

    public JComboBox<SetOfAttributes> createCombo(File dir) {
        JComboBox<SetOfAttributes> comboBox = new JComboBox<>();
        Vector<SetOfAttributes> vector = new Vector<>(listOfSets);
        comboBox.setModel(new DefaultComboBoxModel<>(vector));
        comboBox.setRenderer(new IconRenderer(dir));
        return comboBox;
    }

    class IconRenderer extends DefaultListCellRenderer {

        private Map<String, ImageIcon> iconMap = new HashMap<>();
        private Color background = new Color(0, 100, 255, 15);
        private Color defaultBackground = (Color) UIManager.get("List.background");


        public IconRenderer(File dir) {
            for (SetOfAttributes setOfAttributes : listOfSets) {
                ImageIcon icon = new ImageIcon(dir.getPath()+"/"+setOfAttributes.icon);
                if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    iconMap.put(setOfAttributes.text, icon);
                }
            }
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            SetOfAttributes set = (SetOfAttributes) value;
            this.setText(set.text);
            this.setIcon(iconMap.get(set.text));
            if (!isSelected) {
                this.setBackground(index % 2 == 0 ? background : defaultBackground);
            }
            return this;
        }
    }

}
