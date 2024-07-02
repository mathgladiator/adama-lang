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
package org.adamalang.mysql.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.junit.Assert;

public class SimpleLongCallback implements Callback<Long> {
  public Long value;
  private boolean success;
  private int count;
  private int reason;

  public SimpleLongCallback() {
    this.value = null;
    this.success = false;
    this.count = 0;
    this.reason = -1;
  }

  @Override
  public void success(Long value) {
    this.value = value;
    this.success = true;
    this.count++;
  }

  @Override
  public void failure(ErrorCodeException ex) {
    this.reason = ex.code;
    this.success = false;
    this.count++;
  }

  public void assertSuccess(long value) {
    Assert.assertEquals(1, count);
    Assert.assertTrue(success);
    Assert.assertEquals(value, (long) this.value);
  }

  public void assertFailure(int code) {
    Assert.assertEquals(1, count);
    Assert.assertFalse(success);
    Assert.assertEquals(code, this.reason);
  }
}
