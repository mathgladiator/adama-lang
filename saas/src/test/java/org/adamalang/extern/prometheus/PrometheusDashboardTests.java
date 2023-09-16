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

import org.junit.Test;

import java.io.File;

public class PrometheusDashboardTests {
  @Test
  public void coverage() throws Exception {
    PrometheusDashboard pd = new PrometheusDashboard();
    pd.page("x", "X");
    pd.section("title");
    pd.counter("c");
    pd.inflight("inf");
    pd.makeCallbackMonitor("cb");
    pd.makeItemActionMonitor("iam");
    pd.page("y", "Y");
    pd.makeRequestResponseMonitor("rrm");
    pd.makeStreamMonitor("sm");
    File parentTemp = File.createTempFile("ADAMATEST_", "tampy").getParentFile();
    File prometh = new File(parentTemp, "promethesus-" + System.currentTimeMillis());
    try {
      prometh.mkdir();
      pd.finish(prometh);
    } finally {
      for (File file : prometh.listFiles()) {
        file.delete();
      }
      prometh.delete();
    }
  }
}
