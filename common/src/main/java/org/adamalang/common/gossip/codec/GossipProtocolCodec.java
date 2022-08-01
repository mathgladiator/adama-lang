/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.gossip.codec;

import io.netty.buffer.ByteBuf;
import org.adamalang.common.codec.Helper;
import org.adamalang.common.net.ByteStream;
import org.adamalang.common.gossip.codec.GossipProtocol.ForwardSlowGossip;
import org.adamalang.common.gossip.codec.GossipProtocol.ReverseHashFound;
import org.adamalang.common.gossip.codec.GossipProtocol.HashNotFoundReverseConversation;
import org.adamalang.common.gossip.codec.GossipProtocol.ForwardQuickGossip;
import org.adamalang.common.gossip.codec.GossipProtocol.HashFoundRequestForwardQuickGossip;
import org.adamalang.common.gossip.codec.GossipProtocol.BeginGossip;
import org.adamalang.common.gossip.codec.GossipProtocol.Endpoint;

public class GossipProtocolCodec {

  public static abstract class StreamRaw implements ByteStream {
    public abstract void handle(Endpoint payload);

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
        case 30000:
          handle(readBody_30000(buf, new Endpoint()));
          return;
      }
    }
  }

  public static interface HandlerRaw {
    public void handle(Endpoint payload);
  }

  public static void route(ByteBuf buf, HandlerRaw handler) {
    switch (buf.readIntLE()) {
      case 30000:
        handler.handle(readBody_30000(buf, new Endpoint()));
        return;
    }
  }


  public static abstract class StreamChatter implements ByteStream {
    public abstract void handle(ForwardSlowGossip payload);

    public abstract void handle(ReverseHashFound payload);

    public abstract void handle(HashNotFoundReverseConversation payload);

    public abstract void handle(ForwardQuickGossip payload);

    public abstract void handle(HashFoundRequestForwardQuickGossip payload);

    public abstract void handle(BeginGossip payload);

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
        case 30006:
          handle(readBody_30006(buf, new ForwardSlowGossip()));
          return;
        case 30005:
          handle(readBody_30005(buf, new ReverseHashFound()));
          return;
        case 30004:
          handle(readBody_30004(buf, new HashNotFoundReverseConversation()));
          return;
        case 30003:
          handle(readBody_30003(buf, new ForwardQuickGossip()));
          return;
        case 30002:
          handle(readBody_30002(buf, new HashFoundRequestForwardQuickGossip()));
          return;
        case 30001:
          handle(readBody_30001(buf, new BeginGossip()));
          return;
      }
    }
  }

  public static interface HandlerChatter {
    public void handle(ForwardSlowGossip payload);
    public void handle(ReverseHashFound payload);
    public void handle(HashNotFoundReverseConversation payload);
    public void handle(ForwardQuickGossip payload);
    public void handle(HashFoundRequestForwardQuickGossip payload);
    public void handle(BeginGossip payload);
  }

  public static void route(ByteBuf buf, HandlerChatter handler) {
    switch (buf.readIntLE()) {
      case 30006:
        handler.handle(readBody_30006(buf, new ForwardSlowGossip()));
        return;
      case 30005:
        handler.handle(readBody_30005(buf, new ReverseHashFound()));
        return;
      case 30004:
        handler.handle(readBody_30004(buf, new HashNotFoundReverseConversation()));
        return;
      case 30003:
        handler.handle(readBody_30003(buf, new ForwardQuickGossip()));
        return;
      case 30002:
        handler.handle(readBody_30002(buf, new HashFoundRequestForwardQuickGossip()));
        return;
      case 30001:
        handler.handle(readBody_30001(buf, new BeginGossip()));
        return;
    }
  }


  public static ForwardSlowGossip read_SlowGossip(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 30006:
        return readBody_30006(buf, new ForwardSlowGossip());
    }
    return null;
  }


  private static ForwardSlowGossip readBody_30006(ByteBuf buf, ForwardSlowGossip o) {
    o.all_endpoints = Helper.readArray(buf, (n) -> new Endpoint[n], () -> read_Endpoint(buf));
    return o;
  }

  public static ReverseHashFound read_ReverseHashFound(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 30005:
        return readBody_30005(buf, new ReverseHashFound());
    }
    return null;
  }


  private static ReverseHashFound readBody_30005(ByteBuf buf, ReverseHashFound o) {
    o.counters = Helper.readIntArray(buf);
    o.missing_endpoints = Helper.readArray(buf, (n) -> new Endpoint[n], () -> read_Endpoint(buf));
    return o;
  }

  public static HashNotFoundReverseConversation read_HashNotFoundReverseConversation(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 30004:
        return readBody_30004(buf, new HashNotFoundReverseConversation());
    }
    return null;
  }


  private static HashNotFoundReverseConversation readBody_30004(ByteBuf buf, HashNotFoundReverseConversation o) {
    o.hash = Helper.readString(buf);
    o.missing_endpoints = Helper.readArray(buf, (n) -> new Endpoint[n], () -> read_Endpoint(buf));
    o.recent_deletes = Helper.readStringArray(buf);
    return o;
  }

  public static ForwardQuickGossip read_ForwardQuickGossip(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 30003:
        return readBody_30003(buf, new ForwardQuickGossip());
    }
    return null;
  }


  private static ForwardQuickGossip readBody_30003(ByteBuf buf, ForwardQuickGossip o) {
    o.counters = Helper.readIntArray(buf);
    return o;
  }

  public static HashFoundRequestForwardQuickGossip read_HashFoundRequestForwardQuickGossip(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 30002:
        return readBody_30002(buf, new HashFoundRequestForwardQuickGossip());
    }
    return null;
  }


  private static HashFoundRequestForwardQuickGossip readBody_30002(ByteBuf buf, HashFoundRequestForwardQuickGossip o) {
    o.counters = Helper.readIntArray(buf);
    o.missing_endpoints = Helper.readArray(buf, (n) -> new Endpoint[n], () -> read_Endpoint(buf));
    o.recent_deletes = Helper.readStringArray(buf);
    return o;
  }

  public static BeginGossip read_BeginGossip(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 30001:
        return readBody_30001(buf, new BeginGossip());
    }
    return null;
  }


  private static BeginGossip readBody_30001(ByteBuf buf, BeginGossip o) {
    o.hash = Helper.readString(buf);
    o.recent_endpoints = Helper.readArray(buf, (n) -> new Endpoint[n], () -> read_Endpoint(buf));
    o.recent_deletes = Helper.readStringArray(buf);
    return o;
  }

  public static Endpoint read_Endpoint(ByteBuf buf) {
    switch (buf.readIntLE()) {
      case 30000:
        return readBody_30000(buf, new Endpoint());
    }
    return null;
  }


  private static Endpoint readBody_30000(ByteBuf buf, Endpoint o) {
    o.id = Helper.readString(buf);
    o.ip = Helper.readString(buf);
    o.port = buf.readIntLE();
    o.monitoringPort = buf.readIntLE();
    o.counter = buf.readIntLE();
    o.role = Helper.readString(buf);
    o.created = buf.readLongLE();
    return o;
  }

  public static void write(ByteBuf buf, ForwardSlowGossip o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(30006);
    Helper.writeArray(buf, o.all_endpoints, (item) -> write(buf, item));
  }

  public static void write(ByteBuf buf, ReverseHashFound o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(30005);
    Helper.writeIntArray(buf, o.counters);;
    Helper.writeArray(buf, o.missing_endpoints, (item) -> write(buf, item));
  }

  public static void write(ByteBuf buf, HashNotFoundReverseConversation o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(30004);
    Helper.writeString(buf, o.hash);;
    Helper.writeArray(buf, o.missing_endpoints, (item) -> write(buf, item));
    Helper.writeStringArray(buf, o.recent_deletes);;
  }

  public static void write(ByteBuf buf, ForwardQuickGossip o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(30003);
    Helper.writeIntArray(buf, o.counters);;
  }

  public static void write(ByteBuf buf, HashFoundRequestForwardQuickGossip o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(30002);
    Helper.writeIntArray(buf, o.counters);;
    Helper.writeArray(buf, o.missing_endpoints, (item) -> write(buf, item));
    Helper.writeStringArray(buf, o.recent_deletes);;
  }

  public static void write(ByteBuf buf, BeginGossip o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(30001);
    Helper.writeString(buf, o.hash);;
    Helper.writeArray(buf, o.recent_endpoints, (item) -> write(buf, item));
    Helper.writeStringArray(buf, o.recent_deletes);;
  }

  public static void write(ByteBuf buf, Endpoint o) {
    if (o == null) {
      buf.writeIntLE(0);
      return;
    }
    buf.writeIntLE(30000);
    Helper.writeString(buf, o.id);;
    Helper.writeString(buf, o.ip);;
    buf.writeIntLE(o.port);
    buf.writeIntLE(o.monitoringPort);
    buf.writeIntLE(o.counter);
    Helper.writeString(buf, o.role);;
    buf.writeLongLE(o.created);
  }
}
