/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
