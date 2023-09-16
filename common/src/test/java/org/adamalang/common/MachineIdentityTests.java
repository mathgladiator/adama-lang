/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
    MachineIdentity identity = new MachineIdentity("{\"ip\":\"x\",\"key\":\"y\",\"cert\":\"z\",\"trust\":\"w\"}");
    identity.getCert();
    identity.getKey();
    identity.getCert();
    Assert.assertEquals("x", identity.ip);
  }
}
