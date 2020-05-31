package com.cadplan.jump.language;

import com.vividsolutions.jump.I18N;
import java.io.File;

public class I18NPlug {
   private static File path = new File("com/cadplan/jump/language/VertexSymbolsPlugin");

   public static String getI18N(String key) {
      return getMessage(key);
   }

   public static String getMessage(String label, Object... objects) {
      return I18N.getMessage(path, label, objects);
   }
}
