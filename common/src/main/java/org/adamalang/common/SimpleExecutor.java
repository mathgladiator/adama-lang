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

import java.util.concurrent.*;

/** wraps Java executor for time and simplifies for Adama */
public interface SimpleExecutor {
  /** a default instance for doing things NOW */
  SimpleExecutor NOW = new SimpleExecutor() {
    @Override
    public void execute(NamedRunnable command) {
      command.run();
    }

    @Override
    public Runnable schedule(NamedRunnable command, long milliseconds) {
      return () -> {
      };
    }

    @Override
    public Runnable scheduleNano(NamedRunnable command, long nanoseconds) {
      return () -> {
      };
    }

    @Override
    public CountDownLatch shutdown() {
      return new CountDownLatch(0);
    }
  };

  // TODO: Finishing NamedRunnable instrumentation
  static SimpleExecutor create(String name) {
    ScheduledExecutorService realExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(name));
    return new SimpleExecutor() {
      @Override
      public void execute(NamedRunnable command) {
        command.bind(name);
        realExecutor.execute(command);
      }

      @Override
      public Runnable schedule(NamedRunnable command, long milliseconds) {
        command.bind(name);
        command.delay(milliseconds);
        ScheduledFuture<?> future = realExecutor.schedule(command, milliseconds, TimeUnit.MILLISECONDS);
        return () -> future.cancel(false);
      }

      @Override
      public Runnable scheduleNano(NamedRunnable command, long nanoseconds) {
        command.bind(name);
        command.delay(nanoseconds / 1000000);
        ScheduledFuture<?> future = realExecutor.schedule(command, nanoseconds, TimeUnit.NANOSECONDS);
        return () -> future.cancel(false);
      }

      @Override
      public CountDownLatch shutdown() {
        CountDownLatch latch = new CountDownLatch(1);
        realExecutor.execute(() -> {
          latch.countDown();
          realExecutor.shutdown();
        });
        return latch;
      }
    };
  }

  /** execute the given command in the executor */
  void execute(NamedRunnable command);

  /** schedule the given command to run after some milliseconds within the executor */
  Runnable schedule(NamedRunnable command, long milliseconds);

  Runnable scheduleNano(NamedRunnable command, long nanoseconds);

  /** shutdown the executor */
  CountDownLatch shutdown();
}
