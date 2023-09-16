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
package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class LibPrincipalTests {
  @Test
  public void is() {
    NtPrincipal a = new NtPrincipal("jeff", "adama");
    Assert.assertTrue(LibPrincipal.isAdamaDeveloper(a));
    Assert.assertFalse(LibPrincipal.isAdamaDeveloper(NtPrincipal.NO_ONE));
    Assert.assertTrue(LibPrincipal.fromAuthority(a, "adama"));
    Assert.assertFalse(LibPrincipal.fromAuthority(a, "x"));
    Assert.assertTrue(LibPrincipal.isAnonymous(new NtPrincipal("agent", "anonymous")));
    Assert.assertFalse(LibPrincipal.isAnonymous(new NtPrincipal("agent", "adama")));
  }
}
