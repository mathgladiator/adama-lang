package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class NamedRunnableTests {
  @Test
  public void coverage() {
    AtomicInteger x = new AtomicInteger(0);
    NamedRunnable runnable = new NamedRunnable("me") {
      @Override
      public void execute() throws Exception {
        if (x.incrementAndGet() == 3) {
          throw new Exception("huh");
        }
      }
    };
    Assert.assertEquals("me", runnable.toString());
    runnable.run();
    runnable.run();
    runnable.run();
    runnable.run();
  }
}
