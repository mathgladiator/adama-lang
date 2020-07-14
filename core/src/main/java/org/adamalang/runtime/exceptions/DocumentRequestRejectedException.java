/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.exceptions;

/** An attempt was made to change a document, and it was rejected */
public class DocumentRequestRejectedException extends Exception {
  /** why it was rejected */
  public final DocumentRequestRejectedReason reason;

  public DocumentRequestRejectedException(final DocumentRequestRejectedReason reason) {
    this.reason = reason;
  }
}
