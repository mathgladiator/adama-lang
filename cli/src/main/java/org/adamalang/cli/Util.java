/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli;

public class Util {
  private static Boolean NO_COLOR;

  private static boolean nocolor() {
    if (NO_COLOR == null) {
      String nc = System.getenv("NO_COLOR");
      NO_COLOR = nc != null && !"0".equals(nc);
    }
    return NO_COLOR;
  }

  public static void setNoColor() {
    NO_COLOR = true;
  }

  public static String prefix(String x, ANSI c) {
    if (nocolor()) {
      return x;
    }
    return c.ansi + x + ANSI.Reset.ansi;
  }

  public static String prefixBold(String x, ANSI c) {
    if (nocolor()) {
      return x;
    }
    return ANSI.Bold.ansi + c.ansi + x + ANSI.Reset.ansi;
  }

  public static String justifyLeft(String string, int spacing) {
    return String.format("%-" + spacing + "s", string);
  }
  public static String justifyRight(String string, int spacing) {
    return String.format("%" + spacing + "s", string);
  }

  public enum ANSI {
    Black("\u001b[30m"), Red("\u001b[31m"), Green("\u001b[32m"), Yellow("\u001b[33m"), Blue("\u001b[34m"), Magenta("\u001b[35m"), Cyan("\u001b[36m"), White("\u001b[37m"), Bold("\u001b[1m"), Reset("\u001b[0m"), Normal("\u001b[39m");
    public final String ansi;

    ANSI(String ansi) {
      this.ansi = ansi;
    }
  }
}
