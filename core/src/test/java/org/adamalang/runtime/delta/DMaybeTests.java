/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.delta;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class DMaybeTests {
  @Test
  public void flow() {
    final var db = new DMaybe<DBoolean>();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream);
    final var a = db.get(DBoolean::new);
    final var b = db.get(DBoolean::new);
    Assert.assertTrue(a == b);
    db.hide(writer);
    db.hide(writer);
    final var c = db.get(DBoolean::new);
    Assert.assertTrue(a != c);
    Assert.assertEquals("null", stream.toString());
  }
}
