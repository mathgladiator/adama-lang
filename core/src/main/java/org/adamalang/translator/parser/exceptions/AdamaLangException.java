/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.parser.exceptions;

/** an exception indicating a problem in understanding the language */
public class AdamaLangException extends Exception {
  public AdamaLangException(final String message) {
    super(message);
  }
}
