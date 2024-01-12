package org.adamalang.devbox;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.*;

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

  private void time(String name, long started, AtomicBoolean finished) {
    finished.set(true);
    long delta = System.currentTimeMillis() - started;
    if (delta > 10000) {
      println.accept(" [very slow] " + name + " took: " + delta + "ms");
    } else if (delta > 1500) {
      println.accept(" [slow] " + name + " took: " + delta + "ms");
    }
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

  @Override
  public CallbackMonitor makeCallbackMonitor(String name) {
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

  @Override
  public RequestResponseMonitor makeRequestResponseMonitor(String name) {
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
}
