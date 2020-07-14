/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.mocks.MockRxChild;
import org.adamalang.runtime.mocks.MockRecord;
import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

public class RxRecordBaseTest {
    @Test
    public void sanity() {
        MockRecord mr = new MockRecord(Utility.createObjectNode(), null);
        MockRxChild child = new MockRxChild();
        mr.__subscribe(child);
        mr.id = 123;
        mr.__id();
        mr.__privacyPolicyAllowsCache();
        mr.__getIndexColumns();
        mr.__getIndexValues();
        mr.__name();
        mr.__reindex();
        mr.__deindex();
        mr.__commit(null, Utility.createObjectNode());
        mr.__revert();
        mr.getPrivateViewFor(null);
        mr.setCachedObjectNode(Utility.createObjectNode());
        Assert.assertNotNull(mr.getCachedObjectNode());
        mr.__raiseDirty();
        Assert.assertNull(mr.getCachedObjectNode());
        Assert.assertTrue(mr.__isDirty());
        mr.__lowerDirtyCommit();
        Assert.assertFalse(mr.__isDirty());
        mr.__raiseDirty();
        mr.__lowerDirtyRevert();
        Assert.assertFalse(mr.__isDirty());
        mr.__delete();
        Assert.assertTrue(mr.__isDirty());
        Assert.assertTrue(mr.__isDying());
        child.assertInvalidateCount(4);
        Assert.assertEquals(0, mr.compareTo(mr));
        Assert.assertEquals(123, mr.hashCode());
        Assert.assertTrue(mr.equals(mr));
        Assert.assertFalse(mr.equals(null));
    }

    @Test
    public void cmp() {
        MockRecord a = new MockRecord(Utility.createObjectNode(), null);
        MockRecord b = new MockRecord(Utility.createObjectNode(), null);
        a.id = 1;
        b.id = 2;
        Assert.assertEquals(-1, a.compareTo(b));
        Assert.assertEquals(1, b.compareTo(a));
        Assert.assertFalse(a.equals(b));
        a.__invalidateSubscribers();
        a.__raiseInvalid();
    }
}
