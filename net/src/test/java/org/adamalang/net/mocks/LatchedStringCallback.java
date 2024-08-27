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

public class LatchedStringCallback implements Callback<String> {
  private final CountDownLatch latch;
  private boolean error;
  private String value;

  public LatchedStringCallback() {
    latch = new CountDownLatch(1);
    this.error = false;
    this.value = "";
  }

  @Override
  public void success(String s) {
    this.error = false;
    this.value = s;
    latch.countDown();
  }

  @Override
  public void failure(ErrorCodeException ex) {
    this.error = true;
    this.value = "" + ex.code;
    latch.countDown();
  }

  public void assertSuccess(String v) {
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

  public void assertFail(String c) {
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
