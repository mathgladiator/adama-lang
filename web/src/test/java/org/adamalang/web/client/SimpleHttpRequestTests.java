package org.adamalang.web.client;

import org.junit.Test;

import java.util.HashMap;

public class SimpleHttpRequestTests {
  @Test
  public void coverage() {
    SimpleHttpRequest request = new SimpleHttpRequest("GET", "url", new HashMap<>(), SimpleHttpRequestBody.EMPTY);
  }
}
