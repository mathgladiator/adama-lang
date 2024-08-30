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
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CallbackTests {

  @Test
  public void sanity() {
    MockCallback<Integer> callback = new MockCallback<Integer>();
    callback.success(50);
    Assert.assertEquals(50, (int) callback.result);
  }

  private void waitFor(ScheduledExecutorService executor) throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    executor.execute(() -> latch.countDown());
    latch.await(1000, TimeUnit.MILLISECONDS);
  }

  @Test
  public void transform() {
    MockCallback<Integer> callback = new MockCallback<Integer>();
    callback.success(50);
    Assert.assertEquals(50, (int) callback.result);
    Callback<Integer> t = Callback.transform(callback, 5, (x) -> x * x);
    t.success(10);
    Assert.assertEquals(100, (int) callback.result);
    t.failure(new ErrorCodeException(15, new Exception()));
  }

  @Test
  public void transform_failure() {
    MockCallback<Integer> callback = new MockCallback<Integer>();
    callback.success(50);
    Assert.assertEquals(50, (int) callback.result);
    Callback<Integer> t = Callback.transform(callback, 5, (x) -> {
      throw new NullPointerException();
    });
    t.success(10);
    Assert.assertEquals(5, callback.exception.code);
  }

  @Test
  public void handoff() {
    MockCallback<Integer> callback = new MockCallback<Integer>();
    callback.success(50);
    Assert.assertEquals(50, (int) callback.result);
    AtomicInteger i = new AtomicInteger(0);
    Callback<Void> t = Callback.handoff(callback, 5, () -> {
      i.set(42);
    });
    t.failure(new ErrorCodeException(15, new RuntimeException()));
    t.success(null);
    Assert.assertEquals(50, (int) callback.result);
    Assert.assertEquals(42, i.get());
  }

  @Test
  public void handoff_crash() {
    MockCallback<Integer> callback = new MockCallback<Integer>();
    callback.success(50);
    Assert.assertEquals(50, (int) callback.result);
    AtomicInteger i = new AtomicInteger(0);
    Callback<Void> t = Callback.handoff(callback, 5, () -> {
      throw new NullPointerException();
    });
    t.success(null);
    Assert.assertEquals(5, callback.exception.code);
  }

  @Test
  public void dontcare() {
    Callback.DONT_CARE_INTEGER.success(123);
    Callback.DONT_CARE_INTEGER.failure(new ErrorCodeException(123));
    Callback.DONT_CARE_VOID.success(null);
    Callback.DONT_CARE_VOID.failure(new ErrorCodeException(123));
    Callback.DONT_CARE_STRING.success("xyz");
    Callback.DONT_CARE_STRING.failure(new ErrorCodeException(123));
  }

  @Test
  public void throwaway() {
    AtomicInteger s = new AtomicInteger(0);
    AtomicInteger f = new AtomicInteger(0);
    Callback<Integer> xyz = Callback.SUCCESS_OR_FAILURE_THROW_AWAY_VALUE(new Callback<Void>() {
      @Override
      public void success(Void value) {
        s.incrementAndGet();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        f.incrementAndGet();
      }
    });
    Assert.assertEquals(0, s.get());
    xyz.success(123);
    Assert.assertEquals(1, s.get());
    Assert.assertEquals(0, f.get());
    xyz.failure(new ErrorCodeException(12));
    Assert.assertEquals(1, f.get());
  }

  public class MockCallback<T> implements Callback<T> {
    public T result = null;
    public ErrorCodeException exception;

    @Override
    public void success(T value) {
      result = value;
    }

    @Override
    public void failure(ErrorCodeException ex) {
      exception = ex;
    }
  }
}
