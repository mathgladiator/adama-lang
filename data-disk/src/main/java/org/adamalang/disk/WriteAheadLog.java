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

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.ErrorCodes;
import org.adamalang.common.AwaitHelper;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.disk.wal.WriteAheadMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class WriteAheadLog {
  private static final Logger LOGGER = LoggerFactory.getLogger(WriteAheadLog.class);

  private boolean alive;
  private final DiskBase base;
  private final File root;
  private final int cutOffBytesFlush;
  private final int flushPeriodNanoseconds;
  private final long bytesBeforeLogCut;

  private File currentFile;
  private DataOutputStream output;
  private int at;
  private boolean flushScheduled;
  private long bytesWritten;
  private byte[] mem;
  private ByteBuf buffer;
  private ArrayList<Callback<Void>> callbacks;

  public WriteAheadLog(DiskBase base, int cutOffBytesFlush, int flushPeriodNanoseconds, long bytesBeforeLogCut) {
    this.alive = true;
    this.base = base;
    this.root = base.walWorkingDirectory;
    this.cutOffBytesFlush = cutOffBytesFlush;
    this.flushPeriodNanoseconds = flushPeriodNanoseconds;
    this.bytesBeforeLogCut = bytesBeforeLogCut;

    this.currentFile = null;
    this.output = null;
    this.at = 0;
    this.flushScheduled = false;
    this.bytesWritten = 0;

    this.buffer = Unpooled.buffer(cutOffBytesFlush);
    this.callbacks = new ArrayList<>();
    this.mem = new byte[cutOffBytesFlush / 2];
  }

  private void flushMemory() {
    try {
      if (output == null) {
        currentFile = new File(root, "WAL-" + at);
        at++;
        base.metrics.disk_data_open_wal_files.up();
        output = new DataOutputStream(new FileOutputStream(currentFile));
        bytesWritten = 0;
      }

      output.write(0x42);
      output.writeInt(buffer.writerIndex());;
      while (buffer.isReadable()) {
        int toRead = buffer.readableBytes();
        bytesWritten += toRead;
        if (mem.length <= toRead) {
          mem = new byte[toRead + cutOffBytesFlush];
        }
        buffer.readBytes(mem, 0, toRead);
        output.write(mem, 0, toRead);
      }
      output.flush();
      for (Callback<Void> callback : callbacks) {
        callback.success(null);
      }
      callbacks.clear();
      this.buffer.resetReaderIndex();
      this.buffer.resetWriterIndex();
      if (bytesWritten > bytesBeforeLogCut || !alive) {
        output.close();
        output = null;
        base.attachFile(currentFile);
      }
    } catch (IOException io) {
      LOGGER.error("failed-to-flush", io);
      for (Callback<Void> callback : callbacks) {
        callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_DISK_LOGGER_IOEXCEPTION, io));
      }
      callbacks.clear();
      System.exit(200);
    }
  }

  public Runnable close() {
    CountDownLatch latch = new CountDownLatch(1);
    base.executor.execute(new NamedRunnable("wal-force-flush") {
      @Override
      public void execute() throws Exception {
        alive = false;
        flushMemory();
        latch.countDown();
      }
    });
    return () -> AwaitHelper.block(latch, 2500);
  }

  public void write(WriteAheadMessage message, Callback<Void> callback) {
    if (!alive) {
      callback.failure(new ErrorCodeException(ErrorCodes.CARAVAN_DISK_LOGGER_SHUTDOWN));
      return;
    }
    message.write(buffer);
    callbacks.add(callback);
    if (buffer.readableBytes() > cutOffBytesFlush) {
      flushMemory();
    }
    if (!flushScheduled) {
      flushScheduled = true;
      base.executor.scheduleNano(new NamedRunnable("wal-flush") {
        @Override
        public void execute() throws Exception {
          flushMemory();
          flushScheduled = false;
        }
      }, flushPeriodNanoseconds);
    }
  }
}
