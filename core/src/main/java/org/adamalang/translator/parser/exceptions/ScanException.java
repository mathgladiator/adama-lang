/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.parser.exceptions;

import org.adamalang.translator.tree.common.DocumentPosition;

/** an issue which happened during scanning (i.e. lexical analysis) */
public class ScanException extends AdamaLangException {
  public final DocumentPosition position;

  public ScanException(final String message, final DocumentPosition position) {
    super(message);
    this.position = position;
  }
}
