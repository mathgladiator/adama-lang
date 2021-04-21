/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.natives.lists;

import org.junit.Assert;
import org.junit.Test;

public class EmptyNtListTests {
  @Test
  public void coverage() {
    final var list = new EmptyNtList<String>();
    list.get().get();
    Assert.assertEquals(0, list.size());
    list.orderBy(true, (x, y) -> 0);
    list.skipAndLimit(true, 0, 100);
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
  }
}
