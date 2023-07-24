/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

import java.util.PrimitiveIterator;

/** escaping strings per various rules via flags */
public class Escaping {
  private final String str;
  private boolean escapeDoubleQuote = true;
  private boolean escapeSingleQuote = false;
  private boolean removeReturns = true;
  private boolean escapeReturns = true;

  public Escaping(String str) {
    this.str = str;
  }

  public Escaping switchQuotes() {
    escapeDoubleQuote = !escapeDoubleQuote;
    escapeSingleQuote = !escapeSingleQuote;
    return this;
  }

  public Escaping keepReturns() {
    removeReturns = false;
    return this;
  }

  public Escaping dontEscapeReturns() {
    escapeReturns = false;
    return this;
  }

  public String go() {
    StringBuilder result = new StringBuilder();
    PrimitiveIterator.OfInt it = str.codePoints().iterator();
    while (it.hasNext()) {
      int cp = it.nextInt();
      switch (cp) {
        case '\n':
          result.append("\\n");
          break;
        case '\\':
          result.append("\\\\");
          break;
        case '"':
          if (escapeDoubleQuote) {
            result.append("\\\"");
          } else {
            result.append("\"");
          }
          break;
        case '\'':
          if (escapeSingleQuote) {
            result.append("\\'");
          } else {
            result.append("'");
          }
          break;
        case '\r':
          if (!removeReturns) {
            if (escapeReturns) {
              result.append("\\r");
            } else {
              result.append("\r");
            }
          }
          break;
        default:
          result.append(Character.toChars(cp));
      }
    }
    return result.toString();
  }

  @Override
  public String toString() {
    return go();
  }
}
