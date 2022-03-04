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

import org.adamalang.net.codec.ClientMessage.RequestInventoryHeartbeat;
import org.adamalang.net.codec.ClientMessage.RequestHeat;
import org.adamalang.net.codec.ClientMessage.StreamDisconnect;
import org.adamalang.net.codec.ClientMessage.StreamAttach;
import org.adamalang.net.codec.ClientMessage.StreamAskAttachmentRequest;
import org.adamalang.net.codec.ClientMessage.StreamUpdate;
import org.adamalang.net.codec.ClientMessage.StreamSend;
import org.adamalang.net.codec.ClientMessage.StreamConnect;
import org.adamalang.net.codec.ClientMessage.MeteringDeleteBatch;
import org.adamalang.net.codec.ClientMessage.MeteringBegin;
import org.adamalang.net.codec.ClientMessage.ScanDeploymentRequest;
import org.adamalang.net.codec.ClientMessage.ReflectRequest;
import org.adamalang.net.codec.ClientMessage.CreateRequest;
import org.adamalang.net.codec.ClientMessage.PingRequest;

public class ClientCodec {

  public static interface HandlerServer {
    public void handle(RequestInventoryHeartbeat payload);
    public void handle(RequestHeat payload);
    public void handle(StreamDisconnect payload);
    public void handle(StreamAttach payload);
    public void handle(StreamAskAttachmentRequest payload);
    public void handle(StreamUpdate payload);
    public void handle(StreamSend payload);
    public void handle(StreamConnect payload);
    public void handle(MeteringDeleteBatch payload);
    public void handle(MeteringBegin payload);
    public void handle(ScanDeploymentRequest payload);
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
      case 17345:
        handler.handle(readBody_17345(buf, new StreamDisconnect()));
        return;
      case 16345:
        handler.handle(readBody_16345(buf, new StreamAttach()));
        return;
      case 15345:
        handler.handle(readBody_15345(buf, new StreamAskAttachmentRequest()));
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
        handler.handle(readBody_8921(buf, new ScanDeploymentRequest()));
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

  public static RequestInventoryHeartbeat readRegister_RequestInventoryHeartbeat(ByteBuf buf, RequestInventoryHeartbeat o) {
    switch (buf.readIntLE()) {
      case 7231:
        return readBody_7231(buf, o);
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

  public static RequestHeat readRegister_RequestHeat(ByteBuf buf, RequestHeat o) {
    switch (buf.readIntLE()) {
      case 1919:
        return readBody_1919(buf, o);
    }
    return null;
  }

  private static RequestHeat readBody_1919(ByteBuf buf, RequestHeat o) {
    return o;
  }

  public static StreamDisconnect read_StreamDisconnect(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 17345:
        return readBody_17345(buf, new StreamDisconnect());
    }
    return null;
  }

  public static StreamDisconnect readRegister_StreamDisconnect(ByteBuf buf, StreamDisconnect o) {
    switch (buf.readIntLE()) {
      case 17345:
        return readBody_17345(buf, o);
    }
    return null;
  }

  private static StreamDisconnect readBody_17345(ByteBuf buf, StreamDisconnect o) {
    return o;
  }

  public static StreamAttach read_StreamAttach(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 16345:
        return readBody_16345(buf, new StreamAttach());
    }
    return null;
  }

  public static StreamAttach readRegister_StreamAttach(ByteBuf buf, StreamAttach o) {
    switch (buf.readIntLE()) {
      case 16345:
        return readBody_16345(buf, o);
    }
    return null;
  }

  private static StreamAttach readBody_16345(ByteBuf buf, StreamAttach o) {
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

  public static StreamAskAttachmentRequest readRegister_StreamAskAttachmentRequest(ByteBuf buf, StreamAskAttachmentRequest o) {
    switch (buf.readIntLE()) {
      case 15345:
        return readBody_15345(buf, o);
    }
    return null;
  }

  private static StreamAskAttachmentRequest readBody_15345(ByteBuf buf, StreamAskAttachmentRequest o) {
    return o;
  }

  public static StreamUpdate read_StreamUpdate(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 14345:
        return readBody_14345(buf, new StreamUpdate());
    }
    return null;
  }

  public static StreamUpdate readRegister_StreamUpdate(ByteBuf buf, StreamUpdate o) {
    switch (buf.readIntLE()) {
      case 14345:
        return readBody_14345(buf, o);
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

  public static StreamSend readRegister_StreamSend(ByteBuf buf, StreamSend o) {
    switch (buf.readIntLE()) {
      case 13345:
        return readBody_13345(buf, o);
    }
    return null;
  }

  private static StreamSend readBody_13345(ByteBuf buf, StreamSend o) {
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

  public static StreamConnect readRegister_StreamConnect(ByteBuf buf, StreamConnect o) {
    switch (buf.readIntLE()) {
      case 12345:
        return readBody_12345(buf, o);
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

  public static MeteringDeleteBatch readRegister_MeteringDeleteBatch(ByteBuf buf, MeteringDeleteBatch o) {
    switch (buf.readIntLE()) {
      case 1245:
        return readBody_1245(buf, o);
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

  public static MeteringBegin readRegister_MeteringBegin(ByteBuf buf, MeteringBegin o) {
    switch (buf.readIntLE()) {
      case 1243:
        return readBody_1243(buf, o);
    }
    return null;
  }

  private static MeteringBegin readBody_1243(ByteBuf buf, MeteringBegin o) {
    return o;
  }

  public static ScanDeploymentRequest read_ScanDeploymentRequest(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 8921:
        return readBody_8921(buf, new ScanDeploymentRequest());
    }
    return null;
  }

  public static ScanDeploymentRequest readRegister_ScanDeploymentRequest(ByteBuf buf, ScanDeploymentRequest o) {
    switch (buf.readIntLE()) {
      case 8921:
        return readBody_8921(buf, o);
    }
    return null;
  }

  private static ScanDeploymentRequest readBody_8921(ByteBuf buf, ScanDeploymentRequest o) {
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

  public static ReflectRequest readRegister_ReflectRequest(ByteBuf buf, ReflectRequest o) {
    switch (buf.readIntLE()) {
      case 6735:
        return readBody_6735(buf, o);
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

  public static CreateRequest readRegister_CreateRequest(ByteBuf buf, CreateRequest o) {
    switch (buf.readIntLE()) {
      case 12523:
        return readBody_12523(buf, o);
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

  public static PingRequest readRegister_PingRequest(ByteBuf buf, PingRequest o) {
    switch (buf.readIntLE()) {
      case 24321:
        return readBody_24321(buf, o);
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

  public static void write(ByteBuf buf, StreamDisconnect o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(17345);
  }

  public static void write(ByteBuf buf, StreamAttach o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(16345);
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

  public static void write(ByteBuf buf, ScanDeploymentRequest o) {
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
