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
      Assert.assertTrue(result1.length() > 0);
      Assert.assertEquals("FINISH:{\"identity\":", result1.substring(0, 19));
      String identity1 = Json.parseJsonObject(result1.substring(7)).get("identity").textValue();
      // TODO: CREATE SPACE
      // TODO: DEPLOY SPACE
      // TODO: CREATE DOCUMENT
      // TODO: CONNECT TO DOCUMENT
      // TODO: SEND TO DOCUMENT
    }
  }
}
