/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.deploy;

import org.adamalang.runtime.json.JsonStreamReader;
import org.junit.Assert;
import org.junit.Test;

public class DeployedVersionTests {
  @Test
  public void upgrade() {
    DeployedVersion justString = new DeployedVersion(new JsonStreamReader("\"main\""));
    Assert.assertEquals("main", justString.main);
    justString.hashCode();
  }

  @Test
  public void flow1() {
    DeployedVersion v = new DeployedVersion(new JsonStreamReader("{\"main\":\"xyz\",\"junk\":true,\"includes\":{\"x\":\"y\"}}"));
    Assert.assertEquals("xyz", v.main);
    Assert.assertTrue(v.includes.containsKey("x"));
    Assert.assertEquals("y", v.includes.get("x"));
    Assert.assertNull(v.rxhtml);
    v.hashCode();
  }

  @Test
  public void flow2() {
    DeployedVersion v = new DeployedVersion(new JsonStreamReader("{\"main\":\"xyz\",\"rxhtml\":\"yo\",\"junk\":true,\"includes\":{\"x\":\"y\"}}"));
    Assert.assertEquals("xyz", v.main);
    Assert.assertTrue(v.includes.containsKey("x"));
    Assert.assertEquals("y", v.includes.get("x"));
    Assert.assertEquals("yo", v.rxhtml);
    v.hashCode();
  }
}
