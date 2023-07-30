package org.adamalang;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.ExceptionRunnable;
import org.adamalang.web.io.JsonResponder;

import java.util.concurrent.ExecutorService;

public class ControlPlaneExecutor {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(ControlPlaneExecutor.class);
  private final ExecutorService executor;

  private ControlPlaneExecutor(ExecutorService executor) {
    this.executor = executor;
  }

  public void execute(ExceptionRunnable runnable, Session session, JsonResponder responder) {
    this.executor.execute(() -> {
      // TODO: validate session has been validated
      try {
        runnable.run();
      } catch (Exception ex) {
        responder.error(ErrorCodeException.detectOrWrap(0, ex, LOGGER));
      }
    });
  }
}
