/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.api;

import org.junit.Assert;
import org.junit.Test;

public class QueryVariantTests {
    @Test
    public void testbool() {
        QueryVariant qv = new QueryVariant("true");
        Assert.assertEquals("true", qv.string_value);
        Assert.assertNull(qv.double_value);
        Assert.assertNull(qv.int_value);
        Assert.assertTrue(qv.bool_value);
    }
    @Test
    public void teststr() {
        QueryVariant qv = new QueryVariant("yo");
        Assert.assertEquals("yo", qv.string_value);
        Assert.assertNull(qv.double_value);
        Assert.assertNull(qv.int_value);
        Assert.assertFalse(qv.bool_value);
    }
    @Test
    public void testint() {
        QueryVariant qv = new QueryVariant("123");
        Assert.assertEquals("123", qv.string_value);
        Assert.assertEquals(123, (double) qv.double_value, 0.01);
        Assert.assertEquals(123, (int) qv.int_value);
        Assert.assertFalse(qv.bool_value);
    }
    @Test
    public void testdouble() {
        QueryVariant qv = new QueryVariant("123.5");
        Assert.assertEquals("123.5", qv.string_value);
        Assert.assertEquals(123.5, (double) qv.double_value, 0.01);
        Assert.assertNull(qv.int_value);
        Assert.assertFalse(qv.bool_value);
    }
    @Test
    public void test_one() {
        QueryVariant qv = new QueryVariant("1");
        Assert.assertEquals("1", qv.string_value);
        Assert.assertEquals(1, (double) qv.double_value, 0.01);
        Assert.assertEquals(1, (int) qv.int_value);
        Assert.assertTrue(qv.bool_value);
    }
}
