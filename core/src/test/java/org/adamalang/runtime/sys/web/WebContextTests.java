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
package org.adamalang.runtime.sys.web;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class WebContextTests {
  @Test
  public void save() {
    WebContext context = new WebContext(new NtPrincipal("agent", "auth"), "origin", "ip");
    JsonStreamWriter writer = new JsonStreamWriter();
    context.writeAsObject(writer);
    Assert.assertEquals("{\"who\":{\"agent\":\"agent\",\"authority\":\"auth\"},\"origin\":\"origin\",\"ip\":\"ip\"}", writer.toString());
  }

  @Test
  public void load() {
    WebContext wc = WebContext.readFromObject(new JsonStreamReader("{\"who\":{\"agent\":\"agent\",\"authority\":\"auth\"},\"origin\":\"origin\",\"ip\":\"ip\"}"));
    Assert.assertEquals("agent", wc.who.agent);
    Assert.assertEquals("auth", wc.who.authority);
    Assert.assertEquals("origin", wc.origin);
    Assert.assertEquals("ip", wc.ip);
  }
}
