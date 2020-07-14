/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.mocks.MockRxChild;
import org.junit.Assert;
import org.junit.Test;

public class RxGuardTests {
    @Test
    public void flow() {
        RxGuard guard = new RxGuard();
        Assert.assertEquals(true, guard.invalid);
        guard.__commit(null, null);
        Assert.assertEquals(false, guard.invalid);
        MockRxChild child = new MockRxChild();
        guard.__subscribe(child);
        guard.__raiseInvalid();
        child.assertInvalidateCount(0);
        Assert.assertEquals(true, guard.invalid);
        guard.__revert();
        Assert.assertEquals(true, guard.invalid);
    }
}
