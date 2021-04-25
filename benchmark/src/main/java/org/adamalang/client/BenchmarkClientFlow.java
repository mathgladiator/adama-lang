package org.adamalang.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.Channel;


public interface BenchmarkClientFlow {
  public void ready(Channel channel);

  public void data(ObjectNode node);
}
