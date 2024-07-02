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

  @Test
  public void patch_junk() {
    RemoteSite site = new RemoteSite(42, new RemoteInvocation("service", "method", NtPrincipal.NO_ONE, "{\"new\":\"hope\"}"));
    site.patch(new JsonStreamReader("{\"junk\":123}"));
    site.patch(new JsonStreamReader("123"));
  }
}
