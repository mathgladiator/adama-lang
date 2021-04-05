/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.contracts;

import org.adamalang.runtime.ErrorCodes;
import org.adamalang.runtime.exceptions.ErrorCodeException;

import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

/** This callback interface is used by DataService such that actions not only succeed/fail, but provide
 * progress notifications which could relate to a state diagram. */
public interface DataCallback<T> {
  /** the action happened successfully, and the result is value */
  public void success(T value);

  /** the action failed outright, and the reason is the exception */
  public void failure(ErrorCodeException ex);

  public static <In, Out> DataCallback<In> transform(DataCallback<Out> output, int exceptionErrorCode, Function<In, Out> f) {
    return new DataCallback<>() {
      @Override
      public void success(In value) {
        try {
          output.success(f.apply(value));
        } catch (Throwable ex) {
          output.failure(ErrorCodeException.detectOrWrap(exceptionErrorCode, ex));
        }
      }
      @Override
      public void failure(ErrorCodeException ex) {
        output.failure(ex);
      }
    };
  }

  public static <T> DataCallback<T> bind(ScheduledExecutorService service, int exceptionErrorCode, DataCallback<T> next) {
    return new DataCallback<T>() {
      @Override
      public void success(T value) {
        service.execute(() -> {
          try {
            next.success(value);
          } catch (Throwable ex) {
            next.failure(ErrorCodeException.detectOrWrap(exceptionErrorCode, ex));
          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        service.execute(() -> {
          next.failure(ex);
        });
      }
    };
  }

  public static <T> DataCallback<Void> handoff(DataCallback<T> next, int exceptionErrorCode, Runnable success) {
    return new DataCallback<>() {
      @Override
      public void success(Void value) {
        try {
          success.run();
        } catch (Throwable ex) {
          next.failure(ErrorCodeException.detectOrWrap(exceptionErrorCode, ex));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        next.failure(ex);
      }
    };
  }
}