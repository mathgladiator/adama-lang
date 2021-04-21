package org.adamalang.api.operations;

import org.adamalang.runtime.exceptions.ErrorCodeException;

/** witness exceptions at various points */
public interface ExceptionAccumulator {
  public void witness(ErrorCodeException ex);

  public void uncaught(Throwable t);
}
