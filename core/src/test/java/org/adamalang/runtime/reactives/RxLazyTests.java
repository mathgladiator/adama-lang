/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.bridges.NativeBridge;
import org.junit.Assert;
import org.junit.Test;

public class RxLazyTests {
    @Test
    public void flow() {
        RxInt32 val = new RxInt32(null, 42);
        RxLazy<Integer> lz = new RxLazy<>(null, NativeBridge.INTEGER_NATIVE_SUPPORT, () -> val.get() * val.get());
        RxLazy<Integer> lz2 = new RxLazy<>(null, NativeBridge.INTEGER_NATIVE_SUPPORT, () -> lz.get() / 2);
        val.__subscribe(lz);
        lz.__subscribe(lz2);
        val.set(4);
        Assert.assertEquals(16, (int) lz.get());
        Assert.assertEquals(8, (int) lz2.get());
        val.set(6);
        Assert.assertEquals(18, (int) lz2.get());
        Assert.assertEquals(36, (int) lz.get());
        Assert.assertEquals(18, (int) lz2.get());
        Assert.assertEquals(36, (int) lz.get());
        val.set(10);
        Assert.assertEquals(50, (int) lz2.get());
    }
    @Test
    public void trivial() {
        RxInt32 val = new RxInt32(null, 42);
        RxLazy<Integer> lz = new RxLazy<>(null, NativeBridge.INTEGER_NATIVE_SUPPORT, () -> val.get());
        lz.__commit(null, null);
        lz.__revert();
    }
}
