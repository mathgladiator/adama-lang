package org.adamalang.web.contracts;

import org.junit.Assert;
import org.junit.Test;

public class WellKnownHandlerTests {
  @Test
  public void flow() {
    Assert.assertFalse(WellKnownHandler.DONT_HANDLE("/"));
    Assert.assertTrue(WellKnownHandler.DONT_HANDLE("/.well-known/assetlinks.json"));
  }
}
