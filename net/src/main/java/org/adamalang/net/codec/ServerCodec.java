/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.common.codec.Helper;
import org.adamalang.common.net.ByteStream;
import org.adamalang.net.codec.ServerMessage.ForceBackupResponse;
import org.adamalang.net.codec.ServerMessage.RateLimitResult;
import org.adamalang.net.codec.ServerMessage.ReplicaData;
import org.adamalang.net.codec.ServerMessage.DirectSendResponse;
import org.adamalang.net.codec.ServerMessage.QueryResult;
import org.adamalang.net.codec.ServerMessage.AuthorizationResponse;
import org.adamalang.net.codec.ServerMessage.AuthResponse;
import org.adamalang.net.codec.ServerMessage.WebResponseNet;
import org.adamalang.net.codec.ServerMessage.InventoryHeartbeat;
import org.adamalang.net.codec.ServerMessage.HeatPayload;
import org.adamalang.net.codec.ServerMessage.StreamSeqResponse;
import org.adamalang.net.codec.ServerMessage.StreamAskAttachmentResponse;
import org.adamalang.net.codec.ServerMessage.StreamUpdateComplete;
import org.adamalang.net.codec.ServerMessage.ObserveUpdateComplete;
import org.adamalang.net.codec.ServerMessage.ObserveError;
import org.adamalang.net.codec.ServerMessage.ObserveData;
import org.adamalang.net.codec.ServerMessage.ObserveConnected;
import org.adamalang.net.codec.ServerMessage.StreamError;
import org.adamalang.net.codec.ServerMessage.StreamData;
import org.adamalang.net.codec.ServerMessage.StreamTrafficHint;
import org.adamalang.net.codec.ServerMessage.StreamStatus;
import org.adamalang.net.codec.ServerMessage.ScanDeploymentResponse;
import org.adamalang.net.codec.ServerMessage.ReflectResponse;
import org.adamalang.net.codec.ServerMessage.DeleteResponse;
import org.adamalang.net.codec.ServerMessage.CreateResponse;
import org.adamalang.net.codec.ServerMessage.ProbeCommandResponse;
import org.adamalang.net.codec.ServerMessage.FindResponse;
import org.adamalang.net.codec.ServerMessage.LoadResponse;
import org.adamalang.net.codec.ServerMessage.DrainResponse;
import org.adamalang.net.codec.ServerMessage.PingResponse;

public class ServerCodec {

  public static abstract class StreamLoad implements ByteStream {
    public abstract void handle(LoadResponse payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 24326:
          handle(readBody_24326(buf, new LoadResponse()));
          return;
      }
    }
  }

  public static interface HandlerLoad {
    public void handle(LoadResponse payload);
  }

  public static void route(ByteBuf buf, HandlerLoad handler) {
    switch (buf.readIntLE()) {
      case 24326:
        handler.handle(readBody_24326(buf, new LoadResponse()));
        return;
    }
  }


  public static abstract class StreamForceBackup implements ByteStream {
    public abstract void handle(ForceBackupResponse payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 10231:
          handle(readBody_10231(buf, new ForceBackupResponse()));
          return;
      }
    }
  }

  public static interface HandlerForceBackup {
    public void handle(ForceBackupResponse payload);
  }

  public static void route(ByteBuf buf, HandlerForceBackup handler) {
    switch (buf.readIntLE()) {
      case 10231:
        handler.handle(readBody_10231(buf, new ForceBackupResponse()));
        return;
    }
  }


  public static abstract class StreamQuery implements ByteStream {
    public abstract void handle(QueryResult payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 2001:
          handle(readBody_2001(buf, new QueryResult()));
          return;
      }
    }
  }

  public static interface HandlerQuery {
    public void handle(QueryResult payload);
  }

  public static void route(ByteBuf buf, HandlerQuery handler) {
    switch (buf.readIntLE()) {
      case 2001:
        handler.handle(readBody_2001(buf, new QueryResult()));
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
      return Unpooled.buffer();
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


  public static abstract class StreamFinder implements ByteStream {
    public abstract void handle(FindResponse payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 9001:
          handle(readBody_9001(buf, new FindResponse()));
          return;
      }
    }
  }

  public static interface HandlerFinder {
    public void handle(FindResponse payload);
  }

  public static void route(ByteBuf buf, HandlerFinder handler) {
    switch (buf.readIntLE()) {
      case 9001:
        handler.handle(readBody_9001(buf, new FindResponse()));
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
      return Unpooled.buffer();
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
      return Unpooled.buffer();
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


  public static abstract class StreamDrain implements ByteStream {
    public abstract void handle(DrainResponse payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 24324:
          handle(readBody_24324(buf, new DrainResponse()));
          return;
      }
    }
  }

  public static interface HandlerDrain {
    public void handle(DrainResponse payload);
  }

  public static void route(ByteBuf buf, HandlerDrain handler) {
    switch (buf.readIntLE()) {
      case 24324:
        handler.handle(readBody_24324(buf, new DrainResponse()));
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
      return Unpooled.buffer();
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

    public abstract void handle(StreamUpdateComplete payload);

    public abstract void handle(StreamError payload);

    public abstract void handle(StreamData payload);

    public abstract void handle(StreamTrafficHint payload);

    public abstract void handle(StreamStatus payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
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
        case 30001:
          handle(readBody_30001(buf, new StreamUpdateComplete()));
          return;
        case 19546:
          handle(readBody_19546(buf, new StreamError()));
          return;
        case 10546:
          handle(readBody_10546(buf, new StreamData()));
          return;
        case 11546:
          handle(readBody_11546(buf, new StreamTrafficHint()));
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
    public void handle(StreamUpdateComplete payload);
    public void handle(StreamError payload);
    public void handle(StreamData payload);
    public void handle(StreamTrafficHint payload);
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
      case 30001:
        handler.handle(readBody_30001(buf, new StreamUpdateComplete()));
        return;
      case 19546:
        handler.handle(readBody_19546(buf, new StreamError()));
        return;
      case 10546:
        handler.handle(readBody_10546(buf, new StreamData()));
        return;
      case 11546:
        handler.handle(readBody_11546(buf, new StreamTrafficHint()));
        return;
      case 12546:
        handler.handle(readBody_12546(buf, new StreamStatus()));
        return;
    }
  }


  public static abstract class StreamProbe implements ByteStream {
    public abstract void handle(ProbeCommandResponse payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 1018:
          handle(readBody_1018(buf, new ProbeCommandResponse()));
          return;
      }
    }
  }

  public static interface HandlerProbe {
    public void handle(ProbeCommandResponse payload);
  }

  public static void route(ByteBuf buf, HandlerProbe handler) {
    switch (buf.readIntLE()) {
      case 1018:
        handler.handle(readBody_1018(buf, new ProbeCommandResponse()));
        return;
    }
  }


  public static abstract class StreamReplica implements ByteStream {
    public abstract void handle(ReplicaData payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 10548:
          handle(readBody_10548(buf, new ReplicaData()));
          return;
      }
    }
  }

  public static interface HandlerReplica {
    public void handle(ReplicaData payload);
  }

  public static void route(ByteBuf buf, HandlerReplica handler) {
    switch (buf.readIntLE()) {
      case 10548:
        handler.handle(readBody_10548(buf, new ReplicaData()));
        return;
    }
  }


  public static abstract class StreamDirect implements ByteStream {
    public abstract void handle(DirectSendResponse payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 1783:
          handle(readBody_1783(buf, new DirectSendResponse()));
          return;
      }
    }
  }

  public static interface HandlerDirect {
    public void handle(DirectSendResponse payload);
  }

  public static void route(ByteBuf buf, HandlerDirect handler) {
    switch (buf.readIntLE()) {
      case 1783:
        handler.handle(readBody_1783(buf, new DirectSendResponse()));
        return;
    }
  }


  public static abstract class StreamAuthorization implements ByteStream {
    public abstract void handle(AuthorizationResponse payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 2125:
          handle(readBody_2125(buf, new AuthorizationResponse()));
          return;
      }
    }
  }

  public static interface HandlerAuthorization {
    public void handle(AuthorizationResponse payload);
  }

  public static void route(ByteBuf buf, HandlerAuthorization handler) {
    switch (buf.readIntLE()) {
      case 2125:
        handler.handle(readBody_2125(buf, new AuthorizationResponse()));
        return;
    }
  }


  public static abstract class StreamWeb implements ByteStream {
    public abstract void handle(WebResponseNet payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 1721:
          handle(readBody_1721(buf, new WebResponseNet()));
          return;
      }
    }
  }

  public static interface HandlerWeb {
    public void handle(WebResponseNet payload);
  }

  public static void route(ByteBuf buf, HandlerWeb handler) {
    switch (buf.readIntLE()) {
      case 1721:
        handler.handle(readBody_1721(buf, new WebResponseNet()));
        return;
    }
  }


  public static abstract class StreamAuth implements ByteStream {
    public abstract void handle(AuthResponse payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 2123:
          handle(readBody_2123(buf, new AuthResponse()));
          return;
      }
    }
  }

  public static interface HandlerAuth {
    public void handle(AuthResponse payload);
  }

  public static void route(ByteBuf buf, HandlerAuth handler) {
    switch (buf.readIntLE()) {
      case 2123:
        handler.handle(readBody_2123(buf, new AuthResponse()));
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
      return Unpooled.buffer();
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


  public static abstract class StreamObservation implements ByteStream {
    public abstract void handle(ObserveUpdateComplete payload);

    public abstract void handle(ObserveError payload);

    public abstract void handle(ObserveData payload);

    public abstract void handle(ObserveConnected payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 4004:
          handle(readBody_4004(buf, new ObserveUpdateComplete()));
          return;
        case 4003:
          handle(readBody_4003(buf, new ObserveError()));
          return;
        case 4002:
          handle(readBody_4002(buf, new ObserveData()));
          return;
        case 4001:
          handle(readBody_4001(buf, new ObserveConnected()));
          return;
      }
    }
  }

  public static interface HandlerObservation {
    public void handle(ObserveUpdateComplete payload);
    public void handle(ObserveError payload);
    public void handle(ObserveData payload);
    public void handle(ObserveConnected payload);
  }

  public static void route(ByteBuf buf, HandlerObservation handler) {
    switch (buf.readIntLE()) {
      case 4004:
        handler.handle(readBody_4004(buf, new ObserveUpdateComplete()));
        return;
      case 4003:
        handler.handle(readBody_4003(buf, new ObserveError()));
        return;
      case 4002:
        handler.handle(readBody_4002(buf, new ObserveData()));
        return;
      case 4001:
        handler.handle(readBody_4001(buf, new ObserveConnected()));
        return;
    }
  }


  public static abstract class StreamDeletion implements ByteStream {
    public abstract void handle(DeleteResponse payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 12526:
          handle(readBody_12526(buf, new DeleteResponse()));
          return;
      }
    }
  }

  public static interface HandlerDeletion {
    public void handle(DeleteResponse payload);
  }

  public static void route(ByteBuf buf, HandlerDeletion handler) {
    switch (buf.readIntLE()) {
      case 12526:
        handler.handle(readBody_12526(buf, new DeleteResponse()));
        return;
    }
  }


  public static abstract class StreamRateLimiting implements ByteStream {
    public abstract void handle(RateLimitResult payload);

    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      switch (buf.readIntLE()) {
        case 3045:
          handle(readBody_3045(buf, new RateLimitResult()));
          return;
      }
    }
  }

  public static interface HandlerRateLimiting {
    public void handle(RateLimitResult payload);
  }

  public static void route(ByteBuf buf, HandlerRateLimiting handler) {
    switch (buf.readIntLE()) {
      case 3045:
        handler.handle(readBody_3045(buf, new RateLimitResult()));
        return;
    }
  }


  public static ForceBackupResponse read_ForceBackupResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 10231:
        return readBody_10231(buf, new ForceBackupResponse());
    }
    return null;
  }


  private static ForceBackupResponse readBody_10231(ByteBuf buf, ForceBackupResponse o) {
    o.backupId = Helper.readString(buf);
    return o;
  }

  public static RateLimitResult read_RateLimitResult(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 3045:
        return readBody_3045(buf, new RateLimitResult());
    }
    return null;
  }


  private static RateLimitResult readBody_3045(ByteBuf buf, RateLimitResult o) {
    o.tokens = buf.readIntLE();
    o.milliseconds = buf.readIntLE();
    return o;
  }

  public static ReplicaData read_ReplicaData(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 10548:
        return readBody_10548(buf, new ReplicaData());
    }
    return null;
  }


  private static ReplicaData readBody_10548(ByteBuf buf, ReplicaData o) {
    o.reset = buf.readBoolean();
    o.change = Helper.readString(buf);
    return o;
  }

  public static DirectSendResponse read_DirectSendResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1783:
        return readBody_1783(buf, new DirectSendResponse());
    }
    return null;
  }


  private static DirectSendResponse readBody_1783(ByteBuf buf, DirectSendResponse o) {
    o.seq = buf.readIntLE();
    return o;
  }

  public static QueryResult read_QueryResult(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 2001:
        return readBody_2001(buf, new QueryResult());
    }
    return null;
  }


  private static QueryResult readBody_2001(ByteBuf buf, QueryResult o) {
    o.result = Helper.readString(buf);
    return o;
  }

  public static AuthorizationResponse read_AuthorizationResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 2125:
        return readBody_2125(buf, new AuthorizationResponse());
    }
    return null;
  }


  private static AuthorizationResponse readBody_2125(ByteBuf buf, AuthorizationResponse o) {
    o.agent = Helper.readString(buf);
    o.hash = Helper.readString(buf);
    o.channel = Helper.readString(buf);
    o.success = Helper.readString(buf);
    return o;
  }

  public static AuthResponse read_AuthResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 2123:
        return readBody_2123(buf, new AuthResponse());
    }
    return null;
  }


  private static AuthResponse readBody_2123(ByteBuf buf, AuthResponse o) {
    o.agent = Helper.readString(buf);
    return o;
  }

  public static WebResponseNet read_WebResponseNet(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1721:
        return readBody_1721(buf, new WebResponseNet());
    }
    return null;
  }


  private static WebResponseNet readBody_1721(ByteBuf buf, WebResponseNet o) {
    o.contentType = Helper.readString(buf);
    o.body = Helper.readString(buf);
    o.assetId = Helper.readString(buf);
    o.assetName = Helper.readString(buf);
    o.assetSize = buf.readLongLE();
    o.assetMD5 = Helper.readString(buf);
    o.assetSHA384 = Helper.readString(buf);
    o.cors = buf.readBoolean();
    o.cacheTimeToLiveSeconds = buf.readIntLE();
    o.assetTransform = Helper.readString(buf);
    o.status = buf.readIntLE();
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

  public static StreamUpdateComplete read_StreamUpdateComplete(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 30001:
        return readBody_30001(buf, new StreamUpdateComplete());
    }
    return null;
  }


  private static StreamUpdateComplete readBody_30001(ByteBuf buf, StreamUpdateComplete o) {
    o.op = buf.readIntLE();
    return o;
  }

  public static ObserveUpdateComplete read_ObserveUpdateComplete(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 4004:
        return readBody_4004(buf, new ObserveUpdateComplete());
    }
    return null;
  }


  private static ObserveUpdateComplete readBody_4004(ByteBuf buf, ObserveUpdateComplete o) {
    o.op = buf.readIntLE();
    return o;
  }

  public static ObserveError read_ObserveError(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 4003:
        return readBody_4003(buf, new ObserveError());
    }
    return null;
  }


  private static ObserveError readBody_4003(ByteBuf buf, ObserveError o) {
    o.op = buf.readIntLE();
    o.code = buf.readIntLE();
    return o;
  }

  public static ObserveData read_ObserveData(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 4002:
        return readBody_4002(buf, new ObserveData());
    }
    return null;
  }


  private static ObserveData readBody_4002(ByteBuf buf, ObserveData o) {
    o.delta = Helper.readString(buf);
    return o;
  }

  public static ObserveConnected read_ObserveConnected(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 4001:
        return readBody_4001(buf, new ObserveConnected());
    }
    return null;
  }


  private static ObserveConnected readBody_4001(ByteBuf buf, ObserveConnected o) {
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

  public static StreamTrafficHint read_StreamTrafficHint(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 11546:
        return readBody_11546(buf, new StreamTrafficHint());
    }
    return null;
  }


  private static StreamTrafficHint readBody_11546(ByteBuf buf, StreamTrafficHint o) {
    o.traffic = Helper.readString(buf);
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

  public static DeleteResponse read_DeleteResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 12526:
        return readBody_12526(buf, new DeleteResponse());
    }
    return null;
  }


  private static DeleteResponse readBody_12526(ByteBuf buf, DeleteResponse o) {
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

  public static ProbeCommandResponse read_ProbeCommandResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1018:
        return readBody_1018(buf, new ProbeCommandResponse());
    }
    return null;
  }


  private static ProbeCommandResponse readBody_1018(ByteBuf buf, ProbeCommandResponse o) {
    o.json = Helper.readString(buf);
    o.errors = Helper.readStringArray(buf);
    return o;
  }

  public static FindResponse read_FindResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 9001:
        return readBody_9001(buf, new FindResponse());
    }
    return null;
  }


  private static FindResponse readBody_9001(ByteBuf buf, FindResponse o) {
    o.id = buf.readLongLE();
    o.location = buf.readIntLE();
    o.archive = Helper.readString(buf);
    o.region = Helper.readString(buf);
    o.machine = Helper.readString(buf);
    o.deleted = buf.readBoolean();
    return o;
  }

  public static LoadResponse read_LoadResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 24326:
        return readBody_24326(buf, new LoadResponse());
    }
    return null;
  }


  private static LoadResponse readBody_24326(ByteBuf buf, LoadResponse o) {
    o.documents = buf.readIntLE();
    o.connections = buf.readIntLE();
    return o;
  }

  public static DrainResponse read_DrainResponse(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 24324:
        return readBody_24324(buf, new DrainResponse());
    }
    return null;
  }


  private static DrainResponse readBody_24324(ByteBuf buf, DrainResponse o) {
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

  public static void write(ByteBuf buf, ForceBackupResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(10231);
    Helper.writeString(buf, o.backupId);;
  }

  public static void write(ByteBuf buf, RateLimitResult o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(3045);
    buf.writeIntLE(o.tokens);
    buf.writeIntLE(o.milliseconds);
  }

  public static void write(ByteBuf buf, ReplicaData o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(10548);
    buf.writeBoolean(o.reset);
    Helper.writeString(buf, o.change);;
  }

  public static void write(ByteBuf buf, DirectSendResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1783);
    buf.writeIntLE(o.seq);
  }

  public static void write(ByteBuf buf, QueryResult o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(2001);
    Helper.writeString(buf, o.result);;
  }

  public static void write(ByteBuf buf, AuthorizationResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(2125);
    Helper.writeString(buf, o.agent);;
    Helper.writeString(buf, o.hash);;
    Helper.writeString(buf, o.channel);;
    Helper.writeString(buf, o.success);;
  }

  public static void write(ByteBuf buf, AuthResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(2123);
    Helper.writeString(buf, o.agent);;
  }

  public static void write(ByteBuf buf, WebResponseNet o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1721);
    Helper.writeString(buf, o.contentType);;
    Helper.writeString(buf, o.body);;
    Helper.writeString(buf, o.assetId);;
    Helper.writeString(buf, o.assetName);;
    buf.writeLongLE(o.assetSize);
    Helper.writeString(buf, o.assetMD5);;
    Helper.writeString(buf, o.assetSHA384);;
    buf.writeBoolean(o.cors);
    buf.writeIntLE(o.cacheTimeToLiveSeconds);
    Helper.writeString(buf, o.assetTransform);;
    buf.writeIntLE(o.status);
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

  public static void write(ByteBuf buf, StreamUpdateComplete o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(30001);
    buf.writeIntLE(o.op);
  }

  public static void write(ByteBuf buf, ObserveUpdateComplete o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(4004);
    buf.writeIntLE(o.op);
  }

  public static void write(ByteBuf buf, ObserveError o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(4003);
    buf.writeIntLE(o.op);
    buf.writeIntLE(o.code);
  }

  public static void write(ByteBuf buf, ObserveData o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(4002);
    Helper.writeString(buf, o.delta);;
  }

  public static void write(ByteBuf buf, ObserveConnected o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(4001);
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

  public static void write(ByteBuf buf, StreamTrafficHint o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(11546);
    Helper.writeString(buf, o.traffic);;
  }

  public static void write(ByteBuf buf, StreamStatus o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(12546);
    buf.writeIntLE(o.code);
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

  public static void write(ByteBuf buf, DeleteResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(12526);
  }

  public static void write(ByteBuf buf, CreateResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(12524);
  }

  public static void write(ByteBuf buf, ProbeCommandResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1018);
    Helper.writeString(buf, o.json);;
    Helper.writeStringArray(buf, o.errors);;
  }

  public static void write(ByteBuf buf, FindResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(9001);
    buf.writeLongLE(o.id);
    buf.writeIntLE(o.location);
    Helper.writeString(buf, o.archive);;
    Helper.writeString(buf, o.region);;
    Helper.writeString(buf, o.machine);;
    buf.writeBoolean(o.deleted);
  }

  public static void write(ByteBuf buf, LoadResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(24326);
    buf.writeIntLE(o.documents);
    buf.writeIntLE(o.connections);
  }

  public static void write(ByteBuf buf, DrainResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(24324);
  }

  public static void write(ByteBuf buf, PingResponse o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(24322);
  }
}
