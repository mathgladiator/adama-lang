/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk.demo;

import org.adamalang.common.metrics.CallbackMonitor;
import org.adamalang.common.metrics.MetricsFactory;

/** metrics for the disk operations */
public class DiskMetrics {
  public final CallbackMonitor disk_get;
  public final CallbackMonitor disk_initialize;
  public final CallbackMonitor disk_patch;
  public final CallbackMonitor disk_compute;
  public final CallbackMonitor disk_delete;
  public final CallbackMonitor disk_compact;

  public DiskMetrics(MetricsFactory factory) {
    disk_get = factory.makeCallbackMonitor("disk_get");
    disk_initialize = factory.makeCallbackMonitor("disk_initialize");
    disk_patch = factory.makeCallbackMonitor("disk_patch");
    disk_compute = factory.makeCallbackMonitor("disk_compute");
    disk_delete = factory.makeCallbackMonitor("disk_delete");
    disk_compact = factory.makeCallbackMonitor("disk_compact");
  }
}
