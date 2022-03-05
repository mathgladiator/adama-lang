package org.adamalang.common.net;

import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;

import java.util.*;

/** we monitor the inflight connections so we can terminate them all */
public class SocketChannelSet {
  private final HashMap<Integer, SocketChannel> map;
  private final Random rng;

  public SocketChannelSet() {
    this.map = new HashMap<>();
    this.rng = new Random();
  }

  public synchronized int add(SocketChannel channel) {
    while (true) {
      int id = rng.nextInt();
      if (!map.containsKey(id)) {
        map.put(id, channel);
        return id;
      }
    }
  }

  public synchronized void remove(int key) {
    map.remove(key);
  }

  private synchronized Collection<SocketChannel> killUnderLock() {
    ArrayList<SocketChannel> list = new ArrayList<>(map.values());
    map.clear();
    return list;
  }

  public void kill() {
    ArrayList<ChannelFuture> futures = new ArrayList<>();
    for (SocketChannel channel : killUnderLock()) {
      futures.add(channel.close());
    }
    for (ChannelFuture future : futures) {
      future.syncUninterruptibly();
    }
  }
}
