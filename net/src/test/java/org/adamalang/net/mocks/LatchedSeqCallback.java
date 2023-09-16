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
package org.adamalang.net.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class LatchedSeqCallback implements Callback<Integer> {
  private final CountDownLatch latch;
  private boolean error;
  private int value;

  public LatchedSeqCallback() {
    latch = new CountDownLatch(1);
    this.error = false;
    this.value = 0;
  }

  @Override
  public void success(Integer seq) {
    this.error = false;
    this.value = seq;
    latch.countDown();
  }

  @Override
  public void failure(ErrorCodeException ex) {
    this.error = true;
    this.value = ex.code;
    latch.countDown();
  }

  public void assertSuccess(int v) {
    try {
      Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
      Assert.assertFalse(error);
      Assert.assertEquals(v, this.value);
    } catch (Exception ex) {
      Assert.fail();
    }
  }

  public void assertJustSuccess() {
    try {
      Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
      if (error) {
        System.err.println("EXPECTED NO ERROR, BUT GOT:" + value);
      }
      Assert.assertFalse(error);
    } catch (Exception ex) {
      Assert.fail();
    }
  }

  public void assertFail(int c) {
    try {
      Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(error);
      Assert.assertEquals(c, this.value);
    } catch (Exception ex) {
      Assert.fail();
    }
  }

  public void assertJustFail() {
    try {
      Assert.assertTrue(latch.await(2000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(error);
    } catch (Exception ex) {
      Assert.fail();
    }
  }
}
