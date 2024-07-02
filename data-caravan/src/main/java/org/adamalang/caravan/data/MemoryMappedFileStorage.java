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
    if (storeFile.exists()) {
      storeFile.setWritable(true, false);
    }
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
