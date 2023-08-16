/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.template;

import java.util.Locale;

public class StatePath {
  public final String command;
  public final String name;
  public final boolean simple;

  private StatePath(String command, String name, boolean simple) {
    this.command = command;
    this.name = name;
    this.simple = simple;
  }

  /** resolve the path */
  public static StatePath resolve(String path, String stateVar) {
    // NOTE: this is a very quick and dirty implementation
    String command = stateVar;
    String toParse = path.trim();

    int kColon = toParse.indexOf(':');
    if (kColon >= 0) {
      String switchTo = toParse.substring(0, kColon).trim().toLowerCase(Locale.ENGLISH);
      if ("view".equals(switchTo)) {
        toParse = toParse.substring(kColon + 1).stripLeading();
        command = "$.pV(" + command + ")";
      } else if ("data".equals(switchTo)) {
        toParse = toParse.substring(kColon + 1).stripLeading();
        command = "$.pD(" + command + ")";
      }
    }
    while (true) {
      if (toParse.startsWith("/")) {
        toParse = toParse.substring(1).stripLeading();
        command = "$.pR(" + command + ")";
      } else if (toParse.startsWith("../")) {
        toParse = toParse.substring(3).stripLeading();
        command = "$.pU(" + command + ")";
      } else {
        int kSlash = toParse.indexOf('/');
        if (kSlash > 0) {
          String scopeInto = toParse.substring(0, kSlash).stripTrailing();
          toParse = toParse.substring(kSlash + 1).stripLeading();
          command = "$.pI(" + command + ",'" + scopeInto + "')";
        } else {
          return new StatePath(command, toParse, command.equals(stateVar));
        }
      }
    }
  }
}
