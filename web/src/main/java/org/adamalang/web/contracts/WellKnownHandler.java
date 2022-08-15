package org.adamalang.web.contracts;

import org.adamalang.common.Callback;

public interface WellKnownHandler {
  public void handle(String uri, Callback<String> callback);
}
