package org.adamalang.runtime.stdlib;

import org.adamalang.runtime.natives.NtMaybe;
import org.junit.Assert;
import org.junit.Test;

public class LibArithmeticTests {
    @Test
    public void doubleDivision() {
        Assert.assertEquals(0.5, LibArithmetic.divDD(1, 2.0).get(), 0.01);
        Assert.assertEquals(0.5, LibArithmetic.divDD(new NtMaybe<>(1.0), 2).get(), 0.01);
        Assert.assertEquals(0.5, LibArithmetic.divDD(1.0, new NtMaybe<>(2.0)).get(), 0.01);
        Assert.assertEquals(0.5, LibArithmetic.divDD(new NtMaybe<>(1.0), new NtMaybe<>(2.0)).get(), 0.01);
        Assert.assertFalse(LibArithmetic.divDD(new NtMaybe<>(), 2).has());
        Assert.assertFalse(LibArithmetic.divDD(1.0, new NtMaybe<>()).has());
        Assert.assertFalse(LibArithmetic.divDD(new NtMaybe<>(), new NtMaybe<>()).has());
        Assert.assertFalse(LibArithmetic.divDD(new NtMaybe<>(1.0), new NtMaybe<>()).has());
        Assert.assertFalse(LibArithmetic.divDD(new NtMaybe<>(), new NtMaybe<>(1.0)).has());
        Assert.assertFalse(LibArithmetic.divDD(1, 0.0).has());
    }

    @Test
    public void intDivision() {
        Assert.assertEquals(0.5, LibArithmetic.divII(1, 2).get(), 0.01);
        Assert.assertFalse(LibArithmetic.divII(1, 0).has());
    }
}
