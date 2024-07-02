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
package org.adamalang.runtime.sys.web.partial;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.web.WebContext;
import org.adamalang.runtime.sys.web.WebPut;
import org.junit.Assert;
import org.junit.Test;

public class WebPutPartialTests {
  @Test
  public void nulls() {
    WebContext context = new WebContext(NtPrincipal.NO_ONE, "origin", "ip");
    Assert.assertNull(WebPutPartial.read(new JsonStreamReader("{}")).convert(context));
    Assert.assertNull(WebPutPartial.read(new JsonStreamReader("{\"junk\":[]}")).convert(context));
    Assert.assertNull(WebPutPartial.read(new JsonStreamReader("{\"uri\":\"uri\",\"headers\":{}}")).convert(context));
    Assert.assertNull(WebPutPartial.read(new JsonStreamReader("{\"uri\":\"uri\",\"headers\":{},\"parameters\":{}}")).convert(context));
    Assert.assertNull(WebPutPartial.read(new JsonStreamReader("{\"uri\":\"uri\",\"headers\":\"cake\",\"parameters\":{},\"bodyJson\":\"body\"}")).convert(context));
  }

  @Test
  public void happy() {
    WebContext context = new WebContext(NtPrincipal.NO_ONE, "origin", "ip");
    WebPut put = (WebPut) WebPutPartial.read(new JsonStreamReader("{\"uri\":\"uri\",\"headers\":{\"x\":\"y\"},\"parameters\":{},\"bodyJson\":\"body\"}")).convert(context);
    Assert.assertNotNull(put);
    Assert.assertEquals("uri", put.uri);
    Assert.assertEquals("y", put.headers.get("x"));
    Assert.assertEquals("{}", put.parameters.json);
    Assert.assertEquals("\"body\"", put.bodyJson);
  }
}
