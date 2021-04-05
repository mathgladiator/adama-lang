package org.adamalang.runtime.contracts;

import org.adamalang.runtime.exceptions.ErrorCodeException;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class DataCallbackTests {

  public class MockCallback<T> implements DataCallback<T> {
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

  @Test
  public void sanity() {
    MockCallback<Integer> callback = new MockCallback<Integer>();
    callback.success(50);
    Assert.assertEquals(50, (int) callback.result);
  }

  @Test
  public void transform() {
    MockCallback<Integer> callback = new MockCallback<Integer>();
    callback.success(50);
    Assert.assertEquals(50, (int) callback.result);
    DataCallback<Integer> t = DataCallback.transform(callback, 5, (x) -> x * x);
    t.success(10);
    Assert.assertEquals(100, (int) callback.result);
    t.failure(new ErrorCodeException(15, new Exception()));
  }

  @Test
  public void transform_failure() {
    MockCallback<Integer> callback = new MockCallback<Integer>();
    callback.success(50);
    Assert.assertEquals(50, (int) callback.result);
    DataCallback<Integer> t = DataCallback.transform(callback, 5, (x) -> {
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
    DataCallback<Void> t = DataCallback.handoff(callback, 5, () -> {
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
    DataCallback<Void> t = DataCallback.handoff(callback, 5, () -> {
      throw new NullPointerException();
    });
    t.success(null);
    Assert.assertEquals(5, callback.exception.code);
  }
}
