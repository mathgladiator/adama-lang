/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.parser.token;

import org.adamalang.translator.parser.exceptions.AdamaLangException;
import org.adamalang.translator.parser.exceptions.ScanException;
import org.adamalang.translator.tree.common.DocumentPosition;

import java.util.HashMap;
import java.util.function.Consumer;

/**
 * the state machine for handling codepoints one at a time; this produces output against a consumer
 */
class TokenReaderStateMachine {
  private final StringBuilder currentTokenBuffer;
  private final HashMap<String, String> deduper;
  private final Consumer<Token> output;
  private final String sourceName;
  private int currentCharNo;
  private int currentLineNo;
  private int currentByte;
  private MajorTokenType currentMajorTokenType;
  private MinorTokenType currentMinorTokenType;
  private int escapeHexCharsLeft;
  private int lastCodePoint;
  private ScannerState scanState;
  private int startCharNo;
  private int startLineNo;
  private int startByte;

  TokenReaderStateMachine(final String sourceName, final Consumer<Token> output) {
    this.sourceName = sourceName;
    currentTokenBuffer = new StringBuilder();
    lastCodePoint = 0;
    scanState = ScannerState.Unknown;
    this.output = output;
    startCharNo = 0;
    startLineNo = 0;
    currentMajorTokenType = null;
    currentMinorTokenType = null;
    deduper = new HashMap<>();
    currentByte = 0;
    startByte = 0;
  }

  public void consume(final int codepoint) throws AdamaLangException {
    if (codepoint == 0) {
      cut();
      return;
    }
    var doFinalCut = false;
    boolean doCutThenRetry;
    final var codePointInTable = codepoint < Tables.BOOLEAN_TABLES_SIZE;
    do {
      doCutThenRetry = false;
      switch (scanState) {
        case Unknown:
          currentMajorTokenType = null;
          currentMinorTokenType = null;
          if (codepoint < Tables.BOOLEAN_TABLES_SIZE) {
            if (Tables.START_IDENTIFIER_SCANNER[codepoint]) {
              scanState = ScannerState.ScanIdentifer;
              if (codepoint == '@') {
                currentMajorTokenType = MajorTokenType.Keyword;
              } else if (codepoint == '#') {
                currentMajorTokenType = MajorTokenType.Label;
              } else {
                currentMajorTokenType = MajorTokenType.Identifier;
              }
            } else if (Tables.WHITESPACE_SCANNER[codepoint]) {
              currentMajorTokenType = MajorTokenType.Whitespace;
              scanState = ScannerState.ScanWhitespace;
            } else if (Tables.SYMBOL_SCANNER[codepoint]) {
              scanState = ScannerState.ScanSymbol;
              currentMajorTokenType = MajorTokenType.Symbol;
            } else if (Tables.DIGITS_SCANNER[codepoint]) {
              scanState = ScannerState.ScanNumberLiteral;
              currentMajorTokenType = MajorTokenType.NumberLiteral;
              currentMinorTokenType = MinorTokenType.NumberIsInteger;
              break;
            } else if (codepoint == '"') {
              scanState = ScannerState.ScanStringLiteral;
              currentMajorTokenType = MajorTokenType.StringLiteral;
            } else {
              throw new ScanException("Failed to understand codepoint:" + codepoint + "('" + Character.toString(codepoint) + "')", position());
            }
          } else {
            throw new ScanException("Codepoint fell outside of valid range:" + codepoint + "('" + Character.toString(codepoint) + "') is outside of [0, " + Tables.BOOLEAN_TABLES_SIZE + ")", position());
          }
          break;
        case ScanIdentifer:
          if (!codePointInTable || !Tables.PART_IDENTIFIER_SCANNER[codepoint]) {
            doCutThenRetry = true;
          }
          break;
        case ScanWhitespace:
          if (!codePointInTable || !Tables.WHITESPACE_SCANNER[codepoint]) {
            doCutThenRetry = true;
          }
          break;
        case ScanUntilEndOfLine:
          if (codepoint == '\n') {
            doFinalCut = true;
          }
          break;
        case ScanUntilEndOfComment:
          if (codepoint == '/' && lastCodePoint == '*') { // */
            doFinalCut = true;
          }
          break;
        case ScanSymbol:
          if (codepoint == '/' && lastCodePoint == '/') { //
            cutPriorSymbols();
            currentMajorTokenType = MajorTokenType.Comment;
            currentMinorTokenType = MinorTokenType.CommentEndOfLine;
            scanState = ScannerState.ScanUntilEndOfLine;
            break;
          } else if (codepoint == '*' && lastCodePoint == '/') { // /*
            cutPriorSymbols();
            currentMajorTokenType = MajorTokenType.Comment;
            currentMinorTokenType = MinorTokenType.CommentBlock;
            scanState = ScannerState.ScanUntilEndOfComment;
            break;
          }
          if (!(codepoint < Tables.SYMBOL_SCANNER.length && Tables.SYMBOL_SCANNER[codepoint])) {
            doCutThenRetry = true;
            scanState = ScannerState.Unknown;
          }
          break;
        case ScanNumberLiteral:
          if (currentMinorTokenType == MinorTokenType.NumberIsDouble) {
            if (!(codepoint < Tables.DOUBLE_SCANNER.length && Tables.DOUBLE_SCANNER[codepoint])) {
              doCutThenRetry = true;
              scanState = ScannerState.Unknown;
            }
          } else {
            if (!(codepoint < Tables.HEX_SCANNER.length && Tables.HEX_SCANNER[codepoint])) {
              if (codepoint < Tables.DOUBLE_SCANNER.length && Tables.DOUBLE_SCANNER[codepoint]) {
                currentMinorTokenType = MinorTokenType.NumberIsDouble;
              } else {
                if (codepoint == 'L') {
                  doFinalCut = true;
                } else {
                  doCutThenRetry = true;
                  scanState = ScannerState.Unknown;
                }
              }
            }
          }
          break;
        case ScanStringLiteral:
          if ('\\' == codepoint) {
            scanState = ScannerState.ScanStringLiteralEscape;
          } else if ('"' == codepoint) {
            doFinalCut = true;
            break;
          }
          break;
        case ScanStringLiteralEscape:
          scanState = ScannerState.ScanStringLiteral;
          if (codepoint < Tables.SINGLE_CHAR_ESCAPE_SCANNER.length && Tables.SINGLE_CHAR_ESCAPE_SCANNER[codepoint]) {
            // cool beans, it is valid
          } else if (codepoint == 'u') {
            scanState = ScannerState.ScanStringLiteralUnicodeHexEscape;
            escapeHexCharsLeft = 4;
          } else {
            throw new ScanException("Unrecognized string escape value:" + codepoint + "('" + Character.toString(codepoint) + "')", position());
          }
          break;
        case ScanStringLiteralUnicodeHexEscape:
          if (!(codepoint < Tables.HEX_SCANNER.length && Tables.HEX_SCANNER[codepoint])) {
            throw new ScanException("Unrecognized hex value within the unicode escape value:" + codepoint + "('" + Character.toString(codepoint) + "')", position());
          }
          escapeHexCharsLeft--;
          if (escapeHexCharsLeft <= 0) {
            scanState = ScannerState.ScanStringLiteral;
          }
          break;
      }
      if (doCutThenRetry) {
        cut();
        scanState = ScannerState.Unknown;
      }
    } while (doCutThenRetry);
    currentByte++;
    if ('\n' == codepoint) {
      currentLineNo++;
      currentCharNo = 0;
    } else {
      currentCharNo++;
    }
    if (codePointInTable) {
      currentTokenBuffer.append((char) codepoint);
    } else {
      currentTokenBuffer.append(Character.toString(codepoint));
    }
    lastCodePoint = codepoint;
    if (doFinalCut) {
      cut();
      scanState = ScannerState.Unknown;
    }
  }

  /** Issue #138; comments would assume the entire bundle of symbols taking them out of the stream. Here, we pre-flush the symbols as we transition to comments */
  private void cutPriorSymbols() {
    var text = currentTokenBuffer.toString();
    for (var k = 0; k < text.length() - 1; k++) {
      final var symbol = dedupe(text.substring(k, k + 1), currentMajorTokenType);
      output.accept(new Token(sourceName, symbol, currentMajorTokenType, currentMinorTokenType, startLineNo, startCharNo + k, startLineNo, startCharNo + k + symbol.length(), startByte + k, startByte + k + symbol.length()));
    }
    startLineNo = currentLineNo;
    startCharNo = currentCharNo - 1;
    startByte = currentByte - 1;
    currentTokenBuffer.setLength(0);
    currentTokenBuffer.append(text.charAt(text.length() - 1));
  }

  private void cut() {
    if (currentMajorTokenType == null) {
      // nothing to cut
      return;
    }
    var text = currentTokenBuffer.toString();
    if (currentMajorTokenType == MajorTokenType.Identifier) {
      if (Tables.KEYWORD_TABLE.contains(text)) {
        currentMajorTokenType = MajorTokenType.Keyword;
      }
    }
    if (currentMajorTokenType == MajorTokenType.Symbol && text.length() > 1) {
      for (var k = 0; k < text.length(); k++) {
        final var symbol = dedupe(text.substring(k, k + 1), currentMajorTokenType);
        output.accept(new Token(sourceName, symbol, currentMajorTokenType, currentMinorTokenType, startLineNo, startCharNo + k, startLineNo, startCharNo + k + symbol.length(), startByte + k, startByte + k + symbol.length()));
      }
    } else {
      text = dedupe(text, currentMajorTokenType);
      output.accept(new Token(sourceName, text, currentMajorTokenType, currentMinorTokenType, startLineNo, startCharNo, currentLineNo, currentCharNo, startByte, currentByte));
    }
    startLineNo = currentLineNo;
    startCharNo = currentCharNo;
    startByte = currentByte;
    currentTokenBuffer.setLength(0);
    currentMajorTokenType = null;
    currentMinorTokenType = null;
  }

  public DocumentPosition position() {
    final var position = new DocumentPosition();
    position.ingest(currentLineNo, currentCharNo, currentByte);
    return position;
  }

  public String dedupe(final String text, final MajorTokenType tokenType) {
    switch (tokenType) {
      case NumberLiteral:
      case Symbol:
      case Keyword:
      case Label:
      case Identifier:
        var prior = deduper.get(text);
        if (prior == null) {
          deduper.put(text, text);
          prior = text;
        }
        return prior;
      default:
        return text;
    }
  }
}
