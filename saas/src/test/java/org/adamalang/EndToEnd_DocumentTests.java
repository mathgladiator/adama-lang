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

import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
import java.util.regex.Pattern;

public class EndToEnd_DocumentTests {
  @Test
  public void flow() throws Exception {
    try (TestFrontEnd fe = new TestFrontEnd()) {
      Iterator<String> c0 = fe.execute("{}");
      Assert.assertEquals("ERROR:233120", c0.next());
      Runnable latch1 = fe.latchOnEmail("x@x.com");
      Iterator<String> c1 =
          fe.execute("{\"id\":1,\"method\":\"init/start\",\"email\":\"x@x.com\"}");
      latch1.run();
      Iterator<String> c2 =
          fe.execute(
              "{\"id\":2,\"connection\":1,\"method\":\"init/generate-identity\",\"code\":\""
                  + fe.codesSentToEmail.get("x@x.com")
                  + "\"}");
      String result1 = c2.next();
      Assert.assertEquals("FINISH:{}", c1.next());
      Assert.assertTrue(result1.length() > 0);
      Assert.assertEquals("FINISH:{\"identity\":", result1.substring(0, 19));
      String devIdentity = Json.parseJsonObject(result1.substring(7)).get("identity").textValue();
      Iterator<String> c3  = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"space/create\",\"space\":\"newspace\"}");
      Assert.assertEquals("FINISH:{}", c3.next());
      Iterator<String> c4  =
          fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"space/set\",\"space\":\"newspace\",\"plan\":"+EndToEnd_SpaceTests.planFor(
              "@static { create(who) { return true; } }" +
                  "@connected(who) { return true; }" +
                  "public int x = 1;" +
                  "message M { int z; }" +
                  "channel foo(M m) { x += m.z; }"
          ) + "}");
      Assert.assertEquals("FINISH:{}", c4.next());
      Iterator<String> c5 = fe.execute("{\"id\":7,\"identity\":\"" + devIdentity + "\",\"method\":\"document/create\",\"space\":\"newspace\",\"key\":\"a\",\"arg\":{}}");
      Assert.assertEquals("FINISH:{}", c5.next());
      Iterator<String> c6 = fe.execute("{\"id\":100,\"identity\":\"" + devIdentity + "\",\"method\":\"connection/create\",\"space\":\"newspace\",\"key\":\"a\"}");
      Assert.assertEquals("STREAM:{\"delta\":{\"data\":{\"x\":1},\"seq\":4}}", c6.next());
      Iterator<String> c7 = fe.execute("{\"id\":8,\"method\":\"connection/send\",\"connection\":100,\"channel\":\"foo\",\"message\":{\"z\":2}}");
      Assert.assertEquals("FINISH:{\"seq\":6}", c7.next());
      Assert.assertEquals("STREAM:{\"delta\":{\"data\":{\"x\":3},\"seq\":6}}", c6.next());
      Iterator<String> c8 = fe.execute("{\"id\":8,\"method\":\"connection/end\",\"connection\":100}");
      Assert.assertEquals("FINISH:{}", c8.next());
      Assert.assertEquals("FINISH:{}", c6.next());
    }
  }
}
