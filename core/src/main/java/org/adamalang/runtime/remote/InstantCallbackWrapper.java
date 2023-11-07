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
package org.adamalang.runtime.remote;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

import java.util.function.Consumer;
import java.util.function.Supplier;

/** experimental callback wrapper to instantly detect a return value */
public class InstantCallbackWrapper<T> implements Callback<T> {
  private final Callback<T> original;
  private T value;
  private Consumer<T> action;

  public InstantCallbackWrapper(Callback<T> original) {
    this.original = original;
  }

  @Override
  public void success(T value) {
    set(value).get();
  }

  @Override
  public void failure(ErrorCodeException ex) {
    original.failure(ex);
  }

  public synchronized Supplier<Boolean> set(T value) {
    this.value = value;
    if (this.action != null) {
      return () -> { action.accept(value); return true; };
    } else {
      return () -> { return false; };
    }
  }

  public synchronized Supplier<Boolean> register(Consumer<T> action) {
    this.action = action;
    if (this.value != null) {
      return () -> { action.accept(value); return true; };
    } else {
      return () -> { return false; };
    }
  }
}
