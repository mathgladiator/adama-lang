package org.adamalang.api.operations;

import java.util.concurrent.atomic.AtomicInteger;

public class Total {
  private AtomicInteger value;

  public Total() {
    this.value = new AtomicInteger(0);
  }
  public void dec() {
    value.decrementAndGet();
  }

  public void inc() {
    value.incrementAndGet();
  }

  public void set(int val) {
    value.set(val);
  }

  public int get() {
    return value.get();
  }
}
