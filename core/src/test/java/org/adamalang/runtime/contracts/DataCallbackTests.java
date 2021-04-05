package org.adamalang.runtime.contracts;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class DataCallbackTests {

  public class MockCallback<T> implements DataCallback<T> {
    public T result = null;
    public int progress = 0;
    public int failure = 0;
    public Exception exception;

    @Override
    public void success(T value) {
      result = value;
    }

    @Override
    public void progress(int stage) {
      progress = stage;
    }

    @Override
    public void failure(int stage, Exception ex) {
      failure = stage;
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
    t.failure(10, new RuntimeException());
    t.progress(500);
    Assert.assertEquals(10, callback.failure);
    Assert.assertEquals(500, callback.progress);
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
    Assert.assertEquals(5, callback.failure);
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
    t.failure(10, new RuntimeException());
    t.progress(500);
    Assert.assertEquals(10, callback.failure);
    Assert.assertEquals(500, callback.progress);
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
    Assert.assertEquals(5, callback.failure);
  }
}
