/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
package org.adamalang.common.net;

import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

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

  public void kill() {
    ArrayList<ChannelFuture> futures = new ArrayList<>();
    for (SocketChannel channel : killUnderLock()) {
      futures.add(channel.close());
    }
    for (ChannelFuture future : futures) {
      future.syncUninterruptibly();
    }
  }

  private synchronized Collection<SocketChannel> killUnderLock() {
    ArrayList<SocketChannel> list = new ArrayList<>(map.values());
    map.clear();
    return list;
  }
}
