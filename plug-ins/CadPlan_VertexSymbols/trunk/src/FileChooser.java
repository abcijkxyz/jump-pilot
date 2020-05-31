package com.cadplan.fileio;

import java.awt.Component;
import java.io.File;
import java.util.Properties;
import javax.swing.JFileChooser;

public class FileChooser {
   JFileChooser chooser;
   public String fileName;
   public String dirName;

   public FileChooser(Component paramComponent, String paramString1, String paramString2, String[] paramArrayOfString, String paramString3, int paramInt) {
      Properties properties = System.getProperties();
      File file;
      if (paramString1 == null) {
         file = new File(properties.getProperty("user.dir"));
      } else {
         file = new File(String.valueOf(paramString1) + File.separator);
      }

      this.chooser = new JFileChooser();
      this.chooser.setDialogType(paramInt);
      MyFileFilter myFileFilter = new MyFileFilter(paramString3, paramArrayOfString);
      this.chooser.setFileFilter(myFileFilter);
      this.chooser.setCurrentDirectory(file);
      if (paramString2 != null) {
         this.chooser.setSelectedFile(new File(paramString2));
      }

      int i;
      if (paramInt == 0) {
         i = this.chooser.showOpenDialog(paramComponent);
      } else {
         i = this.chooser.showSaveDialog(paramComponent);
      }

      if (i == 0) {
         this.fileName = this.chooser.getSelectedFile().getName();
         if (paramInt == 1) {
            this.fileName = this.addExtension(this.fileName, paramArrayOfString);
         }

         this.dirName = this.chooser.getCurrentDirectory().getPath();
      } else {
         this.fileName = null;
      }

   }

   public String getFile() {
      return this.fileName;
   }

   public String getDir() {
      return this.dirName;
   }

   private String addExtension(String paramString, String[] paramArrayOfString) {
      boolean bool = false;

      for(byte b = 0; b < paramArrayOfString.length; ++b) {
         String str = paramArrayOfString[b];
         if (paramString.toLowerCase().endsWith("." + str)) {
            bool = true;
         }
      }

      if (!bool) {
         paramString = String.valueOf(paramString) + "." + paramArrayOfString[0];
      }

      return paramString;
   }
}
