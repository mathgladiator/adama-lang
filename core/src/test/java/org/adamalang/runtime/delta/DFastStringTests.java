/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.delta;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class DFastStringTests {
  @Test
  public void flow() {
    final var db = new DFastString();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null);
    db.show("a", writer);
    db.show("a", writer);
    db.show("b", writer);
    db.show("b", writer);
    db.show("c", writer);
    db.show("c", writer);
    db.hide(writer);
    db.hide(writer);
    db.show("d", writer);
    db.show("d", writer);
    db.show("e", writer);
    db.show("e", writer);
    Assert.assertEquals("\"a\"\"b\"\"c\"null\"d\"\"e\"", stream.toString());
  }
}
