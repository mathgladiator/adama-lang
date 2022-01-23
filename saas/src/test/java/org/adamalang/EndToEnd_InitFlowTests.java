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

public class EndToEnd_InitFlowTests {
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
              "{\"id\":2,\"connection\":1,\"method\":\"init/generate-identity\",\"revoke\":true,\"code\":\""
                  + fe.codesSentToEmail.get("x@x.com")
                  + "\"}");
      String result1 = c2.next();
      Assert.assertTrue(result1.length() > 0);
      Assert.assertEquals("FINISH:{\"identity\":", result1.substring(0, 19));
      String identity1 = Json.parseJsonObject(result1.substring(7)).get("identity").textValue();
      Assert.assertEquals("FINISH:{}", c1.next());
      Iterator<String> c3 =
          fe.execute("{\"id\":3,\"method\":\"probe\",\"identity\":\"" + identity1 + "\"}");
      Assert.assertEquals("FINISH:{}", c3.next());
      Runnable latch2 = fe.latchOnEmail("x@x.com");
      Iterator<String> c4 =
          fe.execute("{\"id\":4,\"method\":\"init/start\",\"email\":\"x@x.com\"}");
      latch2.run();
      Iterator<String> c5 =
          fe.execute(
              "{\"id\":5,\"connection\":4,\"method\":\"init/revoke-all\",\"code\":\""
                  + fe.codesSentToEmail.get("x@x.com")
                  + "\"}");
      Assert.assertEquals("FINISH:{}", c5.next());
      Runnable latch2_5 = fe.latchOnEmail("x@x.com");
      Iterator<String> c6_a =
          fe.execute("{\"id\":1,\"method\":\"init/start\",\"email\":\"x@x.com\"}");
      latch2_5.run();
      Iterator<String> c6_b =
          fe.execute(
              "{\"id\":2,\"connection\":1,\"method\":\"init/generate-identity\",\"code\":\""
                  + fe.codesSentToEmail.get("x@x.com")
                  + "\"}");
      String result2 = c6_b.next();
      Assert.assertTrue(result2.length() > 0);
      Assert.assertEquals("FINISH:{\"identity\":", result2.substring(0, 19));
      String identity2 = Json.parseJsonObject(result2.substring(7)).get("identity").textValue();
      Assert.assertEquals("FINISH:{}", c6_a.next());
      Assert.assertEquals("FINISH:{}", c4.next());
      Iterator<String> c7 =
          fe.execute("{\"id\":7,\"method\":\"probe\",\"identity\":\"" + identity1 + "\"}");
      Assert.assertEquals("ERROR:966671", c7.next());
      Iterator<String> c8 =
          fe.execute("{\"id\":8,\"method\":\"probe\",\"identity\":\"" + identity2 + "\"}");
      Assert.assertEquals("FINISH:{}", c8.next());
      Runnable latch3 = fe.latchOnEmail("x@x.com");
      Iterator<String> c9 =
          fe.execute("{\"id\":9,\"method\":\"init/start\",\"email\":\"x@x.com\"}");
      latch3.run();
      Iterator<String> c10 =
          fe.execute(
              "{\"id\":10,\"connection\":9,\"method\":\"init/generate-identity\",\"revoke\":false,\"code\":\""
                  + fe.codesSentToEmail.get("x@x.com") + "V"
                  + "\"}");
      Assert.assertEquals("ERROR:916486", c10.next());
      Assert.assertEquals("FINISH:{}", c9.next());
      Runnable latch4 = fe.latchOnEmail("x@x.com");
      Iterator<String> c11 =
          fe.execute("{\"id\":9,\"method\":\"init/start\",\"email\":\"x@x.com\"}");
      latch4.run();
      Iterator<String> c12 =
          fe.execute(
              "{\"id\":10,\"connection\":9,\"method\":\"init/revoke-all\",\"code\":\""
                  + fe.codesSentToEmail.get("x@x.com") + "V"
                  + "\"}");
      Assert.assertEquals("ERROR:974851", c12.next());
      Assert.assertEquals("FINISH:{}", c11.next());
    }
  }
}
