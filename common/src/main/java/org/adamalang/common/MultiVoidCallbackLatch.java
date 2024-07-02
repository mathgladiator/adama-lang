/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.common;

/** a latch for pumping a callback */
public class MultiVoidCallbackLatch {
  private final Callback<Void> callback;
  private int remaining;
  private final int errorCode;
  private boolean failed;

  public MultiVoidCallbackLatch(Callback<Void> callback, int remaining, int errorCode) {
    this.callback = callback;
    this.remaining = remaining;
    this.errorCode = errorCode;
    this.failed = false;
  }

  private synchronized boolean atomicCuccess() {
    if (failed) {
      return false;
    }
    remaining--;
    return remaining == 0;
  }

  public void success() {
    if (atomicCuccess()) {
      callback.success(null);
    }
  }

  private synchronized boolean atomicFailure() {
    if (failed) {
      return false;
    }
    failed = true;
    return true;
  }

  public void failure() {
    if (atomicFailure()) {
      callback.failure(new ErrorCodeException(errorCode));
    }
  }
}
