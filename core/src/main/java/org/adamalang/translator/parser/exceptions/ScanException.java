/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
