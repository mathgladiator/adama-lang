/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.bald.play.atomic;

import org.adamalang.common.Callback;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class AtomicRandomAccessFile {
  private final SimpleExecutor executor;
  private final RandomAccessFile file;
  private final File wal;
  private final int bytesBetweenWalFlush;
  private final int bytesBetweensFileFlush;
  private DataOutputStream output;
  private int bytesWrittenWal;
  private int bytesWrittenFile;
  private int millisecondsBetweenWalFlush;
  private HashMap<Long, byte[]> buffer;
  private ArrayList<Callback<Void>> flush;
  private boolean flushScheduled;

  public AtomicRandomAccessFile(SimpleExecutor executor, RandomAccessFile file, File wal, int bytesBetweenWalFlush, int millisecondsBetweenWalFlush, int bytesBetweensFileFlush) {
    this.executor = executor;
    this.file = file;
    this.wal = wal;
    this.output = null;
    this.bytesWrittenWal = 0;
    this.bytesWrittenFile = 0;
    this.buffer = new HashMap<>();
    this.flush = new ArrayList<>();
    this.flushScheduled = false;
    this.bytesBetweenWalFlush = bytesBetweenWalFlush;
    this.millisecondsBetweenWalFlush = millisecondsBetweenWalFlush;
    this.bytesBetweensFileFlush = bytesBetweensFileFlush;
    if (wal.exists()) {
      // TODO: lets' replay the wal against the file as there was a crash
    }
  }

  public void sync() {
    executor.execute(new NamedRunnable("araf-sync") {
      @Override
      public void execute() throws Exception {
        flush(true);
      }
    });
  }

  public void close() {
    executor.execute(new NamedRunnable("araf-close") {
      @Override
      public void execute() throws Exception {
        file.close();
      }
    });
  }

  private void prepare() throws IOException {
    if (output == null) {
      output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(wal), bytesBetweenWalFlush * 2 ));
    }
  }

  private void flush(boolean force) throws IOException {
    if (output == null) {
      return;
    }
    output.flush();
    for (Callback<Void> callback : flush) {
      callback.success(null);
    }
    flush.clear();
    bytesWrittenWal = 0;
    if (bytesWrittenFile > bytesBetweensFileFlush || force) {
      file.getFD().sync();
      output.close();
      output = null;
      buffer.clear();
      bytesWrittenFile = 0;
    }
  }

  public void write(long at, byte[] bytes, Callback<Void> callback) {
      executor.execute(new NamedRunnable("araf-write") {
        @Override
        public void execute() throws Exception {
          try {
            prepare();
            output.writeInt(bytes.length);
            output.writeLong(at);
            output.write(bytes);
            if (file.length() < at + bytes.length) {
              file.setLength(file.length() + at + bytes.length);
            }
            file.seek(at);
            file.write(bytes);
            bytesWrittenWal += bytes.length;
            bytesWrittenFile += bytes.length;
            flush.add(callback);
            if (bytesWrittenWal > bytesBetweenWalFlush) {
              flush(false);
            } else if (!flushScheduled) {
              flushScheduled = true;
              executor.schedule(new NamedRunnable("araf-flush") {
                @Override
                public void execute() throws Exception {
                  flushScheduled = false;
                  try {
                    flush(false);
                  } catch (IOException io) {
                    System.exit(150);
                  }
                }
              }, millisecondsBetweenWalFlush);
            }
          } catch (IOException ioe) {
            // we can't reason about
            System.exit(200);
          }
        }
      });
  }

  public void read(long at, int length, Callback<byte[]> callback) {
    executor.execute(new NamedRunnable("araf-read") {
      @Override
      public void execute() throws Exception {
        try {
          file.seek(at);
          byte[] bytes = new byte[length];
          file.readFully(bytes);
          buffer.put(at, bytes);
        } catch (IOException ioe) {
        }
      }
    });
  }
}
