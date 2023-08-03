/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.atl;

import java.util.ArrayList;
import java.util.Iterator;

public class TokenStream {

  private static void transferCharacter(StringBuilder currentText, int cp) {
    currentText.append(Character.toString(cp));
    if (cp == '\\') {
      currentText.append(Character.toString(cp));
    }
  }

  public static ArrayList<Token> tokenize(String text) {
    ArrayList<Token> tokens = new ArrayList<>();
    StringBuilder currentText = new StringBuilder();
    ScanState state = ScanState.Start;
    Iterator<Integer> it = text.codePoints().iterator();
    ArrayList<String> operands = new ArrayList<>();

    while (it.hasNext()) {
      int cp = it.next();
      switch (cp) {
        case '`':
          if (state == ScanState.Start) {
            state = ScanState.Text;
          }
          cp = it.next();
          transferCharacter(currentText, cp);
          break;
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
            default:
              transferCharacter(currentText, cp);
              break;
          }
          break;
        }
        case '}': {
          if (state == ScanState.PushVariable) {
            state = ScanState.Start;
            operands.add(currentText.toString().trim());
            currentText.setLength(0);
            String base = operands.remove(0);
            tokens.add(new Token(Type.Variable, base, operands.toArray(new String[operands.size()])));
            operands.clear();
          } else {
            transferCharacter(currentText, cp);
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
          if (state == ScanState.PushCondition) {
            state = ScanState.Start;
            operands.add(currentText.toString().trim());
            currentText.setLength(0);
            String base = operands.remove(0);
            tokens.add(new Token(Type.Condition, base, operands.toArray(new String[operands.size()])));
            operands.clear();
          } else {
            transferCharacter(currentText, cp);
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
              transferCharacter(currentText, cp);
          }
          break;
        }
        default: {
          switch (state) {
            case Start:
            case Text:
              state = ScanState.Text;
            case PushVariable:
            case PushCondition:
              transferCharacter(currentText, cp);
          }
        }
      }
    }
    if (currentText.length() > 0) {
      if (state == ScanState.Text) {
        tokens.add(new Token(Type.Text, currentText.toString()));
      } else {
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
