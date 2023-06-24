/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.natives;

import org.adamalang.ErrorCodes;
import org.adamalang.runtime.exceptions.ComputeBlockedException;

/** a result for an async operation */
public class NtResult<T> {
  private final T value;
  private final boolean failed;
  private final int failureCode;
  private final String message;

  public NtResult(final T value, boolean failed, int failureCode, String message) {
    this.value = value;
    this.failed = failed;
    this.failureCode = failureCode;
    this.message = failed ? message : (value != null ? "OK" : "waiting...");
  }

  public NtResult(NtResult<T> other) {
    this.value = other.value;
    this.failed = other.failed;
    this.failureCode = other.failureCode;
    this.message = other.message;
  }

  /** get the value; note; this returns null and is not appropriate for the runtime */
  public T get() {
    return this.value;
  }

  /** is it available */
  public boolean has() {
    return value != null;
  }

  /** are we in a failure state */
  public boolean failed() {
    return failed;
  }

  /** get the message about the progress */
  public String message() {
    return message;
  }

  /** the failure code of the result */
  public int code() {
    return failureCode;
  }

  public NtMaybe<T> await() {
    boolean retry = failed && failureCode == ErrorCodes.DOCUMENT_NOT_READY;
    if (!finished() || retry) {
      throw new ComputeBlockedException();
    }
    return as_maybe();
  }

  /** is the result a failure */
  public boolean finished() {
    return value != null || failed;
  }

  public NtMaybe<T> as_maybe() {
    if (this.value != null) {
      return new NtMaybe<>(value);
    } else {
      return new NtMaybe<>();
    }
  }
}
