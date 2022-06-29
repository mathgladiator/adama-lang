package org.adamalang.web.service;

import org.junit.Assert;
import org.junit.Test;

public class WebToAdamaTests {
  @Test
  public void detection() {
    String xyz = WebToAdama.detectBodyAsQueryString("x=123");
    Assert.assertEquals("{\"x\":\"123\"}", xyz);
    Assert.assertNull(WebToAdama.detectBodyAsQueryString("{\"x\":123}"));
  }
}
