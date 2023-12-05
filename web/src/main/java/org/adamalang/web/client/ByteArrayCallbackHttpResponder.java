/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.web.client;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.RequestResponseMonitor;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class ByteArrayCallbackHttpResponder implements SimpleHttpResponder {
  private final Logger logger;
  private final RequestResponseMonitor.RequestResponseMonitorInstance monitor;
  private final Callback<byte[]> callback;
  private ByteArrayOutputStream memory;
  private boolean invokeSuccess;
  private boolean emissionPossible;
  private boolean logBody;

  public ByteArrayCallbackHttpResponder(Logger logger, RequestResponseMonitor.RequestResponseMonitorInstance monitor, Callback<byte[]> callback) {
    this.logger = logger;
    this.monitor = monitor;
    this.callback = callback;
    this.invokeSuccess = false;
    this.emissionPossible = true;
    this.logBody = false;
  }

  @Override
  public void start(SimpleHttpResponseHeader header) {
    if (emissionPossible) {
      if (200 <= header.status && header.status <= 204) {
        invokeSuccess = true;
      } else {
        logger.error("get-callback-not-20x: {}, {}", header.status + ":" + header.headers.toString());
        emissionPossible = false;
        logBody = true;
        int errorCode = HttpError.translateHttpStatusCodeToError(header.status, ErrorCodes.WEB_BYTEARRAY_CALLBACK_NOT_200);
        monitor.failure(errorCode);
        callback.failure(new ErrorCodeException(errorCode, header.status + ""));
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
      callback.success(memory.toByteArray());
      monitor.success();
    }
    if (logBody) {
      logger.error("failed body: {}", new String(memory.toByteArray(), StandardCharsets.UTF_8));
    }
  }

  @Override
  public void failure(ErrorCodeException ex) {
    if (emissionPossible) {
      emissionPossible = false;
      logger.error("bytearray-callback-failure:", ex);
      monitor.failure(ex.code);
      callback.failure(ex);
    }
  }
}
