/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.codec;

import io.netty.buffer.ByteBuf;
import org.adamalang.common.codec.Helper;
import org.adamalang.common.net.ByteStream;
import org.adamalang.net.codec.ClientMessage.RequestInventoryHeartbeat;
import org.adamalang.net.codec.ClientMessage.RequestHeat;
import org.adamalang.net.codec.ClientMessage.StreamAttach;
import org.adamalang.net.codec.ClientMessage.StreamAskAttachmentRequest;
import org.adamalang.net.codec.ClientMessage.StreamDisconnect;
import org.adamalang.net.codec.ClientMessage.StreamUpdate;
import org.adamalang.net.codec.ClientMessage.StreamSend;
import org.adamalang.net.codec.ClientMessage.StreamConnect;
import org.adamalang.net.codec.ClientMessage.MeteringDeleteBatch;
import org.adamalang.net.codec.ClientMessage.MeteringBegin;
import org.adamalang.net.codec.ClientMessage.ScanDeployment;
import org.adamalang.net.codec.ClientMessage.ReflectRequest;
import org.adamalang.net.codec.ClientMessage.CreateRequest;
import org.adamalang.net.codec.ClientMessage.PingRequest;

public class ClientCodec {

  public static abstract class StreamServer implements ByteStream {
    public abstract void handle(RequestInventoryHeartbeat payload);

    public abstract void handle(RequestHeat payload);

    public abstract void handle(StreamAttach payload);

    public abstract void handle(StreamAskAttachmentRequest payload);

    public abstract void handle(StreamDisconnect payload);

    public abstract void handle(StreamUpdate payload);

    public abstract void handle(StreamSend payload);

    public abstract void handle(StreamConnect payload);

    public abstract void handle(MeteringDeleteBatch payload);

    public abstract void handle(MeteringBegin payload);

    public abstract void handle(ScanDeployment payload);

    public abstract void handle(ReflectRequest payload);

    public abstract void handle(CreateRequest payload);

    public abstract void handle(PingRequest payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 7231:
          handle(readBody_7231(buf, new RequestInventoryHeartbeat()));
          return;
        case 1919:
          handle(readBody_1919(buf, new RequestHeat()));
          return;
        case 16345:
          handle(readBody_16345(buf, new StreamAttach()));
          return;
        case 15345:
          handle(readBody_15345(buf, new StreamAskAttachmentRequest()));
          return;
        case 13335:
          handle(readBody_13335(buf, new StreamDisconnect()));
          return;
        case 14345:
          handle(readBody_14345(buf, new StreamUpdate()));
          return;
        case 13345:
          handle(readBody_13345(buf, new StreamSend()));
          return;
        case 12345:
          handle(readBody_12345(buf, new StreamConnect()));
          return;
        case 1245:
          handle(readBody_1245(buf, new MeteringDeleteBatch()));
          return;
        case 1243:
          handle(readBody_1243(buf, new MeteringBegin()));
          return;
        case 8921:
          handle(readBody_8921(buf, new ScanDeployment()));
          return;
        case 6735:
          handle(readBody_6735(buf, new ReflectRequest()));
          return;
        case 12523:
          handle(readBody_12523(buf, new CreateRequest()));
          return;
        case 24321:
          handle(readBody_24321(buf, new PingRequest()));
          return;
      }
    }
  }

  public static interface HandlerServer {
    public void handle(RequestInventoryHeartbeat payload);
    public void handle(RequestHeat payload);
    public void handle(StreamAttach payload);
    public void handle(StreamAskAttachmentRequest payload);
    public void handle(StreamDisconnect payload);
    public void handle(StreamUpdate payload);
    public void handle(StreamSend payload);
    public void handle(StreamConnect payload);
    public void handle(MeteringDeleteBatch payload);
    public void handle(MeteringBegin payload);
    public void handle(ScanDeployment payload);
    public void handle(ReflectRequest payload);
    public void handle(CreateRequest payload);
    public void handle(PingRequest payload);
  }

  public static void route(ByteBuf buf, HandlerServer handler) {
    switch (buf.readIntLE()) {
      case 7231:
        handler.handle(readBody_7231(buf, new RequestInventoryHeartbeat()));
        return;
      case 1919:
        handler.handle(readBody_1919(buf, new RequestHeat()));
        return;
      case 16345:
        handler.handle(readBody_16345(buf, new StreamAttach()));
        return;
      case 15345:
        handler.handle(readBody_15345(buf, new StreamAskAttachmentRequest()));
        return;
      case 13335:
        handler.handle(readBody_13335(buf, new StreamDisconnect()));
        return;
      case 14345:
        handler.handle(readBody_14345(buf, new StreamUpdate()));
        return;
      case 13345:
        handler.handle(readBody_13345(buf, new StreamSend()));
        return;
      case 12345:
        handler.handle(readBody_12345(buf, new StreamConnect()));
        return;
      case 1245:
        handler.handle(readBody_1245(buf, new MeteringDeleteBatch()));
        return;
      case 1243:
        handler.handle(readBody_1243(buf, new MeteringBegin()));
        return;
      case 8921:
        handler.handle(readBody_8921(buf, new ScanDeployment()));
        return;
      case 6735:
        handler.handle(readBody_6735(buf, new ReflectRequest()));
        return;
      case 12523:
        handler.handle(readBody_12523(buf, new CreateRequest()));
        return;
      case 24321:
        handler.handle(readBody_24321(buf, new PingRequest()));
        return;
    }
  }

  public static RequestInventoryHeartbeat read_RequestInventoryHeartbeat(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 7231:
        return readBody_7231(buf, new RequestInventoryHeartbeat());
    }
    return null;
  }


  private static RequestInventoryHeartbeat readBody_7231(ByteBuf buf, RequestInventoryHeartbeat o) {
    return o;
  }

  public static RequestHeat read_RequestHeat(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1919:
        return readBody_1919(buf, new RequestHeat());
    }
    return null;
  }


  private static RequestHeat readBody_1919(ByteBuf buf, RequestHeat o) {
    return o;
  }

  public static StreamAttach read_StreamAttach(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 16345:
        return readBody_16345(buf, new StreamAttach());
    }
    return null;
  }


  private static StreamAttach readBody_16345(ByteBuf buf, StreamAttach o) {
    o.op = buf.readIntLE();
    o.id = Helper.readString(buf);
    o.filename = Helper.readString(buf);
    o.contentType = Helper.readString(buf);
    o.size = buf.readLongLE();
    o.md5 = Helper.readString(buf);
    o.sha384 = Helper.readString(buf);
    return o;
  }

  public static StreamAskAttachmentRequest read_StreamAskAttachmentRequest(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 15345:
        return readBody_15345(buf, new StreamAskAttachmentRequest());
    }
    return null;
  }


  private static StreamAskAttachmentRequest readBody_15345(ByteBuf buf, StreamAskAttachmentRequest o) {
    o.op = buf.readIntLE();
    return o;
  }

  public static StreamDisconnect read_StreamDisconnect(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 13335:
        return readBody_13335(buf, new StreamDisconnect());
    }
    return null;
  }


  private static StreamDisconnect readBody_13335(ByteBuf buf, StreamDisconnect o) {
    return o;
  }

  public static StreamUpdate read_StreamUpdate(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 14345:
        return readBody_14345(buf, new StreamUpdate());
    }
    return null;
  }


  private static StreamUpdate readBody_14345(ByteBuf buf, StreamUpdate o) {
    o.viewerState = Helper.readString(buf);
    return o;
  }

  public static StreamSend read_StreamSend(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 13345:
        return readBody_13345(buf, new StreamSend());
    }
    return null;
  }


  private static StreamSend readBody_13345(ByteBuf buf, StreamSend o) {
    o.op = buf.readIntLE();
    o.channel = Helper.readString(buf);
    o.marker = Helper.readString(buf);
    o.message = Helper.readString(buf);
    return o;
  }

  public static StreamConnect read_StreamConnect(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 12345:
        return readBody_12345(buf, new StreamConnect());
    }
    return null;
  }


  private static StreamConnect readBody_12345(ByteBuf buf, StreamConnect o) {
    o.space = Helper.readString(buf);
    o.key = Helper.readString(buf);
    o.agent = Helper.readString(buf);
    o.authority = Helper.readString(buf);
    o.viewerState = Helper.readString(buf);
    o.origin = Helper.readString(buf);
    return o;
  }

  public static MeteringDeleteBatch read_MeteringDeleteBatch(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1245:
        return readBody_1245(buf, new MeteringDeleteBatch());
    }
    return null;
  }


  private static MeteringDeleteBatch readBody_1245(ByteBuf buf, MeteringDeleteBatch o) {
    o.id = Helper.readString(buf);
    return o;
  }

  public static MeteringBegin read_MeteringBegin(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1243:
        return readBody_1243(buf, new MeteringBegin());
    }
    return null;
  }


  private static MeteringBegin readBody_1243(ByteBuf buf, MeteringBegin o) {
    return o;
  }

  public static ScanDeployment read_ScanDeployment(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 8921:
        return readBody_8921(buf, new ScanDeployment());
    }
    return null;
  }


  private static ScanDeployment readBody_8921(ByteBuf buf, ScanDeployment o) {
    o.space = Helper.readString(buf);
    return o;
  }

  public static ReflectRequest read_ReflectRequest(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 6735:
        return readBody_6735(buf, new ReflectRequest());
    }
    return null;
  }


  private static ReflectRequest readBody_6735(ByteBuf buf, ReflectRequest o) {
    o.space = Helper.readString(buf);
    o.key = Helper.readString(buf);
    return o;
  }

  public static CreateRequest read_CreateRequest(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 12523:
        return readBody_12523(buf, new CreateRequest());
    }
    return null;
  }


  private static CreateRequest readBody_12523(ByteBuf buf, CreateRequest o) {
    o.space = Helper.readString(buf);
    o.key = Helper.readString(buf);
    o.arg = Helper.readString(buf);
    o.entropy = Helper.readString(buf);
    o.agent = Helper.readString(buf);
    o.authority = Helper.readString(buf);
    o.origin = Helper.readString(buf);
    return o;
  }

  public static PingRequest read_PingRequest(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 24321:
        return readBody_24321(buf, new PingRequest());
    }
    return null;
  }


  private static PingRequest readBody_24321(ByteBuf buf, PingRequest o) {
    return o;
  }

  public static void write(ByteBuf buf, RequestInventoryHeartbeat o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(7231);
  }

  public static void write(ByteBuf buf, RequestHeat o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1919);
  }

  public static void write(ByteBuf buf, StreamAttach o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(16345);
    buf.writeIntLE(o.op);
    Helper.writeString(buf, o.id);;
    Helper.writeString(buf, o.filename);;
    Helper.writeString(buf, o.contentType);;
    buf.writeLongLE(o.size);
    Helper.writeString(buf, o.md5);;
    Helper.writeString(buf, o.sha384);;
  }

  public static void write(ByteBuf buf, StreamAskAttachmentRequest o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(15345);
    buf.writeIntLE(o.op);
  }

  public static void write(ByteBuf buf, StreamDisconnect o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(13335);
  }

  public static void write(ByteBuf buf, StreamUpdate o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(14345);
    Helper.writeString(buf, o.viewerState);;
  }

  public static void write(ByteBuf buf, StreamSend o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(13345);
    buf.writeIntLE(o.op);
    Helper.writeString(buf, o.channel);;
    Helper.writeString(buf, o.marker);;
    Helper.writeString(buf, o.message);;
  }

  public static void write(ByteBuf buf, StreamConnect o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(12345);
    Helper.writeString(buf, o.space);;
    Helper.writeString(buf, o.key);;
    Helper.writeString(buf, o.agent);;
    Helper.writeString(buf, o.authority);;
    Helper.writeString(buf, o.viewerState);;
    Helper.writeString(buf, o.origin);;
  }

  public static void write(ByteBuf buf, MeteringDeleteBatch o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1245);
    Helper.writeString(buf, o.id);;
  }

  public static void write(ByteBuf buf, MeteringBegin o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1243);
  }

  public static void write(ByteBuf buf, ScanDeployment o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(8921);
    Helper.writeString(buf, o.space);;
  }

  public static void write(ByteBuf buf, ReflectRequest o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(6735);
    Helper.writeString(buf, o.space);;
    Helper.writeString(buf, o.key);;
  }

  public static void write(ByteBuf buf, CreateRequest o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(12523);
    Helper.writeString(buf, o.space);;
    Helper.writeString(buf, o.key);;
    Helper.writeString(buf, o.arg);;
    Helper.writeString(buf, o.entropy);;
    Helper.writeString(buf, o.agent);;
    Helper.writeString(buf, o.authority);;
    Helper.writeString(buf, o.origin);;
  }

  public static void write(ByteBuf buf, PingRequest o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(24321);
  }
}
