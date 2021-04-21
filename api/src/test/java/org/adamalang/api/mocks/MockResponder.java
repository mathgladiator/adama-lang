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
