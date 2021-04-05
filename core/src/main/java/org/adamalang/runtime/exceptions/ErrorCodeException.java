/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.exceptions;

/** an error happened related to an error code that we can present to the
 * public */
public class ErrorCodeException extends Exception {
  public final int code;

  public ErrorCodeException(final int code) {
    this.code = code;
  }

  public ErrorCodeException(final int code, final Throwable cause) {
    super(cause);
    this.code = code;
  }

  public static ErrorCodeException detectOrWrap(int code, Throwable cause) {
    if (cause instanceof RuntimeException) {
      if (cause.getCause() instanceof ErrorCodeException) {
        return (ErrorCodeException) (cause.getCause());
      }
    }
    if (cause instanceof ErrorCodeException) {
      return (ErrorCodeException) cause;
    }
    return new ErrorCodeException(code, cause);
  }
}
