/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
