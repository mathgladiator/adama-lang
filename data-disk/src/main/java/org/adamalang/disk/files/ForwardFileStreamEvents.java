/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk.files;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.disk.wal.WriteAheadMessage;
import org.adamalang.disk.wal.WriteAheadMessageCodec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface ForwardFileStreamEvents {
  public void on(WriteAheadMessage message) throws IOException;

  public void finished() throws IOException;

  public static void read(DataInputStream data, ForwardFileStreamEvents events) throws IOException {
    while (data.read() > 0) {
      byte[] buffer = new byte[data.readInt()];
      data.readFully(buffer);
      events.on(WriteAheadMessageCodec.read_WriteAheadMessage(Unpooled.wrappedBuffer(buffer)));
    }
    events.finished();
  }

  public static ForwardFileStreamEvents makeWriter(DataOutputStream output) {
    return new ForwardFileStreamEvents() {
      @Override
      public void on(WriteAheadMessage message) throws IOException {
        output.write(1);
        ByteBuf buf = Unpooled.buffer();
        message.write(buf);
        output.writeInt(buf.writerIndex());
        while (buf.isReadable()) {
          byte[] chunk = new byte[buf.readableBytes()];
          buf.readBytes(chunk);
          output.write(chunk);
        }
      }

      @Override
      public void finished() throws IOException {
        output.flush();
      }
    };
  }
}
