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
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.mocks.MockRecord;
import org.adamalang.runtime.mocks.MockRxChild;
import org.adamalang.runtime.mocks.MockRxParent;
import org.adamalang.runtime.natives.NtDateTime;
import org.junit.Assert;
import org.junit.Test;

import java.time.ZonedDateTime;

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
  public void subDt() {
    final var x = new MockRecord(null);
    RxDateTime dt = new RxDateTime(null, new NtDateTime(ZonedDateTime.parse("2021-04-24T17:57:19.802528800-05:00[America/Chicago]")));
    x.__subscribeUpdated(dt, () -> new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]")));
    Assert.assertEquals(dt.get().toString(), "2021-04-24T17:57:19.802528800-05:00[America/Chicago]");
    x.__raiseInvalid();
    Assert.assertEquals(dt.get().toString(), "2023-04-24T17:57:19.802528800-05:00[America/Chicago]");
  }

  @Test
  public void subBump() {
    final var x = new MockRecord(null);
    RxInt32 c = new RxInt32(null, 0);
    x.__subscribeBump(c);
    Assert.assertEquals(0, (int) c.get());
    x.__raiseInvalid();
    x.__raiseInvalid();
    x.__raiseInvalid();
    Assert.assertEquals(1, (int) c.get());
    x.__settle(null);
    x.__raiseInvalid();
    Assert.assertEquals(2, (int) c.get());
  }

  @Test
  public void cost_report() {
    MockRxParent parent = new MockRxParent();
    new MockRecord(parent).__cost(2421);
    Assert.assertEquals(2421, parent.cost);
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
    child.assertInvalidateCount(3);
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
