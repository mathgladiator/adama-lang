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
package org.adamalang.web.io;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;

import java.util.function.Supplier;

/**
 * This is a latch which has N outstanding tasks. Once all the tasks are complete, the supplier is
 * called, and the latch fires the callback. Any failures result in a failure of the lowest code.
 */
public class BulkLatch<T> {
  public final SimpleExecutor executor;
  public final Callback<T> callback;

  public Supplier<T> supply;
  public int outstanding;
  private Integer errorCode;

  public BulkLatch(SimpleExecutor executor, int outstanding, Callback<T> callback) {
    this.executor = executor;
    this.outstanding = outstanding;
    this.callback = callback;
    this.supply = null;
    this.errorCode = null;
  }

  /**
   * we have a split constructor since the latch is going to be defined prior to a complete
   * supplier. The LatchRefCallback is used before the with() call
   */
  public void with(Supplier<T> supply) {
    this.supply = supply;
  }

  /**
   * a service completed either successfully (newErrorCode == null) or not (newErrorCode != null)
   */
  public void countdown(Integer newErrorCode) {
    executor.execute(new NamedRunnable("bulk-latch") {
      @Override
      public void execute() throws Exception {
        // something bad happened
        if (newErrorCode != null) {
          // absorb the error code
          if (errorCode == null) {
            errorCode = newErrorCode;
          } else {
            // if conflicts, pick the smallest error code
            if (newErrorCode < errorCode) {
              errorCode = newErrorCode;
            }
          }
        }
        outstanding--;
        if (outstanding == 0) {
          if (errorCode == null) {
            T value = supply.get();
            callback.success(value);
          } else {
            callback.failure(new ErrorCodeException(errorCode));
          }
        }
      }
    });
  }
}
