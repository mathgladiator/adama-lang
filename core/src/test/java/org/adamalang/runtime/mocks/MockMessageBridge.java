/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.mocks;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.bridges.MessageBridge;
import org.adamalang.runtime.bridges.NativeBridge;

public class MockMessageBridge extends MessageBridge<MockMessage> {
    @Override
    public MockMessage convert(ObjectNode node) {
        MockMessage msg = new MockMessage();
        msg.x = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(node, "x");
        msg.y = NativeBridge.INTEGER_NATIVE_SUPPORT.readFromMessageObject(node, "y");
        return msg;
    }

    @Override
    public MockMessage[] makeArray(int n) {
        return new MockMessage[n];
    }
}
