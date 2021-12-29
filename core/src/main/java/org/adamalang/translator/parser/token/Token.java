/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.parser.token;

import java.util.ArrayList;
import java.util.Objects;

/** a granular unit of a file, a string sequence */
public class Token implements Comparable<Token> {
  /** helper for merging two adjacent tokens */
  public static Token mergeAdjacentTokens(final Token left, final Token right, final MajorTokenType newMajorType, final MinorTokenType newMinorType) {
    final var token = new Token(left.sourceName, left.text + right.text, newMajorType, newMinorType, left.lineStart, left.charStart, right.lineEnd, right.charEnd);
    token.nonSemanticTokensPrior = left.nonSemanticTokensPrior;
    token.nonSemanticTokensAfter = right.nonSemanticTokensAfter;
    return token;
  }

  public static Token WRAP(final String text) {
    return new Token(null, text, null, null, 0, 0, 0, 0);
  }

  /** 0-offset character within the source indicating the end character within the
   * ending line of the token */
  public final int charEnd;
  /** 0-offset character within the source indicating the start character within
   * the starting line of the token */
  public final int charStart;
  /** 0-offset line within the source indicating the end of the token */
  public final int lineEnd;
  /** 0-offset line within the source indicating the start of the token */
  public final int lineStart;
  /** the major type of the token */
  public final MajorTokenType majorType;
  /** the minor type of the token (if available) */
  public final MinorTokenType minorType;
  /** hidden tokens which are after (i.e. right) this token (if available) */
  public ArrayList<Token> nonSemanticTokensAfter;
  /** hidden tokens which are prior (i.e. left) of this token (if available) */
  public ArrayList<Token> nonSemanticTokensPrior;
  /** the source of the token */
  public final String sourceName;
  /** the backing string of the token */
  public final String text;

  /** construct a token */
  public Token(final String sourceName, final String text, final MajorTokenType majorType, final MinorTokenType minorType, final int lineStart, final int charStart, final int lineEnd, final int charEnd) {
    this.sourceName = sourceName;
    this.text = text;
    this.majorType = majorType;
    this.minorType = minorType;
    this.lineStart = lineStart;
    this.charStart = charStart;
    this.lineEnd = lineEnd;
    this.charEnd = charEnd;
    nonSemanticTokensPrior = null;
    nonSemanticTokensAfter = null;
  }

  /** clone the token with new text */
  public Token cloneWithNewText(String newText) {
    return new Token(sourceName, newText, majorType, minorType, lineStart, charStart, lineEnd, charEnd);
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
      if (test == 0) { return Integer.compare(lineEnd, token.lineEnd); }
    }
    return test;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) { return true; }
    if (o == null || getClass() != o.getClass()) { return false; }
    final var token = (Token) o;
    return lineStart == token.lineStart && lineEnd == token.lineEnd && charStart == token.charStart && charEnd == token.charEnd && Objects.equals(sourceName, token.sourceName) && Objects.equals(text, token.text);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceName, text, lineStart, lineEnd, charStart, charEnd);
  }

  /** helper: is the token an identifier of one of the givens */
  public boolean isIdentifier(final String... ids) {
    if (majorType == MajorTokenType.Identifer) {
      if (ids == null || ids.length == 0) { return true; }
      for (final String id : ids) {
        if (id.equals(text)) { return true; }
      }
    }
    return false;
  }

  /** helper: is the token a keyword of one of the givens */
  public boolean isKeyword(final String... kws) {
    if (majorType == MajorTokenType.Keyword) {
      if (kws == null || kws.length == 0) { return true; }
      for (final String id : kws) {
        if (id.equals(text)) { return true; }
      }
    }
    return false;
  }

  /** helper: is the token a label */
  public boolean isLabel() {
    return majorType == MajorTokenType.Label;
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

  /** helper: is the token a symbol */
  public boolean isSymbol() {
    return majorType == MajorTokenType.Symbol;
  }

  /** helper: is the token a symbol and one of the given candidates */
  public boolean isSymbolWithTextEq(final String... candidates) {
    if (majorType == MajorTokenType.Symbol) {
      for (final String candidate : candidates) {
        if (text.equals(candidate)) { return true; }
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

  @Override
  public String toString() {
    return text;
  }
}
