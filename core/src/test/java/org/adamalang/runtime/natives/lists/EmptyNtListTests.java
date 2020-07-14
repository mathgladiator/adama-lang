/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.natives.lists;

import org.adamalang.runtime.bridges.NativeBridge;
import org.junit.Assert;
import org.junit.Test;

public class EmptyNtListTests {
    @Test
    public void coverage() {
        EmptyNtList<String> list = new EmptyNtList<>(NativeBridge.STRING_NATIVE_SUPPORT);
        list.get().get();
        Assert.assertEquals(0, list.size());
        list.orderBy(true, (x, y) -> 0);
        list.skipAndLimit(true, 0, 100);
        list.where(true, null);
        list.shuffle(true, null);
        Assert.assertEquals(0, list.toArray().length);
        Assert.assertFalse(list.lookup(10).has());
        list.transform((x) -> x.length(), NativeBridge.INTEGER_NATIVE_SUPPORT);
        list.__delete();
        Assert.assertFalse(list.iterator().hasNext());
        Assert.assertNull(list.iterator().next());
        Assert.assertEquals(0, list.reduce((x) -> x.length(), (l) -> l.lookup(0)).size());
        list.map((zzz) -> { });
    }
}
