/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.delta;

import org.adamalang.runtime.delta.secure.TestKey;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class DMaybeTests {
  @Test
  public void flow() {
    final var db = new DMaybe<DBoolean>();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
    final var a = db.get(DBoolean::new);
    final var b = db.get(DBoolean::new);
    Assert.assertSame(a, b);
    db.hide(writer);
    db.hide(writer);
    final var c = db.get(DBoolean::new);
    Assert.assertNotSame(a, c);
    Assert.assertEquals("null", stream.toString());
    Assert.assertEquals(80, db.__memory());
    db.clear();
  }
}
