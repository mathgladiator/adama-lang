package org.adamalang.runtime.sys.billing;

import org.adamalang.runtime.sys.PredictiveInventory;
import org.junit.Assert;
import org.junit.Test;

public class BillTests {
  @Test
  public void flow() {
    Bill bill = new Bill("space", "hash", new PredictiveInventory.Billing(100, 200, 42, 1000));
    Assert.assertEquals("space", bill.space);
    Assert.assertEquals("hash", bill.hash);
    Assert.assertEquals(100, bill.memory);
    Assert.assertEquals(200, bill.cpu);
    Assert.assertEquals(42, bill.count);
    Assert.assertEquals(1000, bill.messages);
  }
}
