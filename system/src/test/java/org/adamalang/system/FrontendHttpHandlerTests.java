package org.adamalang.system;

import org.junit.Assert;
import org.junit.Test;

import java.util.TreeMap;

public class FrontendHttpHandlerTests {
  @Test
  public void host2captured() {
    TreeMap<String, String> captured = FrontendHttpHandler.prepareCapture("www.a.com");
    Assert.assertEquals("www.a.com", captured.get("$host"));
    Assert.assertEquals("a.com", captured.get("$host.apex"));
    Assert.assertEquals("www", captured.get("$host.sub"));
  }
}
