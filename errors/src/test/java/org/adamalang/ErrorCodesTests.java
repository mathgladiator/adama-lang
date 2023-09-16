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
package org.adamalang;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashSet;

public class ErrorCodesTests {
  @Test
  public void coverage() {
    new ErrorCodes();
  }

  @Test
  public void no_dupes() throws Exception {
    HashSet<Integer> values = new HashSet<>();
    for (Field field : ErrorCodes.class.getFields()) {
      int v = (int) field.get(null);
      Assert.assertFalse("dupe:" + v, values.contains(v));
      values.add(v);
    }
  }
}
