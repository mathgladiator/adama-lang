/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.contracts;

import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

/** This callback interface is used by DataService such that actions not only succeed/fail, but provide
 * progress notifications which could relate to a state diagram. */
public interface DataCallback<T> {
  /** the action happened successfully, and the result is value */
  public void success(T value);

  /** progress has been made, and the process has emitted a stage */
  public void progress(int stage);

  /** the action failed outright, and the reason is the exception */
  public void failure(int reason, Exception ex);

  public static <In, Out> DataCallback<In> transform(DataCallback<Out> output, int stage, Function<In, Out> f) {
    return new DataCallback<>() {
      @Override
      public void success(In value) {
        try {
          output.success(f.apply(value));
        } catch (Exception ex) {
          ex.printStackTrace();
          System.err.println("WTF!!!");
          output.failure(stage, ex);
        }
      }

      @Override
      public void progress(int stage) {
        output.progress(stage);
      }

      @Override
      public void failure(int code, Exception ex) {
        output.failure(code, ex);
      }
    };
  }

  public static <T> DataCallback<T> bind(ScheduledExecutorService service, DataCallback<T> next) {
    return new DataCallback<T>() {
      @Override
      public void success(T value) {
        service.execute(() -> {
          next.success(value);
        });
      }

      @Override
      public void progress(int stage) {
        service.execute(() -> {
          next.progress(stage);
        });
      }

      @Override
      public void failure(int code, Exception ex) {
        service.execute(() -> {
          next.failure(code, ex);
        });
      }
    };
  }

  public static <T> DataCallback<Void> handoff(DataCallback<T> next, int stage, Runnable success) {
    return new DataCallback<>() {
      @Override
      public void success(Void value) {
        try {
          success.run();
        } catch (Exception ex) {
          failure(stage, ex);
        }
      }

      @Override
      public void progress(int stage) {
        next.progress(stage);
      }

      @Override
      public void failure(int code, Exception ex) {
        next.failure(code, ex);
      }
    };
  }
}