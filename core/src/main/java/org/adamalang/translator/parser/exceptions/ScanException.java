/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
