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
package org.adamalang.mysql.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.LocationType;
import org.adamalang.runtime.data.DocumentLocation;
import org.junit.Assert;

public class SimpleFinderCallback implements Callback<DocumentLocation> {
  public DocumentLocation value;
  public int reads;
  private boolean success;
  private int count;
  private int reason;

  public SimpleFinderCallback() {
    this.success = false;
    this.count = 0;
    this.reason = 0;
    this.reads = 0;
  }

  @Override
  public void success(DocumentLocation value) {
    this.value = value;
    count++;
    success = true;
  }

  @Override
  public void failure(ErrorCodeException ex) {
    count++;
    success = false;
    reason = ex.code;
  }

  public void assertSuccess(LocationType location, String machine, String archiveKey) {
    Assert.assertEquals(1, count);
    Assert.assertTrue(success);
    Assert.assertEquals(location, value.location);
    Assert.assertEquals(machine, value.machine);
    if ("".equals(archiveKey) || archiveKey == null) {
      Assert.assertNull(value.archiveKey);
    } else {
      Assert.assertEquals(archiveKey, value.archiveKey);
    }
  }

  public long assertSuccessAndGetId() {
    Assert.assertEquals(1, count);
    Assert.assertTrue(success);
    return value.id;
  }

  public void assertFailure(int code) {
    Assert.assertEquals(1, count);
    Assert.assertFalse(success);
    Assert.assertEquals(code, this.reason);
  }
}
