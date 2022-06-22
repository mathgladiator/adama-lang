/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
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
                  "public int x = 1;" +
                  "message M { int z; }" +
                  "channel foo(M m) { x += m.z; }" +
                  "view int z; bubble<who, viewer> zpx = viewer.z + x;"
          ) + "}");
      Assert.assertEquals("FINISH:{}", c4.next());
      Iterator<String> c5 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"document/create\",\"space\":\"newspace\",\"key\":\"a\",\"arg\":{}}");
      Assert.assertEquals("FINISH:{}", c5.next());
      Iterator<String> c6 = fe.execute("{\"id\":100,\"identity\":\"" + devIdentity + "\",\"method\":\"connection/create\",\"space\":\"newspace\",\"key\":\"a\"}");
      Assert.assertEquals("STREAM:{\"delta\":{\"data\":{\"x\":1,\"zpx\":1},\"seq\":4}}", c6.next());
      Iterator<String> c7 = fe.execute("{\"id\":8,\"method\":\"connection/send\",\"connection\":100,\"channel\":\"foo\",\"message\":{\"z\":2}}");
      Assert.assertEquals("FINISH:{\"seq\":5}", c7.next());
      Assert.assertEquals("STREAM:{\"delta\":{\"data\":{\"x\":3,\"zpx\":3},\"seq\":5}}", c6.next());
      fe.execute("{\"id\":8,\"method\":\"connection/update\",\"connection\":100,\"viewer-state\":{\"z\":100}}");
      Assert.assertEquals("STREAM:{\"delta\":{\"data\":{\"zpx\":103},\"seq\":6}}", c6.next());
      Iterator<String> c8 = fe.execute("{\"id\":8,\"method\":\"connection/end\",\"connection\":100}");
      Assert.assertEquals("FINISH:{}", c8.next());
      Assert.assertEquals("FINISH:{}", c6.next());
      Iterator<String> c9 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"document/create\",\"space\":\"nope\",\"key\":\"a\",\"arg\":{}}");
      Assert.assertEquals("ERROR:625678", c9.next());
      Iterator<String> c10 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"document/list\",\"space\":\"newspace\"}");
      Assert.assertTrue(c10.next().startsWith("STREAM:{\"key\":\"a\",\"created\":"));
      Assert.assertEquals("FINISH:{}", c10.next());
      Iterator<String> c11 = fe.execute("{\"id\":8,\"method\":\"connection/send\",\"connection\":100,\"channel\":\"foo\",\"message\":{\"z\":2}}");
      Assert.assertEquals("ERROR:457745", c11.next());
      Assert.assertEquals("ERROR:438302", fe.execute("{\"id\":1000,\"method\":\"connection/update\",\"connection\":1000,\"viewer-state\":{\"z\":100}}").next());
      Assert.assertEquals("ERROR:474128", fe.execute("{\"id\":1000,\"method\":\"connection/end\",\"connection\":1000}").next());
      Iterator<String> c12 = fe.execute("{\"id\":125,\"identity\":\"" + devIdentity + "\",\"method\":\"connection/create\",\"space\":\"newspace\",\"key\":\"a\"}");
      Assert.assertEquals("STREAM:{\"delta\":{\"data\":{\"x\":3,\"zpx\":3},\"seq\":11}}", c12.next());
    }
  }
}
