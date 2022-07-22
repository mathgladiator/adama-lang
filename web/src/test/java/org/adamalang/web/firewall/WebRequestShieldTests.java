package org.adamalang.web.firewall;

import org.junit.Assert;
import org.junit.Test;

public class WebRequestShieldTests {
  @Test
  public void coverage() {
    Assert.assertTrue(WebRequestShield.block("/.git/"));
    Assert.assertTrue(WebRequestShield.block("/CSS/"));
    Assert.assertTrue(WebRequestShield.block("/Portal/"));
    Assert.assertTrue(WebRequestShield.block("/actuator/"));
    Assert.assertTrue(WebRequestShield.block("/api/"));
    Assert.assertTrue(WebRequestShield.block("/cgi-bin/"));
    Assert.assertTrue(WebRequestShield.block("/docs/"));
    Assert.assertTrue(WebRequestShield.block("/ecp/"));
    Assert.assertTrue(WebRequestShield.block("/owa/"));
    Assert.assertTrue(WebRequestShield.block("/scripts/"));
    Assert.assertTrue(WebRequestShield.block("/vendor/"));
    Assert.assertFalse(WebRequestShield.block("/my/name/is/ninja/"));
  }
}
