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
package org.adamalang.runtime.index;

import org.adamalang.runtime.mocks.MockRecord;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeSet;

public class ReactiveIndexInvalidatorTests {
  @Test
  public void flow1() {
    final var unknowns = new TreeSet<MockRecord>();
    final var index = new ReactiveIndex<>(unknowns);
    unknowns.add(MockRecord.make(123));
    final ReactiveIndexInvalidator<MockRecord> inv =
        new ReactiveIndexInvalidator<>(index, MockRecord.make(123)) {
          @Override
          public int pullValue() {
            return 42;
          }
        };
    inv.reindex();
    unknowns.clear();
    Assert.assertEquals(0, unknowns.size());
    inv.__raiseInvalid();
    Assert.assertEquals(1, unknowns.size());
    inv.deindex();
    Assert.assertEquals(0, unknowns.size());
  }

  @Test
  public void flow2() {
    final var unknowns = new TreeSet<MockRecord>();
    final var index = new ReactiveIndex<>(unknowns);
    unknowns.add(MockRecord.make(123));
    final ReactiveIndexInvalidator<MockRecord> inv =
        new ReactiveIndexInvalidator<>(index, MockRecord.make(123)) {
          @Override
          public int pullValue() {
            return 42;
          }
        };
    inv.reindex();
    unknowns.clear();
    inv.deindex();
    Assert.assertEquals(0, unknowns.size());
  }

  @Test
  public void flow3() {
    final var unknowns = new TreeSet<MockRecord>();
    final var index = new ReactiveIndex<>(unknowns);
    unknowns.add(MockRecord.make(123));
    final ReactiveIndexInvalidator<MockRecord> inv =
        new ReactiveIndexInvalidator<>(index, MockRecord.make(123)) {
          @Override
          public int pullValue() {
            return 42;
          }
        };
    Assert.assertEquals(1, unknowns.size());
    inv.deindex();
    Assert.assertEquals(0, unknowns.size());
  }
}
