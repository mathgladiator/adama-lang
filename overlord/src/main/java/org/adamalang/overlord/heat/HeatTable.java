/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.overlord.heat;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.overlord.html.ConcurrentCachedHttpHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class HeatTable {

  private final SimpleExecutor executor;
  private final HashMap<String, HeatSample> samples;
  private Consumer<String> targetHot;
  public HeatTable(ConcurrentCachedHttpHandler handler) {
    this.executor = SimpleExecutor.create("heat-table");
    this.samples = new HashMap<>();
    this.targetHot = null;
    this.executor.schedule(new NamedRunnable("summarize") {
      @Override
      public void execute() throws Exception {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>HEAT</title></head><body>\n");
        html.append("<h1>Heat by target</h1>");
        html.append("<table><tr><th>Target</th><th>CPU</th><th>Memory</th><th>ms ago</th></tr>");
        long now = System.currentTimeMillis();
        for (Map.Entry<String, HeatSample> entry : samples.entrySet()) {
          long ago = now - entry.getValue().time;
          html.append("<tr><td>").append(entry.getKey()).append("</td><td>").append(entry.getValue().cpu).append("</td><td>").append(entry.getValue().memory).append("</td><td>").append(ago).append("</td></tr>");
        }
        html.append("</table></body></table>");
        handler.put("/heat", html.toString());
        executor.schedule(this, 250);
      }
    }, 250);
  }

  public void setHeatWarning(Consumer<String> targetHot) {
    executor.execute(new NamedRunnable("set-heat-warning") {
      @Override
      public void execute() throws Exception {
        HeatTable.this.targetHot = targetHot;
      }
    });
  }

  public void onSample(String target,  double cpu, double memory) {
    executor.execute(new NamedRunnable("got-heat-sample") {
      @Override
      public void execute() throws Exception {
        HeatSample sample = samples.get(target);
        if (sample != null) {
          sample.update(cpu, memory);
        } else {
          samples.put(target, new HeatSample(cpu, memory));
        }
      }
    });
  }

  private class HeatSample {
    double cpu;
    double memory;
    long time;

    public HeatSample(double cpu, double memory) {
      this.cpu = cpu;
      this.memory = memory;
      this.time = System.currentTimeMillis();
    }

    public void update(double cpu, double memory) {
      this.cpu = cpu;
      this.memory = memory;
      this.time = System.currentTimeMillis();
    }
  }
}
