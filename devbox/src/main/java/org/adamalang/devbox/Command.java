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

import org.adamalang.runtime.data.Key;

import java.io.File;
import java.util.ArrayList;
import java.util.PrimitiveIterator;

/** command line parser for simple terminal-like behavior */
public class Command {
  public final String command;
  public final String[] args;

  public Command(String command, String[] args) {
    this.command = command;
    this.args = args;
  }

  public static Command parse(String lnRaw) {
    String ln = lnRaw.trim();
    int firstSpace = ln.indexOf(' ');
    if (firstSpace < 0) {
      return new Command(ln.toLowerCase(), new String[]{});
    }
    String command = ln.substring(0, firstSpace).toLowerCase();
    ln = ln.substring(firstSpace).trim();
    PrimitiveIterator.OfInt it = ln.codePoints().iterator();
    StringBuilder sb = new StringBuilder();
    ArrayList<String> args = new ArrayList<>();
    boolean instr = false;
    while (it.hasNext()) {
      int cp = it.nextInt();
      if (instr) {
        if (cp == '\\') {
          cp = it.nextInt();
          switch (cp) {
            case 'n':
              sb.append("\n");
              break;
            case 't':
              sb.append("\t");
              break;
            case 'r':
              sb.append("\r");
              break;
            case 'b':
              sb.append("\b");
              break;
            // TODO: /u
            default:
              sb.append(Character.toString(cp));
          }
        } else if (cp == '"') {
          args.add(sb.toString());
          sb.setLength(0);
          instr = false;
        } else {
          sb.append(Character.toString(cp));
        }
      } else {
        if (cp == '"') {
          instr = true;
        } else if (Character.isWhitespace(cp)) {
          if (sb.length() > 0) {
            args.add(sb.toString());
          }
          sb.setLength(0);
        } else {
          sb.append(Character.toString(cp));
        }
      }
    }
    if (sb.length() > 0) {
      args.add(sb.toString());
    }
    return new Command(command, args.toArray(new String[args.size()]));
  }

  public boolean is(String... candidates) {
    for (String candidate : candidates) {
      if (command.equals(candidate)) {
        return true;
      }
    }
    return false;
  }

  public boolean argIs(int index, String... candidates) {
    if (args.length > index) {
      for (String candidate : candidates) {
        if (args[index].equals(candidate)) {
          return true;
        }
      }
    }
    return false;
  }

  public Integer argAtIsInt(int index) {
    try {
      if (index < args.length) {
        return Integer.parseInt(args[index]);
      }
    } catch (NumberFormatException nfe) {
    }
    return null;
  }

  public Key extractKeyAsFirstTwoArgs(Key defaultKey) {
    if (requireArg(1)) {
      return new Key(argAt(0), argAt(1));
    }
    return defaultKey;
  }

  public boolean requireArg(int index) {
    return args.length > index;
  }

  public String argAt(int index) {
    return args[index];
  }

  public File lastArgAsFileThatMustExist(TerminalIO io) {
    String arg = lastArg();
    if (arg != null) {
      File f = new File(arg);
      if (f.exists() && !f.isDirectory()) {
        return f;
      }
      io.error("File '" + arg + "' does not exist");
    } else {
      io.error("No file specified");
    }
    return null;
  }

  public String lastArg() {
    if (args.length > 0) {
      return args[args.length - 1];
    }
    return null;
  }

  public File lastArgAsFileThatMustNotExist(TerminalIO io) {
    String arg = lastArg();
    if (arg != null) {
      File f = new File(arg);
      if (!f.exists()) {
        return f;
      }
      io.error("File '" + arg + "' already exist");
    } else {
      io.error("No file specified");
    }
    return null;
  }
}
