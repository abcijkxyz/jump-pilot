package com.cadplan.jump.ui;

import com.cadplan.designer.GridBagDesigner;
import com.cadplan.jump.utils.VertexParams;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.saig.core.gui.swing.sldeditor.util.FormUtils;

public class WKTPanel extends JPanel {
   private static final long serialVersionUID = 1L;
   ButtonGroup group;
   Color lineColor;
   Color fillColor;
   private JRadioButton[] imageRB;
   String name;
   int numImages;

   public WKTPanel(ButtonGroup group, Color lineColor, Color fillColor) {
      this.fillColor = fillColor;
      this.lineColor = lineColor;
      this.group = group;
      this.init();
   }

   public void init() {
      if (VertexParams.wktShapes != null) {
         this.numImages = VertexParams.wktNames.length;
         this.setImageRB(new JRadioButton[this.numImages]);
         new GridBagDesigner(this);

         for(int i = 0; i < this.numImages; ++i) {
            final WKTVertexPanel wktVertexPanel = new WKTVertexPanel(VertexParams.wktNames[i], this.lineColor, this.fillColor);
            wktVertexPanel.setBackground(Color.WHITE);
            int row = i % 5;
            int col = i / 5 * 2;
            this.name = VertexParams.wktNames[i];
            int k = this.name.lastIndexOf(".");
            if (k > 0) {
               this.name = this.name.substring(0, k);
            }

            this.getImageRB()[i] = new JRadioButton(this.name);
            this.getImageRB()[i].setBackground(Color.WHITE);
            this.group.add(this.getImageRB()[i]);
            this.getImageRB()[i].addItemListener(new ItemListener() {
               public void itemStateChanged(ItemEvent ev) {
                  if (ev.getStateChange() == 1) {
                     wktVertexPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.red));
                  } else if (ev.getStateChange() == 2) {
                     wktVertexPanel.setBorder(BorderFactory.createEmptyBorder());
                  }

               }
            });
            FormUtils.addRowInGBL(this, row, col, (Icon)null, wktVertexPanel, this.getImageRB()[i]);
         }

      }
   }

   public int getSelectedImage() {
      for(int i = 0; i < this.numImages; ++i) {
         if (this.getImageRB()[i].isSelected()) {
            return i;
         }
      }

      return -1;
   }

   public JRadioButton[] getImageRB() {
      return this.imageRB;
   }

   public void setImageRB(JRadioButton[] imageRB) {
      this.imageRB = imageRB;
   }

   public String getName(int i) {
      return this.imageRB[i].getText();
   }
}
