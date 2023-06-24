/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.natives.lists;

import org.adamalang.runtime.natives.NtMaybe;
import org.junit.Assert;
import org.junit.Test;

public class EmptyNtListTests {
  @Test
  public void coverage() {
    final var list = new EmptyNtList<String>();
    list.get().get();
    Assert.assertEquals(0, list.size());
    list.orderBy(true, (x, y) -> 0);
    list.skip(true, 0);
    list.limit(false, 100);
    list.where(true, null);
    list.shuffle(true, null);
    Assert.assertEquals(0, list.toArray(n -> new String[n]).length);
    Assert.assertFalse(list.lookup(10).has());
    list.transform(String::length);
    list.__delete();
    Assert.assertFalse(list.iterator().hasNext());
    Assert.assertNull(list.iterator().next());
    Assert.assertEquals(0, list.reduce(String::length, l -> l.lookup(0)).size());
    list.map(zzz -> {});
    Assert.assertFalse(list.lookup(134).has());
    Assert.assertFalse(list.lookup(new NtMaybe<>()).has());
    Assert.assertFalse(list.lookup(new NtMaybe<>(42)).has());
    list.mapFunction((x) -> x.length());
  }
}
