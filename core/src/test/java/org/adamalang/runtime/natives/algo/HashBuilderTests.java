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
package org.adamalang.runtime.natives.algo;

import org.adamalang.runtime.natives.*;
import org.junit.Assert;
import org.junit.Test;

import java.time.ZonedDateTime;

public class HashBuilderTests {
  @Test
  public void coverage() {
    HashBuilder hash = new HashBuilder();
    hash.hashNtDynamic(new NtDynamic("{}"));
    hash.hashBoolean(true);
    hash.hashBoolean(false);
    hash.hashDouble(3.14);
    hash.hashNtComplex(new NtComplex(2.71, 3.14));
    hash.hashInteger(42);
    hash.hashNtAsset(NtAsset.NOTHING);
    hash.hashString("xyz");
    hash.hashLong(123451234342L);
    hash.hashNtPrincipal(NtPrincipal.NO_ONE);
    Assert.assertEquals("UYoxfE+b51ZODGPUCfJwIne7mNacxQ7YqHWDkO0SLbB3sUqLRzz3S5JNDtAggHf/", hash.finish());
  }

  @Test
  public void datetime() {
    HashBuilder hash = new HashBuilder();
    hash.hashNtDate(new NtDate(2023, 4, 1));
    hash.hashNtTime(new NtTime(13, 14));
    hash.hashNtTimeSpan(new NtTimeSpan(100));
    hash.hashNtDateTime(new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]")));
    Assert.assertEquals("vNvWxgbcpGdiCJxs0y5avo8q8FgEeSq+s58wqyBH1Ps79feA8rPpZypP9HE+/jxB", hash.finish());
  }

  @Test
  public void coverageReorder() {
    HashBuilder hash = new HashBuilder();
    hash.hashNtAsset(NtAsset.NOTHING);
    hash.hashLong(123451234342L);
    hash.hashNtPrincipal(NtPrincipal.NO_ONE);
    hash.hashDouble(3.14);
    hash.hashInteger(42);
    hash.hashString("xyz");
    hash.hashBoolean(false);
    hash.hashBoolean(true);
    hash.hashNtDynamic(new NtDynamic("{}"));
    hash.hashNtComplex(new NtComplex(2.71, 3.14));
    Assert.assertEquals("fwA2dTJTT6mFZKy7B9Nh2Ol1U6tbqQu0/5UCck6cHUp64vYyf6ZWNaT5LZlejRBg", hash.finish());
  }
}
