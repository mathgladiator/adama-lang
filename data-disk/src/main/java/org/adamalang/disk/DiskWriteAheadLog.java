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

public class DiskWriteAheadLog {
  private final SimpleExecutor executor;
  private FileOutputStream output;
  private boolean flushScheduled;
  private File file;
  private ByteBuf buffer;
  private ArrayList<Callback<Void>> callbacks;
  private int cutOffBytes;
  private int flushPeriodMilliseconds;
  private long bytesWritten;
  private byte[] mem;

  public DiskWriteAheadLog(SimpleExecutor executor, File file, int cutOffBytes, int flushPeriodMilliseconds) throws IOException {
    this.executor = executor;
    this.file = file;
    this.buffer = Unpooled.buffer(cutOffBytes);
    this.callbacks = new ArrayList<>();
    this.flushPeriodMilliseconds = flushPeriodMilliseconds;
    this.cutOffBytes = cutOffBytes;
    this.output = new FileOutputStream(file);
    this.mem = new byte[cutOffBytes * 2];
  }

  // Not thread safe
  public long getBytesWritten() {
    return bytesWritten;
  }

  private void flushWhileInExecutor() {
    try {
      if (buffer.hasArray()) {
        byte[] bytes = buffer.array();
        bytesWritten += bytes.length;
        output.write(buffer.array());
      } else {
        while (buffer.isReadable()) {
          int toRead = buffer.readableBytes();
          bytesWritten += toRead;
          if (mem.length <= toRead) {
            mem = new byte[toRead + cutOffBytes];
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
    } catch (IOException io) {
      for (Callback<Void> callback : callbacks) {
        callback.failure(new ErrorCodeException(-1));
      }
      callbacks.clear();
    }
  }

  public void write(WriteAheadMessage message, Callback<Void> callback) {
    executor.execute(new NamedRunnable("wal-write") {
      @Override
      public void execute() throws Exception {
        message.write(buffer);
        callbacks.add(callback);
        if (buffer.readableBytes() > cutOffBytes) {
          flushWhileInExecutor();
        }
        if (!flushScheduled) {
          flushScheduled = true;
          executor.schedule(new NamedRunnable("wal-flush") {
            @Override
            public void execute() throws Exception {
              flushWhileInExecutor();
              flushScheduled = false;
            }
          }, flushPeriodMilliseconds);
        }
      }
    });
  }

  public void close() throws Exception {
    output.close();
  }
}
