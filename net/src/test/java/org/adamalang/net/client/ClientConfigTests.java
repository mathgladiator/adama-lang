package org.adamalang.net.client;

import org.junit.Assert;
import org.junit.Test;

public class ClientConfigTests {
  @Test
  public void coverage() {
    ClientConfig config = new ClientConfig();
    Assert.assertEquals(1024, config.getClientQueueSize());
  }
}
