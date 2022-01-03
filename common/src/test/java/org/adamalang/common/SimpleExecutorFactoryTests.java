package org.adamalang.common;

import org.junit.Test;

public class SimpleExecutorFactoryTests {
  @Test
  public void coverage() {
    SimpleExecutorFactory.DEFAULT.makeSingle("one").shutdown();
    for (SimpleExecutor executor : SimpleExecutorFactory.DEFAULT.makeMany("whoop", 2)) {
      executor.shutdown();
    }
  }
}
