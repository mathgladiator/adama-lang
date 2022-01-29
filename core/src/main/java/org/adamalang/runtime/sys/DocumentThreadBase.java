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

import org.adamalang.common.Callback;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.TimeSource;
import org.adamalang.runtime.data.DataService;
import org.adamalang.runtime.data.Key;

import java.util.*;
import java.util.function.Consumer;

/**
 * This defines the state required within a thread to run a document. As Documents run in isolated
 * thread without synchronization, access to a durable living document must be access via this base.
 */
public class DocumentThreadBase {
  public final DataService service;
  public final CoreMetrics metrics;
  public final SimpleExecutor executor;
  public final HashMap<Key, DurableLivingDocument> map;
  public final HashMap<Key, ArrayList<Callback<DurableLivingDocument>>> mapInsertsInflight;
  public final TimeSource time;
  private final HashMap<String, PredictiveInventory> inventoryBySpace;
  private final Random rng;
  private int millisecondsForCleanupCheck;
  private int millisecondsAfterLoadForReconciliation;
  private int millisecondsToPerformInventory;
  private int millisecondsToPerformInventoryJitter;

  public DocumentThreadBase(DataService service, CoreMetrics metrics, SimpleExecutor executor, TimeSource time) {
    this.service = service;
    this.metrics = metrics;
    this.executor = executor;
    this.time = time;
    this.map = new HashMap<>();
    this.mapInsertsInflight = new HashMap<>();
    this.inventoryBySpace = new HashMap<>();
    this.millisecondsForCleanupCheck = 2500;
    this.millisecondsAfterLoadForReconciliation = 2500;
    this.rng = new Random();
    this.millisecondsToPerformInventory = 30000;
    this.millisecondsToPerformInventoryJitter = 15000;
  }

  public void kickOffInventory() {
    executor.schedule(new NamedRunnable("base-inventory") {
      @Override
      public void execute() throws Exception {
        performInventory();
      }
    }, 2500);
  }

  public PredictiveInventory getOrCreateInventory(String space) {
    PredictiveInventory inventory = inventoryBySpace.get(space);
    if (inventory == null) {
      inventory = new PredictiveInventory();
      inventoryBySpace.put(space, inventory);
    }
    return inventory;
  }

  public void sampleMetering(Consumer<HashMap<String, PredictiveInventory.MeteringSample>> callback) {
    executor.execute(new NamedRunnable("base-meter-sampling") {
      @Override
      public void execute() throws Exception {
        HashMap<String, PredictiveInventory.MeteringSample> result = new HashMap<>();
        for (Map.Entry<String, PredictiveInventory> entry : inventoryBySpace.entrySet()) {
          result.put(entry.getKey(), entry.getValue().sample());
        }
        callback.accept(result);
      }
    });
  }

  public void performInventory() {
    HashMap<String, PredictiveInventory.PreciseSnapshotAccumulator> accumulators = new HashMap<>(inventoryBySpace.size());
    Iterator<Map.Entry<Key, DurableLivingDocument>> it = map.entrySet().iterator();
    while (it.hasNext()) {
      DurableLivingDocument document = it.next().getValue();
      document.triggerExpire();
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
    for (Map.Entry<String, PredictiveInventory.PreciseSnapshotAccumulator> entry : accumulators.entrySet()) {
      PredictiveInventory inventory = getOrCreateInventory(entry.getKey());
      inventory.accurate(entry.getValue());
      nextInventoryBySpace.put(entry.getKey(), inventory);
    }
    inventoryBySpace.clear();
    inventoryBySpace.putAll(nextInventoryBySpace);
    executor.schedule(new NamedRunnable("base-inventory-scheduled") {
      @Override
      public void execute() throws Exception {
        performInventory();
      }
    }, millisecondsToPerformInventory + rng.nextInt(millisecondsToPerformInventoryJitter) + rng.nextInt(millisecondsToPerformInventoryJitter));
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
