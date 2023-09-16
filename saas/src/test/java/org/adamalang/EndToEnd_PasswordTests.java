/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
