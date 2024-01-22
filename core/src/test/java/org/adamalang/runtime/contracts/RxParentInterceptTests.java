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
package org.adamalang.runtime.contracts;

import org.adamalang.runtime.mocks.MockRxParent;
import org.junit.Assert;
import org.junit.Test;

public class RxParentInterceptTests {
  @Test
  public void coverage() {
    MockRxParent parent = new MockRxParent();
    RxParentIntercept intercept = new RxParentIntercept(parent) {
      @Override
      public void __invalidateUp() {

      }
    };
    intercept.__cost(100);
    Assert.assertTrue(intercept.__isAlive());
    intercept.__raiseDirty();
    intercept.__settle(null);
    intercept.__invalidateUp();
    parent.alive = false;
    Assert.assertFalse(intercept.__isAlive());
  }
}
