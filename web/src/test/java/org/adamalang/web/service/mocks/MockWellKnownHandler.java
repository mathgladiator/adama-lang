package org.adamalang.web.service.mocks;

import org.adamalang.common.Callback;
import org.adamalang.web.contracts.WellKnownHandler;

public class MockWellKnownHandler implements WellKnownHandler {
  @Override
  public void handle(String uri, Callback<String> callback) {
    callback.success("Howdy");
  }
}
