package org.adamalang.web.io;

import org.junit.Assert;
import org.junit.Test;

public class ConnectionContextTests {
    @Test
    public void stripColonIp() {
        ConnectionContext a = new ConnectionContext("you", "123:42", "house");
        Assert.assertEquals("123", a.remoteIp);
    }

    @Test
    public void nulls() {
        ConnectionContext a = new ConnectionContext(null, null, null);
        Assert.assertEquals("", a.remoteIp);
        Assert.assertEquals("", a.userAgent);
        Assert.assertEquals("", a.origin);
    }
}
