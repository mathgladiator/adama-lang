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
package org.adamalang.devbox;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.CallbackMonitor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.common.metrics.RequestResponseMonitor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/** this provides performance feedback in the entire system */
public class DevBoxMetricsFactory extends NoOpMetricsFactory {
  private final SimpleExecutor timeout;
  private final Consumer<String> println;

  public DevBoxMetricsFactory(Consumer<String> println) {
    this.timeout = SimpleExecutor.create("timeouts");
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        timeout.shutdown();
      }
    }));
    this.println = println;
  }

  @Override
  public RequestResponseMonitor makeRequestResponseMonitor(String name) {
    if (ignore(name)) {
      return super.makeRequestResponseMonitor(name);
    }
    return () -> {
      long started = System.currentTimeMillis();
      AtomicBoolean finished = create(name);
      return new RequestResponseMonitor.RequestResponseMonitorInstance() {
        @Override
        public void success() {
          time(name, started, finished);
        }

        @Override
        public void extra() {
        }

        @Override
        public void failure(int code) {
          time(name, started, finished);
        }
      };
    };
  }

  @Override
  public CallbackMonitor makeCallbackMonitor(String name) {
    if (ignore(name)) {
      return super.makeCallbackMonitor(name);
    }
    return new CallbackMonitor() {
      @Override
      public CallbackMonitorInstance start() {
        long started = System.currentTimeMillis();
        AtomicBoolean finished = create(name);
        return new CallbackMonitorInstance() {
          @Override
          public void success() {
            time(name, started, finished);
          }

          @Override
          public void failure(int code) {
            time(name, started, finished);
          }
        };
      }
    };
  }

  private boolean ignore(String name) {
    if ("adamasync_connected".equals(name)) {
      return true;
    }
    return false;
  }

  private AtomicBoolean create(String name) {
    AtomicBoolean finished = new AtomicBoolean(false);
    timeout.schedule(new NamedRunnable("timeout") {
      @Override
      public void execute() throws Exception {
        if (!finished.get()) {
          println.accept(" [!!timeout!!] " + name);
        }
      }
    }, 60000);
    return finished;
  }

  private void time(String name, long started, AtomicBoolean finished) {
    finished.set(true);
    long delta = System.currentTimeMillis() - started;
    if (delta > 10000) {
      println.accept(" [very slow] " + name + " took: " + delta + "ms");
    } else if (delta > 1500) {
      println.accept(" [slow] " + name + " took: " + delta + "ms");
    }
  }
}
