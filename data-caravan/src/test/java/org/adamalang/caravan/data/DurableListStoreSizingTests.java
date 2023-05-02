/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.caravan.data;

import org.junit.Test;

import java.io.File;

public class DurableListStoreSizingTests {

  @Test
  public void large() throws Exception {
    File f = File.createTempFile("ADAMATEST", "XYZ");
    DurableListStoreSizing sz = new DurableListStoreSizing(10L * 1024 * 1024 * 1024L, f);
    sz.storage.close();
    for (File x : f.getParentFile().listFiles()) {
      if (x.getName().startsWith("ADAMA")) {
        x.deleteOnExit();
      }
    }
  }
}
