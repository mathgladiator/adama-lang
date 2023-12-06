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

public class LatchCallback implements Callback<Integer> {

  private CountDownLatch latch;
  private int value;
  private ErrorCodeException ex;

  public LatchCallback() {
    this.latch = new CountDownLatch(1);
    this.value = 0;
    this.ex = null;
  }

  public Callback<Boolean> toBool(int trueValue, int falseValue) {
    return new Callback<>() {
      @Override
      public void success(Boolean value) {
        LatchCallback.this.success(value ? trueValue : falseValue);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        LatchCallback.this.failure(ex);
      }
    };
  }

  @Override
  public void success(Integer value) {
    this.value = value;
    latch.countDown();
  }

  @Override
  public void failure(ErrorCodeException ex) {
    this.ex = ex;
    latch.countDown();
  }

  public void await_success(int value) {
    try {
      Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
      Assert.assertEquals(value, this.value);
    } catch (InterruptedException ie) {
      Assert.fail();
    }
  }

  public void awaitJustSuccess() {
    try {
      Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
      Assert.assertNull(ex);
    } catch (InterruptedException ie) {
      Assert.fail();
    }
  }

  public void await_failure(int code) {
    try {
      Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
      Assert.assertEquals(code, ex.code);
    } catch (InterruptedException ie) {
      Assert.fail();
    }
  }
}
