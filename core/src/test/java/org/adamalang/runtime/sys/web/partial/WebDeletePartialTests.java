/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.web.partial;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.web.WebContext;
import org.adamalang.runtime.sys.web.WebDelete;
import org.junit.Assert;
import org.junit.Test;

public class WebDeletePartialTests {
  @Test
  public void nulls() {
    WebContext context = new WebContext(NtPrincipal.NO_ONE, "origin", "ip");
    Assert.assertNull(WebDeletePartial.read(new JsonStreamReader("{}")).convert(context));
    Assert.assertNull(WebDeletePartial.read(new JsonStreamReader("{\"junk\":[]}")).convert(context));
        Assert.assertNull(WebDeletePartial.read(new JsonStreamReader("{\"uri\":\"uri\",\"headers\":{}}")).convert(context));
    Assert.assertNull(WebDeletePartial.read(new JsonStreamReader("{\"uri\":\"uri\",\"headers\":\"cake\",\"parameters\":{}}")).convert(context));
    Assert.assertNotNull(WebDeletePartial.read(new JsonStreamReader("{\"uri\":\"uri\",\"headers\":{},\"parameters\":{}}")).convert(context));
  }

  @Test
  public void happy() {
    WebContext context = new WebContext(NtPrincipal.NO_ONE, "origin", "ip");
    WebDelete put = (WebDelete) WebDeletePartial.read(new JsonStreamReader("{\"uri\":\"uri\",\"headers\":{\"x\":\"y\"},\"parameters\":{},\"bodyJson\":\"body\"}")).convert(context);
    Assert.assertNotNull(put);
    Assert.assertEquals("uri", put.uri);
    Assert.assertEquals("y", put.headers.get("x"));
    Assert.assertEquals("{}", put.parameters.json);
  }

}
