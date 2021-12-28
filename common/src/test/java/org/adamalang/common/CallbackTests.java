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

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CallbackTests {

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

  private void waitFor(ScheduledExecutorService executor) throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    executor.execute(() -> latch.countDown());
    latch.await(1000, TimeUnit.MILLISECONDS);
  }

  @Test
  public void sanity() {
    MockCallback<Integer> callback = new MockCallback<Integer>();
    callback.success(50);
    Assert.assertEquals(50, (int) callback.result);
  }

  @Test
  public void bind_happy() throws Exception {
    MockCallback<Integer> callback = new MockCallback<Integer>();
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    Callback<Integer> bound = Callback.bind(executor, 40, callback);
    bound.success(42);
    waitFor(executor);
    Assert.assertEquals(42, (int) callback.result);
  }

  @Test
  public void bind_throws() throws Exception {
    MockCallback<Integer> callback = new MockCallback<Integer>();
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    Callback<Integer> bound = Callback.bind(executor, 40, callback);
    bound.failure(new ErrorCodeException(42));
    waitFor(executor);
    Assert.assertTrue(callback.exception instanceof ErrorCodeException);
    Assert.assertEquals(42, ((ErrorCodeException) callback.exception).code);
  }

  @Test
  public void bind_crash() throws Exception {
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    AtomicReference<ErrorCodeException> error = new AtomicReference<>();
    Callback<Integer> bound = Callback.bind(executor, 40, new Callback<Integer>() {
      @Override
      public void success(Integer value) {
        throw new RuntimeException("nope");
      }

      @Override
      public void failure(ErrorCodeException ex) {
        error.set(ex);
      }
    });
    bound.success(4000);
    waitFor(executor);
    Assert.assertTrue(error.get() instanceof ErrorCodeException);
    Assert.assertEquals(40, ((ErrorCodeException) error.get()).code);
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
  }
}
