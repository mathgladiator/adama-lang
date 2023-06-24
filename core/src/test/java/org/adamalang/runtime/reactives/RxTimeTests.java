/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
        "\"x\":\"6:09\"",
        redo.toString());
    Assert.assertEquals(
        "\"x\":\"4:20\"",
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
        "\"4:20\"",
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
        "\"6:09\"\"2:00\"",
        c.toString());
  }

  @Test
  public void patch() {
    JsonStreamWriter c = new JsonStreamWriter();
    RxTime a = new RxTime(null, B);
    a.__patch(
        new JsonStreamReader(
            "\"2:30\""));
    a.__dump(c);
    Assert.assertEquals(
        "\"2:30\"",
        c.toString());
  }
}
