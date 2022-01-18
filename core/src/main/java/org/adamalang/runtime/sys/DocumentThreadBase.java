/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.sys;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.runtime.contracts.DataService;
import org.adamalang.runtime.contracts.Key;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;

/**
 * This defines the state required within a thread to run a document. As Documents run in isolated
 * thread without synchronization, access to a durable living document must be access via this base.
 */
public class DocumentThreadBase {
  public final DataService service;
  public final SimpleExecutor executor;
  public final HashMap<Key, DurableLivingDocument> map;
  public final TimeSource time;
  private final HashMap<String, PredictiveInventory> inventoryBySpace;
  private int millisecondsForCleanupCheck;
  private int millisecondsAfterLoadForReconciliation;
  private final Random rng;
  private int millisecondsToPerformInventory;
  private int millisecondsToPerformInventoryJitter;

  public DocumentThreadBase(DataService service, SimpleExecutor executor, TimeSource time) {
    this.service = service;
    this.executor = executor;
    this.time = time;
    this.map = new HashMap<>();
    this.inventoryBySpace = new HashMap<>();
    this.millisecondsForCleanupCheck = 2500;
    this.millisecondsAfterLoadForReconciliation = 2500;
    this.rng = new Random();
    this.millisecondsToPerformInventory = 30000;
    this.millisecondsToPerformInventoryJitter = 15000;
  }

  public void kickOffInventory() {
    executor.execute(
        new NamedRunnable("base-inventory") {
          @Override
          public void execute() throws Exception {
            performInventory();
          }
        });
  }

  public PredictiveInventory getOrCreateInventory(String space) {
    PredictiveInventory inventory = inventoryBySpace.get(space);
    if (inventory == null) {
      inventory = new PredictiveInventory();
      inventoryBySpace.put(space, inventory);
    }
    return inventory;
  }

  public void bill(Consumer<HashMap<String, PredictiveInventory.Billing>> callback) {
    executor.execute(
        new NamedRunnable("base-billing") {
          @Override
          public void execute() throws Exception {
            HashMap<String, PredictiveInventory.Billing> result = new HashMap<>();
            for (Map.Entry<String, PredictiveInventory> entry : inventoryBySpace.entrySet()) {
              result.put(entry.getKey(), entry.getValue().toBill());
            }
            callback.accept(result);
          }
        });
  }

  public void performInventory() {
    HashMap<String, PredictiveInventory.PreciseSnapshotAccumulator> accumulators =
        new HashMap<>(inventoryBySpace.size());
    for (DurableLivingDocument document : map.values()) {
      PredictiveInventory.PreciseSnapshotAccumulator accum = accumulators.get(document.key.space);
      if (accum == null) {
        accum = new PredictiveInventory.PreciseSnapshotAccumulator();
        accumulators.put(document.key.space, accum);
      }
      accum.memory += document.getMemoryBytes();
      accum.ticks += document.getCodeCost();
      document.zeroOutCodeCost();
      accum.connections += document.getConnectionsCount();
      accum.count++;
    }
    HashMap<String, PredictiveInventory> nextInventoryBySpace = new HashMap<>();
    for (Map.Entry<String, PredictiveInventory.PreciseSnapshotAccumulator> entry :
        accumulators.entrySet()) {
      PredictiveInventory inventory = getOrCreateInventory(entry.getKey());
      inventory.accurate(entry.getValue());
      nextInventoryBySpace.put(entry.getKey(), inventory);
    }
    inventoryBySpace.clear();
    inventoryBySpace.putAll(nextInventoryBySpace);
    executor.schedule(
        new NamedRunnable("base-inventory-scheduled") {
          @Override
          public void execute() throws Exception {
            performInventory();
          }
        },
        millisecondsToPerformInventory
            + rng.nextInt(millisecondsToPerformInventoryJitter)
            + rng.nextInt(millisecondsToPerformInventoryJitter));
  }

  public int getMillisecondsForCleanupCheck() {
    return millisecondsForCleanupCheck;
  }

  public void setMillisecondsForCleanupCheck(int ms) {
    this.millisecondsForCleanupCheck = ms;
  }

  public int getMillisecondsAfterLoadForReconciliation() {
    return millisecondsAfterLoadForReconciliation;
  }

  public void setMillisecondsAfterLoadForReconciliation(int ms) {
    this.millisecondsAfterLoadForReconciliation = ms;
  }

  public void setInventoryMillisecondsSchedule(int period, int jitter) {
    this.millisecondsToPerformInventory = period;
    this.millisecondsToPerformInventoryJitter = jitter;
  }
}
