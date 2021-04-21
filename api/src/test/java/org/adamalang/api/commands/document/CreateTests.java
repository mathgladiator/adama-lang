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

public class CreateTests {
  @Test
  public void happy() {
    Scaffold scaffold = new Scaffold();
    MockResponder responder = new MockResponder();
    scaffold.dispatch("{\"method\":\"create\",\"space\":\"game\",\"key\":42,\"arg\":{}}", responder);
    responder.awaitDone();
    Assert.assertEquals("{\"key\":\"42\",\"seq\":2}", responder.data.get(0));
  }

  @Test
  public void sad() {
    Scaffold scaffold = new Scaffold();
    MockResponder responder = new MockResponder();
    scaffold.dispatch("{\"method\":\"create\",\"space\":\"bad\",\"key\":42,\"arg\":{}}", responder);
    responder.awaitDone();
    Assert.assertEquals(42, responder.ex.code);
  }

  @Test
  public void sadMissingArg() {
    Scaffold scaffold = new Scaffold();
    {
      MockResponder responder = new MockResponder();
      scaffold.dispatch("{\"id\":123,\"method\":\"create\",\"space\":\"bad\",\"key\":42}", responder);
      responder.awaitFirst();
      Assert.assertEquals(40106, responder.ex.code);
    }
  }
}
