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
package org.adamalang.runtime.index;

import org.adamalang.runtime.contracts.IndexQuerySet;
import org.adamalang.runtime.mocks.MockRecord;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeSet;

public class ReactiveIndexTests {
  @Test
  public void del() {
    final var unknowns = new TreeSet<MockRecord>();
    final var index = new ReactiveIndex<>(unknowns);
    unknowns.add(MockRecord.make(123));
    Assert.assertEquals(1, unknowns.size());
    index.delete(MockRecord.make(123));
    Assert.assertEquals(0, unknowns.size());
  }

  @Test
  public void flow() {
    final var unknowns = new TreeSet<MockRecord>();
    final var index = new ReactiveIndex<>(unknowns);
    Assert.assertNull(index.of(42, IndexQuerySet.LookupMode.Equals));
    index.add(42, MockRecord.make(1));
    Assert.assertEquals(1, index.of(42, IndexQuerySet.LookupMode.Equals).size());
    index.add(42, MockRecord.make(12));
    Assert.assertEquals(2, index.of(42, IndexQuerySet.LookupMode.Equals).size());
    Assert.assertFalse(unknowns.contains(MockRecord.make(12)));
    Assert.assertFalse(unknowns.contains(MockRecord.make(1)));
    index.remove(42, MockRecord.make(12));
    Assert.assertTrue(unknowns.contains(MockRecord.make(12)));
    Assert.assertEquals(1, index.of(42, IndexQuerySet.LookupMode.Equals).size());
    index.remove(42, MockRecord.make(1));
    Assert.assertNull(index.of(42, IndexQuerySet.LookupMode.Equals));
    Assert.assertTrue(unknowns.contains(MockRecord.make(1)));
  }

  @Test
  public void flow_lessthan() {
    final var unknowns = new TreeSet<MockRecord>();
    final var index = new ReactiveIndex<>(unknowns);
    Assert.assertNull(index.of(42, IndexQuerySet.LookupMode.LessThan));
    index.add(42, MockRecord.make(1));
    Assert.assertEquals(1, index.of(44, IndexQuerySet.LookupMode.LessThan).size());
    Assert.assertNull(index.of(40, IndexQuerySet.LookupMode.LessThan));
  }

  @Test
  public void flow_lessthan_eq() {
    final var unknowns = new TreeSet<MockRecord>();
    final var index = new ReactiveIndex<>(unknowns);

    Assert.assertNull(index.of(42, IndexQuerySet.LookupMode.LessThanOrEqual));
    index.add(42, MockRecord.make(1));
    Assert.assertEquals(1, index.of(44, IndexQuerySet.LookupMode.LessThanOrEqual).size());
    Assert.assertNull(index.of(40, IndexQuerySet.LookupMode.LessThanOrEqual));
  }

  @Test
  public void flow_greaterthan() {
    final var unknowns = new TreeSet<MockRecord>();
    final var index = new ReactiveIndex<>(unknowns);
    Assert.assertNull(index.of(42, IndexQuerySet.LookupMode.GreaterThan));
    index.add(42, MockRecord.make(1));
    Assert.assertEquals(1, index.of(40, IndexQuerySet.LookupMode.GreaterThan).size());
    Assert.assertNull(index.of(42, IndexQuerySet.LookupMode.GreaterThan));
  }

  @Test
  public void flow_greaterthan_eq() {
    final var unknowns = new TreeSet<MockRecord>();
    final var index = new ReactiveIndex<>(unknowns);
    Assert.assertNull(index.of(42, IndexQuerySet.LookupMode.GreaterThanOrEqual));
    index.add(42, MockRecord.make(1));
    Assert.assertEquals(1, index.of(40, IndexQuerySet.LookupMode.GreaterThanOrEqual).size());
    Assert.assertNull(index.of(44, IndexQuerySet.LookupMode.GreaterThanOrEqual));
  }

  @Test
  public void memory() {
    final var unknowns = new TreeSet<MockRecord>();
    final var index = new ReactiveIndex<>(unknowns);
    Assert.assertEquals(64, index.memory());
    index.add(42, MockRecord.make(1));
    Assert.assertEquals(104, index.memory());
  }
}
