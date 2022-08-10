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
