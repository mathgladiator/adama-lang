/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.delta;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class DDoubleTests {
  @Test
  public void flow() {
    final var db = new DDouble();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null);
    db.show(1d, writer);
    db.show(1d, writer);
    db.show(2d, writer);
    db.show(2d, writer);
    db.show(3.14, writer);
    db.show(3.14, writer);
    db.hide(writer);
    db.hide(writer);
    db.show(42d, writer);
    db.show(42d, writer);
    db.show(2.71, writer);
    db.show(2.71, writer);
    Assert.assertEquals("1.02.03.14null42.02.71", stream.toString());
  }
}
