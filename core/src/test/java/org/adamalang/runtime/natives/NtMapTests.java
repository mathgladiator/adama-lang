/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.natives;

import org.junit.Assert;
import org.junit.Test;

public class NtMapTests {
    @Test
    public void flow() {
        NtMap<Integer, Integer> map = new NtMap<>();
        NtMaybe<Integer> ptr = map.lookup(42);
        Assert.assertEquals(0, map.size());
        ptr.set(100);
        Assert.assertEquals(1, map.size());
        NtMap<Integer, Integer> copy = new NtMap<>(map);
        Assert.assertEquals(1, copy.size());
        NtMap<Integer, Integer> copy2 = new NtMap<>();
        copy2.set(copy);
        Assert.assertEquals(1, copy2.size());
        NtMap<Integer, Integer> copy3 = new NtMap<>();
        copy3.insert(copy);
        Assert.assertEquals(1, copy3.size());
        ptr.delete();
        Assert.assertEquals(0, map.size());
    }
}
