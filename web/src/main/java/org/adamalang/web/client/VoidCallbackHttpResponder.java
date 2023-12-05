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
    if (200 <= header.status && header.status <= 204) {
      monitor.success();
      callback.success(null);
    } else {
      logger.error("void-callback-not-20x: {} -> {}", header.status, header.headers.toString());
      int errorCode = HttpError.translateHttpStatusCodeToError(header.status, ErrorCodes.WEB_VOID_CALLBACK_NOT_200);
      monitor.failure(errorCode);
      callback.failure(new ErrorCodeException(errorCode, header.status + ""));
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
