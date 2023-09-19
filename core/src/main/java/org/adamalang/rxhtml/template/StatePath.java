/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
package org.adamalang.rxhtml.template;

import org.adamalang.rxhtml.template.sp.*;

import java.util.ArrayList;
import java.util.Locale;

public class StatePath {
  public final String command;
  public final String name;
  public final boolean simple;
  public final ArrayList<PathInstruction> instructions;

  private StatePath(ArrayList<PathInstruction> instructions, String command, String name, boolean simple) {
    this.instructions = instructions;
    this.command = command;
    this.name = name;
    this.simple = simple;
  }

  /** resolve the path */
  public static StatePath resolve(String path, String stateVar) {
    // NOTE: this is a very quick and dirty implementation
    String command = stateVar;
    String toParse = path.trim();
    ArrayList<PathInstruction> instructions = new ArrayList<>();
    int kColon = toParse.indexOf(':');
    if (kColon >= 0) {
      String switchTo = toParse.substring(0, kColon).trim().toLowerCase(Locale.ENGLISH);
      if ("view".equals(switchTo)) {
        toParse = toParse.substring(kColon + 1).stripLeading();
        command = "$.pV(" + command + ")";
        instructions.add(new SwitchTo("view"));
      } else if ("data".equals(switchTo)) {
        toParse = toParse.substring(kColon + 1).stripLeading();
        command = "$.pD(" + command + ")";
        instructions.add(new SwitchTo("data"));
      }
    }
    while (true) {
      if (toParse.startsWith("/")) {
        toParse = toParse.substring(1).stripLeading();
        command = "$.pR(" + command + ")";
        instructions.add(new GoRoot());
      } else if (toParse.startsWith("../")) {
        toParse = toParse.substring(3).stripLeading();
        command = "$.pU(" + command + ")";
        instructions.add(new GoParent());
      } else {
        int kDecide = first(toParse.indexOf('/'), toParse.indexOf('.'));
        if (kDecide > 0) {
          String scopeInto = toParse.substring(0, kDecide).stripTrailing();
          toParse = toParse.substring(kDecide + 1).stripLeading();
          command = "$.pI(" + command + ",'" + scopeInto + "')";
          instructions.add(new DiveInto(scopeInto));
        } else {
          return new StatePath(instructions, command, toParse, command.equals(stateVar));
        }
      }
    }
  }

  private static int first(int kDot, int kSlash) {
    // if they are both present, then return the minimum
    if (kDot > 0 && kSlash > 0) {
      return Math.min(kDot, kSlash);
    }
    if (kDot > 0) {
      return kDot;
    } else {
      return kSlash;
    }
  }
}
