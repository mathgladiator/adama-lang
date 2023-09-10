/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
      Assert.assertEquals("STREAM:{\"domain\":\"www.foo.com\",\"space\":\"newspace\",\"key\":null,\"route\":false}", c6.next());
      Assert.assertEquals("FINISH:null", c6.next());
      Iterator<String> c7 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"domain/unmap\",\"domain\":\"www.foo.com\"}");
      Assert.assertEquals("FINISH:{}", c7.next());
      Iterator<String> c8 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"domain/list-by-space\",\"space\":\"newspace\"}");
      Assert.assertEquals("FINISH:null", c8.next());
    }
  }
}
