package com.cadplan.fileio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class TextFile {
   String fileName;
   String dirName;
   BufferedReader reader = null;
   FileWriter writer = null;

   public TextFile(String paramString1, String paramString2) {
      this.dirName = paramString1;
      Properties properties = System.getProperties();
      if (paramString1 == null) {
         this.dirName = properties.getProperty("user.dir");
      }

      this.fileName = paramString2;
   }

   public boolean exists() {
      File file = new File(String.valueOf(this.dirName) + File.separator + this.fileName);
      return file.exists();
   }

   public boolean openRead() {
      File file = new File(String.valueOf(this.dirName) + File.separator + this.fileName);

      try {
         this.reader = new BufferedReader(new FileReader(file));
         return true;
      } catch (FileNotFoundException var3) {
         return false;
      }
   }

   public boolean openWrite() {
      File file = new File(String.valueOf(this.dirName) + File.separator + this.fileName);

      try {
         this.writer = new FileWriter(file);
         return true;
      } catch (IOException var3) {
         return false;
      }
   }

   public String readLine() {
      try {
         return this.reader.readLine();
      } catch (IOException var2) {
         return null;
      }
   }

   public String readAll() {
      String str = "";
      StringBuffer stringBuffer = new StringBuffer();

      try {
         while(str != null) {
            str = this.reader.readLine();
            if (str != null) {
               stringBuffer.append(String.valueOf(str) + "\n");
            }
         }
      } catch (IOException var4) {
         return stringBuffer.toString();
      }

      return stringBuffer.toString();
   }

   public boolean write(String paramString) {
      try {
         this.writer.write(paramString);
         return true;
      } catch (IOException var3) {
         return false;
      }
   }

   public void close() {
      try {
         if (this.reader != null) {
            this.reader.close();
         } else if (this.writer != null) {
            this.writer.flush();
            this.writer.close();
         }
      } catch (IOException var2) {
      }

   }
}
