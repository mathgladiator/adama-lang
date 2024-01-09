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
package org.adamalang.runtime.mocks;

import org.adamalang.runtime.contracts.RxParent;
import org.junit.Assert;

import java.util.Set;

public class MockRxParent implements RxParent {
  public int dirtyCount;
  public boolean alive;
  public int cost;
  public int settleCount;
  public int invalidateUpCalls;

  public MockRxParent() {
    dirtyCount = 0;
    alive = true;
    settleCount = 0;
    invalidateUpCalls = 0;
  }

  @Override
  public void __raiseDirty() {
    dirtyCount++;
  }

  public void assertDirtyCount(final int expected) {
    Assert.assertEquals(expected, dirtyCount);
  }

  @Override
  public boolean __isAlive() {
    return alive;
  }

  @Override
  public void __cost(int cost) {
    this.cost += cost;
  }


  @Override
  public void __invalidateUp() {
    invalidateUpCalls++;
  }

  @Override
  public void __settle(Set<Integer> viewers) {
    settleCount++;
  }
}
