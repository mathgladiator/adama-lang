/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.stdlib;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class IdCodecTests {
  @Test
  public void battery() {
    Random rng = new Random();
    long v = 0;
    for (int k = 0; k < 1000; k++) {
      String enc = IdCodec.encode(v);
      long v2 = IdCodec.decode(enc);
      Assert.assertEquals(v, v2);
      v += rng.nextInt(1000);
      if (k % 25 == 0) {
        v *= 3;
      }
      v = Math.abs(v);
    }
  }
}
