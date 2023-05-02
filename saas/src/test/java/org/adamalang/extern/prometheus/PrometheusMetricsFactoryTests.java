/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.extern.prometheus;

import org.adamalang.common.metrics.*;
import org.junit.Test;

import java.io.File;

public class PrometheusMetricsFactoryTests {
  @Test
  public void coverage() throws Exception {
    PrometheusMetricsFactory pmf = new PrometheusMetricsFactory(9099);
    try {
      pmf.page("x", "X");
      pmf.section("title");
      pmf.counter("c").run();
      Inflight inf = pmf.inflight("inf");
      inf.up();
      inf.set(1234);
      inf.down();
      CallbackMonitor cbm = pmf.makeCallbackMonitor("cbm");
      CallbackMonitor.CallbackMonitorInstance cbm_i1 = cbm.start();
      cbm_i1.success();
      CallbackMonitor.CallbackMonitorInstance cbm_i2 = cbm.start();
      cbm_i2.failure(-1);
      ItemActionMonitor iam = pmf.makeItemActionMonitor("iam");
      ItemActionMonitor.ItemActionMonitorInstance iam_i1 = iam.start();
      iam_i1.executed();
      ItemActionMonitor.ItemActionMonitorInstance iam_i2 = iam.start();
      iam_i2.rejected();
      ItemActionMonitor.ItemActionMonitorInstance iam_i3 = iam.start();
      iam_i3.timeout();
      pmf.page("y", "Y");
      RequestResponseMonitor rrm =  pmf.makeRequestResponseMonitor("rrm");
      RequestResponseMonitor.RequestResponseMonitorInstance rrm_i1 = rrm.start();
      rrm_i1.success();
      rrm_i1.extra();
      RequestResponseMonitor.RequestResponseMonitorInstance rrm_i2 = rrm.start();
      rrm_i2.failure(123);
      StreamMonitor sm = pmf.makeStreamMonitor("sm");
      StreamMonitor.StreamMonitorInstance sm_i1 = sm.start();
      sm_i1.progress();
      sm_i1.finish();
      StreamMonitor.StreamMonitorInstance sm_i2 = sm.start();
      sm_i2.failure(-1);
    } finally {
      pmf.shutdown();
    }
  }
}
