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

/** if there are no errors possible, then this provides a simple approach */
public class NoErrorMultiCallbackLatchImpl implements Callback<Void> {
  private final Runnable finished;

  public NoErrorMultiCallbackLatchImpl(Runnable finished) {
    this.finished = finished;
  }

  @Override
  public void success(Void value) {
    finished.run();
  }

  @Override
  public void failure(ErrorCodeException ex) {
    // ignored
  }

  public static Runnable WRAP(Runnable finished, int count) {
    MultiVoidCallbackLatch latch = new MultiVoidCallbackLatch(new NoErrorMultiCallbackLatchImpl(finished), count, -1);
    return () -> latch.success();
  }
}
