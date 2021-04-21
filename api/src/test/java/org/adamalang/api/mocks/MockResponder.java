/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.api.mocks;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.api.commands.contracts.CommandResponder;
import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.junit.Assert;

import java.util.ArrayList;

public class MockResponder implements CommandResponder {
  public final ArrayList<String> data;
  private boolean done;
  public ErrorCodeException ex;

  public MockResponder() {
    this.data = new ArrayList<>();
    this.done = false;
  }

  @Override
  public void stream(String response) {
    Assert.assertFalse(done);
    data.add(response);
  }

  @Override
  public void finish(String response) {
    Assert.assertFalse(done);
    done = true;
    data.add(response.toString());
  }

  @Override
  public void error(ErrorCodeException ex) {
    Assert.assertFalse(done);
    done = true;
    this.ex = ex;
  }
}
