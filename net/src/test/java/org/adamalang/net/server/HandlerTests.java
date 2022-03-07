package org.adamalang.net.server;

import org.junit.Test;

public class HandlerTests {
  @Test
  public void trivial() {
    Handler handler = new Handler(null, null);
    handler.request(-1);
    handler.create(-1);
  }
}
