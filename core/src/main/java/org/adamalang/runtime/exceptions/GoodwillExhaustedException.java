/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.exceptions;

/** an infinite loop or just too much compute was used. */
public class GoodwillExhaustedException extends RuntimeException {
  public GoodwillExhaustedException(final int startLine, final int startPosition, final int endLine, final int endLinePosition) {
    super("Good will exhausted:" + startLine + "," + startPosition + " --> " + endLine + "," + endLinePosition);
  }
}
