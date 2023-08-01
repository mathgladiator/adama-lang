/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedRunnableTests {
  @Test
  public void coverageLongName() {
    AtomicInteger x = new AtomicInteger(0);
    NamedRunnable runnable = new NamedRunnable("me", "too", "tired") {
      @Override
      public void execute() throws Exception {
        if (x.incrementAndGet() == 3) {
          throw new Exception("huh");
        }
      }
    };
    Assert.assertEquals("me/too/tired", runnable.toString());
    runnable.run();
    runnable.run();
    runnable.run();
    runnable.run();
  }

  @Test
  public void noisy() {
    Assert.assertFalse(NamedRunnable.noisy(new RuntimeException()));
    Assert.assertFalse(NamedRunnable.noisy(new Exception()));
    Assert.assertTrue(NamedRunnable.noisy(new RejectedExecutionException()));
  }

  @Test
  public void coverageSingle() {
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
