/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.index;

import java.util.TreeSet;
import org.adamalang.runtime.mocks.MockRecord;
import org.junit.Assert;
import org.junit.Test;

public class ReactiveIndexInvalidatorTests {
  @Test
  public void flow1() {
    final var unknowns = new TreeSet<MockRecord>();
    final var index = new ReactiveIndex<>(unknowns);
    unknowns.add(MockRecord.make(123));
    final ReactiveIndexInvalidator<MockRecord> inv = new ReactiveIndexInvalidator<>(index, MockRecord.make(123)) {
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
    final ReactiveIndexInvalidator<MockRecord> inv = new ReactiveIndexInvalidator<>(index, MockRecord.make(123)) {
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
    final ReactiveIndexInvalidator<MockRecord> inv = new ReactiveIndexInvalidator<>(index, MockRecord.make(123)) {
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
