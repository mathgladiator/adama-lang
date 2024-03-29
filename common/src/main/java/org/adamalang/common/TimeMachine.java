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

public class TimeMachine implements TimeSource {
  private final TimeSource proxy;
  private final SimpleExecutor executor;
  private final Runnable forceUpdate;
  private long shift;

  public TimeMachine(TimeSource proxy, SimpleExecutor executor, Runnable forceUpdate) {
    this.proxy = proxy;
    this.executor = executor;
    this.forceUpdate = forceUpdate;
    this.shift = 0;
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
      if (ticksLeft > 0) {
        executor.schedule(this, msPerTick);
      }
      forceUpdate.run();
    }
  }
}
