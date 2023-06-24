/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.edhtml.phases.generate;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;

public class FieldListTests {
  @Test
  public void flow() {
    ArrayList<Field> fa = new ArrayList<>();
    fa.add(new Field("x", "xp", "int", new Annotations()));
    fa.add(new Field("y", "yp", "int", new Annotations()));
    FieldList a = new FieldList(fa);

    ArrayList<Field> fb = new ArrayList<>();
    fb.add(new Field("x", "xp", "int", new Annotations()));
    fb.add(new Field("z", "zp", "int", new Annotations()));
    FieldList b = new FieldList(fb);

    FieldList c = FieldList.intersect(a, b);
    Iterator<Field> it = c.iterator();
    Assert.assertTrue(it.hasNext());
    Assert.assertEquals("x", it.next().name);
    Assert.assertFalse(it.hasNext());
  }
}
