/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

public class EndToEnd_InitFlowTests {
  @Test
  public void flow() throws Exception {
    try (TestFrontEnd fe = new TestFrontEnd()) {
      Iterator<String> c0 = fe.execute("{}");
      Assert.assertEquals("ERROR:233120", c0.next());
      String devIdentityNuked = fe.setupDevIdentity();
      String devIdentity = fe.generateIdentity("x@x.com", true);
      Iterator<String> c3 =
          fe.execute("{\"id\":3,\"method\":\"probe\",\"identity\":\"" + devIdentity + "\"}");
      Assert.assertEquals("FINISH:{}", c3.next());
      Iterator<String> c4 = fe.execute("{\"id\":4,\"method\":\"probe\",\"identity\":\"" + devIdentityNuked + "\"}");
      Assert.assertEquals("ERROR:966671", c4.next());
      {
        String email = "f@x.com";
        Runnable latch1 = fe.latchOnEmail(email);
        Iterator<String> c1 = fe.execute("{\"id\":1,\"method\":\"init/setup-account\",\"email\":\""+email+"\"}");
        latch1.run();
        Assert.assertEquals("FINISH:{}", c1.next());
        Iterator<String> c2 = fe.execute("{\"id\":2,\"method\":\"init/complete-account\",\"email\":\""+email+"\",\"code\":\"X" + fe.codesSentToEmail.get(email) + "\"}");
        String result1 = c2.next();
        Assert.assertEquals("ERROR:916486", result1);
      }
    }
  }
}
