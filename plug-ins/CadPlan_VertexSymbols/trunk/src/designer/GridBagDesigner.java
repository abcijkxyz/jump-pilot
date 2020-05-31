package com.cadplan.designer;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;

public class GridBagDesigner {
   private GridBagLayout layout = new GridBagLayout();
   private GridBagConstraints constraints = new GridBagConstraints();
   private Container container;

   public GridBagDesigner(Container paramContainer) {
      paramContainer.setLayout(this.layout);
      this.container = paramContainer;
      this.setDefaults();
   }

   public void addComponent(Component paramComponent) {
      this.layout.setConstraints(paramComponent, this.constraints);
      this.container.add(paramComponent);
      this.setDefaults();
   }

   public void addComponentRetain(Component paramComponent) {
      this.layout.setConstraints(paramComponent, this.constraints);
      this.container.add(paramComponent);
   }

   public void addComponent(JComponent paramJComponent, int width, int height) {
      Dimension dimension = new Dimension(width, height);
      paramJComponent.setMinimumSize(dimension);
      paramJComponent.setMaximumSize(dimension);
      paramJComponent.setPreferredSize(dimension);
      this.layout.setConstraints(paramJComponent, this.constraints);
      this.container.add(paramJComponent);
      this.setDefaults();
   }

   public void addComponentRetain(JComponent paramJComponent, int width, int height) {
      Dimension dimension = new Dimension(width, height);
      paramJComponent.setMinimumSize(dimension);
      paramJComponent.setMaximumSize(dimension);
      paramJComponent.setPreferredSize(dimension);
      this.layout.setConstraints(paramJComponent, this.constraints);
      this.container.add(paramJComponent);
   }

   public void resetLayout() {
      this.setDefaults();
   }

   public void setPosition(int column, int row) {
      this.constraints.gridx = column;
      this.constraints.gridy = row;
   }

   public void setSpan(int column, int row) {
      this.constraints.gridwidth = column;
      this.constraints.gridheight = row;
   }

   public void setWeight(double x, double y) {
      this.constraints.weightx = x;
      this.constraints.weighty = y;
   }

   public void setFill(int space) {
      this.constraints.fill = space;
   }

   public void setAnchor(int position) {
      this.constraints.anchor = position;
   }

   public void setInsets(int top, int left, int bottom, int right) {
      this.constraints.insets = new Insets(top, left, bottom, right);
   }

   private void setDefaults() {
      this.constraints.gridx = 0;
      this.constraints.gridy = 0;
      this.constraints.gridwidth = 1;
      this.constraints.gridheight = 1;
      this.constraints.fill = 0;
      this.constraints.anchor = 10;
      this.constraints.weightx = 0.0D;
      this.constraints.weighty = 0.0D;
      this.constraints.insets = new Insets(0, 0, 0, 0);
   }
}
