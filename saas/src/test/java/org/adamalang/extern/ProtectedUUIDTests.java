package org.adamalang.extern;

import org.junit.Assert;
import org.junit.Test;

public class ProtectedUUIDTests {
  @Test
  public void coverage() {
    try {
      ProtectedUUID.encode(null);
    } catch (RuntimeException re) {
      Assert.assertTrue(re.getCause() instanceof NullPointerException);
    }
    ProtectedUUID.generate();
  }
}
