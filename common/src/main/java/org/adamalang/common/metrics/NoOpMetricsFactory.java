/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.common.metrics;

public class NoOpMetricsFactory implements MetricsFactory {
  @Override
  public RequestResponseMonitor makeRequestResponseMonitor(String name) {
    return new RequestResponseMonitor() {
      @Override
      public RequestResponseMonitorInstance start() {
        return new RequestResponseMonitorInstance() {
          @Override
          public void success() {

          }

          @Override
          public void extra() {

          }

          @Override
          public void failure(int code) {

          }
        };
      }
    };
  }

  @Override
  public StreamMonitor makeStreamMonitor(String name) {
    return new StreamMonitor() {
      @Override
      public StreamMonitorInstance start() {
        return new StreamMonitorInstance() {
          @Override
          public void progress() {

          }

          @Override
          public void finish() {

          }

          @Override
          public void failure(int code) {

          }
        };
      }
    };
  }

  @Override
  public CallbackMonitor makeCallbackMonitor(String name) {
    return new CallbackMonitor() {
      @Override
      public CallbackMonitorInstance start() {
        return new CallbackMonitorInstance() {
          @Override
          public void success() {

          }

          @Override
          public void failure(int code) {

          }
        };
      }
    };
  }

  @Override
  public Runnable counter(String name) {
    return () -> {
    };
  }

  @Override
  public Inflight inflight(String name) {
    return new Inflight() {
      @Override
      public void up() {

      }

      @Override
      public void down() {

      }
    };
  }

  @Override
  public ItemActionMonitor makeItemActionMonitor(String name) {
    return new ItemActionMonitor() {
      @Override
      public ItemActionMonitorInstance start() {
        return new ItemActionMonitorInstance() {
          @Override
          public void executed() {

          }

          @Override
          public void rejected() {

          }

          @Override
          public void timeout() {

          }
        };
      }
    };
  }
}
