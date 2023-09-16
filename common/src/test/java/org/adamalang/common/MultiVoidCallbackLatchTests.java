/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
