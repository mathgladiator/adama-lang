/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

public class MachineIdentityTests {
  @Test
  public void io() throws Exception {
    File trust = File.createTempFile("ADAMATEST_", "trust");
    File cert = File.createTempFile("ADAMATEST_", "cert");
    File key = File.createTempFile("ADAMATEST_", "key");
    File json = File.createTempFile("ADAMATEST_", "identity");
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
    MachineIdentity identity =
        new MachineIdentity("{\"ip\":\"x\",\"key\":\"y\",\"cert\":\"z\",\"trust\":\"w\"}");
    identity.getCert();
    identity.getKey();
    identity.getCert();
    Assert.assertEquals("x", identity.ip);
  }
}
