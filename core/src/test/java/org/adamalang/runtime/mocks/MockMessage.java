/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.mocks;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.runtime.natives.NtMessageBase;
import org.adamalang.runtime.stdlib.Utility;

public class MockMessage implements NtMessageBase {
    public int x;
    public int y;
    public MockMessage() {
        x = 42;
        y = 13;
    }

    @Override
    public ObjectNode convertToObjectNode() {
        ObjectNode node = Utility.createObjectNode();
        node.put("x", x);
        node.put("y", y);
        return node;
    }
}
