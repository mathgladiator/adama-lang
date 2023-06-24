/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.server;

import org.adamalang.net.TestBed;
import org.adamalang.net.client.InstanceClient;
import org.junit.Assert;
import org.junit.Test;

public class ServerTests {
  @Test
  public void ping() throws Exception {
    try (TestBed bed = new TestBed( 20000, "@connected { return true; } public int x; @construct { x = 123; transition #p in 0.5; } #p { x++; } ")) {
      try (InstanceClient ic = bed.makeClient()) {
        Assert.assertFalse(ic.ping(2500));
        bed.startServer();
        Assert.assertTrue(ic.ping(15000));
      }
    }
  }
}
