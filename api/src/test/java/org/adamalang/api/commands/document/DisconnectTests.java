/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.api.commands.document;

import org.adamalang.api.commands.Scaffold;
import org.adamalang.api.mocks.MockResponder;
import org.junit.Assert;
import org.junit.Test;

public class DisconnectTests {
  @Test
  public void happy() {
    Scaffold scaffold = new Scaffold();
    {
      MockResponder responder = new MockResponder();
      scaffold.dispatch("{\"method\":\"create\",\"space\":\"game\",\"key\":42,\"arg\":{}}", responder);
      responder.awaitDone();
      Assert.assertEquals("{\"key\":\"42\",\"seq\":2}", responder.data.get(0));
    }
    MockResponder stream;
    {
      MockResponder responder = new MockResponder();
      scaffold.dispatch("{\"id\":123,\"method\":\"connect\",\"space\":\"game\",\"key\":42}", responder);
      responder.awaitFirst();
      Assert.assertEquals("{\"data\":{\"x\":0},\"outstanding\":[],\"blockers\":[],\"seq\":5}", responder.data.get(0));
      stream = responder;
    }
    {
      MockResponder responder = new MockResponder();
      scaffold.dispatch("{\"id\":45,\"method\":\"disconnect\",\"stream\":123}", responder);
      responder.awaitFirst();
      Assert.assertEquals("{\"result\":true}", responder.data.get(0));
    }
    stream.awaitDone();
    stream.assertLast("{}");
  }
}
