/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.caravan.data;

import org.adamalang.caravan.index.Region;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class MemoryMappedFileStorageTests {

  @Test
  public void flow() throws Exception {
    File fileToUse = File.createTempFile("adama_", "storage");
    MemoryMappedFileStorage storage = new MemoryMappedFileStorage(fileToUse, 8196);
    Assert.assertEquals(8196, storage.size());
    storage.write(new Region(8, 2), "Hi".getBytes(StandardCharsets.UTF_8));
    byte[] read = storage.read(new Region(8, 2));
    Assert.assertEquals("Hi", new String(read, StandardCharsets.UTF_8));
    storage.flush();
    storage.close();
    fileToUse.delete();
  }
}
