package org.adamalang.runtime.sys.capacity;

import org.junit.Assert;
import org.junit.Test;

public class CapacityInstanceTests {
  @Test
  public void trivial() {
    CapacityInstance ci = new CapacityInstance("space", "region", "machine", true);
    Assert.assertEquals("space", ci.space);
    Assert.assertEquals("region", ci.region);
    Assert.assertEquals("machine", ci.machine);
    Assert.assertTrue(ci.override);
  }
}
