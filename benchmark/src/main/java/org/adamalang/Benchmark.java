package org.adamalang;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.adamalang.client.BenchmarkClientFlow;
import org.adamalang.client.WebSocketBenchmarkClientBuilder;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

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

  public static class WebSocketTable {
    private final AtomicInteger idGen;
    private HashMap<Integer, Consumer<ObjectNode>> handlers;

    public WebSocketTable() {
      this.idGen = new AtomicInteger(1);
    }

    public synchronized int map(Consumer<ObjectNode> consumer) {
      int id = this.idGen.incrementAndGet();
      this.handlers.put(id, consumer);
      return id;
    }

    private synchronized void handle(ObjectNode node) {
      JsonNode idNode = node.get("deliver");
      JsonNode doneNode = node.get("done");
      boolean done = doneNode == null || doneNode.isNull() || doneNode.asBoolean();
      if (idNode == null || idNode.isNull()) {
        idNode = node.get("failure");
        done = true;
      }
      if (idNode == null || idNode.isNull() || !idNode.isInt()) {
        return;
      }
      Consumer<ObjectNode> handler = done ? handlers.remove(idNode.intValue()) : handlers.get(idNode.intValue());
      if (handler != null) {
        handler.accept(node);
      }
    }
  }

  public static class CreateGames implements BenchmarkClientFlow {
    private final int count;

    public CreateGames(int count) {
      this.count = count;
    }

    @Override
    public void ready(Channel channel) {
      System.err.println("READY");
      for (int k = 0; k < count; k++) {
        int key = 1 + k;
        channel.writeAndFlush(new TextWebSocketFrame(create(key, key)));
      }
    }
    @Override
    public void data(ObjectNode node) {
      System.err.println("RESULT:" + node.toPrettyString());
    }
  }

  public static void main(String[] args) throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    WebSocketBenchmarkClientBuilder builder = WebSocketBenchmarkClientBuilder.start(group);
    builder.auth("benchmark");
    builder.execute(new CreateGames());
  }
}
