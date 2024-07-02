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
package org.adamalang.common.capacity;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.jvm.MachineHeat;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/** Monitors the heat on the machine and fires LoadEvents */
public class LoadMonitor {
  private final AtomicBoolean alive;
  private final Sample[] samples;
  private final ArrayList<LoadEvent> monitorCPU;
  private final ArrayList<LoadEvent> monitorMemory;
  private final SimpleExecutor executor;

  public LoadMonitor(SimpleExecutor executor, AtomicBoolean alive) {
    this.executor = executor;
    this.alive = alive;
    this.samples = new Sample[30];
    samples[0] = new Sample();
    for (int k = 1; k < samples.length; k++) {
      samples[k] = samples[0];
    }
    this.monitorCPU = new ArrayList<>();
    this.monitorMemory = new ArrayList<>();
    if (alive.get()) {
      this.executor.schedule(new NamedRunnable("load-signal") {
        int at = 0;

        @Override
        public void execute() throws Exception {
          samples[at] = new Sample();
          at++;
          at %= samples.length;
          double sum_cpu = 0;
          double sum_memory = 0;
          for (Sample sample : samples) {
            sum_cpu += sample.cpu;
            sum_memory += sample.memory;
          }
          sum_cpu /= samples.length;
          sum_memory /= samples.length;
          for (LoadEvent e : monitorCPU) {
            e.at(sum_cpu);
          }
          for (LoadEvent e : monitorMemory) {
            e.at(sum_memory);
          }
          if (alive.get()) {
            executor.schedule(this, 250);
          }
        }
      }, 10);
    }
  }

  public void cpu(LoadEvent e) {
    executor.execute(new NamedRunnable("add-cpu-load-event") {
      @Override
      public void execute() throws Exception {
        monitorCPU.add(e);
      }
    });
  }

  public void memory(LoadEvent e) {
    executor.execute(new NamedRunnable("add-cpu-load-event") {
      @Override
      public void execute() throws Exception {
        monitorMemory.add(e);
      }
    });
  }

  private class Sample {
    public final double cpu;
    public final double memory;

    public Sample() {
      this.cpu = MachineHeat.cpu();
      this.memory = MachineHeat.memory();
    }
  }
}
