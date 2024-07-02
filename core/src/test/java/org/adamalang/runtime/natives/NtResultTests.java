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
package org.adamalang.runtime.natives;

import org.adamalang.runtime.exceptions.ComputeBlockedException;
import org.junit.Assert;
import org.junit.Test;

public class NtResultTests {
  @Test
  public void good() {
    NtResult<Integer> result = new NtResult<>(123, false, 0, null);
    Assert.assertEquals(123, (int) result.get());
    Assert.assertEquals(123, (int) result.as_maybe().get());
    Assert.assertTrue(result.finished());
    Assert.assertTrue(result.has());
    Assert.assertFalse(result.failed());
    Assert.assertEquals("OK", result.message());
    Assert.assertEquals(0, result.code());
    Assert.assertEquals(123, (int) result.await().get());
  }

  @Test
  public void copy_cons() {
    NtResult<Integer> result = new NtResult<>(new NtResult<>(123, false, 0, null));
    Assert.assertEquals(123, (int) result.get());
    Assert.assertEquals(123, (int) result.as_maybe().get());
    Assert.assertTrue(result.finished());
    Assert.assertTrue(result.has());
    Assert.assertFalse(result.failed());
    Assert.assertEquals("OK", result.message());
    Assert.assertEquals(0, result.code());
    Assert.assertEquals(123, (int) result.await().get());
  }

  @Test
  public void inprogress() {
    NtResult<Integer> result = new NtResult<>(null, false, 0, null);
    Assert.assertFalse(result.as_maybe().has());
    Assert.assertFalse(result.finished());
    Assert.assertFalse(result.failed());
    Assert.assertFalse(result.has());
    Assert.assertEquals("waiting...", result.message());
    Assert.assertEquals(0, result.code());
    try {
      result.await();
      Assert.fail();
    } catch (ComputeBlockedException cbe) {

    }
  }

  @Test
  public void bad() {
    NtResult<Integer> result = new NtResult<>(null, true, 500, "Failure");
    Assert.assertFalse(result.as_maybe().has());
    Assert.assertTrue(result.finished());
    Assert.assertTrue(result.failed());
    Assert.assertFalse(result.has());
    Assert.assertEquals("Failure", result.message());
    Assert.assertEquals(500, result.code());
    Assert.assertFalse(result.await().has());
  }
}
