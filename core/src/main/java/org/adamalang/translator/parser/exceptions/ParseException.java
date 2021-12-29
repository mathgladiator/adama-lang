/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.parser.exceptions;

import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;

/** an issue happened when building the tree */
public class ParseException extends AdamaLangException {
  /** helpful to convert tokens into a string for the parent exception */
  private static String messageOf(final String message, final Token token) {
    final var sb = new StringBuilder();
    sb.append(message);
    if (token != null) {
      sb.append(token.toExceptionMessageTrailer());
    }
    return sb.toString();
  }

  public final String rawMessage;
  public final Token token;

  public ParseException(final String message, final Token token) {
    super(messageOf(message, token));
    rawMessage = message;
    this.token = token;
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
