/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
