/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.web;

import org.adamalang.common.Json;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.web.partial.WebPartial;
import org.adamalang.runtime.sys.web.partial.WebPutPartial;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeMap;

public class WebPutTests {
  @Test
  public void flow() {
    WebContext context = new WebContext(NtPrincipal.NO_ONE, "origin", "ip");
    TreeMap<String, String> headers = new TreeMap<>();
    headers.put("x", "abc");
    WebPut put = new WebPut(context, "/uri", headers, new NtDynamic("{\"p\":123}"), "{}");
    JsonStreamWriter writer = new JsonStreamWriter();
    put.injectWrite(writer);
    Assert.assertEquals("\"put\":{\"uri\":\"/uri\",\"headers\":{\"x\":\"abc\"},\"parameters\":{\"p\":123},\"bodyJson\":{}}", writer.toString());
    JsonStreamReader reader = new JsonStreamReader(writer.toString());
    Assert.assertEquals("put", reader.fieldName());
    WebPut clone = (WebPut) WebPutPartial.read(reader).convert(context);
    Assert.assertEquals("/uri", clone.uri);
    Assert.assertEquals("{\"p\":123}", clone.parameters.json);
    Assert.assertEquals("{}", clone.bodyJson);
    Assert.assertEquals("abc", clone.headers.storage.get("x"));
    Assert.assertNull(WebPutPartial.read(new JsonStreamReader("{}")).convert(context));
  }

  @Test
  public void route() {
    WebContext context = new WebContext(NtPrincipal.NO_ONE, "origin", "ip");
    JsonStreamReader reader = new JsonStreamReader("{\"put\":{\"uri\":\"/uri\",\"headers\":{\"x\":\"abc\"},\"parameters\":{\"p\":123},\"bodyJson\":{},\"junk\":\"rat\"}}");
    WebPut put = (WebPut) WebPartial.read(reader).convert(context);
    Assert.assertEquals("/uri", put.uri);
    Assert.assertEquals("{\"p\":123}", put.parameters.json);
    Assert.assertEquals("{}", put.bodyJson);
    Assert.assertEquals("abc", put.headers.storage.get("x"));
  }
}
