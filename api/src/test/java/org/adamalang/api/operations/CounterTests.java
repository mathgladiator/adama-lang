package org.adamalang.api.operations;

import org.junit.Assert;
import org.junit.Test;

public class CounterTests {
  @Test
  public void flow() {
    Counter c = new Counter();
    c.bump();
    c.bump();
    c.bump();
    Assert.assertEquals(3, c.getAndReset());
    Assert.assertEquals(0, c.getAndReset());
    c.bump();
    Assert.assertEquals(1, c.getAndReset());
  }
}
