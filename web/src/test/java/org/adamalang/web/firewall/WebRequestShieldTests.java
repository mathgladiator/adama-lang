/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.web.firewall;

import org.junit.Assert;
import org.junit.Test;

public class WebRequestShieldTests {
  @Test
  public void coverage() {
    Assert.assertFalse(WebRequestShield.block("/.well-known"));
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
