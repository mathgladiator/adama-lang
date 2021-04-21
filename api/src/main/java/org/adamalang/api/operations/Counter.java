package org.adamalang.api.operations;

import java.util.concurrent.atomic.AtomicInteger;

/** a single counter */
public class Counter {
  private AtomicInteger value;

  public Counter() {
    this.value = new AtomicInteger(0);
  }

  public void bump() {
    value.incrementAndGet();
  }

  public int getAndReset() {
    int val = value.get();
    value.addAndGet(-val);
    return val;
  }
}
