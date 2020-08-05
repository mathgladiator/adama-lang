/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.support.testgen;

import org.junit.Assert;
import org.junit.Test;

public class TestFileTests {
    @Test
    public void happy() {
        TestFile tst = new TestFile("Z", "x", true);
        Assert.assertEquals("Z_x_success.a", tst.filename());
        TestFile tst2 = TestFile.fromFilename(tst.filename());
        Assert.assertEquals("Z", tst.clazz);
        Assert.assertEquals("x", tst.name);
        Assert.assertTrue(tst2.success);
    }
    @Test
    public void sad() {
        TestFile tst = new TestFile("Z", "x", false);
        Assert.assertEquals("Z_x_failure.a", tst.filename());
        TestFile tst2 = TestFile.fromFilename(tst.filename());
        Assert.assertEquals("Z", tst2.clazz);
        Assert.assertEquals("x", tst2.name);
        Assert.assertFalse(tst2.success);
    }
}
