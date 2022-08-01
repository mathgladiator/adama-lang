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
  @TypeId(30000)
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
  @Flow("Chatter")
  @TypeId(30001)
  public static class BeginGossip {
    @FieldOrder(1)
    public String hash;
    @FieldOrder(2)
    public Endpoint[] recent_endpoints;
    @FieldOrder(3)
    public String[] recent_deletes;
  }

  // server found the hash and knows how to respond to a quick gossip
  @Flow("Chatter")
  @TypeId(30002)
  public static class HashFoundRequestForwardQuickGossip {
    @FieldOrder(1)
    public int[] counters;
    @FieldOrder(2)
    public Endpoint[] missing_endpoints;
    @FieldOrder(3)
    public String[] recent_deletes;
  }

  // server found the hash or client found the reverse hash
  @Flow("Chatter")
  @TypeId(30003)
  public static class ForwardQuickGossip {
    @FieldOrder(1)
    public int[] counters;
  }

  // server couldn't find hash, so it sends its recent endpoints along with its hash
  @Flow("Chatter")
  @TypeId(30004)
  public static class HashNotFoundReverseConversation {
    @FieldOrder(1)
    public String hash;
    @FieldOrder(2)
    public Endpoint[] missing_endpoints;
    @FieldOrder(3)
    public String[] recent_deletes;
  }

  // client is learning that a hash wasn't found, but it found the related hash
  @Flow("Chatter")
  @TypeId(30005)
  public static class ReverseHashFound {
    @FieldOrder(1)
    public int[] counters;
    @FieldOrder(2)
    public Endpoint[] missing_endpoints;
  }

  // client didn't find the reverse hash
  @Flow("Chatter")
  @TypeId(30006)
  public static class SlowGossip {
    @FieldOrder(1)
    public Endpoint[] all_endpoints;
  }
}
