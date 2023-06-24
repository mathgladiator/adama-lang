/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
    v.hashCode();
  }

  @Test
  public void flow2() {
    DeployedVersion v = new DeployedVersion(new JsonStreamReader("{\"main\":\"xyz\",\"junk\":true,\"includes\":{\"x\":\"y\"}}"));
    Assert.assertEquals("xyz", v.main);
    Assert.assertTrue(v.includes.containsKey("x"));
    Assert.assertEquals("y", v.includes.get("x"));
    v.hashCode();
  }
}
