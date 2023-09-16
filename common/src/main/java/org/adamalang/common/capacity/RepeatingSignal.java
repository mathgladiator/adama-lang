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
package org.adamalang.common.capacity;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;

import java.util.concurrent.atomic.AtomicBoolean;

/** repeat the last signal given every period */
public class RepeatingSignal implements BoolConsumer {
  private final SimpleExecutor executor;
  private final AtomicBoolean alive;
  private final int freq_ms;
  private final BoolConsumer event;
  private final AtomicBoolean valueToUse;
  private long lastSent;

  public RepeatingSignal(SimpleExecutor executor, AtomicBoolean alive, int freq_ms, BoolConsumer event) {
    this.executor = executor;
    this.alive = alive;
    this.event = event;
    this.freq_ms = freq_ms;
    this.valueToUse = new AtomicBoolean(false);
    this.executor.schedule(new NamedRunnable("repeat-signal") {
      @Override
      public void execute() throws Exception {
        if (alive.get()) {
          if ((System.currentTimeMillis() - lastSent) + 1 >= freq_ms) {
            event.accept(valueToUse.get());
          }
          executor.schedule(this, freq_ms);
          lastSent = System.currentTimeMillis();
        }
      }
    }, 1);
  }

  @Override
  public void accept(final boolean value) {
    valueToUse.set(value);
    this.executor.execute(new NamedRunnable("send-now") {
      @Override
      public void execute() throws Exception {
        event.accept(valueToUse.get());
        lastSent = System.currentTimeMillis();
      }
    });
  }
}
