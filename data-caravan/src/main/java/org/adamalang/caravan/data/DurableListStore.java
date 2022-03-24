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
import org.adamalang.caravan.index.heaps.IndexedHeap;
import org.adamalang.caravan.index.heaps.SplitHeat;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;

public class DurableListStore {
  // the data structures to manage the giant linear space
  private final DurableListStoreMetrics metrics;
  private final Index index;
  private final Heap heap;

  // the actual file
  private final RandomAccessFile storage;
  private final MappedByteBuffer memory;

  // the directory containing the write-ahead log; since we use Files.move, we create temporary files to cut over
  private final File walRoot;


  // We use a netty buffer for writing data; TODO: use a different construction since we have minimal overhead
  private final ByteBuf buffer;
  private byte[] pageBuffer;

  // the write ahead log stream
  private DataOutputStream output;
  private long bytesWrittenToLog;

  // how many bytes until we introduce a flush
  private final int flushCutOffBytes;

  // how big will we allow the log file to get
  private final long maxLogSize;

  // notifications when the requested action was committed
  private final ArrayList<Runnable> notifications;

  /**
   * Construct the durable list store!
   *
   * @param metrics useful insights into the store
   * @param storeFile the file used to store the data
   * @param walRoot the directory containing the write ahead log
   * @param size - the size of the file to use
   * @param flushCutOffBytes - the number of bytes until we force a flush
   * @param maxLogSize - how big can the log get until we flush and cut the log
   * @throws IOException
   */
  public DurableListStore(DurableListStoreMetrics metrics, File storeFile, File walRoot, long size, int flushCutOffBytes, long maxLogSize) throws IOException {
    this.metrics = metrics;
    this.index = new Index();
    this.heap = new SplitHeat(new IndexedHeap(size / 4), 8196, size / 4, new IndexedHeap((size * 3) / 4));

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

  /** internal: open the log for writing */
  private void openLogForWriting() throws IOException {
    this.output = new DataOutputStream(new FileOutputStream(new File(walRoot, "WAL")));
    this.bytesWrittenToLog = 0;
  }

  /** internal: load and commit the data from the write-ahead log */
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

  /** internal: prepare a new write-ahead file */
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

  /** internal: force everything to flush, prepare a new file, the move the new file in place, and open it */
  private void cutOver() throws IOException {
    memory.force();
    output.close();
    File newFile = prepare();
    Files.move(newFile.toPath(), new File(walRoot, "WAL").toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    openLogForWriting();
  }

  /** internal: write a page to the log */
  private boolean writePage(DataOutputStream dos, ByteBuf page) throws IOException {
    if (page.writerIndex() == 0) {
      return false;
    }
    dos.writeInt(page.writerIndex());
    bytesWrittenToLog += page.writerIndex();

    // TODO: we are using too many buffers, we should simply use an arraylist of writes then build it up directly
    // maybe, it's ok to use DataOutputstream directly? maybe with BufferedOutputStream?
    if (page.hasArray() && page.writerIndex() < page.array().length) {
      dos.write(page.array(), 0, page.writerIndex());
    } else {
      // TODO: this can be optimized a bunch
      while (page.isReadable()) {
        int toRead = page.readableBytes();
        if (toRead >= pageBuffer.length) {
          pageBuffer = new byte[toRead + flushCutOffBytes];
        }
        page.readBytes(pageBuffer, 0, toRead);
        dos.write(pageBuffer, 0, toRead);
      }
    }
    return true;
  }

  /** append a byte array to the given id */
  public Integer append(long id, byte[] bytes, Runnable notification) {
    Region where = heap.ask(bytes.length);
    if (where == null) {
      // we are out of space
      return null;
    }
    this.notifications.add(notification);
    // Java 13+ has a better API; worth considering...
    memory.slice().position((int) where.position).put(bytes);
    int size = index.append(id, where);
    new Append(id, where.position, bytes).write(buffer);
    if (buffer.writerIndex() > flushCutOffBytes) {
      // the buffer is full, so flush it
      flush(false);
    }
    return size;
  }

  /** read the given object by scanning all appends */
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

  /** remove the $count appends from the head of the object */
  public boolean trim(long id, int count, Runnable notification) {
    ArrayList<Region> regions = index.trim(id, count);
    if (regions != null && regions.size() > 0) {
      this.notifications.add(notification);
      new Trim(id, regions.size()).write(buffer);
      for (Region region : regions) {
        heap.free(region);
      }
      if (buffer.writerIndex() > flushCutOffBytes) {
        flush(false);
      }
      return true;
    }
    return false;
  }

  /** delete the given object by id */
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

  /** does the given id exist within the system */
  public boolean exists(long id) {
    return index.exists(id);
  }

  /** flush to disk */
  public void flush(boolean forceCutOver) {
    try {
      metrics.flush.run();
      if (writePage(output, buffer)) {
        output.flush();
      }
      buffer.resetReaderIndex();
      buffer.resetWriterIndex();

      if (bytesWrittenToLog >= maxLogSize || forceCutOver) {
        cutOver();
      }

      // feels excessive, is it better to copy OR re-init? Could we trust the client to simply _not_ be re-entrant?
      ArrayList<Runnable> notificationClone = new ArrayList<>(notifications);
      notifications.clear();

      for (Runnable notification : notificationClone) {
        notification.run();
      }
    } catch (IOException ex) {
      ex.printStackTrace();
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
