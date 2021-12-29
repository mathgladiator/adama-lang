/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

public class MachineIdentityTests {
    public String of(InputStream stream) throws Exception {
        StringBuilder sb = new StringBuilder();
        byte[] chunk = new byte[4096];
        int rd;
        while ((rd = stream.read(chunk)) > 0) {
            sb.append(new String(chunk, 0, rd));
        }
        return sb.toString();
    }

    @Test
    public void io() throws Exception {
        File trust = File.createTempFile("prefix", "trust");
        File cert = File.createTempFile("prefix", "cert");
        File key = File.createTempFile("prefix", "key");
        File json = File.createTempFile("prefix", "identity");
        Files.writeString(trust.toPath(), "X");
        Files.writeString(cert.toPath(), "Y");
        Files.writeString(key.toPath(), "Z");
        String str = MachineIdentity.convertToJson("x", trust, cert, key);
        Files.writeString(json.toPath(), str);
        MachineIdentity identity = MachineIdentity.fromFile(json.getAbsolutePath());
        Assert.assertEquals("x", identity.ip);
        Assert.assertEquals("X", of(identity.getTrust()));
        Assert.assertEquals("Y", of(identity.getCert()));
        Assert.assertEquals("Z", of(identity.getKey()));
    }

    @Test
    public void coverage() throws Exception {
        try {
            new MachineIdentity("{}");
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("ip"));
        }
        try {
            new MachineIdentity("{\"ip\":\"x\"}");
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("key"));
        }
        try {
            new MachineIdentity("{\"ip\":\"x\",\"key\":\"y\"}");
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("cert"));
        }
        try {
            new MachineIdentity("{\"ip\":\"x\",\"key\":\"y\",\"cert\":\"z\"}");
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("trust"));
        }
        MachineIdentity identity = new MachineIdentity("{\"ip\":\"x\",\"key\":\"y\",\"cert\":\"z\",\"trust\":\"w\"}");
        identity.getCert();
        identity.getKey();
        identity.getCert();
        Assert.assertEquals("x", identity.ip);
    }
}
