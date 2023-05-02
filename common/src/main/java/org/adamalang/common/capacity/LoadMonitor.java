/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.capacity;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.jvm.MachineHeat;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/** Monitors the heat on the machine and fires LoadEvents */
public class LoadMonitor {
  private SimpleExecutor executor;
  private final AtomicBoolean alive;
  private final Sample[] samples;
  private final ArrayList<LoadEvent> monitorCPU;
  private final ArrayList<LoadEvent> monitorMemory;

  private class Sample {
    public final double cpu;
    public final double memory;

    public Sample() {
      this.cpu = MachineHeat.cpu();
      this.memory = MachineHeat.memory();
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
