/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.bald.data;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.bald.contracts.WALEntry;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;

import java.awt.event.TextEvent;
import java.io.*;

public class WriteAheadLog {
  private final File walFile;
  private final ByteBuf buffer;
  private OutputStream output;
  private int flushCutOff;
  private byte[] copy;

  public WriteAheadLog(File fileToUse, int flushCutOff) throws IOException  {
    this.walFile = fileToUse;
    this.buffer = Unpooled.buffer(64 * 1024);
    this.output = null;
    this.flushCutOff = flushCutOff;
    this.copy = new byte[flushCutOff];
    // TODO: need to read and prepare the WAL File
  }

  public void flush() {
    try {
      if (output == null) {
        output = new DataOutputStream(new FileOutputStream(walFile));
      }
      while (buffer.isReadable()) {
        int toRead = buffer.readableBytes();
        if (toRead >= copy.length) {
          copy = new byte[toRead + flushCutOff];
        }
        buffer.readBytes(copy, 0, toRead);
        output.write(copy, 0, toRead);
        output.flush();
      }
      buffer.resetReaderIndex();
      buffer.resetWriterIndex();
    } catch (IOException ex) {
      System.exit(100);
    }
  }

  public void write(WALEntry entry) {
    entry.write(buffer);
    if (buffer.writerIndex() > flushCutOff) {
      flush();
    }
  }
}
