/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.async;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.reactives.RxInt32;
import org.junit.Assert;
import org.junit.Test;

public class OutstandingFutureTrackerTests {
  @Test
  public void caching() {
    final var src = new RxInt32(null, 0);
    final var futures = new OutstandingFutureTracker(src);
    final var futureA = futures.make("chan", NtClient.NO_ONE);
    futures.restore();
    final var futureB = futures.make("chan", NtClient.NO_ONE);
    Assert.assertTrue(futureA == futureB);
    futures.commit();
    final var futureC = futures.make("chan", NtClient.NO_ONE);
    Assert.assertTrue(futureA != futureC);
  }

  @Test
  public void multi_user_flow() {
    final var src = new RxInt32(null, 0);
    final var futures = new OutstandingFutureTracker(src);
    final var A = new NtClient("a", "local");
    final var B = new NtClient("b", "local");
    final var futureA1 = futures.make("chan", A);
    final var futureB1 = futures.make("chan", B);
    final var futureA2 = futures.make("chan", A);
    final var futureB2 = futures.make("chan", B);
    final var futureA3 = futures.make("chan", A);
    final var futureB3 = futures.make("chan", B);
    futureA1.json = "{\"a\":1}";
    futureA2.json = "{\"a\":2}";
    futureA3.json = "{\"a\":3}";
    futureB1.json = "{\"b\":1}";
    futureB2.json = "{\"b\":2}";
    futureB3.json = "{\"b\":3}";
    final var viewA = new JsonStreamWriter();
    final var viewB = new JsonStreamWriter();
    futures.dump(viewA, A);
    futures.dump(viewB, B);
    Assert.assertEquals("\"outstanding\":[{\"a\":1},{\"a\":2},{\"a\":3}],\"blockers\":[{\"agent\":\"b\",\"authority\":\"local\"},{\"agent\":\"a\",\"authority\":\"local\"}]", viewA.toString());
    Assert.assertEquals("\"outstanding\":[{\"b\":1},{\"b\":2},{\"b\":3}],\"blockers\":[{\"agent\":\"b\",\"authority\":\"local\"},{\"agent\":\"a\",\"authority\":\"local\"}]", viewB.toString());
  }
}
