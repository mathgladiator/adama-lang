/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
