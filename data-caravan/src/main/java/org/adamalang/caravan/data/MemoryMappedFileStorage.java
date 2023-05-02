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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/** implementation of a Storage using a memory mapped file */
public class MemoryMappedFileStorage implements Storage {
  private final RandomAccessFile storage;
  private final MappedByteBuffer memory;
  private final long size;
  private boolean dirty;

  public MemoryMappedFileStorage(File storeFile, long size) throws IOException {
    this.storage = new RandomAccessFile(storeFile, "rw");
    storage.setLength(size);
    this.size = size;
    this.memory = storage.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, size);
    this.dirty = false;
  }

  @Override
  public long size() {
    return size;
  }

  @Override
  public void write(Region region, byte[] mem) {
    dirty = true;
    memory.slice().position((int) region.position).put(mem);
  }

  @Override
  public byte[] read(Region region) {
    byte[] mem = new byte[region.size];
    memory.slice().position((int) region.position).get(mem);
    return mem;
  }

  @Override
  public void flush() throws IOException {
    if (dirty) {
      memory.force();
    }
  }

  @Override
  public void close() throws IOException {
    storage.close();
  }
}
