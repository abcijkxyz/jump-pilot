package com.cadplan.fileio;

import java.io.File;
import javax.swing.filechooser.FileFilter;

class MyFileFilter extends FileFilter {
   String description;
   String[] extensions;
   boolean acceptAll;

   public MyFileFilter(String paramString1, String paramString2) {
      this(paramString1, new String[]{paramString2});
   }

   public MyFileFilter(String paramString, String[] paramArrayOfString) {
      this.acceptAll = false;
      if (paramString == null) {
         this.description = "Files like: " + paramArrayOfString[0];
      } else {
         this.description = paramString;
      }

      this.extensions = (String[])paramArrayOfString.clone();
      this.toLower(this.extensions);

      for(byte b = 0; b < this.extensions.length; ++b) {
         if (this.extensions[b].equals("*")) {
            this.acceptAll = true;
         }
      }

   }

   private void toLower(String[] paramArrayOfString) {
      byte b = 0;

      for(int i = paramArrayOfString.length; b < i; ++b) {
         paramArrayOfString[b] = paramArrayOfString[b].toLowerCase();
      }

   }

   public String getDescription() {
      return this.description;
   }

   public boolean accept(File paramFile) {
      if (paramFile.isDirectory()) {
         return true;
      } else if (this.acceptAll) {
         return true;
      } else {
         String str = paramFile.getAbsolutePath().toLowerCase();

         for(byte b = 0; b < this.extensions.length; ++b) {
            String str1 = this.extensions[b];
            if (str.endsWith(str1) && str.charAt(str.length() - str1.length() - 1) == '.') {
               return true;
            }
         }

         return false;
      }
   }
}
