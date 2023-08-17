/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
            executor.schedule(this, 1000);
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
