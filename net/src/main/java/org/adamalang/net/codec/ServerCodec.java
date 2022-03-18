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
import org.adamalang.net.codec.ServerMessage.ProxyLocalDataChange;
import org.adamalang.net.codec.ServerMessage.ProxyIntResponse;
import org.adamalang.net.codec.ServerMessage.ProxyVoidResponse;
import org.adamalang.net.codec.ServerMessage.InventoryHeartbeat;
import org.adamalang.net.codec.ServerMessage.HeatPayload;
import org.adamalang.net.codec.ServerMessage.StreamSeqResponse;
import org.adamalang.net.codec.ServerMessage.StreamAskAttachmentResponse;
import org.adamalang.net.codec.ServerMessage.StreamError;
import org.adamalang.net.codec.ServerMessage.StreamData;
import org.adamalang.net.codec.ServerMessage.StreamStatus;
import org.adamalang.net.codec.ServerMessage.MeteringBatchRemoved;
import org.adamalang.net.codec.ServerMessage.MeteringBatchFound;
import org.adamalang.net.codec.ServerMessage.ScanDeploymentResponse;
import org.adamalang.net.codec.ServerMessage.ReflectResponse;
import org.adamalang.net.codec.ServerMessage.CreateResponse;
import org.adamalang.net.codec.ServerMessage.PingResponse;

public class ServerCodec {

  public static abstract class StreamProxyIntResponse implements ByteStream {
    public abstract void handle(ProxyIntResponse payload);

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
        case 9004:
          handle(readBody_9004(buf, new ProxyIntResponse()));
          return;
      }
    }
  }

  public static interface HandlerProxyIntResponse {
    public void handle(ProxyIntResponse payload);
  }

  public static void route(ByteBuf buf, HandlerProxyIntResponse handler) {
    switch (buf.readIntLE()) {
      case 9004:
        handler.handle(readBody_9004(buf, new ProxyIntResponse()));
        return;
    }
  }


  public static abstract class StreamCreation implements ByteStream {
    public abstract void handle(CreateResponse payload);

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
        case 12524:
          handle(readBody_12524(buf, new CreateResponse()));
          return;
      }
    }
  }

  public static interface HandlerCreation {
    public void handle(CreateResponse payload);
  }

  public static void route(ByteBuf buf, HandlerCreation handler) {
    switch (buf.readIntLE()) {
      case 12524:
        handler.handle(readBody_12524(buf, new CreateResponse()));
        return;
    }
  }


  public static abstract class StreamProxyVoidResponse implements ByteStream {
    public abstract void handle(ProxyVoidResponse payload);

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
        case 9002:
          handle(readBody_9002(buf, new ProxyVoidResponse()));
          return;
      }
    }
  }

  public static interface HandlerProxyVoidResponse {
    public void handle(ProxyVoidResponse payload);
  }

  public static void route(ByteBuf buf, HandlerProxyVoidResponse handler) {
    switch (buf.readIntLE()) {
      case 9002:
        handler.handle(readBody_9002(buf, new ProxyVoidResponse()));
        return;
    }
  }


  public static abstract class StreamPing implements ByteStream {
    public abstract void handle(PingResponse payload);

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
        case 24322:
          handle(readBody_24322(buf, new PingResponse()));
          return;
      }
    }
  }

  public static interface HandlerPing {
    public void handle(PingResponse payload);
  }

  public static void route(ByteBuf buf, HandlerPing handler) {
    switch (buf.readIntLE()) {
      case 24322:
        handler.handle(readBody_24322(buf, new PingResponse()));
        return;
    }
  }


  public static abstract class StreamDeployment implements ByteStream {
    public abstract void handle(ScanDeploymentResponse payload);

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
        case 8922:
          handle(readBody_8922(buf, new ScanDeploymentResponse()));
          return;
      }
    }
  }

  public static interface HandlerDeployment {
    public void handle(ScanDeploymentResponse payload);
  }

  public static void route(ByteBuf buf, HandlerDeployment handler) {
    switch (buf.readIntLE()) {
      case 8922:
        handler.handle(readBody_8922(buf, new ScanDeploymentResponse()));
        return;
    }
  }


  public static abstract class StreamReflection implements ByteStream {
    public abstract void handle(ReflectResponse payload);

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
        case 6736:
          handle(readBody_6736(buf, new ReflectResponse()));
          return;
      }
    }
  }

  public static interface HandlerReflection {
    public void handle(ReflectResponse payload);
  }

  public static void route(ByteBuf buf, HandlerReflection handler) {
    switch (buf.readIntLE()) {
      case 6736:
        handler.handle(readBody_6736(buf, new ReflectResponse()));
        return;
    }
  }


  public static abstract class StreamInfo implements ByteStream {
    public abstract void handle(InventoryHeartbeat payload);

    public abstract void handle(HeatPayload payload);

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
        case 7232:
          handle(readBody_7232(buf, new InventoryHeartbeat()));
          return;
        case 5122:
          handle(readBody_5122(buf, new HeatPayload()));
          return;
      }
    }
  }

  public static interface HandlerInfo {
    public void handle(InventoryHeartbeat payload);
    public void handle(HeatPayload payload);
  }

  public static void route(ByteBuf buf, HandlerInfo handler) {
    switch (buf.readIntLE()) {
      case 7232:
        handler.handle(readBody_7232(buf, new InventoryHeartbeat()));
        return;
      case 5122:
        handler.handle(readBody_5122(buf, new HeatPayload()));
        return;
    }
  }


  public static abstract class StreamDocument implements ByteStream {
    public abstract void handle(StreamSeqResponse payload);

    public abstract void handle(StreamAskAttachmentResponse payload);

    public abstract void handle(StreamError payload);

    public abstract void handle(StreamData payload);

    public abstract void handle(StreamStatus payload);

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
        case 1632:
          handle(readBody_1632(buf, new StreamSeqResponse()));
          return;
        case 15546:
          handle(readBody_15546(buf, new StreamAskAttachmentResponse()));
          return;
        case 19546:
          handle(readBody_19546(buf, new StreamError()));
          return;
        case 10546:
          handle(readBody_10546(buf, new StreamData()));
          return;
        case 12546:
          handle(readBody_12546(buf, new StreamStatus()));
          return;
      }
    }
  }

  public static interface HandlerDocument {
    public void handle(StreamSeqResponse payload);
    public void handle(StreamAskAttachmentResponse payload);
    public void handle(StreamError payload);
    public void handle(StreamData payload);
    public void handle(StreamStatus payload);
  }

  public static void route(ByteBuf buf, HandlerDocument handler) {
    switch (buf.readIntLE()) {
      case 1632:
        handler.handle(readBody_1632(buf, new StreamSeqResponse()));
        return;
      case 15546:
        handler.handle(readBody_15546(buf, new StreamAskAttachmentResponse()));
        return;
      case 19546:
        handler.handle(readBody_19546(buf, new StreamError()));
        return;
      case 10546:
        handler.handle(readBody_10546(buf, new StreamData()));
        return;
      case 12546:
        handler.handle(readBody_12546(buf, new StreamStatus()));
        return;
    }
  }


  public static abstract class StreamProxyLocalDataChange implements ByteStream {
    public abstract void handle(ProxyLocalDataChange payload);

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
        case 9006:
          handle(readBody_9006(buf, new ProxyLocalDataChange()));
          return;
      }
    }
  }

  public static interface HandlerProxyLocalDataChange {
    public void handle(ProxyLocalDataChange payload);
  }

  public static void route(ByteBuf buf, HandlerProxyLocalDataChange handler) {
    switch (buf.readIntLE()) {
      case 9006:
        handler.handle(readBody_9006(buf, new ProxyLocalDataChange()));
        return;
    }
  }


  public static abstract class StreamMetering implements ByteStream {
    public abstract void handle(MeteringBatchRemoved payload);

    public abstract void handle(MeteringBatchFound payload);

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
        case 1248:
          handle(readBody_1248(buf, new MeteringBatchRemoved()));
          return;
        case 1246:
          handle(readBody_1246(buf, new MeteringBatchFound()));
          return;
      }
    }
  }

  public static interface HandlerMetering {
    public void handle(MeteringBatchRemoved payload);
    public void handle(MeteringBatchFound payload);
  }

  public static void route(ByteBuf buf, HandlerMetering handler) {
    switch (buf.readIntLE()) {
      case 1248:
        handler.handle(readBody_1248(buf, new MeteringBatchRemoved()));
        return;
      case 1246:
        handler.handle(readBody_1246(buf, new MeteringBatchFound()));
        return;
    }
  }


  public static ProxyLocalDataChange read_ProxyLocalDataChange(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 9006:
        return readBody_9006(buf, new ProxyLocalDataChange());
    }
    return null;
  }


  private static ProxyLocalDataChange readBody_9006(ByteBuf buf, ProxyLocalDataChange o) {
    o.patch = Helper.readString(buf);
    o.reads = buf.readIntLE();
    return o;
  }

  public static ProxyIntResponse read_ProxyIntResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 9004:
        return readBody_9004(buf, new ProxyIntResponse());
    }
    return null;
  }


  private static ProxyIntResponse readBody_9004(ByteBuf buf, ProxyIntResponse o) {
    o.value = buf.readIntLE();
    return o;
  }

  public static ProxyVoidResponse read_ProxyVoidResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 9002:
        return readBody_9002(buf, new ProxyVoidResponse());
    }
    return null;
  }


  private static ProxyVoidResponse readBody_9002(ByteBuf buf, ProxyVoidResponse o) {
    return o;
  }

  public static InventoryHeartbeat read_InventoryHeartbeat(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 7232:
        return readBody_7232(buf, new InventoryHeartbeat());
    }
    return null;
  }


  private static InventoryHeartbeat readBody_7232(ByteBuf buf, InventoryHeartbeat o) {
    o.spaces = Helper.readStringArray(buf);
    return o;
  }

  public static HeatPayload read_HeatPayload(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 5122:
        return readBody_5122(buf, new HeatPayload());
    }
    return null;
  }


  private static HeatPayload readBody_5122(ByteBuf buf, HeatPayload o) {
    o.cpu = buf.readDoubleLE();
    o.mem = buf.readDoubleLE();
    return o;
  }

  public static StreamSeqResponse read_StreamSeqResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1632:
        return readBody_1632(buf, new StreamSeqResponse());
    }
    return null;
  }


  private static StreamSeqResponse readBody_1632(ByteBuf buf, StreamSeqResponse o) {
    o.op = buf.readIntLE();
    o.seq = buf.readIntLE();
    return o;
  }

  public static StreamAskAttachmentResponse read_StreamAskAttachmentResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 15546:
        return readBody_15546(buf, new StreamAskAttachmentResponse());
    }
    return null;
  }


  private static StreamAskAttachmentResponse readBody_15546(ByteBuf buf, StreamAskAttachmentResponse o) {
    o.op = buf.readIntLE();
    o.allowed = buf.readBoolean();
    return o;
  }

  public static StreamError read_StreamError(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 19546:
        return readBody_19546(buf, new StreamError());
    }
    return null;
  }


  private static StreamError readBody_19546(ByteBuf buf, StreamError o) {
    o.op = buf.readIntLE();
    o.code = buf.readIntLE();
    return o;
  }

  public static StreamData read_StreamData(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 10546:
        return readBody_10546(buf, new StreamData());
    }
    return null;
  }


  private static StreamData readBody_10546(ByteBuf buf, StreamData o) {
    o.delta = Helper.readString(buf);
    return o;
  }

  public static StreamStatus read_StreamStatus(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 12546:
        return readBody_12546(buf, new StreamStatus());
    }
    return null;
  }


  private static StreamStatus readBody_12546(ByteBuf buf, StreamStatus o) {
    o.code = buf.readIntLE();
    return o;
  }

  public static MeteringBatchRemoved read_MeteringBatchRemoved(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1248:
        return readBody_1248(buf, new MeteringBatchRemoved());
    }
    return null;
  }


  private static MeteringBatchRemoved readBody_1248(ByteBuf buf, MeteringBatchRemoved o) {
    return o;
  }

  public static MeteringBatchFound read_MeteringBatchFound(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1246:
        return readBody_1246(buf, new MeteringBatchFound());
    }
    return null;
  }


  private static MeteringBatchFound readBody_1246(ByteBuf buf, MeteringBatchFound o) {
    o.id = Helper.readString(buf);
    o.batch = Helper.readString(buf);
    return o;
  }

  public static ScanDeploymentResponse read_ScanDeploymentResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 8922:
        return readBody_8922(buf, new ScanDeploymentResponse());
    }
    return null;
  }


  private static ScanDeploymentResponse readBody_8922(ByteBuf buf, ScanDeploymentResponse o) {
    return o;
  }

  public static ReflectResponse read_ReflectResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 6736:
        return readBody_6736(buf, new ReflectResponse());
    }
    return null;
  }


  private static ReflectResponse readBody_6736(ByteBuf buf, ReflectResponse o) {
    o.schema = Helper.readString(buf);
    return o;
  }

  public static CreateResponse read_CreateResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 12524:
        return readBody_12524(buf, new CreateResponse());
    }
    return null;
  }


  private static CreateResponse readBody_12524(ByteBuf buf, CreateResponse o) {
    return o;
  }

  public static PingResponse read_PingResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 24322:
        return readBody_24322(buf, new PingResponse());
    }
    return null;
  }


  private static PingResponse readBody_24322(ByteBuf buf, PingResponse o) {
    return o;
  }

  public static void write(ByteBuf buf, ProxyLocalDataChange o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(9006);
    Helper.writeString(buf, o.patch);;
    buf.writeIntLE(o.reads);
  }

  public static void write(ByteBuf buf, ProxyIntResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(9004);
    buf.writeIntLE(o.value);
  }

  public static void write(ByteBuf buf, ProxyVoidResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(9002);
  }

  public static void write(ByteBuf buf, InventoryHeartbeat o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(7232);
    Helper.writeStringArray(buf, o.spaces);;
  }

  public static void write(ByteBuf buf, HeatPayload o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(5122);
    buf.writeDoubleLE(o.cpu);
    buf.writeDoubleLE(o.mem);
  }

  public static void write(ByteBuf buf, StreamSeqResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1632);
    buf.writeIntLE(o.op);
    buf.writeIntLE(o.seq);
  }

  public static void write(ByteBuf buf, StreamAskAttachmentResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(15546);
    buf.writeIntLE(o.op);
    buf.writeBoolean(o.allowed);
  }

  public static void write(ByteBuf buf, StreamError o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(19546);
    buf.writeIntLE(o.op);
    buf.writeIntLE(o.code);
  }

  public static void write(ByteBuf buf, StreamData o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(10546);
    Helper.writeString(buf, o.delta);;
  }

  public static void write(ByteBuf buf, StreamStatus o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(12546);
    buf.writeIntLE(o.code);
  }

  public static void write(ByteBuf buf, MeteringBatchRemoved o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1248);
  }

  public static void write(ByteBuf buf, MeteringBatchFound o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1246);
    Helper.writeString(buf, o.id);;
    Helper.writeString(buf, o.batch);;
  }

  public static void write(ByteBuf buf, ScanDeploymentResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(8922);
  }

  public static void write(ByteBuf buf, ReflectResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(6736);
    Helper.writeString(buf, o.schema);;
  }

  public static void write(ByteBuf buf, CreateResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(12524);
  }

  public static void write(ByteBuf buf, PingResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(24322);
  }
}
