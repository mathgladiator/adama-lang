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
    Assert.assertNull(index.of(42));
    index.add(42, MockRecord.make(1));
    Assert.assertEquals(1, index.of(42).size());
    index.add(42, MockRecord.make(12));
    Assert.assertEquals(2, index.of(42).size());
    Assert.assertFalse(unknowns.contains(MockRecord.make(12)));
    Assert.assertFalse(unknowns.contains(MockRecord.make(1)));
    index.remove(42, MockRecord.make(12));
    Assert.assertTrue(unknowns.contains(MockRecord.make(12)));
    Assert.assertEquals(1, index.of(42).size());
    index.remove(42, MockRecord.make(1));
    Assert.assertNull(index.of(42));
    Assert.assertTrue(unknowns.contains(MockRecord.make(1)));
  }
}
