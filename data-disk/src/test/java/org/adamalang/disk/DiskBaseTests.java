/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;

public class DiskBaseTests {
  @Test
  public void flow() throws Exception {
    File file = new File(File.createTempFile("prefix", "suffix").getParentFile(), " base"+ System.currentTimeMillis());
    file.mkdir();
    File fileA = new File(file, "wal");
    File fileB = new File(file, "data");
    new DiskBase(new DiskDataMetrics(new NoOpMetricsFactory()), SimpleExecutor.NOW, file);
    Assert.assertTrue(fileA.exists());
    Assert.assertTrue(fileB.exists());
    fileA.delete();
    fileB.delete();
    Files.writeString(fileA.toPath(), "X");
    Files.writeString(fileB.toPath(), "X");
    try {
      new DiskBase(new DiskDataMetrics(new NoOpMetricsFactory()), SimpleExecutor.NOW, file);
      Assert.fail();
    } catch (Exception ex) {
      Assert.assertTrue(ex.getMessage().startsWith("Failed to detect/find/create wal working directory:"));;
    }
    fileA.delete();
    try {
      new DiskBase(new DiskDataMetrics(new NoOpMetricsFactory()), SimpleExecutor.NOW, file);
      Assert.fail();
    } catch (Exception ex) {
      Assert.assertTrue(ex.getMessage().startsWith("Failed to detect/find/create root directory:"));;
    }
    fileB.delete();
    new DiskBase(new DiskDataMetrics(new NoOpMetricsFactory()), SimpleExecutor.NOW, file);
    fileA.delete();
    fileB.delete();
    file.delete();
  }
}
