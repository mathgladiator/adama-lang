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

import org.adamalang.runtime.mocks.MockMessage;
import org.junit.Assert;
import org.junit.Test;

public class NtTableTests {
  @Test
  public void flow() {
    final var table = new NtTable<>(MockMessage::new);
    new NtTable<>(table);
    table.make();
    Assert.assertEquals(1, table.size());
    table.make();
    Assert.assertEquals(2, table.size());
    table.delete();
    Assert.assertEquals(0, table.size());
    Assert.assertEquals(0, table.iterate(false).size());
    table.make();
    table.make();
    table.make();
    Assert.assertEquals(3, table.iterate(false).size());
    table.__raiseInvalid();
  }
}
