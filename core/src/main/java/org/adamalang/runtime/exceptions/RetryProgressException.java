/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.exceptions;

import org.adamalang.runtime.async.AsyncTask;

/** when we abort, we need to restart the loop. A retry indicates what do the
 * document's state */
public class RetryProgressException extends Exception {
  public final AsyncTask failedTask;

  public RetryProgressException(final AsyncTask failedTask) {
    this.failedTask = failedTask;
  }
}
