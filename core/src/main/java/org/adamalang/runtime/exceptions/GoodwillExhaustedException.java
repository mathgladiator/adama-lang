/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.exceptions;

/** an infinite loop or just too much compute was used. */
public class GoodwillExhaustedException extends RuntimeException {
  public GoodwillExhaustedException(final int startLine, final int startPosition, final int endLine, final int endLinePosition) {
    super("Good will exhausted:" + startLine + "," + startPosition + " --> " + endLine + "," + endLinePosition);
  }
}
