/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
  /** A template */
  ScanTemplate,
  /** scanner is in an unknown state and requires input to decide next step */
  Unknown,
}
