/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.exceptions;

/** a message was aborted, and must be deleted */
public class AbortMessageException extends Exception {
  public final String policyFailure;

  public AbortMessageException() {
    this.policyFailure = null;
  }

  public AbortMessageException(String policyFailure) {
    this.policyFailure = policyFailure;
  }
}
