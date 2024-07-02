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
import org.adamalang.runtime.natives.NtDateTime;
import org.adamalang.runtime.natives.NtDateTimeTests;
import org.junit.Assert;
import org.junit.Test;

import java.time.ZonedDateTime;

public class RxDateTimeTests {
  //
  private static final NtDateTime A = new NtDateTime(ZonedDateTime.parse("2021-04-24T17:57:19.802528800-05:00[America/Chicago]"));
  private static final NtDateTime B = new NtDateTime(ZonedDateTime.parse("2023-04-24T17:57:19.802528800-05:00[America/Chicago]"));

  @Test
  public void memory() {
    Assert.assertEquals(64, A.memory());
    Assert.assertEquals(64, B.memory());
    RxDateTime a = new RxDateTime(null, A);
    RxDateTime b = new RxDateTime(null, B);
    Assert.assertEquals(184, a.__memory());
    Assert.assertEquals(184, b.__memory());
    a.set(A);
    Assert.assertEquals(A, a.get());
  }

  @Test
  public void idx_value() {
    RxDateTime a = new RxDateTime(null, A);
    Assert.assertEquals(26988417, a.getIndexValue());
  }

  @Test
  public void compare() {
    RxDateTime a = new RxDateTime(null, A);
    RxDateTime b = new RxDateTime(null, B);
    Assert.assertEquals(-1, a.compareTo(b));
    Assert.assertEquals(1, b.compareTo(a));
  }

  @Test
  public void commit_no_change() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();

    RxDateTime a = new RxDateTime(null, A);
    a.__commit("x", redo, undo);
    Assert.assertEquals("", redo.toString());
    Assert.assertEquals("", undo.toString());
  }

  @Test
  public void commit_change() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();

    RxDateTime a = new RxDateTime(null, A);
    a.set(B);
    a.__commit("x", redo, undo);
    Assert.assertEquals(
        "\"x\":\"2023-04-24T17:57:19.802528800-05:00[America/Chicago]\"",
        redo.toString());
    Assert.assertEquals(
        "\"x\":\"2021-04-24T17:57:19.802528800-05:00[America/Chicago]\"",
        undo.toString());
  }

  @Test
  public void revert() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();
    RxDateTime a = new RxDateTime(null, A);
    a.set(B);
    a.__revert();
    a.__commit("x", redo, undo);
    Assert.assertEquals("", redo.toString());
    Assert.assertEquals("", undo.toString());
  }

  @Test
  public void dump() {
    JsonStreamWriter c = new JsonStreamWriter();
    RxDateTime a = new RxDateTime(null, A);
    a.__dump(c);
    Assert.assertEquals(
        "\"2021-04-24T17:57:19.802528800-05:00[America/Chicago]\"",
        c.toString());
  }

  @Test
  public void insert() {
    JsonStreamWriter c = new JsonStreamWriter();
    RxDateTime a = new RxDateTime(null, B);
    a.__dump(c);
    a.__insert(
        new JsonStreamReader(
            "\"2027-04-24T17:57:19.802528800-05:00[America/Chicago]\""));
    a.__dump(c);
    Assert.assertEquals(
        "\"2023-04-24T17:57:19.802528800-05:00[America/Chicago]\"\"2027-04-24T17:57:19.802528800-05:00[America/Chicago]\"",
        c.toString());
  }

  @Test
  public void patch() {
    JsonStreamWriter c = new JsonStreamWriter();
    RxDateTime a = new RxDateTime(null, B);
    a.__patch(
        new JsonStreamReader(
            "\"2027-04-24T17:57:19.802528800-05:00[America/Chicago]\""));
    a.__dump(c);
    Assert.assertEquals(
        "\"2027-04-24T17:57:19.802528800-05:00[America/Chicago]\"",
        c.toString());
  }
}
