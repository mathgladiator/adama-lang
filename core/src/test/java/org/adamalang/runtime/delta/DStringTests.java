/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.delta;

import org.adamalang.runtime.delta.secure.TestKey;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.junit.Assert;
import org.junit.Test;

public class DStringTests {
  @Test
  public void flow() {
    final var db = new DString();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtPrincipal.NO_ONE, stream, null, TestKey.ENCODER);
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
    Assert.assertEquals(42, db.__memory());
    db.clear();
  }
}
