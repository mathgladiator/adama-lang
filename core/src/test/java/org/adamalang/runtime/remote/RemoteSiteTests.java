/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.remote;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class RemoteSiteTests {
  @Test
  public void flow() {
    RemoteSite site = new RemoteSite(42, new RemoteInvocation("service", "method", NtPrincipal.NO_ONE, "{\"new\":\"hope\"}"));
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      site.dump(writer);
      Assert.assertEquals("{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"new\":\"hope\"}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}}", writer.toString());
      RemoteSite copy = new RemoteSite(42, new JsonStreamReader(writer.toString()));
      Assert.assertEquals(site.invocation(), copy.invocation());
      Assert.assertEquals(site.invocation(), copy.invocation());
    }
    site.deliver(new RemoteResult("{\"success\":true}", null, null));
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      site.dump(writer);
      Assert.assertEquals("{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"new\":\"hope\"}},\"result\":{\"result\":{\"success\":true},\"failure\":null,\"failure_code\":null}}", writer.toString());
      RemoteSite copy = new RemoteSite(42, new JsonStreamReader(writer.toString()));
      Assert.assertEquals(site.invocation(), copy.invocation());
    }
    site.revert();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      site.dump(writer);
      Assert.assertEquals("{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"new\":\"hope\"}},\"result\":{\"result\":null,\"failure\":null,\"failure_code\":null}}", writer.toString());
      RemoteSite copy = new RemoteSite(42, new JsonStreamReader(writer.toString()));
      Assert.assertEquals(site.invocation(), copy.invocation());
      Assert.assertEquals(site.invocation(), copy.invocation());
    }
    site.deliver(new RemoteResult("{\"success\":true}", null, null));
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      site.dump(writer);
      Assert.assertEquals("{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"new\":\"hope\"}},\"result\":{\"result\":{\"success\":true},\"failure\":null,\"failure_code\":null}}", writer.toString());
      RemoteSite copy = new RemoteSite(42, new JsonStreamReader(writer.toString()));
      Assert.assertEquals(site.invocation(), copy.invocation());
    }
    site.commit();
    {
      JsonStreamWriter writer = new JsonStreamWriter();
      site.dump(writer);
      Assert.assertEquals("{\"invoke\":{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"?\",\"authority\":\"?\"},\"parameter\":{\"new\":\"hope\"}},\"result\":{\"result\":{\"success\":true},\"failure\":null,\"failure_code\":null}}", writer.toString());
      RemoteSite copy = new RemoteSite(42, new JsonStreamReader(writer.toString()));
      Assert.assertEquals(site.invocation(), copy.invocation());
      Assert.assertEquals(site, copy);
      Assert.assertEquals(site.hashCode(), copy.hashCode());
      Assert.assertFalse(site.equals(null));
      Assert.assertFalse(site.equals("X"));
    }
  }
}
