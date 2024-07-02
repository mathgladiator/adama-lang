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
package org.adamalang.runtime.async;

import org.adamalang.runtime.exceptions.ComputeBlockedException;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class SimpleFutureTests {
  @Test
  public void has_value() {
    final var sf = new SimpleFuture<>("chan", NtPrincipal.NO_ONE, "cake");
    Assert.assertTrue(sf.exists());
    Assert.assertEquals("cake", sf.await());
  }

  @Test
  public void no_value() {
    final var sf = new SimpleFuture<String>("chan", NtPrincipal.NO_ONE, null);
    Assert.assertFalse(sf.exists());
    try {
      sf.await();
      Assert.fail();
    } catch (final ComputeBlockedException cbe) {
    }
  }
}
