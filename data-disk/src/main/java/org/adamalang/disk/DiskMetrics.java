package org.adamalang.disk;

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
