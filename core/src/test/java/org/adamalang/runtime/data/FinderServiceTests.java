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
package org.adamalang.runtime.data;

import org.junit.Assert;
import org.junit.Test;

public class FinderServiceTests {

  @Test
  public void location_tests() {
    Assert.assertEquals(2, LocationType.Machine.type);
    Assert.assertEquals(4, LocationType.Archive.type);
    Assert.assertEquals(LocationType.Machine, LocationType.fromType(2));
    Assert.assertEquals(LocationType.Archive, LocationType.fromType(4));
    Assert.assertNull(LocationType.fromType(0));
  }

  @Test
  public void result_coverage() {
    new DocumentLocation(1L, LocationType.Machine, "region", "value", "archive", false);
  }
}
