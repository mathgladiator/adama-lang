package org.adamalang.runtime.natives;

import org.junit.Assert;
import org.junit.Test;

public class NtCreateContextTests {
    @Test
    public void coverage() {
        NtCreateContext context = new NtCreateContext("origin", "ip", "key");
        Assert.assertEquals("origin", context.origin);
        Assert.assertEquals("ip", context.ip);
        Assert.assertEquals("key", context.key);
    }
}
