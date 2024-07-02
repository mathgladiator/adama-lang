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

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtDate;
import org.adamalang.runtime.natives.NtTimeSpan;
import org.junit.Assert;
import org.junit.Test;

public class RxTimeSpanTests {
  private static final NtTimeSpan A = new NtTimeSpan(120);
  private static final NtTimeSpan B = new NtTimeSpan(320);

  @Test
  public void memory() {
    Assert.assertEquals(24, A.memory());
    Assert.assertEquals(24, B.memory());
    RxTimeSpan a = new RxTimeSpan(null, A);
    RxTimeSpan b = new RxTimeSpan(null, B);
    Assert.assertEquals(104, a.__memory());
    Assert.assertEquals(104, b.__memory());
    a.set(A);
    Assert.assertEquals(A, a.get());
  }

  @Test
  public void compare() {
    RxTimeSpan a = new RxTimeSpan(null, A);
    RxTimeSpan b = new RxTimeSpan(null, B);
    Assert.assertEquals(-1, a.compareTo(b));
    Assert.assertEquals(1, b.compareTo(a));
  }

  @Test
  public void commit_no_change() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();

    RxTimeSpan a = new RxTimeSpan(null, A);
    a.__commit("x", redo, undo);
    Assert.assertEquals("", redo.toString());
    Assert.assertEquals("", undo.toString());
  }

  @Test
  public void commit_change() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();

    RxTimeSpan a = new RxTimeSpan(null, A);
    a.set(B);
    a.__commit("x", redo, undo);
    Assert.assertEquals(
        "\"x\":320.0",
        redo.toString());
    Assert.assertEquals(
        "\"x\":120.0",
        undo.toString());
  }

  @Test
  public void revert() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();
    RxTimeSpan a = new RxTimeSpan(null, A);
    a.set(B);
    a.__revert();
    a.__commit("x", redo, undo);
    Assert.assertEquals("", redo.toString());
    Assert.assertEquals("", undo.toString());
  }

  @Test
  public void dump() {
    JsonStreamWriter c = new JsonStreamWriter();
    RxTimeSpan a = new RxTimeSpan(null, A);
    a.__dump(c);
    Assert.assertEquals(
        "120.0",
        c.toString());
  }

  @Test
  public void insert() {
    JsonStreamWriter c = new JsonStreamWriter();
    RxTimeSpan a = new RxTimeSpan(null, B);
    a.__dump(c);
    a.__insert(
        new JsonStreamReader(
            "42"));
    a.__dump(c);
    Assert.assertEquals(
        "320.042.0",
        c.toString());
  }

  @Test
  public void patch() {
    JsonStreamWriter c = new JsonStreamWriter();
    RxTimeSpan a = new RxTimeSpan(null, B);
    a.__patch(
        new JsonStreamReader(
            "42"));
    a.__dump(c);
    Assert.assertEquals(
        "42.0",
        c.toString());
  }
}
