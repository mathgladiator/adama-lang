package org.adamalang.rxhtml;

import java.util.ArrayList;
import java.util.Iterator;

public class AttributeTemplateLanguage {
  private static enum ScanState {
    Start,
    Text,
    PushVariable,
    PushCondition,
  }
  public static enum Type {
    Text,
    Variable,
    IfVariable,
    IfNotVariable,
  }

  public static class Token {
    public final Type type;
    public final String base;
    public final String[] transforms;

    public Token(Type type, String base, String... transforms) {
      this.type = type;
      this.base = base;
      this.transforms = transforms;
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
            case PushVariable:
              break;
            default:
              throw new UnsupportedOperationException("well, unexpected");
          }
          break;
        }
        case '|': {
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
              currentText.append((char) cp); // TODO: escape unicode characters

          }
        }
      }
    }
    return tokens;
  }
  /*
  private final String text;
  private int at;

  public AttributeTemplateLanguage(String text) {
    this.text = text;
    this.at = 0;
  }
  */

  /*
  public static ArrayList<JavaScriptAttributeToken> tokenize(String text) {

  }
  */

  // blah, blah, {var}, blah [x] if x is true [/x] [!x] if x is false [/x] {var | transform1 | transform2 | ... | transformN}
}
