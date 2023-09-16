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
package org.adamalang.net.client.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.LocalDocumentChange;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SimpleDataCallback implements Callback<LocalDocumentChange> {
  public String value;
  private boolean success;
  private int count;
  private int reason;
  public int reads;
  private CountDownLatch latch;

  public SimpleDataCallback() {
    this.success = false;
    this.count = 0;
    this.reason = 0;
    this.reads = 0;
    latch = new CountDownLatch(1);
  }

  @Override
  public void success(LocalDocumentChange value) {
    this.value = value.patch;
    count++;
    success = true;
    this.reads = value.reads;
    latch.countDown();
  }

  @Override
  public void failure(ErrorCodeException ex) {
    count++;
    success = false;
    reason = ex.code;
    latch.countDown();
  }

  public void assertSuccess() throws Exception {
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    synchronized (this) {
      Assert.assertEquals(1, count);
      Assert.assertTrue(success);
    }
  }

  public void assertFailure(int code) throws Exception {
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    synchronized (this) {
      Assert.assertEquals(1, count);
      Assert.assertFalse(success);
      Assert.assertEquals(code, this.reason);
    }
  }
}
