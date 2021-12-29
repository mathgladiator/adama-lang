/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.common;

import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

/** This callback interface is used by DataService such that actions not only succeed/fail, but provide
 * progress notifications which could relate to a state diagram. */
public interface Callback<T> {
  /** the action happened successfully, and the result is value */
  public void success(T value);

  /** the action failed outright, and the reason is the exception */
  public void failure(ErrorCodeException ex);

  public static <In, Out> Callback<In> transform(Callback<Out> output, int exceptionErrorCode, Function<In, Out> f) {
    return new Callback<>() {
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

  public static <T> Callback<T> bind(ScheduledExecutorService service, int exceptionErrorCode, Callback<T> next) {
    return new Callback<T>() {
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

  public static <T> Callback<Void> handoff(Callback<T> next, int exceptionErrorCode, Runnable success) {
    return new Callback<>() {
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

  public static final Callback<Integer> DONT_CARE_INTEGER = new Callback<Integer>() {
    @Override
    public void success(Integer value) {

    }

    @Override
    public void failure(ErrorCodeException ex) {
      ex.printStackTrace();
    }
  };

  public static final Callback<Void> DONT_CARE_VOID = new Callback<Void>() {
    @Override
    public void success(Void value) {

    }

    @Override
    public void failure(ErrorCodeException ex) {
      ex.printStackTrace();
    }
  };
}
