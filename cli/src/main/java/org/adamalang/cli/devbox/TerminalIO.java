/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.devbox;

import org.adamalang.cli.Util;
import org.jline.reader.*;
import org.jline.utils.AttributedString;

/** a thin wrapper around LineReader */
public class TerminalIO {
  private final LineReader reader;

  public TerminalIO() {
    this.reader = LineReaderBuilder
        .builder()
        .appName("Adama DevBox")
        .build();
  }

  public synchronized void notice(String ln) {
    reader.printAbove(AttributedString.fromAnsi(Util.prefix("NOTICE:" + ln, Util.ANSI.Yellow)));
  }

  public synchronized void info(String ln) {
    reader.printAbove(AttributedString.fromAnsi(Util.prefix("  INFO:" + ln, Util.ANSI.Green)));
  }

  public synchronized void error(String ln) {
    reader.printAbove(AttributedString.fromAnsi(Util.prefix(" ERROR:" + ln, Util.ANSI.Red)));
  }

  public String readline() {
    return reader.readLine("adama>");
  }
}
