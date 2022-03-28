/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
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
  public void memory() {
    final var unknowns = new TreeSet<MockRecord>();
    final var index = new ReactiveIndex<>(unknowns);
    Assert.assertEquals(64, index.memory());
    index.add(42, MockRecord.make(1));
    Assert.assertEquals(104, index.memory());
  }
}
