/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.mocks;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.bridges.RecordBridge;
import org.adamalang.runtime.contracts.RxParent;

public class MockRecordBridge extends RecordBridge<MockRecord> {

    public boolean index = false;

    @Override
    public MockRecord construct(ObjectNode item, RxParent parent) {
        return new MockRecord(item, parent);
    }

    @Override
    public int getNumberColumns() {
        if (index) {
            return 1;
        }
        return 0;
    }

    @Override
    public MockRecord[] makeArray(int n) {
        return new MockRecord[n];
    }
}
