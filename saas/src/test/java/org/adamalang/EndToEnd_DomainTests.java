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
package org.adamalang;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

public class EndToEnd_DomainTests {
  @Test
  public void flow() throws Exception {
    try (TestFrontEnd fe = new TestFrontEnd()) {
      String devIdentity = fe.setupDevIdentity();
      Iterator<String> c3  = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"space/create\",\"space\":\"newspace\"}");
      Assert.assertEquals("FINISH:{}", c3.next());
      Iterator<String> c4  =
          fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"space/set\",\"space\":\"newspace\",\"plan\":"+ EndToEnd_SpaceInfoTests.planFor(
              "@static { create { return true; } }" +
                  "@connected { return true; }" +
                  "@delete { return true; }" +
                  "@authorize (u, p) { return u + \":\" + p; }" +
                  "public int x = 1;" +
                  "@password (p) { x = 1000; } " +
                  "message M { int z; }" +
                  "channel foo(M m) { x += m.z; }" +
                  "view int z; bubble zpx = @viewer.z + x;"
          ) + "}");
      Assert.assertEquals("FINISH:{}", c4.next());

      Iterator<String> c5 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"domain/map\",\"space\":\"newspace\",\"domain\":\"www.foo.com\"}");
      Assert.assertEquals("FINISH:{}", c5.next());
      Iterator<String> c6 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"domain/list-by-space\",\"space\":\"newspace\"}");
      Assert.assertEquals("STREAM:{\"domain\":\"www.foo.com\",\"space\":\"newspace\",\"key\":null,\"route\":false,\"forward\":null,\"configured\":false,\"apex_managed\":false}", c6.next());
      Assert.assertEquals("FINISH:null", c6.next());
      Iterator<String> c6x = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"domain/configure\",\"domain\":\"www.foo.com\",\"product-config\":{\"x\":true}}");
      Assert.assertEquals("FINISH:{}", c6x.next());
      Iterator<String> c6y = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"domain/configure\",\"domain\":\"nope\",\"product-config\":{\"x\":true}}");
      Assert.assertEquals("ERROR:982291", c6y.next());
      Iterator<String> c7 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"domain/unmap\",\"domain\":\"www.foo.com\"}");
      Assert.assertEquals("FINISH:{}", c7.next());
      Iterator<String> c8 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"domain/list-by-space\",\"space\":\"newspace\"}");
      Assert.assertEquals("FINISH:null", c8.next());
      Iterator<String> c9 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"domain/claim-apex\",\"domain\":\"adama-platform.com\"}");
      Assert.assertEquals("FINISH:{\"claimed\":false,\"txtToken\":\"adama971511e89f0bf0bdcf2d9321f5fa0a80\"}", c9.next());
    }
  }
}
