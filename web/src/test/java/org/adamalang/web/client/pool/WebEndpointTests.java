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
package org.adamalang.web.client.pool;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;

public class WebEndpointTests {
  @Test
  public void coverage() {
    WebEndpoint we = new WebEndpoint(URI.create("https://www.adama-platform.com"));
    Assert.assertTrue(we.secure);
    Assert.assertEquals(443, we.port);
    Assert.assertEquals("www.adama-platform.com", we.host);
    Assert.assertFalse(we.equals(null));
    Assert.assertFalse(we.equals("www"));
    Assert.assertTrue(we.equals(we));
    Assert.assertTrue(we.equals(new WebEndpoint(URI.create("https://www.adama-platform.com"))));
    Assert.assertFalse(we.equals(new WebEndpoint(URI.create("https://www.adama-platformx.com"))));
    Assert.assertFalse(we.equals(new WebEndpoint(URI.create("http://www.adama-platform.com"))));
    Assert.assertFalse(we.equals(new WebEndpoint(URI.create("https://www.adama-platform.com:444"))));
    we.hashCode();
  }

  @Test
  public void port_override() {
    WebEndpoint we = new WebEndpoint(URI.create("https://www.adama-platform.com:77"));
    Assert.assertEquals(77, we.port);
  }
}
