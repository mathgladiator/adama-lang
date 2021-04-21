/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.exceptions;

/** an infinite loop or just too much compute was used. */
public class GoodwillExhaustedException extends RuntimeException {
  public GoodwillExhaustedException(final int startLine, final int startPosition, final int endLine, final int endLinePosition) {
    super("Good will exhausted:" + startLine + "," + startPosition + " --> " + endLine + "," + endLinePosition);
  }
}
