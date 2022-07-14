/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.remote;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class RemoteInvocationTests {
  @Test
  public void flow() {
    RemoteInvocation invocation = new RemoteInvocation("service", "method", new NtClient("a", "b"), "{\"x\":1000}");
    JsonStreamWriter writer = new JsonStreamWriter();
    invocation.write(writer);
    Assert.assertEquals("{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"a\",\"authority\":\"b\"},\"parameter\":{\"x\":1000}}", writer.toString());
    RemoteInvocation copy = new RemoteInvocation(new JsonStreamReader(writer.toString()));
    Assert.assertEquals("service", copy.service);
    Assert.assertEquals("method", copy.method);
    Assert.assertEquals("a", copy.who.agent);
    Assert.assertEquals("b", copy.who.authority);
    Assert.assertEquals("{\"x\":1000}", copy.parameter);
    Assert.assertEquals(invocation.hashCode(), copy.hashCode());
    Assert.assertTrue(invocation.equals(copy));
    Assert.assertFalse(invocation.equals(null));
    Assert.assertFalse(invocation.equals("X"));
    Assert.assertEquals(0, invocation.compareTo(copy));

    Assert.assertEquals(10, invocation.compareTo(new RemoteInvocation("service", "method", new NtClient("a", "b"), "")));
    Assert.assertEquals(1, invocation.compareTo(new RemoteInvocation("service", "method", new NtClient("", ""), "")));
    Assert.assertEquals(6, invocation.compareTo(new RemoteInvocation("service", "", new NtClient("", ""), "")));
    Assert.assertEquals(7, invocation.compareTo(new RemoteInvocation("", "", new NtClient("", ""), "")));

  }
}
