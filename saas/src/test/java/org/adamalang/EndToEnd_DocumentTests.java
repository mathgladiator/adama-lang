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

public class EndToEnd_DocumentTests {
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
                  "@password (p) { x = 1000; pw = p; } " +
                  "message M { int z; }" +
                  "public string pw;" +
                  "channel foo(M m) { x += m.z; }" +
                  "view int z; bubble zpx = @viewer.z + x;"
          ) + "}");
      Assert.assertEquals("FINISH:{}", c4.next());
      Iterator<String> c5 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"document/create\",\"space\":\"newspace\",\"key\":\"a\",\"arg\":{}}");
      Assert.assertEquals("FINISH:{}", c5.next());
      Iterator<String> c6 = fe.execute("{\"id\":100,\"identity\":\"" + devIdentity + "\",\"method\":\"connection/create\",\"space\":\"newspace\",\"key\":\"a\"}");
      Assert.assertEquals("STREAM:{\"delta\":{\"view-state-filter\":[\"z\"]}}", c6.next());
      Assert.assertEquals("STREAM:{\"delta\":{\"data\":{\"x\":1,\"pw\":\"\",\"zpx\":1},\"seq\":4}}", c6.next());
      Iterator<String> c7 = fe.execute("{\"id\":8,\"method\":\"connection/send\",\"connection\":100,\"channel\":\"foo\",\"message\":{\"z\":2}}");
      Assert.assertEquals("FINISH:{\"seq\":5}", c7.next());
      Assert.assertEquals("STREAM:{\"delta\":{\"data\":{\"x\":3,\"zpx\":3},\"seq\":5}}", c6.next());
      fe.execute("{\"id\":8,\"method\":\"connection/update\",\"connection\":100,\"viewer-state\":{\"z\":100}}");
      Assert.assertEquals("STREAM:{\"delta\":{\"data\":{\"zpx\":103}}}", c6.next());
      Iterator<String> cPASSWORD = fe.execute("{\"id\":8,\"method\":\"connection/password\",\"username\":\"meh\",\"password\":\"meh\",\"new_password\":\"pw\",\"connection\":100}");
      Assert.assertEquals("FINISH:{}", cPASSWORD.next());
      Iterator<String> cLOGIN2 = fe.execute("{\"id\":200,\"method\":\"document/authorize-with-reset\",\"space\":\"newspace\",\"key\":\"a\",\"username\":\"cake\",\"password\":\"ninja\",\"new_password\":\"gnome\"}");
      Assert.assertTrue(cLOGIN2.next().startsWith("FINISH:{\"identity\":\""));
      Assert.assertEquals("STREAM:{\"delta\":{\"data\":{\"x\":1000,\"pw\":\"pw\",\"zpx\":1100},\"seq\":6}}", c6.next());
      Assert.assertEquals("STREAM:{\"delta\":{\"data\":{\"pw\":\"gnome\"},\"seq\":7}}", c6.next());
      Iterator<String> c8 = fe.execute("{\"id\":8,\"method\":\"connection/end\",\"connection\":100}");
      Assert.assertEquals("FINISH:{}", c8.next());
      Assert.assertEquals("FINISH:null", c6.next());
      Iterator<String> c9 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"document/create\",\"space\":\"nope\",\"key\":\"a\",\"arg\":{}}");
      Assert.assertEquals("ERROR:625678", c9.next());
      Iterator<String> c10 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"document/list\",\"space\":\"newspace\"}");
      Assert.assertTrue(c10.next().startsWith("STREAM:{\"key\":\"a\",\"created\":"));
      Assert.assertEquals("FINISH:null", c10.next());
      Iterator<String> cLOGIN = fe.execute("{\"id\":100,\"method\":\"document/authorize\",\"space\":\"newspace\",\"key\":\"a\",\"username\":\"cake\",\"password\":\"ninja\"}");
      Assert.assertTrue(cLOGIN.next().startsWith("FINISH:{\"identity\":\""));
      Iterator<String> c11 = fe.execute("{\"id\":8,\"method\":\"connection/send\",\"connection\":100,\"channel\":\"foo\",\"message\":{\"z\":2}}");
      Assert.assertEquals("ERROR:457745", c11.next());
      Assert.assertEquals("ERROR:438302", fe.execute("{\"id\":1000,\"method\":\"connection/update\",\"connection\":1000,\"viewer-state\":{\"z\":100}}").next());
      Assert.assertEquals("ERROR:474128", fe.execute("{\"id\":1000,\"method\":\"connection/end\",\"connection\":1000}").next());
      Iterator<String> c12 = fe.execute("{\"id\":125,\"identity\":\"" + devIdentity + "\",\"method\":\"connection/create\",\"space\":\"newspace\",\"key\":\"a\"}");
      Assert.assertEquals("STREAM:{\"delta\":{\"view-state-filter\":[\"z\"]}}", c12.next());
      Assert.assertEquals("STREAM:{\"delta\":{\"data\":{\"x\":1000,\"pw\":\"gnome\",\"zpx\":1000},\"seq\":12}}", c12.next());
      Iterator<String> c13 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"document/create\",\"space\":\"ide\",\"key\":\"a\",\"arg\":{}}");
      Assert.assertEquals("ERROR:995505", c13.next());
      Iterator<String> c14 = fe.execute("{\"id\":100,\"identity\":\"" + devIdentity + "\",\"method\":\"connection/create\",\"space\":\"ide\",\"key\":\"newspace\"}");
      Assert.assertTrue(c14.next().startsWith("STREAM:"));
      Iterator<String> c15 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"document/delete\",\"space\":\"nope\",\"key\":\"a\",\"arg\":{}}");
      Assert.assertEquals("ERROR:625678", c15.next());
      Iterator<String> c16 = fe.execute("{\"id\":8,\"identity\":\"" + devIdentity + "\",\"method\":\"document/delete\",\"space\":\"newspace\",\"key\":\"a\",\"arg\":{}}");
      Assert.assertEquals("FINISH:{}", c16.next());

    }
  }
}
