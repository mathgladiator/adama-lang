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
package org.adamalang.translator.parser.token;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** a granular unit of a file, a string sequence */
public class Token implements Comparable<Token> {
  /**
   * 0-offset character within the source indicating the start character within the starting line of
   * the token
   */
  public final int charStart;
  /**
   * 0-offset character within the source indicating the end character within the ending line of the
   * token
   */
  public final int charEnd;
  /** 0-offset line within the source indicating the start of the token */
  public final int lineStart;
  /** 0-offset line within the source indicating the end of the token */
  public final int lineEnd;
  /** 0-offset byte when the token starts */
  public final int byteStart;
  /** 0-offset byte when the token ends */
  public final int byteEnd;

  /** the major type of the token */
  public final MajorTokenType majorType;
  /** the minor type of the token (if available) */
  public final MinorTokenType minorType;
  /** the source of the token */
  public final String sourceName;
  /** the backing string of the token */
  public final String text;
  /** hidden tokens which are after (i.e. right) this token (if available) */
  public ArrayList<Token> nonSemanticTokensAfter;
  /** hidden tokens which are prior (i.e. left) of this token (if available) */
  public ArrayList<Token> nonSemanticTokensPrior;

  /** construct a token */
  public Token(final String sourceName, final String text, final MajorTokenType majorType, final MinorTokenType minorType, final int lineStart, final int charStart, final int lineEnd, final int charEnd, int byteStart, int byteEnd) {
    this.sourceName = sourceName;
    this.text = text;
    this.majorType = majorType;
    this.minorType = minorType;
    this.lineStart = lineStart;
    this.charStart = charStart;
    this.lineEnd = lineEnd;
    this.charEnd = charEnd;
    this.byteStart = byteStart;
    this.byteEnd = byteEnd;
    nonSemanticTokensPrior = null;
    nonSemanticTokensAfter = null;
  }

  /** helper for merging two adjacent tokens */
  public static Token mergeAdjacentTokens(final Token left, final Token right, final MajorTokenType newMajorType, final MinorTokenType newMinorType) {
    StringBuilder text = new StringBuilder();
    {
      text.append(left.text);
      if (left.nonSemanticTokensAfter != null) {
        for (Token after : left.nonSemanticTokensAfter) {
          text.append(after.text);
        }
      }
      if (right.nonSemanticTokensPrior != null) {
        for (Token prior : right.nonSemanticTokensPrior) {
          text.append(prior.text);
        }
      }
      text.append(right.text);
    }
    final var token = new Token(left.sourceName, text.toString(), newMajorType, newMinorType, left.lineStart, left.charStart, right.lineEnd, right.charEnd, left.byteStart, right.byteEnd);
    token.nonSemanticTokensPrior = left.nonSemanticTokensPrior;
    token.nonSemanticTokensAfter = right.nonSemanticTokensAfter;
    return token;
  }

  public static Token WRAP(final String text) {
    return new Token(null, text, null, null, Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0, Integer.MAX_VALUE, 0);
  }

  public static Token WS(final String text) {
    return new Token(null, text, MajorTokenType.Whitespace, null, Integer.MAX_VALUE, Integer.MAX_VALUE, 0, 0, Integer.MAX_VALUE, 0);
  }

  /** clone the token with new text */
  public Token cloneWithNewText(String newText) {
    return new Token(sourceName, newText, majorType, minorType, lineStart, charStart, lineEnd, charEnd, byteStart, byteEnd);
  }

  /** internal: adds a hidden token after this token */
  protected void addHiddenTokenAfter(final Token token) {
    if (nonSemanticTokensAfter == null) {
      nonSemanticTokensAfter = new ArrayList<>(1);
    }
    nonSemanticTokensAfter.add(token);
  }

  @Override
  public int compareTo(final Token token) {
    var test = text.compareTo(token.text);
    if (test == 0) {
      test = Integer.compare(lineStart, token.lineStart);
      if (test == 0) {
        return Integer.compare(lineEnd, token.lineEnd);
      }
    }
    return test;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Token token = (Token) o;
    return charStart == token.charStart && charEnd == token.charEnd && lineStart == token.lineStart && lineEnd == token.lineEnd && byteStart == token.byteStart && byteEnd == token.byteEnd && majorType == token.majorType && minorType == token.minorType && Objects.equals(sourceName, token.sourceName) && Objects.equals(text, token.text) && Objects.equals(nonSemanticTokensAfter, token.nonSemanticTokensAfter) && Objects.equals(nonSemanticTokensPrior, token.nonSemanticTokensPrior);
  }

  @Override
  public int hashCode() {
    return Objects.hash(charStart, charEnd, lineStart, lineEnd, byteStart, byteEnd, majorType, minorType, sourceName, text, nonSemanticTokensAfter, nonSemanticTokensPrior);
  }

  @Override
  public String toString() {
    return text;
  }

  /** helper: is the token an identifier of one of the givens */
  public boolean isIdentifier(final String... ids) {
    if (majorType == MajorTokenType.Identifier) {
      if (ids == null || ids.length == 0) {
        return true;
      }
      for (final String id : ids) {
        if (id.equals(text)) {
          return true;
        }
      }
    }
    return false;
  }

  /** helper: is the token a keyword of one of the givens */
  public boolean isKeyword(final String... kws) {
    if (majorType == MajorTokenType.Keyword) {
      if (kws == null || kws.length == 0) {
        return true;
      }
      for (final String id : kws) {
        if (id.equals(text)) {
          return true;
        }
      }
    }
    return false;
  }

  /** helper: is the token a label */
  public boolean isLabel() {
    return majorType == MajorTokenType.Label;
  }

  public boolean isTemplate() {
    return majorType == MajorTokenType.Template;
  }

  /** helper: is a literal number of either double or integer type */
  public boolean isNumberLiteral() {
    return majorType == MajorTokenType.NumberLiteral;
  }

  /** helper: is the token a numeric double */
  public boolean isNumberLiteralDouble() {
    return majorType == MajorTokenType.NumberLiteral && minorType == MinorTokenType.NumberIsDouble;
  }

  /** helper: is the token a numeric integer */
  public boolean isNumberLiteralInteger() {
    return majorType == MajorTokenType.NumberLiteral && minorType == MinorTokenType.NumberIsInteger;
  }

  /** helper: is the token a string literal */
  public boolean isStringLiteral() {
    return majorType == MajorTokenType.StringLiteral;
  }

  public Token stripStringLiteral() {
    if (isStringLiteral()) {
      return new Token(sourceName, text.substring(1, text.length() - 1),  majorType, minorType, lineStart, charStart, lineEnd, charEnd, byteStart, byteEnd);
    }
    return this;
  }

  /** helper: is the token a symbol */
  public boolean isSymbol() {
    return majorType == MajorTokenType.Symbol;
  }

  /** helper: is the token a symbol and one of the given candidates */
  public boolean isSymbolWithTextEq(final String... candidates) {
    if (majorType == MajorTokenType.Symbol) {
      for (final String candidate : candidates) {
        if (text.equals(candidate)) {
          return true;
        }
      }
    }
    return false;
  }

  /** helpful to indicate where in a file (0-indexed) an issue happened */
  public String toExceptionMessageTrailer() {
    final var str = new StringBuilder();
    str.append(" {Token: `").append(text).append("` @ (").append(lineStart).append(",").append(charStart).append(") -> (").append(lineEnd).append(",").append(charEnd).append("): ").append(majorType);
    if (minorType != null) {
      str.append(":").append(minorType);
    }
    str.append("}");
    return str.toString();
  }

  public static boolean isEmpty(List<Token> set) {
    if (set == null) {
      return true;
    }
    return set.isEmpty();
  }
}
