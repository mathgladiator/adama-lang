/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
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
    Assert.assertEquals("{\"@t\":1,\"agent\":\"?\",\"authority\":\"?\"}{\"@t\":1,\"agent\":\"a\",\"authority\":\"local\"}null{\"@t\":1,\"agent\":\"a\",\"authority\":\"local\"}{\"@t\":1,\"agent\":\"?\",\"authority\":\"?\"}", stream.toString());
  }
}
