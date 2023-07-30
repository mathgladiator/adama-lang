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
  public final RequestResponseMonitor monitor_GlobalFinderDeleteMark;
  public final RequestResponseMonitor monitor_GlobalFinderDeleteCommit;
  public final RequestResponseMonitor monitor_GlobalFinderBackUp;
  public final RequestResponseMonitor monitor_GlobalFinderList;
  public final RequestResponseMonitor monitor_GlobalAuthoritiesCreate;
  public final RequestResponseMonitor monitor_GlobalAuthoritiesSet;
  public final RequestResponseMonitor monitor_GlobalAuthoritiesGetPublic;
  public final RequestResponseMonitor monitor_GlobalAuthoritiesGetProtected;
  public final RequestResponseMonitor monitor_GlobalAuthoritiesList;
  public final RequestResponseMonitor monitor_GlobalAuthoritiesDelete;

  public ApiMetrics(MetricsFactory factory) {
    this.monitor_GlobalMachineStart = factory.makeRequestResponseMonitor("global/machine/start");
    this.monitor_GlobalFinderFind = factory.makeRequestResponseMonitor("global/finder/find");
    this.monitor_GlobalFinderFindbind = factory.makeRequestResponseMonitor("global/finder/findbind");
    this.monitor_GlobalFinderFree = factory.makeRequestResponseMonitor("global/finder/free");
    this.monitor_GlobalFinderDeleteMark = factory.makeRequestResponseMonitor("global/finder/delete/mark");
    this.monitor_GlobalFinderDeleteCommit = factory.makeRequestResponseMonitor("global/finder/delete/commit");
    this.monitor_GlobalFinderBackUp = factory.makeRequestResponseMonitor("global/finder/back-up");
    this.monitor_GlobalFinderList = factory.makeRequestResponseMonitor("global/finder/list");
    this.monitor_GlobalAuthoritiesCreate = factory.makeRequestResponseMonitor("global/authorities/create");
    this.monitor_GlobalAuthoritiesSet = factory.makeRequestResponseMonitor("global/authorities/set");
    this.monitor_GlobalAuthoritiesGetPublic = factory.makeRequestResponseMonitor("global/authorities/get/public");
    this.monitor_GlobalAuthoritiesGetProtected = factory.makeRequestResponseMonitor("global/authorities/get/protected");
    this.monitor_GlobalAuthoritiesList = factory.makeRequestResponseMonitor("global/authorities/list");
    this.monitor_GlobalAuthoritiesDelete = factory.makeRequestResponseMonitor("global/authorities/delete");
  }
}
