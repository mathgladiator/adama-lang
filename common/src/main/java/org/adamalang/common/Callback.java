/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common;

import java.util.function.Function;

/**
 * This callback interface is used by DataService such that actions not only succeed/fail, but
 * provide progress notifications which could relate to a state diagram.
 */
public interface Callback<T> {
  ExceptionLogger CALLBACK_LOGGER = ExceptionLogger.FOR(Callback.class);

  Callback<Integer> DONT_CARE_INTEGER = new Callback<Integer>() {
    @Override
    public void success(Integer value) {
    }

    @Override
    public void failure(ErrorCodeException ex) {
    }
  };
  Callback<Void> DONT_CARE_VOID = new Callback<Void>() {
    @Override
    public void success(Void value) {
    }

    @Override
    public void failure(ErrorCodeException ex) {
    }
  };

  static <In, Out> Callback<In> transform(Callback<Out> output, int exceptionErrorCode, Function<In, Out> f) {
    return new Callback<>() {
      @Override
      public void success(In value) {
        try {
          output.success(f.apply(value));
        } catch (Throwable ex) {
          output.failure(ErrorCodeException.detectOrWrap(exceptionErrorCode, ex, CALLBACK_LOGGER));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        output.failure(ex);
      }
    };
  }

  /** the action happened successfully, and the result is value */
  void success(T value);

  /** the action failed outright, and the reason is the exception */
  void failure(ErrorCodeException ex);

  static <T> Callback<Void> handoff(Callback<T> next, int exceptionErrorCode, Runnable success) {
    return new Callback<>() {
      @Override
      public void success(Void value) {
        try {
          success.run();
        } catch (Throwable ex) {
          next.failure(ErrorCodeException.detectOrWrap(exceptionErrorCode, ex, CALLBACK_LOGGER));
        }
      }

      @Override
      public void failure(ErrorCodeException ex) {
        next.failure(ex);
      }
    };
  }
}
