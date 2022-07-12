/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.natives;

/** a result for an async operation */
public class NtResult<T> {
  private T value;
  private boolean failed;

  public NtResult(final T value, boolean failed) {
    this.value = value;
    this.failed = failed;
  }

  /** get the value; note; this returns null and is not appropriate for the runtime */
  public T get() {
    return this.value;
  }

  /** is it available */
  public boolean has() {
    return value != null;
  }

  public boolean failed() {
    return failed;
  }
}
