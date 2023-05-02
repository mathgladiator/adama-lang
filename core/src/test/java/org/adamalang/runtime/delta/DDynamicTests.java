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
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class DDynamicTests {
  @Test
  public void flow() {
    final var db = new DDynamic();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
    final var A = new NtDynamic("false");
    final var B = new NtDynamic("true");
    db.show(A, writer);
    db.show(A, writer);
    db.hide(writer);
    db.hide(writer);
    db.show(A, writer);
    db.show(A, writer);
    db.show(A, writer);
    db.show(A, writer);
    db.show(B, writer);
    db.show(B, writer);
    db.show(A, writer);
    db.show(A, writer);
    db.show(A, writer);
    Assert.assertEquals("falsenullfalsetruefalse", stream.toString());
    Assert.assertEquals(52, db.__memory());
    db.clear();
  }
}
