/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
    pump.headers(1000, "nope", "md5");
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
    pump.headers(1000, "nope", "md5");
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
    pump.headers(1000, "nope", "md5");
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
