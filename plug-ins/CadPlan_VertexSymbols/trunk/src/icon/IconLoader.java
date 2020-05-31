package com.cadplan.jump.icon;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.net.URL;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;
import org.saig.jump.lang.I18N;

public class IconLoader {
   public static final ImageIcon DEFAULT_UNKNOW_ICON = new ImageIcon(IconLoader.class.getResource("default_icon.png"));
   public static final Logger LOGGER = Logger.getLogger(IconLoader.class);

   public static ImageIcon icon(String filename) {
      return icon(filename, true);
   }

   public static ImageIcon icon(String filename, boolean useDefaultForNull) {
      URL urlIcon = IconLoader.class.getResource(filename);
      if (urlIcon == null) {
         if (useDefaultForNull) {
            LOGGER.warn(I18N.getMessage("com.vividsolutions.jump.workbench.ui.images.IconLoader.The-icon-{0}-has-not-been-found-default-icon-will-be-used", new Object[]{filename}));
            return DEFAULT_UNKNOW_ICON;
         } else {
            return null;
         }
      } else {
         return new ImageIcon(urlIcon);
      }
   }

   public static ImageIcon icon(URL url) {
      if (url == null) {
         LOGGER.warn(I18N.getMessage("com.vividsolutions.jump.workbench.ui.images.IconLoader.The-icon-{0}-has-not-been-found-default-icon-will-be-used", new Object[]{url}));
         return DEFAULT_UNKNOW_ICON;
      } else {
         return new ImageIcon(url);
      }
   }

   public static BufferedImage image(String filename) {
      ImageIcon icon = icon(IconLoader.class.getResource(resolveFile(filename)));
      Image image = icon.getImage();
      BufferedImage bufImg = new BufferedImage(image.getWidth((ImageObserver)null), image.getHeight((ImageObserver)null), 2);
      Graphics2D bGr = bufImg.createGraphics();
      bGr.drawImage(image, 0, 0, (ImageObserver)null);
      bGr.dispose();
      return bufImg;
   }

   protected static String resolveFile(String filename) {
      String[] var4;
      int var3 = (var4 = new String[]{"", "famfam/", "fugue/"}).length;

      for(int var2 = 0; var2 < var3; ++var2) {
         String path = var4[var2];
         if (IconLoader.class.getResource(path + filename) != null) {
            return path + filename;
         }
      }

      return filename;
   }
}
