/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.exceptions;

import org.adamalang.runtime.async.AsyncTask;

/** when we abort, we need to restart the loop. A retry indicates what do the document's state */
public class RetryProgressException extends Exception {
  public final AsyncTask failedTask;

  public RetryProgressException(final AsyncTask failedTask) {
    this.failedTask = failedTask;
  }
}
