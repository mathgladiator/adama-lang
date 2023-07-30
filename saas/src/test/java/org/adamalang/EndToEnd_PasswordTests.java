/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
      {
        Iterator<String> c3 = fe.execute("{\"id\":3,\"method\":\"account/get-payment-plan\",\"identity\":\"" + devIdentity + "\"}");
        Assert.assertTrue(c3.next().startsWith("FINISH:{\"paymentPlan\":\"none\""));
      }
    }
  }
}
