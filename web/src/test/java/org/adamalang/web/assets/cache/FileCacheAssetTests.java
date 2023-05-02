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

import org.adamalang.common.SimpleExecutor;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class FileCacheAssetTests {
  @Test
  public void simpleFlow() throws Exception {
    File root = File.createTempFile("adamafcat", "001");
    root.delete();
    Assert.assertTrue(root.mkdir());
    try {
      FileCacheAsset fca = new FileCacheAsset(123L, root, CacheBattery.ASSET, SimpleExecutor.NOW);
      Assert.assertEquals(1, root.listFiles().length);
      CacheBattery.driveSimple(fca);
      Assert.assertEquals(0, root.listFiles().length);
    } finally {
      for (File x : root.listFiles()) {
        x.delete();
      }
      root.delete();
    }
  }

  @Test
  public void simpleFailure() throws Exception {
    File root = File.createTempFile("adamafcat", "002");
    root.delete();
    Assert.assertTrue(root.mkdir());
    try {
      FileCacheAsset fca = new FileCacheAsset(123L, root, CacheBattery.ASSET, SimpleExecutor.NOW);
      Assert.assertEquals(1, root.listFiles().length);
      CacheBattery.driveFailure(fca);
      Assert.assertEquals(0, root.listFiles().length);
    } finally {
      for (File x : root.listFiles()) {
        x.delete();
      }
      root.delete();
    }
  }

  @Test
  public void concurrentEviction() throws Exception {
    File root = File.createTempFile("adamafcat", "003");
    root.delete();
    Assert.assertTrue(root.mkdir());
    try {
      FileCacheAsset fca = new FileCacheAsset(123L, root, CacheBattery.ASSET, SimpleExecutor.NOW);
      Assert.assertEquals(1, root.listFiles().length);
      CacheBattery.driveEvictionConcurrent(fca).run();
      Assert.assertEquals(0, root.listFiles().length);
    } finally {
      for (File x : root.listFiles()) {
        x.delete();
      }
      root.delete();
    }
  }
}
