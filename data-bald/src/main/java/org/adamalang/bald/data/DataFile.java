package org.adamalang.bald.data;

import org.adamalang.bald.organization.Heap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class DataFile {
  private final RandomAccessFile raf;
  private long length;
  private final MappedByteBuffer buffer;

  public DataFile(File file, long size) throws IOException {
    this.raf = new RandomAccessFile(file, "rw");
    this.buffer = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, size);
    this.length = this.raf.length();
  }

  public void write(Heap.Region region, byte[] memory) throws IOException {
    buffer.put((int) region.position, memory, 0, memory.length);
  }

  public void flush() {
    buffer.force();
  }

  public byte[] read(Heap.Region region) throws IOException  {
    byte[] mem = new byte[region.size];
    buffer.get((int) region.position, mem, 0, mem.length);
    return mem;
  }
}
