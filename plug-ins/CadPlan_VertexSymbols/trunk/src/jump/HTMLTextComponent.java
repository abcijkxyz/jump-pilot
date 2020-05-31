package com.cadplan.jump;

import com.cadplan.jump.ui.LabelCallout;
import com.cadplan.jump.ui.TransparencyFilter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import javax.swing.JEditorPane;
import javax.swing.JWindow;

public class HTMLTextComponent extends Component {
   private static final long serialVersionUID = 1L;
   boolean debug = false;
   String text;
   double offset;
   float x0;
   float y0;
   Color backgroundColor;
   Color foregroundColor;
   double alpha;
   Image timage;
   public int imageWidth;
   public int imageHeight;
   int border;
   int position;
   boolean fillText;
   String fontName;
   int fontSize;
   double scaleFactor = 4.0D;
   MediaTracker mediaTracker = new MediaTracker(this);

   public HTMLTextComponent(String text, double offset, float x0, float y0, Color backgroundColor, Color foregroundColor, double alpha, int border, int position, boolean fillText, String fontName, int fontSize) {
      this.text = text;
      this.offset = offset;
      this.x0 = x0;
      this.y0 = y0;
      this.backgroundColor = backgroundColor;
      this.foregroundColor = foregroundColor;
      this.alpha = alpha;
      this.border = border;
      this.position = position;
      this.fillText = fillText;
      this.fontName = fontName;
      this.fontSize = fontSize;
      this.timage = this.createTextImage();
   }

   private Image createTextImage() {
      JWindow frame = new JWindow();
      JEditorPane editorPane = new JEditorPane();
      editorPane.setForeground(this.foregroundColor);
      Font font = new Font(this.fontName, 0, (int)((double)this.fontSize * this.scaleFactor));
      editorPane.setFont(font);
      editorPane.putClientProperty("JEditorPane.honorDisplayProperties", true);
      editorPane.setBackground(Color.WHITE);
      editorPane.setDoubleBuffered(false);
      editorPane.setEditable(false);
      editorPane.setContentType("text/html");
      editorPane.setText(this.text);
      Rectangle bounds = editorPane.getBounds();
      if (this.debug) {
         System.out.println("Painting text: " + this.text);
      }

      frame.setContentPane(editorPane);
      frame.pack();
      Dimension size = editorPane.getSize();
      BufferedImage image = new BufferedImage(size.width, size.height, 1);
      Graphics2D graphics = image.createGraphics();
      editorPane.paint(graphics);
      this.mediaTracker.addImage(image, 0);

      try {
         this.mediaTracker.waitForID(0);
      } catch (InterruptedException var9) {
         return null;
      }

      if (image == null) {
         if (this.debug) {
            System.out.println("Image is null");
         }

         return null;
      } else {
         this.alpha = 0.0D;
         Image timage = this.makeTransparent(image, Color.WHITE, this.alpha);
         if (this.debug) {
            System.out.println("TImage size: " + timage.getWidth((ImageObserver)null) + "," + timage.getHeight((ImageObserver)null));
         }

         this.imageWidth = (int)((double)timage.getWidth((ImageObserver)null) / this.scaleFactor);
         this.imageHeight = (int)((double)timage.getHeight((ImageObserver)null) / this.scaleFactor);
         return timage;
      }
   }

   public void paint(Graphics g, float x, float y, double drawFactor) {
      int width = this.imageWidth;
      int height = this.imageHeight;
      new LabelCallout(g, this.border, this.position, (int)this.x0, (int)this.y0, (int)x, (int)y, width - 1, height - 1, this.foregroundColor, this.backgroundColor, this.fillText);
      Graphics2D g2 = (Graphics2D)g;
      g2.drawImage(this.timage, (int)x, (int)y, width, height, (ImageObserver)null);
   }

   private Image makeTransparent(Image image, Color color, double alpha) {
      TransparencyFilter filter = new TransparencyFilter(color, alpha);
      ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
      Image timage = Toolkit.getDefaultToolkit().createImage(ip);
      this.mediaTracker.addImage(timage, 2);

      try {
         this.mediaTracker.waitForID(2);
         return timage;
      } catch (InterruptedException var9) {
         return null;
      }
   }
}
