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
package org.adamalang.common.metrics;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

/** monitor a callback */
public abstract class CallbackMonitor {

  public <T> Callback<T> wrap(Callback<T> callback) {
    CallbackMonitorInstance instance = start();
    return new Callback<T>() {
      @Override
      public void success(T value) {
        instance.success();
        callback.success(value);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        instance.failure(ex.code);
        callback.failure(ex);
      }
    };
  }

  public abstract CallbackMonitorInstance start();

  public interface CallbackMonitorInstance {
    void success();

    void failure(int code);
  }
}
