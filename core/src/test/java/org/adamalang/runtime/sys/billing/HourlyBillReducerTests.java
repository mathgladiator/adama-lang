package org.adamalang.runtime.sys.billing;

import org.adamalang.runtime.sys.PredictiveInventory;
import org.junit.Assert;
import org.junit.Test;

public class HourlyBillReducerTests {
  @Test
  public void flow() {
    HourlyBillReducer reducer = new HourlyBillReducer();
    Assert.assertEquals("{}", reducer.toJson());
    reducer.next(new Bill(1, 120, "space", "hash", new PredictiveInventory.Billing(100, 1000, 10, 200)));
    reducer.next(new Bill(1, 120, "mush", "hash", new PredictiveInventory.Billing(100, 1000, 10, 200)));
    Assert.assertEquals(
        "{\"mush\":{\"cpu\":\"1000\",\"messages\":\"200\",\"count_p95\":\"10\",\"memory_p95\":\"100\"},\"space\":{\"cpu\":\"1000\",\"messages\":\"200\",\"count_p95\":\"10\",\"memory_p95\":\"100\"}}",
        reducer.toJson());
  }
}
