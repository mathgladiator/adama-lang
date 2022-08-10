/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.commands.services;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.capacity.BinaryEventOrGate;
import org.adamalang.common.capacity.LoadEvent;
import org.adamalang.common.capacity.LoadMonitor;
import org.adamalang.common.capacity.RepeatingSignal;

import java.util.concurrent.atomic.AtomicBoolean;

/** Sketch of the capacity agent */
public class CapacityAgent {
  private final LoadMonitor resources;

  public CapacityAgent(SimpleExecutor executor, AtomicBoolean alive) {
    resources = new LoadMonitor(executor, alive);

    BinaryEventOrGate add_capacity = new BinaryEventOrGate(new RepeatingSignal(executor, alive, 120000, (b) -> {
      // bring capacity online
    }));
    BinaryEventOrGate rejectMinor = new BinaryEventOrGate((b) -> {
      // reject 50% of new connections
    });
    BinaryEventOrGate rejectMajor = new BinaryEventOrGate((b) -> {
      // reject 100% of new connections
    });
    BinaryEventOrGate rejectHard = new BinaryEventOrGate((b) -> {
      // reject 100% of all requests
    });

    resources.cpu(new LoadEvent(0.70, add_capacity::a));
    resources.memory(new LoadEvent(0.70, add_capacity::b));
    resources.cpu(new LoadEvent(0.80, rejectMinor::a));
    resources.memory(new LoadEvent(0.80, rejectMinor::b));
    resources.cpu(new LoadEvent(0.85, rejectMajor::a));
    resources.memory(new LoadEvent(0.85, rejectMajor::b));
    resources.cpu(new LoadEvent(0.90, rejectHard::a));
    resources.memory(new LoadEvent(0.90, rejectHard::b));
  }
}
