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

  @Test
  public void redundant() {
    RemoteInvocation copy = new RemoteInvocation(new JsonStreamReader("{\"service\":\"service\",\"method\":\"method\",\"who\":{\"agent\":\"a\",\"authority\":\"b\"},\"parameter\":{\"x\":1000},\"junk\":123}"));
    Assert.assertEquals("service", copy.service);
    Assert.assertEquals("method", copy.method);
    Assert.assertEquals("a", copy.who.agent);
    Assert.assertEquals("b", copy.who.authority);
    Assert.assertEquals("{\"x\":1000}", copy.parameter);
  }
}
