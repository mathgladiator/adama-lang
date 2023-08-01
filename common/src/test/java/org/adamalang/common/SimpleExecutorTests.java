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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SimpleExecutorTests {
  @Test
  public void coverageNow() {
    SimpleExecutor.NOW.execute(new NamedRunnable("name") {
      @Override
      public void execute() throws Exception {

      }
    });
    SimpleExecutor.NOW.schedule(new NamedRunnable("name") {
      @Override
      public void execute() throws Exception {

      }
    }, 100).run();
    SimpleExecutor.NOW.scheduleNano(new NamedRunnable("nano") {
      @Override
      public void execute() throws Exception {

      }
    }, 100).run();
    SimpleExecutor.NOW.shutdown();
  }

  @Test
  public void create() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("base");
    CountDownLatch latchExec = new CountDownLatch(3);
    executor.execute(new NamedRunnable("name") {
      @Override
      public void execute() throws Exception {
        latchExec.countDown();
      }
    });
    Runnable cancel1 = executor.schedule(new NamedRunnable("name") {
      @Override
      public void execute() throws Exception {
        latchExec.countDown();
      }
    }, 10);
    Runnable cancel2 = executor.scheduleNano(new NamedRunnable("name") {
      @Override
      public void execute() throws Exception {
        latchExec.countDown();
      }
    }, 10);
    Assert.assertTrue(latchExec.await(1000, TimeUnit.MILLISECONDS));
    Assert.assertTrue(executor.shutdown().await(1000, TimeUnit.MILLISECONDS));
    cancel1.run();
    cancel2.run();
  }
}
