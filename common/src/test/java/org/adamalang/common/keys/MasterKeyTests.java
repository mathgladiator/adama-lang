/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.keys;

import org.junit.Assert;
import org.junit.Test;

public class MasterKeyTests {
  @Test
  public void flow() throws Exception {
    String key = MasterKey.generateMasterKey();
    String result = MasterKey.encrypt(key, "this is secure");
    System.err.println(result);
    String output = MasterKey.decrypt(key, result);
    Assert.assertEquals("this is secure", output);
  }
}
