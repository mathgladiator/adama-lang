package org.adamalang.api.operations;

import org.junit.Test;

public class CounterFactoryTests {
  @Test
  public void flow() {
    CounterFactory counterFactory = new CounterFactory();
    Counter c = counterFactory.makeCounter("c");
    c.bump();
    LatencyDistribution l = counterFactory.makeLatencyDistribution("l");
    l.register(42);
    Total t = counterFactory.makeTotalTracker("t");
    t.set(2);
  }
}
