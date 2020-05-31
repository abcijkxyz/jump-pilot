package com.cadplan.jump.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

public class LabelCallout {
   public LabelCallout(Graphics g, int border, int position, int x0, int y0, int x, int y, int w, int h, Color foregroundColor, Color backgroundColor, boolean fillText) {
      GeneralPath path = new GeneralPath();
      Graphics2D g2 = (Graphics2D)g;
      int s = 7;
      if (border == 1) {
         g.drawRect(x, y, w, h);
         path.moveTo((float)x, (float)y);
         path.lineTo((float)(x + w), (float)y);
         path.lineTo((float)(x + w), (float)(y + h));
         path.lineTo((float)x, (float)(y + h));
         path.lineTo((float)x, (float)y);
      } else if (border == 2) {
         switch(position) {
         case 0:
            g.drawRect(x, y, w, h);
            break;
         case 1:
         case 2:
         case 8:
            path.moveTo((float)x0, (float)y0);
            path.lineTo((float)(x + w / 2 - s), (float)(y + h));
            path.lineTo((float)x, (float)(y + h));
            path.lineTo((float)x, (float)y);
            path.lineTo((float)(x + w), (float)y);
            path.lineTo((float)(x + w), (float)(y + h));
            path.lineTo((float)(x + w / 2 + s), (float)(y + h));
            path.lineTo((float)x0, (float)y0);
            break;
         case 3:
            path.moveTo((float)x0, (float)y0);
            path.lineTo((float)x, (float)(y + h / 2 - s));
            path.lineTo((float)x, (float)y);
            path.lineTo((float)(x + w), (float)y);
            path.lineTo((float)(x + w), (float)(y + h));
            path.lineTo((float)x, (float)(y + h));
            path.lineTo((float)x, (float)(y + h / 2 + s));
            path.lineTo((float)x0, (float)y0);
            break;
         case 4:
         case 5:
         case 6:
            path.moveTo((float)x0, (float)y0);
            path.lineTo((float)(x + w / 2 + s), (float)y);
            path.lineTo((float)(x + w), (float)y);
            path.lineTo((float)(x + w), (float)(y + h));
            path.lineTo((float)x, (float)(y + h));
            path.lineTo((float)x, (float)y);
            path.lineTo((float)(x + w / 2 - 10), (float)y);
            path.lineTo((float)x0, (float)y0);
            break;
         case 7:
            path.moveTo((float)x0, (float)y0);
            path.lineTo((float)(x + w), (float)(y + h / 2 + s));
            path.lineTo((float)(x + w), (float)(y + h));
            path.lineTo((float)x, (float)(y + h));
            path.lineTo((float)x, (float)y);
            path.lineTo((float)(x + w), (float)y);
            path.lineTo((float)(x + w), (float)(y + h / 2 - 10));
            path.lineTo((float)x0, (float)y0);
         }
      }

      if (fillText) {
         g2.setColor(backgroundColor);
         g2.fill(path);
      }

      if (border > 0) {
         g2.setColor(foregroundColor);
         g2.draw(path);
      }

   }
}
