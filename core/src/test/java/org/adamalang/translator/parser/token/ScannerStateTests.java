/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.parser.token;

import org.junit.Assert;
import org.junit.Test;

public class ScannerStateTests {
    @Test
    public void coverage() {
        for (ScannerState ss : ScannerState.values()) {
            Assert.assertEquals(ss, ScannerState.valueOf(ss.name()));
        }
    }
}
