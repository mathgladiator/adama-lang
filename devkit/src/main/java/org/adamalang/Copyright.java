package org.adamalang;

import java.io.File;

public class Copyright {
  private static void scan(File root) {
    for(File f : root.listFiles()) {
      if (f.isDirectory()) {
        scan(f);
      } else {
      }
    }
  }

  public static void main(String[] args) {
    File f = new File(".");
    scan(f);
  }
}
