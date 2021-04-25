package org.adamalang;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.adamalang.client.BenchmarkClientFlow;
import org.adamalang.client.WebSocketBenchmarkClientBuilder;

public class Benchmark {

  private static String create(int id, long key) {
    StringBuilder sb = new StringBuilder();
    sb.append("{\"id\":").append(id).append(",\"method\":\"create\",\"space\":\"bsg\",\"key\":\"" + key + "\",\"arg\":{\"players\":[");
    sb.append("{\"player\":{\"agent\":\"alice\",\"authority\":\"devkit\"}}");
    sb.append(",{\"player\":{\"agent\":\"bob\",\"authority\":\"devkit\"}}");
    sb.append(",{\"player\":{\"agent\":\"carol\",\"authority\":\"devkit\"}}");
    sb.append(",{\"player\":{\"agent\":\"dan\",\"authority\":\"devkit\"}}");
    sb.append("]}}");
    return sb.toString();
  }

  public static void main(String[] args) throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    WebSocketBenchmarkClientBuilder builder = WebSocketBenchmarkClientBuilder.start(group);
    builder.auth("benchmark");
    BenchmarkClientFlow dumb = new BenchmarkClientFlow() {
      @Override
      public void ready(Channel channel) {
        System.err.println("READY");

        long start = 2000;
        for (int k = 0; k < 1000; k++) {
          channel.writeAndFlush(new TextWebSocketFrame(create(1 + k, start + k)));
        }
      }
      @Override
      public void data(ObjectNode node) {
        System.err.println("Data:" + node.toPrettyString());
      }
    };
    builder.execute(dumb);
  }
}
