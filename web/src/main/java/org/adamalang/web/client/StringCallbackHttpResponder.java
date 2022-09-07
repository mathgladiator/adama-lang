/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.client;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.RequestResponseMonitor;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class StringCallbackHttpResponder implements SimpleHttpResponder {
  private final Logger logger;
  private final RequestResponseMonitor.RequestResponseMonitorInstance monitor;
  private final Callback<String> callback;
  private ByteArrayOutputStream memory;
  private boolean invokeSuccess = false;
  private boolean emissionPossible = false;

  public StringCallbackHttpResponder(Logger logger, RequestResponseMonitor.RequestResponseMonitorInstance monitor, Callback<String> callback) {
    this.logger = logger;
    this.monitor = monitor;
    this.callback = callback;
    this.invokeSuccess = false;
    this.emissionPossible = true;
  }

  @Override
  public void start(SimpleHttpResponseHeader header) {
    if (emissionPossible) {
      if (header.status == 200 || header.status == 204) {
        invokeSuccess = true;
      } else {
        logger.error("get-callback-not-200:", header.status + ":" + header.headers.toString());
        monitor.failure(ErrorCodes.WEB_STRING_CALLBACK_NOT_200);
        callback.failure(new ErrorCodeException(ErrorCodes.WEB_STRING_CALLBACK_NOT_200, header.status + ""));
        emissionPossible = false;
      }
    }
  }

  @Override
  public void bodyStart(long size) {
    if (size > 0) {
      this.memory = new ByteArrayOutputStream((int) size);
    }
  }

  @Override
  public void bodyFragment(byte[] chunk, int offset, int len) {
    if (memory == null) { // unknown size due to an error
      this.memory = new ByteArrayOutputStream();
    }
    memory.write(chunk, offset, len);
  }

  @Override
  public void bodyEnd() {
    if (invokeSuccess && emissionPossible) {
      callback.success(new String(memory.toByteArray(), StandardCharsets.UTF_8));
    }
  }

  @Override
  public void failure(ErrorCodeException ex) {
    if (emissionPossible) {
      emissionPossible = false;
      logger.error("string-callback-failure:", ex);
      monitor.failure(ex.code);
      callback.failure(ex);
    }
  }
}
