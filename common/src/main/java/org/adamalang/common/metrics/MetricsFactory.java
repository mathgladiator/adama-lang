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

  Inflight inflight(String name);
}
