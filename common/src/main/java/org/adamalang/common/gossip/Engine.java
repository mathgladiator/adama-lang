/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.gossip;

import io.netty.buffer.ByteBuf;
import org.adamalang.common.gossip.codec.GossipProtocol;
import org.adamalang.common.gossip.codec.GossipProtocolCodec;
import org.adamalang.common.net.ByteStream;
import org.adamalang.common.net.ChannelClient;
import org.adamalang.common.net.Remote;

public class Engine {
  public void registerClient(ChannelClient client) {
  }

  public void unregisterClient(ChannelClient client) {
  }

  public class Exchange extends GossipProtocolCodec.StreamChatterFromServer {
    public void start(Remote remote) {
      ByteBuf buf = remote.create(100);
      GossipProtocol.BeginGossip begin = new GossipProtocol.BeginGossip();
    }

    @Override
    public void handle(GossipProtocol.ReverseSlowGossip payload) {

    }

    @Override
    public void handle(GossipProtocol.ReverseQuickGossip payload) {

    }

    @Override
    public void handle(GossipProtocol.HashNotFoundReverseConversation payload) {

    }

    @Override
    public void handle(GossipProtocol.HashFoundRequestForwardQuickGossip payload) {

    }

    @Override
    public void completed() {
    }

    @Override
    public void error(int errorCode) {
    }
  }

  public Exchange client() {
    return null;
  }

  public ByteStream server(ByteStream upstream) {
    return new GossipProtocolCodec.StreamChatterFromClient() {
      @Override
      public void completed() { }

      @Override
      public void error(int errorCode) { }

      @Override
      public void handle(GossipProtocol.ForwardSlowGossip payload) {

      }

      @Override
      public void handle(GossipProtocol.ReverseHashFound payload) {

      }

      @Override
      public void handle(GossipProtocol.ForwardQuickGossip payload) {

      }

      @Override
      public void handle(GossipProtocol.BeginGossip payload) {

      }
    };
  }
}
