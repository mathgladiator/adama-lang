/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.client;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.RequestResponseMonitor;
import org.slf4j.Logger;

public class VoidCallbackHttpResponder implements SimpleHttpResponder {
  private final Logger logger;
  private final RequestResponseMonitor.RequestResponseMonitorInstance monitor;
  private final Callback<Void> callback;

  public VoidCallbackHttpResponder(Logger logger, RequestResponseMonitor.RequestResponseMonitorInstance monitor, Callback<Void> callback) {
    this.logger = logger;
    this.monitor = monitor;
    this.callback = callback;
  }

  @Override
  public void start(SimpleHttpResponseHeader header) {
    if (header.status == 200 || header.status == 204) {
      monitor.success();
      callback.success(null);
    } else {
      logger.error("void-callback-not-200: {} -> {}", header.status, header.headers.toString());
      monitor.failure(ErrorCodes.WEB_VOID_CALLBACK_NOT_200);
      callback.failure(new ErrorCodeException(ErrorCodes.WEB_VOID_CALLBACK_NOT_200, header.status + ""));
    }
  }

  @Override
  public void bodyStart(long size) {
    if (size != 0) {
      monitor.extra();
    }
  }

  @Override
  public void bodyFragment(byte[] chunk, int offset, int len) {
    if (len > 0) {
      logger.error("unexpected-body: {}", new String(chunk, offset, len));
    }
  }

  @Override
  public void bodyEnd() {}

  @Override
  public void failure(ErrorCodeException ex) {
    logger.error("void-callback-failure:", ex);
    monitor.failure(ex.code);
    callback.failure(ex);
  }
}
