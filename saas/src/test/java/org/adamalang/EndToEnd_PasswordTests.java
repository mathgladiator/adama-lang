/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang;

import org.adamalang.common.Json;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

public class EndToEnd_PasswordTests {
  @Test
  public void flow() throws Exception {
    try (TestFrontEnd fe = new TestFrontEnd()) {
      final String devIdentity;
      {
        Iterator<String> c0 = fe.execute("{}");
        Assert.assertEquals("ERROR:233120", c0.next());
        devIdentity = fe.generateIdentity("x@x.com", true);
        Iterator<String> c3 = fe.execute("{\"id\":3,\"method\":\"probe\",\"identity\":\"" + devIdentity + "\"}");
        Assert.assertEquals("FINISH:{}", c3.next());
      }

      {
        Iterator<String> c1 = fe.execute("{\"id\":3,\"method\":\"account/set-password\",\"identity\":\"" + devIdentity + "\",\"password\":\"cake\"}");
        Assert.assertEquals("FINISH:{}", c1.next());
        Iterator<String> c2 = fe.execute("{\"id\":4,\"method\":\"account/login\",\"email\":\"x@x.com\",\"password\":\"cake\"}");
        String identityJson = c2.next();
        Assert.assertEquals("FINISH:{\"identity\":", identityJson.substring(0, 19));
        String newIdentity = Json.parseJsonObject(identityJson.substring(7)).get("identity").textValue();
        Iterator<String> c3 = fe.execute("{\"id\":3,\"method\":\"probe\",\"identity\":\"" + newIdentity + "\"}");
        Assert.assertEquals("FINISH:{}", c3.next());
        Iterator<String> c4 = fe.execute("{\"id\":4,\"method\":\"account/login\",\"email\":\"x@x.com\",\"password\":\"asdf\"}");
        Assert.assertEquals("ERROR:985216", c4.next());
      }
    }
  }
}
