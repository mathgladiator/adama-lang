/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.assets.cache;

import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.assets.AssetStream;
import org.adamalang.web.assets.MockAssetStream;
import org.junit.Assert;

import java.nio.charset.StandardCharsets;

public class CacheBattery {
  public static final byte[] CHUNK_1 = "Hello:".getBytes(StandardCharsets.UTF_8);
  public static final byte[] CHUNK_2 = "World".getBytes(StandardCharsets.UTF_8);
  public static final NtAsset ASSET = new NtAsset("id", "name", "type", CHUNK_1.length + CHUNK_2.length, "md5", "sha");

  public static void driveSimple(CachedAsset ca) {
    int expectedSize = CHUNK_1.length + CHUNK_2.length;
    Assert.assertEquals(expectedSize, ca.measure());
    MockAssetStream first = new MockAssetStream();
    MockAssetStream second = new MockAssetStream();
    AssetStream pump = ca.attachWhileInExecutor(first);
    Assert.assertNotNull(pump);
    Assert.assertNull(ca.attachWhileInExecutor(second));
    first.assertHeaders(expectedSize, "type");
    second.assertHeaders(expectedSize, "type");
    pump.headers(1000, "nope");
    pump.body(CHUNK_1, 0, CHUNK_1.length, false);
    first.assertBody("Hello:");
    second.assertBody("Hello:");
    first.assertNotDone();
    second.assertNotDone();
    MockAssetStream third = new MockAssetStream();
    Assert.assertNull(ca.attachWhileInExecutor(third));
    third.assertHeaders(expectedSize, "type");
    third.assertBody("Hello:");
    third.assertNotDone();
    pump.body(CHUNK_2, 0, CHUNK_2.length, true);
    MockAssetStream fourth = new MockAssetStream();
    Assert.assertNull(ca.attachWhileInExecutor(fourth));
    fourth.assertHeaders(expectedSize, "type");
    first.assertBody("Hello:World");
    second.assertBody("Hello:World");
    third.assertBody("Hello:World");
    fourth.assertBody("Hello:World");
    first.assertDone();
    second.assertDone();
    third.assertDone();
    fourth.assertDone();
    first.assertNoFailure();
    second.assertNoFailure();
    third.assertNoFailure();
    fourth.assertNoFailure();
    ca.evict();
  }

  public static Runnable driveEvictionConcurrent(CachedAsset ca) {
    int expectedSize = CHUNK_1.length + CHUNK_2.length;
    Assert.assertEquals(expectedSize, ca.measure());
    MockAssetStream first = new MockAssetStream();
    MockAssetStream second = new MockAssetStream();
    AssetStream pump = ca.attachWhileInExecutor(first);
    Assert.assertNotNull(pump);
    Assert.assertNull(ca.attachWhileInExecutor(second));
    first.assertHeaders(expectedSize, "type");
    second.assertHeaders(expectedSize, "type");
    pump.headers(1000, "nope");
    pump.body(CHUNK_1, 0, CHUNK_1.length, false);
    first.assertBody("Hello:");
    second.assertBody("Hello:");
    first.assertNotDone();
    second.assertNotDone();
    MockAssetStream third = new MockAssetStream();
    Assert.assertNull(ca.attachWhileInExecutor(third));
    third.assertHeaders(expectedSize, "type");
    third.assertBody("Hello:");
    third.assertNotDone();
    ca.evict();
    pump.body(CHUNK_2, 0, CHUNK_2.length, true);

    first.assertBody("Hello:World");
    second.assertBody("Hello:World");
    third.assertBody("Hello:World");
    first.assertDone();
    second.assertDone();
    third.assertDone();
    first.assertNoFailure();
    second.assertNoFailure();
    third.assertNoFailure();

    return () -> {
      MockAssetStream fourth = new MockAssetStream();
      Assert.assertNull(ca.attachWhileInExecutor(fourth));
      fourth.assertFailure(920811);
    };
  }

  public static void driveFailure(CachedAsset ca) {
    int expectedSize = CHUNK_1.length + CHUNK_2.length;
    Assert.assertEquals(expectedSize, ca.measure());
    MockAssetStream first = new MockAssetStream();
    MockAssetStream second = new MockAssetStream();
    AssetStream pump = ca.attachWhileInExecutor(first);
    Assert.assertNotNull(pump);
    Assert.assertNull(ca.attachWhileInExecutor(second));
    first.assertHeaders(expectedSize, "type");
    second.assertHeaders(expectedSize, "type");
    pump.headers(1000, "nope");
    pump.body(CHUNK_1, 0, CHUNK_1.length, false);
    first.assertBody("Hello:");
    second.assertBody("Hello:");
    first.assertNotDone();
    second.assertNotDone();
    MockAssetStream third = new MockAssetStream();
    Assert.assertNull(ca.attachWhileInExecutor(third));
    third.assertHeaders(expectedSize, "type");
    third.assertBody("Hello:");
    third.assertNotDone();
    pump.failure(-13);
    MockAssetStream fourth = new MockAssetStream();
    Assert.assertNull(ca.attachWhileInExecutor(fourth));
    first.assertFailure(-13);
    second.assertFailure(-13);
    second.assertFailure(-13);
    fourth.assertFailure(-13);
  }
}
