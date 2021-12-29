/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime;

import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class JsonConstruction {
  @Test
  public void sendMessageBasic() throws Exception {
    final var setup =
        new RealDocumentSetup(
            "@connected (who) { return true; } message M { int x; double y; string z; bool b; } int xx; double yy; string zz; bool bb; channel foo(M m) { xx = m.x; yy = m.y; zz = m.z; bb = m.b; }");
    setup.document.connect(NtClient.NO_ONE, new RealDocumentSetup.AssertInt(3));
    setup.document.send(
        NtClient.NO_ONE,
        "marker",
        "foo",
        "{\"x\":123,\"y\":3.14,\"z\":\"w00t\",\"b\":true}",
        new RealDocumentSetup.AssertInt(5));
    Assert.assertEquals(
        "{\"xx\":123,\"yy\":3.14,\"zz\":\"w00t\",\"bb\":true,\"__state\":\"\",\"__constructed\":true,\"__next_time\":\"0\",\"__last_expire_time\":\"0\",\"__blocked\":false,\"__seq\":5,\"__entropy\":\"8270396388693936851\",\"__auto_future_id\":0,\"__connection_id\":1,\"__message_id\":1,\"__time\":\"0\",\"__auto_table_row_id\":0,\"__dedupe\":{\"?/?/marker\":\"0\"},\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}}}",
        setup.document.json());
  }
}
