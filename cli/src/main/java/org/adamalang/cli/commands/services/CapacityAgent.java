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
import org.adamalang.common.capacity.LoadEvent;
import org.adamalang.common.capacity.LoadMonitor;

import java.util.concurrent.atomic.AtomicBoolean;

/** Sketch of the capacity agent */
public class CapacityAgent {
  private final LoadMonitor resources;

  public CapacityAgent(SimpleExecutor executor, AtomicBoolean alive) {
    resources = new LoadMonitor(executor, alive);
    resources.cpu(new LoadEvent(0.75) {
      @Override
      public void start() {
        // shed some 25% of documents
      }

      @Override
      public void stop() {
        // stop shedding
      }
    });
    resources.memory(new LoadEvent(0.75) {
      @Override
      public void start() {
        // shed some 25% of documents
      }

      @Override
      public void stop() {
        // stop shedding
      }
    });
    resources.cpu(new LoadEvent(0.85) {
      @Override
      public void start() {
        // reject new connects
      }

      @Override
      public void stop() {
        // allow new connects
      }
    });
    resources.memory(new LoadEvent(0.85) {
      @Override
      public void start() {
        // reject new connects
      }

      @Override
      public void stop() {
        // allow new connects
      }
    });
  }
}
