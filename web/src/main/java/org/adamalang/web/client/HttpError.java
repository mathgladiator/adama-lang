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

/** convert standard HTTP Error codes into specialized error codes that are actionable */
public class HttpError {
  public static int translateHttpStatusCodeToError(int status, int given) {
    if (status == 410) {
      return ErrorCodes.WEB_CALLBACK_RESOURCE_GONE;
    } else if (status == 404) {
      return ErrorCodes.WEB_CALLBACK_RESOURCE_NOT_FOUND;
    } else if (status == 403) {
      return ErrorCodes.WEB_CALLBACK_RESOURCE_NOT_AUTHORIZED;
    } else if (status == 301 || status == 302) {
      return ErrorCodes.WEB_CALLBACK_REDIRECT;
    }
    return given;
  }

  public static boolean convert(SimpleHttpResponseHeader header, Logger logger, int defaultErrorCode, RequestResponseMonitor.RequestResponseMonitorInstance monitor, Callback<?> callback) {
    boolean logBody = true;
    switch (header.status) {
      case 302:
      case 301:
      case 410:
      case 404:
      case 403:
        // these are converted to unique errors
        logBody = false;
        break;
      default:
        logger.error("void-callback-not-20x: {} -> {}", header.status, header.headers.toString());
    }
    int errorCode = HttpError.translateHttpStatusCodeToError(header.status, defaultErrorCode);
    if (monitor != null) {
      monitor.failure(errorCode);
    }
    callback.failure(new ErrorCodeException(errorCode, header.status + ""));
    return logBody;
  }
}
