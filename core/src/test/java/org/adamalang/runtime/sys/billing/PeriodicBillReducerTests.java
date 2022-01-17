/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.sys.billing;

import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.sys.PredictiveInventory;
import org.junit.Assert;
import org.junit.Test;

public class PeriodicBillReducerTests {
  @Test
  public void flow() {
    MockTime time = new MockTime();
    time.set(42);
    PeriodicBillReducer reducer = new PeriodicBillReducer(time);
    Assert.assertEquals("{\"time\":\"42\",\"spaces\":{}}", reducer.toJson());
    reducer.next(new Bill(1, 120, "space", "hash", new PredictiveInventory.Billing(100, 1000, 10, 200)));
    reducer.next(new Bill(1, 120, "mush", "hash", new PredictiveInventory.Billing(100, 1000, 10, 200)));
    reducer.next(new Bill(1, 120, "yo", "hash", new PredictiveInventory.Billing(0, 0, 0, 0)));
    Assert.assertEquals(
        "{\"time\":\"42\",\"spaces\":{\"mush\":{\"cpu\":\"1000\",\"messages\":\"200\",\"count_p95\":\"10\",\"memory_p95\":\"100\"},\"space\":{\"cpu\":\"1000\",\"messages\":\"200\",\"count_p95\":\"10\",\"memory_p95\":\"100\"}}}",
        reducer.toJson());
  }
}
