/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.remote;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class RemoteInvocationTests {
  @Test
  public void flow() {
    RemoteInvocation invocation = new RemoteInvocation("service", "method", new NtPrincipal("a", "b"), "{\"x\":1000}");
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

    Assert.assertEquals(10, invocation.compareTo(new RemoteInvocation("service", "method", new NtPrincipal("a", "b"), "")));
    Assert.assertEquals(1, invocation.compareTo(new RemoteInvocation("service", "method", new NtPrincipal("", ""), "")));
    Assert.assertEquals(6, invocation.compareTo(new RemoteInvocation("service", "", new NtPrincipal("", ""), "")));
    Assert.assertEquals(7, invocation.compareTo(new RemoteInvocation("", "", new NtPrincipal("", ""), "")));

  }
}
