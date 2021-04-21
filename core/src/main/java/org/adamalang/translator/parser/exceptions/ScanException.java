/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
