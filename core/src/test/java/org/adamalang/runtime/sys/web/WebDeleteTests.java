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
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.web.partial.WebDeletePartial;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeMap;

public class WebDeleteTests {
  @Test
  public void flow() {
    WebContext context = new WebContext(NtPrincipal.NO_ONE, "origin", "ip");
    TreeMap<String, String> headers = new TreeMap<>();
    headers.put("x", "abc");
    WebDelete delete = new WebDelete(context, "/uri", headers, new NtDynamic("{\"p\":123}"));
    JsonStreamWriter writer = new JsonStreamWriter();
    delete.injectWrite(writer);
    Assert.assertEquals("\"delete\":{\"uri\":\"/uri\",\"headers\":{\"x\":\"abc\"},\"parameters\":{\"p\":123}}", writer.toString());
    JsonStreamReader reader = new JsonStreamReader(writer.toString());
    Assert.assertEquals("delete", reader.fieldName());
    WebDelete clone = (WebDelete) WebDeletePartial.read(reader).convert(context);
    Assert.assertEquals("/uri", clone.uri);
    Assert.assertEquals("{\"p\":123}", clone.parameters.json);
    Assert.assertEquals("abc", clone.headers.storage.get("x"));
    Assert.assertNull(WebDeletePartial.read(new JsonStreamReader("{}")).convert(context));
    JsonStreamWriter whole = new JsonStreamWriter();
    delete.writeAsObject(whole);
    Assert.assertEquals("{\"delete\":{\"uri\":\"/uri\",\"headers\":{\"x\":\"abc\"},\"parameters\":{\"p\":123}}}", whole.toString());
  }
}
