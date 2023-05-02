/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml.atl;

import java.util.ArrayList;
import java.util.Iterator;

public class TokenStream {
  public static ArrayList<Token> tokenize(String text) {
    ArrayList<Token> tokens = new ArrayList<>();
    StringBuilder currentText = new StringBuilder();
    ScanState state = ScanState.Start;
    Iterator<Integer> it = text.codePoints().iterator();
    ArrayList<String> operands = new ArrayList<>();

    while (it.hasNext()) {
      int cp = it.next();
      switch (cp) {
        case '{': {
          switch (state) {
            case Text: {
              tokens.add(new Token(Type.Text, currentText.toString()));
              currentText.setLength(0);
              state = ScanState.PushVariable;
              break;
            }
            case Start: {
              state = ScanState.PushVariable;
              break;
            }
            default: {
              throw new UnsupportedOperationException("well, unexpected");
            }
          }
          break;
        }
        case '}': {
          switch (state) {
            case PushVariable:
              state = ScanState.Start;
              operands.add(currentText.toString().trim());
              currentText.setLength(0);
              String base = operands.remove(0);
              tokens.add(new Token(Type.Variable, base, operands.toArray(new String[operands.size()])));
              operands.clear();
              break;
            default:
              throw new UnsupportedOperationException("well, unexpected");
          }
          break;
        }
        case '[': {
          switch (state) {
            case Text: {
              tokens.add(new Token(Type.Text, currentText.toString()));
              currentText.setLength(0);
              state = ScanState.PushCondition;
              break;
            }
            case Start: {
              state = ScanState.PushCondition;
              break;
            }
            default: {
              throw new UnsupportedOperationException("well, unexpected");
            }
          }
          break;
        }
        case ']': {
          switch (state) {
            case PushCondition:
              state = ScanState.Start;
              operands.add(currentText.toString().trim());
              currentText.setLength(0);
              String base = operands.remove(0);
              tokens.add(new Token(Type.Condition, base, operands.toArray(new String[operands.size()])));
              operands.clear();
              break;
            default:
              throw new UnsupportedOperationException("well, unexpected");
          }
          break;
        }
        case '|': {
          switch (state) {
            case PushCondition:
            case PushVariable:
              operands.add(currentText.toString().trim());
              currentText.setLength(0);
              break;
            default:
              throw new UnsupportedOperationException();
          }
          break;
        }
        default: {
          switch (state) {
            case Start:
            case Text:
              state = ScanState.Text;
              currentText.append((char) cp); // TODO: escape unicode characters
              break;
            case PushVariable:
            case PushCondition:
              currentText.append((char) cp); // TODO: escape unicode characters

          }
        }
      }
    }
    if (currentText.length() > 0) {
      switch (state) {
        case Text:
          tokens.add(new Token(Type.Text, currentText.toString()));
          break;
        default:
          throw new UnsupportedOperationException();
      }
    }
    return tokens;
  }

  private enum ScanState {
    Start, Text, PushVariable, PushCondition,
  }

  public enum Type {
    Text, Variable, Condition
  }

  public enum Modifier {
    None, Not, Else, End,
  }

  public static class Token {
    public final Type type;
    public final Modifier mod;
    public final String base;
    public final String[] transforms;

    public Token(Type type, String base, String... transforms) {
      this.type = type;
      if (base.startsWith("#") && type == Type.Condition) {
        this.mod = Modifier.Else;
        this.base = base.substring(1).trim();
      } else if (base.startsWith("!")) {
        this.mod = Modifier.Not;
        this.base = base.substring(1).trim();
      } else if (base.startsWith("/") && type == Type.Condition) {
        this.mod = Modifier.End;
        this.base = base.substring(1).trim();
      } else {
        this.mod = Modifier.None;
        this.base = base;
      }
      this.transforms = transforms;
    }
  }
}
