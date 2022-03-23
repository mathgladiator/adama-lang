package org.adamalang.caravan.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.caravan.contracts.ByteArrayStream;
import org.adamalang.caravan.entries.Append;
import org.adamalang.caravan.entries.Delete;
import org.adamalang.caravan.entries.OrganizationSnapshot;
import org.adamalang.caravan.entries.Trim;
import org.adamalang.caravan.index.Heap;
import org.adamalang.caravan.index.Index;
import org.adamalang.caravan.index.Region;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;

public class DurableListStore {
  // the data structures to manage the giant linear space
  private final Index index;
  private final Heap heap;

  // the actual file
  private final RandomAccessFile storage;
  private final MappedByteBuffer memory;

  private final File walRoot;

  private final ByteBuf buffer;
  private DataOutputStream output;
  private int flushCutOffBytes;
  private byte[] pageBuffer;
  private long maxLogSize;
  private long bytesWrittenToLog;
  private ArrayList<Runnable> notifications;

  public DurableListStore(File storeFile, File walRoot, long size, int flushCutOffBytes, long maxLogSize) throws IOException {
    // initialize the data structures
    this.index = new Index();
    this.heap = new Heap(size);

    // memory map the storage file
    this.storage = new RandomAccessFile(storeFile, "rw");
    storage.setLength(size);
    this.memory = storage.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, size);
    this.notifications = new ArrayList<>();

    // build the buffer
    this.walRoot = walRoot;
    this.buffer = Unpooled.buffer(flushCutOffBytes * 5 / 4);
    this.output = null;
    this.flushCutOffBytes = flushCutOffBytes;
    this.pageBuffer = new byte[flushCutOffBytes];
    File walFile = new File(walRoot, "WAL");
    if (walFile.exists()) {
      try {
        load(walFile);
      } catch (IOException ioe) {
        Files.copy(walFile.toPath(), new File(walFile, "BAD-WAL-" + System.currentTimeMillis()).toPath());
      }
      Files.move(prepare().toPath(), walFile.toPath());
    }
    this.maxLogSize = maxLogSize;
    this.bytesWrittenToLog = 0;
    openLogForWriting();
  }

  private void openLogForWriting() throws IOException {
    this.output = new DataOutputStream(new FileOutputStream(new File(walRoot, "WAL")));
    this.bytesWrittenToLog = 0;
  }

  private void load(File walFile) throws IOException {
    DataInputStream input = new DataInputStream(new FileInputStream(walFile));
    try {
      int pageSize;
      while ((pageSize = input.readInt()) > 0) {
        byte[] chunk = new byte[pageSize];
        input.readFully(chunk);
        ByteBuf buf = Unpooled.wrappedBuffer(chunk);
        while (buf.isReadable()) {
          byte code = buf.readByte();
          switch (code) {
            case 0x42: // append
              {
                Append append = Append.readAfterTypeId(buf);
                Region region = heap.ask(append.bytes.length);
                // region.position BETTER be append.position
                index.append(append.id, region);
                memory.slice().position((int) region.position).put(append.bytes);
              }
              break;
            case 0x66: // delete
              for (Region region : index.delete(Delete.readAfterTypeId(buf).id)) {
                heap.free(region);
              }
              break;
            case 0x55: // snapshot
              OrganizationSnapshot.populateAfterTypeId(buf, heap, index);
              break;
            case 0x13: // trim
              Trim trim = Trim.readAfterTypeId(buf);
              for (Region region : index.trim(trim.id, trim.count)) {
                heap.free(region);
              }
              break;
            default:
              throw new IOException("unrecogized code:" + code);
          }
        }
      }
    } finally {
      input.close();
    }
  }

  private File prepare() throws IOException {
    File newWalFile = new File(walRoot, "WAL.NEW-" + System.currentTimeMillis());
    DataOutputStream newOutput = new DataOutputStream(new FileOutputStream(newWalFile));
    try {
      ByteBuf first = Unpooled.buffer();
      new OrganizationSnapshot(heap, index).write(first);
      writePage(newOutput, first);
      newOutput.flush();
      return newWalFile;
    } finally {
      newOutput.close();
    }
  }

  private void cutOver() throws IOException {
    output.flush();
    memory.force();
    output.close();
    File newFile = prepare();
    Files.move(newFile.toPath(), new File(walRoot, "WAL").toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    openLogForWriting();
  }

  public boolean append(long id, byte[] bytes, Runnable notification) {
    Region where = heap.ask(bytes.length);
    if (where == null) {
      return false;
    }
    this.notifications.add(notification);
    memory.slice().position((int) where.position).put(bytes);
    index.append(id, where);
    new Append(id, where.position, bytes).write(buffer);
    if (buffer.writerIndex() > flushCutOffBytes) {
      flush(false);
    }
    return true;
  }

  public void read(long id, ByteArrayStream streamback) {
    Iterator<Region> it = index.get(id);
    int at = 0;
    while (it.hasNext()) {
      Region region = it.next();
      byte[] mem = new byte[region.size];
      memory.slice().position((int) region.position).get(mem);
      streamback.next(at, mem);
      at++;
    }
    streamback.finished();
  }

  public void trim(long id, int count, Runnable notification) {
    this.notifications.add(notification);
    ArrayList<Region> regions = index.trim(id, count);
    if (regions != null) {
      new Trim(id, regions.size()).write(buffer);
      for (Region region : regions) {
        heap.free(region);
      }
      if (buffer.writerIndex() > flushCutOffBytes) {
        flush(false);
      }
    }
  }

  public boolean delete(long id, Runnable notification) {
    ArrayList<Region> regions = index.delete(id);
    if (regions != null) {
      for (Region region : regions) {
        heap.free(region);
      }
      new Delete(id).write(buffer);
      this.notifications.add(notification);
      if (buffer.writerIndex() > flushCutOffBytes) {
        flush(false);
      }
      return true;
    } else {
      return false;
    }
  }

  public boolean exists(long id) {
    return index.exists(id);
  }

  private void writePage(DataOutputStream dos, ByteBuf page) throws IOException {
    dos.writeInt(page.writerIndex());
    while (page.isReadable()) {
      int toRead = page.readableBytes();
      if (toRead >= pageBuffer.length) {
        pageBuffer = new byte[toRead + flushCutOffBytes];
      }
      page.readBytes(pageBuffer, 0, toRead);
      dos.write(pageBuffer, 0, toRead);
      bytesWrittenToLog += toRead;
    }
  }

  public void flush(boolean forceCutOver) {
    try {
      writePage(output, buffer);
      output.flush();
      buffer.resetReaderIndex();
      buffer.resetWriterIndex();

      if (bytesWrittenToLog > maxLogSize || forceCutOver) {
        cutOver();
      }

      ArrayList<Runnable> notificationClone = new ArrayList<>(notifications);
      notifications.clear();

      for (Runnable notification : notificationClone) {
        notification.run();
      }
    } catch (IOException ex) {
      System.exit(100);
    }
  }

  public void shutdown() throws IOException {
    output.flush();
    output.close();
    memory.force();
    storage.close();
  }
}
