/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.logger;

import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

public class ObjectNodeLoggerTests {
    @Test
    public void flow1() {
        ObjectNodeLogger log = ObjectNodeLogger.fresh();
        log.ingest(new Transaction(-1, Utility.createObjectNode(), Utility.parseJsonObject("{\"x\":1}"), new TransactionResult(true, 0, 0)));
        Assert.assertEquals("{\"x\":1}", log.node.toString());
        log.close();
    }
    @Test
    public void flow2() {
        ObjectNodeLogger log = ObjectNodeLogger.recover(Utility.parseJsonObject("{\"x\":1}"));
        Assert.assertEquals("{\"x\":1}", log.node.toString());
        log.ingest(new Transaction(-1, Utility.createObjectNode(), Utility.parseJsonObject("{\"x\":5}"), new TransactionResult(true, 0, 0)));
        Assert.assertEquals("{\"x\":5}", log.node.toString());
    }
}
