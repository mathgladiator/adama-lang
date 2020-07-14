/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.ops;

import org.adamalang.runtime.stdlib.Utility;
import org.junit.Assert;
import org.junit.Test;

public class TestReportBuilderTests {
    @Test
    public void flow() {
        TestReportBuilder trb = new TestReportBuilder();
        trb.begin("xyz");
        trb.annotate("x", Utility.createObjectNode());
        trb.end(new AssertionStats(50, 0));
        Assert.assertEquals(0, trb.getFailures());
        trb.begin("t2");
        trb.end(new AssertionStats(50, 4));
        Assert.assertEquals(4, trb.getFailures());
        Assert.assertEquals("TEST[xyz] = 100.0%\n" +
                "TEST[t2] = 92.0% (HAS FAILURES)\n", trb.toString());
    }

    @Test
    public void flow2() {
        TestReportBuilder trb = new TestReportBuilder();
        trb.begin("xyz");
        trb.end(new AssertionStats(0, 0));
        trb.begin("zx");
        trb.annotate("dump", Utility.parseJsonObject("{\"x\":true}"));
        trb.end(new AssertionStats(0, 0));
        Assert.assertEquals("TEST[xyz] HAS NO ASSERTS\n" +
                "TEST[zx]...DUMP:{\"x\":true}\n" +
                " HAS NO ASSERTS\n", trb.toString());
    }
}
