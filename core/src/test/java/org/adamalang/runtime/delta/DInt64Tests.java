/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.delta;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class DInt64Tests {
  @Test
  public void flow() {
    final var db = new DInt64();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null);
    db.show(1L, writer);
    db.show(1L, writer);
    db.show(22L, writer);
    db.show(22L, writer);
    db.show(3L, writer);
    db.show(3L, writer);
    db.hide(writer);
    db.hide(writer);
    db.show(4L, writer);
    db.show(4L, writer);
    db.show(5L, writer);
    db.show(5L, writer);
    Assert.assertEquals("\"1\"\"22\"\"3\"null\"4\"\"5\"", stream.toString());
  }
}
