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
package org.adamalang.runtime.natives.algo;

import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtTemplate;
import org.junit.Assert;
import org.junit.Test;

public class SizingTests {
  @Test
  public void flow() {
    Assert.assertEquals(86, Sizing.memoryOf("XYZ"));
    Assert.assertEquals(136, Sizing.memoryOf(new NtAsset("id", "name", "type", 42, "md5", "sha")));
    Assert.assertEquals(88, Sizing.memoryOf(new NtDynamic("{}")));
    Assert.assertEquals(148, Sizing.memoryOf(new NtPrincipal("agent", "authority")));
    Assert.assertEquals(345, Sizing.memoryOf(new NtTemplate("xyz[[msg]]")));
  }
}
