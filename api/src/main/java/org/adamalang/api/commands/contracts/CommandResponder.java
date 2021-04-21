package org.adamalang.api.commands.contracts;

import org.adamalang.runtime.exceptions.ErrorCodeException;

/** how commands respond */
public interface CommandResponder {

  /** stream an update */
  public void stream(String json);

  /** respond in a terminal fashion */
  public void finish(String json);

  /** respond with a terminal error */
  public void error(ErrorCodeException ex);
}
