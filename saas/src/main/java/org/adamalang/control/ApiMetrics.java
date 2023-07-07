/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.control;


import org.adamalang.common.metrics.*;

public class ApiMetrics {
  public final RequestResponseMonitor monitor_GlobalMachineStart;
  public final RequestResponseMonitor monitor_GlobalFinderFind;
  public final RequestResponseMonitor monitor_GlobalFinderFindbind;
  public final RequestResponseMonitor monitor_GlobalFinderFree;
  public final RequestResponseMonitor monitor_GlobalFinderDelete;
  public final RequestResponseMonitor monitor_GlobalFinderBackUp;
  public final RequestResponseMonitor monitor_GlobalFinderList;

  public ApiMetrics(MetricsFactory factory) {
    this.monitor_GlobalMachineStart = factory.makeRequestResponseMonitor("global/machine/start");
    this.monitor_GlobalFinderFind = factory.makeRequestResponseMonitor("global/finder/find");
    this.monitor_GlobalFinderFindbind = factory.makeRequestResponseMonitor("global/finder/findbind");
    this.monitor_GlobalFinderFree = factory.makeRequestResponseMonitor("global/finder/free");
    this.monitor_GlobalFinderDelete = factory.makeRequestResponseMonitor("global/finder/delete");
    this.monitor_GlobalFinderBackUp = factory.makeRequestResponseMonitor("global/finder/back-up");
    this.monitor_GlobalFinderList = factory.makeRequestResponseMonitor("global/finder/list");
  }
}
