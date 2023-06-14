package org.adamalang.web.service;

import org.junit.Test;

public class BundleJavaScriptTests {
  @Test
  public void execute() throws Exception {
    BundleJavaScript.bundle("../release/libadama.js");
  }
}
