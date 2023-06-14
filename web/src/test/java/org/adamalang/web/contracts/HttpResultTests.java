package org.adamalang.web.contracts;

import org.adamalang.runtime.natives.NtAsset;
import org.junit.Test;

public class HttpResultTests {
  @Test
  public void trivial() {
    HttpHandler.HttpResult a = new HttpHandler.HttpResult("", null, true);
    HttpHandler.HttpResult b = new HttpHandler.HttpResult("space", "key", NtAsset.NOTHING, true);
  }
}
