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

public class ConnectTests {
  @Test
  public void happy() {
    Scaffold scaffold = new Scaffold();
    {
      MockResponder responder = new MockResponder();
      scaffold.dispatch("{\"method\":\"create\",\"space\":\"game\",\"key\":42,\"arg\":{}}", responder);
      responder.awaitDone();
      Assert.assertEquals("{\"seq\":2}", responder.data.get(0));
    }

    {
      MockResponder responder = new MockResponder();
      scaffold.dispatch("{\"id\":123,\"method\":\"connect\",\"space\":\"game\",\"key\":42}", responder);
      responder.awaitFirst();
      Assert.assertEquals("{\"data\":{\"x\":0},\"outstanding\":[],\"blockers\":[],\"seq\":5}", responder.data.get(0));
    }
  }

  @Test
  public void connectTwice() {
    Scaffold scaffold = new Scaffold();
    {
      MockResponder responder = new MockResponder();
      scaffold.dispatch("{\"method\":\"create\",\"space\":\"game\",\"key\":42,\"arg\":{}}", responder);
      responder.awaitDone();
      Assert.assertEquals("{\"seq\":2}", responder.data.get(0));
    }

    {
      MockResponder responder = new MockResponder();
      scaffold.dispatch("{\"id\":123,\"method\":\"connect\",\"space\":\"game\",\"key\":42}", responder);
      responder.awaitFirst();
      Assert.assertEquals("{\"data\":{\"x\":0},\"outstanding\":[],\"blockers\":[],\"seq\":5}", responder.data.get(0));
    }

    {
      MockResponder responder = new MockResponder();
      scaffold.dispatch("{\"id\":42,\"method\":\"connect\",\"space\":\"game\",\"key\":42}", responder);
      responder.awaitFirst();
      Assert.assertEquals("{\"data\":{\"x\":0},\"outstanding\":[],\"blockers\":[],\"seq\":7}", responder.data.get(0));
    }
  }

  @Test
  public void sad() {
    Scaffold scaffold = new Scaffold();
    {
      MockResponder responder = new MockResponder();
      scaffold.dispatch("{\"id\":123,\"method\":\"connect\",\"space\":\"bad\",\"key\":42}", responder);
      responder.awaitFirst();
      Assert.assertEquals(100, responder.ex.code);
    }
  }
}
