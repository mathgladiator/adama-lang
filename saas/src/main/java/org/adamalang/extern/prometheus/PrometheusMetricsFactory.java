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
package org.adamalang.extern.prometheus;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.*;
import org.adamalang.ErrorTable;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

public class PrometheusMetricsFactory implements MetricsFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(PrometheusMetricsFactory.class);
  private static final double[] LATENCY_BUCKETS = new double[]{0.001D, 0.005D, 0.01D, 0.02D, 0.03D, 0.04D, 0.05D, 0.075D, 0.1D, 0.2D, 0.3D, 0.4D, 0.5D, 1.0D, 2.0D, 5.0D, 10.0D};
  private final HTTPServer server;
  private final SimpleExecutor executor;

  public PrometheusMetricsFactory(int metricsHttpPort) throws Exception {
    server = new HTTPServer.Builder().withPort(metricsHttpPort).build();
    new MemoryPoolsExports().register();
    new GarbageCollectorExports().register();
    new StandardExports().register();
    new MemoryAllocationExports().register();
    new ThreadExports().register();
    this.executor = SimpleExecutor.create("timeout");
  }

  public void shutdown() {
    executor.shutdown();
    server.close();
  }

  @Override
  public RequestResponseMonitor makeRequestResponseMonitor(String nameRaw) {
    String name = makeNameCompatibleWithPrometheus(nameRaw);
    Counter start = Counter.build().name("rr_" + name + "_start").help("Request started for " + name).register();
    Counter extra = Counter.build().name("rr_" + name + "_progress").help("Extra request data for " + name).register();
    Counter success = Counter.build().name("rr_" + name + "_success").help("Request success for " + name).register();
    Counter failure = Counter.build().name("rr_" + name + "_failure").help("Request failure for " + name).register();
    Counter user_issue = Counter.build().name("rr_" + name + "_user_issue").help("Callback user failure for " + name).register();
    Gauge inflight = Gauge.build().name("rr_" + name + "_inflight").help("Inprogress streams for " + name).register();
    Histogram firstLatency = Histogram.build().name("rr_" + name + "_latency").buckets(LATENCY_BUCKETS).help("Latency for the first bit of progress for " + name).register();
    return () -> {
      Histogram.Timer timer = firstLatency.startTimer();
      start.inc();
      inflight.inc();
      return new RequestResponseMonitor.RequestResponseMonitorInstance() {
        boolean first = true;

        @Override
        public void success() {
          time();
          success.inc();
        }

        private void time() {
          if (first) {
            timer.close();
            inflight.dec();
            first = false;
          }
        }

        @Override
        public void extra() {
          extra.inc();
        }

        @Override
        public void failure(int code) {
          time();
          if (ErrorTable.INSTANCE.isNotAProblem(code)) {
            /** the exception is not a problem as it is being used back tracking */
            success.inc();
          } else if (ErrorTable.INSTANCE.isUserProblem(code)) {
            /** this is not actionable by the platform and we consider it a success */
            success.inc();
            user_issue.inc();
          } else {
            failure.inc();
          }
        }
      };
    };
  }

  public static String makeNameCompatibleWithPrometheus(String nameRaw) {
    return nameRaw //
        .replaceAll(Pattern.quote("/"), "") //
        .replaceAll(Pattern.quote("-"), "") //
        .toLowerCase(Locale.ROOT);
  }

  @Override
  public StreamMonitor makeStreamMonitor(String nameRaw) {
    String name = makeNameCompatibleWithPrometheus(nameRaw);
    Counter start = Counter.build().name("stream_" + name + "_start").help("Stream requests started for " + name).register();
    Counter progress = Counter.build().name("stream_" + name + "_progress").help("Stream progress made for " + name).register();
    Counter finish = Counter.build().name("stream_" + name + "_finish").help("Stream finished for " + name).register();
    Counter user_issue = Counter.build().name("stream_" + name + "_user_issue").help("Stream user failure for " + name).register();
    Gauge timeout = Gauge.build().name("stream_" + name + "_timeout").help("Timeouts streams for " + name).register();
    Counter failure = Counter.build().name("stream_" + name + "_failure").help("Stream filure for " + name).register();
    Gauge inflight = Gauge.build().name("stream_" + name + "_inflight").help("Inprogress streams for " + name).register();
    Histogram firstLatency = Histogram.build().name("stream_" + name + "_first_latency").buckets(LATENCY_BUCKETS).help("Latency for the first bit of progress for " + name).register();
    return () -> {
      AtomicBoolean responded = makeTimeoutBoolean(timeout, name);
      Histogram.Timer timer = firstLatency.startTimer();
      start.inc();
      inflight.inc();
      return new StreamMonitor.StreamMonitorInstance() {
        boolean first = true;
        boolean lowered = false;

        @Override
        public void progress() {
          time();
          progress.inc();
        }

        private void time() {
          if (first) {
            timer.close();
            first = false;
            responded.set(true);
          }
        }

        @Override
        public void finish() {
          time();
          finish.inc();
          dec();
        }

        private void dec() {
          if (!lowered) {
            inflight.dec();
            lowered = true;
          }
        }

        @Override
        public void failure(int code) {
          time();
          dec();
          if (ErrorTable.INSTANCE.isNotAProblem(code)) {
            /** the exception is not a problem as it is being used back tracking */
            finish.inc();
          } else if (ErrorTable.INSTANCE.isUserProblem(code)) {
            /** this is not actionable by the platform and we consider it a success */
            finish.inc();
            user_issue.inc();
          } else {
            failure.inc();
          }
        }
      };
    };
  }

  @Override
  public CallbackMonitor makeCallbackMonitor(String name) {
    Counter start = Counter.build().name("cb_" + name + "_start").help("Callback started for " + name).register();
    Counter success = Counter.build().name("cb_" + name + "_success").help("Callback success for " + name).register();
    Counter failure = Counter.build().name("cb_" + name + "_failure").help("Callback failure for " + name).register();
    Counter user_issue = Counter.build().name("cb_" + name + "_user_issue").help("Callback user failure for " + name).register();
    Gauge timeout = Gauge.build().name("cb_" + name + "_timeout").help("Internal timeout for " + name).register();
    Gauge inflight = Gauge.build().name("cb_" + name + "_inflight").help("Inprogress callbacks for " + name).register();
    Histogram latency = Histogram.build().name("cb_" + name + "_latency").buckets(LATENCY_BUCKETS).help("Latency callback to complete " + name).register();
    return new CallbackMonitor() {
      @Override
      public CallbackMonitor.CallbackMonitorInstance start() {
        start.inc();
        inflight.inc();
        Histogram.Timer timer = latency.startTimer();
        AtomicBoolean responded = makeTimeoutBoolean(timeout, name);
        return new CallbackMonitorInstance() {
          boolean first = true;

          @Override
          public void success() {
            success.inc();
            time();
          }

          private void time() {
            if (first) {
              timer.observeDuration();
              inflight.dec();
              first = false;
              responded.set(true);
            }
          }

          @Override
          public void failure(int code) {
            if (ErrorTable.INSTANCE.isNotAProblem(code)) {
              /** the exception is not a problem as it is being used back tracking */
              success.inc();
            } else if (ErrorTable.INSTANCE.isUserProblem(code)) {
              /** this is not actionable by the platform and we consider it a success */
              success.inc();
              user_issue.inc();
            } else {
              failure.inc();
            }
            time();
          }
        };
      }
    };
  }

  @Override
  public Runnable counter(String name) {
    Counter start = Counter.build().name("raw_" + name).help("Raw counter for " + name).register();
    return () -> {
      start.inc();
    };
  }

  @Override
  public Inflight inflight(String name) {
    Gauge inflight = Gauge.build().name("inf_" + name).help("Inflight measure for " + name).register();
    return new Inflight() {
      @Override
      public void up() {
        inflight.inc();
      }

      @Override
      public void down() {
        inflight.dec();
      }

      @Override
      public void set(int value) {
        inflight.set(value);
      }
    };
  }

  @Override
  public ItemActionMonitor makeItemActionMonitor(String name) {
    Counter start = Counter.build().name("im_" + name + "_start").help("Item Monitor started for " + name).register();
    Counter executed = Counter.build().name("im_" + name + "_executed").help("Item Monitor executed for " + name).register();
    Counter rejected = Counter.build().name("im_" + name + "_rejected").help("Item Monitor rejected for " + name).register();
    Counter timeout = Counter.build().name("im_" + name + "_timeout").help("Item Monitor timeout for " + name).register();
    Gauge inflight = Gauge.build().name("im_" + name + "_inflight").help("Inprogress Item Monitors for " + name).register();
    Histogram latency = Histogram.build().name("im_" + name + "_latency").buckets(LATENCY_BUCKETS).help("Latency Item Monitor to succeed " + name).register();
    return () -> {
      start.inc();
      inflight.inc();
      Histogram.Timer timer = latency.startTimer();
      return new ItemActionMonitor.ItemActionMonitorInstance() {
        @Override
        public void executed() {
          executed.inc();
          fin();
        }

        void fin() {
          inflight.dec();
          timer.close();
        }

        @Override
        public void rejected() {
          rejected.inc();
          fin();
        }

        @Override
        public void timeout() {
          timeout.inc();
          fin();
        }
      };
    };
  }

  @Override
  public void page(String name, String title) {
  }

  @Override
  public void section(String title) {
  }

  /** register a timeout */
  private AtomicBoolean makeTimeoutBoolean(Gauge timeout, String metricName) {
    AtomicBoolean responded = new AtomicBoolean(false);
    executor.schedule(new NamedRunnable("timeout-test-callback") {
      @Override
      public void execute() throws Exception {
        if (!responded.get()) {
          LOGGER.error("timeout-" + metricName);
          timeout.inc();
        }
      }
    }, 60000);
    return responded;
  }
}
