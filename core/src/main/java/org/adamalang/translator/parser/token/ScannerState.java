/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.parser.token;

/** The scanner use a simple state machine, and this is the state of the
 * scanner */
enum ScannerState {
  /** scanner is scanning for an identifier */
  ScanIdentifer,
  /** scanner is scanning for a numeric literal */
  ScanNumberLiteral,
  /** scanner is scanning for a double quote to end the string */
  ScanStringLiteral,
  /** scanner is scanning with a double quoted string, and is currently escaping a
   * value */
  ScanStringLiteralEscape,
  /** scanner is scanning with a double quoted string and is currently reading
   * four unicode values */
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
