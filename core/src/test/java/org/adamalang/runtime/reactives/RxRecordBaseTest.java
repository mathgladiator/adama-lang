/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.mocks.MockRecord;
import org.adamalang.runtime.mocks.MockRxChild;
import org.adamalang.runtime.mocks.MockRxParent;
import org.junit.Assert;
import org.junit.Test;

public class RxRecordBaseTest {
  @Test
  public void cmp() {
    final var a = new MockRecord(null);
    final var b = new MockRecord(null);
    a.id = 1;
    b.id = 2;
    Assert.assertEquals(-1, a.compareTo(b));
    Assert.assertEquals(1, b.compareTo(a));
    Assert.assertFalse(a.equals(b));
    a.__invalidateSubscribers();
    a.__raiseInvalid();
  }

  @Test
  public void memory() {
    final var a = new MockRecord(null);
    Assert.assertEquals(42, a.__memory());
  }

  @Test
  public void sanity() {
    final var mr = new MockRecord(null);
    final var child = new MockRxChild();
    mr.__subscribe(child);
    mr.id = 123;
    mr.__id();
    mr.__getIndexColumns();
    mr.__getIndexValues();
    mr.__name();
    mr.__reindex();
    mr.__deindex();
    mr.__commit(null, new JsonStreamWriter(), new JsonStreamWriter());
    mr.__revert();
    mr.__raiseDirty();
    Assert.assertTrue(mr.__isDirty());
    mr.__lowerDirtyCommit();
    Assert.assertFalse(mr.__isDirty());
    mr.__raiseDirty();
    mr.__lowerDirtyRevert();
    Assert.assertFalse(mr.__isDirty());
    mr.__delete();
    Assert.assertTrue(mr.__isDirty());
    Assert.assertTrue(mr.__isDying());
    child.assertInvalidateCount(4);
    Assert.assertEquals(0, mr.compareTo(mr));
    Assert.assertEquals(123, mr.hashCode());
    Assert.assertTrue(mr.equals(mr));
    Assert.assertFalse(mr.equals(null));
  }

  @Test
  public void alive_without_parent() {
    final var mr = new MockRecord(null);
    Assert.assertTrue(mr.__isAlive());
    mr.__kill();
    Assert.assertFalse(mr.__isAlive());
  }

  @Test
  public void alive_with_parent() {
    MockRxParent parent = new MockRxParent();
    final var mr = new MockRecord(parent);
    Assert.assertTrue(mr.__isAlive());
    parent.alive = false;
    Assert.assertFalse(mr.__isAlive());
  }
}
