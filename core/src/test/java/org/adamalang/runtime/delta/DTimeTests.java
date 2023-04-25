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
import org.adamalang.runtime.natives.NtTime;
import org.junit.Assert;
import org.junit.Test;

public class DTimeTests {
  @Test
  public void flow() {
    final var db = new DTime();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
    db.show(new NtTime(13, 14), writer);
    db.show(new NtTime(15, 17), writer);
    db.hide(writer);
    db.hide(writer);
    db.show(new NtTime(13, 14), writer);
    db.show(new NtTime(15, 17), writer);
    Assert.assertEquals("\"13:14\"\"15:17\"null\"13:14\"\"15:17\"", stream.toString());
    Assert.assertEquals(56, db.__memory());
    db.clear();
  }
}
