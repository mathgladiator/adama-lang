/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.sys;

import org.junit.Assert;
import org.junit.Test;

public class PredictiveInventoryTests {
  @Test
  public void flow() {
    PredictiveInventory inventory = new PredictiveInventory();
    inventory.grow();
    PredictiveInventory.PreciseSnapshotAccumulator snapshot = new PredictiveInventory.PreciseSnapshotAccumulator();
    snapshot.count = 1;
    snapshot.memory = 1000;
    snapshot.ticks = 10000;
    inventory.accurate(snapshot);
    inventory.grow();
    inventory.message();
    inventory.message();
    inventory.message();
    PredictiveInventory.MeteringSample meteringSample = inventory.sample();
    Assert.assertEquals(2, meteringSample.count);
    Assert.assertEquals(2000, meteringSample.memory);
    Assert.assertEquals(20000, meteringSample.cpu);
    Assert.assertEquals(3, meteringSample.messages);
    snapshot.count = 2;
    snapshot.memory = 2000;
    snapshot.ticks = 20000;
    inventory.accurate(snapshot);
    inventory.grow();
    inventory.grow();
    inventory.message();
    inventory.message();
    meteringSample = inventory.sample();
    Assert.assertEquals(4, meteringSample.count);
    Assert.assertEquals(4000, meteringSample.memory);
    Assert.assertEquals(40000, meteringSample.cpu);
    Assert.assertEquals(2, meteringSample.messages);
    snapshot.count = 10;
    snapshot.memory = 5000;
    snapshot.ticks = 60000;
    inventory.accurate(snapshot);
    inventory.grow();
    inventory.grow();
    inventory.message();
    inventory.message();
    inventory.message();
    inventory.message();
    inventory.message();
    meteringSample = inventory.sample();
    Assert.assertEquals(12, meteringSample.count);
    Assert.assertEquals(6230, meteringSample.memory);
    Assert.assertEquals(73846, meteringSample.cpu);
    Assert.assertEquals(5, meteringSample.messages);
  }

  @Test
  public void add() {
    PredictiveInventory.MeteringSample a = new PredictiveInventory.MeteringSample(100, 1000, 5, 100, 41, 123, 456, 789);
    PredictiveInventory.MeteringSample b = new PredictiveInventory.MeteringSample(1100, 21000, 51, 1100, 91, 123, 456, 789);
    PredictiveInventory.MeteringSample s = PredictiveInventory.MeteringSample.add(a, b);
    Assert.assertEquals(1200, s.memory);
    Assert.assertEquals(22000, s.cpu);
    Assert.assertEquals(56, s.count);
    Assert.assertEquals(1200, s.messages);
    Assert.assertEquals(132, s.connections);
  }
}
