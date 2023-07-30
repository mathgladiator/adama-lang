/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
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
