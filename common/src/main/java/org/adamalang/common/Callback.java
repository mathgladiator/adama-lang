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

import java.util.concurrent.CountDownLatch;
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

  public static Callback<Void> FINISHED_LATCH_DONT_CARE_VOID(CountDownLatch latch) {
    return new Callback<Void>() {
      @Override
      public void success(Void value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        latch.countDown();
      }
    };
  }

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
