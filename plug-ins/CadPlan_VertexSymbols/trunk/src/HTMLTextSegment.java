package com.cadplan.jump;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

public class HTMLTextSegment {
   private boolean debug = false;
   private String line;
   private int size;
   private int style;
   private double height;
   private double width;
   private double lineSpace;
   private String fontName;
   private Font font;

   public HTMLTextSegment(Graphics2D g, String line, String fontName, int size, int style) {
      this.fontName = fontName;
      this.line = line;
      this.size = size;
      this.style = style;
      this.font = new Font(fontName, style, size);
      FontRenderContext frc = g.getFontRenderContext();
      TextLayout layout = new TextLayout(line, this.font, frc);
      this.lineSpace = (double)(layout.getAscent() + layout.getDescent());
      Rectangle2D bounds = layout.getBounds();
      this.width = bounds.getWidth();
      this.height = bounds.getHeight();
   }

   public double getHeight() {
      return this.height;
   }

   public double getWidth() {
      return this.width;
   }

   public Font getFont() {
      return this.font;
   }
}
