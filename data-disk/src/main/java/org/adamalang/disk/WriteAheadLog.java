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
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.disk.wal.WriteAheadMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class WriteAheadLog {
  private final DiskBase base;
  private final File root;
  private final int cutOffBytesFlush;
  private final int flushPeriodMilliseconds;
  private final long bytesBeforeLogCut;

  private File currentFile;
  private FileOutputStream output;
  private int at = 0;
  private boolean flushScheduled;
  private long bytesWritten;

  private ByteBuf buffer;
  private ArrayList<Callback<Void>> callbacks;
  private byte[] mem;

  public WriteAheadLog(DiskBase base, File root, int cutOffBytesFlush, int flushPeriodMilliseconds, long bytesBeforeLogCut) {
    this.base = base;
    this.root = root;
    this.cutOffBytesFlush = cutOffBytesFlush;
    this.flushPeriodMilliseconds = flushPeriodMilliseconds;
    this.bytesBeforeLogCut = bytesBeforeLogCut;

    this.currentFile = null;
    this.output = null;
    this.at = 0;
    this.flushScheduled = false;
    this.bytesWritten = 0;


    this.buffer = Unpooled.buffer(cutOffBytesFlush);
    this.callbacks = new ArrayList<>();
    this.mem = new byte[cutOffBytesFlush * 2];
  }

  private void flushMemory() {
    try {
      if (output == null) {
        currentFile = new File(root, "WAL-" + at);
        at++;
        output = new FileOutputStream(currentFile);
      }
      if (buffer.hasArray()) {
        byte[] bytes = buffer.array();
        bytesWritten += bytes.length;
        output.write(buffer.array());
      } else {
        while (buffer.isReadable()) {
          int toRead = buffer.readableBytes();
          bytesWritten += toRead;
          if (mem.length <= toRead) {
            mem = new byte[toRead + cutOffBytesFlush];
          }
          buffer.readBytes(mem, 0, toRead);
          output.write(mem, 0, toRead);
        }
      }
      output.flush();
      for (Callback<Void> callback : callbacks) {
        callback.success(null);
      }
      callbacks.clear();
      this.buffer.resetReaderIndex();
      this.buffer.resetWriterIndex();
      if (bytesWritten > bytesBeforeLogCut) {
        output.close();
        output = null;
        base.flush(currentFile);
      }
    } catch (IOException io) {
      for (Callback<Void> callback : callbacks) {
        callback.failure(new ErrorCodeException(-1));
      }
      callbacks.clear();
    }
  }

  public void write(WriteAheadMessage message, Callback<Void> callback) {
    message.write(buffer);
    callbacks.add(callback);
    if (buffer.readableBytes() > cutOffBytesFlush) {
      flushMemory();
    }
    if (!flushScheduled) {
      flushScheduled = true;
      base.executor.schedule(new NamedRunnable("wal-flush") {
        @Override
        public void execute() throws Exception {
          flushMemory();
          flushScheduled = false;
        }
      }, flushPeriodMilliseconds);
    }
  }
}
