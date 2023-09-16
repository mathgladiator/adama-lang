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
package org.adamalang.runtime.natives;

import org.junit.Assert;
import org.junit.Test;

public class NtAssetTests {
  @Test
  public void flow() {
    NtAsset a = new NtAsset("123", "name", "png", 42, "hash", "sheesh");
    NtAsset b = new NtAsset("42", "name", "png", 42, "hash", "sheesh");

    Assert.assertEquals(-3, a.compareTo(b));
    Assert.assertEquals(3, b.compareTo(a));
    Assert.assertEquals(-973748297, a.hashCode());
    Assert.assertTrue(a.equals(a));
    Assert.assertFalse(a.equals(""));
    Assert.assertFalse(a.equals(b));
    Assert.assertEquals("123", a.id());
    Assert.assertEquals("name", a.name());
    Assert.assertEquals("png", a.type());
    Assert.assertEquals(42, a.size());
    Assert.assertTrue(a.valid());
    Assert.assertEquals(88, a.memory());
    Assert.assertEquals("hash", a.md5());
    Assert.assertEquals("sheesh", a.sha384());
  }
}
