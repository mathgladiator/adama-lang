/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.delta;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.json.PrivateLazyDeltaWriter;
import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class DClientTests {
  @Test
  public void flow() {
    final var db = new DClient();
    final var stream = new JsonStreamWriter();
    final var writer = PrivateLazyDeltaWriter.bind(NtClient.NO_ONE, stream, null);
    final var A = new NtClient("a", "local");
    db.show(NtClient.NO_ONE, writer);
    db.show(NtClient.NO_ONE, writer);
    db.show(A, writer);
    db.show(A, writer);
    db.hide(writer);
    db.hide(writer);
    db.show(A, writer);
    db.show(A, writer);
    db.show(NtClient.NO_ONE, writer);
    db.show(NtClient.NO_ONE, writer);
    Assert.assertEquals("{\"agent\":\"?\",\"authority\":\"?\"}{\"agent\":\"a\",\"authority\":\"local\"}null{\"agent\":\"a\",\"authority\":\"local\"}{\"agent\":\"?\",\"authority\":\"?\"}", stream.toString());
  }
}
