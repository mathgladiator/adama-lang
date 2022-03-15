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
import org.adamalang.disk.wal.WriteAheadMessage;
import org.adamalang.disk.wal.WriteAheadMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.TreeSet;

public class Startup {
  private static final Logger LOGGER = LoggerFactory.getLogger(Startup.class);

  public static void transfer(DiskBase base) throws IOException {
    File[] walsInOrder = getWalFilesToIngest(base);
    for (File walFile : walsInOrder) {
      replay(walFile, base);
      base.flushAllNow(true);
      walFile.delete();
    }
  }

  private static File[] getWalFilesToIngest(DiskBase base) throws IOException {
    File[] files = base.walWorkingDirectory.listFiles();
    TreeSet<Integer> ids = new TreeSet<>();
    int minimum = Integer.MAX_VALUE;
    for (File file : files) {
      if (file.getName().startsWith("WAL-")) {
        try {
          int id = Integer.parseInt(file.getName().substring(4));
          if (id < minimum) {
            minimum = id;
          }
          ids.add(id);
        } catch (NumberFormatException ex) {
          LOGGER.error("found a 'WAL-' prefixed file with an invalid sequencer (SKIP)");
        }
      }
    }

    File[] sorted = new File[ids.size()];
    for (int k = 0; k < ids.size(); k++) {
      int at = minimum + k;
      sorted[k] = new File(base.walWorkingDirectory, "WAL-" + at);
      if (!ids.contains(at) || !sorted[k].exists()) {
        LOGGER.error("A gap in the WAL- prefixed files was found at {} (ABORT) ", at);
        throw new IOException("Gap detected in WAL- prefixed files");
      }
    }

    return sorted;
  }

  private static void replay(File walFile, DiskBase base) throws IOException {
    FileInputStream fileInputStream = new FileInputStream(walFile);
    try {
      DataInputStream read = new DataInputStream(fileInputStream);
      int rd;
      while ((rd = read.read()) > 0) {
        if (rd != 0x42) {
          LOGGER.error("Failed to read a magic 'record-start' byte of 0x42");
          throw new IOException("Failed to read a magic 'record-start' byte of 0x42");
        }
        int pageBytes = read.readInt();
        byte[] page = new byte[pageBytes];
        read.readFully(page);
        ByteBuf buf = Unpooled.wrappedBuffer(page);
        while (buf.readableBytes() > 0) {
          WriteAheadMessage message = WriteAheadMessageCodec.read_WriteAheadMessage(buf);
          DocumentMemoryLog log = base.getOrCreate(message.key());
          if (message.requiresLoad()) {
            log.loadIfNotLoaded();
          }
          message.apply(log);
        }
      }
    } finally {
      fileInputStream.close();
    }
  }
}
