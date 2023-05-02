/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.json.JsonStreamReader;
import org.junit.Assert;
import org.junit.Test;

public class RxEnumInt32Tests {
  @Test
  public void flow() {
    final var iv = new RxEnumInt32(null, 1, (v) -> {
      if (v < 3) {
        return v;
      }
      return 3;
    });
    iv.__insert(new JsonStreamReader("45"));
    Assert.assertEquals(3, (int) iv.get());
    iv.set(100);
    Assert.assertEquals(3, (int) iv.get());
    iv.__patch(new JsonStreamReader("45"));
    Assert.assertEquals(3, (int) iv.get());
    iv.forceSet(1000);
    Assert.assertEquals(3, (int) iv.get());
  }
}
