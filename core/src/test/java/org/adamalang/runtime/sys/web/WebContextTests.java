/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
