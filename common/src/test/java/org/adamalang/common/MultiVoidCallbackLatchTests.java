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

import java.util.concurrent.atomic.AtomicInteger;

public class MultiVoidCallbackLatchTests {
  @Test
  public void seven_success() {
    AtomicInteger successes = new AtomicInteger(0);
    AtomicInteger failures = new AtomicInteger(0);
    MultiVoidCallbackLatch latch = new MultiVoidCallbackLatch(new Callback<Void>() {
      @Override
      public void success(Void value) {
        successes.incrementAndGet();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        failures.incrementAndGet();
      }
    }, 7, 1000);

    Assert.assertEquals(0, successes.get());
    Assert.assertEquals(0, failures.get());
    latch.success();
    latch.success();
    latch.success();
    latch.success();
    latch.success();
    latch.success();
    Assert.assertEquals(0, successes.get());
    Assert.assertEquals(0, failures.get());
    latch.success();
    Assert.assertEquals(1, successes.get());
    Assert.assertEquals(0, failures.get());
    latch.success();
    latch.success();
    latch.success();
    Assert.assertEquals(1, successes.get());
    Assert.assertEquals(0, failures.get());
  }

  @Test
  public void six_success_one_failure() {
    AtomicInteger successes = new AtomicInteger(0);
    AtomicInteger failures = new AtomicInteger(0);
    MultiVoidCallbackLatch latch = new MultiVoidCallbackLatch(new Callback<Void>() {
      @Override
      public void success(Void value) {
        successes.incrementAndGet();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        failures.incrementAndGet();
      }
    }, 7, 1000);

    Assert.assertEquals(0, successes.get());
    Assert.assertEquals(0, failures.get());
    latch.success();
    latch.success();
    latch.success();
    latch.success();
    latch.success();
    Assert.assertEquals(0, successes.get());
    Assert.assertEquals(0, failures.get());
    latch.failure();
    Assert.assertEquals(0, successes.get());
    Assert.assertEquals(1, failures.get());
    latch.success();
    latch.success();
    latch.success();
    Assert.assertEquals(0, successes.get());
    Assert.assertEquals(1, failures.get());
  }


  @Test
  public void three_success_four_failure() {
    AtomicInteger successes = new AtomicInteger(0);
    AtomicInteger failures = new AtomicInteger(0);
    MultiVoidCallbackLatch latch = new MultiVoidCallbackLatch(new Callback<Void>() {
      @Override
      public void success(Void value) {
        successes.incrementAndGet();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        failures.incrementAndGet();
      }
    }, 7, 1000);

    Assert.assertEquals(0, successes.get());
    Assert.assertEquals(0, failures.get());
    latch.success();
    latch.failure();
    Assert.assertEquals(0, successes.get());
    Assert.assertEquals(1, failures.get());
    latch.failure();
    latch.failure();
    latch.success();
    latch.failure();
    Assert.assertEquals(0, successes.get());
    Assert.assertEquals(1, failures.get());
    latch.success();
    latch.success();
    latch.success();
    Assert.assertEquals(0, successes.get());
    Assert.assertEquals(1, failures.get());
  }

  @Test
  public void seven_failures() {
    AtomicInteger successes = new AtomicInteger(0);
    AtomicInteger failures = new AtomicInteger(0);
    MultiVoidCallbackLatch latch = new MultiVoidCallbackLatch(new Callback<Void>() {
      @Override
      public void success(Void value) {
        successes.incrementAndGet();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        failures.incrementAndGet();
      }
    }, 7, 1000);

    Assert.assertEquals(0, successes.get());
    Assert.assertEquals(0, failures.get());
    latch.failure();
    Assert.assertEquals(0, successes.get());
    Assert.assertEquals(1, failures.get());
    latch.failure();
    latch.failure();
    latch.failure();
    latch.failure();
    latch.failure();
    Assert.assertEquals(0, successes.get());
    Assert.assertEquals(1, failures.get());
  }
}
