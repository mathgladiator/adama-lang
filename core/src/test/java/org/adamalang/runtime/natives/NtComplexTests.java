package org.adamalang.runtime.natives;

import org.junit.Assert;
import org.junit.Test;

public class NtComplexTests {
    @Test
    public void equality() {
        NtComplex a = new NtComplex(1.2, 3.4);
        NtComplex b = new NtComplex(1.2, 3.4);
        NtComplex c = new NtComplex(3.4, -1.2);
        Assert.assertEquals(a, a);
        Assert.assertEquals(a, b);
        Assert.assertNotEquals(a, c);
        Assert.assertNotEquals(a, "z");
        Assert.assertNotEquals("z", a);
    }
}
