/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.mocks;

import org.adamalang.runtime.contracts.RxParent;
import org.junit.Assert;

public class MockRxParent implements RxParent {
    public int dirtyCount;
    public MockRxParent() {
        this.dirtyCount = 0;
    }

    @Override
    public void __raiseDirty() {
        this.dirtyCount++;
    }

    public void assertDirtyCount(int expected) {
        Assert.assertEquals(expected, dirtyCount);
    }
}
