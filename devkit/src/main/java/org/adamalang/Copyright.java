/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang;

import java.io.File;
import java.nio.file.Files;

public class Copyright {
  public static final String NEW_COPYRIGHT_MESSAGE = "/*\n" +
          " * This file is subject to the terms and conditions outlined in the file 'LICENSE'\n" +
          " * which is in the root directory of the repository. This file is part of the 'Adama'\n" +
          " * project which is a programming language and document store for board games.\n" +
          " * \n" +
          " * See http://www.adama-lang.org/ for more information.\n" +
          " * \n" +
          " * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)\n" +
          "*/\n";

  private static void scan(File root) throws Exception {
    for(File f : root.listFiles()) {
      if (f.isDirectory()) {
        scan(f);
      } else {
        if(f.getName().endsWith(".java")) {
          String code = Files.readString(f.toPath());
          int start = code.indexOf("/*");
          int end = code.indexOf("*/");
          if (start >= 0 && start <= 5 && end > start) {
            String newCode = NEW_COPYRIGHT_MESSAGE + code.substring(end + 2).trim() + "\n";
            Files.writeString(f.toPath(), newCode);
          } else {
            String newCode = NEW_COPYRIGHT_MESSAGE + code.trim() + "\n";
            Files.writeString(f.toPath(), newCode);
          }
        }
      }
    }
  }

  public static void main(String[] args) throws Exception {
    File f = new File(".");
    scan(f);
  }
}
