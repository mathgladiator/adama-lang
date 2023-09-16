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
package org.adamalang.runtime;

import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.CoreRequestContext;
import org.junit.Assert;
import org.junit.Test;

public class JsonConstructionTests {

  @Test
  public void listMaybeStrSetup() throws Exception {
    new RealDocumentSetup("record S { maybe<string> item; } table<S> s; public formula j = (iterate s).item.join(\", \");");
  }

  @Test
  public void sendMessageBasic() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "@connected { return true; } message M { int x; double y; string z; bool b; } int xx; double yy; string zz; bool bb; channel foo(M m) { xx = m.x; yy = m.y; zz = m.z; bb = m.b; }");
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    setup.document.send(
        ContextSupport.WRAP(NtPrincipal.NO_ONE),
        null,
        "marker",
        "foo",
        "{\"x\":123,\"y\":3.14,\"z\":\"w00t\",\"b\":true}",
        new RealDocumentSetup.AssertInt(4));
    Assert.assertEquals(
        "{\"xx\":123,\"yy\":3.14,\"zz\":\"w00t\",\"bb\":true,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":4,\"__entropy\":\"-4186135525725789677\",\"__auto_future_id\":0,\"__connection_id\":1,\"__message_id\":0,\"__time\":\"0\",\"__timezone\":\"UTC\",\"__auto_table_row_id\":0,\"__auto_gen\":0,\"__auto_cache_id\":0,\"__cache\":{},\"__webTaskId\":0,\"__dedupe\":{\"?/?/marker\":\"0\"},\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}},\"__webqueue\":{},\"__replication\":{}}",
        setup.document.json());
  }

  @Test
  public void sendMessageFixInvalid() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "@connected { return true; } message M { int x; double y; string z; bool b; } int xx; double yy; string zz; bool bb; channel foo(M m) { xx = m.x; yy = m.y; zz = m.z; bb = m.b; }");
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    setup.document.send(
        ContextSupport.WRAP(NtPrincipal.NO_ONE),
        null,
        "marker1",
        "foo",
        "{\"x\":\"\",\"y\":null,\"z\":\"w00t\",\"b\":true}",
        new RealDocumentSetup.AssertInt(4));
  }

  @Test
  public void sendMessageInvalidCrash() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "@connected { return true; } message M { int x; double y; string z; bool b; } int xx; double yy; string zz; bool bb; channel foo(M m) { xx = m.x; yy = m.y; zz = m.z; bb = m.b; }");
    setup.document.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), new RealDocumentSetup.AssertInt(2));
    setup.document.send(
        ContextSupport.WRAP(NtPrincipal.NO_ONE),
        null,
        "marker1",
        "foo",
        "{\"x\":true,\"y\":null,\"z\":\"w00t\",\"b\":true}",
        new RealDocumentSetup.AssertFailure(145627));
  }
}
