/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.async;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.reactives.RxInt32;
import org.adamalang.runtime.reactives.RxInt64;
import org.junit.Assert;
import org.junit.Test;

public class OutstandingFutureTrackerTests {

  public OutstandingFutureTracker makeFutures() {
    final var src = new RxInt32(null, 0);
    final var futures = new OutstandingFutureTracker(src, new TimeoutTracker(new RxInt64(null, 0)));
    return futures;
  }

  @Test
  public void caching() {
    final var futures = makeFutures();
    final var futureA = futures.make("chan", NtPrincipal.NO_ONE);
    futures.restore();
    final var futureB = futures.make("chan", NtPrincipal.NO_ONE);
    Assert.assertTrue(futureA == futureB);
    futures.commit();
    final var futureC = futures.make("chan", NtPrincipal.NO_ONE);
    Assert.assertTrue(futureA != futureC);
  }

  @Test
  public void multi_user_flow() {
    final var futures = makeFutures();
    final var A = new NtPrincipal("a", "local");
    final var B = new NtPrincipal("b", "local");
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
    Assert.assertEquals(
        "\"outstanding\":[{\"a\":1},{\"a\":2},{\"a\":3}],\"blockers\":[{\"agent\":\"b\",\"authority\":\"local\"},{\"agent\":\"a\",\"authority\":\"local\"}]",
        viewA.toString());
    Assert.assertEquals(
        "\"outstanding\":[{\"b\":1},{\"b\":2},{\"b\":3}],\"blockers\":[{\"agent\":\"b\",\"authority\":\"local\"},{\"agent\":\"a\",\"authority\":\"local\"}]",
        viewB.toString());
  }
}
