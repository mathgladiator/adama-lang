/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.delta;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class DInt32Tests {
  @Test
  public void flow() {
    final var db = new DInt32();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null);
    db.show(1, writer);
    db.show(1, writer);
    db.show(22, writer);
    db.show(22, writer);
    db.show(3, writer);
    db.show(3, writer);
    db.hide(writer);
    db.hide(writer);
    db.show(4, writer);
    db.show(4, writer);
    db.show(5, writer);
    db.show(5, writer);
    Assert.assertEquals("1223null45", stream.toString());
  }
}
