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
package org.adamalang.runtime.contracts;

import org.junit.Assert;
import org.junit.Test;

public class RxParentTests {
  @Test
  public void coverage() {
    Assert.assertFalse(RxParent.DEAD.__isAlive());
    RxParent.DEAD.__cost(0);
    RxParent.DEAD.__settle(null);
    RxParent.DEAD.__raiseDirty();
    RxParent.DEAD.__invalidateUp();
  }
}
