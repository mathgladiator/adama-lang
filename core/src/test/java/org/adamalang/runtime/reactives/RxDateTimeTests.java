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
