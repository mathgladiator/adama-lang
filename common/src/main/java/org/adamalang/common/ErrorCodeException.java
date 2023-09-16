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
package org.adamalang.common;

/** an error happened related to an error code that we can present to the public */
public class ErrorCodeException extends Exception {
  public final int code;

  public ErrorCodeException(final int code) {
    super("code:" + code);
    this.code = code;
  }

  public ErrorCodeException(final int code, String message) {
    super(message);
    this.code = code;
  }

  public ErrorCodeException(final int code, final Throwable cause) {
    super("code:" + code + ":" + cause.getMessage(), cause);
    this.code = code;
  }

  public static ErrorCodeException detectOrWrap(int code, Throwable cause, ExceptionLogger logger) {
    if (cause instanceof RuntimeException) {
      if (cause.getCause() instanceof ErrorCodeException) {
        return (ErrorCodeException) (cause.getCause());
      }
    }
    if (cause instanceof ErrorCodeException) {
      return (ErrorCodeException) cause;
    }
    if (logger != null) {
      logger.convertedToErrorCode(cause, code);
    }
    return new ErrorCodeException(code, cause);
  }
}
