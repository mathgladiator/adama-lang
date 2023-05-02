/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.caravan;

import org.junit.Test;

import java.io.File;
import java.util.Locale;

public class CleanupTests {

  @Test
  public void trash_temp() throws Exception {
    File f = File.createTempFile("ADAMATEST", "XYZ");
    for (File x : f.getParentFile().listFiles()) {
      if (x.getName().toLowerCase(Locale.ROOT).startsWith("adama")) {
        x.deleteOnExit();
      }
    }
  }
}
