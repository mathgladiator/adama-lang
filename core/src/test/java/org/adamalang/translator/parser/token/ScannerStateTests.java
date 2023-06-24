/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.parser.token;

import org.junit.Assert;
import org.junit.Test;

public class ScannerStateTests {
  @Test
  public void coverage() {
    for (final ScannerState ss : ScannerState.values()) {
      Assert.assertEquals(ss, ScannerState.valueOf(ss.name()));
    }
  }
}
