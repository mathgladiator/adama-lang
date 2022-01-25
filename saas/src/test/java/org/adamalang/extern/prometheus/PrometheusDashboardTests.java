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
    pd.finish(File.createTempFile("Tempy", "tampy").getParentFile());
  }
}
