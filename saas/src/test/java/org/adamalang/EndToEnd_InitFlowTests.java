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

public class EndToEnd_InitFlowTests {
  @Test
  public void flow() throws Exception {
    try (TestFrontEnd fe = new TestFrontEnd()) {
      String devIdentityNuked = fe.setupDevIdentity();
      String devIdentity = fe.generateIdentity("x@x.com", true);
      Iterator<String> c3 =
          fe.execute("{\"id\":3,\"method\":\"probe\",\"identity\":\"" + devIdentity + "\"}");
      Assert.assertEquals("FINISH:{}", c3.next());
      Iterator<String> c4 = fe.execute("{\"id\":4,\"method\":\"probe\",\"identity\":\"" + devIdentityNuked + "\"}");
      Assert.assertEquals("ERROR:403403", c4.next());
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
