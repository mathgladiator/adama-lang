/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.parser.token;

/** The scanner use a simple state machine, and this is the state of the scanner */
enum ScannerState {
  /** scanner is scanning for an identifier */
  ScanIdentifer,
  /** scanner is scanning for a numeric literal */
  ScanNumberLiteral,
  /** scanner is scanning for a double quote to end the string */
  ScanStringLiteral,
  /** scanner is scanning with a double quoted string, and is currently escaping a value */
  ScanStringLiteralEscape,
  /**
   * scanner is scanning with a double quoted string and is currently reading four unicode values
   */
  ScanStringLiteralUnicodeHexEscape,
  /** scanner is building a bundle of symbols */
  ScanSymbol,
  /** scanner is scanning a comment until the pairing, like --> */
  ScanUntilEndOfComment,
  /** scanner is scanning a comment until end of line */
  ScanUntilEndOfLine,
  /** scanner is scanning white space */
  ScanWhitespace,
  /** scanner is in an unknown state and requires input to decide next step */
  Unknown,
}
