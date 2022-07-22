/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
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

    Assert.assertTrue(WebRequestShield.block("/d/"));
    Assert.assertTrue(WebRequestShield.block("/portal/"));
    Assert.assertTrue(WebRequestShield.block("/remote/"));
    Assert.assertTrue(WebRequestShield.block("/.aws/"));
  }
}
