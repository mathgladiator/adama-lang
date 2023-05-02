/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
