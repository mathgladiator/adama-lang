/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
