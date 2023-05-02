/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml.template;

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
    String toParse = path;
    if (toParse.startsWith("view:")) {
      toParse = toParse.substring(5);
      command = "$.pV(" + command + ")";
    }
    if (toParse.startsWith("data:")) {
      toParse = toParse.substring(5);
      command = "$.pD(" + command + ")";
    }
    while (true) {
      if (toParse.startsWith("/")) {
        toParse = toParse.substring(1);
        command = "$.pR(" + command + ")";
      } else if (toParse.startsWith("../")) {
        toParse = toParse.substring(3);
        command = "$.pU(" + command + ")";
      } else {
        int kSlash = toParse.indexOf('/');
        if (kSlash > 0) {
          String scopeInto = toParse.substring(0, kSlash);
          toParse = toParse.substring(kSlash + 1);
          command = "$.pI(" + command + ",'" + scopeInto + "')";
        } else {
          return new StatePath(command, toParse, command.equals(stateVar));
        }
      }
    }
  }
}
