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
import org.adamalang.net.codec.ClientMessage.ForceBackupRequest;
import org.adamalang.net.codec.ClientMessage.RateLimitTestRequest;
import org.adamalang.net.codec.ClientMessage.AuthorizationRequest;
import org.adamalang.net.codec.ClientMessage.Authorize;
import org.adamalang.net.codec.ClientMessage.ReplicaDisconnect;
import org.adamalang.net.codec.ClientMessage.ReplicaConnect;
import org.adamalang.net.codec.ClientMessage.DirectSend;
import org.adamalang.net.codec.ClientMessage.ExecuteQuery;
import org.adamalang.net.codec.ClientMessage.WebDelete;
import org.adamalang.net.codec.ClientMessage.WebOptions;
import org.adamalang.net.codec.ClientMessage.WebPut;
import org.adamalang.net.codec.ClientMessage.WebGet;
import org.adamalang.net.codec.ClientMessage.Header;
import org.adamalang.net.codec.ClientMessage.RequestInventoryHeartbeat;
import org.adamalang.net.codec.ClientMessage.RequestHeat;
import org.adamalang.net.codec.ClientMessage.StreamAttach;
import org.adamalang.net.codec.ClientMessage.StreamAskAttachmentRequest;
import org.adamalang.net.codec.ClientMessage.StreamDisconnect;
import org.adamalang.net.codec.ClientMessage.StreamUpdate;
import org.adamalang.net.codec.ClientMessage.StreamPassword;
import org.adamalang.net.codec.ClientMessage.StreamSend;
import org.adamalang.net.codec.ClientMessage.StreamConnect;
import org.adamalang.net.codec.ClientMessage.ScanDeployment;
import org.adamalang.net.codec.ClientMessage.ReflectRequest;
import org.adamalang.net.codec.ClientMessage.DeleteRequest;
import org.adamalang.net.codec.ClientMessage.CreateRequest;
import org.adamalang.net.codec.ClientMessage.ProbeCommandRequest;
import org.adamalang.net.codec.ClientMessage.FindRequest;
import org.adamalang.net.codec.ClientMessage.LoadRequest;
import org.adamalang.net.codec.ClientMessage.DrainRequest;
import org.adamalang.net.codec.ClientMessage.PingRequest;

public class ClientCodec {

  public static abstract class StreamServer implements ByteStream {
    public abstract void handle(ForceBackupRequest payload);

    public abstract void handle(RateLimitTestRequest payload);

    public abstract void handle(AuthorizationRequest payload);

    public abstract void handle(Authorize payload);

    public abstract void handle(ReplicaDisconnect payload);

    public abstract void handle(ReplicaConnect payload);

    public abstract void handle(DirectSend payload);

    public abstract void handle(ExecuteQuery payload);

    public abstract void handle(WebDelete payload);

    public abstract void handle(WebOptions payload);

    public abstract void handle(WebPut payload);

    public abstract void handle(WebGet payload);

    public abstract void handle(RequestInventoryHeartbeat payload);

    public abstract void handle(RequestHeat payload);

    public abstract void handle(StreamAttach payload);

    public abstract void handle(StreamAskAttachmentRequest payload);

    public abstract void handle(StreamDisconnect payload);

    public abstract void handle(StreamUpdate payload);

    public abstract void handle(StreamPassword payload);

    public abstract void handle(StreamSend payload);

    public abstract void handle(StreamConnect payload);

    public abstract void handle(ScanDeployment payload);

    public abstract void handle(ReflectRequest payload);

    public abstract void handle(DeleteRequest payload);

    public abstract void handle(CreateRequest payload);

    public abstract void handle(ProbeCommandRequest payload);

    public abstract void handle(FindRequest payload);

    public abstract void handle(LoadRequest payload);

    public abstract void handle(DrainRequest payload);

    public abstract void handle(PingRequest payload);

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
        case 10303:
          handle(readBody_10303(buf, new ForceBackupRequest()));
          return;
        case 3044:
          handle(readBody_3044(buf, new RateLimitTestRequest()));
          return;
        case 2126:
          handle(readBody_2126(buf, new AuthorizationRequest()));
          return;
        case 2124:
          handle(readBody_2124(buf, new Authorize()));
          return;
        case 13337:
          handle(readBody_13337(buf, new ReplicaDisconnect()));
          return;
        case 12347:
          handle(readBody_12347(buf, new ReplicaConnect()));
          return;
        case 17345:
          handle(readBody_17345(buf, new DirectSend()));
          return;
        case 1999:
          handle(readBody_1999(buf, new ExecuteQuery()));
          return;
        case 1727:
          handle(readBody_1727(buf, new WebDelete()));
          return;
        case 1725:
          handle(readBody_1725(buf, new WebOptions()));
          return;
        case 1723:
          handle(readBody_1723(buf, new WebPut()));
          return;
        case 1721:
          handle(readBody_1721(buf, new WebGet()));
          return;
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
        case 12945:
          handle(readBody_12945(buf, new StreamPassword()));
          return;
        case 13345:
          handle(readBody_13345(buf, new StreamSend()));
          return;
        case 12345:
          handle(readBody_12345(buf, new StreamConnect()));
          return;
        case 8921:
          handle(readBody_8921(buf, new ScanDeployment()));
          return;
        case 6735:
          handle(readBody_6735(buf, new ReflectRequest()));
          return;
        case 12525:
          handle(readBody_12525(buf, new DeleteRequest()));
          return;
        case 12523:
          handle(readBody_12523(buf, new CreateRequest()));
          return;
        case 1017:
          handle(readBody_1017(buf, new ProbeCommandRequest()));
          return;
        case 9001:
          handle(readBody_9001(buf, new FindRequest()));
          return;
        case 24325:
          handle(readBody_24325(buf, new LoadRequest()));
          return;
        case 24323:
          handle(readBody_24323(buf, new DrainRequest()));
          return;
        case 24321:
          handle(readBody_24321(buf, new PingRequest()));
          return;
      }
    }
  }

  public static interface HandlerServer {
    public void handle(ForceBackupRequest payload);
    public void handle(RateLimitTestRequest payload);
    public void handle(AuthorizationRequest payload);
    public void handle(Authorize payload);
    public void handle(ReplicaDisconnect payload);
    public void handle(ReplicaConnect payload);
    public void handle(DirectSend payload);
    public void handle(ExecuteQuery payload);
    public void handle(WebDelete payload);
    public void handle(WebOptions payload);
    public void handle(WebPut payload);
    public void handle(WebGet payload);
    public void handle(RequestInventoryHeartbeat payload);
    public void handle(RequestHeat payload);
    public void handle(StreamAttach payload);
    public void handle(StreamAskAttachmentRequest payload);
    public void handle(StreamDisconnect payload);
    public void handle(StreamUpdate payload);
    public void handle(StreamPassword payload);
    public void handle(StreamSend payload);
    public void handle(StreamConnect payload);
    public void handle(ScanDeployment payload);
    public void handle(ReflectRequest payload);
    public void handle(DeleteRequest payload);
    public void handle(CreateRequest payload);
    public void handle(ProbeCommandRequest payload);
    public void handle(FindRequest payload);
    public void handle(LoadRequest payload);
    public void handle(DrainRequest payload);
    public void handle(PingRequest payload);
  }

  public static void route(ByteBuf buf, HandlerServer handler) {
    switch (buf.readIntLE()) {
      case 10303:
        handler.handle(readBody_10303(buf, new ForceBackupRequest()));
        return;
      case 3044:
        handler.handle(readBody_3044(buf, new RateLimitTestRequest()));
        return;
      case 2126:
        handler.handle(readBody_2126(buf, new AuthorizationRequest()));
        return;
      case 2124:
        handler.handle(readBody_2124(buf, new Authorize()));
        return;
      case 13337:
        handler.handle(readBody_13337(buf, new ReplicaDisconnect()));
        return;
      case 12347:
        handler.handle(readBody_12347(buf, new ReplicaConnect()));
        return;
      case 17345:
        handler.handle(readBody_17345(buf, new DirectSend()));
        return;
      case 1999:
        handler.handle(readBody_1999(buf, new ExecuteQuery()));
        return;
      case 1727:
        handler.handle(readBody_1727(buf, new WebDelete()));
        return;
      case 1725:
        handler.handle(readBody_1725(buf, new WebOptions()));
        return;
      case 1723:
        handler.handle(readBody_1723(buf, new WebPut()));
        return;
      case 1721:
        handler.handle(readBody_1721(buf, new WebGet()));
        return;
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
      case 12945:
        handler.handle(readBody_12945(buf, new StreamPassword()));
        return;
      case 13345:
        handler.handle(readBody_13345(buf, new StreamSend()));
        return;
      case 12345:
        handler.handle(readBody_12345(buf, new StreamConnect()));
        return;
      case 8921:
        handler.handle(readBody_8921(buf, new ScanDeployment()));
        return;
      case 6735:
        handler.handle(readBody_6735(buf, new ReflectRequest()));
        return;
      case 12525:
        handler.handle(readBody_12525(buf, new DeleteRequest()));
        return;
      case 12523:
        handler.handle(readBody_12523(buf, new CreateRequest()));
        return;
      case 1017:
        handler.handle(readBody_1017(buf, new ProbeCommandRequest()));
        return;
      case 9001:
        handler.handle(readBody_9001(buf, new FindRequest()));
        return;
      case 24325:
        handler.handle(readBody_24325(buf, new LoadRequest()));
        return;
      case 24323:
        handler.handle(readBody_24323(buf, new DrainRequest()));
        return;
      case 24321:
        handler.handle(readBody_24321(buf, new PingRequest()));
        return;
    }
  }


  public static abstract class StreamWebGetHeader implements ByteStream {
    public abstract void handle(Header payload);

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
        case 1722:
          handle(readBody_1722(buf, new Header()));
          return;
      }
    }
  }

  public static interface HandlerWebGetHeader {
    public void handle(Header payload);
  }

  public static void route(ByteBuf buf, HandlerWebGetHeader handler) {
    switch (buf.readIntLE()) {
      case 1722:
        handler.handle(readBody_1722(buf, new Header()));
        return;
    }
  }


  public static ForceBackupRequest read_ForceBackupRequest(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 10303:
        return readBody_10303(buf, new ForceBackupRequest());
    }
    return null;
  }


  private static ForceBackupRequest readBody_10303(ByteBuf buf, ForceBackupRequest o) {
    o.space = Helper.readString(buf);
    o.key = Helper.readString(buf);
    o.agent = Helper.readString(buf);
    o.authority = Helper.readString(buf);
    o.origin = Helper.readString(buf);
    o.ip = Helper.readString(buf);
    return o;
  }

  public static RateLimitTestRequest read_RateLimitTestRequest(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 3044:
        return readBody_3044(buf, new RateLimitTestRequest());
    }
    return null;
  }


  private static RateLimitTestRequest readBody_3044(ByteBuf buf, RateLimitTestRequest o) {
    o.ip = Helper.readString(buf);
    o.session = Helper.readString(buf);
    o.resource = Helper.readString(buf);
    o.type = Helper.readString(buf);
    return o;
  }

  public static AuthorizationRequest read_AuthorizationRequest(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 2126:
        return readBody_2126(buf, new AuthorizationRequest());
    }
    return null;
  }


  private static AuthorizationRequest readBody_2126(ByteBuf buf, AuthorizationRequest o) {
    o.space = Helper.readString(buf);
    o.key = Helper.readString(buf);
    o.payload = Helper.readString(buf);
    o.ip = Helper.readString(buf);
    o.origin = Helper.readString(buf);
    return o;
  }

  public static Authorize read_Authorize(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 2124:
        return readBody_2124(buf, new Authorize());
    }
    return null;
  }


  private static Authorize readBody_2124(ByteBuf buf, Authorize o) {
    o.space = Helper.readString(buf);
    o.key = Helper.readString(buf);
    o.username = Helper.readString(buf);
    o.password = Helper.readString(buf);
    o.new_password = Helper.readString(buf);
    o.ip = Helper.readString(buf);
    o.origin = Helper.readString(buf);
    return o;
  }

  public static ReplicaDisconnect read_ReplicaDisconnect(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 13337:
        return readBody_13337(buf, new ReplicaDisconnect());
    }
    return null;
  }


  private static ReplicaDisconnect readBody_13337(ByteBuf buf, ReplicaDisconnect o) {
    return o;
  }

  public static ReplicaConnect read_ReplicaConnect(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 12347:
        return readBody_12347(buf, new ReplicaConnect());
    }
    return null;
  }


  private static ReplicaConnect readBody_12347(ByteBuf buf, ReplicaConnect o) {
    o.space = Helper.readString(buf);
    o.key = Helper.readString(buf);
    return o;
  }

  public static DirectSend read_DirectSend(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 17345:
        return readBody_17345(buf, new DirectSend());
    }
    return null;
  }


  private static DirectSend readBody_17345(ByteBuf buf, DirectSend o) {
    o.space = Helper.readString(buf);
    o.key = Helper.readString(buf);
    o.agent = Helper.readString(buf);
    o.authority = Helper.readString(buf);
    o.origin = Helper.readString(buf);
    o.ip = Helper.readString(buf);
    o.marker = Helper.readString(buf);
    o.channel = Helper.readString(buf);
    o.message = Helper.readString(buf);
    return o;
  }

  public static ExecuteQuery read_ExecuteQuery(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1999:
        return readBody_1999(buf, new ExecuteQuery());
    }
    return null;
  }


  private static ExecuteQuery readBody_1999(ByteBuf buf, ExecuteQuery o) {
    o.headers = Helper.readArray(buf, (n) -> new Header[n], () -> read_Header(buf));
    return o;
  }

  public static WebDelete read_WebDelete(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1727:
        return readBody_1727(buf, new WebDelete());
    }
    return null;
  }


  private static WebDelete readBody_1727(ByteBuf buf, WebDelete o) {
    o.space = Helper.readString(buf);
    o.key = Helper.readString(buf);
    o.agent = Helper.readString(buf);
    o.authority = Helper.readString(buf);
    o.uri = Helper.readString(buf);
    o.headers = Helper.readArray(buf, (n) -> new Header[n], () -> read_Header(buf));
    o.parametersJson = Helper.readString(buf);
    o.origin = Helper.readString(buf);
    o.ip = Helper.readString(buf);
    return o;
  }

  public static WebOptions read_WebOptions(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1725:
        return readBody_1725(buf, new WebOptions());
    }
    return null;
  }


  private static WebOptions readBody_1725(ByteBuf buf, WebOptions o) {
    o.space = Helper.readString(buf);
    o.key = Helper.readString(buf);
    o.agent = Helper.readString(buf);
    o.authority = Helper.readString(buf);
    o.uri = Helper.readString(buf);
    o.headers = Helper.readArray(buf, (n) -> new Header[n], () -> read_Header(buf));
    o.parametersJson = Helper.readString(buf);
    o.origin = Helper.readString(buf);
    o.ip = Helper.readString(buf);
    return o;
  }

  public static WebPut read_WebPut(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1723:
        return readBody_1723(buf, new WebPut());
    }
    return null;
  }


  private static WebPut readBody_1723(ByteBuf buf, WebPut o) {
    o.space = Helper.readString(buf);
    o.key = Helper.readString(buf);
    o.agent = Helper.readString(buf);
    o.authority = Helper.readString(buf);
    o.uri = Helper.readString(buf);
    o.headers = Helper.readArray(buf, (n) -> new Header[n], () -> read_Header(buf));
    o.parametersJson = Helper.readString(buf);
    o.bodyJson = Helper.readString(buf);
    o.origin = Helper.readString(buf);
    o.ip = Helper.readString(buf);
    return o;
  }

  public static WebGet read_WebGet(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1721:
        return readBody_1721(buf, new WebGet());
    }
    return null;
  }


  private static WebGet readBody_1721(ByteBuf buf, WebGet o) {
    o.space = Helper.readString(buf);
    o.key = Helper.readString(buf);
    o.agent = Helper.readString(buf);
    o.authority = Helper.readString(buf);
    o.uri = Helper.readString(buf);
    o.headers = Helper.readArray(buf, (n) -> new Header[n], () -> read_Header(buf));
    o.parametersJson = Helper.readString(buf);
    o.origin = Helper.readString(buf);
    o.ip = Helper.readString(buf);
    return o;
  }

  public static Header read_Header(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1722:
        return readBody_1722(buf, new Header());
    }
    return null;
  }


  private static Header readBody_1722(ByteBuf buf, Header o) {
    o.key = Helper.readString(buf);
    o.value = Helper.readString(buf);
    return o;
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
    o.op = buf.readIntLE();
    o.viewerState = Helper.readString(buf);
    return o;
  }

  public static StreamPassword read_StreamPassword(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 12945:
        return readBody_12945(buf, new StreamPassword());
    }
    return null;
  }


  private static StreamPassword readBody_12945(ByteBuf buf, StreamPassword o) {
    o.op = buf.readIntLE();
    o.password = Helper.readString(buf);
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
    o.ip = Helper.readString(buf);
    o.mode = buf.readIntLE();
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

  public static DeleteRequest read_DeleteRequest(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 12525:
        return readBody_12525(buf, new DeleteRequest());
    }
    return null;
  }


  private static DeleteRequest readBody_12525(ByteBuf buf, DeleteRequest o) {
    o.space = Helper.readString(buf);
    o.key = Helper.readString(buf);
    o.agent = Helper.readString(buf);
    o.authority = Helper.readString(buf);
    o.origin = Helper.readString(buf);
    o.ip = Helper.readString(buf);
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
    o.ip = Helper.readString(buf);
    return o;
  }

  public static ProbeCommandRequest read_ProbeCommandRequest(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 1017:
        return readBody_1017(buf, new ProbeCommandRequest());
    }
    return null;
  }


  private static ProbeCommandRequest readBody_1017(ByteBuf buf, ProbeCommandRequest o) {
    o.command = Helper.readString(buf);
    o.args = Helper.readStringArray(buf);
    return o;
  }

  public static FindRequest read_FindRequest(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 9001:
        return readBody_9001(buf, new FindRequest());
    }
    return null;
  }


  private static FindRequest readBody_9001(ByteBuf buf, FindRequest o) {
    o.space = Helper.readString(buf);
    o.key = Helper.readString(buf);
    return o;
  }

  public static LoadRequest read_LoadRequest(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 24325:
        return readBody_24325(buf, new LoadRequest());
    }
    return null;
  }


  private static LoadRequest readBody_24325(ByteBuf buf, LoadRequest o) {
    return o;
  }

  public static DrainRequest read_DrainRequest(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 24323:
        return readBody_24323(buf, new DrainRequest());
    }
    return null;
  }


  private static DrainRequest readBody_24323(ByteBuf buf, DrainRequest o) {
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

  public static void write(ByteBuf buf, ForceBackupRequest o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(10303);
    Helper.writeString(buf, o.space);;
    Helper.writeString(buf, o.key);;
    Helper.writeString(buf, o.agent);;
    Helper.writeString(buf, o.authority);;
    Helper.writeString(buf, o.origin);;
    Helper.writeString(buf, o.ip);;
  }

  public static void write(ByteBuf buf, RateLimitTestRequest o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(3044);
    Helper.writeString(buf, o.ip);;
    Helper.writeString(buf, o.session);;
    Helper.writeString(buf, o.resource);;
    Helper.writeString(buf, o.type);;
  }

  public static void write(ByteBuf buf, AuthorizationRequest o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(2126);
    Helper.writeString(buf, o.space);;
    Helper.writeString(buf, o.key);;
    Helper.writeString(buf, o.payload);;
    Helper.writeString(buf, o.ip);;
    Helper.writeString(buf, o.origin);;
  }

  public static void write(ByteBuf buf, Authorize o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(2124);
    Helper.writeString(buf, o.space);;
    Helper.writeString(buf, o.key);;
    Helper.writeString(buf, o.username);;
    Helper.writeString(buf, o.password);;
    Helper.writeString(buf, o.new_password);;
    Helper.writeString(buf, o.ip);;
    Helper.writeString(buf, o.origin);;
  }

  public static void write(ByteBuf buf, ReplicaDisconnect o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(13337);
  }

  public static void write(ByteBuf buf, ReplicaConnect o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(12347);
    Helper.writeString(buf, o.space);;
    Helper.writeString(buf, o.key);;
  }

  public static void write(ByteBuf buf, DirectSend o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(17345);
    Helper.writeString(buf, o.space);;
    Helper.writeString(buf, o.key);;
    Helper.writeString(buf, o.agent);;
    Helper.writeString(buf, o.authority);;
    Helper.writeString(buf, o.origin);;
    Helper.writeString(buf, o.ip);;
    Helper.writeString(buf, o.marker);;
    Helper.writeString(buf, o.channel);;
    Helper.writeString(buf, o.message);;
  }

  public static void write(ByteBuf buf, ExecuteQuery o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1999);
    Helper.writeArray(buf, o.headers, (item) -> write(buf, item));
  }

  public static void write(ByteBuf buf, WebDelete o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1727);
    Helper.writeString(buf, o.space);;
    Helper.writeString(buf, o.key);;
    Helper.writeString(buf, o.agent);;
    Helper.writeString(buf, o.authority);;
    Helper.writeString(buf, o.uri);;
    Helper.writeArray(buf, o.headers, (item) -> write(buf, item));
    Helper.writeString(buf, o.parametersJson);;
    Helper.writeString(buf, o.origin);;
    Helper.writeString(buf, o.ip);;
  }

  public static void write(ByteBuf buf, WebOptions o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1725);
    Helper.writeString(buf, o.space);;
    Helper.writeString(buf, o.key);;
    Helper.writeString(buf, o.agent);;
    Helper.writeString(buf, o.authority);;
    Helper.writeString(buf, o.uri);;
    Helper.writeArray(buf, o.headers, (item) -> write(buf, item));
    Helper.writeString(buf, o.parametersJson);;
    Helper.writeString(buf, o.origin);;
    Helper.writeString(buf, o.ip);;
  }

  public static void write(ByteBuf buf, WebPut o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1723);
    Helper.writeString(buf, o.space);;
    Helper.writeString(buf, o.key);;
    Helper.writeString(buf, o.agent);;
    Helper.writeString(buf, o.authority);;
    Helper.writeString(buf, o.uri);;
    Helper.writeArray(buf, o.headers, (item) -> write(buf, item));
    Helper.writeString(buf, o.parametersJson);;
    Helper.writeString(buf, o.bodyJson);;
    Helper.writeString(buf, o.origin);;
    Helper.writeString(buf, o.ip);;
  }

  public static void write(ByteBuf buf, WebGet o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1721);
    Helper.writeString(buf, o.space);;
    Helper.writeString(buf, o.key);;
    Helper.writeString(buf, o.agent);;
    Helper.writeString(buf, o.authority);;
    Helper.writeString(buf, o.uri);;
    Helper.writeArray(buf, o.headers, (item) -> write(buf, item));
    Helper.writeString(buf, o.parametersJson);;
    Helper.writeString(buf, o.origin);;
    Helper.writeString(buf, o.ip);;
  }

  public static void write(ByteBuf buf, Header o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1722);
    Helper.writeString(buf, o.key);;
    Helper.writeString(buf, o.value);;
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
    buf.writeIntLE(o.op);
    Helper.writeString(buf, o.viewerState);;
  }

  public static void write(ByteBuf buf, StreamPassword o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(12945);
    buf.writeIntLE(o.op);
    Helper.writeString(buf, o.password);;
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
    Helper.writeString(buf, o.ip);;
    buf.writeIntLE(o.mode);
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

  public static void write(ByteBuf buf, DeleteRequest o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(12525);
    Helper.writeString(buf, o.space);;
    Helper.writeString(buf, o.key);;
    Helper.writeString(buf, o.agent);;
    Helper.writeString(buf, o.authority);;
    Helper.writeString(buf, o.origin);;
    Helper.writeString(buf, o.ip);;
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
    Helper.writeString(buf, o.ip);;
  }

  public static void write(ByteBuf buf, ProbeCommandRequest o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(1017);
    Helper.writeString(buf, o.command);;
    Helper.writeStringArray(buf, o.args);;
  }

  public static void write(ByteBuf buf, FindRequest o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(9001);
    Helper.writeString(buf, o.space);;
    Helper.writeString(buf, o.key);;
  }

  public static void write(ByteBuf buf, LoadRequest o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(24325);
  }

  public static void write(ByteBuf buf, DrainRequest o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(24323);
  }

  public static void write(ByteBuf buf, PingRequest o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(24321);
  }
}
