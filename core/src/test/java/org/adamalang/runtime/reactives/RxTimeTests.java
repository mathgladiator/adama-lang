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

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtTime;
import org.junit.Assert;
import org.junit.Test;

public class RxTimeTests {
  private static final NtTime A = new NtTime(4, 20);
  private static final NtTime B = new NtTime(6, 9);

  @Test
  public void memory() {
    Assert.assertEquals(24, A.memory());
    Assert.assertEquals(24, B.memory());
    RxTime a = new RxTime(null, A);
    RxTime b = new RxTime(null, B);
    Assert.assertEquals(104, a.__memory());
    Assert.assertEquals(104, b.__memory());
    a.set(A);
    Assert.assertEquals(A, a.get());
  }

  @Test
  public void idx_value() {
    RxTime a = new RxTime(null, A);
    Assert.assertEquals(260, a.getIndexValue());
  }

  @Test
  public void compare() {
    RxTime a = new RxTime(null, A);
    RxTime b = new RxTime(null, B);
    Assert.assertEquals(-1, a.compareTo(b));
    Assert.assertEquals(1, b.compareTo(a));
  }

  @Test
  public void commit_no_change() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();

    RxTime a = new RxTime(null, A);
    a.__commit("x", redo, undo);
    Assert.assertEquals("", redo.toString());
    Assert.assertEquals("", undo.toString());
  }

  @Test
  public void commit_change() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();

    RxTime a = new RxTime(null, A);
    a.set(B);
    a.__commit("x", redo, undo);
    Assert.assertEquals(
        "\"x\":\"06:09\"",
        redo.toString());
    Assert.assertEquals(
        "\"x\":\"04:20\"",
        undo.toString());
  }

  @Test
  public void revert() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();
    RxTime a = new RxTime(null, A);
    a.set(B);
    a.__revert();
    a.__commit("x", redo, undo);
    Assert.assertEquals("", redo.toString());
    Assert.assertEquals("", undo.toString());
  }

  @Test
  public void dump() {
    JsonStreamWriter c = new JsonStreamWriter();
    RxTime a = new RxTime(null, A);
    a.__dump(c);
    Assert.assertEquals(
        "\"04:20\"",
        c.toString());
  }

  @Test
  public void insert() {
    JsonStreamWriter c = new JsonStreamWriter();
    RxTime a = new RxTime(null, B);
    a.__dump(c);
    a.__insert(
        new JsonStreamReader(
            "2:32"));
    a.__dump(c);
    Assert.assertEquals(
        "\"06:09\"\"02:00\"",
        c.toString());
  }

  @Test
  public void patch() {
    JsonStreamWriter c = new JsonStreamWriter();
    RxTime a = new RxTime(null, B);
    a.__patch(
        new JsonStreamReader(
            "\"02:30\""));
    a.__dump(c);
    Assert.assertEquals(
        "\"02:30\"",
        c.toString());
  }
}
