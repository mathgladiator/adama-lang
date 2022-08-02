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

import org.adamalang.common.codec.FieldOrder;
import org.adamalang.common.codec.Flow;
import org.adamalang.common.codec.TypeId;

/**
 client picks a random known host
 (1) sends a BeginGossip with its most recent hash of its endpoint set along with a handful of recent endpoints learned about
 (2) server seeing BeginGossip will
 (a) integrate the recent endpoints
 (b) look the provided hash within the HashSetChain
 (i) if the Set was found within the server's HashSetChain
 (x) send client a HashFound with the counters and a recent set of endpoints  (completed exchange)
 (y) the client will then integrate the counters for the Set used
 (z) the client will integrate the recent endpoints if there are any
 (w) the client will then send a QuickGossip to complete the exchange
 (ii) if the Set was not found
 (x) send client a HashNotFoundReverseConversation with the most recent hash serer knows about along with recent endpoints
 (y) the client will integrate the recent endpoints if there are any
 (z) the client will search its HashSetChain for the hash
 (u) if the Set was found within the client's HashSetChain, then send a ReverseHashFound (completed exchange)
 (v) if the Set was not found, then send a SlowGossip and stop (breaking asymmetry)
 */
public class GossipProtocol {
  @Flow("Raw")
  @TypeId(30)
  public static class Endpoint {
    @FieldOrder(1)
    public String id;
    @FieldOrder(2)
    public String ip;
    @FieldOrder(3)
    public int port;
    @FieldOrder(4)
    public int monitoringPort;
    @FieldOrder(5)
    public int counter;
    @FieldOrder(6)
    public String role;
    @FieldOrder(7)
    public long created;
  }

  // client initiates gossip by sending its hash along with an optimistic list of endpoints
  @Flow("ChatterFromClient")
  @TypeId(31)
  public static class BeginGossip {
    @FieldOrder(1)
    public String hash;
    @FieldOrder(2)
    public Endpoint[] recent_endpoints;
    @FieldOrder(3)
    public String[] recent_deletes;
  }

  // server found the hash and knows how to respond to a quick gossip
  @Flow("ChatterFromServer")
  @TypeId(33)
  public static class HashFoundRequestForwardQuickGossip {
    @FieldOrder(1)
    public int[] counters;
    @FieldOrder(2)
    public Endpoint[] recent_endpoints;
    @FieldOrder(3)
    public String[] recent_deletes;
  }

  // server found the hash or client found the reverse hash
  @Flow("ChatterFromClient")
  @TypeId(34)
  public static class ForwardQuickGossip {
    @FieldOrder(1)
    public int[] counters;
  }

  // server couldn't find hash, so it sends its recent endpoints along with its hash
  @Flow("ChatterFromServer")
  @TypeId(35)
  public static class HashNotFoundReverseConversation {
    @FieldOrder(1)
    public String hash;
    @FieldOrder(2)
    public Endpoint[] recent_endpoints;
    @FieldOrder(3)
    public String[] recent_deletes;
  }

  // client is learning that a hash wasn't found, but it found the related hash
  @Flow("ChatterFromClient")
  @TypeId(36)
  public static class ReverseHashFound {
    @FieldOrder(1)
    public int[] counters;
    @FieldOrder(2)
    public Endpoint[] missing_endpoints;
  }

  // client is learning that a hash wasn't found, but it found the related hash
  @Flow("ChatterFromServer")
  @TypeId(37)
  public static class ReverseQuickGossip {
    @FieldOrder(1)
    public int[] counters;
    @FieldOrder(2)
    public Endpoint[] missing_endpoints;
  }

  // client didn't find the reverse hash, send everything
  @Flow("ChatterFromClient")
  @TypeId(38)
  public static class ForwardSlowGossip {
    @FieldOrder(1)
    public Endpoint[] all_endpoints;
    @FieldOrder(2)
    public String[] recent_deletes;
  }

  // server responses to a slow gossip with a slow gossip
  @Flow("ChatterFromServer")
  @TypeId(39)
  public static class ReverseSlowGossip {
    @FieldOrder(1)
    public Endpoint[] all_endpoints;
    @FieldOrder(2)
    public String[] recent_deletes;
  }
}
