package org.adamalang.runtime.deploy;

import org.adamalang.common.Callback;

/** event to trigger a deployment */
public interface Deploy {

  public void deploy(String space, Callback<Void> callback);
}
