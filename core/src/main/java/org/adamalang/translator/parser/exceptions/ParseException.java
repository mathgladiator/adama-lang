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
package org.adamalang.translator.parser.exceptions;

import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;

/** an issue happened when building the tree */
public class ParseException extends AdamaLangException {
  public final String rawMessage;
  public final Token token;

  public ParseException(final String message, final Token token) {
    super(messageOf(message, token));
    rawMessage = message;
    this.token = token;
  }

  /** helpful to convert tokens into a string for the parent exception */
  private static String messageOf(final String message, final Token token) {
    final var sb = new StringBuilder();
    sb.append(message);
    if (token != null) {
      sb.append(token.toExceptionMessageTrailer());
    }
    return sb.toString();
  }

  /** this is the position within the tree */
  public DocumentPosition toDocumentPosition() {
    final var dp = new DocumentPosition();
    if (token != null) {
      dp.ingest(token);
    }
    return dp;
  }
}
