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
package org.adamalang.system;

import org.junit.Assert;
import org.junit.Test;

import java.util.TreeMap;

public class FrontendHttpHandlerTests {
  @Test
  public void host2captured() {
    TreeMap<String, String> captured = FrontendHttpHandler.prepareCapture("www.a.com");
    Assert.assertEquals("www.a.com", captured.get("$host"));
    Assert.assertEquals("a.com", captured.get("$host.apex"));
    Assert.assertEquals("www", captured.get("$host.sub"));
  }

  @Test
  public void localhost() {
    TreeMap<String, String> captured = FrontendHttpHandler.prepareCapture("localhost");
    Assert.assertEquals("localhost", captured.get("$host"));
    Assert.assertEquals("localhost", captured.get("$host.apex"));
    Assert.assertNull(captured.get("$host.sub"));
  }
}
