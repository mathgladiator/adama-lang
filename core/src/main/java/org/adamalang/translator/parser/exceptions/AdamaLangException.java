/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.parser.exceptions;

/** an exception indicating a problem in understanding the language */
public class AdamaLangException extends Exception {
  public AdamaLangException(final String message) {
    super(message);
  }
}
