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
package org.adamalang.caravan.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.PlatformDependent;
import org.adamalang.caravan.contracts.ByteArrayStream;
import org.adamalang.caravan.entries.*;
import org.adamalang.caravan.events.EventCodec;
import org.adamalang.caravan.events.RestoreWalker;
import org.adamalang.caravan.index.*;
import org.adamalang.runtime.data.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DurableListStore {
  private static final Logger LOGGER = LoggerFactory.getLogger(DurableListStore.class);
  private static final boolean IS_NOISY = System.getProperty("NOISY_DLS") != null;
  // the data structures to manage the giant linear space
  private final DiskMetrics metrics;
  private final Index index;
  private final Heap heap;
  private final KeyMap keymap;

  // storage used for reading/writing
  private final Storage storage;

  // the directory containing the write-ahead log; since we use Files.move, we create temporary files to cut over
  private final File walRoot;

  // We use a netty buffer for writing data; TODO: use a different construction since we have minimal overhead
  private final ByteBuf buffer;
  // how many bytes until we introduce a flush
  private final int flushCutOffBytes;
  // how big will we allow the log file to get
  private final long maxLogSize;
  // notifications when the requested action was committed
  private final ArrayList<Runnable> notifications;
  private byte[] pageBuffer;
  // the write ahead log stream
  private DataOutputStream output;
  private long bytesWrittenToLog;

  /**
   * Construct the durable list store!
   * @param metrics useful insights into the store
   * @param storeFile the directory used to store the data
   * @param walRoot the directory containing the write ahead log
   * @param size - the size of the file to use
   * @param flushCutOffBytes - the number of bytes until we force a flush
   * @param maxLogSize - how big can the log get until we flush and cut the log
   * @throws IOException
   */
  public DurableListStore(DiskMetrics metrics, File storeFile, File walRoot, long size, int flushCutOffBytes, long maxLogSize) throws IOException {
    this.metrics = metrics;
    this.index = new Index();
    this.keymap = new KeyMap();
    DurableListStoreSizing sizing = new DurableListStoreSizing(size, storeFile);
    this.heap = sizing.heap;
    this.storage = sizing.storage;

    this.notifications = new ArrayList<>();
    // build the buffer
    this.walRoot = walRoot;
    this.buffer = Unpooled.buffer(flushCutOffBytes * 8 / 7);
    if (IS_NOISY) {
      LOGGER.error("created buffer:" + buffer.getClass() + " (ideal:" + (flushCutOffBytes * 5 / 4));
      LOGGER.error("buffer capacity:" + buffer.capacity());
      LOGGER.error("platform has unsafe:" + PlatformDependent.hasUnsafe());
    }
    this.output = null;
    this.flushCutOffBytes = flushCutOffBytes;
    this.pageBuffer = new byte[flushCutOffBytes];
    File walFile = new File(walRoot, "WAL");
    if (walFile.exists()) {
      walFile.setWritable(true);
      try {
        load(walFile);
      } catch (IOException ioe) {
        LOGGER.error("wal-truncated-exception:", ioe);
        Files.copy(walFile.toPath(), new File(walRoot, "BAD-WAL-" + System.currentTimeMillis()).toPath());
      }
      Files.move(prepare().toPath(), walFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    }
    this.maxLogSize = maxLogSize;
    this.bytesWrittenToLog = 0;
    openLogForWriting();
    LOGGER.error("DurableListStore opened; type=" + (buffer.getClass()) + ", PlatformDependent: unsafe" + PlatformDependent.hasUnsafe() + ", prefer-direct:" + PlatformDependent.directBufferPreferred() + ", buffer-capacity:" + buffer.capacity());
  }

  /** build a report of the free disk space available; side-effect: emit metrics */
  public void report() {
    Report report = new Report();
    this.heap.report(report);
    metrics.total_storage_allocated.set((int) (report.getTotalBytes() / 1000000L));
    metrics.free_storage_available.set((int) (report.getFreeBytesAvailable() / 1000000L));
    metrics.alarm_storage_over_80_percent.set(report.alarm(0.2) ? 1 : 0);
    this.index.report(metrics);
  }

  /** return a list of ids */
  public Set<Long> listIndex() {
    return index.list();
  }

  /** return a list of keys */
  public Map<Key, Integer> map() {
    return keymap.copy();
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
              if (region.position != append.position || region.size != append.bytes.length) {
                throw new IOException("heap corruption!");
              }
              index.append(append.id, new AnnotatedRegion(region.position, region.size, append.seq, append.assetBytes));
              storage.write(region, append.bytes);
            }
            break;
            case 0x66: // delete
              for (Region region : index.delete(Delete.readAfterTypeId(buf).id)) {
                heap.free(region);
              }
              break;
            case 0x57: // snapshot
              OrganizationSnapshot.populateAfterTypeId(buf, heap, index, keymap);
              break;
            case 0x13: // trim
              Trim trim = Trim.readAfterTypeId(buf);
              for (Region region : index.trim(trim.id, trim.maxSize)) {
                heap.free(region);
              }
              break;
            case 0x30:
              keymap.apply(MapKey.readAfterTypeId(buf));
              break;
            case 0x36:
              keymap.apply(DelKey.readAfterTypeId(buf));
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
    ByteBuf first = Unpooled.buffer();
    new OrganizationSnapshot(heap, index, keymap).write(first);
    if (IS_NOISY) {
      LOGGER.error("preparing new WAL file;" + keymap.toString());
    }
    writePage(newOutput, first);
    newOutput.flush();
    newOutput.close();
    return newWalFile;
  }

  /** internal: open the log for writing */
  private void openLogForWriting() throws IOException {
    File file = new File(walRoot, "WAL");
    if (file.exists()) {
      file.setWritable(true, false);
    }
    this.output = new DataOutputStream(new FileOutputStream(file, true));
    this.bytesWrittenToLog = 0;
  }

  /** internal: write a page to the log */
  private boolean writePage(DataOutputStream dos, ByteBuf page) throws IOException {
    if (page.writerIndex() == 0) {
      return false;
    }
    if (IS_NOISY) {
      LOGGER.error("writePage(WI:" + page.writerIndex() + ", IR:" + page.isReadable() + ", HA:" + page.hasArray() + ", CAP:" + buffer.capacity() + ")");
    }
    dos.writeInt(page.writerIndex());
    bytesWrittenToLog += page.writerIndex();

    // TODO: we are using too many buffers, we should simply use an arraylist of writes then build it up directly
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

  /** how many bytes are available to allocate */
  public long available() {
    return heap.available();
  }

  /** helper: pair a region to a byte array */
  private class RegionByteArrayPairing {
    private final Region where;
    private final byte[] bytes;
    private RegionByteArrayPairing(Region where, byte[] bytes) {
      this.where = where;
      this.bytes = bytes;
    }
  }

  private int mapKeyToLocalId(Key key) {
    Integer result = keymap.get(key);
    if (result == null) {
      MapKey mk = keymap.inventAndApply(key);
      mk.write(buffer);
      return mk.id;
    }
    return result;
  }

  /** append a byte array to the given id */
  public Integer append(Key key, ArrayList<byte[]> batch, int seq, long assetBytes, Runnable notification) {
    if (IS_NOISY) {
      LOGGER.error("appending to " + key.space + "/" + key.key + " [batch:" + batch.size() + "] " + buffer.writerIndex());
    }

    // allocate the items in the batch
    ArrayList<RegionByteArrayPairing> wheres = new ArrayList<>();
    for (byte[] bytes : batch) {
      Region where = heap.ask(bytes.length);
      if (where == null) {
        // we failed to allocate one, so we free everything and return a null
        for (RegionByteArrayPairing prior : wheres) {
          heap.free(prior.where);
        }
        return null;
      } else {
        wheres.add(new RegionByteArrayPairing(where, bytes));
      }
    }

    // track the final notification
    this.notifications.add(notification);

    // walk the regions allocated and the bytes
    int lastSize = -1;
    int id = mapKeyToLocalId(key);
    for (RegionByteArrayPairing pairing : wheres) {
      metrics.appends.run();
      RestoreWalker walker = new RestoreWalker();
      EventCodec.route(Unpooled.wrappedBuffer(pairing.bytes), walker);
      storage.write(pairing.where, pairing.bytes);
      lastSize = index.append(id, new AnnotatedRegion(pairing.where.position, pairing.bytes.length, walker.seq, walker.assetBytes));
      new Append(id, pairing.where.position, pairing.bytes, seq, assetBytes).write(buffer);
    }

    if (buffer.writerIndex() > flushCutOffBytes) {
      flush(false);
    }
    return lastSize;
  }

  /** flush to disk */
  public void flush(boolean forceCutOver) {
    if (IS_NOISY) {
      LOGGER.error("flushing :" + forceCutOver + "::" + bytesWrittenToLog);
    }

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
      if (notifications.size() > 0) {
        ArrayList<Runnable> notificationClone = new ArrayList<>(notifications);
        notifications.clear();

        for (Runnable notification : notificationClone) {
          notification.run();
        }
      }
    } catch (IOException ex) {
      LOGGER.error("critical-exception:", ex);
      System.exit(100);
    }
  }

  /** internal: force everything to flush, prepare a new file, the move the new file in place, and open it */
  private void cutOver() throws IOException {
    if (IS_NOISY) {
      LOGGER.error("cutting over to new log");
    }
    storage.flush();
    output.close();
    File newFile = prepare();
    Files.move(newFile.toPath(), new File(walRoot, "WAL").toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
    openLogForWriting();
  }

  public Integer append(Key key, byte[] bytes, int seq, long assetBytes, Runnable notification) {
    if (IS_NOISY) {
      LOGGER.error("appending to " + key.space + "/" + key.key + " [solo] size=" + bytes.length + ":before:" + buffer.writerIndex());
    }
    Region where = heap.ask(bytes.length);
    if (where == null) {
      LOGGER.error("store-full for " + key.space + "/" + key.key);
      metrics.failed_append.run();
      // we are out of space
      return null;
    }
    metrics.appends.run();
    this.notifications.add(notification);
    storage.write(where, bytes);
    int id = mapKeyToLocalId(key);
    int size = index.append(id, new AnnotatedRegion(where.position, where.size, seq, assetBytes));
    new Append(id, where.position, bytes, seq, assetBytes).write(buffer);
    if (buffer.writerIndex() > flushCutOffBytes) {
      // the buffer is full, so flush it
      flush(false);
    }
    if (IS_NOISY) {
      LOGGER.error("appended to " + key.space + "/" + key.key + " [solo] :after:" + buffer.writerIndex());
    }
    return size;
  }

  /** read the given object by scanning all appends */
  public void read(Key key, ByteArrayStream streamback) throws Exception {
    Integer id = keymap.get(key);
    if (id != null) {
      Iterator<AnnotatedRegion> it = index.get(id);
      int at = 0;
      while (it.hasNext()) {
        metrics.reads.run();
        AnnotatedRegion region = it.next();
        byte[] mem = storage.read(region);
        streamback.next(at, mem, region.seq, region.assetBytes);
        at++;
      }
    }
    streamback.finished();
  }

  /** remove the $count appends from the head of the object */
  public boolean trim(Key key, int maxSize, Runnable notification) {
    if (IS_NOISY) {
      LOGGER.error("TRIM" + key.space + "/" + key.key + " max:" + maxSize);
    }

    Integer id = keymap.get(key);
    if (maxSize > 0 && id != null) {
      ArrayList<AnnotatedRegion> regions = index.trim(id, maxSize);
      if (regions != null && regions.size() > 0) {
        this.notifications.add(notification);
        // int localId = mapKeyToLocalId(key);
        new Trim(id, maxSize).write(buffer);
        for (Region region : regions) {
          metrics.items_trimmed.run();
          heap.free(region);
        }
        if (buffer.writerIndex() > flushCutOffBytes) {
          flush(false);
        }
        return true;
      }
    }
    notification.run();
    return false;
  }

  /** delete the given object by id */
  public boolean delete(Key key, Runnable notification) {
    if (IS_NOISY) {
      LOGGER.error("DELETE" + key.space + "/" + key.key);
    }
    Integer id = keymap.get(key);
    if (id == null) {
      return false;
    }
    ArrayList<AnnotatedRegion> regions = index.delete(id);
    if (regions != null) {
      for (Region region : regions) {
        heap.free(region);
      }
      DelKey dk = new DelKey(key);
      keymap.apply(dk);
      dk.write(buffer);
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
  public boolean exists(Key key) {
    return keymap.exists(key);
  }

  public void shutdown() throws IOException {
    output.writeInt(0);
    output.flush();
    output.close();
    storage.flush();
    storage.close();
  }
}
