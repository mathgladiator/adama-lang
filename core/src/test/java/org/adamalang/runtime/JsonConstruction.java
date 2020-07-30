/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime;

import org.adamalang.runtime.natives.NtClient;
import org.junit.Assert;
import org.junit.Test;

public class JsonConstruction {
  @Test
  public void sendMessageBasic() throws Exception {
    final var setup = new RealDocumentSetup("@connected (who) { return true; } message M { int x; double y; string z; bool b; } int xx; double yy; string zz; bool bb; channel foo(M m) { xx = m.x; yy = m.y; zz = m.z; bb = m.b; }");
    setup.drive(setup.transactor.construct(NtClient.NO_ONE, "{}", "123"));
    setup.drive(setup.transactor.connect(NtClient.NO_ONE));
    setup.transactor.send(NtClient.NO_ONE, "foo", "{\"x\":123,\"y\":3.14,\"z\":\"w00t\",\"b\":true}");
    setup.transactor.drive();
    Assert.assertEquals(
        "{\"__constructed\":true,\"__entropy\":\"8270396388693936851\",\"__seedUsed\":\"786253046697430328\",\"__seq\":5,\"__connection_id\":1,\"__clients\":{\"0\":{\"agent\":\"?\",\"authority\":\"?\"}},\"__message_id\":1,\"xx\":123,\"yy\":3.14,\"zz\":\"w00t\",\"bb\":true}",
        setup.logger.node.toString());
  }
}
