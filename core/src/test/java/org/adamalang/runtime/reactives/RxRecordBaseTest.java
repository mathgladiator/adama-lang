/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.mocks.MockRecord;
import org.adamalang.runtime.mocks.MockRxChild;
import org.adamalang.runtime.stdlib.Utility;
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
}
