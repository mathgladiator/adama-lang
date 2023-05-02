/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.data;

import org.junit.Assert;
import org.junit.Test;

public class ComputeMethodTests {
  @Test
  public void sanity() {
    Assert.assertNull(ComputeMethod.fromType(0));
    Assert.assertEquals(ComputeMethod.HeadPatch, ComputeMethod.fromType(1));
    Assert.assertEquals(ComputeMethod.Rewind, ComputeMethod.fromType(2));
  }
}
