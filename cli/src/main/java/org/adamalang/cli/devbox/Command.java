package org.adamalang.cli.devbox;

import java.util.ArrayList;
import java.util.PrimitiveIterator;

public class Command {
  public final String command;
  public final String[] args;

  public Command(String command, String[] args) {
    this.command = command;
    this.args = args;
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

  public boolean requireArg(int index) {
    return args.length > index;
  }

  public String argAt(int index) {
    return args[index];
  }

  public static Command parse(String lnRaw) {
    String ln = lnRaw.trim();
    int firstSpace = ln.indexOf(' ');
    if (firstSpace < 0) {
      return new Command(ln.toLowerCase(), new String[] {});
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
}
