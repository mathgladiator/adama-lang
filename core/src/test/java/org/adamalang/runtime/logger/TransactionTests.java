/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

import org.adamalang.runtime.stdlib.Utility;
import org.junit.Test;

public class TransactionTests {
    @Test
    public void flow() {
        new Transaction(-1, Utility.createObjectNode(), Utility.parseJsonObject("{\"x\":1}"), new TransactionResult(true, 0, 0)).toString();
        new Transaction(-1, Utility.createObjectNode(), Utility.parseJsonObject("{\"x\":1}"), new TransactionResult(false, 0, 0)).toString();
    }
}
