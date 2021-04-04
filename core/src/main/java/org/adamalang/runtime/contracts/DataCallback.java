/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.contracts;

import java.util.function.Consumer;
import java.util.function.Function;

/** This callback interface is used by DataService such that actions not only succeed/fail, but provide
 * progress notifications which could relate to a state diagram. */
public interface DataCallback<T> {
  /** the action happened successfully, and the result is value */
  public void success(T value);

  /** progress has been made, and the process has emitted a stage */
  public void progress(int stage);

  /** the action failed outright, and the reason is the exception */
  public void failure(int stage, Exception ex);

  public static <In, Out> DataCallback<In> transform(DataCallback<Out> output, int stage, Function<In, Out> f) {
    return new DataCallback<>() {
      @Override
      public void success(In value) {
        Out result;
        try {
          result = f.apply(value);
        } catch (Exception ex) {
          output.failure(stage, ex);
          return;
        }
        output.success(result);
      }

      @Override
      public void progress(int stage) {
        output.progress(stage);
      }

      @Override
      public void failure(int stage, Exception ex) {
        output.failure(stage, ex);
      }
    };
  }
}