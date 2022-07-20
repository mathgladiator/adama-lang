/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli;

import java.util.Locale;

public class Util {
  public static String normalize(String x) {
    return x.trim().toLowerCase(Locale.ROOT);
  }

  public static String[] tail(String[] y) {
    String[] x = new String[y.length - 1];
    for (int k = 1; k < y.length; k++) {
      x[k - 1] = y[k];
    }
    return x;
  }

  public static String prefix(String x, ANSI c) {
    return c.ansi + x + ANSI.Reset.ansi;
  }

  public static String extractOrCrash(String longField, String shortField, String[] args) throws Exception {
    for (int k = 0; k + 1 < args.length; k++) {
      if (longField.equals(args[k]) || shortField.equals(args[k])) {
        return args[k + 1];
      }
    }
    System.err.println("Missing parameter " + longField + "/" + shortField);
    throw new Exception("Missing " + longField);
  }

  public static String extractWithDefault(String longField, String shortField, String defaultValue, String[] args) {
    for (int k = 0; k + 1 < args.length; k++) {
      if (longField.equals(args[k]) || shortField.equals(args[k])) {
        return args[k + 1];
      }
    }
    return defaultValue;
  }

  public static boolean scan(String field, String[] args) {
    for (int k = 0; k < args.length; k++) {
      if (field.equals(args[k])) {
        return true;
      }
    }
    return false;
  }

  public enum ANSI {
    Black("\u001b[30m"), Red("\u001b[31m"), Green("\u001b[32m"), Yellow("\u001b[33m"), Blue("\u001b[34m"), Magenta("\u001b[35m"), Cyan("\u001b[36m"), White("\u001b[37m"), Reset("\u001b[0m");
    public final String ansi;

    ANSI(String ansi) {
      this.ansi = ansi;
    }
  }
}
