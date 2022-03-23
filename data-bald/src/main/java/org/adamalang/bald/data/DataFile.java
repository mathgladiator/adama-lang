package org.adamalang.bald.data;

import org.adamalang.bald.organization.Region;

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
    raf.setLength(size);
    this.buffer = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, size);
    this.length = this.raf.length();
  }

  public void write(Region region, byte[] memory) throws IOException {
    buffer.slice().position((int) region.position).put(memory);
  }

  public void flush() {
    buffer.force();
  }

  public byte[] read(Region region) throws IOException  {
    byte[] mem = new byte[region.size];
    buffer.slice().position((int) region.position).get(mem);
    //buffer.slice((int) region.position, region.size).get(mem);
    return mem;
  }
}
