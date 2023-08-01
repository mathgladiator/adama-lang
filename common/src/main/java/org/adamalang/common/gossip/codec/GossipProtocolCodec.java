/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common.gossip.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.adamalang.common.codec.Helper;
import org.adamalang.common.gossip.codec.GossipProtocol.*;
import org.adamalang.common.net.ByteStream;

public class GossipProtocolCodec {

  public static void route(ByteBuf buf, HandlerChatterFromServer handler) {
    switch (buf.readIntLE()) {
      case 39:
        handler.handle(readBody_39(buf, new ReverseSlowGossip()));
        return;
      case 37:
        handler.handle(readBody_37(buf, new ReverseQuickGossip()));
        return;
      case 35:
        handler.handle(readBody_35(buf, new HashNotFoundReverseConversation()));
        return;
      case 33:
        handler.handle(readBody_33(buf, new HashFoundRequestForwardQuickGossip()));
    }
  }

  public static void route(ByteBuf buf, HandlerChatterFromClient handler) {
    switch (buf.readIntLE()) {
      case 38:
        handler.handle(readBody_38(buf, new ForwardSlowGossip()));
        return;
      case 36:
        handler.handle(readBody_36(buf, new ReverseHashFound()));
        return;
      case 34:
        handler.handle(readBody_34(buf, new ForwardQuickGossip()));
        return;
      case 31:
        handler.handle(readBody_31(buf, new BeginGossip()));
    }
  }

  public static void route(ByteBuf buf, HandlerRaw handler) {
    if (buf.readIntLE() == 30) {
      handler.handle(readBody_30(buf, new Endpoint()));
    }
  }

  private static Endpoint readBody_30(ByteBuf buf, Endpoint o) {
    o.id = Helper.readString(buf);
    o.ip = Helper.readString(buf);
    o.port = buf.readIntLE();
    o.monitoringPort = buf.readIntLE();
    o.counter = buf.readIntLE();
    o.role = Helper.readString(buf);
    o.created = buf.readLongLE();
    return o;
  }

  public static ReverseSlowGossip read_ReverseSlowGossip(ByteBuf buf) {
    if (buf.readIntLE() == 39) {
      return readBody_39(buf, new ReverseSlowGossip());
    }
    return null;
  }

  private static ReverseSlowGossip readBody_39(ByteBuf buf, ReverseSlowGossip o) {
    o.all_endpoints = Helper.readArray(buf, (n) -> new Endpoint[n], () -> read_Endpoint(buf));
    o.recent_deletes = Helper.readStringArray(buf);
    return o;
  }

  public static Endpoint read_Endpoint(ByteBuf buf) {
    if (buf.readIntLE() == 30) {
      return readBody_30(buf, new Endpoint());
    }
    return null;
  }

  public static ForwardSlowGossip read_ForwardSlowGossip(ByteBuf buf) {
    if (buf.readIntLE() == 38) {
      return readBody_38(buf, new ForwardSlowGossip());
    }
    return null;
  }

  private static ForwardSlowGossip readBody_38(ByteBuf buf, ForwardSlowGossip o) {
    o.all_endpoints = Helper.readArray(buf, (n) -> new Endpoint[n], () -> read_Endpoint(buf));
    o.recent_deletes = Helper.readStringArray(buf);
    return o;
  }

  public static ReverseQuickGossip read_ReverseQuickGossip(ByteBuf buf) {
    if (buf.readIntLE() == 37) {
      return readBody_37(buf, new ReverseQuickGossip());
    }
    return null;
  }

  private static ReverseQuickGossip readBody_37(ByteBuf buf, ReverseQuickGossip o) {
    o.counters = Helper.readIntArray(buf);
    o.missing_endpoints = Helper.readArray(buf, (n) -> new Endpoint[n], () -> read_Endpoint(buf));
    o.recent_deletes = Helper.readStringArray(buf);
    return o;
  }

  public static ReverseHashFound read_ReverseHashFound(ByteBuf buf) {
    if (buf.readIntLE() == 36) {
      return readBody_36(buf, new ReverseHashFound());
    }
    return null;
  }

  private static ReverseHashFound readBody_36(ByteBuf buf, ReverseHashFound o) {
    o.counters = Helper.readIntArray(buf);
    o.missing_endpoints = Helper.readArray(buf, (n) -> new Endpoint[n], () -> read_Endpoint(buf));
    o.recent_deletes = Helper.readStringArray(buf);
    return o;
  }

  public static HashNotFoundReverseConversation read_HashNotFoundReverseConversation(ByteBuf buf) {
    if (buf.readIntLE() == 35) {
      return readBody_35(buf, new HashNotFoundReverseConversation());
    }
    return null;
  }

  private static HashNotFoundReverseConversation readBody_35(ByteBuf buf, HashNotFoundReverseConversation o) {
    o.hash = Helper.readString(buf);
    o.recent_endpoints = Helper.readArray(buf, (n) -> new Endpoint[n], () -> read_Endpoint(buf));
    o.recent_deletes = Helper.readStringArray(buf);
    return o;
  }

  public static ForwardQuickGossip read_ForwardQuickGossip(ByteBuf buf) {
    if (buf.readIntLE() == 34) {
      return readBody_34(buf, new ForwardQuickGossip());
    }
    return null;
  }

  private static ForwardQuickGossip readBody_34(ByteBuf buf, ForwardQuickGossip o) {
    o.counters = Helper.readIntArray(buf);
    o.recent_endpoints = Helper.readArray(buf, (n) -> new Endpoint[n], () -> read_Endpoint(buf));
    o.recent_deletes = Helper.readStringArray(buf);
    return o;
  }

  public static HashFoundRequestForwardQuickGossip read_HashFoundRequestForwardQuickGossip(ByteBuf buf) {
    if (buf.readIntLE() == 33) {
      return readBody_33(buf, new HashFoundRequestForwardQuickGossip());
    }
    return null;
  }

  private static HashFoundRequestForwardQuickGossip readBody_33(ByteBuf buf, HashFoundRequestForwardQuickGossip o) {
    o.counters = Helper.readIntArray(buf);
    o.recent_endpoints = Helper.readArray(buf, (n) -> new Endpoint[n], () -> read_Endpoint(buf));
    o.recent_deletes = Helper.readStringArray(buf);
    return o;
  }

  public static BeginGossip read_BeginGossip(ByteBuf buf) {
    if (buf.readIntLE() == 31) {
      return readBody_31(buf, new BeginGossip());
    }
    return null;
  }

  private static BeginGossip readBody_31(ByteBuf buf, BeginGossip o) {
    o.hash = Helper.readString(buf);
    o.recent_endpoints = Helper.readArray(buf, (n) -> new Endpoint[n], () -> read_Endpoint(buf));
    o.recent_deletes = Helper.readStringArray(buf);
    return o;
  }

  public static void write(ByteBuf buf, ReverseSlowGossip o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(39);
    Helper.writeArray(buf, o.all_endpoints, (item) -> write(buf, item));
    Helper.writeStringArray(buf, o.recent_deletes);
  }

  public static void write(ByteBuf buf, Endpoint o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(30);
    Helper.writeString(buf, o.id);
    Helper.writeString(buf, o.ip);
    buf.writeIntLE(o.port);
    buf.writeIntLE(o.monitoringPort);
    buf.writeIntLE(o.counter);
    Helper.writeString(buf, o.role);
    buf.writeLongLE(o.created);
  }

  public static void write(ByteBuf buf, ForwardSlowGossip o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(38);
    Helper.writeArray(buf, o.all_endpoints, (item) -> write(buf, item));
    Helper.writeStringArray(buf, o.recent_deletes);
  }

  public static void write(ByteBuf buf, ReverseQuickGossip o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(37);
    Helper.writeIntArray(buf, o.counters);
    Helper.writeArray(buf, o.missing_endpoints, (item) -> write(buf, item));
    Helper.writeStringArray(buf, o.recent_deletes);
  }

  public static void write(ByteBuf buf, ReverseHashFound o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(36);
    Helper.writeIntArray(buf, o.counters);
    Helper.writeArray(buf, o.missing_endpoints, (item) -> write(buf, item));
    Helper.writeStringArray(buf, o.recent_deletes);
  }

  public static void write(ByteBuf buf, HashNotFoundReverseConversation o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(35);
    Helper.writeString(buf, o.hash);
    Helper.writeArray(buf, o.recent_endpoints, (item) -> write(buf, item));
    Helper.writeStringArray(buf, o.recent_deletes);
  }

  public static void write(ByteBuf buf, ForwardQuickGossip o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(34);
    Helper.writeIntArray(buf, o.counters);
    Helper.writeArray(buf, o.recent_endpoints, (item) -> write(buf, item));
    Helper.writeStringArray(buf, o.recent_deletes);
  }

  public static void write(ByteBuf buf, HashFoundRequestForwardQuickGossip o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(33);
    Helper.writeIntArray(buf, o.counters);
    Helper.writeArray(buf, o.recent_endpoints, (item) -> write(buf, item));
    Helper.writeStringArray(buf, o.recent_deletes);
  }

  public static void write(ByteBuf buf, BeginGossip o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(31);
    Helper.writeString(buf, o.hash);
    Helper.writeArray(buf, o.recent_endpoints, (item) -> write(buf, item));
    Helper.writeStringArray(buf, o.recent_deletes);
  }

  public interface HandlerChatterFromServer {
    void handle(ReverseSlowGossip payload);

    void handle(ReverseQuickGossip payload);

    void handle(HashNotFoundReverseConversation payload);

    void handle(HashFoundRequestForwardQuickGossip payload);
  }

  public interface HandlerChatterFromClient {
    void handle(ForwardSlowGossip payload);

    void handle(ReverseHashFound payload);

    void handle(ForwardQuickGossip payload);

    void handle(BeginGossip payload);
  }

  public interface HandlerRaw {
    void handle(Endpoint payload);
  }

  public static abstract class StreamChatterFromServer implements ByteStream {
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
        case 39:
          handle(readBody_39(buf, new ReverseSlowGossip()));
          return;
        case 37:
          handle(readBody_37(buf, new ReverseQuickGossip()));
          return;
        case 35:
          handle(readBody_35(buf, new HashNotFoundReverseConversation()));
          return;
        case 33:
          handle(readBody_33(buf, new HashFoundRequestForwardQuickGossip()));
      }
    }

    public abstract void handle(ReverseSlowGossip payload);

    public abstract void handle(ReverseQuickGossip payload);

    public abstract void handle(HashNotFoundReverseConversation payload);

    public abstract void handle(HashFoundRequestForwardQuickGossip payload);
  }

  public static abstract class StreamChatterFromClient implements ByteStream {
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
        case 38:
          handle(readBody_38(buf, new ForwardSlowGossip()));
          return;
        case 36:
          handle(readBody_36(buf, new ReverseHashFound()));
          return;
        case 34:
          handle(readBody_34(buf, new ForwardQuickGossip()));
          return;
        case 31:
          handle(readBody_31(buf, new BeginGossip()));
      }
    }

    public abstract void handle(ForwardSlowGossip payload);

    public abstract void handle(ReverseHashFound payload);

    public abstract void handle(ForwardQuickGossip payload);

    public abstract void handle(BeginGossip payload);
  }

  public static abstract class StreamRaw implements ByteStream {
    @Override
    public void request(int bytes) {
    }

    @Override
    public ByteBuf create(int size) {
      return Unpooled.buffer();
    }

    @Override
    public void next(ByteBuf buf) {
      if (buf.readIntLE() == 30) {
        handle(readBody_30(buf, new Endpoint()));
      }
    }

    public abstract void handle(Endpoint payload);
  }
}
