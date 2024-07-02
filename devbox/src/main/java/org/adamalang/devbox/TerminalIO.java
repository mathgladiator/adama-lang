/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.devbox;

import org.adamalang.common.ANSI;
import org.adamalang.common.ColorUtilTools;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.utils.AttributedString;

/** a thin wrapper around LineReader */
public class TerminalIO {
  private final LineReader reader;

  public TerminalIO() {
    this.reader = LineReaderBuilder.builder().appName("Adama DevBox").build();
  }

  public synchronized void notice(String ln) {
    reader.printAbove(AttributedString.fromAnsi(ColorUtilTools.prefix("     NOTICE:" + ln, ANSI.Yellow)));
  }

  public synchronized void witness(String ln) {
    reader.printAbove(AttributedString.fromAnsi(ColorUtilTools.prefix("    WITNESS:" + ln, ANSI.Magenta)));
  }

  public synchronized void important(String ln) {
    reader.printAbove(AttributedString.fromAnsi(ColorUtilTools.prefix(">IMPORTANT<:" + ln, ANSI.Cyan)));
  }

  public synchronized void info(String ln) {
    reader.printAbove(AttributedString.fromAnsi(ColorUtilTools.prefix("       INFO:" + ln, ANSI.Green)));
  }

  public synchronized void error(String ln) {
    reader.printAbove(AttributedString.fromAnsi(ColorUtilTools.prefix("      ERROR:" + ln, ANSI.Red)));
  }

  public String readline() {
    return reader.readLine("adama>");
  }
}
