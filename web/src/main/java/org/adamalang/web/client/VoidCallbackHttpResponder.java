/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
  private boolean emissionPossible;

  public VoidCallbackHttpResponder(Logger logger, RequestResponseMonitor.RequestResponseMonitorInstance monitor, Callback<Void> callback) {
    this.logger = logger;
    this.monitor = monitor;
    this.callback = callback;
    this.emissionPossible = true;
  }

  @Override
  public void start(SimpleHttpResponseHeader header) {
    if (emissionPossible) {
      emissionPossible = false;
      if (200 <= header.status && header.status <= 204) {
        monitor.success();
        callback.success(null);
      } else {
        HttpError.convert(header, logger,  ErrorCodes.WEB_VOID_CALLBACK_NOT_200, monitor, callback);
      }
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
    if (emissionPossible) {
      emissionPossible = false;
      logger.error("void-callback-failure:", ex);
      monitor.failure(ex.code);
      callback.failure(ex);
    }
  }
}
