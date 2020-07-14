/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.mocks;

import org.adamalang.runtime.contracts.RxChild;
import org.junit.Assert;

public class MockRxChild implements RxChild {
    public int invalidCount;
    public boolean alive;
    public MockRxChild() {
        this.invalidCount = 0;
        this.alive = true;
    }

    @Override
    public boolean __raiseInvalid() {
        this.invalidCount++;
        return alive;
    }

    public void assertInvalidateCount(int expected) {
        Assert.assertEquals(expected, invalidCount);
    }
}
