/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.delta;

import org.adamalang.runtime.delta.secure.TestKey;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class DPairTests {
  @Test
  public void flow() {
    DPair<DInt32, DInt32> dp = new DPair<>();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
    dp.hide(writer);
    dp.clear();
    dp.key(() -> new DInt32()).show(542, writer);
    dp.value(() -> new DInt32()).show(100, writer);
    Assert.assertEquals(96, dp.__memory());
    dp.hide(writer);
    Assert.assertEquals(16, dp.__memory());
    Assert.assertEquals("542100null", stream.toString());
  }
}
