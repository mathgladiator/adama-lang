package org.adamalang;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.adamalang.api.util.Json;
import org.adamalang.client.BenchmarkClientFlow;
import org.adamalang.client.WebSocketBenchmarkClientBuilder;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Benchmark {

  private static String create(int id, long key) {
    StringBuilder sb = new StringBuilder();
    sb.append("{\"id\":").append(id).append(",\"method\":\"create\",\"space\":\"bsg\",\"key\":\"" + key + "\",\"arg\":{\"players\":[");
    sb.append("{\"player\":{\"agent\":\"alice\",\"authority\":\"me\"}}");
    sb.append(",{\"player\":{\"agent\":\"bob\",\"authority\":\"me\"}}");
    sb.append(",{\"player\":{\"agent\":\"carol\",\"authority\":\"me\"}}");
    sb.append(",{\"player\":{\"agent\":\"dan\",\"authority\":\"me\"}}");
    sb.append("]}}");
    return sb.toString();
  }

  public static class WebSocketTable {
    private final AtomicInteger idGen;
    private HashMap<Integer, Consumer<ObjectNode>> handlers;

    public WebSocketTable() {
      this.idGen = new AtomicInteger(0);
      this.handlers = new HashMap<>();
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

  public static class BatchPlayer implements BenchmarkClientFlow {
    private EventLoopGroup group;
    private Channel channel;
    private WebSocketTable table;
    private HashSet<Integer> respondedTo;
    private Random rng;
    private boolean canRespond;

    public BatchPlayer(EventLoopGroup group, Random rng) {
      this.group = group;
      this.table = new WebSocketTable();
      this.respondedTo = new HashSet<>();
      this.rng = rng;
      this.canRespond = true;
    }

    @Override
    public void ready(Channel channel) {
      System.err.println("player connected");
      this.channel = channel;
      AtomicInteger start = new AtomicInteger(0);
      AtomicReference<Runnable> ref = new AtomicReference<>();
      Runnable add = new Runnable() {
        @Override
        public void run() {
          int key = start.incrementAndGet();
          if (key > 1200) return;
          AtomicBoolean next = new AtomicBoolean(true);
          int connectID = table.map((data) -> {
            System.err.println("got:" + data.toString());
            if (next.compareAndSet(true, false)) {
              group.execute(ref.get());
            }
          });
          String connectStr = new StringBuilder().append("{\"id\":").append(connectID).append(",\"method\":\"connect\",\"space\":\"bsg\",\"key\":\"" + key + "\"}").toString();
          channel.writeAndFlush(new TextWebSocketFrame(connectStr));
        }
      };
      ref.set(add);
      add.run();

      /*
      for (int k = 0; k < 1200; k++) {

      }
      */
    }

    @Override
    public void data(ObjectNode node) {
      table.handle(node);
    }
  }

  public static class SoloPlayer implements BenchmarkClientFlow {
    private EventLoopGroup group;
    private Channel channel;
    public int gameId;
    private WebSocketTable table;
    private HashSet<Integer> respondedTo;
    private Random rng;
    private boolean canRespond;

    public SoloPlayer(EventLoopGroup group, int gameId, Random rng) {
      this.group = group;
      this.gameId = gameId;
      this.table = new WebSocketTable();
      this.respondedTo = new HashSet<>();
      this.rng = rng;
      this.canRespond = true;
    }

    @Override
    public void ready(Channel channel) {
      System.err.println("player connected");
      this.channel = channel;
      int connectID = table.map((data) -> {
        try {
          boolean cylonWin = data.get("response").get("data").get("cylon_win").asBoolean();
          boolean humansWin = data.get("response").get("data").get("humans_win").asBoolean();
          boolean over = cylonWin || humansWin;
          if (over) {
            System.err.println("Game over: " + gameId);
            return;
          }
        } catch (NullPointerException npe) {
          // ignore
        }
        JsonNode outstandingNode = data.get("response").get("outstanding");
        if (outstandingNode != null && outstandingNode.isArray() && outstandingNode.size() > 0) {
          // report NOTDONE
          ArrayNode outstandingArray = (ArrayNode) outstandingNode;
          for (int k = 0; k < outstandingArray.size(); k++) {
            JsonNode decisionNode = outstandingArray.get(k);
            JsonNode decisionIdNode = decisionNode.get("id");
            if (decisionIdNode != null && decisionIdNode.isInt()) {
              int id = decisionIdNode.asInt();
              if (!respondedTo.contains(id)) {
                respondedTo.add(id);
                ObjectNode toSend = Json.newJsonObject();
                toSend.put("method", "send");
                toSend.put("space", "bsg");
                toSend.put("key", "" + gameId); // TODO: decision should return game id
                toSend.set("channel", decisionNode.get("channel"));
                int count = decisionNode.get("options").size();
                toSend.put("marker", UUID.randomUUID().toString());
                toSend.set("message", decisionNode.get("options").get(rng.nextInt(count)));
                int rpcID = table.map((resp) -> { });
                toSend.put("id", rpcID);
                if (canRespond) {
                  canRespond = false;
                  group.schedule(() -> {
                    System.err.println("WRITE:" + toSend.toString());
                    channel.writeAndFlush(new TextWebSocketFrame(toSend.toString()));
                  }, 1000, TimeUnit.MILLISECONDS);
                }
              }
            }
          }
        } else {
          // report DONE
        }
      });
      //String connectStr = new StringBuilder().append("{\"id\":").append(connectID).append(",\"method\":\"connect\",\"space\":\"bsg\",\"key\":\"" + gameId + "\"}").toString();
      //channel.writeAndFlush(new TextWebSocketFrame(connectStr));
    }

    @Override
    public void data(ObjectNode node) {
      table.handle(node);
    }
  }

  public static void main(String[] args) throws Exception {
    // EventLoopGroup group = new NioEventLoopGroup();

    // WebSocketBenchmarkClientBuilder.start(group).server(server, port).auth("alice").execute(new CreateGames(1));

    /*
    WebSocketBenchmarkClientBuilder.start(group).server(server, port).auth("alice").execute(new BatchPlayer(group, new Random()));
    WebSocketBenchmarkClientBuilder.start(group).server(server, port).auth("bob").execute(new BatchPlayer(group, new Random()));
    WebSocketBenchmarkClientBuilder.start(group).server(server, port).auth("carol").execute(new BatchPlayer(group, new Random()));
    WebSocketBenchmarkClientBuilder.start(group).server(server, port).auth("dan").execute(new BatchPlayer(group, new Random()));
    */

    /*
    int start = 500;
    for (int gameId = start; gameId < start + 1200; gameId++) {
      WebSocketBenchmarkClientBuilder.start(group).server(server, port).auth("alice").execute(new Player(group, gameId, new Random()));
      if (gameId == start) {
        for (int k = 0; k < 15; k++) {
          System.err.println("waiting for compilation: " + k);
          // Thread.sleep(1000);
        }
      }
      WebSocketBenchmarkClientBuilder.start(group).server(server, port).auth("bob").execute(new Player(group, gameId, new Random()));
      WebSocketBenchmarkClientBuilder.start(group).server(server, port).auth("carol").execute(new Player(group, gameId, new Random()));
      WebSocketBenchmarkClientBuilder.start(group).server(server, port).auth("dan").execute(new Player(group, gameId, new Random()));
      if (gameId % 25 == 0) {
        Thread.sleep(1000);
        System.err.println("Wrote:" + (gameId - start));
      }
    }
    System.err.println("Done");
    */
  }
}
