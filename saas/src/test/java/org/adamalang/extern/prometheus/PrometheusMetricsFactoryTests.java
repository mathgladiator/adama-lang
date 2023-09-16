/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
