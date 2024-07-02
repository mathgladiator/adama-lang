/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.web.io;

import org.junit.Assert;
import org.junit.Test;

import java.util.TreeMap;

public class ConnectionContextTests {
  @Test
  public void stripColonIp() {
    ConnectionContext a = new ConnectionContext("you", "123:42", "house", null);
    Assert.assertEquals("123", a.remoteIp);
  }

  @Test
  public void nulls() {
    ConnectionContext a = new ConnectionContext(null, null, null, null);
    Assert.assertEquals("", a.remoteIp);
    Assert.assertEquals("", a.userAgent);
    Assert.assertEquals("", a.origin);
    Assert.assertEquals("abc", a.identityOf("abc"));
  }

  @Test
  public void cookies() {
    TreeMap<String, String> identites = new TreeMap<>();
    identites.put("x", "abc");
    ConnectionContext a = new ConnectionContext("you", "123:42", "house", identites);
    Assert.assertEquals("abc", a.identityOf("cookie:x"));
  }
}
