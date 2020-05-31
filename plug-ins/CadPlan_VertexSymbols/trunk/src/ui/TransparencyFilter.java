package com.cadplan.jump.ui;

import java.awt.Color;
import java.awt.image.RGBImageFilter;

public class TransparencyFilter extends RGBImageFilter {
   int markerRGB;
   int base = 16777215;
   int trans = 0;
   int limit = 150;
   int newrgb;

   public TransparencyFilter(Color color, double alpha) {
      this.markerRGB = color.getRGB() | -16777216;
      this.trans = (int)(alpha * 255.0D) << 24;
   }

   public int filterRGB(int x, int y, int rgb) {
      Color newColor = null;
      if ((rgb | -16777216) == this.markerRGB) {
         this.newrgb = (this.base | this.trans) & rgb;
         new Color(this.newrgb, true);
      } else {
         this.newrgb = rgb;
      }

      return this.newrgb;
   }
}
