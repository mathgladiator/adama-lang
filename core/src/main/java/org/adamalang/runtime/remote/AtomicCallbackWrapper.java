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
package org.adamalang.runtime.remote;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

/** an attomic callback for swapping out the end path */
public class AtomicCallbackWrapper<T> implements Callback<T> {
  private Callback<T> ref;

  public AtomicCallbackWrapper(Callback<T> initial) {
    this.ref = initial;
  }

  @Override
  public synchronized void success(T value) {
    ref.success(value);
  }

  @Override
  public synchronized void failure(ErrorCodeException ex) {
    ref.failure(ex);
  }

  public synchronized Callback<T> set(Callback<T> v) {
    Callback<T> prior = this.ref;
    this.ref = v;
    return prior;
  }
}
