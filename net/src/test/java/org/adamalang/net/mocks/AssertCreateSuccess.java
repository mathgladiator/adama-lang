/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

public class AssertCreateSuccess implements Callback<Void> {
  private final CountDownLatch latch;
  private boolean success;

  public AssertCreateSuccess() {
    this.latch = new CountDownLatch(1);
    this.success = false;
  }

  @Override
  public void success(Void value) {
    success = true;
    latch.countDown();
  }

  @Override
  public void failure(ErrorCodeException ex) {
    latch.countDown();
  }

  public void await() {
    try {
      Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
      Assert.assertTrue(success);
    } catch (InterruptedException ie) {
      Assert.fail();
    }
  }
}
