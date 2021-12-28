/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
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

  @Deprecated
  public static ErrorCodeException detectOrWrap(int code, Throwable cause) {
    return detectOrWrap(code, cause, null);
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
