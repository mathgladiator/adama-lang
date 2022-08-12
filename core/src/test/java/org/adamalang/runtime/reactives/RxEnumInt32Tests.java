/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
