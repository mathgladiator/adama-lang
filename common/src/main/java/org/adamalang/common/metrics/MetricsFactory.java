/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.metrics;

/**
 * the front-door for all metrics; all metrics are known in the first few ms of the process starting
 * up
 */
public interface MetricsFactory {
  /** produce a monitor for request response style operations */
  RequestResponseMonitor makeRequestResponseMonitor(String name);

  /** produce a monitor for a stream operation */
  StreamMonitor makeStreamMonitor(String name);

  /** produce a monitor for a callback */
  CallbackMonitor makeCallbackMonitor(String name);

  /** produce a counter */
  Runnable counter(String name);

  /** produce an inflight measurement */
  Inflight inflight(String name);

  /** produce a monitor for an item action queue */
  ItemActionMonitor makeItemActionMonitor(String name);

  /** kick of a dashboard page */
  void page(String name, String title);

  /** within a page group metrics under a section */
  void section(String title);
}
