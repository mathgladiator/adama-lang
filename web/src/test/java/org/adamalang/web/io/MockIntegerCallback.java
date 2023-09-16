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
package org.adamalang.web.io;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MockIntegerCallback implements Callback<Integer> {
  public Integer response;
  public ErrorCodeException ex;
  private CountDownLatch done;

  public MockIntegerCallback() {
    this.done = new CountDownLatch(1);
  }

  public void awaitDone() throws Exception {
    Assert.assertTrue(done.await(1000, TimeUnit.MILLISECONDS));
  }

  public void assertErrorCode(int code) {
    Assert.assertNull(response);
    Assert.assertNotNull(ex);
    Assert.assertEquals(code, ex.code);
  }

  public void assertValue(int code) {
    Assert.assertNull(ex);
    Assert.assertNotNull(response);
    Assert.assertEquals(code, (int) response);
  }

  @Override
  public void success(Integer value) {
    this.response = value;
    done.countDown();
  }

  @Override
  public void failure(ErrorCodeException ex) {
    this.ex = ex;
    done.countDown();
  }
}
