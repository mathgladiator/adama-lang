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
package org.adamalang.runtime.sys.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class NullCallbackLatch implements Callback<Void> {
  private final CountDownLatch latch;
  private final AtomicBoolean failed;
  private final AtomicInteger failed_code;

  public NullCallbackLatch() {
    this.latch = new CountDownLatch(1);
    this.failed = new AtomicBoolean(false);
    this.failed_code = new AtomicInteger(0);
  }

  @Override
  public void success(Void value) {
    latch.countDown();
  }

  @Override
  public void failure(ErrorCodeException ex) {
    failed_code.set(ex.code);
    failed.set(true);
    latch.countDown();
  }

  public void await_success() throws Exception {
    Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
    Assert.assertFalse(failed.get());
  }

  public void await_failure() throws Exception {
    Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
    Assert.assertTrue(failed.get());
  }

  public void await_failure(int code) throws Exception {
    Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
    Assert.assertTrue(failed.get());
    Assert.assertEquals(code, failed_code.get());
  }
}
