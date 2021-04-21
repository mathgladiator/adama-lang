package org.adamalang.api.operations;

import org.junit.Assert;
import org.junit.Test;

public class TotalTests {
  @Test
  public void flow() {
    Total t = new Total();
    t.inc();
    t.inc();
    t.inc();
    Assert.assertEquals(3, t.get());
    t.dec();
    Assert.assertEquals(2, t.get());
    t.set(42);
    Assert.assertEquals(42, t.get());
  }
}
