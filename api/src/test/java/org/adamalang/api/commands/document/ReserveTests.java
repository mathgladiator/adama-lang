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

public class ReserveTests {
  @Test
  public void happy() {
    Scaffold scaffold = new Scaffold();
    {
      MockResponder responder = new MockResponder();
      scaffold.dispatch("{\"method\":\"reserve\",\"space\":\"game\"}", responder);
      responder.awaitDone();
      Assert.assertEquals("{\"key\":\"0\"}", responder.data.get(0));
    }
    {
      MockResponder responder = new MockResponder();
      scaffold.dispatch("{\"method\":\"reserve\",\"space\":\"game\"}", responder);
      responder.awaitDone();
      Assert.assertEquals("{\"key\":\"1\"}", responder.data.get(0));
    }
  }

  @Test
  public void exhaust() {
    Scaffold scaffold = new Scaffold();
    {
      MockResponder responder = new MockResponder();
      scaffold.dispatch("{\"method\":\"reserve\",\"space\":\"game\"}", responder);
      responder.awaitDone();
      Assert.assertEquals("{\"key\":\"0\"}", responder.data.get(0));
    }
    {
      MockResponder responder = new MockResponder();
      scaffold.dispatch("{\"method\":\"reserve\",\"space\":\"game\"}", responder);
      responder.awaitDone();
      Assert.assertEquals("{\"key\":\"1\"}", responder.data.get(0));
    }
    for (int k = 2; k < 100; k++) {
      {
        MockResponder responder = new MockResponder();
        scaffold.dispatch("{\"method\":\"reserve\",\"space\":\"game\"}", responder);
        responder.awaitDone();
        Assert.assertEquals("{\"key\":\""+k+"\"}", responder.data.get(0));
      }
    }
    {
      MockResponder responder = new MockResponder();
      scaffold.dispatch("{\"method\":\"reserve\",\"space\":\"game\"}", responder);
      responder.awaitDone();
      Assert.assertEquals(12345, responder.ex.code);
    }
  }

}
