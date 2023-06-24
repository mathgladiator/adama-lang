/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.assets.cache;

import org.adamalang.common.SimpleExecutor;
import org.junit.Test;

public class MemoryCacheAssetTests {
  @Test
  public void simpleFlow() throws Exception {
    MemoryCacheAsset mca = new MemoryCacheAsset(CacheBattery.ASSET, SimpleExecutor.NOW);
    CacheBattery.driveSimple(mca);
  }

  @Test
  public void simpleFailure() throws Exception {
    MemoryCacheAsset mca = new MemoryCacheAsset(CacheBattery.ASSET, SimpleExecutor.NOW);
    CacheBattery.driveFailure(mca);
  }

  @Test
  public void concurrentEviction() throws Exception {
    MemoryCacheAsset mca = new MemoryCacheAsset(CacheBattery.ASSET, SimpleExecutor.NOW);
    CacheBattery.driveEvictionConcurrent(mca);
  }
}
