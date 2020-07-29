/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.natives;

import org.junit.Assert;
import org.junit.Test;

public class NtMapTests {
  @Test
  public void flow() {
    final var map = new NtMap<Integer, Integer>();
    final var ptr = map.lookup(42);
    Assert.assertEquals(0, map.size());
    ptr.set(100);
    Assert.assertEquals(1, map.size());
    final var it = map.iterator();
    Assert.assertTrue(it.hasNext());
    Assert.assertEquals(100, (int) it.next().getValue());
    Assert.assertFalse(it.hasNext());
    final var copy = new NtMap<>(map);
    Assert.assertEquals(1, copy.size());
    final var copy2 = new NtMap<Integer, Integer>();
    copy2.set(copy);
    Assert.assertEquals(1, copy2.size());
    final var copy3 = new NtMap<Integer, Integer>();
    copy3.insert(copy);
    Assert.assertEquals(1, copy3.size());
    ptr.delete();
    Assert.assertEquals(0, map.size());
    map.put(1000, 40);
    Assert.assertEquals(1, map.size());
    Assert.assertEquals(40, (int) map.lookup(1000).get());
  }
}
