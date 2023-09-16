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
package org.adamalang.support.testgen;

import org.junit.Assert;
import org.junit.Test;

public class TestFileTests {
  @Test
  public void happy() {
    final var tst = new TestFile("Z", "x", true);
    Assert.assertEquals("Z_x_success.a", tst.filename());
    final var tst2 = TestFile.fromFilename(tst.filename());
    Assert.assertEquals("Z", tst.clazz);
    Assert.assertEquals("x", tst.name);
    Assert.assertTrue(tst2.success);
  }

  @Test
  public void sad() {
    final var tst = new TestFile("Z", "x", false);
    Assert.assertEquals("Z_x_failure.a", tst.filename());
    final var tst2 = TestFile.fromFilename(tst.filename());
    Assert.assertEquals("Z", tst2.clazz);
    Assert.assertEquals("x", tst2.name);
    Assert.assertFalse(tst2.success);
  }

  @Test
  public void failure() {
    try {
      new TestFile("Z", "x_x", false);
      Assert.fail();
    } catch (Exception ex) {
      Assert.assertTrue(ex.getMessage().contains("underscore"));
    }
  }
}
