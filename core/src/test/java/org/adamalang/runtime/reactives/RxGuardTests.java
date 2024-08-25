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
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.mocks.MockRecord;
import org.adamalang.runtime.mocks.MockRxChild;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class RxGuardTests {
  @Test
  public void dump() {
    final var d = new RxGuard(null);
    final var writer = new JsonStreamWriter();
    d.__dump(writer);
    Assert.assertEquals("", writer.toString());
  }

  @Test
  public void flow() {
    final var guard = new RxGuard(null);
    Assert.assertEquals(1, guard.getGeneration(0));
    Assert.assertEquals(true, guard.__invalid);
    guard.__settle(null);
    Assert.assertEquals(1, guard.getGeneration(0));
    Assert.assertEquals(false, guard.__invalid);
    final var child = new MockRxChild();
    guard.__subscribe(child);
    guard.__raiseInvalid();
    child.assertInvalidateCount(1);
    Assert.assertEquals(65522, guard.getGeneration(0));
    Assert.assertEquals(true, guard.__invalid);
    guard.__settle(null);
    Assert.assertEquals(false, guard.__invalid);
    Assert.assertEquals(65522, guard.getGeneration(0));
    guard.__insert(new JsonStreamReader("{}"));
    guard.__patch(new JsonStreamReader("{}"));
    guard.__commit(null, null, null);
    guard.__revert();
    guard.__dump(null);
    Assert.assertEquals(64, guard.__memory());
  }

  @Test
  public void preventDeadlock() {
    AtomicReference<RxGuard> self = new AtomicReference<>();
    RxGuard g = new RxGuard(new RxParent() {
      @Override
      public void __raiseDirty() {
        self.get().__raiseInvalid();
      }

      @Override
      public boolean __isAlive() {
        return false;
      }

      @Override
      public void __cost(int cost) {
      }

      @Override
      public void __invalidateUp() {
      }

      @Override
      public void __settle(Set<Integer> viewers) {
      }
    });
    self.set(g);
    g.__raiseInvalid();
    Assert.assertEquals(64, g.__memory());
  }

  @Test
  public void inheritRecordId() {
    MockRecord record = new MockRecord(null);
    RxGuard g = new RxGuard(record);
    g.__raiseInvalid();
    Assert.assertEquals(1, g.getGeneration(0));
    Assert.assertEquals(64, g.__memory());
  }
}
