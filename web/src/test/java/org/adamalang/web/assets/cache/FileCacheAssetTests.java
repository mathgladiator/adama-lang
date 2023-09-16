/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
