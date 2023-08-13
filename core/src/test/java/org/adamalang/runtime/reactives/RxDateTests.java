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
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtDate;
import org.junit.Assert;
import org.junit.Test;

public class RxDateTests {
  private static final NtDate A = new NtDate(2011, 11, 9);
  private static final NtDate B = new NtDate(2014, 10, 8);

  @Test
  public void memory() {
    Assert.assertEquals(24, A.memory());
    Assert.assertEquals(24, B.memory());
    RxDate a = new RxDate(null, A);
    RxDate b = new RxDate(null, B);
    Assert.assertEquals(104, a.__memory());
    Assert.assertEquals(104, b.__memory());
    a.set(A);
    Assert.assertEquals(A, a.get());
  }

  @Test
  public void idx_value() {
    RxDate a = new RxDate(null, A);
    Assert.assertEquals(23553193, a.getIndexValue());
  }

  @Test
  public void compare() {
    RxDate a = new RxDate(null, A);
    RxDate b = new RxDate(null, B);
    Assert.assertEquals(-1, a.compareTo(b));
    Assert.assertEquals(1, b.compareTo(a));
  }

  @Test
  public void commit_no_change() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();

    RxDate a = new RxDate(null, A);
    a.__commit("x", redo, undo);
    Assert.assertEquals("", redo.toString());
    Assert.assertEquals("", undo.toString());
  }

  @Test
  public void commit_change() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();

    RxDate a = new RxDate(null, A);
    a.set(B);
    a.__commit("x", redo, undo);
    Assert.assertEquals(
        "\"x\":\"2014-10-08\"",
        redo.toString());
    Assert.assertEquals(
        "\"x\":\"2011-11-09\"",
        undo.toString());
  }

  @Test
  public void revert() {
    JsonStreamWriter redo = new JsonStreamWriter();
    JsonStreamWriter undo = new JsonStreamWriter();
    RxDate a = new RxDate(null, A);
    a.set(B);
    a.__revert();
    a.__commit("x", redo, undo);
    Assert.assertEquals("", redo.toString());
    Assert.assertEquals("", undo.toString());
  }

  @Test
  public void dump() {
    JsonStreamWriter c = new JsonStreamWriter();
    RxDate a = new RxDate(null, A);
    a.__dump(c);
    Assert.assertEquals(
        "\"2011-11-09\"",
        c.toString());
  }

  @Test
  public void insert() {
    JsonStreamWriter c = new JsonStreamWriter();
    RxDate a = new RxDate(null, B);
    a.__dump(c);
    a.__insert(
        new JsonStreamReader(
            "\"2021/1/2\""));
    a.__dump(c);
    Assert.assertEquals(
        "\"2014-10-08\"\"2021-01-02\"",
        c.toString());
  }

  @Test
  public void patch() {
    JsonStreamWriter c = new JsonStreamWriter();
    RxDate a = new RxDate(null, B);
    a.__patch(
        new JsonStreamReader(
            "\"2021/1/2\""));
    a.__dump(c);
    Assert.assertEquals(
        "\"2021-01-02\"",
        c.toString());
  }
}
