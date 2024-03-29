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
package org.adamalang.common;

import java.util.function.Consumer;

public class TimeMachine implements TimeSource {
  private final Consumer<String> log;
  private final TimeSource proxy;
  private final SimpleExecutor executor;
  private final Runnable forceUpdate;
  private long shift;

  public TimeMachine(TimeSource proxy, SimpleExecutor executor, Runnable forceUpdate, Consumer<String> log) {
    this.proxy = proxy;
    this.executor = executor;
    this.forceUpdate = forceUpdate;
    this.shift = 0;
    this.log = log;
  }

  @Override
  public long nowMilliseconds() {
    return proxy.nowMilliseconds() + shift;
  }

  public void add(long deltaMilliseconds, int seconds) {
    int ticks = seconds * 10;
    AddStateMachine asm = new AddStateMachine(ticks, 100, deltaMilliseconds / ticks);
    executor.execute(asm);
  }

  public void reset(Runnable done) {
    executor.execute(new ResetStateMachine(done));
  }

  private class ResetStateMachine extends NamedRunnable {
    private int ticksLeft;
    private long delta;
    private Runnable done;

    public ResetStateMachine(Runnable done) {
      super("reset-time-machine");
      this.ticksLeft = 10;
      this.delta = -shift / 10;
      this.done = done;
    }

    @Override
    public void execute() throws Exception {
      shift += delta;
      ticksLeft--;
      if (ticksLeft > 0) {
        forceUpdate.run();
        log.accept("shift[" + delta + "]");
        executor.schedule(this,  100);
      } else {
        log.accept("reset complete");
        shift = 0;
        forceUpdate.run();
        done.run();
      }
    }
  }

  private class AddStateMachine extends NamedRunnable {
    private int ticksLeft;
    private final int msPerTick;
    private final long addPerTick;

    public AddStateMachine(int ticksLeft, int msPerTick, long addPerTick) {
      super("time-machine-go");
      this.ticksLeft = ticksLeft;
      this.msPerTick = msPerTick;
      this.addPerTick = addPerTick;
    }

    @Override
    public void execute() throws Exception {
      shift += addPerTick;
      ticksLeft--;
      if (ticksLeft > 0 && addPerTick > 0) {
        log.accept("shift[" + addPerTick + "]");
        executor.schedule(this, msPerTick);
      } else {
        log.accept("done");
      }
      forceUpdate.run();
    }
  }
}
